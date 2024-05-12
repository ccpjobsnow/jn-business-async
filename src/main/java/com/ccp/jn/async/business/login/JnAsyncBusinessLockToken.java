package com.ccp.jn.async.business.login;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.actions.TransferRecordBetweenEntities;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;
import com.jn.commons.entities.JnEntityLoginToken;

public class JnAsyncBusinessLockToken implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {


	public static final JnAsyncBusinessLockToken INSTANCE = new JnAsyncBusinessLockToken();
	
	private JnAsyncBusinessLockToken() {
		
	}

	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {
		
		TransferRecordBetweenEntities registerLock = new TransferRecordBetweenEntities(JnEntityLoginToken.INSTANCE);

		JnAsyncCommitAndAudit.INSTANCE.
		executeSelectUnionAllThenExecuteBulkOperation(
				values 
				, registerLock
				);
		
		return CcpConstants.EMPTY_JSON;
	}

	

}
