package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;

public class BigQueryNoxxon implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator>{

	@Override
	public CcpMapDecorator apply(CcpMapDecorator t) {
		System.out.println(t.asJson());
		return t;
	}

}
