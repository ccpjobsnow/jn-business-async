package com.ccp.jn.async.commons;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.jn.async.business.commons.JnAsyncBusinessSendEmailMessage;
import com.ccp.jn.async.business.commons.JnAsyncBusinessTryToSendInstantMessage;
import com.jn.commons.business.utils.CommonsBusinessUtilsGetMessage;
import com.jn.commons.entities.JnEntityEmailParametersToSend;
import com.jn.commons.entities.JnEntityEmailTemplateMessage;
import com.jn.commons.entities.JnEntityInstantMessengerParametersToSend;
import com.jn.commons.entities.JnEntityInstantMessengerTemplateMessage;

public class JnAsyncBusinessNotifySupport {
	

	public static final JnAsyncBusinessNotifySupport INSTANCE = new JnAsyncBusinessNotifySupport();
	
	private JnAsyncBusinessNotifySupport() {
		
	}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation values, String topic, CcpEntity entity) {
		String supportLanguage = new CcpStringDecorator("application_properties").propertiesFrom().environmentVariablesOrClassLoaderOrFile().getAsString("supportLanguage");
		
		if(supportLanguage.trim().isEmpty()) {
			throw new RuntimeException("It is missing the configuration 'supportLanguage'");
		}

		CcpJsonRepresentation renameKey = values.renameKey("message", "msg");
		
		CommonsBusinessUtilsGetMessage jnCommonsBusinessUtilsGetMessage = new CommonsBusinessUtilsGetMessage();
		jnCommonsBusinessUtilsGetMessage
		.addOneLenientStep(JnAsyncBusinessTryToSendInstantMessage.INSTANCE, JnEntityInstantMessengerParametersToSend.INSTANCE, JnEntityInstantMessengerTemplateMessage.INSTANCE)
		.addOneLenientStep(JnAsyncBusinessSendEmailMessage.INSTANCE, JnEntityEmailParametersToSend.INSTANCE, JnEntityEmailTemplateMessage.INSTANCE)
		.executeAllSteps(topic, entity, renameKey, supportLanguage);

		return values;
	}

}
