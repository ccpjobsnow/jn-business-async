package com.ccp.jn.async.business;

import java.util.Arrays;
import java.util.List;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.jn.async.commons.others.MessagesTranslatorAndSender;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnEntity;
import com.jn.commons.JnInstantMessageBotType;
import com.jn.commons.JnTopic;

public class NotifySupport {
	private final SendInstantMessage sendInstantMessage = CcpDependencyInjection.getInjected(SendInstantMessage.class);

	private final SendEmail sendEmail = CcpDependencyInjection.getInjected(SendEmail.class);

	private final MessagesTranslatorAndSender messagesTranslatorAndSender = CcpDependencyInjection.getInjected(MessagesTranslatorAndSender.class);
	
	private final String supportLanguage;
	
	public NotifySupport() {
		this.supportLanguage =  new CcpStringDecorator("application.properties").propertiesFileFromClassLoader().getAsString("supportLanguage");
	}
	
	public CcpMapDecorator execute(CcpMapDecorator values, JnTopic topic, JnEntity entity) {

		List<CcpProcess> asList = Arrays.asList(this.sendInstantMessage, this.sendEmail);
		
		CcpMapDecorator put = values.put("botType", JnInstantMessageBotType.support).put("language", this.supportLanguage);
		
		this.messagesTranslatorAndSender.execute(put, topic, entity, asList, "telegramMessage", "emailMessage", "subject");

		return values;
	}

}
