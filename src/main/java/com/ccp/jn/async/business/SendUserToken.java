package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.JnBusinessTopic;

public class SendUserToken implements CcpProcess{
	
	private final SendEmail sendEmail = CcpDependencyInjection.getInjected(SendEmail.class);

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
