package com.ccp.jn.async.messages;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.jn.async.commons.JnAsyncNotifySupport;
import com.jn.commons.entities.JnEntityJobsnowWarning;
import com.jn.commons.utils.JnAsyncBusiness;

public class JnAsyncUtilsLenientGetMessage extends JnAsyncUtilsGetMessage{

	
	public JnAsyncUtilsGetMessage addOneStep(Function<CcpJsonRepresentation, CcpJsonRepresentation> step, CcpEntity parameterEntity, CcpEntity messageEntity) {
		Function<CcpJsonRepresentation, CcpJsonRepresentation> lenientProcess = values -> {
			try {
				CcpJsonRepresentation apply = step.apply(values);
				return apply;
			} catch (Exception e) {
				CcpJsonRepresentation errorDetails = new CcpJsonRepresentation(e);
				String name = JnAsyncBusiness.notifyError.name();
				JnAsyncUtilsGetMessage x = new JnAsyncUtilsJustLogGetMessage();
				JnAsyncNotifySupport.INSTANCE.apply(errorDetails, name, JnEntityJobsnowWarning.INSTANCE, x);
				return values;
			}
		};
		JnAsyncUtilsGetMessage addFlow = super.addOneStep(lenientProcess, parameterEntity, messageEntity);
		return addFlow;
	}	
}
