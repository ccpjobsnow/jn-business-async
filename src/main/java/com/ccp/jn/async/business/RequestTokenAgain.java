package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.JnBusinessTopic;

public class RequestTokenAgain implements CcpProcess{

	private final NotifyContactUs notifyContactUs = CcpDependencyInjection.getInjected(NotifyContactUs.class);
	
	private final EmailToken emailToken = CcpDependencyInjection.getInjected(EmailToken.class);

	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {
		
		this.emailToken.execute(values, JnBusinessTopic.requestTokenAgain, JnBusinessEntity.request_token_again, this.notifyContactUs);
		//JnBusinessEntity.login_token.resetData(parameters); o que essa coisa faz aqui?

		return values;
	}

}
