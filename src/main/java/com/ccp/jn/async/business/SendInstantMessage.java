package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.dao.CcpDao;
import com.ccp.especifications.instant.messenger.CcpInstantMessenger;
import com.ccp.exceptions.instant.messenger.ThisBotWasBlockedByThisUser;
import com.ccp.exceptions.instant.messenger.TooManyRequests;

import com.ccp.utils.Utils;
import com.jn.commons.JnEntity;

public class SendInstantMessage implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator>{

	@CcpDependencyInject
	private CcpInstantMessenger instantMessenger;

	@CcpDependencyInject
	private CcpDao dao;
	
	private SendHttpRequest sendHttpRequest = CcpDependencyInjection.getInjected(SendHttpRequest.class);
	
	@Override
	public CcpMapDecorator apply(CcpMapDecorator values) {
		
		CcpMapDecorator parametersToSendMessage = values.getInternalMap("telegram").renameKey("recipient", "chatId");
		
		CcpMapDecorator dataFromThisRecipient = this.dao.getAllData(parametersToSendMessage, JnEntity.instant_messenger_bot_locked, JnEntity.instant_messenger_message_sent);

		boolean thisRecipientRecentlyReceivedThisMessageFromThisBot =  dataFromThisRecipient.containsKey(JnEntity.instant_messenger_message_sent.name());

		if(thisRecipientRecentlyReceivedThisMessageFromThisBot) {
			Integer sleep = parametersToSendMessage.getAsIntegerNumber("sleep");
			Utils.sleep(sleep);
			CcpMapDecorator execute = this.apply(values);
			return execute;
		}

		boolean thisBotHasBeenBlocked = dataFromThisRecipient.containsKey(JnEntity.instant_messenger_bot_locked.name());
		
		if(thisBotHasBeenBlocked) {
			return values;
		}
		
		CcpMapDecorator allData = values.putAll(parametersToSendMessage);
		
		try {
			CcpMapDecorator instantMessengerData = this.sendHttpRequest.execute(allData, x -> this.instantMessenger.sendMessage(x), JnHttpRequestType.instantMessenger, "subjectType");
			long totalDeSegundosDecorridosDesdeMeiaNoiteDesteDia = new CcpTimeDecorator().getTotalDeSegundosDecorridosDesdeMeiaNoiteDesteDia();
			CcpMapDecorator instantMessageSent = allData.putAll(instantMessengerData).put("interval", totalDeSegundosDecorridosDesdeMeiaNoiteDesteDia / 3);
			JnEntity.instant_messenger_message_sent.createOrUpdate(instantMessageSent);
			return values;
		} catch (TooManyRequests e) {
			Utils.sleep(3000);
			return this.apply(values);
		} catch(ThisBotWasBlockedByThisUser e) {
			return saveBlockedBot(allData);
		}
	}

	private CcpMapDecorator saveBlockedBot(CcpMapDecorator putAll) {
		JnEntity.instant_messenger_bot_locked.createOrUpdate(putAll);
		return putAll;
	}

}
