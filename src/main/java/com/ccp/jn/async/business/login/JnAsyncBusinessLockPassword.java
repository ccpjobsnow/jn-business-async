package com.ccp.jn.async.business.login;

import java.util.function.Function;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.actions.TransferRecordToReverseEntity;
import com.jn.commons.entities.JnEntityLoginPassword;
import com.jn.commons.utils.JnCommonsExecuteBulkOperation;

public class JnAsyncBusinessLockPassword implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {


	public static final JnAsyncBusinessLockPassword INSTANCE = new JnAsyncBusinessLockPassword();
	
	private JnAsyncBusinessLockPassword() {
		
	}

	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		
		TransferRecordToReverseEntity registerLock = new TransferRecordToReverseEntity(JnEntityLoginPassword.ENTITY, CcpOtherConstants.DO_NOTHING, CcpOtherConstants.DO_NOTHING, CcpOtherConstants.DO_NOTHING, CcpOtherConstants.DO_NOTHING);
		JnCommonsExecuteBulkOperation.INSTANCE.
		executeSelectUnionAllThenExecuteBulkOperation(
				json 
				, registerLock
				);
		
		return CcpOtherConstants.EMPTY_JSON;
	}

	

}
