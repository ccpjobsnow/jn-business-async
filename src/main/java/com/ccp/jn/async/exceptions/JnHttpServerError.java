package com.ccp.jn.async.exceptions;

@SuppressWarnings("serial")
public class JnHttpServerError extends RuntimeException{
	public JnHttpServerError(Throwable e) {
		super(e);
	}

}
