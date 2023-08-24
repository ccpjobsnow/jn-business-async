package com.ccp.jn.async;

import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.jn.async.business.NotifyContactUs;
import com.ccp.jn.async.business.NotifyError;
import com.ccp.jn.async.business.RemoveTries;
import com.ccp.jn.async.business.RequestTokenAgain;
import com.ccp.jn.async.business.RequestUnlockToken;
import com.ccp.jn.async.business.SaveCandidateData;
import com.ccp.jn.async.business.SaveResumesQuery;
import com.ccp.jn.async.business.SendEmail;
import com.ccp.jn.async.business.SendUserToken;
import com.ccp.jn.async.commons.others.TryToSendInstantMessage;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class AsyncServices {
	
	private static CcpMapDecorator catalog = new CcpMapDecorator()
			.put(JnTopic.sendInstantMessage.name(),  new TryToSendInstantMessage())
			.put(JnTopic.requestUnlockToken.name(), new RequestUnlockToken())
			.put(JnTopic.saveCandidateData.name(), new SaveCandidateData())
			.put(JnTopic.requestTokenAgain.name(), new RequestTokenAgain())
			.put(JnTopic.saveResumesQuery.name(), new SaveResumesQuery())
			.put(JnTopic.notifyContactUs.name(), new NotifyContactUs())
			.put(JnTopic.sendUserToken.name(), new SendUserToken())
			.put(JnTopic.removeTries.name(), new RemoveTries())
			.put(JnTopic.notifyError.name(), new NotifyError())
			.put(JnTopic.sendEmail.name(), new SendEmail())
			;
	
	private static Function<CcpMapDecorator, CcpMapDecorator> getProcess(String processName) {
		Function<CcpMapDecorator, CcpMapDecorator> asObject = catalog.getAsObject(processName);
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
			saveProcessResult(asyncTaskDetails, response,asyncTaskId, true);
		} catch (Throwable e) {
			CcpMapDecorator response = new CcpMapDecorator(e);
			saveProcessResult(asyncTaskDetails, response, asyncTaskId, false);
			throw e;
		}
	}

	public static CcpMapDecorator execute(String processName, CcpMapDecorator values) {
		Function<CcpMapDecorator, CcpMapDecorator> process = getProcess(processName);
		CcpMapDecorator response = process.apply(values);
		return response;
	}

	private static void saveProcessResult(CcpMapDecorator messageDetails, CcpMapDecorator response,String asyncTaskId, boolean success) {
		Long finished = System.currentTimeMillis();
		CcpMapDecorator processResult = messageDetails.put("response", response).put("finished", finished).put("success", success);
		JnEntity.async_task.createOrUpdate(processResult, asyncTaskId);
	}
}
