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

public class SendInstantMessage implements CcpProcess{

	@CcpDependencyInject
	private CcpInstantMessenger instantMessenger;

	
	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {
		
		boolean esteUsuarioBloqueouEsteBot = JnBusinessEntity.instant_messenger_bot_locked.exists(values);
		
		if(esteUsuarioBloqueouEsteBot) {
			return values;
		}
		
		boolean estaMensagemJaFoiEnviadaNesteIntervalo = JnBusinessEntity.instant_messenger_message_sent.exists(values);
		
		if(estaMensagemJaFoiEnviadaNesteIntervalo) {
			Utils.sleep(3000);
			CcpMapDecorator enviarMensagem = this.enviarMensagem(values);
			return enviarMensagem;
		}
		
		boolean aindaNaoSeTentouEnviarEstaMensagemParaEsteDestinatario = JnBusinessEntity.instant_messenger_try_to_send_message.exists(values) == false;
		
		if(aindaNaoSeTentouEnviarEstaMensagemParaEsteDestinatario) {
			CcpMapDecorator enviarMensagem = this.enviarMensagem(values);
			return enviarMensagem;
		}
		
		CcpMapDecorator tentativasDeEnviarEstaMensagem = JnBusinessEntity.instant_messenger_try_to_send_message.get(values, CcpConstants.DO_NOTHING);
		
		Long tries = tentativasDeEnviarEstaMensagem.getAsLongNumber("tries");
		
		if(tries == null) {
			return values;
		}
		
		if(tries >= 3) {
			JnBusinessEntity.instant_messenger_api_unavailable.save(values);
			return values;
		}
		
		JnBusinessEntity.instant_messenger_try_to_send_message.remove(values.put("tries", ++tries));
		
		CcpMapDecorator enviarMensagem = this.enviarMensagem(values);
		return enviarMensagem;
	}

	private CcpMapDecorator enviarMensagem(CcpMapDecorator values) {
		
		CcpMapDecorator dadosNecessariosParaEnviarMensagemPeloTelegram = values.getSubMap("botToken", "chatId", "message", "subjectType", "subject");
		
		Long replyTo = values.containsAllKeys("replyTo") ? values.getAsLongNumber("replyTo") : 0L;
		String botToken = dadosNecessariosParaEnviarMensagemPeloTelegram.getAsString("botToken");
		String message = dadosNecessariosParaEnviarMensagemPeloTelegram.getAsString("message");
		Long chatId = dadosNecessariosParaEnviarMensagemPeloTelegram.getAsLongNumber("chatId");
		
		try {
			this.instantMessenger.sendMessage(botToken, message, chatId, replyTo);
			JnBusinessEntity.instant_messenger_try_to_send_message.remove(values);
			long totalDeSegundosDecorridosDesdeMeiaNoiteDesteDia = new CcpTimeDecorator().getTotalDeSegundosDecorridosDesdeMeiaNoiteDesteDia();
			JnBusinessEntity.instant_messenger_message_sent.save(values.put("interval", totalDeSegundosDecorridosDesdeMeiaNoiteDesteDia / 3));
			return values;
		} catch (TooManyRequests e) {
			Utils.sleep(3000);
			return this.enviarMensagem(values);
		} catch(ThisBotWasBlockedByThisUser e) {
			JnBusinessEntity.instant_messenger_bot_locked.save(values);
			return values;
		}catch(InstantMessageApiIsUnavailable e) {
			JnBusinessEntity.instant_messenger_try_to_send_message.save(values);
			Utils.sleep(3000);
			return this.execute(values);
		}
	}

}
