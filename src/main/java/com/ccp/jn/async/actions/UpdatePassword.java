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
	
	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation json, CcpJsonRepresentation recordFound) {

		List<CcpBulkItem> asList = this.savePassword(json, CcpEntityOperationType.update);
		
		return asList;
	}

	private List<CcpBulkItem> savePassword(CcpJsonRepresentation json, CcpEntityOperationType operation) {
		String password = json.getAsString("password");
		
		CcpPasswordHandler dependency = CcpDependencyInjection.getDependency(CcpPasswordHandler.class);
		
		String passwordHash = dependency.getHash(password);
		
		CcpJsonRepresentation jsonPassword = json.put("password", passwordHash); 
		
		CcpBulkItem itemPassword = new CcpBulkItem(jsonPassword, operation, JnEntityLoginPassword.INSTANCE);
		
		List<CcpBulkItem> asList = Arrays.asList(itemPassword);
		return asList;
	}

	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation json) {

		List<CcpBulkItem> asList = this.savePassword(json, CcpEntityOperationType.create);
		return asList;
	}

	public CcpEntity getEntityToSearch() {
		return JnEntityLoginPassword.INSTANCE;
	}

}
