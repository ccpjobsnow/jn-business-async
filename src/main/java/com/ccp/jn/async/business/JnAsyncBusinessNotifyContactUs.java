package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.jn.commons.entities.JnEntityContactUs;
import com.jn.commons.utils.JnTopic;

public class JnAsyncBusinessNotifyContactUs implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator>{

	private final JnAsyncBusinessNotifySupport notifySupport = new JnAsyncBusinessNotifySupport();

	public CcpMapDecorator apply(CcpMapDecorator values) {

		this.notifySupport.apply(values, JnTopic.notifyContactUs, new JnEntityContactUs());
		
		return values;
	}
	

}
