package com.ccp.jn.async.commons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpDbBulkExecutor;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.especifications.db.crud.CcpCrud;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.crud.WhenRecordIsFoundInUnionAll;
import com.ccp.especifications.db.utils.CcpEntity;
import com.jn.commons.entities.JnEntityAudit;
import com.jn.commons.entities.JnEntityRecordToReprocess;

public class JnAsyncCommitAndAudit {

	public static final JnAsyncCommitAndAudit INSTANCE = new JnAsyncCommitAndAudit();
	
	private JnAsyncCommitAndAudit() {
		
	}
	
	public void execute(List<CcpJsonRepresentation> records, CcpEntityOperationType operation, CcpEntity entity) {
		
		boolean emptyRecords = records.isEmpty();
		if(emptyRecords) {
			return;
		}
		
		CcpDbBulkExecutor dbBulkExecutor = CcpDependencyInjection.getDependency(CcpDbBulkExecutor.class);
		
		dbBulkExecutor = dbBulkExecutor.addRecords(records, operation, entity);

		dbBulkExecutor.commitAndAuditLogingErrors(JnEntityRecordToReprocess.INSTANCE, JnEntityAudit.INSTANCE, CcpConstants.DO_BY_PASS , CcpConstants.DO_BY_PASS);
	}

	
	public void execute(List<CcpBulkItem> items) {
		
		boolean emptyItems = items.isEmpty();
		if(emptyItems) {
			return;
		}

		CcpDbBulkExecutor dbBulkExecutor = CcpDependencyInjection.getDependency(CcpDbBulkExecutor.class);

		for (CcpBulkItem item : items) {
			dbBulkExecutor = dbBulkExecutor.addRecord(item);
		}
		dbBulkExecutor.commitAndAuditLogingErrors(JnEntityRecordToReprocess.INSTANCE, JnEntityAudit.INSTANCE, CcpConstants.DO_BY_PASS , CcpConstants.DO_BY_PASS);
	}
	
	@SuppressWarnings("unchecked")
	public void executeSelectUnionAllThenExecuteBulkOperation(CcpJsonRepresentation values,  WhenRecordIsFoundInUnionAll<List<CcpBulkItem>> ... handlers) {
		Set<CcpEntity> collect = Arrays.asList(handlers).stream().map(x -> x.getEntity()).collect(Collectors.toSet());
		CcpEntity[] array = collect.toArray(new CcpEntity[collect.size()]);
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		CcpSelectUnionAll unionAll = crud.unionAll(values, array);
		
		List<CcpBulkItem> all = new ArrayList<CcpBulkItem>();
		
		for (WhenRecordIsFoundInUnionAll<List<CcpBulkItem>> handler : handlers) {
			List<CcpBulkItem> list =  unionAll.whenRecordIsFoundInUnionAll(values, handler);
			all.addAll(list);
		}
		this.execute(all);
	}
	
}
