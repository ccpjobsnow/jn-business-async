package com.ccp.jn.async.business;

import com.ccp.decorators.CcpJsonRepresentation;
import com.jn.commons.entities.JnEntityJobsnowError;
import com.jn.commons.utils.JnTopics;

public class JnAsyncBusinessNotifyError implements  java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private final JnAsyncBusinessNotifySupport notifySupport = new JnAsyncBusinessNotifySupport();

	@Override
	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {
		
		this.notifySupport.apply(values, JnTopics.notifyError.getTopicName(), new JnEntityJobsnowError());

		return values;
	}
	
	public CcpJsonRepresentation apply(Throwable e) {
		
		CcpJsonRepresentation values = new CcpJsonRepresentation(e);
		CcpJsonRepresentation execute = this.apply(values.renameKey("message", "msg"));
		return execute;
	}

}
