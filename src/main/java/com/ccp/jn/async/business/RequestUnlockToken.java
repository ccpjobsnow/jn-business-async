package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.JnBusinessTopic;

public class RequestUnlockToken implements CcpProcess{

	private final NotifyContactUs notifyContactUs = CcpDependencyInjection.getInjected(NotifyContactUs.class);
	

	public CcpMapDecorator execute(CcpMapDecorator values) {
		
		CcpMapDecorator idToSearch = new CcpMapDecorator().put("name", JnBusinessTopic.requestUnlockToken.name());

		CcpMapDecorator parameters = JnBusinessEntity._static.get(idToSearch);
		
		parameters = values.putAll(parameters);

		String emailText = parameters.getFilledTemplate("emailTemplate");
		
		parameters = parameters.put("message", emailText);
		
		this.notifyContactUs.execute(parameters);
		
		JnBusinessEntity.request_unlock_token.save(parameters);

		return values;
	}

}
