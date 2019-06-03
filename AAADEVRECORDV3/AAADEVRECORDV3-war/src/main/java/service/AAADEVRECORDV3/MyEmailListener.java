package service.AAADEVRECORDV3;

import com.avaya.collaboration.email.EmailListener;
import com.avaya.collaboration.email.EmailRequest;
import com.avaya.collaboration.email.EmailResponse;
import com.avaya.collaboration.util.logger.Logger;

public final class MyEmailListener implements EmailListener
{

    private final EmailRequest emailRequest;
    private final Logger logger;

    public MyEmailListener(final EmailRequest emailRequest)
    {
        this.emailRequest = emailRequest;
        this.logger = Logger.getLogger(MyEmailListener.class);
    }

    @Override
    public void responseReceived(final EmailResponse emailResponse)
    {
        if (logger.isFinestEnabled())
        {
            logger.finest("responseReceived: " + emailResponse.toString() + " for " + emailRequest.toString());
        }
    }

}