package com.ccp.jn.async.commons.query;

import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.query.CcpDbQueryOptions;
import com.ccp.especifications.db.query.CcpDbQueryMust;


public class JnAddSortCriteria implements  java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	@Override
	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {

		CcpDbQueryMust must = values.getAsObject("_must");
		CcpDbQueryOptions query = must.endMustAndBackToBool().endBoolAndBackToQuery().endQueryAndBackToRequest();
		
		CcpJsonRepresentation sort = values.getInnerJson("sort");
		
		query = this.addSorting(query, sort, "desc");
		query = this.addSorting(query, sort, "asc");
		
		must = query.startQuery().startBool().startMust();
		
		CcpJsonRepresentation put = values.put("_must", must);
		
		return put;
	}

	private CcpDbQueryOptions addSorting(CcpDbQueryOptions query, CcpJsonRepresentation sort, String sortType) {
		List<String> list = sort.getAsStringList(sortType);
		String[] field = new String[list.size()];
		list.toArray(field);
		query = query.addSorting(sortType,  field);
		return query;
	}

	
	
 
}
