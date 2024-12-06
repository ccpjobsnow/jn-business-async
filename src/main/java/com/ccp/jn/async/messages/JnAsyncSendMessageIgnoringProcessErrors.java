package com.ccp.jn.async.messages;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.jn.async.commons.JnAsyncNotifySupport;
import com.jn.commons.entities.JnEntityJobsnowWarning;
import com.jn.commons.utils.JnAsyncBusiness;

public class JnAsyncSendMessageIgnoringProcessErrors extends JnAsyncSendMessage{

	
	public JnAsyncSendMessage addOneStep(Function<CcpJsonRepresentation, CcpJsonRepresentation> step, CcpEntity parameterEntity, CcpEntity messageEntity) {
		Function<CcpJsonRepresentation, CcpJsonRepresentation> lenientProcess = values -> {
			try {
				CcpJsonRepresentation apply = step.apply(values);
				return apply;
			} catch (Exception e) {
				CcpJsonRepresentation errorDetails = new CcpJsonRepresentation(e);
				String name = JnAsyncBusiness.notifyError.name();
				JnAsyncSendMessage x = new JnAsyncSendMessageAndJustErrors();
				JnAsyncNotifySupport.INSTANCE.apply1(errorDetails, name, JnEntityJobsnowWarning.ENTITY, x);
				return values;
			}
		};
		JnAsyncSendMessage addFlow = super.addOneStep(lenientProcess, parameterEntity, messageEntity);
		return addFlow;
	}	
}
