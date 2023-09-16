package com.ccp.jn.async.commons.query;

import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.query.CcpDbQueryMust;
import com.ccp.especifications.db.utils.CcpEntityField;

public class JnAddFilter implements  Function<CcpMapDecorator, CcpMapDecorator> {
	
	private final CcpEntityField filter;
	
	public JnAddFilter(CcpEntityField filter) {
		this.filter = filter;
	}



	@Override
	public CcpMapDecorator apply(CcpMapDecorator values) {
		
		CcpDbQueryMust must = values.getAsObject("_must");
		Object value = values.get(this.filter.name());
		must = must.term(this.filter, value);
		CcpMapDecorator put = values.put("_must", must);

		return put;
	}

}
