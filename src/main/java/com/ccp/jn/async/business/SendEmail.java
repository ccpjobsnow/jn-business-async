package com.ccp.jn.async.business;

import java.util.List;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.crud.CcpDao;
import com.ccp.especifications.db.utils.CcpOperationType;
import com.ccp.especifications.email.CcpEmailSender;
import com.ccp.exceptions.email.EmailApiIsUnavailable;
import com.ccp.exceptions.email.EmailMessageNotSent;
import com.ccp.exceptions.email.ExceededTriesToSentMailMessage;
import com.ccp.exceptions.email.ThereWasClientError;
import com.ccp.jn.async.commons.others.CommitAndAudit;
import com.ccp.jn.async.commons.others.MessagesTranslation;
import com.ccp.process.CcpProcess;
import com.ccp.utils.Utils;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class SendEmail implements CcpProcess{

	@CcpDependencyInject
	private CcpEmailSender emailSender;

	@CcpDependencyInject
	private CcpDao crud;
	
	private CommitAndAudit commitAndAudit = CcpDependencyInjection.getInjected(CommitAndAudit.class);

	private RemoveTries removeTries = CcpDependencyInjection.getInjected(RemoveTries.class);

	private MessagesTranslation messagesTranslation = CcpDependencyInjection.getInjected(MessagesTranslation.class);

	
	public CcpMapDecorator execute(CcpMapDecorator values) {
		
		List<String> emails = values.getAsStringList("emails", "email");
		
		String language = values.getAsString("language");
		
		CcpMapDecorator emailParameters = this.messagesTranslation.getMergedParameters(JnTopic.sendEmail, values, language, "subject", "sender", "subjectType");
		//TODO TRAZER TODOS OS E-MAILS DE UMA SÓ VEZ
		List<String> justEmailsThatWasNotRepportedAsSpamAndNotAlreadySent = emails.stream().filter(email -> 
				this.crud.noMatches(values.putAll(emailParameters).put("email", email), JnEntity.email_message_sent, JnEntity.email_reported_as_spam)
		).collect(Collectors.toList());
		
		if(justEmailsThatWasNotRepportedAsSpamAndNotAlreadySent.isEmpty()) {
			throw new EmailMessageNotSent();
		}
		
		CcpMapDecorator put = emailParameters.put("emails", justEmailsThatWasNotRepportedAsSpamAndNotAlreadySent);
		
		try {
			return this.tryToSendEmail(values, put);

		} catch (ThereWasClientError e) {
		
			this.saveClientError(values, put);
			throw e;
		
		}catch(EmailApiIsUnavailable e) {
			
			return this.retryToSendEmail(values, put);
		}	
	}

	private CcpMapDecorator tryToSendEmail(CcpMapDecorator values, CcpMapDecorator parametersToSendEmail) {

		CcpMapDecorator putAll = values.putAll(parametersToSendEmail);

		this.emailSender.send(putAll);

		this.removeTries.execute(putAll, "tries", 3, JnEntity.email_try_to_send_message);
		
		List<String> emails = putAll.getAsStringList("emails");
		
		List<CcpMapDecorator> records = emails.stream().map(email -> putAll.put("email", email)).collect(Collectors.toList());
		
		this.commitAndAudit.execute(records, CcpOperationType.create, JnEntity.email_message_sent);

		return values;
	}


	private CcpMapDecorator saveClientError(CcpMapDecorator values, CcpMapDecorator parametersToSendEmail) {
		CcpMapDecorator putAll = values.putAll(parametersToSendEmail);
		JnEntity.email_api_client_error.createOrUpdate(putAll);
		return values;
	}


	private CcpMapDecorator retryToSendEmail(CcpMapDecorator values, CcpMapDecorator parametersToSendEmail) {
		
		Utils.sleep(5000);
		
		CcpMapDecorator putAll = values.putAll(parametersToSendEmail);
		
		boolean exceededTries = JnEntity.email_try_to_send_message.exceededTries(putAll, "tries", 3);
		
		if(exceededTries) {
			JnEntity.email_api_unavailable.createOrUpdate(putAll);
			throw new ExceededTriesToSentMailMessage();
		}
		
		return this.execute(values);
	}

	
}
