package com.ccp.jn.async.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.especifications.db.crud.HandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.CcpEntity;
import com.jn.commons.entities.JnEntityLoginSessionCurrent;
import com.jn.commons.entities.JnEntityLoginConflict;

public class SolveLoginConflict implements HandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{

	private SolveLoginConflict() {
		
	}
	
	public static final SolveLoginConflict INSTANCE = new SolveLoginConflict();
	
	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation values, CcpJsonRepresentation recordFound) {
	
		String email = recordFound.getAsString("email");
		CcpJsonRepresentation newLogin = JnEntityLoginSessionCurrent.INSTANCE.getOnlyExistingFields(values);
		CcpJsonRepresentation loginConflict = CcpConstants.EMPTY_JSON.put("email", email).put("newLogin", newLogin).put("oldLogin", recordFound);
		CcpBulkItem itemLoginLoginConflict = new CcpBulkItem(loginConflict, CcpEntityOperationType.create, JnEntityLoginConflict.INSTANCE);
		
		List<CcpBulkItem> asList = Arrays.asList(itemLoginLoginConflict);
		return asList;
	}

	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation values) {
		return new ArrayList<>();
	}

	public CcpEntity getEntityToSearch() {
		return JnEntityLoginSessionCurrent.INSTANCE;
	}

}
