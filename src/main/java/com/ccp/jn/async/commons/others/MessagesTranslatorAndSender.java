package com.ccp.jn.async.commons.others;

import java.util.List;
import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.dao.CcpDao;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class MessagesTranslatorAndSender {

	@CcpDependencyInject
	private CcpDao dao;

	public void execute(CcpMapDecorator externalParameters, JnTopic templateId, JnEntity entityToSave, List<Function<CcpMapDecorator, CcpMapDecorator>> processes, String... fields) {
	
		CcpMapDecorator put = externalParameters.put("id", templateId.name()).duplicateValueFromKey("id", "subjectType");
		
		CcpMapDecorator allData = this.dao.getAllData(put, entityToSave, JnEntity.parameters, JnEntity.messages);
		
		boolean alreadySaved = allData.containsAllKeys(entityToSave.name());
		
		if(alreadySaved) {
			return;
		}
		
		CcpMapDecorator parameters = allData.getInternalMap(JnEntity.parameters.name()).getInternalMap("value");
		
		CcpMapDecorator message = allData.getInternalMap(JnEntity.messages.name()).getInternalMap("value");
		
		CcpMapDecorator messageToSend = message.putAll(parameters);
		
		for (String field : fields) {
			CcpMapDecorator translated = messageToSend.putFilledTemplate(field, field);
			messageToSend = translated.putAll(externalParameters).putFilledTemplate(field, field);
		}
		for (Function<CcpMapDecorator, CcpMapDecorator> process : processes) {
			process.apply(messageToSend.put("subjectType", templateId.name()));
		}
		
		entityToSave.createOrUpdate(messageToSend);
	}
	

}
