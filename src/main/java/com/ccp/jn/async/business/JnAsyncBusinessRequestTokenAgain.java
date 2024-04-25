package com.ccp.jn.async.business;

import com.ccp.decorators.CcpJsonRepresentation;
import com.jn.commons.entities.JnEntityRequestTokenAgain;
import com.jn.commons.utils.JnTopics;

public class JnAsyncBusinessRequestTokenAgain implements  java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private final JnAsyncBusinessNotifySupport notifySupport = new JnAsyncBusinessNotifySupport();

	@Override
	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {
		
		//JnBusinessEntity.login_token.resetData(parameters); o que essa coisa faz aqui?
		this.notifySupport.apply(values, JnTopics.requestTokenAgain.name(), JnEntityRequestTokenAgain.INSTANCE);

		return values;
	}

}
