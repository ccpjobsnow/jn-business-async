package com.ccp.jn.async.business.login;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.commons.JnAsyncBusinessNotifySupport;
import com.jn.commons.entities.JnEntityRequestUnlockToken;
import com.jn.commons.utils.JnAsyncBusiness;

public class JnAsyncBusinessRequestUnlockToken implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	public static final JnAsyncBusinessRequestUnlockToken INSTANCE = new JnAsyncBusinessRequestUnlockToken();
	
	private JnAsyncBusinessRequestUnlockToken() {
		
	}

	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {
		
		JnAsyncBusinessNotifySupport.INSTANCE.apply(values, JnAsyncBusiness.requestUnlockToken.name(), JnEntityRequestUnlockToken.INSTANCE);

		return values;
	}

}
