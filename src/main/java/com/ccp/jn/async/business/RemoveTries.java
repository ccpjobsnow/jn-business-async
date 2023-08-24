package com.ccp.jn.async.business;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.query.ElasticQuery;
import com.ccp.especifications.db.query.Should;
import com.ccp.especifications.db.utils.DefaultField;
import com.jn.commons.JnEntity;

public class RemoveTries implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator>{

	
	public CcpMapDecorator apply(CcpMapDecorator object, String fieldName, Integer limit, JnEntity... entities) {

		Should startShould = new ElasticQuery()
		.startQuery()
		.startBool()
		.startShould(1);
		
		Set<String> ids = new HashSet<>();
		
		for(int k = 1; k <= limit; k++) {
			CcpMapDecorator put = object.put(fieldName, k);
			for (JnEntity entity : entities) {
				String id = entity.getId(put);
				ids.add(id);
			}
		}
		
		for (String id : ids) {
			startShould = startShould.term(DefaultField._id, id);
		}
		
		String[] array = Arrays.asList(entities).stream().map(x -> x.name()).collect(Collectors.toList()).toArray(new String[entities.length]);
		
		CcpMapDecorator delete = startShould.endShouldAndBackToBool().endBoolAndBackToQuery()
		.endQueryAndBackToRequest()
		.selectFrom(array)
		.delete();
		
		return delete;

	}

	@Override
	public CcpMapDecorator apply(CcpMapDecorator values) {
		String fieldName = values.getAsString("fieldName");
		Integer limit = values.getAsIntegerNumber("limit");
		List<String> array = values.getAsStringList("entities");
		JnEntity[] entities = new JnEntity[array.size()];
		array.stream().map(x -> JnEntity.valueOf(x)).collect(Collectors.toList()).toArray(entities);
		CcpMapDecorator remove = this.apply(values, fieldName, limit, entities);
		return remove;
	}
	
}
