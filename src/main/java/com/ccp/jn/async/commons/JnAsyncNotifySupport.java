package com.ccp.jn.async.commons;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.jn.async.messages.JnAsyncUtilsGetMessage;
import com.jn.commons.entities.JnEntityEmailMessageSent;

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
		.createDefaultEmailStep()
		.and()
		.createDefaultTelegramStep()
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
