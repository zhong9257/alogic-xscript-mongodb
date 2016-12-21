package com.alogic.xscript.mongodb.util.filter;

import org.bson.conversions.Bson;

import com.alogic.xscript.mongodb.util.FilterBuilder;
import com.mongodb.client.model.Filters;

/**
 * @author zhongyi
 *
 */
public class Or extends FilterBuilder.Multi{

	@Override
	public Bson getFilter(Bson[] bsons) {
		return Filters.or(bsons);
	}

}
