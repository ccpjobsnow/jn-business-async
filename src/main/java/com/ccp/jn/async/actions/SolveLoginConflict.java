package com.ccp.jn.async.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.constantes.CcpStringConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.CcpEntity;
import com.jn.commons.entities.JnEntityLoginSessionCurrent;
import com.jn.commons.entities.JnEntityLoginConflict;

public class SolveLoginConflict implements CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{

	private SolveLoginConflict() {}
	
	public static final SolveLoginConflict INSTANCE = new SolveLoginConflict();
	
	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation json, CcpJsonRepresentation recordFound) {
	
		String email = recordFound.getAsString(CcpStringConstants.EMAIL.value);
		CcpJsonRepresentation newLogin = JnEntityLoginSessionCurrent.ENTITY.getOnlyExistingFields(json);
		CcpJsonRepresentation loginConflict = CcpOtherConstants.EMPTY_JSON.put(CcpStringConstants.EMAIL.value, email).put("newLogin", newLogin).put("oldLogin", recordFound);
		CcpBulkItem itemLoginLoginConflict = JnEntityLoginConflict.ENTITY.toBulkItem(loginConflict, CcpEntityOperationType.create);
		
		List<CcpBulkItem> asList = Arrays.asList(itemLoginLoginConflict);
		return asList;
	}

	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation json) {
		return new ArrayList<>();
	}

	public CcpEntity getEntityToSearch() {
		return JnEntityLoginSessionCurrent.ENTITY;
	}

}
