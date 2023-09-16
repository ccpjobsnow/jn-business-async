package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.decorators.CcpStringDecorator;
import com.jn.commons.business.JnCommonsBusinessGetMessage;
import com.jn.commons.entities.JnEntity;
import com.jn.commons.utils.JnTopic;

public class JnAsyncBusinessNotifySupport {
	
	private final JnAsyncBusinessTryToSendInstantMessage sendInstantMessage = new JnAsyncBusinessTryToSendInstantMessage();

	private final JnCommonsBusinessGetMessage getMessage = new JnCommonsBusinessGetMessage();

	private final JnAsyncBusinessSendEmail sendEmail = new JnAsyncBusinessSendEmail();
	
	private final String supportLanguage;
	
	public JnAsyncBusinessNotifySupport() {

		this.supportLanguage =  new CcpStringDecorator("application.properties").propertiesFrom().environmentVariablesOrClassLoaderOrFile().getAsString("supportLanguage");
	
		if(this.supportLanguage.trim().isEmpty()) {
			throw new RuntimeException("It is missing the configuration 'supportLanguage'");
		}
	}
	
	public CcpMapDecorator apply(CcpMapDecorator values, JnTopic topic, JnEntity entity) {

		CcpMapDecorator renameKey = values.renameKey("message", "msg");
		this.getMessage
		.addLenientFlow(this.sendInstantMessage, JnEntity.instant_messenger_parameters_to_send, JnEntity.instant_messenger_template_message)
		.addLenientFlow(this.sendEmail, JnEntity.email_parameters_to_send, JnEntity.email_template_message)
		.execute(topic, entity, renameKey, this.supportLanguage);

		return values;
	}

}
