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
public class Mod extends FilterBuilder.Abstract{
	
	protected String field="_id";
	protected String remainder="";
	protected String divisor="";

	

	@Override
	public Bson getFilter(Properties p,LogicletContext ctx){
		remainder=ctx.transform(remainder);
		divisor=ctx.transform(divisor);
		return Filters.mod(field, Long.parseLong(divisor),Long.parseLong(remainder));
	}

	@Override
	public void configure(Properties p) {
		field = PropertiesConstants.getRaw(p, "field", field);
		remainder = PropertiesConstants.getRaw(p, "remainder", remainder);
		divisor = PropertiesConstants.getRaw(p, "divisor", divisor);
	}

}
