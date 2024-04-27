package com.ccp.jn.async.commons;

import java.util.List;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpDbBulkExecutor;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.especifications.db.utils.CcpEntity;
import com.jn.commons.entities.JnEntityAudit;
import com.jn.commons.entities.JnEntityRecordToReprocess;

public class JnAsyncBusinessCommitAndAudit {

	public static final JnAsyncBusinessCommitAndAudit INSTANCE = new JnAsyncBusinessCommitAndAudit();
	
	private JnAsyncBusinessCommitAndAudit() {
		
	}
	
	public void execute(List<CcpJsonRepresentation> records, CcpEntityOperationType operation, CcpEntity entity) {
		CcpDbBulkExecutor dbBulkExecutor = CcpDependencyInjection.getDependency(CcpDbBulkExecutor.class);
		
		dbBulkExecutor = dbBulkExecutor.addRecords(records, operation, entity);

		dbBulkExecutor.commitAndAuditLogingErrors(JnEntityRecordToReprocess.INSTANCE, JnEntityAudit.INSTANCE, CcpConstants.DO_BY_PASS , CcpConstants.DO_BY_PASS);
	}

	
	public void execute(List<CcpBulkItem> items) {
		
		CcpDbBulkExecutor dbBulkExecutor = CcpDependencyInjection.getDependency(CcpDbBulkExecutor.class);

		for (CcpBulkItem item : items) {
			dbBulkExecutor = dbBulkExecutor.addRecord(item);
		}
		dbBulkExecutor.commitAndAuditLogingErrors(JnEntityRecordToReprocess.INSTANCE, JnEntityAudit.INSTANCE, CcpConstants.DO_BY_PASS , CcpConstants.DO_BY_PASS);
	}
	
}
