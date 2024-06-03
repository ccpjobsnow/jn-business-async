package com.ccp.jn.async.business.support;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.commons.JnAsyncNotifySupport;
import com.ccp.jn.async.commons.JnAsyncUtilsGetMessage;
import com.ccp.jn.async.commons.JnAsyncUtilsLenientGetMessage;
import com.jn.commons.entities.JnEntityJobsnowError;
import com.jn.commons.utils.JnAsyncBusiness;

public class JnAsyncBusinessNotifyError implements Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	public static final JnAsyncBusinessNotifyError INSTANCE = new JnAsyncBusinessNotifyError();
	
	private JnAsyncBusinessNotifyError() {
		
	}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		
		String name = JnAsyncBusiness.notifyError.name();
		JnAsyncUtilsGetMessage x = new JnAsyncUtilsLenientGetMessage();
		JnAsyncNotifySupport.INSTANCE.apply(json, name, JnEntityJobsnowError.INSTANCE, x);

		return json;
	}
	
	public CcpJsonRepresentation apply(Throwable e) {
		
		CcpJsonRepresentation json = new CcpJsonRepresentation(e);
		CcpJsonRepresentation renameKey = json.renameField("message", "msg");
		CcpJsonRepresentation execute = this.apply(renameKey);
		return execute;
	}

}
