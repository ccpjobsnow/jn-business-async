package com.ccp.jn.async.commons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpBulkOperationResult;
import com.ccp.especifications.db.bulk.CcpDbBulkExecutor;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.especifications.db.crud.CcpCrud;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.crud.HandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.exceptions.db.CcpEntityRecordNotFound;
import com.jn.commons.entities.JnEntityRecordToReprocess;
import com.jn.commons.entities.base.JnIncopiableEntity;

public class JnAsyncCommitAndAudit {

	public static final JnAsyncCommitAndAudit INSTANCE = new JnAsyncCommitAndAudit();
	
	private JnAsyncCommitAndAudit() {}
	
	public void execute1(List<CcpJsonRepresentation> records, CcpEntityOperationType operation, CcpEntity entity) {
		
		boolean emptyRecords = records.isEmpty();
		
		if(emptyRecords) {
			return;
		}
		
		CcpDbBulkExecutor dbBulkExecutor = CcpDependencyInjection.getDependency(CcpDbBulkExecutor.class);
		
		dbBulkExecutor = dbBulkExecutor.addRecords(records, operation, entity);
		
		this.commitAndSaveErrors(dbBulkExecutor);
	}
	
	public void execute1(List<CcpBulkItem> items) {
		
		boolean emptyItems = items.isEmpty();
		if(emptyItems) {
			return;
		}

		CcpDbBulkExecutor dbBulkExecutor = CcpDependencyInjection.getDependency(CcpDbBulkExecutor.class);

		for (CcpBulkItem item : items) {
			
			dbBulkExecutor = dbBulkExecutor.addRecord(item);
			
			boolean incopiableEntity = this.isIncopiableEntity(item);
			
			if(incopiableEntity) {
				continue;
			}
			
			try {
				CcpBulkItem recordToBulkOperation = item.getSecondRecordToBulkOperation();
				dbBulkExecutor = dbBulkExecutor.addRecord(recordToBulkOperation);
			} catch (CcpEntityRecordNotFound | UnsupportedOperationException e) {

			}
		}

		this.commitAndSaveErrors(dbBulkExecutor);
	}
	
	private boolean isIncopiableEntity(CcpBulkItem bulkItem) {
		Class<? extends CcpEntity> clazz = bulkItem.entity.getClass();
		boolean annotationPresent = clazz.isAnnotationPresent(JnIncopiableEntity.class);
		return annotationPresent;
		
	}
	
	
	private void commitAndSaveErrors(CcpDbBulkExecutor dbBulkExecutor) {

		List<CcpBulkOperationResult> bulkOperationResult = dbBulkExecutor.getBulkOperationResult();
		Function<CcpBulkOperationResult, CcpJsonRepresentation> reprocessJsonMapper = this.getReprocessJsonMapper();
		List<CcpBulkItem> collect = bulkOperationResult.stream().map(x -> x.getReprocess(reprocessJsonMapper, JnEntityRecordToReprocess.INSTANCE)).collect(Collectors.toList());
		this.execute1(collect);
		
	}
	private Function<CcpBulkOperationResult, CcpJsonRepresentation> getReprocessJsonMapper() {
		return result -> {
			long currentTimeMillis = System.currentTimeMillis();
			CcpJsonRepresentation put = CcpConstants.EMPTY_JSON
					.put("timestamp", currentTimeMillis);
			CcpBulkItem bulkItem = result.getBulkItem();
			CcpJsonRepresentation putAll = put.putAll(bulkItem.json);
			CcpJsonRepresentation errorDetails = result.getErrorDetails();
			CcpJsonRepresentation putAll2 = putAll.putAll(errorDetails);
			CcpJsonRepresentation renameKey = putAll2.renameKey("type", "errorType");
			CcpJsonRepresentation jsonPiece = renameKey.getJsonPiece("errorType", "reason");
			return jsonPiece;
		};
	}
	@SuppressWarnings("unchecked")
	public void executeSelectUnionAllThenExecuteBulkOperation(CcpJsonRepresentation values,  HandleWithSearchResultsInTheEntity<List<CcpBulkItem>> ... handlers) {
		Set<CcpEntity> collect = Arrays.asList(handlers).stream().map(x -> x.getEntityToSearch()).collect(Collectors.toSet());
		CcpEntity[] array = collect.toArray(new CcpEntity[collect.size()]);
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		CcpSelectUnionAll unionAll = crud.unionAll(values, array);
		
		List<CcpBulkItem> all = new ArrayList<CcpBulkItem>();
		
		for (HandleWithSearchResultsInTheEntity<List<CcpBulkItem>> handler : handlers) {
			List<CcpBulkItem> list =  unionAll.whenRecordIsFoundInUnionAll(values, handler);
			all.addAll(list);
		}
		this.execute1(all);
	}

}
