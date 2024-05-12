package com.ccp.jn.async.business.login;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.actions.TransferRecordBetweenEntities;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;
import com.jn.commons.entities.JnEntityLoginPasswordLocked;
import com.jn.commons.entities.JnEntityLoginPasswordUnlocked;

public class JnAsyncBusinessLockPassword implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {


	public static final JnAsyncBusinessLockPassword INSTANCE = new JnAsyncBusinessLockPassword();
	
	private JnAsyncBusinessLockPassword() {
		
	}

	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {
		
		TransferRecordBetweenEntities registerLock = new TransferRecordBetweenEntities(JnEntityLoginPasswordUnlocked.INSTANCE, JnEntityLoginPasswordLocked.INSTANCE);

		JnAsyncCommitAndAudit.INSTANCE.
		executeSelectUnionAllThenExecuteBulkOperation(
				values 
				, registerLock
				);
		
		return CcpConstants.EMPTY_JSON;
	}

	

}
