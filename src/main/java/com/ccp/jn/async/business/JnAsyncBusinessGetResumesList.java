package com.ccp.jn.async.business;

import java.util.List;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.query.CcpDbQueryMust;
import com.ccp.especifications.db.query.CcpDbQueryOptions;
import com.ccp.especifications.db.query.CcpQueryExecutorDecorator;
import com.jn.commons.entities.JnEntityCandidate;

public class JnAsyncBusinessGetResumesList implements  java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	@Override
	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {
		
		CcpDbQueryMust must = values.getAsObject("_must");
		
		CcpDbQueryOptions query = must.endMustAndBackToBool().endBoolAndBackToQuery().endQueryAndBackToRequest();

		CcpQueryExecutorDecorator selectFrom = query.selectFrom(new JnEntityCandidate().name());
		
		List<CcpJsonRepresentation> resultAsList = selectFrom.getResultAsList();
		
		CcpJsonRepresentation put = CcpConstants.EMPTY_JSON.put("results", resultAsList);

		return put;
	}

}
