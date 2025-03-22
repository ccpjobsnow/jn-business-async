package com.ccp.jn.async.actions;

import java.util.Arrays;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityBulkOperationType;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.CcpEntity;
import com.jn.commons.entities.JnEntityLoginSessionConflict;
import com.jn.commons.entities.JnEntityLoginSessionValidation;

public class RegisterLogin implements CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{

	
	private RegisterLogin() {}
	
	public static final RegisterLogin INSTANCE = new RegisterLogin();
	
	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation json, CcpJsonRepresentation recordFound) {

		CcpBulkItem newSession = JnEntityLoginSessionConflict.ENTITY.toBulkItem(json, CcpEntityBulkOperationType.create);
		CcpBulkItem newLogin = JnEntityLoginSessionValidation.ENTITY.toBulkItem(json, CcpEntityBulkOperationType.create);
		List<CcpBulkItem> asList = Arrays.asList(newLogin, newSession);
		return asList;
	}

	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation json) {
		CcpBulkItem newLoginSessionConflict = JnEntityLoginSessionConflict.ENTITY.toBulkItem(json, CcpEntityBulkOperationType.create);
		CcpBulkItem newLoginSessionValidation = JnEntityLoginSessionValidation.ENTITY.toBulkItem(json, CcpEntityBulkOperationType.create);
		List<CcpBulkItem> asList = Arrays.asList(newLoginSessionValidation, newLoginSessionConflict);
		return asList;
	}

	public CcpEntity getEntityToSearch() {
		return JnEntityLoginSessionConflict.ENTITY;
	}

}
