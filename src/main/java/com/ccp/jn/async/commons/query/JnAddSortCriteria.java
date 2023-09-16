package com.ccp.jn.async.commons.query;

import java.util.List;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.query.CcpDbQueryOptions;
import com.ccp.especifications.db.query.CcpDbQueryMust;


public class JnAddSortCriteria implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator>{

	@Override
	public CcpMapDecorator apply(CcpMapDecorator values) {

		CcpDbQueryMust must = values.getAsObject("_must");
		CcpDbQueryOptions query = must.endMustAndBackToBool().endBoolAndBackToQuery().endQueryAndBackToRequest();
		
		CcpMapDecorator sort = values.getInternalMap("sort");
		
		query = this.addSorting(query, sort, "desc");
		query = this.addSorting(query, sort, "asc");
		
		must = query.startQuery().startBool().startMust();
		
		CcpMapDecorator put = values.put("_must", must);
		
		return put;
	}

	private CcpDbQueryOptions addSorting(CcpDbQueryOptions query, CcpMapDecorator sort, String sortType) {
		List<String> list = sort.getAsStringList(sortType);
		String[] field = new String[list.size()];
		list.toArray(field);
		query = query.addSorting(sortType,  field);
		return query;
	}

	
	
 
}
