package com.ccp.jn.async.business;

import java.util.List;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.jn.commons.JnBusinessEntity;

public class MessagesTranslation {

	@CcpDependencyInject
	private CcpDbCrud crud;

	public CcpMapDecorator translate(Enum<?> _enum, String language, String key, CcpMapDecorator externalParameters) {
		
		CcpMapDecorator manyByIdAsSingle = this.getManyByIdAsSingle(language, _enum);
		
		CcpMapDecorator values = manyByIdAsSingle.getInternalMap(JnBusinessEntity.values.name()).getInternalMap("value");
		
		CcpMapDecorator message = manyByIdAsSingle.getInternalMap(JnBusinessEntity.message.name());
		
		CcpMapDecorator translated = message.putAll(values).putFilledTemplate("value", key);
		
		CcpMapDecorator retranslated = translated.putAll(externalParameters).putFilledTemplate(key, key);
		
		return retranslated;
	}
	
	private CcpMapDecorator getManyByIdAsSingle(String language, Enum<?> _enum) {
		CcpMapDecorator manyByIdAsSingle = new CcpMapDecorator();
		
		List<CcpMapDecorator> manyById = this.getManyById(language, _enum);
		
		for (CcpMapDecorator json : manyById) {
			String index = json.getAsString("_index");
			manyByIdAsSingle = manyByIdAsSingle.put(index, json);
		}
		
		return manyByIdAsSingle;
	}
	
	
	public CcpMapDecorator getMergedParameters(Enum<?> _enum,CcpMapDecorator externalParameters, String language, String... keys) {
	
		List<CcpMapDecorator> manyById = this.getManyById(language, _enum);
		
		List<CcpMapDecorator> jsons = manyById.stream().map(x -> x.getInternalMap("value")).collect(Collectors.toList());
		
		CcpMapDecorator mergedParameters = new CcpMapDecorator();
		
		externalLoop:for (String key : keys) {
			for (CcpMapDecorator json : jsons) {
				
				boolean theKeyWasFound = json.containsKey(key);
				
				if(theKeyWasFound) {
					Object value = json.get(key);
					mergedParameters = mergedParameters.put(key, value);
					continue externalLoop;
				}
			}
		}
		
		return mergedParameters;
	}

	private List<CcpMapDecorator> getManyById(String language, Enum<?> _enum) {
	
		String templateName = _enum.name();

		
		CcpMapDecorator idToSearch = new CcpMapDecorator().put("id", templateName).put("language", language);

		List<CcpMapDecorator> manyById = this.crud.getManyById(idToSearch, JnBusinessEntity.message, JnBusinessEntity.values);
		return manyById;
	}

}
