
package com.ccp.jn.async.business.login;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.business.commons.JnAsyncBusinessSendEmailMessage;
import com.ccp.jn.async.messages.JnAsyncUtilsGetMessage;
import com.jn.commons.entities.JnEntityEmailMessageSent;
import com.jn.commons.entities.JnEntityEmailParametersToSend;
import com.jn.commons.entities.JnEntityEmailTemplateMessage;
import com.jn.commons.utils.JnAsyncBusiness;

public class JnAsyncBusinessSendUserToken implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{
	
	public static final JnAsyncBusinessSendUserToken INSTANCE = new JnAsyncBusinessSendUserToken();
	
	private JnAsyncBusinessSendUserToken() {
		
	}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		CcpJsonRepresentation entityValue = json.putRandomPassword(8, "token", "tokenHash").renameField("originalEmail", "email");
		String language = json.getAsString("language");
		
		String topic = JnAsyncBusiness.sendUserToken.name();
		JnAsyncUtilsGetMessage getMessage = new JnAsyncUtilsGetMessage();
		
		getMessage
		.createStep()
		.withProcess(JnAsyncBusinessSendEmailMessage.INSTANCE)
		.andWithParametersEntity(JnEntityEmailParametersToSend.INSTANCE)
		.andWithTemplateEntity(JnEntityEmailTemplateMessage.INSTANCE)
		.soWithAllAddedStepsAnd()
		.withTemplateEntity(topic)
		.andWithEntityToSave(JnEntityEmailMessageSent.INSTANCE)
		.andWithJsonValues(entityValue)
		.andWithSupportLanguage(language)
		.executeAllAddedSteps()
		;

		return json;
	}

}
