package com.ccp.jn.async;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.jn.async.business.NotifyContactUs;
import com.ccp.jn.async.business.NotifyError;
import com.ccp.jn.async.business.RemoveTries;
import com.ccp.jn.async.business.RequestTokenAgain;
import com.ccp.jn.async.business.RequestUnlockToken;
import com.ccp.jn.async.business.SaveCandidateData;
import com.ccp.jn.async.business.SaveResumesQuery;
import com.ccp.jn.async.business.SendEmail;
import com.ccp.jn.async.business.SendInstantMessage;
import com.ccp.jn.async.business.SendUserToken;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnTopic;

public class AsyncServices {
	
	private static CcpMapDecorator catalog = new CcpMapDecorator()
			.put(JnTopic.requestUnlockToken.name(), CcpDependencyInjection.getInjected(RequestUnlockToken.class))
			.put(JnTopic.sendInstantMessage.name(), CcpDependencyInjection.getInjected(SendInstantMessage.class))
			.put(JnTopic.saveCandidateData.name(), CcpDependencyInjection.getInjected(SaveCandidateData.class))
			.put(JnTopic.requestTokenAgain.name(), CcpDependencyInjection.getInjected(RequestTokenAgain.class))
			.put(JnTopic.saveResumesQuery.name(), CcpDependencyInjection.getInjected(SaveResumesQuery.class))
			.put(JnTopic.notifyContactUs.name(), CcpDependencyInjection.getInjected(NotifyContactUs.class))
			.put(JnTopic.sendUserToken.name(), CcpDependencyInjection.getInjected(SendUserToken.class))
			.put(JnTopic.notifyError.name(), CcpDependencyInjection.getInjected(NotifyError.class))
			.put(JnTopic.removeTries.name(), CcpDependencyInjection.getInjected(RemoveTries.class))
			.put(JnTopic.sendEmail.name(), CcpDependencyInjection.getInjected(SendEmail.class))
			;
	
	private static CcpProcess getProcess(String processName) {
		CcpProcess asObject = catalog.getAsObject(processName);
		if(asObject == null) {
			throw new RuntimeException("The process '" + processName + "' was not found");
		}
		return asObject;
	}
	
	public static CcpMapDecorator executeProcess(String processName, CcpMapDecorator values) {
		CcpProcess process = getProcess(processName);
		CcpMapDecorator result = process.execute(values);
		return result;
	}
}
