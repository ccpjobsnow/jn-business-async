package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.email.CcpEmailSender;
import com.ccp.process.CcpProcess;

public class SendEmail implements CcpProcess{

	@CcpDependencyInject
	private CcpEmailSender emailSender;

	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {
		// TODO Auto-generated method stub
		return null;
	}

}
