
package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.jn.commons.GetMessage;
import com.jn.commons.JnConstants;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class SendUserToken implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator>{
	
	private final GetMessage getMessage = new GetMessage();
	
	private final SendEmail sendEmail = new SendEmail();

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
