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
import com.mongodb.client.result.DeleteResult;

public class MgDelete extends MgTableOperation{
    /**
     * Filter Builder
     */
    protected FilterBuilder fb = null;
    protected Bson filter = null;
	private Properties filterProperties;
	
	protected String many="false";
	

 
	public MgDelete(String tag, Logiclet p) {
		super(tag, p);
	}
	
	
	@Override
	public void configure(Properties p) {
		super.configure(p);
		many = PropertiesConstants.getRaw(p, "many", "");
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
		
		DeleteResult result=null;
		if(getBoolean(many, false)){
			if(fb!=null){
				filter=fb.getFilter(filterProperties, ctx);
				result=collection.deleteMany(filter);				
			}else{
				
			}
		}else{
			if(fb!=null){
				filter=fb.getFilter(filterProperties, ctx);
				result=collection.deleteOne(filter);
			}else{
				
			}
		}
		
		log(String.format("delete doc[%s] success!", collection.getNamespace()), "info");
		
	}
	
	public static void main(String[] args) {
		
	}

}
