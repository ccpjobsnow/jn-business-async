package com.ccp.jn.async.commons.query;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.query.ElasticQuery;
import com.ccp.especifications.db.query.Must;


public class AddSizeInTheQuery implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator> {

	public CcpMapDecorator apply(CcpMapDecorator values) {
		Integer size = values.getAsIntegerNumber("size");
		if(size == null) {
			size = 0;
		}
		Must must = new ElasticQuery().setSize(size).startQuery().startBool().startMust();
		return values.put("_must", must);
	}

}
