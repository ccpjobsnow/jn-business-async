package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.jn.async.commons.others.MessagesTranslation;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class NotifyContactUs implements CcpProcess{

	private final SendInstantMessage sendInstantMessage = CcpDependencyInjection.getInjected(SendInstantMessage.class);

	private final SendEmail sendEmail = CcpDependencyInjection.getInjected(SendEmail.class);

	private MessagesTranslation messagesTranslation = CcpDependencyInjection.getInjected(MessagesTranslation.class);

	public CcpMapDecorator execute(CcpMapDecorator values) {
	
		boolean jaFoiCadastado = JnEntity.contact_us.exists(values);
		
		if(jaFoiCadastado) {
			return values;
		}

		
		return values;
	}

}
