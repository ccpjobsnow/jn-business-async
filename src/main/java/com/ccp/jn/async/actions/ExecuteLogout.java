package com.ccp.jn.async.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.especifications.db.crud.WhenRecordIsFoundInUnionAll;
import com.ccp.especifications.db.utils.CcpEntity;
import com.jn.commons.entities.JnEntityLogin;
import com.jn.commons.entities.JnEntityLoginLogout;

public class ExecuteLogout implements WhenRecordIsFoundInUnionAll<List<CcpBulkItem>>{

	
	private ExecuteLogout() {
		
	}
	
	public static final ExecuteLogout INSTANCE = new ExecuteLogout();
	
	public List<CcpBulkItem> whenRecordExists(CcpJsonRepresentation values, CcpJsonRepresentation recordFound) {
	
		CcpBulkItem logout = new CcpBulkItem(recordFound, CcpEntityOperationType.create, JnEntityLoginLogout.INSTANCE);
		CcpBulkItem oldLogin = new CcpBulkItem(recordFound, CcpEntityOperationType.delete, JnEntityLogin.INSTANCE);
		List<CcpBulkItem> asList = Arrays.asList(oldLogin, logout);
		return asList;
	}

	public List<CcpBulkItem> whenRecordDoesNotExist(CcpJsonRepresentation values) {
		return new ArrayList<>();
	}

	public CcpEntity getEntity() {
		return JnEntityLogin.INSTANCE;
	}

}
