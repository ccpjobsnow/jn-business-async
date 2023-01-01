package com.ccp.jn.async.business;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.jn.async.AsyncServices;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.JnBusinessTopic;

public class NotifyContactUs implements CcpProcess{

	CcpMapDecorator idToSearch = new CcpMapDecorator().put("name", JnBusinessTopic.notifyContactUs.name());
	
	@CcpDependencyInject
	private SendInstantMessage sendInstantMessage = AsyncServices.catalog.getAsObject(JnBusinessTopic.sendInstantMessage.name());

	@CcpDependencyInject
	private SendEmail sendEmail = AsyncServices.catalog.getAsObject(JnBusinessTopic.sendEmail.name());

	public CcpMapDecorator execute(CcpMapDecorator values) {
		
		boolean jaFoiCadastado = JnBusinessEntity.contact_us.exists(values);
		
		if(jaFoiCadastado) {
			return values;
		}

		CcpMapDecorator contactUsConfigurations = JnBusinessEntity._static.get(this.idToSearch, CcpConstants.DO_NOTHING);
		
		CcpMapDecorator contactUsParameters = contactUsConfigurations.getInternalMap("value");
		
		CcpMapDecorator dadosDoUsuario = values.getSubMap("subject", "subjectType", "message", "email");

		CcpMapDecorator dadosDoSuporte = contactUsParameters.getSubMap("emailTo", "chatId", "botToken");
		
		CcpMapDecorator todosOsDadosNecessarios = dadosDoUsuario.putAll(dadosDoSuporte);
	
		this.sendInstantMessage.execute(todosOsDadosNecessarios);

		this.sendEmail.execute(todosOsDadosNecessarios);
		
		JnBusinessEntity.contact_us.save(todosOsDadosNecessarios);
		
		return values;
	}

}
