package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.jn.commons.entities.JnEntityRequestTokenAgain;
import com.jn.commons.utils.JnTopic;

public class JnAsyncBusinessRequestTokenAgain implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator>{

	private final JnAsyncBusinessNotifySupport notifySupport = new JnAsyncBusinessNotifySupport();

	@Override
	public CcpMapDecorator apply(CcpMapDecorator values) {
		
		//JnBusinessEntity.login_token.resetData(parameters); o que essa coisa faz aqui?
		this.notifySupport.apply(values, JnTopic.requestTokenAgain, new JnEntityRequestTokenAgain());

		return values;
	}

}
