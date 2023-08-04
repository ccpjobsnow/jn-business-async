package com.ccp.jn.async.exceptions;

@SuppressWarnings("serial")
public class JnHttpClientError extends RuntimeException{
	public JnHttpClientError(Throwable e) {
		super(e);
	}
}
