package com.ccp.jn.async.business;

import java.util.List;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.bulk.CcpDbBulkExecutor;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.email.CcpEmailSender;
import com.ccp.especifications.email.CcpEmailSender.EmailApiIsUnavailable;
import com.ccp.especifications.email.CcpEmailSender.ThereWasClientError;
import com.ccp.exceptions.commons.CcpFlow;
import com.ccp.process.CcpProcess;
import com.ccp.utils.Utils;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.JnBusinessTopic;

public class SendEmail implements CcpProcess{

	@CcpDependencyInject
	private CcpEmailSender emailSender;

	@CcpDependencyInject
	private CcpDbCrud crud;
	
	@CcpDependencyInject
	private CcpDbBulkExecutor dbBulkExecutor;

	private RemoveTries removeTries = CcpDependencyInjection.getInjected(RemoveTries.class);

	private MessagesTranslation messagesTranslation = CcpDependencyInjection.getInjected(MessagesTranslation.class);

	
	public CcpMapDecorator execute(CcpMapDecorator values) {
		
		List<String> emails = values.getAsStringList("emails", "email");
		
		String language = values.getAsString("language");
		
		CcpMapDecorator emailParameters = this.messagesTranslation.getMergedParameters(JnBusinessTopic.sendEmail, values, language, "subject", "sender", "subjectType");
		
		List<String> justEmailsThatWasNotRepportedAsSpamAndNotAlreadySent = emails.stream().filter(email -> this.canSendEmail(values, emailParameters, email)).collect(Collectors.toList());
		
		if(justEmailsThatWasNotRepportedAsSpamAndNotAlreadySent.isEmpty()) {
			return values;
		}
		
		CcpMapDecorator put = emailParameters.put("emails", justEmailsThatWasNotRepportedAsSpamAndNotAlreadySent);
		
		try {
			return this.tryToSendEmail(values, put);

		} catch (ThereWasClientError e) {
		
			return this.saveClientError(values, put);
		
		}catch(EmailApiIsUnavailable e) {
			
			return this.retryToSendEmail(values, put);
	
		}	
	}

	private CcpMapDecorator tryToSendEmail(CcpMapDecorator values, CcpMapDecorator parametersToSendEmail) {

		CcpMapDecorator putAll = values.putAll(parametersToSendEmail);

		this.emailSender.send(putAll);

		this.removeTries.execute(putAll, "tries", 3, JnBusinessEntity.email_try_to_send_message);
		
		List<String> emails = putAll.getAsStringList("emails");
		
		List<CcpMapDecorator> records = emails.stream().map(email -> putAll.put("email", email)).collect(Collectors.toList());
		
		this.dbBulkExecutor.commit(records, "create", JnBusinessEntity.email_message_sent);

		return values;
	}


	private CcpMapDecorator saveClientError(CcpMapDecorator values, CcpMapDecorator parametersToSendEmail) {
		CcpMapDecorator putAll = values.putAll(parametersToSendEmail);
		JnBusinessEntity.email_api_client_error.save(putAll);
		return values;
	}


	private CcpMapDecorator retryToSendEmail(CcpMapDecorator values, CcpMapDecorator parametersToSendEmail) {
		
		Utils.sleep(5000);
		
		CcpMapDecorator putAll = values.putAll(parametersToSendEmail);
		
		boolean exceededTries = JnBusinessEntity.email_try_to_send_message.exceededTries(putAll, "tries", 3);
		
		if(exceededTries) {
			JnBusinessEntity.email_api_unavailable.save(putAll);
			return values;
		}
		
		return this.execute(values);
	}

	
	private boolean canSendEmail(CcpMapDecorator values, CcpMapDecorator parametersToSendEmail, String email) {
		
		CcpMapDecorator putAll = values.putAll(parametersToSendEmail).put("email", email);

		try {
			this.crud
			.useThisId(putAll)
			.toBeginProcedureAnd()
			.ifThisIdIsPresentInTable(JnBusinessEntity.email_reported_as_spam).returnStatus(409).and()
			.ifThisIdIsPresentInTable(JnBusinessEntity.email_message_sent).returnStatus(409)
			.andFinally()
			.endThisProcedure()
			;
			return true;
		} catch (CcpFlow e) {
			return false;
		}
	}
}
