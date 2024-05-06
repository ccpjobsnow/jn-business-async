package com.ccp.jn.async.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.especifications.db.crud.HandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.CcpEntity;

public class ExecuteUnlock implements HandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{

	private final CcpEntity entityLock; 

	private final CcpEntity entityUnlock; 
	
	

	
	public ExecuteUnlock(CcpEntity entityLock, CcpEntity entityUnlock) {
		this.entityUnlock = entityUnlock;
		this.entityLock = entityLock;
	}

	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation values, CcpJsonRepresentation recordFound) {
	
		CcpBulkItem unlock = new CcpBulkItem(recordFound, CcpEntityOperationType.create, this.entityUnlock);
		CcpBulkItem lock = new CcpBulkItem(recordFound, CcpEntityOperationType.delete, this.entityLock);
		List<CcpBulkItem> asList = Arrays.asList(unlock, lock);
		return asList;
	}

	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation values) {
		return new ArrayList<>();
	}

	public CcpEntity getEntityToSearch() {
		return this.entityLock;
	}

}
