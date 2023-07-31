package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class RequestUnlockToken implements CcpProcess{

	private final NotifySupport notifySupport = CcpDependencyInjection.getInjected(NotifySupport.class);

	public CcpMapDecorator execute(CcpMapDecorator values) {
		
		this.notifySupport.execute(values, JnTopic.requestUnlockToken, JnEntity.request_unlock_token);

		return values;
	}

}
