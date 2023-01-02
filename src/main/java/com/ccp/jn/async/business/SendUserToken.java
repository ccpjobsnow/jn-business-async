package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.jn.async.AsyncServices;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.JnBusinessTopic;

public class SendUserToken implements CcpProcess{
	
	@CcpDependencyInject
	private SendEmail sendEmail = AsyncServices.catalog.getAsObject(JnBusinessTopic.sendEmail.name());

	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {
	
		CcpMapDecorator idToSearch = new CcpMapDecorator().put("name", JnBusinessTopic.sendUserToken.name());

		CcpMapDecorator parameters = JnBusinessEntity._static.get(idToSearch);
		
		parameters = values.putAll(parameters);

		String emailText = parameters.getFilledTemplate("emailTemplate");
		
		parameters = parameters.put("message", emailText);
		
		this.sendEmail.execute(parameters);
		
		return values;
	}

}
