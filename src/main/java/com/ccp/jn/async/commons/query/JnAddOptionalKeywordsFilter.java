package com.ccp.jn.async.commons.query;

import java.util.List;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.query.CcpDbQueryBool;
import com.ccp.especifications.db.query.CcpDbQueryMust;
import com.ccp.especifications.db.query.CcpDbQueryShould;

import com.jn.commons.entities.fields.A3D_candidate;

public class JnAddOptionalKeywordsFilter implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator> {

	public CcpMapDecorator apply(CcpMapDecorator values) {
		CcpDbQueryMust must = values.getAsObject("_must");
		CcpDbQueryBool bool = must.endMustAndBackToBool();
		
		List<String> optionalKeywords = values.getAsStringList("requiredKeywords");
		
		for (String keyword : optionalKeywords) {
			CcpDbQueryShould should = bool.startShould(1);
			
			should = should.match(A3D_candidate.resumeWords, keyword, 2D, "and");
			should = should.matchPhrase(A3D_candidate.desiredJob, keyword, 1D);
			should = should.matchPhrase(A3D_candidate.currentJob, keyword, 5D);
			should = should.matchPhrase(A3D_candidate.synonyms, keyword, 3D);
			should = should.matchPhrase(A3D_candidate.keywords, keyword, 4D);
			
			bool = should.endShouldAndBackToBool();
		}
		
		must = bool.endBoolAndBackToMust();
		
		CcpMapDecorator put = values.put("_must", must);
		return put;
	}
	
}
