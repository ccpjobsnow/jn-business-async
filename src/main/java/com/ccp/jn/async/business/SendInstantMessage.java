package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.dao.CcpDao;
import com.ccp.especifications.instant.messenger.CcpInstantMessenger;
import com.ccp.exceptions.http.CcpHttpInternalServerError;
import com.ccp.exceptions.instant.messenger.ThisBotWasBlockedByThisUser;
import com.ccp.exceptions.instant.messenger.TooManyRequests;
import com.ccp.process.CcpProcess;
import com.ccp.utils.Utils;
import com.jn.commons.JnEntity;

public class SendInstantMessage implements CcpProcess{

	@CcpDependencyInject
	private CcpInstantMessenger instantMessenger;

	@CcpDependencyInject
	private CcpDao dao;
	
	private final RemoveTries removeTries = CcpDependencyInjection.getInjected(RemoveTries.class);

	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {
		
		CcpMapDecorator allData = this.dao.getAllData(values, JnEntity.instant_messenger_bot_locked, JnEntity.instant_messenger_message_sent);

		boolean thisRecipientRecentlyReceivedThisMessageFromThisBot =  allData.containsKey(JnEntity.instant_messenger_message_sent.name());
		
		if(thisRecipientRecentlyReceivedThisMessageFromThisBot) {
			Utils.sleep(3000);
			CcpMapDecorator execute = this.execute(values);
			return execute;
		}

		boolean thisBotHasBeenBlocked = allData.containsKey(JnEntity.instant_messenger_bot_locked.name());
		
		if(thisBotHasBeenBlocked) {
			return values;
		}
		
		try {
			return this.tryToSendIntantMessage(values);
		} catch (TooManyRequests e) {
			Utils.sleep(3000);
			return this.execute(values);
		} catch(ThisBotWasBlockedByThisUser e) {
			return saveBlockedBot(values);
		}catch(CcpHttpInternalServerError e) {
			return this.retryToSendIntantMessage(values);
		}
	}

	private CcpMapDecorator tryToSendIntantMessage(CcpMapDecorator putAll) {
		this.instantMessenger.sendMessage(putAll);
		long totalDeSegundosDecorridosDesdeMeiaNoiteDesteDia = new CcpTimeDecorator().getTotalDeSegundosDecorridosDesdeMeiaNoiteDesteDia();
		JnEntity.instant_messenger_message_sent.createOrUpdate(putAll.put("interval", totalDeSegundosDecorridosDesdeMeiaNoiteDesteDia / 3));
		this.removeTries.execute(putAll, "tries", 3, JnEntity.instant_messenger_try_to_send_message);
		return putAll;
	}

	private CcpMapDecorator saveBlockedBot(CcpMapDecorator putAll) {
		JnEntity.instant_messenger_bot_locked.createOrUpdate(putAll);
		return putAll;
	}

	private CcpMapDecorator retryToSendIntantMessage(CcpMapDecorator putAll) {
		boolean exceededTries = JnEntity.instant_messenger_try_to_send_message.exceededTries(putAll, "tries", 3);
		
		if(exceededTries) {
			JnEntity.instant_messenger_api_unavailable.createOrUpdate(putAll);
			return putAll;
		}
		
		Utils.sleep(5000);
		return this.execute(putAll);
	}
}
