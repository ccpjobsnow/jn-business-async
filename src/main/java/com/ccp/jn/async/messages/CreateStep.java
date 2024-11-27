package com.ccp.jn.async.messages;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public class CreateStep {

	final JnAsyncUtilsGetMessage getMessage;

	public CreateStep(JnAsyncUtilsGetMessage getMessage) {
		this.getMessage = getMessage;
	}
	
	public WithProcess withProcess(Function<CcpJsonRepresentation, CcpJsonRepresentation> process) {
		return new WithProcess(this, process);
	}
}
