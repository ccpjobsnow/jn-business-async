package com.ccp.jn.async.business;

import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.bulk.CcpDbBulkExecutor;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.db.utils.CcpEntityOperationType;
import com.jn.commons.entities.JnEntityAudit;
import com.jn.commons.entities.JnEntityRecordToReprocess;

public class JnAsyncBusinessCommitAndAudit {

	private CcpDbBulkExecutor dbBulkExecutor = CcpDependencyInjection.getDependency(CcpDbBulkExecutor.class);
	
	public void execute(List<CcpJsonRepresentation> records, CcpEntityOperationType operation, CcpEntity entity) {
		this.dbBulkExecutor.commitAndAuditAndSaveErrors(records, operation, entity, new JnEntityAudit(), new JnEntityRecordToReprocess());

	}
	
}
