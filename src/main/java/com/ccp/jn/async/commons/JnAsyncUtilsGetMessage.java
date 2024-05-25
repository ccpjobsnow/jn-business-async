package com.ccp.jn.async.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.crud.CcpCrud;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.utils.CcpEntity;

public class JnAsyncUtilsGetMessage {

	private final List<Function<CcpJsonRepresentation, CcpJsonRepresentation>> process = new ArrayList<>();

	private final List<CcpEntity> parameterEntities = new ArrayList<>() ;
	
	private final List<CcpEntity> messageEntities = new ArrayList<>();
	
	public JnAsyncUtilsGetMessage addOneStep(Function<CcpJsonRepresentation, CcpJsonRepresentation> process, CcpEntity parameterEntity, CcpEntity messageEntity) {
		
		JnAsyncUtilsGetMessage getMessage = new JnAsyncUtilsGetMessage();
		
		getMessage.parameterEntities.addAll(this.parameterEntities);
		getMessage.messageEntities.addAll(this.messageEntities);
		getMessage.process.addAll(this.process);
		
		getMessage.parameterEntities.add(parameterEntity);
		getMessage.messageEntities.add(messageEntity);
		getMessage.process.add(process);
		
		return getMessage;
	}
	
	public CcpJsonRepresentation executeAllSteps(String entityId, CcpEntity entityToSave, CcpJsonRepresentation entityValues, String language) {
		
		List<CcpEntity> allEntitiesToSearch = new ArrayList<>();
		allEntitiesToSearch.addAll(this.parameterEntities);
		allEntitiesToSearch.addAll(this.messageEntities);
		allEntitiesToSearch.add(entityToSave);
		
		CcpEntity[] entities = allEntitiesToSearch.toArray(new CcpEntity[allEntitiesToSearch.size()]);
		CcpJsonRepresentation idToSearch = entityValues.put("language", language)
				.put("templateId", entityId);
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		
		CcpSelectUnionAll unionAll = crud.unionAll(idToSearch, entities);
		
		boolean alreadySaved = entityToSave.isPresentInThisUnionAll(unionAll, idToSearch);
		
		if(alreadySaved) {
			return entityValues;
		}
		
		int k = 0;
		
		for (CcpEntity messageEntity : this.messageEntities) {
			
			CcpEntity parameterEntity = this.parameterEntities.get(k);
			
			CcpJsonRepresentation parameterData = unionAll.getRequiredEntityRow(parameterEntity, idToSearch);
			CcpJsonRepresentation moreParameters = parameterData.getInnerJson("moreParameters");
			CcpJsonRepresentation allParameters = parameterData.removeKey("moreParameters").putAll(moreParameters);
			CcpJsonRepresentation messageData = unionAll.getRequiredEntityRow(messageEntity, idToSearch);
			
			CcpJsonRepresentation allDataTogether = messageData.putAll(allParameters).putAll(entityValues);
			
 			Set<String> keySet = messageData.keySet();
			
			CcpJsonRepresentation messageToSend = allDataTogether;
			
			for (String key : keySet) {
				messageToSend = messageToSend.putFilledTemplate(key, key);
			}
			Function<CcpJsonRepresentation, CcpJsonRepresentation> process = this.process.get(k);
			process.apply(messageToSend);
			k++;
		}
		entityToSave.createOrUpdate(entityValues);
		return entityValues;
	}
}
