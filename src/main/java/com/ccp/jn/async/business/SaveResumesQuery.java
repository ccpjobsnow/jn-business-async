package com.ccp.jn.async.business;

import java.util.List;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.query.CcpDbQueryExecutor;
import com.ccp.especifications.db.query.ElasticQuery;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.tables.fields.A3D_candidate;

public class SaveResumesQuery implements CcpProcess{

	CcpMapDecorator keywordsTables = new CcpMapDecorator()
			.put("4", JnBusinessEntity.keywords_operational)
			.put("3", JnBusinessEntity.keywords_college)
			.put("1", JnBusinessEntity.keywords_it)
			.put("2", JnBusinessEntity.keywords_hr)
			;

	
	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {

		notifyAboutUnknownKeyword(values);
		
		return values;
	}

	private void notifyAboutUnknownKeyword(CcpMapDecorator values) {
		
		Integer jobType = values.getAsIntegerNumber(A3D_candidate.jobType.name());
		
		if(jobType == null) {
			return;
		}
		
		Integer total = values.getAsIntegerNumber("total");
		
		if(total == null) {
			return;
		}
		
		if(total < 2) {
			return;
		}
		
		JnBusinessEntity keywordsTable = this.keywordsTables.getAsObject(jobType.toString());
	
		if(keywordsTable == null) {
			return;
		}
		
		List<String> optionalKeywords = values.getAsStringList("optionalKeywords");
		List<String> requiredKeywords = values.getAsStringList("requiredKeywords");
	}

}
