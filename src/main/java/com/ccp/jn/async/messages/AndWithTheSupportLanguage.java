package com.ccp.jn.async.messages;

public class AndWithTheSupportLanguage {

	final AndWithTheJsonValues andWithJsonValues;
	
	final String supportLanguage;

	AndWithTheSupportLanguage(AndWithTheJsonValues andWithJsonValues, String supportLanguage) {
		this.andWithJsonValues = andWithJsonValues;
		this.supportLanguage = supportLanguage;
	}
	
	public void sendAllMessages() {
		this.andWithJsonValues.andWithEntityToSave.withTemplateId.soExecuteAllAddedSteps.getMessage.
		executeAllSteps(this.andWithJsonValues.andWithEntityToSave.withTemplateId.templateId, 
				this.andWithJsonValues.andWithEntityToSave.entityToSave, this.andWithJsonValues.jsonValues, supportLanguage);
	}
}
