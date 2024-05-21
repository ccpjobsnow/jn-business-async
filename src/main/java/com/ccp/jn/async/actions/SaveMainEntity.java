package com.ccp.jn.async.actions;

import java.util.Arrays;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.especifications.db.crud.HandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.CcpEntity;

public class SaveMainEntity implements HandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{

	private final CcpEntity entity;
	
	public SaveMainEntity(CcpEntity entity) {
		this.entity = entity;
	}
	

	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation searchParameter,	CcpJsonRepresentation recordFound) {
		CcpBulkItem bulkItem = this.entity.toBulkItem(searchParameter, CcpEntityOperationType.update);
		List<CcpBulkItem> asList = Arrays.asList(bulkItem);
		return asList;
	}

	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation searchParameter) {
		CcpBulkItem bulkItem = this.entity.toBulkItem(searchParameter, CcpEntityOperationType.create);
		List<CcpBulkItem> asList = Arrays.asList(bulkItem);
		return asList;
	}

	public CcpEntity getEntityToSearch() {
		return this.entity;
	}

}
