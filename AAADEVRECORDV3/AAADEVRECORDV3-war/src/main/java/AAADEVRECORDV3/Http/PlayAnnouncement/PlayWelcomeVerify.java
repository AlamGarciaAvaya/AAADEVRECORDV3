package AAADEVRECORDV3.Http.PlayAnnouncement;

import java.net.URISyntaxException;

import AAADEVRECORDV3.Bean.Usuario;
import AAADEVRECORDV3.Http.MediaListeners.MediaListenerVerify;
import AAADEVRECORDV3.util.AttributeStore;
import AAADEVRECORDV3.util.Constants;
import AAADEVRECORDV3.util.LanguageAttribute;
import AAADEVRECORDV3.util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.Participant;
import com.avaya.collaboration.call.media.MediaFactory;
import com.avaya.collaboration.call.media.MediaServerInclusion;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.call.media.PlayItem;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

public class PlayWelcomeVerify {
	final Call call;
	final Usuario usuario;
	public PlayWelcomeVerify(final Call call, final Usuario usuario){
		this.call = call;
		this.usuario = usuario;
	}
	
	public void welcomeVerify() throws URISyntaxException {
		// set the Media Inclusion Policy to AS_NEEDED. Media Server is not
		// required for the whole duration of the call.
		call.getCallPolicies().setMediaServerInclusion(
				MediaServerInclusion.AS_NEEDED);
		LanguageAttribute languageAtribute = new LanguageAttribute(call);
		String announcement = null;
		/*
		 * Solicitar el idioma por Service Profile
		 */
		String folderWavs = AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AUDIOS_FOLDER);
		if (languageAtribute.getLanguageAttribute().equals("es")) {

			announcement = "Audios/"+folderWavs+"/ES/Bienvenido_Autenticacion_Verbio_ES.wav";
		}
		if (languageAtribute.getLanguageAttribute().equals("en")) {
			announcement = "Audios/"+folderWavs+"/EN/Bienvenido_Autenticacion_Verbio_EN.wav";
		}
		if (languageAtribute.getLanguageAttribute().equals("pt")) {
			announcement = "Audios/"+folderWavs+"/PT/Bienvenido_Autenticacion_Verbio_PT.wav";
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
		final MediaListenerVerify mediaListenerVerify = new MediaListenerVerify(call, usuario);
		mediaService.play(participant, playItem, mediaListenerVerify);

	}
}
