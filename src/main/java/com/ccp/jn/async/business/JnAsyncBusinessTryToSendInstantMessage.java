package com.ccp.jn.async.business;

import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.jn.async.commons.utils.JnHttpRequestType;

public class JnAsyncBusinessTryToSendInstantMessage implements Function<CcpMapDecorator, CcpMapDecorator>{

	private JnAsyncBusinessSendHttpRequest sendHttpRequest = new JnAsyncBusinessSendHttpRequest();

	private JnAsyncBusinessSendInstantMessage sendInstantMessage = new JnAsyncBusinessSendInstantMessage();
	
	@Override
	public CcpMapDecorator apply(CcpMapDecorator values) {
		CcpMapDecorator instantMessengerData = this.sendHttpRequest.execute(values, x -> this.sendInstantMessage.apply(x), JnHttpRequestType.instantMessenger, "subjectType");
		return instantMessengerData;
	}

}
