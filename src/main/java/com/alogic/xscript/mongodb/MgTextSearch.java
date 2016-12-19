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
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import static com.mongodb.client.model.Projections.*;
/**
 * 文本搜索
 * @author cenwan
 *
 */
public class MgTextSearch extends MongoTableOperation{
	
	protected String tag="";
	protected String searchterm="";//按照匹配度排序
	protected String textscore = "";//是否返回searchterm的相关度分数
	protected String skip="";//跳过指定个文档
	protected String limit="";//返回指定数量的文档

	public MgTextSearch(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p) {
		super.configure(p);
		tag = PropertiesConstants.getRaw(p, "tag", "");
		searchterm = PropertiesConstants.getRaw(p, "searchterm", "");
		textscore = PropertiesConstants.getRaw(p, "textscore", "");
		skip = PropertiesConstants.getRaw(p, "skip", "");
		limit = PropertiesConstants.getRaw(p, "limit", "");
	}
	
	@Override
	protected void onExecute(MongoCollection<Document> collection, Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
            
        	FindIterable<Document> iter = null;
        	if(searchterm != ""){
        		iter = collection.find(Filters.text(searchterm));
        	} else {
        		iter = collection.find();
        	}
        	
        	if(getBoolean(textscore, false)){
            	iter = iter.projection(Projections.metaTextScore("score"));
            	iter = iter.sort(Sorts.metaTextScore("score"));
        	}
        	
        	if(skip != null){
        		iter = iter.skip(getInt(skip,0));
        	}
        	
        	if(limit != null){//limit不为空时，返回 指定数量的个文档，limit为0或空 时都返回全部文档，其他情况返回|limit|数量的文档
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
