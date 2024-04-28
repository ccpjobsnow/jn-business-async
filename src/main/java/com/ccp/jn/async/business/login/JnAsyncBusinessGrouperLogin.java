package com.ccp.jn.async.business.login;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;

public class JnAsyncBusinessGrouperLogin implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {


	public static final JnAsyncBusinessGrouperLogin INSTANCE = new JnAsyncBusinessGrouperLogin();
	
	private JnAsyncBusinessGrouperLogin() {
		
	}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation t) {
		return CcpConstants.EMPTY_JSON;
	}

}
