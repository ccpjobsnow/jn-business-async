package com.ccp.jn.async.business;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.query.CcpDbQueryOptions;
import com.ccp.especifications.db.query.CcpDbQueryShould;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.db.utils.CcpEntityDefaultField;

public class JnAsyncBusinessRemoveTries implements  java.util.function.Function<CcpMapDecorator, CcpMapDecorator>{

	
	public CcpMapDecorator apply(CcpMapDecorator object, String fieldName, Integer limit, CcpEntity... entities) {

		CcpDbQueryShould startShould = new CcpDbQueryOptions()
		.startQuery()
		.startBool()
		.startShould(1);
		
		Set<String> ids = new HashSet<>();
		
		for(int k = 1; k <= limit; k++) {
			CcpMapDecorator put = object.put(fieldName, k);
			for (CcpEntity entity : entities) {
				String id = entity.getId(put);
				ids.add(id);
			}
		}
		
		for (String id : ids) {
			startShould = startShould.term(CcpEntityDefaultField._id, id);
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
		CcpEntity[] entities = new CcpEntity[array.size()];
		array.stream()
		//.map(x -> JnEntity.valueOf(x))//TODO
		.collect(Collectors.toList()).toArray(entities);
		CcpMapDecorator remove = this.apply(values, fieldName, limit, entities);
		return remove;
	}
	
}
