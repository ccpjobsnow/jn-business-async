package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.instant.messenger.CcpInstantMessenger;
import com.ccp.especifications.instant.messenger.CcpInstantMessenger.InstantMessageApiIsUnavailable;
import com.ccp.especifications.instant.messenger.CcpInstantMessenger.ThisBotWasBlockedByThisUser;
import com.ccp.especifications.instant.messenger.CcpInstantMessenger.TooManyRequests;
import com.ccp.process.CcpProcess;
import com.ccp.utils.Utils;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class SendInstantMessage implements CcpProcess{

	@CcpDependencyInject
	private CcpInstantMessenger instantMessenger;
	
	private RemoveTries removeTries = CcpDependencyInjection.getInjected(RemoveTries.class);

	CcpMapDecorator idToSearch = new CcpMapDecorator().put("name", JnTopic.sendInstantMessage.name());

	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {
		
		CcpMapDecorator instantMessageParameters = JnEntity.messages.getOneById(this.idToSearch);

		CcpMapDecorator putAll = values.putAll(instantMessageParameters);

		boolean esteUsuarioBloqueouEsteBot = JnEntity.instant_messenger_bot_locked.exists(putAll);
		
		if(esteUsuarioBloqueouEsteBot) {
			return values;
		}
		
		boolean estaMensagemJaFoiEnviadaNesteIntervalo = JnEntity.instant_messenger_message_sent.exists(putAll);
		
		if(estaMensagemJaFoiEnviadaNesteIntervalo) {
			Utils.sleep(3000);
			CcpMapDecorator enviarMensagem = this.execute(putAll);
			return enviarMensagem;
		}
		
		try {
			return this.tryToSendIntantMessage(putAll);
		} catch (TooManyRequests e) {
			Utils.sleep(3000);
			return this.execute(putAll);
		} catch(ThisBotWasBlockedByThisUser e) {
			return saveBlockedBot(putAll);
		}catch(InstantMessageApiIsUnavailable e) {
			return this.retryToSendIntantMessage(putAll);
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
