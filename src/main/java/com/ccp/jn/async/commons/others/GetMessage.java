package com.ccp.jn.async.commons.others;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.dao.CcpDao;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class GetMessage {

	private final List<Function<CcpMapDecorator, CcpMapDecorator>> process = new ArrayList<>();

	private final List<JnEntity> parameterEntities = new ArrayList<>() ;
	
	private final List<JnEntity> messageEntities = new ArrayList<>();
	
	@CcpDependencyInject
	private CcpDao dao;

	public GetMessage addFlow(Function<CcpMapDecorator, CcpMapDecorator> process, JnEntity parameterEntity, JnEntity messageEntity) {
		
		GetMessage getMessage = new GetMessage();
		
		getMessage.parameterEntities.addAll(this.parameterEntities);
		getMessage.messageEntities.addAll(this.messageEntities);
		getMessage.process.addAll(this.process);
		
		getMessage.parameterEntities.add(parameterEntity);
		getMessage.messageEntities.add(messageEntity);
		getMessage.process.add(process);
		getMessage.dao = this.dao;
		
		return getMessage;
	}
	
	public CcpMapDecorator execute(JnTopic id, JnEntity entityToSave, CcpMapDecorator values, String language) {
		
		List<JnEntity> allEntitiesToSearch = new ArrayList<>();
		allEntitiesToSearch.addAll(this.parameterEntities);
		allEntitiesToSearch.addAll(this.messageEntities);
		allEntitiesToSearch.add(entityToSave);
		
		JnEntity[] entities = allEntitiesToSearch.toArray(new JnEntity[allEntitiesToSearch.size()]);
		CcpMapDecorator idToSearch = new CcpMapDecorator().put("language", language).put("id", id.name());
		CcpMapDecorator allData = this.dao.getAllData(idToSearch, entities);
		boolean alreadySaved = allData.containsAllKeys(entityToSave.name());
		
		if(alreadySaved) {
			return values;
		}
		
		
		int k = 0;
		
		for (JnEntity messageEntity : this.messageEntities) {
			
			JnEntity parameterEntity = this.parameterEntities.get(k);
			
			CcpMapDecorator parameterData = allData.getInternalMap(parameterEntity.name());
			CcpMapDecorator moreParameters = parameterData.getInternalMap("moreParameters");
			CcpMapDecorator allParameters = parameterData.putAll(moreParameters);
			CcpMapDecorator messageData = allData.getInternalMap(messageEntity.name());
			
			CcpMapDecorator allDataTogether = messageData.putAll(allParameters).putAll(values);
			
			Set<String> keySet = messageData.keySet();
			
			CcpMapDecorator messageToSend = allDataTogether;
			
			for (String key : keySet) {
				messageToSend = messageToSend.putFilledTemplate(key, key);
			}
			Function<CcpMapDecorator, CcpMapDecorator> process = this.process.get(k);
			process.apply(messageToSend);
			k++;
		}
		return values;
	}
}
