package com.ccp.jn.async.business.login;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.actions.UpdatePassword;
import com.ccp.jn.async.actions.ExecuteLogout;
import com.ccp.jn.async.actions.ExecuteUnlock;
import com.ccp.jn.async.actions.RegisterLogin;
import com.ccp.jn.async.actions.RemoveAttempts;
import com.ccp.jn.async.actions.SolveLoginConflict;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;
import com.jn.commons.entities.JnEntityLoginTokenLocked;
import com.jn.commons.entities.JnEntityLoginTokenAttempts;
import com.jn.commons.entities.JnEntityLoginTokenUnlocked;

public class JnAsyncBusinessUpdatePassword implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {


	public static final JnAsyncBusinessUpdatePassword INSTANCE = new JnAsyncBusinessUpdatePassword();
	
	private JnAsyncBusinessUpdatePassword() {
		
	}

	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {
		
		ExecuteUnlock registerUnlock = new ExecuteUnlock(JnEntityLoginTokenLocked.INSTANCE, JnEntityLoginTokenUnlocked.INSTANCE);
		RemoveAttempts removeAttempts = new RemoveAttempts(JnEntityLoginTokenAttempts.INSTANCE);

		JnAsyncCommitAndAudit.INSTANCE.
		executeSelectUnionAllThenExecuteBulkOperation(
				values 
				, registerUnlock
				, removeAttempts
				, ExecuteLogout.INSTANCE
				, RegisterLogin.INSTANCE
				, UpdatePassword.INSTANCE
				, SolveLoginConflict.INSTANCE
				);
		
		return CcpConstants.EMPTY_JSON;
	}

	

}
