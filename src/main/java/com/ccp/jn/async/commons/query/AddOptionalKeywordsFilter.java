package com.ccp.jn.async.commons.query;

import java.util.List;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.query.Bool;
import com.ccp.especifications.db.query.Must;
import com.ccp.especifications.db.query.Should;
import com.ccp.process.CcpProcess;
import com.jn.commons.entities.fields.A3D_candidate;

public class AddOptionalKeywordsFilter implements CcpProcess {

	public CcpMapDecorator execute(CcpMapDecorator values) {
		Must must = values.getAsObject("_must");
		Bool bool = must.endMustAndBackToBool();
		
		List<String> optionalKeywords = values.getAsStringList("requiredKeywords");
		
		for (String keyword : optionalKeywords) {
			Should should = bool.startShould(1);
			
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
