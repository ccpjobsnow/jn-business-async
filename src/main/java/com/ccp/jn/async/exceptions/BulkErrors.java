package com.ccp.jn.async.exceptions;

import java.util.List;

import com.ccp.decorators.CcpMapDecorator;

@SuppressWarnings("serial")
public class BulkErrors extends RuntimeException{

	public BulkErrors(List<CcpMapDecorator> failedRecords) {
		super(failedRecords.toString());
	}
	
}
