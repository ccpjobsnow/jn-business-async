package com.ccp.jn.async.exceptions;

@SuppressWarnings("serial")
public class HttpServerError extends RuntimeException{
	public HttpServerError(Throwable e) {
		super(e);
	}

}
