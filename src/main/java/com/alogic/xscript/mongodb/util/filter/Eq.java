package com.alogic.xscript.mongodb.util.filter;

import org.bson.conversions.Bson;

import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.mongodb.util.FilterBuilder;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.mongodb.client.model.Filters;

/**
 * @author zhongyi
 *
 */
public class Eq extends FilterBuilder.Abstract{
	
	protected String field="_id";
	protected String value="-1qmwnebrvtc-1";//毫无规则的默认值，避免误操作
	

	@Override
	public Bson getFilter(Properties p,LogicletContext ctx) {
		return Filters.eq(field, ctx.transform(value));
	}

	@Override
	public void configure(Properties p) {
		field = PropertiesConstants.getRaw(p, "field", field);
		value = PropertiesConstants.getRaw(p, "value", value);
	}

}
