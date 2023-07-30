package com.ccp.jn.async.commons.others;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.query.CcpDbQueryExecutor;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.jn.async.commons.query.AddFilter;
import com.ccp.jn.async.commons.query.AddGroupByCriteria;
import com.ccp.jn.async.commons.query.AddOptionalKeywordsFilter;
import com.ccp.jn.async.commons.query.AddRequiredKeywordsFilter;
import com.ccp.jn.async.commons.query.AddSizeInTheQuery;
import com.ccp.jn.async.commons.query.AddSortCriteria;
import com.jn.commons.JnTopic;
import com.jn.commons.entities.fields.A3D_candidate;

public class ListResumes {

	@CcpDependencyInject
	private CcpDbQueryExecutor requestExecutor;

	
	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;

	
	public List<Map<String, Object>> execute(String recruiter, String json){
		
		CcpMapDecorator values = this.createSelect(json);
		
		values = this.createWhere(values);

		List<Map<String, Object>> extractResults = this.extractResults(values);
		
		this.sendToTopic(values, extractResults, recruiter);

		return extractResults;
	}

	private void sendToTopic(CcpMapDecorator values, List<Map<String, Object>> extractResults, String recruiter) {
		CcpMapDecorator message = new CcpMapDecorator().put("query",values).put("results", extractResults).put("recruiter", recruiter);
		this.mensageriaSender.send(message, JnTopic.saveResumesQuery);
	}

	private List<Map<String, Object>> extractResults(CcpMapDecorator values) {
		
		values = values.whenHasNotKey(A3D_candidate.ddd.name(), new GetResumesStatis(this.requestExecutor));
		
		values = values.whenHasKey(A3D_candidate.ddd.name(), new GetResumesList(this.requestExecutor));
		
		List<Map<String, Object>> results = values.getAsMapList("results").stream().map(x -> x.content).collect(Collectors.toList());
	
		return results;
	}

	private CcpMapDecorator createWhere(CcpMapDecorator values) {
		
		values = values.whenHasKey(A3D_candidate.seniority.name(), new AddFilter(A3D_candidate.seniority));
		values = values.whenHasKey(A3D_candidate.pcd.name(), new AddFilter(A3D_candidate.pcd));
		values = values.whenHasKey(A3D_candidate.ddd.name(), new AddFilter(A3D_candidate.ddd));
		values = values.whenHasKey("requiredKeywords", new AddRequiredKeywordsFilter());
		values = values.whenHasKey("optionalKeywords", new AddOptionalKeywordsFilter());
		
		return values;
	}

	private CcpMapDecorator createSelect(String json) {
		CcpMapDecorator values = new CcpMapDecorator(json);
		
		values = values.getTransformed(new AddSizeInTheQuery());
		values = values.whenHasKey("sort", new AddSortCriteria());
		values = values.whenHasNotKey(A3D_candidate.ddd.name(), new AddGroupByCriteria());
		return values;
	}
}