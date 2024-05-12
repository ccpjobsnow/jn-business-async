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

	private final CcpEntity to; 
	
	public TransferRecordBetweenEntities(CcpEntity from, CcpEntity to) {
		this.from = from;
		this.to = to;
	}

	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation values, CcpJsonRepresentation recordFound) {
	
		CcpBulkItem itemTo = new CcpBulkItem(recordFound, CcpEntityOperationType.create, this.to);
		CcpBulkItem itemFrom = new CcpBulkItem(recordFound, CcpEntityOperationType.delete, this.from);
		List<CcpBulkItem> asList = Arrays.asList(itemTo, itemFrom);
		return asList;
	}

	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation values) {
		CcpBulkItem itemTo = new CcpBulkItem(values, CcpEntityOperationType.create, this.to);
		List<CcpBulkItem> asList = Arrays.asList(itemTo);
		return asList;
	}

	public CcpEntity getEntityToSearch() {
		return this.from;
	}

}
