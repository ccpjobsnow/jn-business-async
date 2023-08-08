package com.ccp.jn.async.business;

import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.exceptions.http.CcpHttpError;
import com.ccp.jn.async.exceptions.JnHttpClientError;
import com.ccp.jn.async.exceptions.JnHttpServerError;
import com.ccp.utils.Utils;
import com.jn.commons.JnEntity;

public class SendHttpRequest {
	private final RemoveTries removeTries = CcpDependencyInjection.getInjected(RemoveTries.class);

	public CcpMapDecorator execute(CcpMapDecorator values, Function<CcpMapDecorator, CcpMapDecorator> processThatSendsHttpRequest, JnHttpRequestType httpRequestType, String...keys) {
		try {
			CcpMapDecorator valuesWithHttpParameters = JnEntity.http_api_parameters.getOneById(values);
			CcpMapDecorator apply = processThatSendsHttpRequest.apply(valuesWithHttpParameters);
			return apply;
		} catch (CcpHttpError e) {
			if(e.clientError) {
				JnEntity.http_api_error_client.createOrUpdate(e.entity.put("details", values.getSubMap(keys).asJson()));
				throw new JnHttpClientError(e);
			}
			
			if(e.serverError) {
				return this.retryToSendIntantMessage(values, processThatSendsHttpRequest, httpRequestType,  e, keys);
			}
			throw e;
		}
	}

	
	private CcpMapDecorator retryToSendIntantMessage(CcpMapDecorator values,Function<CcpMapDecorator, CcpMapDecorator> processThatSendsHttpRequest, JnHttpRequestType httpRequestType, CcpHttpError e, String... keys) {
		
		CcpMapDecorator tries = e.entity.put("details", values.getSubMap(keys).asJson());
		Integer maxTries = values.getAsIntegerNumber("maxTries");
		boolean exceededTries = JnEntity.http_api_retry_send_request.exceededTries(tries, "tries", maxTries);
		
		if(exceededTries) {
			JnEntity.http_api_error_server.createOrUpdate(e.entity);
			throw new JnHttpServerError(e);
		}
		
		Integer sleep = values.getAsIntegerNumber("sleep");
		Utils.sleep(sleep);
		CcpMapDecorator execute = this.execute(values, processThatSendsHttpRequest, httpRequestType, keys);
		this.removeTries.apply(tries, "tries", 3, JnEntity.http_api_retry_send_request);
		return execute;
	}

}
