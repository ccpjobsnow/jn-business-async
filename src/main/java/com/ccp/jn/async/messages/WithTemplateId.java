package com.ccp.jn.async.messages;

import com.ccp.especifications.db.utils.CcpEntity;

public class WithTemplateId {

	final SoWithAllAddedStepsAnd soExecuteAllAddedSteps;
	
	final String templateId;

	WithTemplateId(SoWithAllAddedStepsAnd soExecuteAllAddedSteps, String templateId) {
		this.soExecuteAllAddedSteps = soExecuteAllAddedSteps;
		this.templateId = templateId;
	}
	
	
	public AndWithEntityToSave andWithEntityToSave(CcpEntity entityToSave) {
		return new AndWithEntityToSave(this, entityToSave);
	}
	
}
