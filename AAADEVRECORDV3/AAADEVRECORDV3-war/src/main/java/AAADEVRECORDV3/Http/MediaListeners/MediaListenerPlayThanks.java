package AAADEVRECORDV3.Http.MediaListeners;

import java.util.UUID;

import AAADEVRECORDV3.util.AttributeStore;
import AAADEVRECORDV3.util.Constants;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.MediaListenerAbstract;
import com.avaya.collaboration.call.media.PlayOperationCause;

public class MediaListenerPlayThanks extends MediaListenerAbstract{
	private final Call call;
    /*
     * Constructor
     */
    public MediaListenerPlayThanks(final Call call, final boolean dropAfterPlayComplete)
    {
        this.call = call;
    }
    @Override
    public void playCompleted(final UUID requestId, final PlayOperationCause cause)
    {	

		String extension;
		extension = AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AGENT_PHONE);
		call.divertTo(extension);
    }
}
