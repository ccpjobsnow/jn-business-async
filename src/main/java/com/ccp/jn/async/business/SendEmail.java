package com.ccp.jn.async.business;

import java.util.List;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.dao.CcpDao;
import com.ccp.especifications.db.utils.CcpOperationType;
import com.ccp.especifications.email.CcpEmailSender;
import com.ccp.jn.async.commons.others.CommitAndAudit;

import com.jn.commons.JnEntity;

public class SendEmail implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator>{

	@CcpDependencyInject
	private CcpEmailSender emailSender;

	@CcpDependencyInject
	private CcpDao dao;
	
	private CommitAndAudit commitAndAudit = CcpDependencyInjection.getInjected(CommitAndAudit.class);

	private SendHttpRequest sendHttpRequest = CcpDependencyInjection.getInjected(SendHttpRequest.class);

	public CcpMapDecorator apply(CcpMapDecorator values1) {
		
		CcpMapDecorator emailApiParameters = values1.getInternalMap("_parametersToSendMailMessage");
		
		CcpMapDecorator parametersToSendEmail = values1.putAll(emailApiParameters);
		
		List<String> onlyEmailsNotSentAndNotRepportedAsSpam = this.getOnlyEmailsNotSentAndNotRepportedAsSpam(parametersToSendEmail);
		
		if(onlyEmailsNotSentAndNotRepportedAsSpam.isEmpty()) {
			return parametersToSendEmail;
		}
		List<String> list = parametersToSendEmail.getAsStringList("emails", "email");
		
		this.sendHttpRequest.execute(parametersToSendEmail, x -> this.emailSender.send(parametersToSendEmail),JnHttpRequestType.email, "subjectType");
		
		List<CcpMapDecorator> records = list.stream().map(email -> parametersToSendEmail.put("email", email)).collect(Collectors.toList());
		
		this.commitAndAudit.execute(records, CcpOperationType.create, JnEntity.email_message_sent);
		
		return parametersToSendEmail;
	}

	private List<String> getOnlyEmailsNotSentAndNotRepportedAsSpam(CcpMapDecorator values) {
		List<String> emails = values.getAsStringList("emails", "email");
		
		List<CcpMapDecorator> objects = emails.stream().map(email -> values.put("email", email)).collect(Collectors.toList());
		List<CcpMapDecorator> manyById = this.dao.getManyById(objects, JnEntity.email_message_sent, JnEntity.email_reported_as_spam);
		List<String> collect = manyById.stream().map(x -> x.getAsString("email")).collect(Collectors.toList());
		List<String> onlyEmailsNotSentAndNotRepportedAsSpam = emails.stream().filter(x -> collect.contains(x) == false).collect(Collectors.toList());
		return onlyEmailsNotSentAndNotRepportedAsSpam;
	}
}
