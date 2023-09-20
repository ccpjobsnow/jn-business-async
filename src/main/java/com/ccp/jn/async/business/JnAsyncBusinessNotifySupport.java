package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.especifications.db.utils.CcpEntity;
import com.jn.commons.business.JnCommonsBusinessGetMessage;
import com.jn.commons.entities.JnEntityEmailParametersToSend;
import com.jn.commons.entities.JnEntityEmailTemplateMessage;
import com.jn.commons.entities.JnEntityInstantMessengerParametersToSend;
import com.jn.commons.entities.JnEntityInstantMessengerTemplateMessage;
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
	
	public CcpMapDecorator apply(CcpMapDecorator values, JnTopic topic, CcpEntity entity) {

		CcpMapDecorator renameKey = values.renameKey("message", "msg");
		this.getMessage
		.addLenientFlow(this.sendInstantMessage, new JnEntityInstantMessengerParametersToSend(), new JnEntityInstantMessengerTemplateMessage())
		.addLenientFlow(this.sendEmail, new JnEntityEmailParametersToSend(), new JnEntityEmailTemplateMessage())
		.execute(topic, entity, renameKey, this.supportLanguage);

		return values;
	}

}
