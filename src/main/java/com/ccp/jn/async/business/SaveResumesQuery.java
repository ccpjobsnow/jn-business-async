package com.ccp.jn.async.business;

import java.util.List;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.bulk.CcpBulkOperation;
import com.ccp.especifications.db.bulk.CcpDbBulkExecutor;
import com.ccp.jn.async.AsyncServices;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.JnBusinessTopic;
import com.jn.commons.tables.fields.A3D_candidate;
import com.jn.commons.tables.fields.A3D_keywords_unknown;

public class SaveResumesQuery implements CcpProcess{

	private CcpMapDecorator keywordsTables = new CcpMapDecorator()
			.put("4", JnBusinessEntity.keywords_operational)
			.put("3", JnBusinessEntity.keywords_college)
			.put("1", JnBusinessEntity.keywords_it)
			.put("2", JnBusinessEntity.keywords_hr)
			;

	@CcpDependencyInject
	private SendInstantMessage sendInstantMessage = AsyncServices.catalog.getAsObject(JnBusinessTopic.sendInstantMessage.name());
	
	@CcpDependencyInject
	private CcpDbBulkExecutor bulkExecutor;

	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {
		
		JnBusinessEntity.search_resumes_stats.save(values);
		
		values = this.getUnknownKeywords(values, "requiredKeywords");
		values = this.getUnknownKeywords(values, "optionalKeywords");
		
		CcpMapDecorator message = values.getInternalMap("message");

		boolean naoHaMensagemParaEnviar = message.isEmpty();
		
		if(naoHaMensagemParaEnviar) {
			return values;
		}
		
		CcpMapDecorator idToSearch = new CcpMapDecorator().put("name", JnBusinessTopic.saveResumesQuery.name());
		CcpMapDecorator parameters = JnBusinessEntity._static.get(idToSearch);
		values = values.putAll(parameters);
		this.sendInstantMessage.execute(values);
		
		return values;
	}

	private CcpMapDecorator getUnknownKeywords(CcpMapDecorator values, String keywordType) {
		
		Integer jobType = values.getAsIntegerNumber(A3D_candidate.jobType.name());
		
		if(jobType == null) {
			return values;
		}
		
		Integer total = values.getAsIntegerNumber("total");
		
		if(total == null) {
			return values;
		}
		
		if(total < 2) {
			return values;
		}
		
		JnBusinessEntity keywordsTable = this.keywordsTables.getAsObject(jobType.toString());
	
		if(keywordsTable == null) {
			return values;
		}

		
		List<String> keywords = values.getAsStringList(keywordType);
		JnBusinessEntity keywordsUnknown = JnBusinessEntity.keywords_unknown;
	
		List<CcpMapDecorator> idsToKnownWords = keywords.stream().map(keyword -> this.putKeyword(keyword)).collect(Collectors.toList());
		List<CcpMapDecorator> unknowKeywords = keywordsTable.getManyByIds(idsToKnownWords).stream().filter(x -> this.notFound(x)).collect(Collectors.toList());
		List<CcpMapDecorator> idsToUnknownWords = unknowKeywords.stream().map(keyword -> this.getUnknownKeyword(keywordType, jobType)).collect(Collectors.toList());
		List<CcpMapDecorator> newUnknowKeywords = keywordsUnknown.getManyByIds(idsToUnknownWords).stream().filter(x -> this.notFound(x)).collect(Collectors.toList());
		
		if(newUnknowKeywords.isEmpty()) {
			return values;
		}
		List<CcpMapDecorator> newUnknowKeywordsToSave = newUnknowKeywords.stream().map(x -> x.getInternalMap("_id")).collect(Collectors.toList());
		
		this.bulkExecutor.commit(newUnknowKeywordsToSave, CcpBulkOperation.INSERT, keywordsUnknown);
		
		List<String> justStrings = newUnknowKeywordsToSave.stream().map(x -> x.getAsString("keyword")).collect(Collectors.toList());

		values = values
        	  .putSubKey("message", A3D_keywords_unknown.keywordType.name(), keywordType)
        	  .putSubKey("message", A3D_candidate.jobType.name(), jobType)
			  .putSubKey("message", keywordType, justStrings)
				;
		return values;
	}

	private CcpMapDecorator putKeyword(String keyword) {
		return new CcpMapDecorator().put("keyword", keyword);
	}

	private boolean notFound(CcpMapDecorator x) {
		return x.getAsBoolean("_found") == false;
	}

	private CcpMapDecorator getUnknownKeyword(String keywordType, Integer jobType) {
		CcpMapDecorator put = new CcpMapDecorator().renameKey("id", "keyword").put(A3D_keywords_unknown.keywordType.name(), keywordType).put(A3D_candidate.jobType.name(), jobType);
		return put;
	}
	

}
