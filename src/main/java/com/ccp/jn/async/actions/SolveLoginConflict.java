package com.ccp.jn.async.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.especifications.db.crud.WhenRecordIsFoundInUnionAll;
import com.ccp.especifications.db.utils.CcpEntityIdGenerator;
import com.jn.commons.entities.JnEntityLogin;
import com.jn.commons.entities.JnEntityLoginConflict;

public class SolveLoginConflict implements WhenRecordIsFoundInUnionAll<List<CcpBulkItem>>{

	private SolveLoginConflict() {
		
	}
	
	public static final SolveLoginConflict INSTANCE = new SolveLoginConflict();
	
	public List<CcpBulkItem> whenRecordIsFound(CcpJsonRepresentation values, CcpJsonRepresentation recordFound) {
	
		String email = recordFound.getAsString("email");
		CcpJsonRepresentation newLogin = JnEntityLogin.INSTANCE.getOnlyExistingFields(values);
		CcpJsonRepresentation loginConflict = CcpConstants.EMPTY_JSON.put("email", email).put("newLogin", newLogin).put("oldLogin", recordFound);
		CcpBulkItem itemLoginLoginConflict = new CcpBulkItem(loginConflict, CcpEntityOperationType.create, JnEntityLoginConflict.INSTANCE);
		
		List<CcpBulkItem> asList = Arrays.asList(itemLoginLoginConflict);
		return asList;
	}

	public List<CcpBulkItem> whenRecordIsNotFound(CcpJsonRepresentation values) {
		return new ArrayList<>();
	}

	public CcpEntityIdGenerator getEntity() {
		return JnEntityLogin.INSTANCE;
	}

}
