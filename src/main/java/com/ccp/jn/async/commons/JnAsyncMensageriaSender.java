package com.ccp.jn.async.commons;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.exceptions.process.CcpAsyncTask;
import com.jn.commons.entities.JnEntityAsyncTask;
import com.jn.commons.utils.JnTopic;

public class JnAsyncMensageriaSender {
	private final CcpMensageriaSender mensageriaSender = CcpDependencyInjection.getDependency(CcpMensageriaSender.class);
	
	private JnAsyncMensageriaSender() {}
	
	public static final JnAsyncMensageriaSender INSTANCE = new JnAsyncMensageriaSender();
	
	public void send(JnTopic topic, CcpEntity entity, CcpJsonRepresentation... messages) {
		List<CcpJsonRepresentation> msgs = Arrays.asList(messages).stream().map(json -> this.getMessageDetails(topic.name(), json)).collect(Collectors.toList());
		List<CcpBulkItem> bulkItems = msgs.stream().filter(x -> topic.canSave()).map(msg -> this.toBulkItem(entity, msg)).collect(Collectors.toList());
		JnAsyncCommitAndAudit.INSTANCE.executeBulk(bulkItems);
		this.mensageriaSender.send(topic.name(), msgs);
	}
	
	public void send(JnTopic topic,  List<CcpJsonRepresentation> messages) {
		
		int size = messages.size();
		CcpJsonRepresentation[] a = new CcpJsonRepresentation[size];
		CcpJsonRepresentation[] array = messages.toArray(a);
		this.send(topic, JnEntityAsyncTask.ENTITY, array);
	}

	public CcpJsonRepresentation send(JnTopic topic,  CcpJsonRepresentation... messages) {
		this.send(topic, JnEntityAsyncTask.ENTITY, messages);
		return CcpOtherConstants.EMPTY_JSON;
	}
	
	private CcpBulkItem toBulkItem( CcpEntity entity, CcpJsonRepresentation msg) {
		String asyncTaskId = entity.calculateId(msg);
		CcpBulkItem bulkItem = new CcpBulkItem(msg, CcpEntityOperationType.create, entity, asyncTaskId);
		return bulkItem;
	}
	
	private CcpJsonRepresentation getMessageDetails(String topic, CcpJsonRepresentation json) {
		
		String formattedCurrentDateTime = new CcpTimeDecorator().getFormattedDateTime("dd/MM/yyyy HH:mm:ss");
		
		long currentTimeMillis = System.currentTimeMillis();
		
		CcpJsonRepresentation messageDetails = CcpOtherConstants.EMPTY_JSON
				.put(JnEntityAsyncTask.Fields.data.name(), formattedCurrentDateTime)
				.put(JnEntityAsyncTask.Fields.messageId.name(), currentTimeMillis)
				.put(JnEntityAsyncTask.Fields.started.name(), currentTimeMillis)
				.put(JnEntityAsyncTask.Fields.request.name(), json)
				.put(JnEntityAsyncTask.Fields.topic.name(), topic)
				.putAll(json)
				;
		
		return messageDetails;
	}
	private void saveResult(
			CcpEntity entity, 
			CcpJsonRepresentation messageDetails, 
			Throwable e,
			Function<CcpJsonRepresentation, CcpJsonRepresentation> jnAsyncBusinessNotifyError
			) {
		CcpJsonRepresentation response = new CcpJsonRepresentation(e);
		this.saveResult(entity, messageDetails, response, false);
		
	}

	private void saveResult(CcpEntity entity, CcpJsonRepresentation messageDetails, CcpJsonRepresentation response) {
		this.saveResult(entity, messageDetails, response, true);
	}
	
	public void executeProcesss(
			CcpEntity entity,
			String processName, 
			CcpJsonRepresentation messageDetails,
			Function<CcpJsonRepresentation, CcpJsonRepresentation> jnAsyncBusinessNotifyError
			) {
		try {
			Function<CcpJsonRepresentation, CcpJsonRepresentation> process = CcpAsyncTask.getProcess(processName);
			CcpJsonRepresentation response = process.apply(messageDetails);
			this.saveResult(entity, messageDetails, response);
		} catch (Throwable e) {
			this.saveResult(entity, messageDetails, e, jnAsyncBusinessNotifyError);
		}

	}
	
	private void saveResult(CcpEntity entity, CcpJsonRepresentation messageDetails, CcpJsonRepresentation response, boolean success) {
		Long finished = System.currentTimeMillis();
		CcpJsonRepresentation oneById = entity.getOneById(messageDetails);
		Long started = oneById.getAsLongNumber("started");
		Long enlapsedTime = finished - started;
		CcpJsonRepresentation processResult = messageDetails.put("enlapsedTime", enlapsedTime).put("response", response).put("finished", finished).put("success", success);
		entity.createOrUpdate(processResult);
	}

}
