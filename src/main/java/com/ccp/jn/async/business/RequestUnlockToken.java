package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.jn.async.commons.others.EmailToken;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class RequestUnlockToken implements CcpProcess{

	private final NotifyContactUs notifyContactUs = CcpDependencyInjection.getInjected(NotifyContactUs.class);

	private final EmailToken emailToken = CcpDependencyInjection.getInjected(EmailToken.class);

	public CcpMapDecorator execute(CcpMapDecorator values) {
		
		this.emailToken.execute(values, JnTopic.requestUnlockToken, JnEntity.request_unlock_token, this.notifyContactUs);

		return values;
	}

}
