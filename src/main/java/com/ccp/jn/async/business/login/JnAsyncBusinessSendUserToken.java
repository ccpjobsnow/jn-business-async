
package com.ccp.jn.async.business.login;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.messages.JnAsyncSendMessage;
import com.jn.commons.entities.JnEntityEmailTemplateMessage;
import com.jn.commons.entities.JnEntityInstantMessengerMessageSent;
import com.jn.commons.entities.JnEntityInstantMessengerParametersToSend;
import com.jn.commons.entities.JnEntityLoginToken;
import com.jn.commons.utils.JnAsyncBusiness;

public class JnAsyncBusinessSendUserToken implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{
	
	public static final JnAsyncBusinessSendUserToken INSTANCE = new JnAsyncBusinessSendUserToken();
	
	private JnAsyncBusinessSendUserToken() {}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		String language = json.getAsString(JnEntityEmailTemplateMessage.Fields.language.name());
		CcpJsonRepresentation jsonPiece = JnEntityLoginToken.ENTITY.getHandledJson(json);
		
		String topic = JnAsyncBusiness.sendUserToken.name();
		JnAsyncSendMessage getMessage = new JnAsyncSendMessage();
		
		CcpJsonRepresentation request = json.getInnerJson("request");
		CcpJsonRepresentation duplicateValueFromField = request.putAll(jsonPiece)
				.duplicateValueFromField("originalEmail", JnEntityLoginToken.Fields.email.name(), 
						JnEntityInstantMessengerParametersToSend.Fields.recipient.name())
				.duplicateValueFromField("originalToken", JnEntityInstantMessengerMessageSent.Fields.token.name())
				;
		getMessage
		.addDefaultProcessForEmailSending()
		.soWithAllAddedProcessAnd()
		.withTheTemplateEntity(topic)
		.andWithTheEntityToBlockMessageResend(JnEntityLoginToken.ENTITY)
		.andWithTheMessageValuesFromJson(duplicateValueFromField)
		.andWithTheSupportLanguage(language)
		.sendAllMessages()
		;

		return json;
	}

}
