package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.jn.async.AsyncServices;
import com.jn.commons.JnBusinessTopic;

public class NotifyError{

	@CcpDependencyInject
	private NotifyContactUs notifyContactUs = AsyncServices.catalog.getAsObject(JnBusinessTopic.notifyContactUs.toString());
	
	public void sendErrorToSupport(Throwable e) {
		
		CcpMapDecorator parameters = new CcpMapDecorator();
		CcpMapDecorator mdError = new CcpMapDecorator(e);

		parameters = parameters.put("subjectType", "stackTrace");
		parameters = parameters.put("subject", "stackTrace");
		parameters = parameters.put("message", mdError);
		
		this.notifyContactUs.execute(parameters);
		
	}


}
