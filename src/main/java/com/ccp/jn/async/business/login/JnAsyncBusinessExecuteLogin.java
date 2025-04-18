package com.ccp.jn.async.business.login;

import java.util.function.Function;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.jn.async.actions.TransferRecordToReverseEntity;
import com.ccp.jn.async.actions.RegisterLogin;
import com.ccp.jn.async.actions.RemoveAttempts;
import com.jn.commons.entities.JnEntityLoginPassword;
import com.jn.commons.entities.JnEntityLoginPasswordAttempts;
import com.jn.commons.entities.JnEntityLoginSessionValidation;
import com.jn.commons.utils.JnCommonsExecuteBulkOperation;

public class JnAsyncBusinessExecuteLogin implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public static final JnAsyncBusinessExecuteLogin INSTANCE = new JnAsyncBusinessExecuteLogin();
	
	private JnAsyncBusinessExecuteLogin() {}
	
	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		CcpJsonRepresentation renameField = json.renameField("sessionToken", JnEntityLoginSessionValidation.Fields.token.name());
		
		CcpEntity twinEntity = JnEntityLoginPassword.ENTITY.getTwinEntity();
		TransferRecordToReverseEntity executeUnlock = new TransferRecordToReverseEntity(twinEntity, CcpOtherConstants.DO_NOTHING, CcpOtherConstants.DO_NOTHING, CcpOtherConstants.DO_NOTHING, CcpOtherConstants.DO_NOTHING);
		CcpEntity entityAttempts = JnEntityLoginPasswordAttempts.ENTITY;
		RemoveAttempts removeAttempts = new RemoveAttempts(entityAttempts);

		JnCommonsExecuteBulkOperation.INSTANCE.
		executeSelectUnionAllThenExecuteBulkOperation(
				renameField 
				, executeUnlock
				, removeAttempts
				, RegisterLogin.INSTANCE
				);
		
		return CcpOtherConstants.EMPTY_JSON;
	}

}
