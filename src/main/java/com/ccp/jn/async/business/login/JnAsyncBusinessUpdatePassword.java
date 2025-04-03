package com.ccp.jn.async.business.login;

import java.util.function.Function;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.jn.async.actions.RegisterLogin;
import com.ccp.jn.async.actions.RemoveAttempts;
import com.ccp.jn.async.actions.SolveLoginConflict;
import com.ccp.jn.async.actions.TransferRecordToReverseEntity;
import com.ccp.jn.async.actions.UpdatePassword;
import com.jn.commons.entities.JnEntityLoginPassword;
import com.jn.commons.entities.JnEntityLoginPasswordAttempts;
import com.jn.commons.entities.JnEntityLoginSessionValidation;
import com.jn.commons.utils.JnCommonsExecuteBulkOperation;

public class JnAsyncBusinessUpdatePassword implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public static final JnAsyncBusinessUpdatePassword INSTANCE = new JnAsyncBusinessUpdatePassword();
	
	private JnAsyncBusinessUpdatePassword() {}

	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {

		TransferRecordToReverseEntity executeLogout = new TransferRecordToReverseEntity(JnEntityLoginSessionValidation.ENTITY, CcpOtherConstants.DO_NOTHING, CcpOtherConstants.DO_NOTHING, CcpOtherConstants.DO_NOTHING, CcpOtherConstants.DO_NOTHING);
		
		CcpEntity twinEntity = JnEntityLoginPassword.ENTITY.getTwinEntity();
		TransferRecordToReverseEntity registerUnlock = new TransferRecordToReverseEntity(twinEntity, CcpOtherConstants.DO_NOTHING, CcpOtherConstants.DO_NOTHING, CcpOtherConstants.DO_NOTHING, CcpOtherConstants.DO_NOTHING);
		RemoveAttempts removeAttempts = new RemoveAttempts(JnEntityLoginPasswordAttempts.ENTITY);

		CcpJsonRepresentation renameField = json.renameField("sessionToken", JnEntityLoginSessionValidation.Fields.token.name());
		JnCommonsExecuteBulkOperation.INSTANCE.
		executeSelectUnionAllThenExecuteBulkOperation(
				renameField 
				, UpdatePassword.INSTANCE
				, registerUnlock
				, removeAttempts
				, executeLogout
				, RegisterLogin.INSTANCE
				, SolveLoginConflict.INSTANCE
				);
		
		return CcpOtherConstants.EMPTY_JSON;
	}
}
