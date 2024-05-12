package com.ccp.jn.async.business.support;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.commons.JnAsyncNotifySupport;
import com.ccp.jn.async.commons.JnAsyncUtilsGetMessage;
import com.jn.commons.entities.JnEntityContactUs;
import com.jn.commons.utils.JnAsyncBusiness;

public class JnAsyncBusinessNotifyContactUs implements  java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	public static final JnAsyncBusinessNotifyContactUs INSTANCE = new JnAsyncBusinessNotifyContactUs();
	
	private JnAsyncBusinessNotifyContactUs() {
		
	}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {

		JnAsyncUtilsGetMessage x = new JnAsyncUtilsGetMessage();
		JnAsyncNotifySupport.INSTANCE.apply(values, JnAsyncBusiness.notifyContactUs.name(), JnEntityContactUs.INSTANCE, x);
		
		return values;
	}
	

}
