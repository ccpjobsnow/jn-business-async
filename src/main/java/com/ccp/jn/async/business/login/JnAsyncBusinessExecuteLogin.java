package com.ccp.jn.async.business.login;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.actions.ExecuteUnlock;
import com.ccp.jn.async.actions.RegisterLogin;
import com.ccp.jn.async.actions.RemoveAttempts;
import com.ccp.jn.async.commons.JnAsyncBusinessCommitAndAudit;
import com.jn.commons.entities.JnEntityLoginLockedPassword;
import com.jn.commons.entities.JnEntityLoginPasswordAttempts;
import com.jn.commons.entities.JnEntityLoginUnlockedPassword;

public class JnAsyncBusinessExecuteLogin implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {


	public static final JnAsyncBusinessExecuteLogin INSTANCE = new JnAsyncBusinessExecuteLogin();
	
	private JnAsyncBusinessExecuteLogin() {
		
	}
	
	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {
		
		ExecuteUnlock executeUnlock = new ExecuteUnlock(JnEntityLoginLockedPassword.INSTANCE, JnEntityLoginUnlockedPassword.INSTANCE);
		JnEntityLoginPasswordAttempts entityAttempts = JnEntityLoginPasswordAttempts.INSTANCE;
		RemoveAttempts removeAttempts = new RemoveAttempts(entityAttempts);

		JnAsyncBusinessCommitAndAudit.INSTANCE.
		executeSelectUnionAllThenExecuteBulkOperation(
				values 
				, executeUnlock
				, removeAttempts
				, RegisterLogin.INSTANCE
				);
		
		return CcpConstants.EMPTY_JSON;
	}

}
