package com.ccp.jn.async.business;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.jn.async.commons.query.JnAddFilter;
import com.ccp.jn.async.commons.query.JnAddGroupByCriteria;
import com.ccp.jn.async.commons.query.JnAddOptionalKeywordsFilter;
import com.ccp.jn.async.commons.query.JnAddRequiredKeywordsFilter;
import com.ccp.jn.async.commons.query.JnAddSizeInTheQuery;
import com.ccp.jn.async.commons.query.JnAddSortCriteria;
import com.jn.commons.entities.fields.A3D_candidate;
import com.jn.commons.utils.JnTopic;

public class JnAsyncBusinessListResumes {

	public List<Map<String, Object>> execute(String recruiter, String json){
		
		CcpMapDecorator values = this.createSelect(json);
		
		values = this.createWhere(values);

		List<Map<String, Object>> extractResults = this.extractResults(values);
		
		this.sendToTopic(values, extractResults, recruiter);

		return extractResults;
	}

	private void sendToTopic(CcpMapDecorator values, List<Map<String, Object>> extractResults, String recruiter) {
		CcpMapDecorator message = new CcpMapDecorator().put("query",values).put("results", extractResults).put("recruiter", recruiter);
		JnTopic.saveResumesQuery.send(message);
	}

	private List<Map<String, Object>> extractResults(CcpMapDecorator values) {
		
		values = values.whenHasNotKey(A3D_candidate.ddd.name(), new JnAsyncBusinessGetResumesStatis());
		
		values = values.whenHasKey(A3D_candidate.ddd.name(), new JnAsyncBusinessGetResumesList());
		
		List<Map<String, Object>> results = values.getAsMapList("results").stream().map(x -> x.content).collect(Collectors.toList());
	
		return results;
	}

	private CcpMapDecorator createWhere(CcpMapDecorator values) {
		
		values = values.whenHasKey(A3D_candidate.seniority.name(), new JnAddFilter(A3D_candidate.seniority));
		values = values.whenHasKey(A3D_candidate.pcd.name(), new JnAddFilter(A3D_candidate.pcd));
		values = values.whenHasKey(A3D_candidate.ddd.name(), new JnAddFilter(A3D_candidate.ddd));
		values = values.whenHasKey("requiredKeywords", new JnAddRequiredKeywordsFilter());
		values = values.whenHasKey("optionalKeywords", new JnAddOptionalKeywordsFilter());
		
		return values;
	}

	private CcpMapDecorator createSelect(String json) {
		CcpMapDecorator values = new CcpMapDecorator(json);
		
		values = values.getTransformed(new JnAddSizeInTheQuery());
		values = values.whenHasKey("sort", new JnAddSortCriteria());
		values = values.whenHasNotKey(A3D_candidate.ddd.name(), new JnAddGroupByCriteria());
		return values;
	}
}
