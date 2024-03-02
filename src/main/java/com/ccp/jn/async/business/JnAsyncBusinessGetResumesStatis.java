package com.ccp.jn.async.business;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.query.CcpDbQueryMust;
import com.ccp.especifications.db.query.CcpDbQueryOptions;
import com.ccp.especifications.db.query.CcpQueryExecutorDecorator;
import com.ccp.especifications.db.utils.CcpEntityField;
import com.jn.commons.entities.JnEntityCandidate;

public class JnAsyncBusinessGetResumesStatis implements  java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation> {



	@Override
	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {

		CcpDbQueryMust must = values.getAsObject("_must");
		
		CcpDbQueryOptions query = must.endMustAndBackToBool().endBoolAndBackToQuery().endQueryAndBackToRequest();
		
		CcpQueryExecutorDecorator selectFrom = query.selectFrom(new JnEntityCandidate().name());
		
		CcpJsonRepresentation aggregations = selectFrom.getAggregations();

		CcpJsonRepresentation total = aggregations.getInnerJson("total");
		
		Integer doc_count = total.getAsIntegerNumber("doc_count");
		
		CcpJsonRepresentation entireCountry = aggregations.put("doc_count", doc_count).put("key", 0);
		
		CcpJsonRepresentation allRegions = aggregations.getInnerJson("ddd");
		
		List<CcpJsonRepresentation> eachRegion = allRegions.getAsJsonList("buckets")
				.stream().map(ddd ->  this.getMapDecorator(ddd, 
						JnEntityCandidate.Fields.experience, 
						JnEntityCandidate.Fields.clt, 
						JnEntityCandidate.Fields.btc,
						JnEntityCandidate.Fields.pj
						)).collect(Collectors.toList());

		ArrayList<CcpJsonRepresentation> results = new ArrayList<>(eachRegion);
		 
		results.add(entireCountry);
		
		CcpJsonRepresentation put = CcpConstants.EMPTY_JSON.put("results", results);
		
		return put;
	}

	
	private CcpJsonRepresentation getMapDecorator(CcpJsonRepresentation ddd, CcpEntityField... fields) {
		
		CcpJsonRepresentation values = CcpConstants.EMPTY_JSON
				.put("ddd", ddd.getAsIntegerNumber("key"))
				.put("total", ddd.getAsIntegerNumber("doc_count"))
				;
		
		for (CcpEntityField field : fields) {
			String fieldName = field.name();
			CcpJsonRepresentation internalMap = ddd.getInnerJson(fieldName);
			Double value = internalMap.getAsDoubleNumber("value");
			values = values.put(fieldName, value);
		}
		
		return values;
	}
}
