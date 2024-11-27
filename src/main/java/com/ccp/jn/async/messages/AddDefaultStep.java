package com.ccp.jn.async.messages;

public class AddDefaultStep {

	final JnAsyncSendMessage getMessage;

	AddDefaultStep(JnAsyncSendMessage getMessage) {
		this.getMessage = getMessage;
	}

	public CreateStep andCreateAnotherStep() {
		return new CreateStep(this.getMessage);
	}

	public SoWithAllAddedStepsAnd soWithAllAddedStepsAnd() {
		return new SoWithAllAddedStepsAnd(this.getMessage);
	}

	public JnAsyncSendMessage and() {
		return this.getMessage;
	}
}
