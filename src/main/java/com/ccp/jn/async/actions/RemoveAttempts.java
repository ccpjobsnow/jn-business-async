package com.ccp.jn.async.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.especifications.db.crud.WhenRecordIsFoundInUnionAll;
import com.ccp.especifications.db.utils.CcpEntity;

public class RemoveAttempts implements WhenRecordIsFoundInUnionAll<List<CcpBulkItem>>{
	
	private final CcpEntity entityAttempts;

	
	
	public RemoveAttempts(CcpEntity entityAttempts) {
		this.entityAttempts = entityAttempts;
	}

	public List<CcpBulkItem> whenRecordExists(CcpJsonRepresentation values, CcpJsonRepresentation recordFound) {
	
		CcpBulkItem attempts = new CcpBulkItem(recordFound, CcpEntityOperationType.delete, this.entityAttempts);
		List<CcpBulkItem> asList = Arrays.asList(attempts);
		return asList;
	}

	public List<CcpBulkItem> whenRecordDoesNotExist(CcpJsonRepresentation values) {
		return new ArrayList<>();
	}

	public CcpEntity getEntity() {
		return this.entityAttempts;
	}

}
