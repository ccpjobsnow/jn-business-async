package com.ccp.jn.async.commons.query;

import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.query.CcpDbQueryBool;
import com.ccp.especifications.db.query.CcpDbQueryMust;
import com.ccp.especifications.db.query.CcpDbQueryShould;
import com.jn.commons.entities.JnEntityCandidate;

public class JnAddOptionalKeywordsFilter implements  java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {
		CcpDbQueryMust must = values.getAsObject("_must");
		CcpDbQueryBool bool = must.endMustAndBackToBool();
		
		List<String> optionalKeywords = values.getAsStringList("requiredKeywords");
		
		for (String keyword : optionalKeywords) {
			CcpDbQueryShould should = bool.startShould(1);
			
			should = should.match(JnEntityCandidate.Fields.resumeWords, keyword, 2D, "and");
			should = should.matchPhrase(JnEntityCandidate.Fields.desiredJob, keyword, 1D);
			should = should.matchPhrase(JnEntityCandidate.Fields.currentJob, keyword, 5D);
			should = should.matchPhrase(JnEntityCandidate.Fields.synonyms, keyword, 3D);
			should = should.matchPhrase(JnEntityCandidate.Fields.keywords, keyword, 4D);
			
			bool = should.endShouldAndBackToBool();
		}
		
		must = bool.endBoolAndBackToMust();
		
		CcpJsonRepresentation put = values.put("_must", must);
		return put;
	}
	
}
