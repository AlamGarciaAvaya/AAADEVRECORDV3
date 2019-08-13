package service.AAADEVRECORDV3;

import AAADEVRECORDV3.Http.MediaListeners.MyEmailListener;
import AAADEVRECORDV3.util.AttributeStore;
import AAADEVRECORDV3.util.Constants;

import com.avaya.collaboration.bus.CollaborationBusException;
import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.email.EmailFactory;
import com.avaya.collaboration.email.EmailRequest;
import com.avaya.collaboration.util.logger.Logger;


public final class MyEmailSender
{

    private final Logger logger = Logger.getLogger(getClass());

    public void sendEmail(final String emailTo, final String emailSubject, final String emailBody) throws NoAttributeFoundException, ServiceNotFoundException
    {
        final EmailRequest emailRequest = EmailFactory.createEmailRequest();
        emailRequest.addTo(emailTo);
        emailRequest.setFrom(AttributeStore.INSTANCE.getAttributeValue(Constants.EMAIL_FROM));
        emailRequest.setSubject(emailSubject);
        emailRequest.setTextBody(emailBody);
        emailRequest.setListener(new MyEmailListener(emailRequest));
        
        try
        {
            emailRequest.send();
        }
        catch (final CollaborationBusException e)
        {
            logger.error("Could not send email request", e);
        }
    }

    public void sendCallAlertEmail(final Call call) throws NoAttributeFoundException, ServiceNotFoundException
    {
        this.sendEmail(Constants.EMAIL, "Call Alert", call.getCallingParty()
		        .getAddress() +
		        " calling " + call.getCalledParty().getAddress() + " at " +
		        System.currentTimeMillis());
    }
    
    public void sendErrorByEmail(String text, Call call){

    	final EmailRequest emailRequest = EmailFactory.createEmailRequest();
        emailRequest.addTo(AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.EMAIL));
        emailRequest.setFrom(AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.EMAIL_FROM));
        emailRequest.setSubject("ERROR AAADEVRECORDV3");
        emailRequest.setTextBody(text);
        emailRequest.setListener(new MyEmailListener(emailRequest));

    }

	public void sendErrorByEmail(String string) {
	   	final EmailRequest emailRequest = EmailFactory.createEmailRequest();
        emailRequest.addTo("umansilla@avaya.com");
        emailRequest.setFrom("lab132@collaboratory.avaya.com");
        emailRequest.setSubject("ERROR AAADEVRECORDV3");
        emailRequest.setTextBody(string);
        emailRequest.setListener(new MyEmailListener(emailRequest));
		
	}
}