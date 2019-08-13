package AAADEVRECORDV3.Http.PlayAnnouncement;

import java.net.URISyntaxException;

import AAADEVRECORDV3.Http.MediaListeners.MediaListenerRingOut;
import AAADEVRECORDV3.util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.MediaFactory;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.call.media.PlayItem;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

public class PlayRingOut {
	private final Call call;
	public PlayRingOut(final Call call){
		this.call = call;
	}
	public void ringOut() throws URISyntaxException{
		final TrafficInterfaceAddressRetrieverImpl addressRetriever = new TrafficInterfaceAddressRetrieverImpl();
		final String trafficInterfaceAddress = addressRetriever
				.getTrafficInterfaceAddress();

		final String myServiceName = ServiceUtil.getServiceDescriptor()
				.getName();
		final StringBuilder sb = new StringBuilder();
		sb.append("http://").append(trafficInterfaceAddress)
				.append("/services/").append(myServiceName).append("/")
				.append("Ring_Out.wav");

		PlayItem playItem = null;

		playItem = MediaFactory.createPlayItem().setInterruptible(false)
				.setIterateCount(1).setSource(sb.toString());

		final MediaService mediaServiceError = MediaFactory
				.createMediaService();
		final MediaListenerRingOut mediaListenerRingOut = new MediaListenerRingOut(call);
		mediaServiceError.play(call.getCallingParty(), playItem, mediaListenerRingOut);
	}
}
