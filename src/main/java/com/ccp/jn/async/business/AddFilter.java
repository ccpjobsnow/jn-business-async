package com.ccp.jn.async.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.query.Must;
import com.ccp.especifications.db.utils.CcpField;
import com.ccp.process.CcpProcess;

public class AddFilter implements CcpProcess {
	
	private final CcpField filter;
	
	public AddFilter(CcpField filter) {
		this.filter = filter;
	}



	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {
		
		Must must = values.getAsObject("_must");
		Object value = values.get(this.filter.name());
		must = must.term(this.filter, value);
		CcpMapDecorator put = values.put("_must", must);

		return put;
	}

}
