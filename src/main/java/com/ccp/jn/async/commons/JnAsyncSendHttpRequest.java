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

public class JnAsyncSendHttpRequest {

	public static final JnAsyncSendHttpRequest INSTANCE = new JnAsyncSendHttpRequest();
	private JnAsyncSendHttpRequest() {}
	public CcpJsonRepresentation execute(CcpJsonRepresentation json, Function<CcpJsonRepresentation, CcpJsonRepresentation> processThatSendsHttpRequest, JnAsyncHttpRequestType httpRequestType, String...keys) {

		CcpJsonRepresentation jsonWithApiName = json.put(JnEntityHttpApiParameters.Fields.apiName.name(), httpRequestType.name());
		CcpJsonRepresentation httpApiParameters = JnEntityHttpApiParameters.ENTITY.getOneById(jsonWithApiName);
		CcpJsonRepresentation jsonWithHttpApiParameters = json.putAll(httpApiParameters);
		
		try {
			CcpJsonRepresentation apply = processThatSendsHttpRequest.apply(jsonWithHttpApiParameters);
			return apply;
		} catch (CcpHttpError e) {
			
			String details = jsonWithHttpApiParameters.getJsonPiece(keys).asUgglyJson();
			
			CcpJsonRepresentation httpErrorDetails = e.entity.putAll(jsonWithHttpApiParameters).put(JnEntityHttpApiErrorClient.Fields.details.name(), details);
			
			if(e.clientError) {
				String request = httpErrorDetails.getAsString(JnEntityHttpApiErrorClient.Fields.request.name());
				httpErrorDetails = httpErrorDetails.put(JnEntityHttpApiErrorClient.Fields.request.name(), request);
				JnEntityHttpApiErrorClient.ENTITY.createOrUpdate(httpErrorDetails);
				throw new HttpClientError(e);
			}
			
			if(e.serverError) {
				return this.retryToSendIntantMessage(e, json, httpErrorDetails, processThatSendsHttpRequest, httpRequestType, keys);
			}
			throw e;
		}
	}
	
	private CcpJsonRepresentation retryToSendIntantMessage(CcpHttpError e, CcpJsonRepresentation json, CcpJsonRepresentation httpErrorDetails, Function<CcpJsonRepresentation, CcpJsonRepresentation> processThatSendsHttpRequest, JnAsyncHttpRequestType httpRequestType, String... keys) {
		//LATER RENOMEAR ENTIDADES E CAMPOS
		Integer maxTries = httpErrorDetails.getAsIntegerNumber(JnEntityHttpApiParameters.Fields.maxTries.name());
		boolean exceededTries = JnEntityHttpApiRetrySendRequest.exceededTries(httpErrorDetails, JnEntityHttpApiRetrySendRequest.Fields.tries.name(), maxTries);
		
		if(exceededTries) {
			JnEntityHttpApiErrorServer.ENTITY.createOrUpdate(httpErrorDetails);
			throw new HttpServerError(e);
		}
		
		Integer sleep = httpErrorDetails.getAsIntegerNumber(JnEntityHttpApiParameters.Fields.sleep.name());
		new CcpTimeDecorator().sleep(sleep);
		CcpJsonRepresentation execute = this.execute(json, processThatSendsHttpRequest, httpRequestType, keys);
		//DOUBT REMOVER TENTATIVAS
		//		JnAsyncBusinessRemoveTries.INSTANCE.apply(httpErrorDetails, "tries", 3, JnEntityHttpApiRetrySendRequest.INSTANCE);
		return execute;
	}

}
