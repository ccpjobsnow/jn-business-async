package com.ccp.jn.async.messages;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntity;

public class AndWithEntityToSave {
	final WithTemplateId withTemplateId;
	
	final CcpEntity entityToSave;

	public AndWithEntityToSave(WithTemplateId withTemplateId, CcpEntity entityToSave) {
		super();
		this.withTemplateId = withTemplateId;
		this.entityToSave = entityToSave;
	}
	
	public AndWithJsonValues andWithJsonValues(CcpJsonRepresentation jsonValues) {
		return new AndWithJsonValues(this, jsonValues);
	}
}
