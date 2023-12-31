package com.ccp.jn.async.commons.query;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.query.CcpDbQueryMust;
import com.ccp.especifications.db.query.CcpDbQueryOptions;
import com.jn.commons.entities.JnEntityCandidate;

public class JnAddGroupByCriteria implements  java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	@Override
	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {

		CcpDbQueryMust must = values.getAsObject("_must");
		
		CcpDbQueryOptions query = must.endMustAndBackToBool().endBoolAndBackToQuery().endQueryAndBackToRequest();
		
		query = query.startAggregations()
					.addAvgAggregation(JnEntityCandidate.Fields.experience.name(), JnEntityCandidate.Fields.experience)
					.addAvgAggregation(JnEntityCandidate.Fields.clt.name(), JnEntityCandidate.Fields.clt)
					.addAvgAggregation(JnEntityCandidate.Fields.btc.name(), JnEntityCandidate.Fields.btc)
					.addAvgAggregation(JnEntityCandidate.Fields.pj.name(), JnEntityCandidate.Fields.pj)
					.startBucket(JnEntityCandidate.Fields.ddd.name(), JnEntityCandidate.Fields.ddd, 6666)
						.startAggregations()
							.addAvgAggregation(JnEntityCandidate.Fields.experience.name(), JnEntityCandidate.Fields.experience)
							.addAvgAggregation(JnEntityCandidate.Fields.clt.name(), JnEntityCandidate.Fields.clt)
							.addAvgAggregation(JnEntityCandidate.Fields.btc.name(), JnEntityCandidate.Fields.btc)
							.addAvgAggregation(JnEntityCandidate.Fields.pj.name(), JnEntityCandidate.Fields.pj)
						.endAggregationsAndBackToBucket()
					.endTermsBuckedAndBackToAggregations()
				.endAggregationsAndBackToRequest();
		
		must = query.startQuery().startBool().startMust();
		
		CcpJsonRepresentation put = values.put("_must", must);
		
		return put;
	}

}
