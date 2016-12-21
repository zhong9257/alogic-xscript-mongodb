package com.alogic.xscript.mongodb.util.filter;

import org.bson.conversions.Bson;

import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.mongodb.util.FilterBuilder;
import com.alogic.xscript.mongodb.util.ValueConvertor;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.mongodb.client.model.Filters;

/**
 * @author zhongyi
 *
 */
public class Lte extends FilterBuilder.Abstract{
	protected String field="_id";
	protected String value="";//毫无规则的默认值，避免误操作
	protected String type="string";
	protected String pattten="";
	

	@Override
	public Bson getFilter(Properties p,LogicletContext ctx){
		return Filters.lt(field, ValueConvertor.convert(type, ctx.transform(value),null));
	}

	@Override
	public void configure(Properties p) {
		field = PropertiesConstants.getRaw(p, "field", field);
		value = PropertiesConstants.getRaw(p, "value", value);
		type = PropertiesConstants.getRaw(p, "type", type);
		pattten = PropertiesConstants.getRaw(p, "pattten", pattten);
	}
}
