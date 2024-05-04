package com.ccp.jn.async.actions;

import java.util.Arrays;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.especifications.db.crud.WhenRecordIsFoundInUnionAll;
import com.ccp.especifications.db.utils.CcpEntityIdGenerator;
import com.jn.commons.entities.JnEntityLogin;

public class RegisterLogin implements WhenRecordIsFoundInUnionAll<List<CcpBulkItem>>{

	
	private RegisterLogin() {
		
	}
	
	public static final RegisterLogin INSTANCE = new RegisterLogin();
	
	public List<CcpBulkItem> whenRecordIsFound(CcpJsonRepresentation values, CcpJsonRepresentation recordFound) {

		List<CcpBulkItem> whenRecordIsNotFound = this.whenRecordIsNotFound(values);
		return whenRecordIsNotFound;
	}

	public List<CcpBulkItem> whenRecordIsNotFound(CcpJsonRepresentation values) {
		CcpBulkItem itemNewLogin = new CcpBulkItem(values, CcpEntityOperationType.create, JnEntityLogin.INSTANCE);
		List<CcpBulkItem> asList = Arrays.asList(itemNewLogin);
		return asList;
	}

	public CcpEntityIdGenerator getEntity() {
		return JnEntityLogin.INSTANCE;
	}

}
