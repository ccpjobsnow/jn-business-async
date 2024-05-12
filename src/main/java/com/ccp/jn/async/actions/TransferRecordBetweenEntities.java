package com.ccp.jn.async.actions;

import java.util.Arrays;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.especifications.db.crud.HandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.CcpEntity;

public class TransferRecordBetweenEntities implements HandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{

	private final CcpEntity from; 

	
	public TransferRecordBetweenEntities(CcpEntity from) {
		this.from = from;
	}

	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation values, CcpJsonRepresentation recordFound) {
	
		CcpEntity mirrorEntity = this.from.getMirrorEntity();
		CcpBulkItem itemTo = new CcpBulkItem(recordFound, CcpEntityOperationType.create, mirrorEntity);
		CcpBulkItem itemFrom = new CcpBulkItem(recordFound, CcpEntityOperationType.delete, this.from);
		List<CcpBulkItem> asList = Arrays.asList(itemTo, itemFrom);
		return asList;
	}

	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation values) {
		CcpEntity mirrorEntity = this.from.getMirrorEntity();
		CcpBulkItem itemTo = new CcpBulkItem(values, CcpEntityOperationType.create, mirrorEntity);
		List<CcpBulkItem> asList = Arrays.asList(itemTo);
		return asList;
	}

	public CcpEntity getEntityToSearch() {
		return this.from;
	}

}
