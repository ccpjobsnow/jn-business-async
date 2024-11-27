package com.ccp.jn.async.messages;

import com.ccp.especifications.db.utils.CcpEntity;

public class AndWithTemplateEntity {
	
	final AndWithParametersEntity andWithParametersEntity;
	
	final CcpEntity templateEntity;

	public AndWithTemplateEntity(AndWithParametersEntity andWithParametersEntity, CcpEntity templateEntity) {
		this.andWithParametersEntity = andWithParametersEntity;
		this.templateEntity = templateEntity;
	}
	
	public CreateStep andCreateAnotherStep() {
		this.addStep();
		return new CreateStep(this.andWithParametersEntity.withProcess.createStep.getMessage);
	}

	private void addStep() {
		this.andWithParametersEntity.withProcess.createStep.getMessage
		.addOneStep(this.andWithParametersEntity.withProcess.process, this.andWithParametersEntity.parametersEntity, this.templateEntity);
	}
	
	public SoWithAllAddedStepsAnd soWithAllAddedStepsAnd() {
		this.addStep();
		return new SoWithAllAddedStepsAnd(this.andWithParametersEntity.withProcess.createStep.getMessage);
	}
	
}
