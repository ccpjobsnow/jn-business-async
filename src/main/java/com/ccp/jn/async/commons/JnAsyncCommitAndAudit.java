package com.ccp.jn.async.commons;

import java.util.ArrayList;
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
		
		List<CcpBulkItem> collect = records.stream().map(json -> new CcpBulkItem(json, operation, entity)).collect(Collectors.toList());
		
		this.executeBulk(collect);
	}
	
	public void executeBulk(CcpJsonRepresentation json, CcpEntity entity, CcpEntityOperationType operation) {
		CcpEntity mirrorEntity = entity.getTwinEntity();
		CcpBulkItem bulkItem = entity.toBulkItem(json, operation);
		CcpBulkItem bulkItem2 = mirrorEntity.toBulkItem(json, operation);
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
			
			boolean canNotSaveCopy = item.entity.isCopyableEntity() == false;
			if(canNotSaveCopy) {
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
	
	private void commitAndSaveErrors(CcpDbBulkExecutor dbBulkExecutor) {

		Function<CcpBulkOperationResult, CcpJsonRepresentation> reprocessJsonMapper = this.getReprocessJsonMapper();
		List<CcpBulkOperationResult> bulkOperationResult2 = dbBulkExecutor.getBulkOperationResult();
		List<CcpBulkOperationResult> bulkOperationResult = bulkOperationResult2.stream().filter(x -> x.hasError()).collect(Collectors.toList());
		List<CcpBulkItem> collect = bulkOperationResult.stream().map(x -> x.getReprocess(reprocessJsonMapper, JnEntityRecordToReprocess.ENTITY)).collect(Collectors.toList());
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
			CcpJsonRepresentation renameKey = putAll2.renameField("type", "errorType");
			CcpJsonRepresentation jsonPiece = renameKey.getJsonPiece("errorType", "reason");
			return jsonPiece;
		};
	}
	
	public CcpSelectUnionAll changeStatus(CcpJsonRepresentation json, CcpEntity entity) {
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		CcpEntity mirrorEntity = entity.getTwinEntity();
		CcpEntity[] array = new CcpEntity[] {entity, mirrorEntity};
		CcpSelectUnionAll unionAll = crud.unionAll(json, array);
		CcpBulkItem mirror = new CcpBulkItem(json, unionAll, mirrorEntity);
		CcpBulkItem main = new CcpBulkItem(json, unionAll, entity);
		
		this.executeBulk(main, mirror);
		return unionAll;
	}
	
	
	
	@SuppressWarnings("unchecked")
	public CcpSelectUnionAll executeSelectUnionAllThenExecuteBulkOperation(CcpJsonRepresentation json,  HandleWithSearchResultsInTheEntity<List<CcpBulkItem>> ... handlers) {
		Set<CcpEntity> collect = Arrays.asList(handlers).stream().map(x -> x.getEntityToSearch()).collect(Collectors.toSet());
		CcpEntity[] array = collect.toArray(new CcpEntity[collect.size()]);
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		CcpSelectUnionAll unionAll = crud.unionAll(json, array);
		
		Set<CcpBulkItem> all = new HashSet<>();
		
		for (HandleWithSearchResultsInTheEntity<List<CcpBulkItem>> handler : handlers) {
			List<CcpBulkItem> list =  unionAll.handleRecordInUnionAll(json, handler);
			all.addAll(list);
		}
		this.executeBulk(all);

		CcpJsonRepresentation data = json;
	
		for (HandleWithSearchResultsInTheEntity<List<CcpBulkItem>> handler : handlers) {
			
			CcpEntity entityToSearch = handler.getEntityToSearch();
			
			boolean presentInThisUnionAll = entityToSearch.isPresentInThisUnionAll(unionAll, data);
			
			if(presentInThisUnionAll) {
				Function<CcpJsonRepresentation, CcpJsonRepresentation> doAfterSavingIfRecordIsFound = handler.doAfterSavingIfRecordIsFound();
				data = doAfterSavingIfRecordIsFound.apply(data);
				continue;
			}
			Function<CcpJsonRepresentation, CcpJsonRepresentation> doAfterSavingIfRecordIsNotFound = handler.doAfterSavingIfRecordIsNotFound();
			data = doAfterSavingIfRecordIsNotFound.apply(data);
		}
		
		return unionAll;
	}

	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation executeSelectUnionAllThenSaveInTheMainAndMirrorEntities(CcpJsonRepresentation json, 
			CcpEntity mainEntity, Function<CcpJsonRepresentation, CcpJsonRepresentation> whenPresentInMainEntityOrIsNewRecord) {
		
		CcpEntity supportEntity = mainEntity.getTwinEntity();
		
		SaveMainEntity saveMainEntity = new SaveMainEntity(mainEntity);
		
		SaveSupportEntity saveSupportEntity = new SaveSupportEntity(supportEntity);
		
		HandleWithSearchResultsInTheEntity<List<CcpBulkItem>>[] array = new HandleWithSearchResultsInTheEntity[]{
				saveMainEntity,
				saveSupportEntity
		};
		
		CcpSelectUnionAll result = this.executeSelectUnionAllThenExecuteBulkOperation(json, array);
		
		boolean isPresentInMainEntity = mainEntity.isPresentInThisUnionAll(result, json);
		
		if(isPresentInMainEntity) {
			CcpJsonRepresentation apply = whenPresentInMainEntityOrIsNewRecord.apply(json);
			return apply;
		}
		
		boolean isNewRecord = false == supportEntity.isPresentInThisUnionAll(result, json);

		if(isNewRecord) {
			CcpJsonRepresentation apply = whenPresentInMainEntityOrIsNewRecord.apply(json);
			return apply;
		}
			
		return json;
	}

	
	public void executeBulk(CcpJsonRepresentation json, CcpEntityOperationType operation, CcpEntity...entities) {
		
		List<CcpBulkItem> items = new ArrayList<>();
 		
		for (CcpEntity entity : entities) {
			CcpBulkItem bulkItem = entity.toBulkItem(json, operation);
			items.add(bulkItem);
		}
		
		this.executeBulk(items);
	}
}
