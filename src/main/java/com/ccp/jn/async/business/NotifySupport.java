package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.jn.async.commons.others.GetMessage;
import com.ccp.jn.async.commons.others.TryToSendInstantMessage;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class NotifySupport {
	
	private final TryToSendInstantMessage sendInstantMessage = CcpDependencyInjection.getInjected(TryToSendInstantMessage.class);

	private final GetMessage getMessage = CcpDependencyInjection.getInjected(GetMessage.class);

	private final SendEmail sendEmail = CcpDependencyInjection.getInjected(SendEmail.class);
	
	private final String supportLanguage;
	
	public NotifySupport() {

		this.supportLanguage =  new CcpStringDecorator("application.properties").propertiesFileFromClassLoader().getAsString("supportLanguage");
	
		if(this.supportLanguage.trim().isEmpty()) {
			throw new RuntimeException("It is missing the configuration 'supportLanguage'");
		}
	}
	
	public CcpMapDecorator apply(CcpMapDecorator values, JnTopic topic, JnEntity entity) {

		this.getMessage
		.addLenientFlow(this.sendInstantMessage, JnEntity.instant_messenger_parameters_to_send, JnEntity.instant_messenger_template_message)
		.addLenientFlow(this.sendEmail, JnEntity.email_parameters_to_send, JnEntity.email_template_message)
		.execute(topic, entity, values, this.supportLanguage);

		return values;
	}

}
