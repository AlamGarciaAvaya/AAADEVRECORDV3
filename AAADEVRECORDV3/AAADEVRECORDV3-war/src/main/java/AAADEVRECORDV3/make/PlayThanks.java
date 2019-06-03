package AAADEVRECORDV3.make;

import java.net.URISyntaxException;

import service.AAADEVRECORDV3.MediaListenerPlayThanks;
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

public class PlayThanks {
	private final Logger logger;

	public PlayThanks() {

		logger = Logger.getLogger(getClass());

	}

	public void playThanks(final Call call, final boolean playWelcome, String announcement)
			throws URISyntaxException, NoAttributeFoundException,
			ServiceNotFoundException {
		logger.info("playThanks");
		call.getCallPolicies().setMediaServerInclusion(
				MediaServerInclusion.AS_NEEDED);

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
		final Participant participant = call.getCallingParty();
		final MediaListenerPlayThanks mediaListenerPlayThanks = new MediaListenerPlayThanks(call, false);
		mediaService.play(participant, playItem, mediaListenerPlayThanks);
	}
}