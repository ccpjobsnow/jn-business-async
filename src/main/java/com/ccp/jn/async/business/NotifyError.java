package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class NotifyError implements CcpProcess{

	private final NotifySupport notifySupport = CcpDependencyInjection.getInjected(NotifySupport.class);

	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {
		
		this.notifySupport.execute(values, JnTopic.notifyError, JnEntity.jobsnow_error);

		return values;
	}
	
	public CcpMapDecorator execute(Throwable e) {
		CcpMapDecorator values = new CcpMapDecorator(e);
		CcpMapDecorator execute = this.execute(values);
		return execute;
	}

}
