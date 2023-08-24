package com.ccp.jn.async.commons.others;

import java.util.List;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpInstanceInjection;
import com.ccp.especifications.db.bulk.CcpDbBulkExecutor;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.db.utils.CcpOperationType;
import com.jn.commons.JnEntity;

public class CommitAndAudit {

	private CcpDbBulkExecutor dbBulkExecutor = CcpInstanceInjection.getInstance(CcpDbBulkExecutor.class);
	
	public void execute(List<CcpMapDecorator> records, CcpOperationType operation, CcpEntity entity) {
		this.dbBulkExecutor.commitAndAuditAndSaveErrors(records, operation, entity, JnEntity.audit, JnEntity.record_to_reprocess);

	}
	
}
