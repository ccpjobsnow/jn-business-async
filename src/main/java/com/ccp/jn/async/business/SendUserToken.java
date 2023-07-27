package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.JnBusinessTopic;

public class SendUserToken implements CcpProcess{
	
	private final SendEmail sendEmail = CcpDependencyInjection.getInjected(SendEmail.class);
	private final EmailToken emailToken = CcpDependencyInjection.getInjected(EmailToken.class);

	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {
		this.emailToken.execute(values, JnBusinessTopic.sendUserToken, JnBusinessEntity.login_token, this.sendEmail);
		return values;
	}

}
