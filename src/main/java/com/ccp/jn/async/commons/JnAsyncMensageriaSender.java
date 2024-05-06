package com.ccp.jn.async.commons;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.jn.commons.entities.JnEntityAsyncTask;
import com.jn.commons.utils.JnGenerateRandomToken;

public class JnAsyncMensageriaSender {
	private final CcpMensageriaSender mensageriaSender = CcpDependencyInjection.getDependency(CcpMensageriaSender.class);
	
	private JnAsyncMensageriaSender() {
		
	}
	
	public static final JnAsyncMensageriaSender INSTANCE = new JnAsyncMensageriaSender();
	
	public void send(String topic, CcpEntity entity, CcpJsonRepresentation... messages) {
		List<CcpJsonRepresentation> msgs = Arrays.asList(messages).stream().map(message -> this.getMessageDetails(topic, message)).collect(Collectors.toList());
		List<CcpBulkItem> bulkItems = msgs.stream().map(msg -> this.toBulkItem(entity, msg)).collect(Collectors.toList());
		JnAsyncCommitAndAudit.INSTANCE.execute(bulkItems);
		this.mensageriaSender.send(topic, msgs);
	}
	
	public void send(Enum<?> topic,  List<CcpJsonRepresentation> messages) {
		String topicName = topic.name();
		int size = messages.size();
		CcpJsonRepresentation[] a = new CcpJsonRepresentation[size];
		CcpJsonRepresentation[] array = messages.toArray(a);
		this.send(topicName, JnEntityAsyncTask.INSTANCE, array);
	}

	public void send(Enum<?> topic,  CcpJsonRepresentation... messages) {
		String topicName = topic.name();
		this.send(topicName, JnEntityAsyncTask.INSTANCE, messages);
	}
	
	private CcpBulkItem toBulkItem( CcpEntity entity, CcpJsonRepresentation msg) {
		String asyncTaskId = entity.getId(msg);
		CcpBulkItem bulkItem = new CcpBulkItem(msg, CcpEntityOperationType.create, entity, asyncTaskId);
		return bulkItem;
	}
	
	private CcpJsonRepresentation getMessageDetails(String topic, CcpJsonRepresentation values) {
		String formattedCurrentDateTime = new CcpTimeDecorator().getFormattedDateTime("dd/MM/yyyy HH:mm:ss");
		CcpJsonRepresentation messageDetails = CcpConstants.EMPTY_JSON
				.put("started", System.currentTimeMillis())
				.put("data", formattedCurrentDateTime)
				.put("request", values)
				.put("topic", topic)
				.putAll(values)
				;
		JnGenerateRandomToken transformer = new JnGenerateRandomToken(20, "id");
		CcpJsonRepresentation transformed = messageDetails.getTransformed(transformer);
		return transformed;
	}

}
