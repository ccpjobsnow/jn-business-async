package com.ccp.jn.async.business;

import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.exceptions.http.CcpHttpError;
import com.ccp.utils.Utils;
import com.jn.commons.JnEntity;

public class SendHttpRequest {
	private final RemoveTries removeTries = CcpDependencyInjection.getInjected(RemoveTries.class);

	public CcpMapDecorator execute(CcpMapDecorator values, Function<CcpMapDecorator, CcpMapDecorator> processThatSendsHttpRequest, String...keys) {
		try {
			CcpMapDecorator apply = processThatSendsHttpRequest.apply(values);
			return apply;
		} catch (CcpHttpError e) {
			if(e.clientError) {
				JnEntity.http_api_error_client.createOrUpdate(e.entity.put("details", values.getSubMap(keys).asJson()));
				throw e;
			}
			
			if(e.serverError) {
				return this.retryToSendIntantMessage(values, processThatSendsHttpRequest, e, keys);
			}
			
			throw e;
		}
	}

	
	private CcpMapDecorator retryToSendIntantMessage(CcpMapDecorator values,Function<CcpMapDecorator, CcpMapDecorator> processThatSendsHttpRequest, CcpHttpError e, String... keys) {
		
		boolean exceededTries = JnEntity.http_api_retry_send_request.exceededTries(values, "tries", 3);
		
		if(exceededTries) {
			JnEntity.http_api_error_server.createOrUpdate(e.entity);
			throw e;
		}
		
		Utils.sleep(5000);
		CcpMapDecorator execute = this.execute(values, processThatSendsHttpRequest, keys);
		this.removeTries.apply(e.entity, "tries", 3, JnEntity.http_api_retry_send_request);
		return execute;
	}

}
