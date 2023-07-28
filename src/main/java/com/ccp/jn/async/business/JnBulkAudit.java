package com.ccp.jn.async.business;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.bulk.CcpBulkAudit;
import com.ccp.especifications.db.bulk.CcpDbBulkExecutor;
import com.ccp.especifications.db.utils.CcpOperationType;
import com.jn.commons.JnEntity;

public class JnBulkAudit implements CcpBulkAudit {
	private JnBulkAudit() {
		
	}
	@CcpDependencyInject
	private CcpDbBulkExecutor bulkExecutor;
	
	@Override
	public void commit(List<CcpMapDecorator> records, CcpOperationType operation, CcpMapDecorator bulkResult) {

		List<CcpMapDecorator> failedRecords = new ArrayList<>();
		List<CcpMapDecorator> succedRecords = new ArrayList<>();

		List<CcpMapDecorator> items = bulkResult.getAsMapList("items").stream().map(x -> x.getInternalMap(operation.name())).collect(Collectors.toList());
		
		for (CcpMapDecorator item : items) {
			
			CcpMapDecorator auditObject = this.getAuditObject(records, item, operation);

			boolean hasNoError = item.containsKey("error") == false;
			
			if(hasNoError) {
				succedRecords.add(auditObject);
				continue;
			}
			
			failedRecords.add(auditObject);
		}
		
		
		CcpBulkAudit doNothing = (x, y, z) -> {};
		this.bulkExecutor.commit(failedRecords, CcpOperationType.create, JnEntity.record_to_reprocess, doNothing);
		this.bulkExecutor.commit(succedRecords, CcpOperationType.create, JnEntity.audit, doNothing);
	}

	
	private CcpMapDecorator getAuditObject(List<CcpMapDecorator> records, CcpMapDecorator error, CcpOperationType operation) {
		String id = error.getAsString("_id");
		String index = error.getAsString("_index");
		JnEntity entity = JnEntity.valueOf(index);
		Integer status = error.getAsIntegerNumber("status");
		CcpMapDecorator errorDetails = error.getInternalMap("error").renameKey("type", "errorType").getSubMap("errorType", "reason");
		
		CcpMapDecorator json = new ArrayList<>(records).stream().filter(record -> entity.getId(record).equals(id)).findFirst().get();
		CcpMapDecorator mappedError = new CcpMapDecorator().put("date", System.currentTimeMillis()).put("operation", operation.name())
				.put("index", index).put("id", id).put("json", json).put("status", status).putAll(errorDetails)
				;
		return mappedError;
	}
}
