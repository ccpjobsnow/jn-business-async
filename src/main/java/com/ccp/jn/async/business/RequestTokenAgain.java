package com.ccp.jn.async.business;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpMapDecorator;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.decorators.CcpTextDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.jn.async.AsyncServices;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.JnBusinessTopic;

public class RequestTokenAgain implements CcpProcess{

	@CcpDependencyInject
	private NotifyContactUs notifyContactUs = AsyncServices.catalog.getAsObject(JnBusinessTopic.requestTokenAgain.name());
	
	private CcpMapDecorator idToSearch = new CcpMapDecorator().put("name", JnBusinessTopic.requestTokenAgain.name());

	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {
		
		CcpTextDecorator textDecorator = new CcpStringDecorator(CcpConstants.CHARACTERS_TO_GENERATE_TOKEN).text();
		
		String token = textDecorator.generateToken(8);
		
		values = values.put("token", token);

		CcpMapDecorator parameters = JnBusinessEntity._static.get(this.idToSearch);
	
		parameters = values.putAll(parameters);
		
		String emailText = parameters.getFilledTemplate("emailTemplate");
		
		parameters = parameters.put("message", emailText);
		
		JnBusinessEntity.login_request.resetData(parameters);

		this.notifyContactUs.execute(parameters);
		
		JnBusinessEntity.request_token_again.save(parameters);
		
		
		return values;
	}

}
