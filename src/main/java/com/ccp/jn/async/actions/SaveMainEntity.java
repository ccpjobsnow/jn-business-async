package com.ccp.jn.async.actions;

import java.util.Arrays;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityBulkOperationType;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.CcpEntity;

public class SaveMainEntity implements CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{

	private final CcpEntity mainEntity;
	
	public SaveMainEntity(CcpEntity mainEntity) {
		this.mainEntity = mainEntity;
	}
	

	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation searchParameter,	CcpJsonRepresentation recordFound) {
		CcpBulkItem updateIntoMainEntity = this.mainEntity.toBulkItem(searchParameter, CcpEntityBulkOperationType.update);
		List<CcpBulkItem> asList = Arrays.asList(updateIntoMainEntity);
		return asList;
	}

	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation searchParameter) {
		CcpBulkItem createIntoMainEntity = this.mainEntity.toBulkItem(searchParameter, CcpEntityBulkOperationType.create);
		List<CcpBulkItem> asList = Arrays.asList(createIntoMainEntity);
		return asList;
	}

	public CcpEntity getEntityToSearch() {
		return this.mainEntity;
	}

}
