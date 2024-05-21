package com.ccp.jn.async.actions;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.especifications.db.crud.HandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.CcpEntity;

public class TransferRecordToReverseEntity implements HandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{

	private final Function<CcpJsonRepresentation, CcpJsonRepresentation> callBackWhenRecordIsFound;
	private final CcpEntity from; 
	
	public TransferRecordToReverseEntity(CcpEntity from) {
		this(from, CcpConstants.DO_BY_PASS);
	}
	
	public TransferRecordToReverseEntity(CcpEntity from, Function<CcpJsonRepresentation, CcpJsonRepresentation> callBackWhenRecordIsFound) {
		this.callBackWhenRecordIsFound = callBackWhenRecordIsFound;
		this.from = from;
		
	}

	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation values, CcpJsonRepresentation recordFound) {
	
		CcpJsonRepresentation apply = this.callBackWhenRecordIsFound.apply(recordFound);
		CcpEntity mirrorEntity = this.from.getMirrorEntity();
		CcpBulkItem itemTo = new CcpBulkItem(apply, CcpEntityOperationType.create, mirrorEntity);
		CcpBulkItem itemFrom = new CcpBulkItem(apply, CcpEntityOperationType.delete, this.from);
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
