package service.AAADEVRECORDV3;

import java.util.UUID;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.MediaListenerAbstract;
import com.avaya.collaboration.call.media.PlayOperationCause;
import com.avaya.collaboration.util.logger.Logger;

public class MediaListenerPlayError extends MediaListenerAbstract{
	private final Logger logger = Logger.getLogger(getClass());
	private final Call call;
    /*
     * Constructor
     */
    public MediaListenerPlayError(final Call call)
    {
        this.call = call;
    }
    @Override
	public void playCompleted(final UUID requestId,
			final PlayOperationCause cause) {
    	call.drop();
    }
    
}
