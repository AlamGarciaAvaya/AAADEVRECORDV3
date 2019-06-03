package service.AAADEVRECORDV3;

import java.util.UUID;

import AAADEVRECORDV3.util.AttributeStore;
import AAADEVRECORDV3.util.Constants;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.MediaListenerAbstract;
import com.avaya.collaboration.call.media.PlayOperationCause;
import com.avaya.collaboration.util.logger.Logger;

public class MediaListenerPlayThanks extends MediaListenerAbstract{
	private final Call call;
    private final Logger logger = Logger.getLogger(getClass());
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
		try {
			extension = AttributeStore.INSTANCE.getAttributeValue(Constants.AGENT_PHONE);
			// llamar.makeCall(firstParty, secondParty, callingId, display);
			call.divertTo(extension);
		} catch (NoAttributeFoundException | ServiceNotFoundException e) {
			logger.info("NoAttributeFoundException: " + e);
		}


    }
}
