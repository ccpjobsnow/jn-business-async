package com.ccp.jn.async.business;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.commons.utils.JnHttpRequestType;

public class JnAsyncBusinessTryToSendInstantMessage implements Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private JnAsyncBusinessSendHttpRequest sendHttpRequest = new JnAsyncBusinessSendHttpRequest();

	private JnAsyncBusinessSendInstantMessage sendInstantMessage = new JnAsyncBusinessSendInstantMessage();
	
	@Override
	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {
		CcpJsonRepresentation instantMessengerData = this.sendHttpRequest.execute(values, x -> this.sendInstantMessage.apply(x), JnHttpRequestType.instantMessenger, "subjectType");
		return instantMessengerData;
	}

}
