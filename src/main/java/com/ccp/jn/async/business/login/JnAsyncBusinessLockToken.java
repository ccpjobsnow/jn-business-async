package com.ccp.jn.async.business.login;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.actions.TransferRecordToReverseEntity;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;
import com.jn.commons.entities.JnEntityLoginToken;

public class JnAsyncBusinessLockToken implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {


	public static final JnAsyncBusinessLockToken INSTANCE = new JnAsyncBusinessLockToken();
	
	private JnAsyncBusinessLockToken() {
		
	}

	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		
		TransferRecordToReverseEntity registerLock = new TransferRecordToReverseEntity(JnEntityLoginToken.ENTITY, CcpConstants.DO_NOTHING, CcpConstants.DO_NOTHING, CcpConstants.DO_NOTHING, CcpConstants.DO_NOTHING);

		JnAsyncCommitAndAudit.INSTANCE.
		executeSelectUnionAllThenExecuteBulkOperation(
				json 
				, registerLock
				);
		
		return CcpConstants.EMPTY_JSON;
	}

	

}
