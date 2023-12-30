
package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.jn.commons.business.JnCommonsBusinessGetMessage;
import com.jn.commons.entities.JnEntityEmailParametersToSend;
import com.jn.commons.entities.JnEntityEmailTemplateMessage;
import com.jn.commons.entities.JnEntityLoginToken;
import com.jn.commons.utils.JnConstants;
import com.jn.commons.utils.JnTopic;

public class JnAsyncBusinessSendUserToken implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator>{
	
	private final JnCommonsBusinessGetMessage getMessage = new JnCommonsBusinessGetMessage();
	
	private final JnAsyncBusinessSendEmail stepToSendEmail = new JnAsyncBusinessSendEmail();

	@Override
	public CcpMapDecorator apply(CcpMapDecorator values) {
		CcpMapDecorator entityValue = values.getTransformed(JnConstants.PUT_EMAIL_TOKEN);
		String language = values.getAsString("language");
		
		JnEntityEmailParametersToSend parametersToSendEmail = new JnEntityEmailParametersToSend();
		JnEntityEmailTemplateMessage templateToSendEmail = new JnEntityEmailTemplateMessage();
		JnEntityLoginToken entityToSave = new JnEntityLoginToken();
		JnTopic entityId = JnTopic.sendUserToken;
		this.getMessage
		.addOneStep(this.stepToSendEmail, parametersToSendEmail, templateToSendEmail)
		.executeAllSteps(entityId, entityToSave, entityValue, language);
		return values;
	}

}
