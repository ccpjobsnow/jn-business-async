package com.ccp.jn.async.actions;

import java.util.Arrays;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.CcpEntity;
import com.jn.commons.entities.JnEntityLoginSessionCurrent;
import com.jn.commons.entities.JnEntityLoginSessionToken;

public class RegisterLogin implements CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{

	
	private RegisterLogin() {}
	
	public static final RegisterLogin INSTANCE = new RegisterLogin();
	
	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation json, CcpJsonRepresentation recordFound) {

		List<CcpBulkItem> whenRecordIsNotFound = this.whenRecordWasNotFoundInTheEntitySearch(json);
		return whenRecordIsNotFound;
	}

	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation json) {
		CcpBulkItem newSession = new CcpBulkItem(json, CcpEntityOperationType.create, JnEntityLoginSessionToken.ENTITY);
		CcpBulkItem newLogin = new CcpBulkItem(json, CcpEntityOperationType.create, JnEntityLoginSessionCurrent.ENTITY);
		List<CcpBulkItem> asList = Arrays.asList(newLogin, newSession);
		return asList;
	}

	public CcpEntity getEntityToSearch() {
		return JnEntityLoginSessionCurrent.ENTITY;
	}

}
