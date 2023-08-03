package com.ccp.jn.async.commons.query;

import java.util.List;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.query.Must;
import com.ccp.especifications.db.query.Should;

import com.jn.commons.entities.fields.A3D_candidate;

public class AddRequiredKeywordsFilter implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator> {

	public CcpMapDecorator apply(CcpMapDecorator values) {
		
		Must must = values.getAsObject("_must");
		
		List<String> requiredKeywords = values.getAsStringList("requiredKeywords");
		
		Must must2 = must.startBool().startMust();
		
		for (String keyword : requiredKeywords) {
			Should should = must2.startBool().startShould(1);
			
			should = should.match(A3D_candidate.resumeWords, keyword, 2D, "and");
			should = should.matchPhrase(A3D_candidate.desiredJob, keyword, 5D);
			should = should.matchPhrase(A3D_candidate.currentJob, keyword, 1D);
			should = should.matchPhrase(A3D_candidate.synonyms, keyword, 3D);
			should = should.matchPhrase(A3D_candidate.keywords, keyword, 4D);
			
			must2 = should.endShouldAndBackToBool().endBoolAndBackToMust();
		}
		
		must = must2.endMustAndBackToBool().endBoolAndBackToMust();
		
		CcpMapDecorator put = values.put("_must", must);
		return put;
	}
	
}
