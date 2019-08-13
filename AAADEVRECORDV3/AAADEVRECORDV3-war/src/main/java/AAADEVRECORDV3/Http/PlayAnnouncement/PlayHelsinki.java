package AAADEVRECORDV3.Http.PlayAnnouncement;

import java.net.URISyntaxException;
import java.util.UUID;

import service.AAADEVRECORDV3.MyEmailSender;
import AAADEVRECORDV3.Http.PeticionesExternas;
import AAADEVRECORDV3.Http.MediaListeners.MediaListenerPlayHelsinky;
import AAADEVRECORDV3.util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.MediaFactory;
import com.avaya.collaboration.call.media.MediaServerInclusion;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.call.media.PlayItem;
import com.avaya.collaboration.util.logger.Logger;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

public class PlayHelsinki extends Thread {
	private final Call call;
	private final Logger logger;
	private MediaService mediaServicehelsinky = null;
	private UUID requestid = null;

	public PlayHelsinki(final Call call) {
		this.call = call;
		logger = Logger.getLogger(PlayHelsinki.class);
	}

	@Override
	public void run() {
		try {
		final TrafficInterfaceAddressRetrieverImpl addressRetriever = new TrafficInterfaceAddressRetrieverImpl();
		final String trafficInterfaceAddress = addressRetriever
				.getTrafficInterfaceAddress();

		final String myServiceName = ServiceUtil.getServiceDescriptor()
				.getName();
		final StringBuilder sb = new StringBuilder();
		sb.append("http://").append(trafficInterfaceAddress)
		.append("/services/").append(myServiceName).append("/")
		.append("Helsinki.wav");

		PlayItem playItem = MediaFactory.createPlayItem().setInterruptible(true)
					.setIterateCount(5)
					.setSource(sb.toString());


		mediaServicehelsinky = MediaFactory.createMediaService();
		final MediaListenerPlayHelsinky mediaListenerPalyHelsinky = new MediaListenerPlayHelsinky(call);
		requestid = mediaServicehelsinky.play(call.getCallingParty(), playItem, mediaListenerPalyHelsinky);
		
		PeticionesExternas peticiones = new PeticionesExternas(call);
		peticiones.peticionesExternas(call.getCallingParty(), requestid, mediaServicehelsinky);
			
		} catch (NoAttributeFoundException | ServiceNotFoundException | URISyntaxException e) {
	    	mediaServicehelsinky.stop(call.getCallingParty(), requestid);
	    	PlayError playError = new PlayError(call);
			playError.audioError();
			logger.error("Error PlayHelsinki " + e.toString());
			new MyEmailSender().sendErrorByEmail("Error PlayHelsinki " + e.toString(), call);
			
		} 

	}

}
