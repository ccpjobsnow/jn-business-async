package com.ccp.jn.async.business;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.query.CcpDbQueryExecutor;
import com.ccp.especifications.db.query.ElasticQuery;
import com.ccp.especifications.db.query.Should;
import com.ccp.especifications.db.utils.DefaultField;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;

public class RemoveTries implements CcpProcess{

	@CcpDependencyInject
	private CcpDbQueryExecutor requestExecutor;
	
	public CcpMapDecorator execute(CcpMapDecorator object, String fieldName, Integer limit, JnBusinessEntity... entities) {

		Should startShould = new ElasticQuery()
		.startQuery()
		.startBool()
		.startShould(1);
		
		Set<String> ids = new HashSet<>();
		
		for(int k = 0; k < limit; k++) {
			CcpMapDecorator put = object.put(fieldName, k);
			for (JnBusinessEntity entity : entities) {
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
		.selectFrom(this.requestExecutor, array)
		.delete();
		
		return delete;

	}

	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {
		String fieldName = values.getAsString("fieldName");
		Integer limit = values.getAsIntegerNumber("limit");
		List<String> array = values.getAsStringList("entities");
		JnBusinessEntity[] entities = new JnBusinessEntity[array.size()];
		array.stream().map(x -> JnBusinessEntity.valueOf(x)).collect(Collectors.toList()).toArray(entities);
		CcpMapDecorator remove = this.execute(values, fieldName, limit, entities);
		return remove;
	}
	
}
