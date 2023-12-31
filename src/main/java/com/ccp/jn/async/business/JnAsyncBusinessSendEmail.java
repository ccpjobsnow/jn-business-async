package com.ccp.jn.async.business;

import java.util.List;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.dao.CcpDao;
import com.ccp.especifications.db.utils.CcpEntityOperationType;
import com.ccp.especifications.email.CcpEmailSender;
import com.ccp.jn.async.commons.utils.JnHttpRequestType;
import com.jn.commons.entities.JnEntityEmailMessageSent;
import com.jn.commons.entities.JnEntityEmailReportedAsSpam;

public class JnAsyncBusinessSendEmail implements  java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private CcpEmailSender emailSender = CcpDependencyInjection.getDependency(CcpEmailSender.class);

	private CcpDao dao = CcpDependencyInjection.getDependency(CcpDao.class);
	
	private JnAsyncBusinessCommitAndAudit commitAndAudit = new JnAsyncBusinessCommitAndAudit();

	private JnAsyncBusinessSendHttpRequest sendHttpRequest = new JnAsyncBusinessSendHttpRequest();

	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {
		
		List<String> onlyEmailsNotSentAndNotRepportedAsSpam = this.getOnlyEmailsNotSentAndNotRepportedAsSpam(values);
		
		if(onlyEmailsNotSentAndNotRepportedAsSpam.isEmpty()) {
			return values;
		}
		List<String> list = values.getAsStringList("emails", "email");
		
		this.sendHttpRequest.execute(values, x -> this.emailSender.send(x),JnHttpRequestType.email, "subjectType");
		
		List<CcpJsonRepresentation> records = list.stream().map(email -> values.put("email", email)).collect(Collectors.toList());
		
		this.commitAndAudit.execute(records, CcpEntityOperationType.create, new JnEntityEmailMessageSent());
		
		return values;
	}

	private List<String> getOnlyEmailsNotSentAndNotRepportedAsSpam(CcpJsonRepresentation values) {
		List<String> emails = values.getAsStringList("emails", "email");
		
		List<CcpJsonRepresentation> objects = emails.stream().map(email -> values.put("email", email)).collect(Collectors.toList());
		JnEntityEmailMessageSent jnEntityEmailMessageSent = new JnEntityEmailMessageSent();
		JnEntityEmailReportedAsSpam jnEntityEmailReportedAsSpam = new JnEntityEmailReportedAsSpam();
		List<CcpJsonRepresentation> manyById = this.dao.getManyById(objects, jnEntityEmailMessageSent, jnEntityEmailReportedAsSpam);
		List<String> collect = manyById.stream().map(x -> x.getAsString("email")).collect(Collectors.toList());
		List<String> onlyEmailsNotSentAndNotRepportedAsSpam = emails.stream().filter(x -> collect.contains(x) == false).collect(Collectors.toList());
		return onlyEmailsNotSentAndNotRepportedAsSpam;
	}
}
