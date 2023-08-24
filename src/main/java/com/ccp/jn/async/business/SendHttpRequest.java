package com.ccp.jn.async.business;

import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.exceptions.http.CcpHttpError;
import com.ccp.jn.async.exceptions.JnHttpClientError;
import com.ccp.jn.async.exceptions.JnHttpServerError;
import com.jn.commons.JnEntity;

public class SendHttpRequest {
	private final RemoveTries removeTries = new RemoveTries();

	public CcpMapDecorator execute(CcpMapDecorator values, Function<CcpMapDecorator, CcpMapDecorator> processThatSendsHttpRequest, JnHttpRequestType httpRequestType, String...keys) {

		CcpMapDecorator valuesWithApiName = values.put("apiName", httpRequestType.name());
		CcpMapDecorator httpApiParameters = JnEntity.http_api_parameters.getOneById(valuesWithApiName);
		CcpMapDecorator valuesWithHttpApiParameters = values.putAll(httpApiParameters);
		
		try {
			CcpMapDecorator apply = processThatSendsHttpRequest.apply(valuesWithHttpApiParameters);
			return apply;
		} catch (CcpHttpError e) {
			
			String details = valuesWithHttpApiParameters.getSubMap(keys).asJson();
			
			CcpMapDecorator httpErrorDetails = e.entity.putAll(valuesWithHttpApiParameters).put("details", details);
			
			if(e.clientError) {
				JnEntity.http_api_error_client.createOrUpdate(httpErrorDetails);
				throw new JnHttpClientError(e);
			}
			
			if(e.serverError) {
				return this.retryToSendIntantMessage(e, values, httpErrorDetails, processThatSendsHttpRequest, httpRequestType, keys);
			}
			throw e;
		}
	}
	
	private CcpMapDecorator retryToSendIntantMessage(CcpHttpError e, CcpMapDecorator values, CcpMapDecorator httpErrorDetails, Function<CcpMapDecorator, CcpMapDecorator> processThatSendsHttpRequest, JnHttpRequestType httpRequestType, String... keys) {
		
		Integer maxTries = httpErrorDetails.getAsIntegerNumber("maxTries");
		boolean exceededTries = JnEntity.http_api_retry_send_request.exceededTries(httpErrorDetails, "tries", maxTries);
		
		if(exceededTries) {
			JnEntity.http_api_error_server.createOrUpdate(httpErrorDetails);
			throw new JnHttpServerError(e);
		}
		
		Integer sleep = httpErrorDetails.getAsIntegerNumber("sleep");
		new CcpTimeDecorator().sleep(sleep);
		CcpMapDecorator execute = this.execute(values, processThatSendsHttpRequest, httpRequestType, keys);
		this.removeTries.apply(httpErrorDetails, "tries", 3, JnEntity.http_api_retry_send_request);
		return execute;
	}

}
