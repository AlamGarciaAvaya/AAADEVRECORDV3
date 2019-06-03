package AAADEVRECORDV3.make;

import java.net.URISyntaxException;

import service.AAADEVRECORDV3.MediaListenerPlayError;
import AAADEVRECORDV3.util.LanguageAttribute;
import AAADEVRECORDV3.util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.Participant;
import com.avaya.collaboration.call.media.MediaFactory;
import com.avaya.collaboration.call.media.MediaServerInclusion;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.call.media.PlayItem;
import com.avaya.collaboration.util.logger.Logger;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

public class PlayError {
	
	private final Call call;
	private final Logger logger;

	public PlayError(final Call call) {
		this.call = call;
		logger = Logger.getLogger(PlayError.class);
	}
	
	public void audioError() throws NoAttributeFoundException, ServiceNotFoundException{
		logger.info("PlayError");
		call.getCallPolicies().setMediaServerInclusion(
				MediaServerInclusion.AS_NEEDED);
		

		
		String anuncio = null;
		LanguageAttribute languageAttribute = new LanguageAttribute(call);
		if (languageAttribute.getLanguageAttribute().equals("es")) {
			anuncio = "Error_es.wav";
		}
		if (languageAttribute.getLanguageAttribute().equals("en")) {
			anuncio = "Error_en.wav";
		}
		if (languageAttribute.getLanguageAttribute().equals("pt")) {
			anuncio = "engano_pt.wav";
		}
		final TrafficInterfaceAddressRetrieverImpl addressRetriever = new TrafficInterfaceAddressRetrieverImpl();
		final String trafficInterfaceAddress = addressRetriever
				.getTrafficInterfaceAddress();

		final String myServiceName = ServiceUtil.getServiceDescriptor()
				.getName();
		final StringBuilder sb = new StringBuilder();
		sb.append("http://").append(trafficInterfaceAddress)
		.append("/services/").append(myServiceName).append("/")
		.append(anuncio);

		PlayItem playItem = null;
		try {
			playItem = MediaFactory.createPlayItem().setInterruptible(true)
					.setIterateCount(1)
					.setSource(sb.toString());
			
		} catch (URISyntaxException e) {
			logger.error("Error audioError: " + e);
		}

		final MediaService mediaServiceError = MediaFactory.createMediaService();
		final Participant participant = call.getCallingParty();
		final MediaListenerPlayError mediaListenerPlayError = new MediaListenerPlayError(call);

		mediaServiceError.play(participant, playItem,
				mediaListenerPlayError);
		
	}
	

}
