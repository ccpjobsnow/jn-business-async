package com.ccp.jn.async.messages;

import com.ccp.decorators.CcpJsonRepresentation;

public class AndWithJsonValues {

	final AndWithEntityToSave andWithEntityToSave;
	
	final CcpJsonRepresentation jsonValues;

	AndWithJsonValues(AndWithEntityToSave andWithEntityToSave, CcpJsonRepresentation jsonValues) {
		this.andWithEntityToSave = andWithEntityToSave;
		this.jsonValues = jsonValues;
	}
	
	public AndWithSupportLanguage andWithSupportLanguage(String supportLanguage) {
		return new AndWithSupportLanguage(this, supportLanguage);
	}
	
}
