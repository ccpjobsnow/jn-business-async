package com.ccp.jn.async.business;

import java.util.List;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.query.CcpDbQueryExecutor;
import com.ccp.especifications.db.query.CcpQueryExecutorDecorator;
import com.ccp.especifications.db.query.ElasticQuery;
import com.ccp.especifications.db.query.Must;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;

public class GetResumesList implements CcpProcess {

	private final CcpDbQueryExecutor requestExecutor;

	
	
	public GetResumesList(CcpDbQueryExecutor requestExecutor) {
		this.requestExecutor = requestExecutor;
	}



	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {
		
		Must must = values.getAsObject("_must");
		
		ElasticQuery query = must.endMustAndBackToBool().endBoolAndBackToQuery().endQueryAndBackToRequest();

		CcpQueryExecutorDecorator selectFrom = query.selectFrom(this.requestExecutor, JnBusinessEntity.candidate.name());
		
		List<CcpMapDecorator> resultAsList = selectFrom.getResultAsList();
		
		CcpMapDecorator put = new CcpMapDecorator().put("results", resultAsList);

		return put;
	}

}
