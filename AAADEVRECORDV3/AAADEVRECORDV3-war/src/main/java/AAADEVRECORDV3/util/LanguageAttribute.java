package AAADEVRECORDV3.util;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.NoServiceProfileFoundException;
import com.avaya.collaboration.businessdata.api.NoUserFoundException;
import com.avaya.collaboration.businessdata.api.ServiceData;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.Participant;
import com.avaya.collaboration.dal.factory.CollaborationDataFactory;
import com.avaya.zephyr.platform.dal.api.ServiceDescriptor;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

public class LanguageAttribute {
	private final Call call;
	
	public LanguageAttribute(final Call call){
		this.call = call;
	}
	
	public String getLanguageAttribute(){
		ServiceDescriptor svc = ServiceUtil.getServiceDescriptor();
		Participant participant = call.getCalledParty();
		String handle = participant.getHandle();
		ServiceData svcData = CollaborationDataFactory.getServiceData(svc.getName(), svc.getVersion());
		try {
			String languageAtribute = svcData.getServiceAttribute(handle+"@collaboratory.avaya.com", "idioma");
			return languageAtribute;
		} catch (NoUserFoundException | NoAttributeFoundException
				| ServiceNotFoundException | NoServiceProfileFoundException e) {
			return e.toString();
		}
	}
	
}
