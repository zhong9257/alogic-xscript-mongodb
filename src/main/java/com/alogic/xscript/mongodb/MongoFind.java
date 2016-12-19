package com.alogic.xscript.mongodb;

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
 * 文档查找
 * @author cenwan
 *
 */
public class MongoFind extends MongoTableOperation{
	
	private Properties filterProperties;
	
	protected String tag="";
	protected String first="";//first为true时，只返回最先匹配到的一个文档；其他情况返回多个文档
	protected String sort = "";//对指定字段进行排序，可以有多个字段,用逗号隔开，升序字段放到分号前面，降序字段放分号后面.mongodb是先排序后limit
	protected String skip="";//跳过指定数量的文档
	protected String limit="";//返回指定数量的文档，只有first为false时有效
	protected String projection="";//返回指定字段
	
    protected FilterBuilder fb = null;
    protected Bson filter = null;

	public MongoFind(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p) {
		super.configure(p);
		tag = PropertiesConstants.getRaw(p, "tag", "");
		first = PropertiesConstants.getRaw(p, "first", "");
		sort = PropertiesConstants.getRaw(p, "sort", "");
		skip = PropertiesConstants.getRaw(p, "skip", "");
		limit = PropertiesConstants.getRaw(p, "limit", "");
		projection = PropertiesConstants.getRaw(p, "projection", "");
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
            
            if(getBoolean(first, false)){//first为true的时候，只返回最先匹配到的一个文档
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
				
            } else {//返回多个匹配的文档
            	FindIterable<Document> iter = null;
            	if(fb!=null){//根据指定段匹配文档
    				filter=fb.getFilter(filterProperties, ctx);
    				iter = collection.find(filter);
    			} else {
    				iter = collection.find();
    			}
            	
            	if(sort != ""){//对指定字段排序
            		/*
            		 * 多字段传入
            		 * 
            		 * 
            		 */
            		int index = sort.indexOf(';');
            		if(index == 0){
            			iter = iter.sort(Sorts.descending(sort.substring(1,sort.length())));
            		} else if ((index + 1) == sort.length()){
            			iter = iter.sort(Sorts.ascending(sort.substring(0,index)));
            		} else {
            			iter = iter.sort(Sorts.orderBy(Sorts.ascending(sort.substring(0,index)), Sorts.descending(sort.substring(index + 1,sort.length()))));
            		}
            	}
            	
            	if(skip != null){
            		iter = iter.skip(getInt(skip,0));
            	}
            	
            	if(limit != null){//limit不为空时，返回 指定数量的个文档，limit为0或空 时都返回全部文档，其他情况返回|limit|数量的文档
            		iter = iter.limit(getInt(limit,0));
            	}
            	
            	if(projection != ""){
            		/*
            		 * 多字段传入
            		 * 
            		 * 
            		 */
            		//Object str = "\"name\",\"_id\"";
            		//System.out.println(projection);
            		iter = iter.projection(fields(include(projection)));
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
}
