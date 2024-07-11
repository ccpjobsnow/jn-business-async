
package com.ccp.jn.async.business.login;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.business.commons.JnAsyncBusinessSendEmailMessage;
import com.ccp.jn.async.commons.JnAsyncUtilsGetMessage;
import com.ccp.json.transformers.CcpJsonTransformerGenerateRandomTokenWithHash;
import com.jn.commons.entities.JnEntityEmailParametersToSend;
import com.jn.commons.entities.JnEntityEmailTemplateMessage;
import com.jn.commons.entities.JnEntityLoginToken;
import com.jn.commons.utils.JnAsyncBusiness;

public class JnAsyncBusinessSendUserToken implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{
	
	public static final JnAsyncBusinessSendUserToken INSTANCE = new JnAsyncBusinessSendUserToken();
	
	private JnAsyncBusinessSendUserToken() {
		
	}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		CcpJsonTransformerGenerateRandomTokenWithHash transformer = new CcpJsonTransformerGenerateRandomTokenWithHash(8, "token", "tokenHash");
		CcpJsonRepresentation entityValue = json.getTransformed(transformer).renameField("originalEmail", "email");
		String language = json.getAsString("language");
		
		JnAsyncUtilsGetMessage jnCommonsBusinessUtilsGetMessage = new JnAsyncUtilsGetMessage();
		String name = JnAsyncBusiness.sendUserToken.name();
		jnCommonsBusinessUtilsGetMessage
		.addOneStep(JnAsyncBusinessSendEmailMessage.INSTANCE, JnEntityEmailParametersToSend.INSTANCE, JnEntityEmailTemplateMessage.INSTANCE)
		.executeAllSteps(name, JnEntityLoginToken.INSTANCE, entityValue, language);
		return json;
	}

}
