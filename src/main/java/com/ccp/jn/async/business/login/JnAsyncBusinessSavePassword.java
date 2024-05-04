package com.ccp.jn.async.business.login;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.actions.ExecuteLogout;
import com.ccp.jn.async.actions.ExecuteUnlock;
import com.ccp.jn.async.actions.RegisterLogin;
import com.ccp.jn.async.actions.RemoveAttempts;
import com.ccp.jn.async.actions.SolveLoginConflict;
import com.ccp.jn.async.commons.JnAsyncBusinessCommitAndAudit;
import com.jn.commons.entities.JnEntityLoginLockedToken;
import com.jn.commons.entities.JnEntityLoginTokenAttempts;
import com.jn.commons.entities.JnEntityLoginUnlockedToken;

public class JnAsyncBusinessSavePassword implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {


	public static final JnAsyncBusinessSavePassword INSTANCE = new JnAsyncBusinessSavePassword();
	
	private JnAsyncBusinessSavePassword() {
		
	}

	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {
		
		ExecuteUnlock executeUnlock = new ExecuteUnlock(JnEntityLoginLockedToken.INSTANCE, JnEntityLoginUnlockedToken.INSTANCE);
		JnEntityLoginTokenAttempts entityAttempts = JnEntityLoginTokenAttempts.INSTANCE;
		RemoveAttempts removeAttempts = new RemoveAttempts(entityAttempts);

		JnAsyncBusinessCommitAndAudit.INSTANCE.
		executeSelectUnionAllThenExecuteBulkOperation(
				values 
				, executeUnlock
				, removeAttempts
				, ExecuteLogout.INSTANCE
				, RegisterLogin.INSTANCE
				, SolveLoginConflict.INSTANCE
				);
		
		return CcpConstants.EMPTY_JSON;
	}

	

}
