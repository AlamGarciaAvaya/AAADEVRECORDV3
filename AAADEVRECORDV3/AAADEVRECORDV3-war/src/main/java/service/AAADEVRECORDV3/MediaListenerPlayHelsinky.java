package service.AAADEVRECORDV3;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.MediaListenerAbstract;
import com.avaya.collaboration.util.logger.Logger;

public class MediaListenerPlayHelsinky extends MediaListenerAbstract{
	private final Logger logger = Logger.getLogger(getClass());
	private final Call call;
    /*
     * Constructor
     */
    public MediaListenerPlayHelsinky(final Call call)
    {
        this.call = call;
    }
    
    
    
}
