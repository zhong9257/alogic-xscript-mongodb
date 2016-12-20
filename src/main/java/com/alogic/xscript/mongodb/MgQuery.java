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
 * 文档查找
 * @author cenwan
 *
 */
public class MgQuery extends MgTableOperation{
	
	private Properties filterProperties;
	
	protected String tagValue="";
	protected String first="";//first为true时，只返回最先匹配到的一个文档；其他情况返回多个文档
	protected String sort = "";//对指定字段进行排序，可以有多个字段,用逗号隔开，升序字段放到分号前面，降序字段放分号后面.mongodb是先排序后limit
	protected String offset="";//跳过指定数量的文档
	protected String limit="";//返回指定数量的文档，只有first为false时有效
	protected String projection="";//返回指定字段
	
    protected FilterBuilder fb = null;
    protected Bson filter = null;

	public MgQuery(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p) {
		super.configure(p);
		tagValue = PropertiesConstants.getRaw(p, "tagValue", "");
		first = PropertiesConstants.getRaw(p, "first", "");
		sort = PropertiesConstants.getRaw(p, "sort", "");
		offset = PropertiesConstants.getRaw(p, "offset", "");
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
				current.put(tagValue,map);
				
            } else {//返回多个匹配的文档
            	FindIterable<Document> iter = null;
            	if(fb!=null){//根据指定段匹配文档
    				filter=fb.getFilter(filterProperties, ctx);
    				iter = collection.find(filter);
    			} else {
    				iter = collection.find();
    			}
            	
            	if(sort != ""){//对指定字段排序
            		int index = sort.indexOf(';');
            		String descendPart = sort.substring(index + 1,sort.length());
            		String ascendPart = sort.substring(0,index);
            		if(index == 0){
            			iter = iter.sort(Sorts.descending(stringToArray(descendPart)));
            		} else if ((index + 1) == sort.length()){
            			iter = iter.sort(Sorts.ascending(stringToArray(ascendPart)));
            		} else {
            			iter = iter.sort(Sorts.orderBy(Sorts.ascending(stringToArray(ascendPart)), Sorts.descending(stringToArray(descendPart))));
            		}
            	}
            	
            	if(offset != null){
            		iter = iter.skip(getInt(offset,0));
            	}
            	
            	if(limit != null){//limit不为空时，返回 指定数量的个文档，limit为0或空 时都返回全部文档，其他情况返回|limit|数量的文档
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
    			current.put(tagValue,list);
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
