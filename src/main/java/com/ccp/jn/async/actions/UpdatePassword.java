package com.ccp.jn.async.actions;

import java.util.Arrays;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.especifications.db.crud.HandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.password.CcpPasswordHandler;
import com.jn.commons.entities.JnEntityLoginPassword;

public class UpdatePassword implements HandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{

	private UpdatePassword() {
		
	}
	
	public static final UpdatePassword INSTANCE = new UpdatePassword();
	
	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation values, CcpJsonRepresentation recordFound) {

		List<CcpBulkItem> asList = this.savePassword(values, CcpEntityOperationType.update);
		
		return asList;
	}

	private List<CcpBulkItem> savePassword(CcpJsonRepresentation values, CcpEntityOperationType operation) {
		String password = values.getAsString("password");
		
		CcpPasswordHandler dependency = CcpDependencyInjection.getDependency(CcpPasswordHandler.class);
		
		String passwordHash = dependency.getHash(password);
		
		CcpJsonRepresentation jsonPassword = values.put("password", passwordHash); 
		
		CcpBulkItem itemPassword = new CcpBulkItem(jsonPassword, operation, JnEntityLoginPassword.INSTANCE);
		
		List<CcpBulkItem> asList = Arrays.asList(itemPassword);
		return asList;
	}

	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation values) {

		List<CcpBulkItem> asList = this.savePassword(values, CcpEntityOperationType.create);
		return asList;
	}

	public CcpEntity getEntityToSearch() {
		return JnEntityLoginPassword.INSTANCE;
	}

}
