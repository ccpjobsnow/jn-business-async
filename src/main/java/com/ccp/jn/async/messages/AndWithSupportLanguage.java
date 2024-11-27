package com.ccp.jn.async.messages;

public class AndWithSupportLanguage {

	final AndWithJsonValues andWithJsonValues;
	
	final String supportLanguage;

	AndWithSupportLanguage(AndWithJsonValues andWithJsonValues, String supportLanguage) {
		this.andWithJsonValues = andWithJsonValues;
		this.supportLanguage = supportLanguage;
	}
	
	public void executeAllAddedSteps() {
		this.andWithJsonValues.andWithEntityToSave.withTemplateId.soExecuteAllAddedSteps.getMessage.
		executeAllSteps(this.andWithJsonValues.andWithEntityToSave.withTemplateId.templateId, 
				this.andWithJsonValues.andWithEntityToSave.entityToSave, this.andWithJsonValues.jsonValues, supportLanguage);
	}
}
