package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class RequestTokenAgain implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator>{

	private final NotifySupport notifySupport = new NotifySupport();

	@Override
	public CcpMapDecorator apply(CcpMapDecorator values) {
		
		//JnBusinessEntity.login_token.resetData(parameters); o que essa coisa faz aqui?
		this.notifySupport.apply(values, JnTopic.requestTokenAgain, JnEntity.request_token_again);

		return values;
	}

}
