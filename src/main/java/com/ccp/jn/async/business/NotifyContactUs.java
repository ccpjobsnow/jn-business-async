package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class NotifyContactUs implements CcpProcess{

	private final SendInstantMessage sendInstantMessage = CcpDependencyInjection.getInjected(SendInstantMessage.class);

	private final SendEmail sendEmail = CcpDependencyInjection.getInjected(SendEmail.class);

	private CcpMapDecorator idToSearch = new CcpMapDecorator().put("name", JnTopic.sendEmail.name());

	
	public CcpMapDecorator execute(CcpMapDecorator values) {
	
		boolean jaFoiCadastado = JnEntity.contact_us.exists(values);
		
		if(jaFoiCadastado) {
			return values;
		}

		CcpMapDecorator parameters = JnEntity.messages.getOneById(this.idToSearch);

		parameters = values.putAll(parameters);
		
		this.sendInstantMessage.execute(parameters);

		this.sendEmail.execute(parameters);
		
		JnEntity.contact_us.createOrUpdate(parameters);
		
		return values;
	}

}
