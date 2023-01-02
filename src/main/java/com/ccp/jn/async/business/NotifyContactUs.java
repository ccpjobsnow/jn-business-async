package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.jn.async.AsyncServices;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.JnBusinessTopic;

public class NotifyContactUs implements CcpProcess{

	@CcpDependencyInject
	private SendInstantMessage sendInstantMessage = AsyncServices.catalog.getAsObject(JnBusinessTopic.sendInstantMessage.name());

	@CcpDependencyInject
	private SendEmail sendEmail = AsyncServices.catalog.getAsObject(JnBusinessTopic.sendEmail.name());

	private CcpMapDecorator idToSearch = new CcpMapDecorator().put("name", JnBusinessTopic.sendEmail.name());

	
	public CcpMapDecorator execute(CcpMapDecorator values) {
	
		boolean jaFoiCadastado = JnBusinessEntity.contact_us.exists(values);
		
		if(jaFoiCadastado) {
			return values;
		}

		CcpMapDecorator parameters = JnBusinessEntity._static.get(this.idToSearch);

		parameters = values.putAll(parameters);
		
		this.sendInstantMessage.execute(parameters);

		this.sendEmail.execute(parameters);
		
		JnBusinessEntity.contact_us.save(parameters);
		
		return values;
	}

}
