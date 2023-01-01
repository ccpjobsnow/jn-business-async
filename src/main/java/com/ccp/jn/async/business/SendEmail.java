package com.ccp.jn.async.business;

import java.util.List;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpConstants;
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

	CcpMapDecorator idToSearch = new CcpMapDecorator().put("name", JnBusinessTopic.sendEmail.name());
	
	
	public CcpMapDecorator execute(CcpMapDecorator values) {
		
		List<String> emails = values.getAsStringList("emails");
		
		List<String> apenasEmailsPermitidos = emails.stream().filter(email -> this.canMeSendEmailToThisAddress(values.put("email", email)) == false).collect(Collectors.toList());
		
		if(apenasEmailsPermitidos.isEmpty()) {
			return values;
		}
		
		CcpMapDecorator emailParameters = JnBusinessEntity._static.get(this.idToSearch, CcpConstants.DO_NOTHING);
		
		emailParameters = emailParameters.put("emails", apenasEmailsPermitidos);
		
		try {
			return this.tryToSendEmail(values, emailParameters);

		} catch (ThereWasClientError e) {
		
			return this.saveClientError(values, emailParameters);
		
		}catch(EmailApiIsUnavailable e) {
			
			return this.tryToSendEmailAgain(values, emailParameters);
	
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


	private CcpMapDecorator tryToSendEmailAgain(CcpMapDecorator values, CcpMapDecorator parametersToSendEmail) {
		
		Utils.sleep(5000);
		
		CcpMapDecorator putAll = values.putAll(parametersToSendEmail);
		
		boolean exceededTries = JnBusinessEntity.email_try_to_send_message.exceededTries(putAll, "tries", 3);
		
		if(exceededTries) {
			JnBusinessEntity.email_api_unavailable.save(putAll);
			return values;
		}
		
		return this.execute(values);
	}

	
	private boolean canMeSendEmailToThisAddress(CcpMapDecorator values) {
		
		boolean jaFoiEnviado = JnBusinessEntity.email_message_sent.exists(values);
		
		if(jaFoiEnviado) {
			return false;
		}
		
		boolean reportouComoSpam = JnBusinessEntity.email_reported_as_spam.exists(values);
		
		if(reportouComoSpam) {
			return false;
		}
		
		return true;
	}
}
