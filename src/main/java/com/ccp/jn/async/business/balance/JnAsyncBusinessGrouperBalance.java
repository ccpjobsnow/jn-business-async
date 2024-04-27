package com.ccp.jn.async.business.balance;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public class JnAsyncBusinessGrouperBalance implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public static final JnAsyncBusinessGrouperBalance INSTANCE = new JnAsyncBusinessGrouperBalance();
	
	private JnAsyncBusinessGrouperBalance() {
		
	}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation t) {
		return null;
	}

}
