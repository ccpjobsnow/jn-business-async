package com.ccp.jn.async.messages;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntity;
import com.jn.commons.entities.JnEntityJobsnowWarning;

public class JnAsyncSendMessageAndJustErrors extends JnAsyncSendMessage{

	
	public JnAsyncSendMessage addOneStep(Function<CcpJsonRepresentation, CcpJsonRepresentation> step, CcpEntity parameterEntity, CcpEntity messageEntity) {
		Function<CcpJsonRepresentation, CcpJsonRepresentation> process = values -> {
			try {
				CcpJsonRepresentation apply = step.apply(values);
				return apply;
			} catch (Exception e) {
				CcpJsonRepresentation errorDetails = new CcpJsonRepresentation(e);
				JnEntityJobsnowWarning.ENTITY.createOrUpdate(errorDetails);
				e.printStackTrace();
				return values;
			}
		};
		JnAsyncSendMessage addFlow = super.addOneStep(process, parameterEntity, messageEntity);
		return addFlow;
	}	
}
