package com.ccp.jn.async.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.especifications.db.crud.WhenRecordIsFoundInUnionAll;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.db.utils.CcpEntityIdGenerator;

public class ExecuteUnlock implements WhenRecordIsFoundInUnionAll<List<CcpBulkItem>>{

	private final CcpEntity entityLock; 

	private final CcpEntity entityUnlock; 
	
	

	
	public ExecuteUnlock(CcpEntity entityLock, CcpEntity entityUnlock) {
		this.entityUnlock = entityUnlock;
		this.entityLock = entityLock;
	}

	public List<CcpBulkItem> whenRecordIsFound(CcpJsonRepresentation values, CcpJsonRepresentation recordFound) {
	
		CcpBulkItem unlock = new CcpBulkItem(recordFound, CcpEntityOperationType.create, this.entityUnlock);
		CcpBulkItem lock = new CcpBulkItem(recordFound, CcpEntityOperationType.delete, this.entityLock);
		List<CcpBulkItem> asList = Arrays.asList(unlock, lock);
		return asList;
	}

	public List<CcpBulkItem> whenRecordIsNotFound(CcpJsonRepresentation values) {
		return new ArrayList<>();
	}

	public CcpEntityIdGenerator getEntity() {
		return this.entityLock;
	}

}
