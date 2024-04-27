package com.ccp.jn.async.business.support;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.commons.JnAsyncBusinessNotifySupport;
import com.jn.commons.entities.JnEntityJobsnowError;
import com.jn.commons.utils.JnAsyncBusiness;

public class JnAsyncBusinessNotifyError implements Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	public static final JnAsyncBusinessNotifyError INSTANCE = new JnAsyncBusinessNotifyError();
	
	private JnAsyncBusinessNotifyError() {
		
	}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {
		
		
		String name = JnAsyncBusiness.notifyError.name();
		JnAsyncBusinessNotifySupport.INSTANCE.apply(values, name, JnEntityJobsnowError.INSTANCE);

		return values;
	}
	
	public CcpJsonRepresentation apply(Throwable e) {
		
		CcpJsonRepresentation values = new CcpJsonRepresentation(e);
		CcpJsonRepresentation execute = this.apply(values.renameKey("message", "msg"));
		return execute;
	}

}
