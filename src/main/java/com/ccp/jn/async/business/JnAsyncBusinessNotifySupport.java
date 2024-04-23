package com.ccp.jn.async.business;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.especifications.db.utils.CcpEntity;
import com.jn.commons.business.utils.JnCommonsBusinessUtilsGetMessage;
import com.jn.commons.entities.JnEntityEmailParametersToSend;
import com.jn.commons.entities.JnEntityEmailTemplateMessage;
import com.jn.commons.entities.JnEntityInstantMessengerParametersToSend;
import com.jn.commons.entities.JnEntityInstantMessengerTemplateMessage;

public class JnAsyncBusinessNotifySupport {
	
	private final JnAsyncBusinessTryToSendInstantMessage sendInstantMessage = new JnAsyncBusinessTryToSendInstantMessage();

	private final JnCommonsBusinessUtilsGetMessage getMessage = new JnCommonsBusinessUtilsGetMessage();

	private final JnAsyncBusinessSendEmail sendEmail = new JnAsyncBusinessSendEmail();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation values, String topic, CcpEntity entity) {
		String supportLanguage = new CcpStringDecorator("application_properties").propertiesFrom().environmentVariablesOrClassLoaderOrFile().getAsString("supportLanguage");
		
		if(supportLanguage.trim().isEmpty()) {
			throw new RuntimeException("It is missing the configuration 'supportLanguage'");
		}

		CcpJsonRepresentation renameKey = values.renameKey("message", "msg");
		
		JnEntityInstantMessengerParametersToSend instantMessengerParametersToSend = new JnEntityInstantMessengerParametersToSend();
		JnEntityInstantMessengerTemplateMessage instantMessengerTemplateMessage = new JnEntityInstantMessengerTemplateMessage();
		JnEntityEmailParametersToSend emailParametersToSend = new JnEntityEmailParametersToSend();
		JnEntityEmailTemplateMessage emailTemplateMessage = new JnEntityEmailTemplateMessage();
		
		this.getMessage
		.addOneLenientStep(this.sendInstantMessage, instantMessengerParametersToSend, instantMessengerTemplateMessage)
		.addOneLenientStep(this.sendEmail, emailParametersToSend, emailTemplateMessage)
		.executeAllSteps(topic, entity, renameKey, supportLanguage);

		return values;
	}

}
