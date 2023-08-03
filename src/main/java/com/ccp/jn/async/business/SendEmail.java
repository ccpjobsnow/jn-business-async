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

	public CcpMapDecorator apply(CcpMapDecorator values) {
		
		List<String> onlyEmailsNotSentAndNotRepportedAsSpam = this.getOnlyEmailsNotSentAndNotRepportedAsSpam(values);
		
		CcpMapDecorator put = values.put("emails", onlyEmailsNotSentAndNotRepportedAsSpam);
		
		CcpMapDecorator putAll = values.putAll(put);

		this.sendHttpRequest.execute(putAll, x -> this.emailSender.send(x.renameKey("emailMessage", "message")));
		
		List<String> emails = putAll.getAsStringList("emails");
		
		List<CcpMapDecorator> records = emails.stream().map(email -> putAll.put("email", email)).collect(Collectors.toList());
		
		this.commitAndAudit.execute(records, CcpOperationType.create, JnEntity.email_message_sent);
		
		return values;
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
