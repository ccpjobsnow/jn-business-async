package com.ccp.jn.async.business;

import com.ccp.decorators.CcpJsonRepresentation;
import com.jn.commons.entities.JnEntityJobsnowError;
import com.jn.commons.utils.JnTopics;

public class JnAsyncBusinessNotifyError implements  java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	@Override
	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {
		
		final JnAsyncBusinessNotifySupport notifySupport = new JnAsyncBusinessNotifySupport();
		
		notifySupport.apply(values, JnTopics.notifyError.name(), new JnEntityJobsnowError());

		return values;
	}
	
	public CcpJsonRepresentation apply(Throwable e) {
		
		CcpJsonRepresentation values = new CcpJsonRepresentation(e);
		CcpJsonRepresentation execute = this.apply(values.renameKey("message", "msg"));
		return execute;
	}

}
