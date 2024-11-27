package com.ccp.jn.async.messages;

public class AddDefaultStep {

	final JnAsyncUtilsGetMessage getMessage;

	AddDefaultStep(JnAsyncUtilsGetMessage getMessage) {
		this.getMessage = getMessage;
	}

	public CreateStep andCreateAnotherStep() {
		return new CreateStep(this.getMessage);
	}

	public SoWithAllAddedStepsAnd soWithAllAddedStepsAnd() {
		return new SoWithAllAddedStepsAnd(this.getMessage);
	}

	public JnAsyncUtilsGetMessage and() {
		return this.getMessage;
	}
}
