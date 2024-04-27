package com.ccp.jn.async.business.login;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.commons.JnAsyncBusinessNotifySupport;
import com.jn.commons.entities.JnEntityRequestTokenAgain;
import com.jn.commons.utils.JnAsyncBusiness;

public class JnAsyncBusinessRequestTokenAgain implements Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private JnAsyncBusinessRequestTokenAgain() {
		
	}
	
	public static final JnAsyncBusinessRequestTokenAgain INSTANCE = new JnAsyncBusinessRequestTokenAgain();
	

	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {
		
		//JnBusinessEntity.login_token.resetData(parameters); o que essa coisa faz aqui?
		JnAsyncBusinessNotifySupport.INSTANCE.apply(values, JnAsyncBusiness.requestTokenAgain.name(), JnEntityRequestTokenAgain.INSTANCE);

		return values;
	}

}
