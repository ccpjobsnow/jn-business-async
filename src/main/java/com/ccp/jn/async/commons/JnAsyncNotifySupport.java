package com.ccp.jn.async.commons;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.jn.async.business.commons.JnAsyncBusinessSendEmailMessage;
import com.ccp.jn.async.business.commons.JnAsyncBusinessTryToSendInstantMessage;
import com.ccp.jn.async.messages.JnAsyncUtilsGetMessage;
import com.jn.commons.entities.JnEntityEmailMessageSent;
import com.jn.commons.entities.JnEntityEmailParametersToSend;
import com.jn.commons.entities.JnEntityEmailTemplateMessage;
import com.jn.commons.entities.JnEntityInstantMessengerParametersToSend;
import com.jn.commons.entities.JnEntityInstantMessengerTemplateMessage;

public class JnAsyncNotifySupport {
	

	public static final JnAsyncNotifySupport INSTANCE = new JnAsyncNotifySupport();
	
	private JnAsyncNotifySupport() {
		
	}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json, String topic, CcpEntity entityToSaveError, JnAsyncUtilsGetMessage getMessage) {
		String supportLanguage = new CcpStringDecorator("application_properties").propertiesFrom().environmentVariablesOrClassLoaderOrFile().getAsString("supportLanguage");
		
		if(supportLanguage.trim().isEmpty()) {
			throw new RuntimeException("It is missing the configuration 'supportLanguage'");
		}

		CcpJsonRepresentation renameKey = json.duplicateValueFromField("message", "msg");
		getMessage
		.createStep()
		.withProcess(JnAsyncBusinessSendEmailMessage.INSTANCE)
		.andWithParametersEntity(JnEntityEmailParametersToSend.INSTANCE)
		.andWithTemplateEntity(JnEntityEmailTemplateMessage.INSTANCE)
		.andCreateAnotherStep()
		.withProcess(JnAsyncBusinessTryToSendInstantMessage.INSTANCE)
		.andWithParametersEntity(JnEntityInstantMessengerParametersToSend.INSTANCE)
		.andWithTemplateEntity(JnEntityInstantMessengerTemplateMessage.INSTANCE)
		.soWithAllAddedStepsAnd()
		.withTemplateEntity(topic)
		.andWithEntityToSave(JnEntityEmailMessageSent.INSTANCE)
		.andWithJsonValues(renameKey)
		.andWithSupportLanguage(supportLanguage)
		.executeAllAddedSteps()
		;
		

		return json;
	}

}
