package AAADEVRECORDV3.Http.PlayAnnouncement;

import java.net.URISyntaxException;

import service.AAADEVRECORDV3.MyEmailSender;
import AAADEVRECORDV3.Http.MediaListeners.MediaListenerPlayError;
import AAADEVRECORDV3.util.AttributeStore;
import AAADEVRECORDV3.util.Constants;
import AAADEVRECORDV3.util.LanguageAttribute;
import AAADEVRECORDV3.util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.MediaFactory;
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
	
	public void audioError(){
		try{
		String anuncio = null;
		LanguageAttribute languageAttribute = new LanguageAttribute(call);
		String folderWavs = AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AUDIOS_FOLDER);
		if (languageAttribute.getLanguageAttribute().equals("es")) {
			anuncio = "Audios/" + folderWavs + "/ES/Error_ES.wav";
		}
		if (languageAttribute.getLanguageAttribute().equals("en")) {
			anuncio = "Audios/"+folderWavs+"/EN/Error_EN.wav";
		}
		if (languageAttribute.getLanguageAttribute().equals("pt")) {
			anuncio = "Audios/"+folderWavs+"/PT/Error_PT.wav";
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

		PlayItem playItem = MediaFactory
					.createPlayItem()
					.setInterruptible(false)
					.setIterateCount(1)
					.setSource(sb.toString());
			

		final MediaService mediaServiceError = MediaFactory.createMediaService();
		final MediaListenerPlayError mediaListenerPlayError = new MediaListenerPlayError(call);

		mediaServiceError.play(call.getCallingParty(), playItem, mediaListenerPlayError);
		
		}catch (URISyntaxException e) {
			logger.error("Error PlayError: " + e.toString());
			new MyEmailSender().sendErrorByEmail("Error PlayError: " + e.toString(), call);
		}
		
	}

}
