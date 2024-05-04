package com.ccp.jn.async.commons;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.exceptions.http.CcpHttpError;
import com.ccp.jn.async.exceptions.HttpClientError;
import com.ccp.jn.async.exceptions.HttpServerError;
import com.jn.commons.entities.JnEntityHttpApiErrorClient;
import com.jn.commons.entities.JnEntityHttpApiErrorServer;
import com.jn.commons.entities.JnEntityHttpApiParameters;
import com.jn.commons.entities.JnEntityHttpApiRetrySendRequest;

public class JnAsyncBusinessSendHttpRequest {

	public static final JnAsyncBusinessSendHttpRequest INSTANCE = new JnAsyncBusinessSendHttpRequest();
	private JnAsyncBusinessSendHttpRequest() {}
	public CcpJsonRepresentation execute(CcpJsonRepresentation values, Function<CcpJsonRepresentation, CcpJsonRepresentation> processThatSendsHttpRequest, JnHttpRequestType httpRequestType, String...keys) {

		CcpJsonRepresentation valuesWithApiName = values.put("apiName", httpRequestType.name());
		CcpJsonRepresentation httpApiParameters = JnEntityHttpApiParameters.INSTANCE.getOneById(valuesWithApiName);
		CcpJsonRepresentation valuesWithHttpApiParameters = values.putAll(httpApiParameters);
		
		try {
			CcpJsonRepresentation apply = processThatSendsHttpRequest.apply(valuesWithHttpApiParameters);
			return apply;
		} catch (CcpHttpError e) {
			
			String details = valuesWithHttpApiParameters.getJsonPiece(keys).asUgglyJson();
			
			CcpJsonRepresentation httpErrorDetails = e.entity.putAll(valuesWithHttpApiParameters).put("details", details);
			
			if(e.clientError) {
				String request = httpErrorDetails.getAsString("request");
				httpErrorDetails = httpErrorDetails.put("request", request);
				JnEntityHttpApiErrorClient.INSTANCE.createOrUpdate(httpErrorDetails);
				throw new HttpClientError(e);
			}
			
			if(e.serverError) {
				return this.retryToSendIntantMessage(e, values, httpErrorDetails, processThatSendsHttpRequest, httpRequestType, keys);
			}
			throw e;
		}
	}
	
	private CcpJsonRepresentation retryToSendIntantMessage(CcpHttpError e, CcpJsonRepresentation values, CcpJsonRepresentation httpErrorDetails, Function<CcpJsonRepresentation, CcpJsonRepresentation> processThatSendsHttpRequest, JnHttpRequestType httpRequestType, String... keys) {
		
		Integer maxTries = httpErrorDetails.getAsIntegerNumber("maxTries");
		boolean exceededTries = JnEntityHttpApiRetrySendRequest.INSTANCE.exceededTries(httpErrorDetails, "tries", maxTries);
		
		if(exceededTries) {
			JnEntityHttpApiErrorServer.INSTANCE.createOrUpdate(httpErrorDetails);
			throw new HttpServerError(e);
		}
		
		Integer sleep = httpErrorDetails.getAsIntegerNumber("sleep");
		new CcpTimeDecorator().sleep(sleep);
		CcpJsonRepresentation execute = this.execute(values, processThatSendsHttpRequest, httpRequestType, keys);
		//TODO O QUE É ISSO AQUI EMBAIXO?
		//		JnAsyncBusinessRemoveTries.INSTANCE.apply(httpErrorDetails, "tries", 3, JnEntityHttpApiRetrySendRequest.INSTANCE);
		return execute;
	}

}