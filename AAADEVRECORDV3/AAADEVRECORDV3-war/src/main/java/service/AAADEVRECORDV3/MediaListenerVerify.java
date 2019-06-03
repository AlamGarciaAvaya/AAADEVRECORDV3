package service.AAADEVRECORDV3;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

import AAADEVRECORDV3.make.VerbioClient;
import AAADEVRECORDV3.util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.Participant;
import com.avaya.collaboration.call.media.DigitCollectorOperationCause;
import com.avaya.collaboration.call.media.MediaFactory;
import com.avaya.collaboration.call.media.MediaListenerAbstract;
import com.avaya.collaboration.call.media.MediaServerInclusion;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.call.media.PlayItem;
import com.avaya.collaboration.call.media.PlayOperationCause;
import com.avaya.collaboration.call.media.RecordItem;
import com.avaya.collaboration.call.media.RecordOperationCause;
import com.avaya.collaboration.call.media.SendDigitsOperationCause;
import com.avaya.collaboration.ssl.util.SSLUtilityException;
import com.avaya.collaboration.util.logger.Logger;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

public class MediaListenerVerify extends MediaListenerAbstract {
	private final Call call;
	private final Logger logger = Logger.getLogger(getClass());
	private final String verbioUser;
	private final String nameuser;
	private final int MAX_RECORD_TIME = 60000; // 60 seconds
	/*
	 * Constructor
	 */
	public MediaListenerVerify(final Call call, final String verbioUser, final String nameuser) {
		this.call = call;
		this.verbioUser = verbioUser;
		this.nameuser = nameuser;
	}

	@Override
	public void playCompleted(final UUID requestId,
			final PlayOperationCause cause) {
		if (cause == PlayOperationCause.COMPLETE) {
			/*
			 * Recupera la ruta del archivo guardado
			 */
			final String storageUrl = formRecordingStoreUrl();
			/*
			 * Prepara los parámetros para realizar la grabación. public
			 * interface RecordItem Provides methods to access or modify the
			 * properties related to a record operation.
			 */
			final RecordItem recordItem = MediaFactory.createRecordItem();

			recordItem.setMaxDuration(MAX_RECORD_TIME).setTerminationKey("#")
					.setFileUri(storageUrl);
			MediaListenerVerify mediaListenerRecord = new MediaListenerVerify(call, verbioUser, nameuser);
			MediaService mediaService = MediaFactory.createMediaService();

			mediaService.record(call.getCallingParty(), recordItem,
					mediaListenerRecord);
		}
	}

	@Override
	public void digitsCollected(final UUID requestId, final String digits,
			final DigitCollectorOperationCause cause) {
	}

	@Override
	public void sendDigitsCompleted(final UUID requestId,
			final SendDigitsOperationCause cause) {
	}

	@Override
	public void recordCompleted(final UUID requestId,
			final RecordOperationCause cause) {
		if (cause == RecordOperationCause.TERMINATION_KEY_PRESSED) {
			//Verificar el Audio
			VerbioClient verify = new VerbioClient();
			try {
				verify.verify(verbioUser, call, nameuser);
			} catch (UnsupportedOperationException | IOException
					| SSLUtilityException | NoAttributeFoundException | ServiceNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String formRecordingStoreUrl() {
		/*
		 * Define la ruta del archivo grabado para ser almacenado (incluye
		 * StoreRecordingServlet) Pribar si hace un POST al Servlet
		 * StoreRecordingServlet
		 */
		final TrafficInterfaceAddressRetrieverImpl addressRetriever = new TrafficInterfaceAddressRetrieverImpl();
		final String trafficInterfaceAddress = addressRetriever
				.getTrafficInterfaceAddress();
		final String myServiceName = ServiceUtil.getServiceDescriptor()
				.getName();
		final StringBuilder sb = new StringBuilder();
		sb.append("http://").append(trafficInterfaceAddress)
				.append("/services/").append(myServiceName)
				.append("/StoreRecordingServlet/").append("recording")
				.append(myServiceName).append(".wav");
		return sb.toString();
	}

}
