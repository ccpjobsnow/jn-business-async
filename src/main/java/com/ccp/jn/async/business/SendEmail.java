package com.ccp.jn.async.business;

import java.util.List;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.dao.CcpDao;
import com.ccp.especifications.db.utils.CcpOperationType;
import com.ccp.especifications.email.CcpEmailSender;
import com.ccp.exceptions.email.ExceededTriesToSentMailMessage;
import com.ccp.exceptions.http.CcpHttpClientError;
import com.ccp.exceptions.http.CcpHttpError;
import com.ccp.exceptions.http.CcpHttpInternalServerError;
import com.ccp.jn.async.commons.others.CommitAndAudit;
import com.ccp.process.CcpProcess;
import com.ccp.utils.Utils;
import com.jn.commons.JnEntity;

public class SendEmail implements CcpProcess{

	@CcpDependencyInject
	private CcpEmailSender emailSender;

	@CcpDependencyInject
	private CcpDao dao;
	
	private CommitAndAudit commitAndAudit = CcpDependencyInjection.getInjected(CommitAndAudit.class);

	private RemoveTries removeTries = CcpDependencyInjection.getInjected(RemoveTries.class);

	public CcpMapDecorator execute(CcpMapDecorator values) {
		
		List<String> onlyEmailsNotSentAndNotRepportedAsSpam = this.getOnlyEmailsNotSentAndNotRepportedAsSpam(values);
		
		CcpMapDecorator put = values.put("emails", onlyEmailsNotSentAndNotRepportedAsSpam);
		
		try {
			return this.tryToSendEmail(values, put);

		} catch (CcpHttpClientError e) {
			CcpMapDecorator asEntity = e.entity;
			JnEntity.email_api_client_error.createOrUpdate(asEntity);
			throw e;
		
		}catch(CcpHttpInternalServerError e) {
			
			return this.retryToSendEmail(values, e);
		}	
	}

	private List<String> getOnlyEmailsNotSentAndNotRepportedAsSpam(CcpMapDecorator values) {
		List<String> emails = values.getAsStringList("emails", "email");
		
		List<CcpMapDecorator> objects = emails.stream().map(email -> values.put("email", email)).collect(Collectors.toList());
		List<CcpMapDecorator> manyById = this.dao.getManyById(objects, JnEntity.email_message_sent, JnEntity.email_reported_as_spam);
		List<String> collect = manyById.stream().map(x -> x.getAsString("email")).collect(Collectors.toList());
		List<String> onlyEmailsNotSentAndNotRepportedAsSpam = emails.stream().filter(x -> collect.contains(x) == false).collect(Collectors.toList());
		return onlyEmailsNotSentAndNotRepportedAsSpam;
	}

	private CcpMapDecorator tryToSendEmail(CcpMapDecorator values, CcpMapDecorator parametersToSendEmail) {

		CcpMapDecorator putAll = values.putAll(parametersToSendEmail);

		CcpMapDecorator entity = this.emailSender.send(putAll.renameKey("emailMessage", "message"));

		this.removeTries.execute(entity, "tries", 3, JnEntity.email_try_to_send_message);

		List<String> emails = putAll.getAsStringList("emails");
		
		List<CcpMapDecorator> records = emails.stream().map(email -> putAll.put("email", email)).collect(Collectors.toList());
		
		this.commitAndAudit.execute(records, CcpOperationType.create, JnEntity.email_message_sent);
		
		return values;
	}

	private CcpMapDecorator retryToSendEmail(CcpMapDecorator values, CcpHttpError e) {
		
		Utils.sleep(5000);
		
		boolean exceededTries = JnEntity.email_try_to_send_message.exceededTries(e.entity, "tries", 3);
		
		if(exceededTries) {
			JnEntity.email_api_unavailable.createOrUpdate(e.entity);
			throw new ExceededTriesToSentMailMessage();
		}
		
		CcpMapDecorator execute = this.execute(values);
		return execute;
	}

	
}
