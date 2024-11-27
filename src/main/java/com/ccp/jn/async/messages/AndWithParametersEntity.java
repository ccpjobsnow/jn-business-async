package com.ccp.jn.async.messages;

import com.ccp.especifications.db.utils.CcpEntity;

public class AndWithParametersEntity {

	final WithProcess withProcess;

	final CcpEntity parametersEntity;

	public AndWithParametersEntity(WithProcess withProcess, CcpEntity parametersEntity) {
		super();
		this.withProcess = withProcess;
		this.parametersEntity = parametersEntity;
	}
	
	public AndWithTemplateEntity andWithTemplateEntity(CcpEntity templateEntity) {
		return new AndWithTemplateEntity(this, templateEntity);
	}

	
}
