package com.ccp.jn.async.messages;

import com.ccp.especifications.db.utils.CcpEntity;

public class AndWithParametersEntity {

	final WithProcess withProcess;

	final CcpEntity parametersEntity;

	AndWithParametersEntity(WithProcess withProcess, CcpEntity parametersEntity) {
		this.withProcess = withProcess;
		this.parametersEntity = parametersEntity;
	}
	
	public AndWithTemplateEntity andWithTemplateEntity(CcpEntity templateEntity) {
		return new AndWithTemplateEntity(this, templateEntity);
	}

	
}
