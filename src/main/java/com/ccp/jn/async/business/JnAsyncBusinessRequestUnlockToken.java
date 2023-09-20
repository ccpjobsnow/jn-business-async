package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.jn.commons.entities.JnEntityRequestUnlockToken;
import com.jn.commons.utils.JnTopic;

public class JnAsyncBusinessRequestUnlockToken implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator>{

	private final JnAsyncBusinessNotifySupport notifySupport = new JnAsyncBusinessNotifySupport();

	public CcpMapDecorator apply(CcpMapDecorator values) {
		
		this.notifySupport.apply(values, JnTopic.requestUnlockToken, new JnEntityRequestUnlockToken());

		return values;
	}

}
