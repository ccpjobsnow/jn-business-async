package com.ccp.jn.async.commons.others;

import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.jn.async.business.JnHttpRequestType;
import com.ccp.jn.async.business.SendHttpRequest;
import com.ccp.jn.async.business.SendInstantMessage;

public class TryToSendInstantMessage implements Function<CcpMapDecorator, CcpMapDecorator>{

	private SendHttpRequest sendHttpRequest = new SendHttpRequest();

	private SendInstantMessage sendInstantMessage = new SendInstantMessage();
	
	@Override
	public CcpMapDecorator apply(CcpMapDecorator values) {
		CcpMapDecorator instantMessengerData = this.sendHttpRequest.execute(values, x -> this.sendInstantMessage.apply(x), JnHttpRequestType.instantMessenger, "subjectType");
		return instantMessengerData;
	}

}
