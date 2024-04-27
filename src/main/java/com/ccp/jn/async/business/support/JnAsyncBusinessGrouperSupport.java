package com.ccp.jn.async.business.support;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public class JnAsyncBusinessGrouperSupport implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public static final JnAsyncBusinessGrouperSupport INSTANCE = new JnAsyncBusinessGrouperSupport();
	
	private JnAsyncBusinessGrouperSupport() {
		
	}
			
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation t) {
		return null;
	}

}
