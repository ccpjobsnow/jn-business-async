package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;

import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class NotifyError implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator>{

	private final NotifySupport notifySupport = CcpDependencyInjection.getInjected(NotifySupport.class);

	@Override
	public CcpMapDecorator apply(CcpMapDecorator values) {
		
		this.notifySupport.apply(values, JnTopic.notifyError, JnEntity.jobsnow_error);

		return values;
	}
	
	public CcpMapDecorator apply(Throwable e) {
		
		CcpMapDecorator values = new CcpMapDecorator(e);
		CcpMapDecorator execute = this.apply(values.renameKey("message", "msg"));
		return execute;
	}

}
