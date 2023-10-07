package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.jn.commons.entities.JnEntityJobsnowError;
import com.jn.commons.utils.JnTopic;

public class JnAsyncBusinessNotifyError implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator>{

	private final JnAsyncBusinessNotifySupport notifySupport = new JnAsyncBusinessNotifySupport();

	@Override
	public CcpMapDecorator apply(CcpMapDecorator values) {
		
		this.notifySupport.apply(values, JnTopic.notifyError, new JnEntityJobsnowError());

		return values;
	}
	
	public CcpMapDecorator apply(Throwable e) {
		
		CcpMapDecorator values = new CcpMapDecorator(e);
		CcpMapDecorator execute = this.apply(values.renameKey("message", "msg"));
		return execute;
	}

}
