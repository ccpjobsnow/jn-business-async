package com.ccp.jn.async.business.factory;

import com.ccp.dependency.injection.CcpInstanceProvider;
import com.ccp.especifications.async.business.factory.CcpAsyncBusinessFactory;

public class CcpJnAsyncBusinessFactory implements CcpInstanceProvider<CcpAsyncBusinessFactory> {

	public CcpAsyncBusinessFactory getInstance() {
		return JnAsyncBusinessFactory.INSTANCE;
	}
}
