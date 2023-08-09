
package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.jn.async.commons.others.GetMessage;
import com.jn.commons.JnConstants;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class SendUserToken implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator>{
	
	private final GetMessage getMessage = CcpDependencyInjection.getInjected(GetMessage.class);
	
	private final SendEmail sendEmail = CcpDependencyInjection.getInjected(SendEmail.class);

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
