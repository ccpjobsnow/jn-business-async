package com.ccp.jn.async.business.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.async.business.factory.CcpAsyncBusinessFactory;
import com.ccp.jn.async.business.commons.JnAsyncBusinessSendEmailMessage;
import com.ccp.jn.async.business.commons.JnAsyncBusinessTryToSendInstantMessage;
import com.ccp.jn.async.business.login.JnAsyncBusinessExecuteLogin;
import com.ccp.jn.async.business.login.JnAsyncBusinessExecuteLogout;
import com.ccp.jn.async.business.login.JnAsyncBusinessLockPassword;
import com.ccp.jn.async.business.login.JnAsyncBusinessLockToken;
import com.ccp.jn.async.business.login.JnAsyncBusinessSendUserToken;
import com.ccp.jn.async.business.login.JnAsyncBusinessUpdatePassword;
import com.ccp.jn.async.business.support.JnAsyncBusinessNotifyContactUs;
import com.ccp.jn.async.business.support.JnAsyncBusinessNotifyError;
import com.jn.commons.utils.JnAsyncBusiness;

class JnAsyncBusinessFactory implements CcpAsyncBusinessFactory {

	private Map<String, Function<CcpJsonRepresentation, CcpJsonRepresentation>> map = new HashMap<>();

	public static final JnAsyncBusinessFactory INSTANCE = new JnAsyncBusinessFactory();
	
	private JnAsyncBusinessFactory() {
		this.map.put(JnAsyncBusiness.sendInstantMessage.name(), JnAsyncBusinessTryToSendInstantMessage.INSTANCE);
		this.map.put(JnAsyncBusiness.sendEmailMessage.name(), JnAsyncBusinessSendEmailMessage.INSTANCE);
		this.map.put(JnAsyncBusiness.notifyContactUs.name(), JnAsyncBusinessNotifyContactUs.INSTANCE);
		this.map.put(JnAsyncBusiness.updatePassword.name(), JnAsyncBusinessUpdatePassword.INSTANCE);
		this.map.put(JnAsyncBusiness.sendUserToken.name(), JnAsyncBusinessSendUserToken.INSTANCE);
		this.map.put(JnAsyncBusiness.executeLogout.name(), JnAsyncBusinessExecuteLogout.INSTANCE);
		this.map.put(JnAsyncBusiness.executeLogin.name(), JnAsyncBusinessExecuteLogin.INSTANCE);
		this.map.put(JnAsyncBusiness.lockPassword.name(), JnAsyncBusinessLockPassword.INSTANCE);
		this.map.put(JnAsyncBusiness.notifyError.name(), JnAsyncBusinessNotifyError.INSTANCE);
		this.map.put(JnAsyncBusiness.lockToken.name(), JnAsyncBusinessLockToken.INSTANCE);


	}
	
	public Map<String, Function<CcpJsonRepresentation, CcpJsonRepresentation>> getMap() {
		return this.map;
	}


}
