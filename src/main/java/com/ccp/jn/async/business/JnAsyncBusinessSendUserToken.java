
package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.jn.commons.business.JnCommonsBusinessGetMessage;
import com.jn.commons.entities.JnEntity;
import com.jn.commons.utils.JnConstants;
import com.jn.commons.utils.JnTopic;

public class JnAsyncBusinessSendUserToken implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator>{
	
	private final JnCommonsBusinessGetMessage getMessage = new JnCommonsBusinessGetMessage();
	
	private final JnAsyncBusinessSendEmail sendEmail = new JnAsyncBusinessSendEmail();

	@Override
	public CcpMapDecorator apply(CcpMapDecorator values) {
		CcpMapDecorator transformed = values.getTransformed(JnConstants.PUT_EMAIL_TOKEN);
		String language = values.getAsString("language");
		
		this.getMessage
		.addFlow(this.sendEmail, JnEntity.email_parameters_to_send, JnEntity.email_template_message)
		.execute(JnTopic.sendUserToken, JnEntity.login_token, transformed, language);
		return values;
	}

}
