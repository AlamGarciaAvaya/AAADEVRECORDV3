package AAADEVRECORDV3.Http.PlayAnnouncement;

import java.net.URISyntaxException;

import AAADEVRECORDV3.Http.MediaListeners.MyMediaListener;
import AAADEVRECORDV3.util.AttributeStore;
import AAADEVRECORDV3.util.Constants;
import AAADEVRECORDV3.util.LanguageAttribute;
import AAADEVRECORDV3.util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.Participant;
import com.avaya.collaboration.call.media.MediaFactory;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.call.media.PlayItem;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

public class PlayNoUser {
	final Call call;
	
	public PlayNoUser(final Call call){
		this.call = call;
	}
	
	public void userNotRegistered() throws URISyntaxException{
		LanguageAttribute languageAtribute = new LanguageAttribute(call);
		String announcement = null;
		/*
		 * Solicitar el idioma por Service Profile
		 */
		String folderWavs = AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AUDIOS_FOLDER);
		if (languageAtribute.getLanguageAttribute().equals("es")) {
			announcement = "Audios/"+folderWavs+"/ES/Bienvenido_ES.wav";
		}
		if (languageAtribute.getLanguageAttribute().equals("en")) {
			announcement = "Audios/"+folderWavs+"/EN/Bienvenido_EN.wav";
		}
		if (languageAtribute.getLanguageAttribute().equals("pt")) {
			announcement = "Audios/"+folderWavs+"/PT/Bienvenido_PT.wav";
		}
		/*
		 * Determina la URL del servicio
		 */
		final TrafficInterfaceAddressRetrieverImpl addressRetriever = new TrafficInterfaceAddressRetrieverImpl();
		final String trafficInterfaceAddress = addressRetriever
				.getTrafficInterfaceAddress();

		final String myServiceName = ServiceUtil.getServiceDescriptor()
				.getName();

		final StringBuilder sb = new StringBuilder();
		sb.append("http://").append(trafficInterfaceAddress)
				.append("/services/").append(myServiceName).append("/")
				.append(announcement);

		PlayItem playItem = null;

		/*
		 * PlayWelcome == null reproduce el mensaje de bienvenida
		 * "Bienvenido.wav" es el resultado del stringBuilder
		 */
		playItem = MediaFactory.createPlayItem().setInterruptible(true)
				.setIterateCount(1).setSource(sb.toString());

		final MediaService mediaService = MediaFactory.createMediaService();
		final Participant participant = call.getCallingParty();
		final MyMediaListener myMediaListener = new MyMediaListener(call);
		mediaService.play(participant, playItem, myMediaListener);
	}
}
