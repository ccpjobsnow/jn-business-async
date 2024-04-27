package com.ccp.jn.async.exceptions;

@SuppressWarnings("serial")
public class HttpClientError extends RuntimeException{
	public HttpClientError(Throwable e) {
		super(e);
	}
}
