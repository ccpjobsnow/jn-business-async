
package com.ccp.jn.async.business;

import java.util.Arrays;
import java.util.List;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.jn.async.commons.others.MessagesTranslatorAndSender;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnConstants;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class SendUserToken implements CcpProcess{
	
	private final MessagesTranslatorAndSender messagesTranslatorAndSender = CcpDependencyInjection.getInjected(MessagesTranslatorAndSender.class);
	
	private final SendEmail sendEmail = CcpDependencyInjection.getInjected(SendEmail.class);

	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {
		
		CcpMapDecorator externalParameters = values.getTransformed(JnConstants.PUT_EMAIL_TOKEN).put("subjectType", JnTopic.sendUserToken.name());

		List<CcpProcess> processes = Arrays.asList(this.sendEmail);
		
		this.messagesTranslatorAndSender.execute(externalParameters, JnTopic.sendUserToken, JnEntity.login_token, processes, "emailMessage");
		
		return values;
	}

}
