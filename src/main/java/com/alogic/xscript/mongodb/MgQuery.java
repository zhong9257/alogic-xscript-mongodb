package com.alogic.xscript.mongodb;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.w3c.dom.Element;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.mongodb.util.FilterBuilder;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Sorts;
import static com.mongodb.client.model.Projections.*;

/**
 * 文档查询 version 1.0
 * @author cenwan
 *
 */
public class MgQuery extends MgTableOperation{
	
	private Properties filterProperties;
	
	protected String first="";
	protected String sort = "";
	protected String offset="";
	protected String limit="";
	protected String projection="";
	
    protected FilterBuilder fb = null;
    protected Bson filter = null;

	public MgQuery(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
    public void configure(Element e, Properties p) {
        Properties props = new XmlElementProperties(e, p);
        filterProperties=props;
        
		tag = PropertiesConstants.getRaw(props, "tag", "$mg-query");
		first = PropertiesConstants.getRaw(props, "first", "");
		sort = PropertiesConstants.getRaw(props, "sort", "");
		offset = PropertiesConstants.getRaw(props, "offset", "");
		limit = PropertiesConstants.getRaw(props, "limit", "");
		projection = PropertiesConstants.getRaw(props, "projection", "");
		
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
            
            if(getBoolean(first, false)){
            	Document doc = null;
    			if(fb!=null){
    				filter=fb.getFilter(filterProperties, ctx);
    				doc = collection.find(filter).first();
    			} else {
    				doc = collection.find().first();
    			}
    			
            	Map<String, Object> map = new LinkedHashMap<>();
				for(String s :doc.keySet()){
					if(s.equals("_id" )){
						if((doc.get(s) instanceof ObjectId)){
							map.put(s,doc.getObjectId("_id").toString());
						} else {
							map.put(s,doc.get(s));
						}
					} else {
						map.put(s,doc.get(s));
					}
				}	
				current.put(tag,map);
				
            } else {
            	FindIterable<Document> iter = null;
            	if(fb!=null){
    				filter=fb.getFilter(filterProperties, ctx);
    				iter = collection.find(filter);
    			} else {
    				iter = collection.find();
    			}
            	
            	if(sort != ""){
            		String str = sort;
            		if (str.indexOf(' ') == (-1)) {
            			iter = iter.sort(Sorts.ascending(stringToArray(str)));
            		} else {
            			while(true){
            				int index = str.indexOf(' ');
            				if(str.substring(index + 1, index + 2).equals("D")){
            					iter = iter.sort(Sorts.descending(stringToArray(str.substring(0, index))));
            					if(index + 6 > str.length()){
            						break;
            					}
            					str = str.substring(index + 6);
            				} else if(str.substring(index + 1, index + 2).equals("A")){
            					iter = iter.sort(Sorts.ascending(stringToArray(str.substring(0, index))));
            					if(index + 5 > str.length()){
            						break;
            					}
            					str = str.substring(index + 5);
            				} else {
            					break;
            				}
            			}
            		}
            	}
            	
            	if(offset != null){
            		iter = iter.skip(getInt(offset,0));
            	}
            	
            	if(limit != null){
            		iter = iter.limit(getInt(limit,0));
            	}
            	
            	if(projection != ""){
            		iter = iter.projection(fields(include(stringToArray(projection))));
            	}
            	
    			MongoCursor<Document> cursor = iter.iterator();
    			List<Map<String, Object>> list = new LinkedList<>();
    			while (cursor.hasNext()) {
    				Document doc = cursor.next();
    				Map<String, Object> map = new LinkedHashMap<>();
    				for(String s :doc.keySet()){
    					if(s.equals("_id" )){
    						if((doc.get(s) instanceof ObjectId)){
    							map.put(s,doc.getObjectId("_id").toString());
    						} else {
    							map.put(s,doc.get(s));
    						}
    					} else {
    						map.put(s,doc.get(s));
    					}
    				}	
    				list.add(map);
    			}
    			current.put(tag,list);
            }
	}
	
	/**
	 * 带多个字段的字符串转化为数组
	 * @param src
	 * @return List
	 */
	public static List<String> stringToArray(String src){
		if(src != null){
			List<String> list = new ArrayList<>();
			int index = src.indexOf(',');
			int len = 0;
			while(index != (-1)){
				len = src.length();
				list.add(src.substring(0, index));
				src = src.substring(index + 1, len);
				index = src.indexOf(',');
			}
			list.add(src);
			return list;
		} else {
			return null;
		}
	}
}
