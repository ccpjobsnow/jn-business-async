package com.ccp.jn.async.business.login;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.jn.async.actions.TransferRecordBetweenEntities;
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
	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {
		
		CcpEntity mirrorEntity = JnEntityLoginPassword.INSTANCE.getMirrorEntity();
		TransferRecordBetweenEntities executeUnlock = new TransferRecordBetweenEntities(mirrorEntity);
		JnEntityLoginPasswordAttempts entityAttempts = JnEntityLoginPasswordAttempts.INSTANCE;
		RemoveAttempts removeAttempts = new RemoveAttempts(entityAttempts);

		JnAsyncCommitAndAudit.INSTANCE.
		executeSelectUnionAllThenExecuteBulkOperation(
				values 
				, executeUnlock
				, removeAttempts
				, RegisterLogin.INSTANCE
				);
		
		return CcpConstants.EMPTY_JSON;
	}

}
