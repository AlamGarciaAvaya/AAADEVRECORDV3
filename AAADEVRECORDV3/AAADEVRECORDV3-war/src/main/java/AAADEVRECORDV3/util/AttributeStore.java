package AAADEVRECORDV3.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import service.AAADEVRECORDV3.MyEmailSender;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.NoServiceProfileFoundException;
import com.avaya.collaboration.businessdata.api.NoUserFoundException;
import com.avaya.collaboration.businessdata.api.ServiceData;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Participant;
import com.avaya.collaboration.dal.factory.CollaborationDataFactory;
import com.avaya.zephyr.platform.dal.api.ServiceDescriptor;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

public enum AttributeStore
{
    INSTANCE;
    private final Map<String, String> attributeMap = new HashMap<String, String>();
    private final ServiceData serviceData;

    private AttributeStore()
    {	

        final ServiceDescriptor serviceDescriptor = ServiceUtil.getServiceDescriptor();
        if (serviceDescriptor == null)
        {
            throw new IllegalStateException("Couldn't get service descriptor");
        }
        

        serviceData = CollaborationDataFactory.getServiceData(serviceDescriptor.getName(), serviceDescriptor.getVersion());
    }

    public void addAttribute(final String name, final String value)
    {
        attributeMap.put(name, value);
    }

    public String getAttributeValue(final String attributeName) throws NoAttributeFoundException, ServiceNotFoundException
    {
        String attributeValue = attributeMap.get(attributeName);
        if (StringUtils.isEmpty(attributeValue))
        {	
 
            attributeValue = serviceData.getServiceAttribute(attributeName);
        }
        return attributeValue;
    }
    
    public String getServiceProfilesAttributeValue(final Participant participantCalled, final String attributeName)
    {
        String attributeValue = attributeMap.get(attributeName);
        if (StringUtils.isEmpty(attributeValue))
        {	
        	
            try {
				attributeValue = serviceData.getServiceAttribute(participantCalled.getAddress(), attributeName);
			} catch (NoUserFoundException | NoAttributeFoundException
					| ServiceNotFoundException | NoServiceProfileFoundException e) {
				new MyEmailSender().sendErrorByEmail("Error al obtener Atributo por Service Profile");
			}
        }
        
        return attributeValue;
    }
}