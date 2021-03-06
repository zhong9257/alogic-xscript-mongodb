package com.alogic.xscript.mongodb;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

/**
 * 文本搜索 version 1.0
 * @author cenwan
 *
 */
public class MgTextSearch extends MgTableOperation{
	
	protected String keywords="";
	protected String textscore = "";
	protected String offset="";
	protected String limit="";

	public MgTextSearch(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p) {
		super.configure(p);
		tag = PropertiesConstants.getRaw(p, "tag", "$mg-textsearch");
		keywords = PropertiesConstants.getRaw(p, "keywords", "");
		textscore = PropertiesConstants.getRaw(p, "textscore", "");
		offset = PropertiesConstants.getRaw(p, "offset", "");
		limit = PropertiesConstants.getRaw(p, "limit", "");
	}
	
	@Override
	protected void onExecute(MongoCollection<Document> collection, Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
            
        	FindIterable<Document> iter = null;
        	if(keywords != ""){
        		iter = collection.find(Filters.text(keywords));
        	} else {
        		iter = collection.find();
        	}
        	
        	if(getBoolean(textscore, false)){
            	iter = iter.projection(Projections.metaTextScore("score"));
            	iter = iter.sort(Sorts.metaTextScore("score"));
        	}
        	
        	if(offset != null){
        		iter = iter.skip(getInt(offset,0));
        	}
        	
        	if(limit != null){
        		iter = iter.limit(getInt(limit,0));
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
