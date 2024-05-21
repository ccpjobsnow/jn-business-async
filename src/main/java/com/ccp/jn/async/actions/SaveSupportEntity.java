package com.ccp.jn.async.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.especifications.db.crud.HandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.CcpEntity;

public class SaveSupportEntity implements HandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{

	private final CcpEntity entity;
	
	public SaveSupportEntity(CcpEntity entity) {
		this.entity = entity;
	}
	

	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation searchParameter,	CcpJsonRepresentation recordFound) {
		CcpEntity mirrorEntity = this.entity.getMirrorEntity();
		CcpBulkItem bulkItem = this.entity.toBulkItem(searchParameter, CcpEntityOperationType.update);
		CcpBulkItem deleteFromMainEntity = mirrorEntity.toBulkItem(searchParameter, CcpEntityOperationType.delete);
		List<CcpBulkItem> asList = Arrays.asList(bulkItem, deleteFromMainEntity);
		return asList;
	}

	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation searchParameter) {
		return new ArrayList<>();
	}

	public CcpEntity getEntityToSearch() {
		return this.entity;
	}

}
