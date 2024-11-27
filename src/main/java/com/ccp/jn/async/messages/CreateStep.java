package com.ccp.jn.async.messages;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public class CreateStep {

	final JnAsyncSendMessage getMessage;

	CreateStep(JnAsyncSendMessage getMessage) {
		this.getMessage = getMessage;
	}
	
	public WithTheProcess withTheProcess(Function<CcpJsonRepresentation, CcpJsonRepresentation> process) {
		return new WithTheProcess(this, process);
	}
}
