package com.ccp.jn.async.business;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.jn.async.AsyncServices;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.JnBusinessTopic;

public class NotifyError implements CcpProcess{

	@CcpDependencyInject
	private NotifyContactUs notifyContactUs = AsyncServices.catalog.getAsObject(JnBusinessTopic.notifyContactUs.toString());
	
	CcpMapDecorator idToSearch = new CcpMapDecorator().put("name", JnBusinessTopic.notifyError.name());

	
	public void sendErrorToSupport(Throwable e) {
		
		CcpMapDecorator parameters = JnBusinessEntity._static.get(this.idToSearch, CcpConstants.DO_NOTHING);
		CcpMapDecorator mdError = new CcpMapDecorator(e);

		parameters = parameters.put("message", mdError);
		
		this.notifyContactUs.execute(parameters);
		
	}


	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {
		CcpMapDecorator parameters = JnBusinessEntity._static.get(this.idToSearch, CcpConstants.DO_NOTHING);
		parameters = parameters.putAll(values);
		this.notifyContactUs.execute(parameters);
		return values;
	}


}
