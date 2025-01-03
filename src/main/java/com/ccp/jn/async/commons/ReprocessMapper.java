package com.ccp.jn.async.commons;

import java.util.function.Function;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpBulkOperationResult;

class ReprocessMapper implements Function<CcpBulkOperationResult, CcpJsonRepresentation>{

	public static final ReprocessMapper INSTANCE = new ReprocessMapper();
	
	private ReprocessMapper() {}

	public CcpJsonRepresentation apply(CcpBulkOperationResult result) {
		long currentTimeMillis = System.currentTimeMillis();
		CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON
				.put("timestamp", currentTimeMillis);
		CcpBulkItem bulkItem = result.getBulkItem();
		CcpJsonRepresentation putAll = put.putAll(bulkItem.json);
		CcpJsonRepresentation errorDetails = result.getErrorDetails();
		CcpJsonRepresentation putAll2 = putAll.putAll(errorDetails);
		CcpJsonRepresentation renameKey = putAll2.renameField("type", "errorType");
		CcpJsonRepresentation jsonPiece = renameKey.getJsonPiece("errorType", "reason");
		return jsonPiece;
	}
	
	
	
}
