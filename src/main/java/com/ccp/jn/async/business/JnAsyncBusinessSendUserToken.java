
package com.ccp.jn.async.business;

import com.ccp.decorators.CcpJsonRepresentation;
import com.jn.commons.business.utils.JnCommonsBusinessUtilsGetMessage;
import com.jn.commons.entities.JnEntityEmailParametersToSend;
import com.jn.commons.entities.JnEntityEmailTemplateMessage;
import com.jn.commons.entities.JnEntityLoginToken;
import com.jn.commons.utils.JnConstants;
import com.jn.commons.utils.JnTopics;

public class JnAsyncBusinessSendUserToken implements  java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation>{
	
	private final JnCommonsBusinessUtilsGetMessage getMessage = new JnCommonsBusinessUtilsGetMessage();
	
	private final JnAsyncBusinessSendEmail stepToSendEmail = new JnAsyncBusinessSendEmail();

	@Override
	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {
		CcpJsonRepresentation entityValue = values.getTransformed(JnConstants.PUT_EMAIL_TOKEN);
		String language = values.getAsString("language");
		
		this.getMessage
		.addOneStep(this.stepToSendEmail, JnEntityEmailParametersToSend.INSTANCE, JnEntityEmailTemplateMessage.INSTANCE)
		.executeAllSteps(JnTopics.sendUserToken.name(), JnEntityLoginToken.INSTANCE, entityValue, language);
		return values;
	}

}
