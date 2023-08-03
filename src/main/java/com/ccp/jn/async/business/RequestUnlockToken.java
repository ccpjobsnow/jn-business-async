package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;

import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class RequestUnlockToken implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator>{

	private final NotifySupport notifySupport = CcpDependencyInjection.getInjected(NotifySupport.class);

	public CcpMapDecorator apply(CcpMapDecorator values) {
		
		this.notifySupport.apply(values, JnTopic.requestUnlockToken, JnEntity.request_unlock_token);

		return values;
	}

}
