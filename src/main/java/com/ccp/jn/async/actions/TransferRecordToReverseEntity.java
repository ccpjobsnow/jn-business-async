package com.ccp.jn.async.actions;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.especifications.db.crud.HandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.CcpEntity;

public class TransferRecordToReverseEntity implements HandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{

	private final CcpEntity from; 
	private final Function<CcpJsonRepresentation, CcpJsonRepresentation> doAfterSavingIfRecordIsFound;
	private final Function<CcpJsonRepresentation, CcpJsonRepresentation> doAfterSavingIfRecordIsNotFound;
	

	
	public TransferRecordToReverseEntity(CcpEntity from,
			Function<CcpJsonRepresentation, CcpJsonRepresentation> doAfterSavingIfRecordIsFound,
			Function<CcpJsonRepresentation, CcpJsonRepresentation> doAfterSavingIfRecordIsNotFound) {

		this.doAfterSavingIfRecordIsNotFound = doAfterSavingIfRecordIsNotFound;
		this.doAfterSavingIfRecordIsFound = doAfterSavingIfRecordIsFound;
		this.from = from;
	}

	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation json, CcpJsonRepresentation recordFound) {
	
		CcpEntity mirrorEntity = this.from.getMirrorEntity();
		CcpBulkItem itemTo = new CcpBulkItem(json, CcpEntityOperationType.create, mirrorEntity);
		CcpBulkItem itemFrom = new CcpBulkItem(json, CcpEntityOperationType.delete, this.from);
		List<CcpBulkItem> asList = Arrays.asList(itemTo, itemFrom);
		
		return asList;
	}

	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation json) {
		CcpEntity mirrorEntity = this.from.getMirrorEntity();
		CcpBulkItem itemTo = new CcpBulkItem(json, CcpEntityOperationType.create, mirrorEntity);
		List<CcpBulkItem> asList = Arrays.asList(itemTo);
		return asList;
	}

	public CcpEntity getEntityToSearch() {
		return this.from;
	}

	public Function<CcpJsonRepresentation, CcpJsonRepresentation> doAfterSavingIfRecordIsFound() {
		return this.doAfterSavingIfRecordIsFound;
	}

	public Function<CcpJsonRepresentation, CcpJsonRepresentation> doAfterSavingIfRecordIsNotFound() {
		return this.doAfterSavingIfRecordIsNotFound;
	}
}
