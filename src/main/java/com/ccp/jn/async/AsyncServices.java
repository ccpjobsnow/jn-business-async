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
import com.jn.commons.JnEntity;
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
	
	public static void executeProcess(String processName, CcpMapDecorator values) {
		
		String asyncTaskId = values.getAsString("asyncTaskId");
		CcpMapDecorator asyncTaskDetails = JnEntity.async_task.getOneById(asyncTaskId);	
		
		try {
			CcpMapDecorator response = execute(processName, values);
			saveProcessResult(asyncTaskDetails, response, true);
		} catch (Throwable e) {
			CcpMapDecorator response = new CcpMapDecorator(e);
			saveProcessResult(asyncTaskDetails, response, false);
			throw e;
		}
	}

	public static CcpMapDecorator execute(String processName, CcpMapDecorator values) {
		CcpProcess process = getProcess(processName);
		CcpMapDecorator response = process.execute(values);
		return response;
	}

	private static void saveProcessResult(CcpMapDecorator messageDetails, CcpMapDecorator response, boolean success) {
		Long finished = System.currentTimeMillis();
		CcpMapDecorator processResult = messageDetails.put("response", response).put("finished", finished).put("success", success);
		JnEntity.async_task.createOrUpdate(processResult);
	}
}
