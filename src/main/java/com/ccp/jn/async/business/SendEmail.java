package com.ccp.jn.async.business;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.email.CcpEmailSender;
import com.ccp.especifications.email.CcpEmailSender.EmailWasNotSent;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.JnBusinessTopic;

public class SendEmail implements CcpProcess{

	@CcpDependencyInject
	private CcpEmailSender emailSender;

	CcpMapDecorator idToSearch = new CcpMapDecorator().put("name", JnBusinessTopic.sendEmail.name());
	
	
	public CcpMapDecorator execute(CcpMapDecorator values) {
		
		CcpMapDecorator emailParameters = JnBusinessEntity._static.get(this.idToSearch, CcpConstants.DO_NOTHING);
		
		CcpMapDecorator parametersToSendEmail = values.putAll(emailParameters);
		try {
			this.emailSender.send(parametersToSendEmail);
		} catch (EmailWasNotSent e) {
			//tratamento para erro de não envio e demais tratamentos para evitar, dentre outras coisas, o spam
		}
		
		return values;
	}

}
