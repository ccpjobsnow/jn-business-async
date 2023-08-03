package com.ccp.jn.async.business;

import java.util.function.Consumer;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.exceptions.http.CcpHttpClientError;
import com.ccp.exceptions.http.CcpHttpServerError;
import com.ccp.utils.Utils;
import com.jn.commons.JnEntity;

public class SendHttpRequest {
	//TODO API TYPE
	private final RemoveTries removeTries = CcpDependencyInjection.getInjected(RemoveTries.class);

	public void execute(CcpMapDecorator values, Consumer<CcpMapDecorator> processThatSendsHttpRequest) {
		try {
			processThatSendsHttpRequest.accept(values);
			this.removeTries.apply(values, "tries", 3, JnEntity.http_api_retry_send_request);
		} catch (CcpHttpServerError e) {
			this.retryToSendIntantMessage(values, processThatSendsHttpRequest, e);
		} catch (CcpHttpClientError e) {
			JnEntity.http_api_error_client.createOrUpdate(e.entity);
			throw e;
		}
	}

	
	private void retryToSendIntantMessage(CcpMapDecorator values, Consumer<CcpMapDecorator> processThatSendsHttpRequest, CcpHttpServerError e) {
		boolean exceededTries = JnEntity.http_api_retry_send_request.exceededTries(values, "tries", 3);
		
		if(exceededTries) {
			JnEntity.http_api_error_server.createOrUpdate(e.entity);
			return;
		}
		
		Utils.sleep(5000);
		this.execute(values, processThatSendsHttpRequest);
	}

}
