package com.ccp.jn.async.business.login;

import java.util.function.Function;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.actions.TransferRecordToReverseEntity;
import com.jn.commons.entities.JnEntityLoginSessionConflict;
import com.jn.commons.entities.JnEntityLoginSessionValidation;
import com.jn.commons.utils.JnCommonsExecuteBulkOperation;
import com.jn.commons.utils.JnDeleteFromEntity;

public class JnAsyncBusinessExecuteLogout implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {


	public static final JnAsyncBusinessExecuteLogout INSTANCE = new JnAsyncBusinessExecuteLogout();
	
	private JnAsyncBusinessExecuteLogout() {}
	
	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		
		TransferRecordToReverseEntity executeLogout = new TransferRecordToReverseEntity(JnEntityLoginSessionValidation.ENTITY, CcpOtherConstants.DO_NOTHING, CcpOtherConstants.DO_NOTHING, CcpOtherConstants.DO_NOTHING, CcpOtherConstants.DO_NOTHING);
		JnDeleteFromEntity deleteLoginSessionConflict = new JnDeleteFromEntity(JnEntityLoginSessionConflict.ENTITY);
		JnCommonsExecuteBulkOperation.INSTANCE.
		executeSelectUnionAllThenExecuteBulkOperation(
				json 
				, executeLogout
				, deleteLoginSessionConflict
				);
		
		return CcpOtherConstants.EMPTY_JSON;
	}

}
