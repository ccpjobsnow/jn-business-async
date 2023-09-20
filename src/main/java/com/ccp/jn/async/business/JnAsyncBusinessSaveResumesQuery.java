package com.ccp.jn.async.business;

import java.util.List;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.db.utils.CcpEntityOperationType;
import com.jn.commons.entities.JnEntityCandidate;
import com.jn.commons.entities.JnEntityKeywordsCollege;
import com.jn.commons.entities.JnEntityKeywordsHr;
import com.jn.commons.entities.JnEntityKeywordsIt;
import com.jn.commons.entities.JnEntityKeywordsOperational;
import com.jn.commons.entities.JnEntityKeywordsUnknow;
import com.jn.commons.entities.JnEntitySearchResumeStatis;

public class JnAsyncBusinessSaveResumesQuery implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator>{

	private CcpMapDecorator keywordsentities = new CcpMapDecorator()
			.put("4", new JnEntityKeywordsOperational())
			.put("3", new JnEntityKeywordsCollege())
			.put("1", new JnEntityKeywordsIt())
			.put("2", new JnEntityKeywordsHr())
			;

	private final JnAsyncBusinessTryToSendInstantMessage sendInstantMessage = new JnAsyncBusinessTryToSendInstantMessage();

	private JnAsyncBusinessCommitAndAudit commitAndAudit = new JnAsyncBusinessCommitAndAudit();

	@Override
	public CcpMapDecorator apply(CcpMapDecorator values) {
		
		new JnEntitySearchResumeStatis().createOrUpdate(values);
		
		values = this.getUnknownKeywords(values, "requiredKeywords");
		values = this.getUnknownKeywords(values, "optionalKeywords");
		
		CcpMapDecorator message = values.getInternalMap("message");

		boolean naoHaMensagemParaEnviar = message.isEmpty();
		
		if(naoHaMensagemParaEnviar) {
			return values;
		}
		
//		CcpMapDecorator idToSearch = new CcpMapDecorator().put("name", JnTopic.saveResumesQuery.name());
		CcpMapDecorator parameters = new CcpMapDecorator();//TODO
		values = values.putAll(parameters);
		this.sendInstantMessage.apply(values);
		
		return values;
	}

	private CcpMapDecorator getUnknownKeywords(CcpMapDecorator values, String keywordType) {
		
		Integer jobType = values.getAsIntegerNumber(JnEntityCandidate.Fields.jobType.name());
		
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
		
		CcpEntity keywordsEntity = this.keywordsentities.getAsObject(jobType.toString());
	
		if(keywordsEntity == null) {
			return values;
		}

		
		List<String> keywords = values.getAsStringList(keywordType);
		CcpEntity keywordsUnknown = new JnEntityKeywordsUnknow();
	
		List<CcpMapDecorator> idsToKnownWords = keywords.stream().map(keyword -> this.putKeyword(keyword)).collect(Collectors.toList());
		List<CcpMapDecorator> unknowKeywords = keywordsEntity.getManyByIds(idsToKnownWords).stream().filter(x -> this.notFound(x)).collect(Collectors.toList());
		List<CcpMapDecorator> idsToUnknownWords = unknowKeywords.stream().map(keyword -> this.getUnknownKeyword(keywordType, jobType)).collect(Collectors.toList());
		List<CcpMapDecorator> newUnknowKeywords = keywordsUnknown.getManyByIds(idsToUnknownWords).stream().filter(x -> this.notFound(x)).collect(Collectors.toList());
		
		if(newUnknowKeywords.isEmpty()) {
			return values;
		}
		List<CcpMapDecorator> newUnknowKeywordsToSave = newUnknowKeywords.stream().map(x -> x.getInternalMap("_id")).collect(Collectors.toList());
		
		this.commitAndAudit.execute(newUnknowKeywordsToSave, CcpEntityOperationType.create, keywordsUnknown);
		
		List<String> justStrings = newUnknowKeywordsToSave.stream().map(x -> x.getAsString("keyword")).collect(Collectors.toList());

		values = values
        	  .putSubKey("message", JnEntityKeywordsUnknow.Fields.keywordType.name(), keywordType)
        	  .putSubKey("message", JnEntityCandidate.Fields.jobType.name(), jobType)
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
		CcpMapDecorator put = new CcpMapDecorator().renameKey("id", "keyword").put(JnEntityKeywordsUnknow.Fields.keywordType.name(), keywordType).put(JnEntityCandidate.Fields.jobType.name(), jobType);
		return put;
	}
	

}
