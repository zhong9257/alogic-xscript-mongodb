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
public class Nin extends FilterBuilder.Abstract{
	protected String field="_id";
	protected String value="";
	protected String type="string";
	protected String pattten="";
	protected String delimeter=";";
	

	@Override
	public Bson getFilter(Properties p,LogicletContext ctx){
		String afterTransValue=ctx.transform(value);
		String[] items=afterTransValue.split(delimeter);
		Object[] values=new Object[items.length];
		for (int i = 0; i < items.length; i++) {
			values[i]=ValueConvertor.convert(type, ctx.transform(items[i]),pattten);
		}
		
		return Filters.nin(field, values);
	}

	@Override
	public void configure(Properties p) {
		field = PropertiesConstants.getRaw(p, "field", field);
		value = PropertiesConstants.getRaw(p, "value", value);
		type = PropertiesConstants.getRaw(p, "type", type);
		pattten = PropertiesConstants.getRaw(p, "pattten", pattten);
		delimeter = PropertiesConstants.getRaw(p, "delimeter", delimeter);
	}
}
