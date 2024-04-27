package com.ccp.jn.async.business.commons;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.commons.JnAsyncBusinessSendHttpRequest;
import com.ccp.jn.async.commons.JnAsyncBusinessSendInstantMessage;
import com.ccp.jn.async.commons.JnHttpRequestType;

public class JnAsyncBusinessTryToSendInstantMessage implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {
	
	public static final JnAsyncBusinessTryToSendInstantMessage INSTANCE = new JnAsyncBusinessTryToSendInstantMessage();
	
	private JnAsyncBusinessTryToSendInstantMessage() {
		
	}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {
		CcpJsonRepresentation instantMessengerData = JnAsyncBusinessSendHttpRequest.INSTANCE.execute(values, x -> JnAsyncBusinessSendInstantMessage.INSTANCE.apply(x), JnHttpRequestType.instantMessenger, "subjectType");
		return instantMessengerData;
	}

}
