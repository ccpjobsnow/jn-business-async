package com.ccp.jn.async.business.login;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.actions.TransferRecordToReverseEntity;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;
import com.jn.commons.entities.JnEntityLoginSessionCurrent;

public class JnAsyncBusinessExecuteLogout implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {


	public static final JnAsyncBusinessExecuteLogout INSTANCE = new JnAsyncBusinessExecuteLogout();
	
	private JnAsyncBusinessExecuteLogout() {
		
	}
	
	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {
		TransferRecordToReverseEntity executeLogout = new TransferRecordToReverseEntity(JnEntityLoginSessionCurrent.INSTANCE);

		JnAsyncCommitAndAudit.INSTANCE.
		executeSelectUnionAllThenExecuteBulkOperation(
				values 
				, executeLogout
				);
		
		return CcpConstants.EMPTY_JSON;
	}

}
