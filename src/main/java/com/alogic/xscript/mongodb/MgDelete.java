package com.alogic.xscript.mongodb;

import java.util.HashMap;
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

/**从指定集合里删除一个或者多个文档
 * @author zhongyi
 *
 */
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
		
	}

	
    @Override
    public void configure(Element e, Properties props) {
        Properties p = new XmlElementProperties(e, props);
        many = PropertiesConstants.getRaw(p, "many", "");
		tag = PropertiesConstants.getRaw(p, "tag", "$mg-delete");
        
        
        filterProperties=p;
        
        Element filter = XmlTools.getFirstElementByPath(e, "filter");
        if (filter != null) {
            FilterBuilder.TheFactory f = new FilterBuilder.TheFactory();
            try {
                fb = f.newInstance(filter, p, "module");
                
            } catch (Exception ex) {
                log("Can not create instance of FilterBuilder.", "error");
            }
        }
        configure(p);
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
				result=collection.deleteMany(Document.parse("{}"));
			}
		}else{
			if(fb!=null){
				filter=fb.getFilter(filterProperties, ctx);
				result=collection.deleteOne(filter);
			}else{
				result=collection.deleteOne(Document.parse("{}"));
			}
		}
		current.put(tag, deleteResultConvert(result));
		log(String.format("delete doc[%s] success!", collection.getNamespace()), "info");
		
	}
	
	private Map<String,Object> deleteResultConvert(DeleteResult r){
		Map<String,Object> m= new HashMap<>();
		if(null!=r){
			m.put("deletedCount", r.getDeletedCount());
		}else{
			m.put("deletedCount", "");
		}
		
		return m;
	} 

}
