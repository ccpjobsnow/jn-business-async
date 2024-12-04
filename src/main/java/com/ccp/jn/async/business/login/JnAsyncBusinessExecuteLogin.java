package com.ccp.jn.async.business.login;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.jn.async.actions.TransferRecordToReverseEntity;
import com.ccp.jn.async.actions.RegisterLogin;
import com.ccp.jn.async.actions.RemoveAttempts;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;
import com.jn.commons.entities.JnEntityLoginPassword;
import com.jn.commons.entities.JnEntityLoginPasswordAttempts;

public class JnAsyncBusinessExecuteLogin implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {


	public static final JnAsyncBusinessExecuteLogin INSTANCE = new JnAsyncBusinessExecuteLogin();
	
	private JnAsyncBusinessExecuteLogin() {
		
	}
	
	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		
		CcpEntity twinEntity = JnEntityLoginPassword.ENTITY.getTwinEntity();
		TransferRecordToReverseEntity executeUnlock = new TransferRecordToReverseEntity(twinEntity, CcpConstants.DO_NOTHING, CcpConstants.DO_NOTHING, CcpConstants.DO_NOTHING, CcpConstants.DO_NOTHING);
		CcpEntity entityAttempts = JnEntityLoginPasswordAttempts.ENTITY;
		RemoveAttempts removeAttempts = new RemoveAttempts(entityAttempts);

		JnAsyncCommitAndAudit.INSTANCE.
		executeSelectUnionAllThenExecuteBulkOperation(
				json 
				, executeUnlock
				, removeAttempts
				, RegisterLogin.INSTANCE
				);
		
		return CcpConstants.EMPTY_JSON;
	}

}
