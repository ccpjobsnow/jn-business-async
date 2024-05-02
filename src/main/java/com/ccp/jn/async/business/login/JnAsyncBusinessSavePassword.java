package com.ccp.jn.async.business.login;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;

public class JnAsyncBusinessSavePassword implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {


	public static final JnAsyncBusinessSavePassword INSTANCE = new JnAsyncBusinessSavePassword();
	
	private JnAsyncBusinessSavePassword() {
		
	}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation t) {
		return CcpConstants.EMPTY_JSON;
	}

}
