package com.ccp.jn.async.business;

import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.bulk.CcpDbBulkExecutor;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.especifications.db.utils.CcpEntity;
import com.jn.commons.entities.JnEntityAudit;
import com.jn.commons.entities.JnEntityRecordToReprocess;

public class JnAsyncBusinessCommitAndAudit {

	public void execute(List<CcpJsonRepresentation> records, CcpEntityOperationType operation, CcpEntity entity) {
		CcpDbBulkExecutor dbBulkExecutor = CcpDependencyInjection.getDependency(CcpDbBulkExecutor.class);
		
		dbBulkExecutor = dbBulkExecutor.addRecords(records, operation, entity);

		JnEntityAudit auditEntity = new JnEntityAudit();
		JnEntityRecordToReprocess errorEntity = new JnEntityRecordToReprocess();

		dbBulkExecutor.commitAndAuditLogingErrors(errorEntity, auditEntity);
	}
	
}
