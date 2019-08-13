package AAADEVRECORDV3.Http.PlayAnnouncement;

import java.net.URISyntaxException;

import AAADEVRECORDV3.Http.MediaListeners.MyMediaListener;
import AAADEVRECORDV3.util.AttributeStore;
import AAADEVRECORDV3.util.Constants;
import AAADEVRECORDV3.util.LanguageAttribute;
import AAADEVRECORDV3.util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.NoServiceProfileFoundException;
import com.avaya.collaboration.businessdata.api.NoUserFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.MediaFactory;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.call.media.PlayItem;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

public class PlayNotUser {
	final private Call call;

	public PlayNotUser(final Call call) {
		this.call = call;
	}

	public Call getCall() {
		return call;
	}

	public void promptPlayAndExecute() throws URISyntaxException,
			NoAttributeFoundException, ServiceNotFoundException,
			NoUserFoundException, NoServiceProfileFoundException {


		LanguageAttribute languageAttribute = new LanguageAttribute(call);
		String announcement = null;
		/*
		 * Solicitar el idioma
		 */
		String folderWavs = AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AUDIOS_FOLDER);
		if (languageAttribute.getLanguageAttribute().equals("es")) {
			announcement = "Audios/"+folderWavs+"/ES/Not_User_ES.wav";
		}
		if (languageAttribute.getLanguageAttribute().equals("en")) {
			announcement = "Audios/"+folderWavs+"/EN/Not_User_EN.wav";
		}
		if (languageAttribute.getLanguageAttribute().equals("pt")) {
			announcement = "Audios/"+folderWavs+"/PT/Not_User_PT.wav";
		}
		/*
		 * Determinar la url del servicio
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

		PlayItem playItem = MediaFactory.createPlayItem().setInterruptible(true)
				.setIterateCount(1).setSource(sb.toString());

		final MediaService mediaService = MediaFactory.createMediaService();
		final MyMediaListener myMediaListener = new MyMediaListener(call);
		mediaService.play(call.getCallingParty(), playItem, myMediaListener);
	}

}
