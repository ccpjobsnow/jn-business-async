package com.ccp.jn.async.commons;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.jn.async.messages.JnAsyncSendMessage;

public class JnAsyncNotifySupport {
	

	public static final JnAsyncNotifySupport INSTANCE = new JnAsyncNotifySupport();
	
	private JnAsyncNotifySupport() {
		
	}
	
	public CcpJsonRepresentation apply1(CcpJsonRepresentation json, String topic, CcpEntity entityToSaveError, JnAsyncSendMessage sender) {
		String supportLanguage = new CcpStringDecorator("application_properties").propertiesFrom().environmentVariablesOrClassLoaderOrFile().getAsString("supportLanguage");
		
		if(supportLanguage.trim().isEmpty()) {
			throw new RuntimeException("It is missing the configuration 'supportLanguage'");
		}

		CcpJsonRepresentation renameKey = json.duplicateValueFromField("message", "msg");
		
		sender
		.addDefaultProcessForEmailSending()
		.and()
		.addDefaultStepForTelegramSending()
		.soWithAllAddedProcessAnd()
		.withTheTemplateEntity(topic)
		.andWithTheEntityToBlockMessageResend(entityToSaveError)
		.andWithTheMessageValuesFromJson(renameKey)
		.andWithTheSupportLanguage(supportLanguage)
		.sendAllMessages()
		;
		

		return json;
	}

}
