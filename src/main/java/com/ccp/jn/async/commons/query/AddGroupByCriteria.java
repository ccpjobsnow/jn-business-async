package com.ccp.jn.async.commons.query;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.query.ElasticQuery;
import com.ccp.especifications.db.query.Must;
import com.ccp.process.CcpProcess;
import com.jn.commons.tables.fields.A3D_candidate;

public class AddGroupByCriteria implements CcpProcess {

	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {

		Must must = values.getAsObject("_must");
		
		ElasticQuery query = must.endMustAndBackToBool().endBoolAndBackToQuery().endQueryAndBackToRequest();

		query = query.startAggregations()
					.addAvgAggregation(A3D_candidate.experience.name(), A3D_candidate.experience)
					.addAvgAggregation(A3D_candidate.clt.name(), A3D_candidate.clt)
					.addAvgAggregation(A3D_candidate.btc.name(), A3D_candidate.btc)
					.addAvgAggregation(A3D_candidate.pj.name(), A3D_candidate.pj)
					.startBucket(A3D_candidate.ddd.name(), A3D_candidate.ddd, 6666)
						.startAggregations()
							.addAvgAggregation(A3D_candidate.experience.name(), A3D_candidate.experience)
							.addAvgAggregation(A3D_candidate.clt.name(), A3D_candidate.clt)
							.addAvgAggregation(A3D_candidate.btc.name(), A3D_candidate.btc)
							.addAvgAggregation(A3D_candidate.pj.name(), A3D_candidate.pj)
						.endAggregationsAndBackToBucket()
					.endTermsBuckedAndBackToAggregations()
				.endAggregationsAndBackToRequest();
		
		must = query.startQuery().startBool().startMust();
		
		CcpMapDecorator put = values.put("_must", must);
		
		return put;
	}

}
