package com.ccp.jn.async.commons.others;

import java.util.List;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.dao.CcpDao;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class MessagesTranslatorAndSender {

	@CcpDependencyInject
	private CcpDao dao;

	public void execute(CcpMapDecorator values, JnTopic templateId, JnEntity entityToSave, List<CcpProcess> processes, String... fields) {
	
		CcpMapDecorator put = values.put("id", templateId.name());
		
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
			messageToSend = translated.putAll(values).putFilledTemplate(field, field);
		}
		for (CcpProcess process : processes) {
			process.execute(messageToSend);
		}
		
		entityToSave.createOrUpdate(messageToSend);
	}
	

}
