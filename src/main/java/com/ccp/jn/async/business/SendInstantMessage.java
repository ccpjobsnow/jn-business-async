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
		
		CcpMapDecorator allData = this.dao.getAllData(values, JnEntity.instant_messenger_bot_locked, JnEntity.instant_messenger_message_sent);

		boolean thisRecipientRecentlyReceivedThisMessageFromThisBot =  allData.containsKey(JnEntity.instant_messenger_message_sent.name());
		
		if(thisRecipientRecentlyReceivedThisMessageFromThisBot) {
			Utils.sleep(3000);
			CcpMapDecorator execute = this.apply(values);
			return execute;
		}

		boolean thisBotHasBeenBlocked = allData.containsKey(JnEntity.instant_messenger_bot_locked.name());
		
		if(thisBotHasBeenBlocked) {
			return values;
		}
		
		try {
			CcpMapDecorator instantMessengerData = this.sendHttpRequest.execute(values, x -> this.instantMessenger.sendMessage(x), "subjectType");
			long totalDeSegundosDecorridosDesdeMeiaNoiteDesteDia = new CcpTimeDecorator().getTotalDeSegundosDecorridosDesdeMeiaNoiteDesteDia();
			CcpMapDecorator instantMessageSent = values.putAll(instantMessengerData).put("interval", totalDeSegundosDecorridosDesdeMeiaNoiteDesteDia / 3);
			JnEntity.instant_messenger_message_sent.createOrUpdate(instantMessageSent);
			return values;
		} catch (TooManyRequests e) {
			Utils.sleep(3000);
			return this.apply(values);
		} catch(ThisBotWasBlockedByThisUser e) {
			return saveBlockedBot(values);
		}
	}

	private CcpMapDecorator saveBlockedBot(CcpMapDecorator putAll) {
		JnEntity.instant_messenger_bot_locked.createOrUpdate(putAll);
		return putAll;
	}

}
