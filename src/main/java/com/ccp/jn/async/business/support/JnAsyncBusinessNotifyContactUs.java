package com.ccp.jn.async.business.support;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.commons.JnAsyncNotifySupport;
import com.ccp.jn.async.messages.JnAsyncUtilsGetMessage;
import com.jn.commons.entities.JnEntityContactUs;
import com.jn.commons.utils.JnAsyncBusiness;

public class JnAsyncBusinessNotifyContactUs implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	public static final JnAsyncBusinessNotifyContactUs INSTANCE = new JnAsyncBusinessNotifyContactUs();
	
	private JnAsyncBusinessNotifyContactUs() {
		
	}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {

		JnAsyncUtilsGetMessage x = new JnAsyncUtilsGetMessage();
		JnAsyncNotifySupport.INSTANCE.apply(json, JnAsyncBusiness.notifyContactUs.name(), JnEntityContactUs.INSTANCE, x);
		
		return json;
	}
	

}
