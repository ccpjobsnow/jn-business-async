package com.ccp.jn.async.messages;

public class SoWithAllAddedStepsAnd {

	final JnAsyncSendMessage getMessage;

	SoWithAllAddedStepsAnd(JnAsyncSendMessage getMessage) {
		this.getMessage = getMessage;
	}
	
	public WithTheTemplateId withTheTemplateEntity(String templateId) {
		return new WithTheTemplateId(this, templateId);
	}
	
}
