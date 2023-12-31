package com.ccp.jn.async.commons.query;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.query.CcpDbQueryMust;
import com.ccp.especifications.db.utils.CcpEntityField;

public class JnAddFilter implements  Function<CcpJsonRepresentation, CcpJsonRepresentation> {
	
	private final CcpEntityField filter;
	
	public JnAddFilter(CcpEntityField filter) {
		this.filter = filter;
	}



	@Override
	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {
		
		CcpDbQueryMust must = values.getAsObject("_must");
		Object value = values.get(this.filter.name());
		must = must.term(this.filter, value);
		CcpJsonRepresentation put = values.put("_must", must);

		return put;
	}

}
