package AAADEVRECORDV3.Http.MediaListeners;

import java.util.UUID;

import service.AAADEVRECORDV3.MyEmailSender;
import AAADEVRECORDV3.Http.PlayAnnouncement.PlayBeep;
import AAADEVRECORDV3.Http.PlayAnnouncement.PlayError;
import AAADEVRECORDV3.Http.PlayAnnouncement.PlayHelsinki;
import AAADEVRECORDV3.util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.MediaFactory;
import com.avaya.collaboration.call.media.MediaListenerAbstract;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.call.media.PlayOperationCause;
import com.avaya.collaboration.call.media.RecordItem;
import com.avaya.collaboration.call.media.RecordOperationCause;
import com.avaya.collaboration.util.logger.Logger;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

public class MediaListenerTTSAnnouncement extends MediaListenerAbstract{
	private final Call call;
	private final Logger logger = Logger.getLogger(getClass());
	private final int MAX_RECORD_TIME = 60000; // 60 seconds
	/*
	 * Constructor
	 */
	public MediaListenerTTSAnnouncement(final Call call) {
		this.call = call;
	}
	@Override
	public void playCompleted(final UUID requestId,
			final PlayOperationCause cause) {
		if (cause == PlayOperationCause.COMPLETE) {
			PlayBeep playBeep = new PlayBeep(call);
			playBeep.beep();
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
			MyMediaListener mediaListenerRecord = new MyMediaListener(call);
			MediaService mediaService = MediaFactory.createMediaService();

			mediaService.record(call.getCallingParty(), recordItem,
					mediaListenerRecord);
		}

	}

	@Override
	public void recordCompleted(final UUID requestId,
			final RecordOperationCause cause) {
		if (cause == RecordOperationCause.TERMINATION_KEY_PRESSED) {
			try {
				PlayHelsinki play = new PlayHelsinki(call);
				play.start();
			} catch (Exception e) {
				PlayError playError = new PlayError(call);
					playError.audioError();
				logger.info("Error MediaListenerTTSAnnouncement: " + e.toString());
				new MyEmailSender().sendErrorByEmail("Error MediaListenerTTSAnnouncement: " + e.toString(), call);
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
