package com.ccp.jn.async.business;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.exceptions.http.CcpHttpError;
import com.ccp.jn.async.commons.utils.JnHttpRequestType;
import com.ccp.jn.async.exceptions.JnHttpClientError;
import com.ccp.jn.async.exceptions.JnHttpServerError;
import com.jn.commons.entities.JnEntityHttpApiErrorClient;
import com.jn.commons.entities.JnEntityHttpApiErrorServer;
import com.jn.commons.entities.JnEntityHttpApiParameters;
import com.jn.commons.entities.JnEntityHttpApiRetrySendRequest;

public class JnAsyncBusinessSendHttpRequest {
	private final JnAsyncBusinessRemoveTries removeTries = new JnAsyncBusinessRemoveTries();

	public CcpJsonRepresentation execute(CcpJsonRepresentation values, Function<CcpJsonRepresentation, CcpJsonRepresentation> processThatSendsHttpRequest, JnHttpRequestType httpRequestType, String...keys) {

		CcpJsonRepresentation valuesWithApiName = values.put("apiName", httpRequestType.name());
		CcpJsonRepresentation httpApiParameters = new JnEntityHttpApiParameters().getOneById(valuesWithApiName);
		CcpJsonRepresentation valuesWithHttpApiParameters = values.putAll(httpApiParameters);
		
		try {
			CcpJsonRepresentation apply = processThatSendsHttpRequest.apply(valuesWithHttpApiParameters);
			return apply;
		} catch (CcpHttpError e) {
			
			String details = valuesWithHttpApiParameters.getJsonPiece(keys).asUgglyJson();
			
			CcpJsonRepresentation httpErrorDetails = e.entity.putAll(valuesWithHttpApiParameters).put("details", details);
			
			if(e.clientError) {
				new JnEntityHttpApiErrorClient().createOrUpdate(httpErrorDetails);
				throw new JnHttpClientError(e);
			}
			
			if(e.serverError) {
				return this.retryToSendIntantMessage(e, values, httpErrorDetails, processThatSendsHttpRequest, httpRequestType, keys);
			}
			throw e;
		}
	}
	
	private CcpJsonRepresentation retryToSendIntantMessage(CcpHttpError e, CcpJsonRepresentation values, CcpJsonRepresentation httpErrorDetails, Function<CcpJsonRepresentation, CcpJsonRepresentation> processThatSendsHttpRequest, JnHttpRequestType httpRequestType, String... keys) {
		
		Integer maxTries = httpErrorDetails.getAsIntegerNumber("maxTries");
		boolean exceededTries = new JnEntityHttpApiRetrySendRequest().exceededTries(httpErrorDetails, "tries", maxTries);
		
		if(exceededTries) {
			new JnEntityHttpApiErrorServer().createOrUpdate(httpErrorDetails);
			throw new JnHttpServerError(e);
		}
		
		Integer sleep = httpErrorDetails.getAsIntegerNumber("sleep");
		new CcpTimeDecorator().sleep(sleep);
		CcpJsonRepresentation execute = this.execute(values, processThatSendsHttpRequest, httpRequestType, keys);
		this.removeTries.apply(httpErrorDetails, "tries", 3, new JnEntityHttpApiRetrySendRequest());
		return execute;
	}

}
