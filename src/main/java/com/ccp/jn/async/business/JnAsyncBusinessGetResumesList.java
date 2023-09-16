package com.ccp.jn.async.business;

import java.util.List;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.query.CcpQueryExecutorDecorator;
import com.jn.commons.entities.JnEntity;
import com.ccp.especifications.db.query.CcpDbQueryOptions;
import com.ccp.especifications.db.query.CcpDbQueryMust;

public class JnAsyncBusinessGetResumesList implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator> {

	@Override
	public CcpMapDecorator apply(CcpMapDecorator values) {
		
		CcpDbQueryMust must = values.getAsObject("_must");
		
		CcpDbQueryOptions query = must.endMustAndBackToBool().endBoolAndBackToQuery().endQueryAndBackToRequest();

		CcpQueryExecutorDecorator selectFrom = query.selectFrom(JnEntity.candidate.name());
		
		List<CcpMapDecorator> resultAsList = selectFrom.getResultAsList();
		
		CcpMapDecorator put = new CcpMapDecorator().put("results", resultAsList);

		return put;
	}

}
