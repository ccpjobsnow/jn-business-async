package com.ccp.jn.async.commons.query;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.query.ElasticQuery;
import com.ccp.especifications.db.query.Must;
import com.ccp.process.CcpProcess;

public class AddSizeInTheQuery implements CcpProcess {

	public CcpMapDecorator execute(CcpMapDecorator values) {
		Integer size = values.getAsIntegerNumber("size");
		if(size == null) {
			size = 0;
		}
		Must must = new ElasticQuery().setSize(size).startQuery().startBool().startMust();
		return values.put("_must", must);
	}

}
