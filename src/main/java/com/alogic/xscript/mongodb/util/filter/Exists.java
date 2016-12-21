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
public class Exists extends FilterBuilder.Abstract{
	
	protected String field="_id";
	protected String value="false";//毫无规则的默认值，避免误操作


	@Override
	public Bson getFilter(Properties p,LogicletContext ctx){
		boolean exists = false;
		try {
			exists=Boolean.parseBoolean(value);
		} catch (Exception e) {
		}
		return Filters.exists(field,exists );
	}

	@Override
	public void configure(Properties p) {
		field = PropertiesConstants.getRaw(p, "field", field);
		value = PropertiesConstants.getRaw(p, "value", value);
		
	}

}
