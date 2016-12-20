package com.alogic.xscript.mongodb;

import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.w3c.dom.Element;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.mongodb.util.FilterBuilder;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.mongodb.client.MongoCollection;

/**
 * 根据查询条件，返回文档的数量 version 1.0
 * @author cenwan
 *
 */
public class MgCount extends MgTableOperation{
	
	private Properties filterProperties;
	
	protected String tagValue="";
	
    protected FilterBuilder fb = null;
    protected Bson filter = null;

	public MgCount(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p) {
		super.configure(p);
		tagValue = PropertiesConstants.getRaw(p, "tagValue", "");
	}
	
	@Override
    public void configure(Element e, Properties p) {
        Properties props = new XmlElementProperties(e, p);
        filterProperties=props;
        
        Element filter = XmlTools.getFirstElementByPath(e, "filter");
        if (filter != null) {
            FilterBuilder.TheFactory f = new FilterBuilder.TheFactory();
            try {
                fb = f.newInstance(filter, props, "module");
            } catch (Exception ex) {
                log("Can not create instance of FilterBuilder.", "error");
            }
        }
        configure(props);
    }

	@Override
	protected void onExecute(MongoCollection<Document> collection, Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		long count = 0;
    	if(fb!=null){//根据指定段匹配文档
			filter=fb.getFilter(filterProperties, ctx);
			count = collection.count(filter);
		} else {
			count = collection.count();
		}
		current.put(tagValue, count);
    }
}
