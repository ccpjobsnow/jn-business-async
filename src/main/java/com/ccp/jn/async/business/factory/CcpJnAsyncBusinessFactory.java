package com.ccp.jn.async.business.factory;

import com.ccp.dependency.injection.CcpInstanceProvider;

public class CcpJnAsyncBusinessFactory implements CcpInstanceProvider {

	public Object getInstance() {
		return new JnAsyncBusinessFactory();
	}

}
