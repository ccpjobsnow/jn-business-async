package com.ccp.jn.async.commons;


import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.crud.CcpCrud;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.instant.messenger.CcpInstantMessenger;
import com.ccp.exceptions.instant.messenger.CcpThisBotWasBlockedByThisUser;
import com.ccp.exceptions.instant.messenger.CcpTooManyRequests;
import com.jn.commons.entities.JnEntityInstantMessengerBotLocked;
import com.jn.commons.entities.JnEntityInstantMessengerMessageSent;

public class JnAsyncSendInstantMessage {

	
	public static final JnAsyncSendInstantMessage INSTANCE = new JnAsyncSendInstantMessage();
	
	private JnAsyncSendInstantMessage() {
		
	}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {
		CcpInstantMessenger instantMessenger = CcpDependencyInjection.getDependency(CcpInstantMessenger.class);
		
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		String token = instantMessenger.getToken(values);
		
		long totalDeSegundosDecorridosDesdeMeiaNoiteDesteDia = new CcpTimeDecorator().getSecondsEnlapsedSinceMidnight();
		values = values.put("interval", totalDeSegundosDecorridosDesdeMeiaNoiteDesteDia / 3).put("token", token);
		CcpSelectUnionAll dataFromThisRecipient = crud.unionAll(values, JnEntityInstantMessengerBotLocked.INSTANCE, JnEntityInstantMessengerMessageSent.INSTANCE);

		boolean thisRecipientRecentlyReceivedThisMessageFromThisBot =  dataFromThisRecipient.isPresent(JnEntityInstantMessengerMessageSent.INSTANCE, values);

		if(thisRecipientRecentlyReceivedThisMessageFromThisBot) {
			Integer sleep = values.getAsIntegerNumber("sleep");
			new CcpTimeDecorator().sleep(sleep);
			CcpJsonRepresentation execute = this.apply(values);
			return execute;
		}

		boolean thisBotHasBeenBlocked = dataFromThisRecipient.isPresent(JnEntityInstantMessengerBotLocked.INSTANCE, values);
		
		if(thisBotHasBeenBlocked) {
			return values;
		}
		
		try {
			CcpJsonRepresentation instantMessengerData = instantMessenger.sendMessage(values);
			CcpJsonRepresentation instantMessageSent = values.putAll(instantMessengerData);
			JnEntityInstantMessengerMessageSent.INSTANCE.createOrUpdate(instantMessageSent);
			return values;
		} catch (CcpTooManyRequests e) {
			
			return this.retryToSendMessage(values);
			
		} catch(CcpThisBotWasBlockedByThisUser e) {
			return saveBlockedBot(values, e.token);
		}
	}

	private CcpJsonRepresentation retryToSendMessage(CcpJsonRepresentation values) {
		
		Integer maxTriesToSendMessage = values.getAsIntegerNumber("maxTriesToSendMessage");
		Integer triesToSendMessage = values.getOrDefault("triesToSendMessage", 1);
		
		if(triesToSendMessage >= maxTriesToSendMessage) {
			throw new RuntimeException("This message couldn't be sent. Details: " + values);
		}
		
		Integer sleepToSendMessage = values.getAsIntegerNumber("sleepToSendMessage");
		
		new CcpTimeDecorator().sleep(sleepToSendMessage);
		return this.apply(values.put("triesToSendMessage", triesToSendMessage + 1));
	}

	private CcpJsonRepresentation saveBlockedBot(CcpJsonRepresentation putAll, String token) {
		JnEntityInstantMessengerBotLocked.INSTANCE.createOrUpdate(putAll.put("token", token));
		return putAll;
	}

}
