package com.ccp.jn.async.business;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.dao.CcpDao;
import com.ccp.especifications.instant.messenger.CcpInstantMessenger;
import com.ccp.exceptions.instant.messenger.CcpThisBotWasBlockedByThisUser;
import com.ccp.exceptions.instant.messenger.CcpTooManyRequests;
import com.jn.commons.entities.JnEntityInstantMessengerBotLocked;
import com.jn.commons.entities.JnEntityInstantMessengerMessageSent;

public class JnAsyncBusinessSendInstantMessage implements  java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private CcpInstantMessenger instantMessenger = CcpDependencyInjection.getDependency(CcpInstantMessenger.class);

	private CcpDao dao = CcpDependencyInjection.getDependency(CcpDao.class);
	
	
	@Override
	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {

		String token = this.instantMessenger.getToken(values);
		
		JnEntityInstantMessengerBotLocked jnEntityInstantMessengerBotLocked = new JnEntityInstantMessengerBotLocked();
		JnEntityInstantMessengerMessageSent jnEntityInstantMessengerMessageSent = new JnEntityInstantMessengerMessageSent();
		CcpJsonRepresentation dataFromThisRecipient = this.dao.getAllData(values.put("token", token), jnEntityInstantMessengerBotLocked, jnEntityInstantMessengerMessageSent);

		boolean thisRecipientRecentlyReceivedThisMessageFromThisBot =  dataFromThisRecipient.containsKey(jnEntityInstantMessengerMessageSent.name());

		if(thisRecipientRecentlyReceivedThisMessageFromThisBot) {
			Integer sleep = values.getAsIntegerNumber("sleep");
			new CcpTimeDecorator().sleep(sleep);
			CcpJsonRepresentation execute = this.apply(values);
			return execute;
		}

		boolean thisBotHasBeenBlocked = dataFromThisRecipient.containsKey(jnEntityInstantMessengerBotLocked.name());
		
		if(thisBotHasBeenBlocked) {
			return values;
		}
		
		try {
			CcpJsonRepresentation instantMessengerData = this.instantMessenger.sendMessage(values);
			long totalDeSegundosDecorridosDesdeMeiaNoiteDesteDia = new CcpTimeDecorator().getSecondsEnlapsedSinceMidnight();
			CcpJsonRepresentation instantMessageSent = values.putAll(instantMessengerData).put("interval", totalDeSegundosDecorridosDesdeMeiaNoiteDesteDia / 3);
			new JnEntityInstantMessengerMessageSent().createOrUpdate(instantMessageSent);
			return values;
		} catch (CcpTooManyRequests e) {
			
			return this.retryToSendMessage(values);
			
		} catch(CcpThisBotWasBlockedByThisUser e) {
			return saveBlockedBot(values, e.token);
		}
	}

	private CcpJsonRepresentation retryToSendMessage(CcpJsonRepresentation values) {
		
		Integer maxTriesToSendMessage = values.getAsIntegerNumber("maxTriesToSendMessage");
		Integer triesToSendMessage = values.getAsIntegerNumber("triesToSendMessage");
		
		if(triesToSendMessage == null) {
			triesToSendMessage = 1;
		}
		
		if(triesToSendMessage >= maxTriesToSendMessage) {
			throw new RuntimeException("This message couldn't be sent. Details: " + values);
		}
		
		Integer sleepToSendMessage = values.getAsIntegerNumber("sleepToSendMessage");
		
		new CcpTimeDecorator().sleep(sleepToSendMessage);
		return this.apply(values.put("triesToSendMessage", triesToSendMessage + 1));
	}

	private CcpJsonRepresentation saveBlockedBot(CcpJsonRepresentation putAll, String token) {
		new JnEntityInstantMessengerBotLocked().createOrUpdate(putAll.put("token", token));
		return putAll;
	}

}
