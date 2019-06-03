package AAADEVRECORDV3.make;

import java.io.NotActiveException;
import java.net.URISyntaxException;

import javax.print.ServiceUI;

import service.AAADEVRECORDV3.MyMediaListener;
import AAADEVRECORDV3.util.LanguageAttribute;
import AAADEVRECORDV3.util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.NoServiceProfileFoundException;
import com.avaya.collaboration.businessdata.api.NoUserFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.Participant;
import com.avaya.collaboration.call.media.MediaFactory;
import com.avaya.collaboration.call.media.MediaServerInclusion;
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

		call.getCallPolicies().setMediaServerInclusion(
				MediaServerInclusion.AS_NEEDED);
		LanguageAttribute languageAttribute = new LanguageAttribute(call);
		String announcement = null;
		/*
		 * Solicitar el idioma
		 */
		if (languageAttribute.getLanguageAttribute().equals("es")) {
			announcement = "NotUser_es.wav";
		}
		if (languageAttribute.getLanguageAttribute().equals("en")) {
			announcement = "NotUser_en.wav";
		}
		if (languageAttribute.getLanguageAttribute().equals("pt")) {
			announcement = "NotUser_pt.wav";
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

		PlayItem playItem = null;

		/*
		 * Play Announcement Configurations
		 */

		playItem = MediaFactory.createPlayItem().setInterruptible(true)
				.setIterateCount(1).setSource(sb.toString());

		final MediaService mediaService = MediaFactory.createMediaService();
		final Participant participant = call.getCallingParty();
		final MyMediaListener myMediaListener = new MyMediaListener(call);
		mediaService.play(participant, playItem, myMediaListener);
	}

}
