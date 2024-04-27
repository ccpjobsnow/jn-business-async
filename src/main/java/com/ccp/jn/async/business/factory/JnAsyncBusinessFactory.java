package com.ccp.jn.async.business.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.async.business.factory.CcpAsyncBusinessFactory;
import com.ccp.jn.async.business.balance.JnAsyncBusinessGrouperBalance;
import com.ccp.jn.async.business.commons.JnAsyncBusinessRemoveTries;
import com.ccp.jn.async.business.commons.JnAsyncBusinessSendEmailMessage;
import com.ccp.jn.async.business.commons.JnAsyncBusinessTryToSendInstantMessage;
import com.ccp.jn.async.business.login.JnAsyncBusinessGrouperLogin;
import com.ccp.jn.async.business.login.JnAsyncBusinessRequestTokenAgain;
import com.ccp.jn.async.business.login.JnAsyncBusinessRequestUnlockToken;
import com.ccp.jn.async.business.login.JnAsyncBusinessSendUserToken;
import com.ccp.jn.async.business.support.JnAsyncBusinessGrouperSupport;
import com.ccp.jn.async.business.support.JnAsyncBusinessNotifyContactUs;
import com.ccp.jn.async.business.support.JnAsyncBusinessNotifyError;
import com.jn.commons.utils.JnAsyncBusiness;

class JnAsyncBusinessFactory implements CcpAsyncBusinessFactory {

	private Map<String, Function<CcpJsonRepresentation, CcpJsonRepresentation>> map = new HashMap<>();
	public static final JnAsyncBusinessFactory INSTANCE = new JnAsyncBusinessFactory();
	private JnAsyncBusinessFactory() {
		this.map.put(JnAsyncBusiness.sendInstantMessage.name(), JnAsyncBusinessTryToSendInstantMessage.INSTANCE);
		this.map.put(JnAsyncBusiness.requestUnlockToken.name(), JnAsyncBusinessRequestUnlockToken.INSTANCE);
		this.map.put(JnAsyncBusiness.requestTokenAgain.name(), JnAsyncBusinessRequestTokenAgain.INSTANCE);
		this.map.put(JnAsyncBusiness.sendEmailMessage.name(), JnAsyncBusinessSendEmailMessage.INSTANCE);
		this.map.put(JnAsyncBusiness.notifyContactUs.name(), JnAsyncBusinessNotifyContactUs.INSTANCE);
		this.map.put(JnAsyncBusiness.grouperBalance.name(), JnAsyncBusinessGrouperBalance.INSTANCE);
		this.map.put(JnAsyncBusiness.grouperSupport.name(), JnAsyncBusinessGrouperSupport.INSTANCE);
		this.map.put(JnAsyncBusiness.sendUserToken.name(), JnAsyncBusinessSendUserToken.INSTANCE);
		this.map.put(JnAsyncBusiness.grouperLogin.name(), JnAsyncBusinessGrouperLogin.INSTANCE);
		this.map.put(JnAsyncBusiness.notifyError.name(), JnAsyncBusinessNotifyError.INSTANCE);
		this.map.put(JnAsyncBusiness.removeTries.name(), JnAsyncBusinessRemoveTries.INSTANCE);
	}
	
	public Map<String, Function<CcpJsonRepresentation, CcpJsonRepresentation>> getMap() {
		return this.map;
	}


}
