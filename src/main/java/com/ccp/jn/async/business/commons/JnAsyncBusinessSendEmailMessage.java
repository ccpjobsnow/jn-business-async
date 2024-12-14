package com.ccp.jn.async.business.commons;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.crud.CcpCrud;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.email.CcpEmailSender;
import com.ccp.jn.async.commons.JnAsyncHttpRequestType;
import com.ccp.jn.async.commons.JnAsyncSendHttpRequest;
import com.jn.commons.entities.JnEntityEmailMessageSent;
import com.jn.commons.entities.JnEntityEmailReportedAsSpam;
import com.jn.commons.utils.JnDeleteKeysFromCache;


public class JnAsyncBusinessSendEmailMessage implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	public static final JnAsyncBusinessSendEmailMessage INSTANCE = new JnAsyncBusinessSendEmailMessage();
	
	private JnAsyncBusinessSendEmailMessage() {
		
	}

	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		
		CcpEmailSender emailSender = CcpDependencyInjection.getDependency(CcpEmailSender.class);
		
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		
		CcpSelectUnionAll unionAll = crud.unionAll(json, JnDeleteKeysFromCache.INSTANCE, JnEntityEmailMessageSent.ENTITY, JnEntityEmailReportedAsSpam.ENTITY);
		
		boolean emailAlreadySent = JnEntityEmailMessageSent.ENTITY.isPresentInThisUnionAll(unionAll, json);
		
		if(emailAlreadySent) {
			return json;
		}

		boolean emailReportedAsSpam = JnEntityEmailReportedAsSpam.ENTITY.isPresentInThisUnionAll(unionAll, json);
		
		if(emailReportedAsSpam) {
			//DOUBT alerta de envio de e-mail pra alguem que reportou como spam
			return json;
		}
		
		JnAsyncSendHttpRequest.INSTANCE.execute(json, x -> emailSender.send(x),JnAsyncHttpRequestType.email, "subjectType");
		JnEntityEmailMessageSent.ENTITY.createOrUpdate(json);
		return json;
	}

}
