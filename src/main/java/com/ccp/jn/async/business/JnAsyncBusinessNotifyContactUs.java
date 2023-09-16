package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.jn.commons.entities.JnEntity;
import com.jn.commons.utils.JnTopic;

public class JnAsyncBusinessNotifyContactUs implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator>{

	private final JnAsyncBusinessNotifySupport notifySupport = new JnAsyncBusinessNotifySupport();

	public CcpMapDecorator apply(CcpMapDecorator values) {

		this.notifySupport.apply(values, JnTopic.notifyContactUs, JnEntity.contact_us);
		
		return values;
	}
	

}
