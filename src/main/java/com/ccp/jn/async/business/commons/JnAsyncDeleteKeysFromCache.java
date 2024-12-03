package com.ccp.jn.async.business.commons;

import java.util.Collection;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.cache.CcpCacheDecorator;

public class JnAsyncDeleteKeysFromCache implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public static final JnAsyncDeleteKeysFromCache INSTANCE = new JnAsyncDeleteKeysFromCache();
	
	private JnAsyncDeleteKeysFromCache() {}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		
		Collection<String> allCacheKeys = json.getAsStringList("keysToDeleteInCache");
		
		for (String cacheKey : allCacheKeys) {
			CcpCacheDecorator cache = new CcpCacheDecorator(cacheKey);
			cache.delete();
		}
		return json;
	}

}
