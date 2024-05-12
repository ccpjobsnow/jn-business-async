package com.ccp.jn.async.business.login;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.jn.async.actions.RegisterLogin;
import com.ccp.jn.async.actions.RemoveAttempts;
import com.ccp.jn.async.actions.SolveLoginConflict;
import com.ccp.jn.async.actions.TransferRecordBetweenEntities;
import com.ccp.jn.async.actions.UpdatePassword;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;
import com.jn.commons.entities.JnEntityLoginSessionCurrent;
import com.jn.commons.entities.JnEntityLoginToken;
import com.jn.commons.entities.JnEntityLoginTokenAttempts;

public class JnAsyncBusinessUpdatePassword implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {


	public static final JnAsyncBusinessUpdatePassword INSTANCE = new JnAsyncBusinessUpdatePassword();
	
	private JnAsyncBusinessUpdatePassword() {
		
	}

	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {

		TransferRecordBetweenEntities executeLogout = new TransferRecordBetweenEntities(JnEntityLoginSessionCurrent.INSTANCE);
		
		CcpEntity mirrorEntity = JnEntityLoginToken.INSTANCE.getMirrorEntity();
		TransferRecordBetweenEntities registerUnlock = new TransferRecordBetweenEntities(mirrorEntity);
		RemoveAttempts removeAttempts = new RemoveAttempts(JnEntityLoginTokenAttempts.INSTANCE);

		JnAsyncCommitAndAudit.INSTANCE.
		executeSelectUnionAllThenExecuteBulkOperation(
				values 
				, registerUnlock
				, removeAttempts
				, executeLogout
				, RegisterLogin.INSTANCE
				, UpdatePassword.INSTANCE
				, SolveLoginConflict.INSTANCE
				);
		
		return CcpConstants.EMPTY_JSON;
	}

	

}
