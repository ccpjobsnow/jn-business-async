
package com.ccp.jn.async.business.login;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.messages.JnAsyncSendMessage;
import com.jn.commons.entities.JnEntityLoginToken;
import com.jn.commons.utils.JnAsyncBusiness;

public class JnAsyncBusinessSendUserToken implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{
	
	public static final JnAsyncBusinessSendUserToken INSTANCE = new JnAsyncBusinessSendUserToken();
	
	private JnAsyncBusinessSendUserToken() {}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		CcpJsonRepresentation entityValue = json.putRandomPassword(8, "token", "tokenHash").renameField("originalEmail", "email");
		String language = json.getAsString("language");
		
		String topic = JnAsyncBusiness.sendUserToken.name();
		JnAsyncSendMessage getMessage = new JnAsyncSendMessage();
		
		getMessage
		.addDefaultProcessForEmailSending()
		.soWithAllAddedProcessAnd()
		.withTheTemplateEntity(topic)
		.andWithTheEntityToBlockMessageResend(JnEntityLoginToken.ENTITY)
		.andWithTheMessageValuesFromJson(entityValue)
		.andWithTheSupportLanguage(language)
		.sendAllMessages()
		;

		return json;
	}

}
