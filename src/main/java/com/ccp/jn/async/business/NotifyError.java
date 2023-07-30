package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnTopic;

public class NotifyError implements CcpProcess{

	private final NotifyContactUs notifyContactUs = CcpDependencyInjection.getInjected(NotifyContactUs.class);
	
	
	public void sendErrorToSupport(Throwable e) {
		
		CcpMapDecorator mdError = new CcpMapDecorator(e);

		CcpMapDecorator parameters = new CcpMapDecorator().put("message", mdError);
		
		this.notifyContactUs(parameters);
	}


	@Override
	public CcpMapDecorator execute(CcpMapDecorator parameters) {

		CcpMapDecorator subjectType = this.notifyContactUs(parameters);
		
		return subjectType;
	}


	private CcpMapDecorator notifyContactUs(CcpMapDecorator parameters) {
		
		parameters = parameters.put("subjectType", JnTopic.notifyError);
		
		this.notifyContactUs.execute(parameters);
		
		return parameters;
	}


}
