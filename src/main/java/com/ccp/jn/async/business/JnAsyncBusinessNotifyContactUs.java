package com.ccp.jn.async.business;

import com.ccp.decorators.CcpJsonRepresentation;
import com.jn.commons.entities.JnEntityContactUs;
import com.jn.commons.utils.JnTopic;

public class JnAsyncBusinessNotifyContactUs implements  java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private final JnAsyncBusinessNotifySupport notifySupport = new JnAsyncBusinessNotifySupport();

	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {

		this.notifySupport.apply(values, JnTopic.jnNotifyContactUs, new JnEntityContactUs());
		
		return values;
	}
	

}
