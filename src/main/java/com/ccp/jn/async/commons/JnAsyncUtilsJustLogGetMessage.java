package com.ccp.jn.async.commons;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntity;
import com.jn.commons.entities.JnEntityJobsnowWarning;

public class JnAsyncUtilsJustLogGetMessage extends JnAsyncUtilsGetMessage{

	
	public JnAsyncUtilsGetMessage addOneStep(Function<CcpJsonRepresentation, CcpJsonRepresentation> step, CcpEntity parameterEntity, CcpEntity messageEntity) {
		Function<CcpJsonRepresentation, CcpJsonRepresentation> process = values -> {
			try {
				CcpJsonRepresentation apply = step.apply(values);
				return apply;
			} catch (Exception e) {
				CcpJsonRepresentation errorDetails = new CcpJsonRepresentation(e);
				JnEntityJobsnowWarning.INSTANCE.createOrUpdate(errorDetails);
				e.printStackTrace();
				return values;
			}
		};
		JnAsyncUtilsGetMessage addFlow = super.addOneStep(process, parameterEntity, messageEntity);
		return addFlow;
	}	
}
