package com.ccp.jn.async.commons.others;

import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;

public class LenientProcess implements Function<CcpMapDecorator, CcpMapDecorator>{

	private final Function<CcpMapDecorator, CcpMapDecorator> process;

	public LenientProcess(Function<CcpMapDecorator, CcpMapDecorator> process) {
		this.process = process;
	}

	@Override
	public CcpMapDecorator apply(CcpMapDecorator t) {
		try {
			CcpMapDecorator apply = this.process.apply(t);
			return apply;
		} catch (Exception e) {
			 return t;
		}
	}
	
	
	
}
