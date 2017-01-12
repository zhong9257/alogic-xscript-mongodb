package com.alogic.xscript.mongodb;

import java.util.LinkedList;
import java.util.List;
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
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

/**
 * 列举指定字段的不同值  version 1.0
 * @author cenwan
 *
 */
public class MgDistinct extends MgTableOperation{
	
	private Properties filterProperties;
	
    protected FilterBuilder fb = null;
    protected Bson filter = null;
    
    protected String idKey = "";
    protected String fieldName = "";
    
	public MgDistinct(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
    public void configure(Element element, Properties props) {
        Properties p = new XmlElementProperties(element, props);
        filterProperties=props;
        
        tag = PropertiesConstants.getRaw(p, "tag", "$mg-count");
        idKey = PropertiesConstants.getRaw(p, "idKey", "");
        fieldName = PropertiesConstants.getRaw(p, "fieldName", "");
        
        Element filter = XmlTools.getFirstElementByPath(element, "filter");
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
		
		if (fb != null) {
			filter = fb.getFilter(filterProperties, ctx);	
		}else{
			filter=Document.parse("{}");
		}
		DistinctIterable<Object> iter = collection.distinct(fieldName, filter, Object.class);
		MongoCursor<Object> cursor = iter.iterator();
		List<Object> list = new LinkedList<>();
		while(cursor.hasNext()){
			list.add(cursor.next());
		}
		current.put(tag, list);
    }
}
