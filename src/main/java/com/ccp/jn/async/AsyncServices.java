package com.ccp.jn.async;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.jn.async.business.NotifyContactUs;
import com.ccp.jn.async.business.NotifyError;
import com.ccp.jn.async.business.RequestTokenAgain;
import com.ccp.jn.async.business.RequestUnlockToken;
import com.ccp.jn.async.business.SaveCandidateData;
import com.ccp.jn.async.business.SaveResumesQuery;
import com.ccp.jn.async.business.SendEmail;
import com.ccp.jn.async.business.SendInstantMessage;
import com.ccp.jn.async.business.SendUserToken;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessTopic;

public class AsyncServices {
	
	private static CcpMapDecorator catalog = new CcpMapDecorator()
			.put(JnBusinessTopic.requestUnlockToken.name(), CcpDependencyInjection.getInjected(RequestUnlockToken.class))
			.put(JnBusinessTopic.sendInstantMessage.name(), CcpDependencyInjection.getInjected(SendInstantMessage.class))
			.put(JnBusinessTopic.saveCandidateData.name(), CcpDependencyInjection.getInjected(SaveCandidateData.class))
			.put(JnBusinessTopic.requestTokenAgain.name(), CcpDependencyInjection.getInjected(RequestTokenAgain.class))
			.put(JnBusinessTopic.saveResumesQuery.name(), CcpDependencyInjection.getInjected(SaveResumesQuery.class))
			.put(JnBusinessTopic.notifyContactUs.name(), CcpDependencyInjection.getInjected(NotifyContactUs.class))
			.put(JnBusinessTopic.sendUserToken.name(), CcpDependencyInjection.getInjected(SendUserToken.class))
			.put(JnBusinessTopic.notifyError.name(), CcpDependencyInjection.getInjected(NotifyError.class))
			.put(JnBusinessTopic.sendEmail.name(), CcpDependencyInjection.getInjected(SendEmail.class))
			;
	
	public static CcpProcess getProcess(String processName) {
		CcpProcess asObject = catalog.getAsObject(processName);
		if(asObject == null) {
			throw new RuntimeException("The process '" + processName + "' was not found");
		}
		return asObject;
	}
}
