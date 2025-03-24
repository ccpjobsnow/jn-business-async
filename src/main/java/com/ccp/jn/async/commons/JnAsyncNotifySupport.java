package com.ccp.jn.async.commons;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.jn.async.messages.JnAsyncSendMessage;
import com.jn.commons.entities.JnEntityJobsnowError;
import com.jn.commons.entities.JnEntityJobsnowPenddingError;
import com.jn.commons.exceptions.JnAsyncSupportLanguageIsMissing;

public class JnAsyncNotifySupport {
	

	public static final JnAsyncNotifySupport INSTANCE = new JnAsyncNotifySupport();
	
	private JnAsyncNotifySupport() {
		
	}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json, String topic, CcpEntity entityToSaveError, JnAsyncSendMessage sender) {
		String supportLanguage = new CcpStringDecorator("application_properties").propertiesFrom().environmentVariablesOrClassLoaderOrFile().getAsString("supportLanguage");
		
		boolean hasNotLanguage = supportLanguage.trim().isEmpty();
		
		if(hasNotLanguage) {
			throw new JnAsyncSupportLanguageIsMissing();
		}

		CcpJsonRepresentation duplicateValueFromField = json.renameField(JnEntityJobsnowError.Fields.message.name(), "msg");
		CcpJsonRepresentation result = sender
		.addDefaultProcessForEmailSending()
		.and()
		.addDefaultStepForTelegramSending()
		.soWithAllAddedProcessAnd()
		.withTheTemplateEntity(topic)
		.andWithTheEntityToBlockMessageResend(entityToSaveError)
		.andWithTheMessageValuesFromJson(duplicateValueFromField)
		.andWithTheSupportLanguage(supportLanguage)
		.sendAllMessages();
		
		JnEntityJobsnowPenddingError.ENTITY.createOrUpdate(result);
		

		return json;
	}

}
