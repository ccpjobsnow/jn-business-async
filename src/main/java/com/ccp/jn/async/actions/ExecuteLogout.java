package com.ccp.jn.async.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.especifications.db.crud.HandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.CcpEntity;
import com.jn.commons.entities.JnEntityLoginSessionCurrent;
import com.jn.commons.entities.JnEntityLoginLogout;

public class ExecuteLogout implements HandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{

	
	private ExecuteLogout() {
		
	}
	
	public static final ExecuteLogout INSTANCE = new ExecuteLogout();
	
	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation values, CcpJsonRepresentation recordFound) {
	
		CcpBulkItem logout = new CcpBulkItem(recordFound, CcpEntityOperationType.create, JnEntityLoginLogout.INSTANCE);
		CcpBulkItem oldLogin = new CcpBulkItem(recordFound, CcpEntityOperationType.delete, JnEntityLoginSessionCurrent.INSTANCE);
		List<CcpBulkItem> asList = Arrays.asList(oldLogin, logout);
		return asList;
	}

	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation values) {
		return new ArrayList<>();
	}

	public CcpEntity getEntityToSearch() {
		return JnEntityLoginSessionCurrent.INSTANCE;
	}

}
