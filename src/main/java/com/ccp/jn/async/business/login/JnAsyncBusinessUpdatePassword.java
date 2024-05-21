package com.ccp.jn.async.business.login;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.jn.async.actions.RegisterLogin;
import com.ccp.jn.async.actions.RemoveAttempts;
import com.ccp.jn.async.actions.SolveLoginConflict;
import com.ccp.jn.async.actions.TransferRecordToReverseEntity;
import com.ccp.jn.async.actions.UpdatePassword;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;
import com.jn.commons.entities.JnEntityLoginPassword;
import com.jn.commons.entities.JnEntityLoginPasswordAttempts;
import com.jn.commons.entities.JnEntityLoginSessionCurrent;

public class JnAsyncBusinessUpdatePassword implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {


	public static final JnAsyncBusinessUpdatePassword INSTANCE = new JnAsyncBusinessUpdatePassword();
	
	private JnAsyncBusinessUpdatePassword() {
		
	}

	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {

		TransferRecordToReverseEntity executeLogout = new TransferRecordToReverseEntity(JnEntityLoginSessionCurrent.INSTANCE);
		
		CcpEntity mirrorEntity = JnEntityLoginPassword.INSTANCE.getMirrorEntity();
		TransferRecordToReverseEntity registerUnlock = new TransferRecordToReverseEntity(mirrorEntity);
		RemoveAttempts removeAttempts = new RemoveAttempts(JnEntityLoginPasswordAttempts.INSTANCE);

		JnAsyncCommitAndAudit.INSTANCE.
		executeSelectUnionAllThenExecuteBulkOperation(
				values 
				, UpdatePassword.INSTANCE
				, registerUnlock
				, removeAttempts
				, executeLogout
				, RegisterLogin.INSTANCE
				, SolveLoginConflict.INSTANCE
				);
		
		return CcpConstants.EMPTY_JSON;
	}

	

}
