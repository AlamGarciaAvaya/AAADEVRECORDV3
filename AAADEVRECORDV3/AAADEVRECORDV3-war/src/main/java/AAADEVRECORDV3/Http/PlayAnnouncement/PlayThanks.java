package AAADEVRECORDV3.Http.PlayAnnouncement;

import java.net.URISyntaxException;

import AAADEVRECORDV3.Http.MediaListeners.MediaListenerPlayThanks;
import AAADEVRECORDV3.util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.MediaFactory;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.call.media.PlayItem;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

public class PlayThanks {

	public void playThanks(final Call call, final boolean playWelcome, String announcement)
			throws URISyntaxException, NoAttributeFoundException,
			ServiceNotFoundException {
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

		playItem = MediaFactory.createPlayItem().setInterruptible(false)
				.setIterateCount(1).setSource(sb.toString());

		final MediaService mediaService = MediaFactory.createMediaService();
		final MediaListenerPlayThanks mediaListenerPlayThanks = new MediaListenerPlayThanks(call, false);
		mediaService.play(call.getCallingParty(), playItem, mediaListenerPlayThanks);
	}
}