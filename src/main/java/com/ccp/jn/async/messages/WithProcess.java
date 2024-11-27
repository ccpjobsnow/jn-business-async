package com.ccp.jn.async.messages;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntity;

public class WithProcess {

	final CreateStep createStep;
	
	final Function<CcpJsonRepresentation, CcpJsonRepresentation> process;

	public WithProcess(CreateStep createStep, Function<CcpJsonRepresentation, CcpJsonRepresentation> process) {
		this.createStep = createStep;
		this.process = process;
	}

	public AndWithParametersEntity andWithParametersEntity(CcpEntity parametersEntity) {
		return new AndWithParametersEntity(this, parametersEntity);
	}
	
	
}
