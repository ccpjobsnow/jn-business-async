package com.ccp.jn.async.actions;

import java.util.Arrays;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityBulkOperationType;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.CcpEntity;
import com.jn.commons.entities.JnEntityLoginPassword;

public class UpdatePassword implements CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{

	private UpdatePassword() {
		
	}
	
	public static final UpdatePassword INSTANCE = new UpdatePassword();
	
	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation json, CcpJsonRepresentation recordFound) {

		List<CcpBulkItem> asList = this.savePassword(json, CcpEntityBulkOperationType.update);
		
		return asList;
	}

	private List<CcpBulkItem> savePassword(CcpJsonRepresentation json, CcpEntityBulkOperationType operation) {

		CcpBulkItem itemPassword =  JnEntityLoginPassword.ENTITY.toBulkItem(json, operation);
		
		List<CcpBulkItem> asList = Arrays.asList(itemPassword);
		
		return asList;
	}

	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation json) {

		List<CcpBulkItem> asList = this.savePassword(json, CcpEntityBulkOperationType.create);
		return asList;
	}

	public CcpEntity getEntityToSearch() {
		return JnEntityLoginPassword.ENTITY;
	}

}
