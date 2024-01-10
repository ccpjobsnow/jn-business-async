package com.ccp.jn.async;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.business.JnAsyncBusinessNotifyContactUs;
import com.ccp.jn.async.business.JnAsyncBusinessNotifyError;
import com.ccp.jn.async.business.JnAsyncBusinessRemoveTries;
import com.ccp.jn.async.business.JnAsyncBusinessRequestTokenAgain;
import com.ccp.jn.async.business.JnAsyncBusinessRequestUnlockToken;
import com.ccp.jn.async.business.JnAsyncBusinessSendEmail;
import com.ccp.jn.async.business.JnAsyncBusinessSendUserToken;
import com.ccp.jn.async.business.JnAsyncBusinessTryToSendInstantMessage;
import com.jn.commons.entities.JnEntityAsyncTask;
import com.jn.commons.utils.JnTopic;

public class JnAsyncBusiness {
	
	private static CcpJsonRepresentation catalog = CcpConstants.EMPTY_JSON
			.put(JnTopic.sendInstantMessage.name(),  new JnAsyncBusinessTryToSendInstantMessage())
			.put(JnTopic.requestUnlockToken.name(), new JnAsyncBusinessRequestUnlockToken())
			.put(JnTopic.requestTokenAgain.name(), new JnAsyncBusinessRequestTokenAgain())
			.put(JnTopic.notifyContactUs.name(), new JnAsyncBusinessNotifyContactUs())
			.put(JnTopic.sendUserToken.name(), new JnAsyncBusinessSendUserToken())
			.put(JnTopic.removeTries.name(), new JnAsyncBusinessRemoveTries())
			.put(JnTopic.notifyError.name(), new JnAsyncBusinessNotifyError())
			.put(JnTopic.sendEmail.name(), new JnAsyncBusinessSendEmail())
			;
	
	private static Function<CcpJsonRepresentation, CcpJsonRepresentation> getProcess(String processName) {
		Function<CcpJsonRepresentation, CcpJsonRepresentation> asObject = catalog.getAsObject(processName);
		if(asObject == null) {
			throw new RuntimeException("The process '" + processName + "' was not found");
		}
		return asObject;
	}
	
	public static CcpJsonRepresentation executeProcess(String processName, CcpJsonRepresentation values) {
		
		String asyncTaskId = values.getAsString("asyncTaskId");
		
		CcpJsonRepresentation asyncTaskDetails = new JnEntityAsyncTask().getOneById(asyncTaskId);	
		
		try {
			CcpJsonRepresentation response = execute(processName, values);
			saveProcessResult(asyncTaskDetails, response,asyncTaskId, true);
			return response;
		} catch (Throwable e) {
			CcpJsonRepresentation response = new CcpJsonRepresentation(e);
			saveProcessResult(asyncTaskDetails, response, asyncTaskId, false);
			throw e;
		}
	}

	public static CcpJsonRepresentation execute(String processName, CcpJsonRepresentation values) {
		Function<CcpJsonRepresentation, CcpJsonRepresentation> process = getProcess(processName);
		CcpJsonRepresentation response = process.apply(values);
		return response;
	}

	private static void saveProcessResult(CcpJsonRepresentation messageDetails, CcpJsonRepresentation response,String asyncTaskId, boolean success) {
		Long finished = System.currentTimeMillis();
		CcpJsonRepresentation processResult = messageDetails.put("response", response).put("finished", finished).put("success", success);
		new JnEntityAsyncTask().createOrUpdate(processResult, asyncTaskId);
	}
}
