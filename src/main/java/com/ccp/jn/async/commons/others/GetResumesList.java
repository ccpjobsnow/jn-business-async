package com.ccp.jn.async.commons.others;

import java.util.List;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.query.CcpQueryExecutorDecorator;
import com.ccp.especifications.db.query.ElasticQuery;
import com.ccp.especifications.db.query.Must;
import com.jn.commons.JnEntity;

public class GetResumesList implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator> {

	@Override
	public CcpMapDecorator apply(CcpMapDecorator values) {
		
		Must must = values.getAsObject("_must");
		
		ElasticQuery query = must.endMustAndBackToBool().endBoolAndBackToQuery().endQueryAndBackToRequest();

		CcpQueryExecutorDecorator selectFrom = query.selectFrom(JnEntity.candidate.name());
		
		List<CcpMapDecorator> resultAsList = selectFrom.getResultAsList();
		
		CcpMapDecorator put = new CcpMapDecorator().put("results", resultAsList);

		return put;
	}

}
