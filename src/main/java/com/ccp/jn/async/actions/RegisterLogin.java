package com.ccp.jn.async.actions;

import java.util.Arrays;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.especifications.db.crud.WhenRecordIsFoundInUnionAll;
import com.ccp.especifications.db.utils.CcpEntity;
import com.jn.commons.entities.JnEntityLoginSessionCurrent;
import com.jn.commons.entities.JnEntityLoginSessionToken;

public class RegisterLogin implements WhenRecordIsFoundInUnionAll<List<CcpBulkItem>>{

	
	private RegisterLogin() {
		
	}
	
	public static final RegisterLogin INSTANCE = new RegisterLogin();
	
	public List<CcpBulkItem> whenRecordExists(CcpJsonRepresentation values, CcpJsonRepresentation recordFound) {

		List<CcpBulkItem> whenRecordIsNotFound = this.whenRecordDoesNotExist(values);
		return whenRecordIsNotFound;
	}

	public List<CcpBulkItem> whenRecordDoesNotExist(CcpJsonRepresentation values) {
		CcpBulkItem newSession = new CcpBulkItem(values, CcpEntityOperationType.create, JnEntityLoginSessionToken.INSTANCE);
		CcpBulkItem newLogin = new CcpBulkItem(values, CcpEntityOperationType.create, JnEntityLoginSessionCurrent.INSTANCE);
		List<CcpBulkItem> asList = Arrays.asList(newLogin, newSession);
		return asList;
	}

	public CcpEntity getEntity() {
		return JnEntityLoginSessionCurrent.INSTANCE;
	}

}
