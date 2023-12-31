package com.ccp.jn.async.commons.query;

import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.query.CcpDbQueryMust;
import com.ccp.especifications.db.query.CcpDbQueryShould;
import com.jn.commons.entities.JnEntityCandidate;

public class JnAddRequiredKeywordsFilter implements  java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {
		
		CcpDbQueryMust must = values.getAsObject("_must");
		
		List<String> requiredKeywords = values.getAsStringList("requiredKeywords");
		
		CcpDbQueryMust must2 = must.startBool().startMust();
		
		for (String keyword : requiredKeywords) {
			CcpDbQueryShould should = must2.startBool().startShould(1);
			
			should = should.match(JnEntityCandidate.Fields.resumeWords, keyword, 2D, "and");
			should = should.matchPhrase(JnEntityCandidate.Fields.desiredJob, keyword, 5D);
			should = should.matchPhrase(JnEntityCandidate.Fields.currentJob, keyword, 1D);
			should = should.matchPhrase(JnEntityCandidate.Fields.synonyms, keyword, 3D);
			should = should.matchPhrase(JnEntityCandidate.Fields.keywords, keyword, 4D);
			
			must2 = should.endShouldAndBackToBool().endBoolAndBackToMust();
		}
		
		must = must2.endMustAndBackToBool().endBoolAndBackToMust();
		
		CcpJsonRepresentation put = values.put("_must", must);
		return put;
	}
	
}
