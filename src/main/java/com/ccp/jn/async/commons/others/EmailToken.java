package com.ccp.jn.async.commons.others;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpMapDecorator;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.decorators.CcpTextDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.exceptions.email.EmailMessageNotSent;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class EmailToken {
	
	private MessagesTranslation messagesTranslation = CcpDependencyInjection.getInjected(MessagesTranslation.class);
	
	public void execute(CcpMapDecorator values, JnTopic templateId, JnEntity entityToSave, CcpProcess businessExecutor) {
		
		CcpTextDecorator textDecorator = new CcpStringDecorator(CcpConstants.CHARACTERS_TO_GENERATE_TOKEN).text();

		String token = textDecorator.generateToken(8);

		CcpMapDecorator externalParameters = values.put("token", token);
		
		String language = values.getAsString("language");
		
		CcpMapDecorator messageToSend = this.messagesTranslation.translate(templateId, language, "message", externalParameters);
		try {
			businessExecutor.execute(messageToSend);
			
			CcpMapDecorator tokenData = values.getSubMap("email").put("token", token);
			
			entityToSave.createOrUpdate(tokenData);
			
		} catch (EmailMessageNotSent e) {
		}
	}
}
