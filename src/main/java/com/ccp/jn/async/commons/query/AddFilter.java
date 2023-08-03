package com.ccp.jn.async.commons.query;

import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.query.Must;
import com.ccp.especifications.db.utils.CcpField;

public class AddFilter implements  Function<CcpMapDecorator, CcpMapDecorator> {
	
	private final CcpField filter;
	
	public AddFilter(CcpField filter) {
		this.filter = filter;
	}



	@Override
	public CcpMapDecorator apply(CcpMapDecorator values) {
		
		Must must = values.getAsObject("_must");
		Object value = values.get(this.filter.name());
		must = must.term(this.filter, value);
		CcpMapDecorator put = values.put("_must", must);

		return put;
	}

}
