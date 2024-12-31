package com.ccp.jn.async.business.commons;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.commons.JnAsyncSendHttpRequest;
import com.ccp.jn.async.commons.JnAsyncSendInstantMessage;
import com.jn.commons.entities.JnEntityInstantMessengerParametersToSend;
import com.ccp.jn.async.commons.JnAsyncHttpRequestType;

public class JnAsyncBusinessTryToSendInstantMessage implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {
	
	public static final JnAsyncBusinessTryToSendInstantMessage INSTANCE = new JnAsyncBusinessTryToSendInstantMessage();
	
	private JnAsyncBusinessTryToSendInstantMessage() {}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		CcpJsonRepresentation instantMessengerData = JnAsyncSendHttpRequest.INSTANCE.execute(json, x -> JnAsyncSendInstantMessage.INSTANCE.apply(x), JnAsyncHttpRequestType.instantMessenger, JnEntityInstantMessengerParametersToSend.Fields.subjectType.name());
		return instantMessengerData;
	}

}
