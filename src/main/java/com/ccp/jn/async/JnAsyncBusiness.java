package com.ccp.jn.async;

import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.jn.async.business.JnAsyncBusinessNotifyContactUs;
import com.ccp.jn.async.business.JnAsyncBusinessNotifyError;
import com.ccp.jn.async.business.JnAsyncBusinessRemoveTries;
import com.ccp.jn.async.business.JnAsyncBusinessRequestTokenAgain;
import com.ccp.jn.async.business.JnAsyncBusinessRequestUnlockToken;
import com.ccp.jn.async.business.JnAsyncBusinessSaveCandidateData;
import com.ccp.jn.async.business.JnAsyncBusinessSaveResumesQuery;
import com.ccp.jn.async.business.JnAsyncBusinessSendEmail;
import com.ccp.jn.async.business.JnAsyncBusinessSendUserToken;
import com.ccp.jn.async.business.JnAsyncBusinessTryToSendInstantMessage;
import com.jn.commons.entities.JnEntity;
import com.jn.commons.utils.JnTopic;

public class JnAsyncBusiness {
	
	private static CcpMapDecorator catalog = new CcpMapDecorator()
			.put(JnTopic.sendInstantMessage.name(),  new JnAsyncBusinessTryToSendInstantMessage())
			.put(JnTopic.requestUnlockToken.name(), new JnAsyncBusinessRequestUnlockToken())
			.put(JnTopic.saveCandidateData.name(), new JnAsyncBusinessSaveCandidateData())
			.put(JnTopic.requestTokenAgain.name(), new JnAsyncBusinessRequestTokenAgain())
			.put(JnTopic.saveResumesQuery.name(), new JnAsyncBusinessSaveResumesQuery())
			.put(JnTopic.notifyContactUs.name(), new JnAsyncBusinessNotifyContactUs())
			.put(JnTopic.sendUserToken.name(), new JnAsyncBusinessSendUserToken())
			.put(JnTopic.removeTries.name(), new JnAsyncBusinessRemoveTries())
			.put(JnTopic.notifyError.name(), new JnAsyncBusinessNotifyError())
			.put(JnTopic.sendEmail.name(), new JnAsyncBusinessSendEmail())
			;
	
	private static Function<CcpMapDecorator, CcpMapDecorator> getProcess(String processName) {
		Function<CcpMapDecorator, CcpMapDecorator> asObject = catalog.getAsObject(processName);
		if(asObject == null) {
			throw new RuntimeException("The process '" + processName + "' was not found");
		}
		return asObject;
	}
	
	public static CcpMapDecorator executeProcess(String processName, CcpMapDecorator values) {
		
		String asyncTaskId = values.getAsString("asyncTaskId");
		
		CcpMapDecorator asyncTaskDetails = JnEntity.async_task.getOneById(asyncTaskId);	
		
		try {
			CcpMapDecorator response = execute(processName, values);
			saveProcessResult(asyncTaskDetails, response,asyncTaskId, true);
			return response;
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
