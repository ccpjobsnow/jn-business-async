package com.ccp.jn.async.business;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpMapDecorator;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.instant.messenger.CcpInstantMessenger;
import com.ccp.especifications.instant.messenger.CcpInstantMessenger.InstantMessageApiIsUnavailable;
import com.ccp.especifications.instant.messenger.CcpInstantMessenger.ThisBotWasBlockedByThisUser;
import com.ccp.especifications.instant.messenger.CcpInstantMessenger.TooManyRequests;
import com.ccp.process.CcpProcess;
import com.ccp.utils.Utils;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.JnBusinessTopic;

public class SendInstantMessage implements CcpProcess{

	@CcpDependencyInject
	private CcpInstantMessenger instantMessenger;

	CcpMapDecorator idToSearch = new CcpMapDecorator().put("name", JnBusinessTopic.sendInstantMessage.name());

	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {
		
		CcpMapDecorator instantMessageParameters = JnBusinessEntity._static.get(this.idToSearch, CcpConstants.DO_NOTHING);

		CcpMapDecorator putAll = values.putAll(instantMessageParameters);

		boolean esteUsuarioBloqueouEsteBot = JnBusinessEntity.instant_messenger_bot_locked.exists(putAll);
		
		if(esteUsuarioBloqueouEsteBot) {
			return values;
		}
		
		boolean estaMensagemJaFoiEnviadaNesteIntervalo = JnBusinessEntity.instant_messenger_message_sent.exists(putAll);
		
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
		JnBusinessEntity.instant_messenger_message_sent.save(putAll.put("interval", totalDeSegundosDecorridosDesdeMeiaNoiteDesteDia / 3));
		JnBusinessEntity.instant_messenger_try_to_send_message.removeTries(putAll, "tries", 3);
		return putAll;
	}

	private CcpMapDecorator saveBlockedBot(CcpMapDecorator putAll) {
		JnBusinessEntity.instant_messenger_bot_locked.save(putAll);
		return putAll;
	}

	private CcpMapDecorator retryToSendIntantMessage(CcpMapDecorator putAll) {
		boolean exceededTries = JnBusinessEntity.instant_messenger_try_to_send_message.exceededTries(putAll, "tries", 3);
		
		if(exceededTries) {
			JnBusinessEntity.instant_messenger_api_unavailable.save(putAll);
			return putAll;
		}
		
		Utils.sleep(5000);
		return this.execute(putAll);
	}
}
