package com.ccp.jn.async.commons;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
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
import com.ccp.jn.async.actions.SaveMainEntity;
import com.ccp.jn.async.actions.SaveSupportEntity;
import com.jn.commons.entities.JnEntityRecordToReprocess;

public class JnAsyncCommitAndAudit {

	public static final JnAsyncCommitAndAudit INSTANCE = new JnAsyncCommitAndAudit();
	
	private JnAsyncCommitAndAudit() {}
	
	public void executeBulk(List<CcpJsonRepresentation> records, CcpEntityOperationType operation, CcpEntity entity) {
		
		boolean emptyRecords = records.isEmpty();
		
		if(emptyRecords) {
			return;
		}
		
		CcpDbBulkExecutor dbBulkExecutor = CcpDependencyInjection.getDependency(CcpDbBulkExecutor.class);
		
		dbBulkExecutor = dbBulkExecutor.addRecords(records, operation, entity);
		
		this.commitAndSaveErrors(dbBulkExecutor);
	}
	
	public void executeBulk(CcpJsonRepresentation values, CcpEntity entity, CcpEntityOperationType operation) {
		CcpBulkItem bulkItem = entity.toBulkItem(values, operation);
		CcpEntity mirrorEntity = entity.getMirrorEntity();
		CcpBulkItem bulkItem2 = mirrorEntity.toBulkItem(values, operation);
		this.executeBulk(bulkItem, bulkItem2);
	}
	
	public void executeBulk(CcpBulkItem... items) {
		List<CcpBulkItem> asList = Arrays.asList(items);
		this.executeBulk(asList);
	}
	
	public void executeBulk(Collection<CcpBulkItem> items) {
		
		boolean emptyItems = items.isEmpty();
		
		if(emptyItems) {
			return;
		}

		CcpDbBulkExecutor dbBulkExecutor = CcpDependencyInjection.getDependency(CcpDbBulkExecutor.class);

		for (CcpBulkItem item : items) {
			
			dbBulkExecutor = dbBulkExecutor.addRecord(item);
			
			boolean canNotSaveCopy = item.entity.canSaveCopy() == false;
			if(canNotSaveCopy) {
				continue;
			}
			try {
				CcpBulkItem recordToBulkOperation = item.getSecondRecordToBulkOperation();
				dbBulkExecutor = dbBulkExecutor.addRecord(recordToBulkOperation);
			} catch (CcpEntityRecordNotFound e) {

			}
		}

		this.commitAndSaveErrors(dbBulkExecutor);
	}
	
	private void commitAndSaveErrors(CcpDbBulkExecutor dbBulkExecutor) {

		List<CcpBulkOperationResult> bulkOperationResult = dbBulkExecutor.getBulkOperationResult();
		Function<CcpBulkOperationResult, CcpJsonRepresentation> reprocessJsonMapper = this.getReprocessJsonMapper();
		List<CcpBulkItem> collect = bulkOperationResult.stream().map(x -> x.getReprocess(reprocessJsonMapper, JnEntityRecordToReprocess.INSTANCE)).collect(Collectors.toList());
		this.executeBulk(collect);
		
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
		
		Set<CcpBulkItem> all = new HashSet<>();
		
		for (HandleWithSearchResultsInTheEntity<List<CcpBulkItem>> handler : handlers) {
			List<CcpBulkItem> list =  unionAll.whenRecordIsFoundInUnionAll(values, handler);
			all.addAll(list);
		}
		this.executeBulk(all);
	}

	@SuppressWarnings("unchecked")
	public void executeSelectUnionAllThenSaveInTheMainAndMirrorEntities(CcpJsonRepresentation values, CcpEntity mainEntity) {
		CcpEntity supportEntity = mainEntity.getMirrorEntity();
		SaveMainEntity saveMainEntity = new SaveMainEntity(mainEntity);
		SaveSupportEntity saveSupportEntity = new SaveSupportEntity(supportEntity);
		HandleWithSearchResultsInTheEntity<List<CcpBulkItem>>[] array = new HandleWithSearchResultsInTheEntity[]{
				saveMainEntity,
				saveSupportEntity
		};
		this.executeSelectUnionAllThenExecuteBulkOperation(values,array);
	}

}
