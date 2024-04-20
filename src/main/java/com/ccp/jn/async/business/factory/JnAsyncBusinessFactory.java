package com.ccp.jn.async.business.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.async.business.factory.CcpAsyncBusinessFactory;
import com.ccp.jn.async.business.JnAsyncBusinessNotifyContactUs;
import com.ccp.jn.async.business.JnAsyncBusinessNotifyError;
import com.ccp.jn.async.business.JnAsyncBusinessRemoveTries;
import com.ccp.jn.async.business.JnAsyncBusinessRequestTokenAgain;
import com.ccp.jn.async.business.JnAsyncBusinessRequestUnlockToken;
import com.ccp.jn.async.business.JnAsyncBusinessSendUserToken;
import com.jn.commons.utils.JnTopics;

class JnAsyncBusinessFactory implements CcpAsyncBusinessFactory {
	
	
	private Map<String, Function<CcpJsonRepresentation, CcpJsonRepresentation>> map = new HashMap<>();
	
	public JnAsyncBusinessFactory() {
		this.map.put(JnTopics.requestUnlockToken.name(), new JnAsyncBusinessRequestUnlockToken());
		this.map.put(JnTopics.requestTokenAgain.name(), new JnAsyncBusinessRequestTokenAgain());
		this.map.put(JnTopics.notifyContactUs.name(), new JnAsyncBusinessNotifyContactUs());
		this.map.put(JnTopics.sendUserToken.name(), new JnAsyncBusinessSendUserToken());
		this.map.put(JnTopics.notifyError.name(), new JnAsyncBusinessNotifyError());
		this.map.put(JnTopics.removeTries.name(), new JnAsyncBusinessRemoveTries());
	}
	
	@Override
	public Function<CcpJsonRepresentation, CcpJsonRepresentation> getAsyncBusiness(String processName) {
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> function = this.map.get(processName);
		
		if(function == null) {
			throw new RuntimeException("The topic '" + processName + "' does not exist");
		}
		
		return function;
	}

}
