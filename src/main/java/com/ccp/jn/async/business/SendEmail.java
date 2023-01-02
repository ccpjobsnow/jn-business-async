package com.ccp.jn.async.business;

import java.util.List;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.email.CcpEmailSender;
import com.ccp.especifications.email.CcpEmailSender.EmailApiIsUnavailable;
import com.ccp.especifications.email.CcpEmailSender.ThereWasClientError;
import com.ccp.process.CcpProcess;
import com.ccp.utils.Utils;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.JnBusinessTopic;

public class SendEmail implements CcpProcess{

	@CcpDependencyInject
	private CcpEmailSender emailSender;

	private CcpMapDecorator idToSearch = new CcpMapDecorator().put("name", JnBusinessTopic.sendEmail.name());
	
	
	public CcpMapDecorator execute(CcpMapDecorator values) {
		
		List<String> emails = values.getAsStringList("emails");
		
		CcpMapDecorator emailParameters = JnBusinessEntity._static.get(this.idToSearch);

		List<String> apenasEmailsPermitidos = emails.stream().filter(email -> this.canSendEmail(values, emailParameters, email)).collect(Collectors.toList());
		
		if(apenasEmailsPermitidos.isEmpty()) {
			return values;
		}
		
		CcpMapDecorator put = emailParameters.put("emails", apenasEmailsPermitidos);
		
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

		JnBusinessEntity.email_try_to_send_message.removeTries(putAll, "tries", 3);
		
		List<String> emails = putAll.getAsStringList("emails");
		
		for (String email : emails) {
			JnBusinessEntity.email_message_sent.save(putAll.put("email", email));
		}
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

		boolean jaFoiEnviado = JnBusinessEntity.email_message_sent.exists(putAll);
		
		if(jaFoiEnviado) {
			return false;
		}
		
		boolean reportouComoSpam = JnBusinessEntity.email_reported_as_spam.exists(putAll);
		
		if(reportouComoSpam) {
			return false;
		}
		
		return true;
	}
}
