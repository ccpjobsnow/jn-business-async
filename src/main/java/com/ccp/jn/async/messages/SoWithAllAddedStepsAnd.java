package com.ccp.jn.async.messages;

public class SoWithAllAddedStepsAnd {

	final JnAsyncUtilsGetMessage getMessage;

	public SoWithAllAddedStepsAnd(JnAsyncUtilsGetMessage getMessage) {
		this.getMessage = getMessage;
	}
	
	public WithTemplateId withTemplateEntity(String templateId) {
		return new WithTemplateId(this, templateId);
	}
	
}
