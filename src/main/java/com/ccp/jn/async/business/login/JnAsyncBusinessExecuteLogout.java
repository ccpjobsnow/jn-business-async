package com.ccp.jn.async.business.login;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.actions.ExecuteLogout;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;

public class JnAsyncBusinessExecuteLogout implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {


	public static final JnAsyncBusinessExecuteLogout INSTANCE = new JnAsyncBusinessExecuteLogout();
	
	private JnAsyncBusinessExecuteLogout() {
		
	}
	
	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {

		JnAsyncCommitAndAudit.INSTANCE.
		executeSelectUnionAllThenExecuteBulkOperation(
				values 
				, ExecuteLogout.INSTANCE
				);
		
		return CcpConstants.EMPTY_JSON;
	}

}
