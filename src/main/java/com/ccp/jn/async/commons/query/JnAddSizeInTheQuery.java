package com.ccp.jn.async.commons.query;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.query.CcpDbQueryOptions;
import com.ccp.especifications.db.query.CcpDbQueryMust;


public class JnAddSizeInTheQuery implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator> {

	public CcpMapDecorator apply(CcpMapDecorator values) {
		Integer size = values.getAsIntegerNumber("size");
		if(size == null) {
			size = 0;
		}
		CcpDbQueryMust must = new CcpDbQueryOptions().setSize(size).startQuery().startBool().startMust();
		return values.put("_must", must);
	}

}
