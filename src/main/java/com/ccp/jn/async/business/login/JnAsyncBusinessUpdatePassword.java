package com.ccp.jn.async.business.login;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.actions.ExecuteLogout;
import com.ccp.jn.async.actions.TransferRecordBetweenEntities;
import com.ccp.jn.async.actions.RegisterLogin;
import com.ccp.jn.async.actions.RemoveAttempts;
import com.ccp.jn.async.actions.SolveLoginConflict;
import com.ccp.jn.async.actions.UpdatePassword;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;
import com.jn.commons.entities.JnEntityLoginPasswordAttempts;
import com.jn.commons.entities.JnEntityLoginPasswordLocked;
import com.jn.commons.entities.JnEntityLoginPasswordUnlocked;

public class JnAsyncBusinessUpdatePassword implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {


	public static final JnAsyncBusinessUpdatePassword INSTANCE = new JnAsyncBusinessUpdatePassword();
	
	private JnAsyncBusinessUpdatePassword() {
		
	}

	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {
		
		TransferRecordBetweenEntities registerUnlock = new TransferRecordBetweenEntities(JnEntityLoginPasswordLocked.INSTANCE, JnEntityLoginPasswordUnlocked.INSTANCE);
		RemoveAttempts removeAttempts = new RemoveAttempts(JnEntityLoginPasswordAttempts.INSTANCE);

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
