package com.ccp.jn.async.business;

import com.ccp.decorators.CcpJsonRepresentation;
import com.jn.commons.entities.JnEntityRequestUnlockToken;
import com.jn.commons.utils.JnTopics;

public class JnAsyncBusinessRequestUnlockToken implements  java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private final JnAsyncBusinessNotifySupport notifySupport = new JnAsyncBusinessNotifySupport();

	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {
		
		JnEntityRequestUnlockToken entity = new JnEntityRequestUnlockToken();
		
		this.notifySupport.apply(values, JnTopics.requestUnlockToken.getTopicName(), entity);

		return values;
	}

}
