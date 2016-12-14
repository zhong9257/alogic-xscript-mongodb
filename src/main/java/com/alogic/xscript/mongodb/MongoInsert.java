package com.alogic.xscript.mongodb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.BsonArray;
import org.bson.Document;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.mongodb.client.MongoCollection;

public class MongoInsert extends MongoTableOperation{
	
	protected String doc="";
	protected String many="false";

	public MongoInsert(String tag, Logiclet p) {
		super(tag, p);
	}
	
	
	@Override
	public void configure(Properties p) {
		super.configure(p);
		doc = PropertiesConstants.getRaw(p, "doc", "");
		many = PropertiesConstants.getRaw(p, "many", "");
	}


	@Override
	protected void onExecute(MongoCollection<Document> collection, Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		
		if(getBoolean(many, false)){
			BsonArray bsonArray= BsonArray.parse(ctx.transform(many));
			List<Document> docs = new ArrayList<Document>();
			for (int i = 0; i < bsonArray.size(); i++) {
				docs.add(Document.parse(bsonArray.get(i).asDocument().toJson()));
			}
			collection.insertMany(docs);
		}else{			
			collection.insertOne(Document.parse(ctx.transform(doc)));
		}
		
		log(String.format("insert doc[%s] success!", collection.getNamespace()), "info");
		
	}
	
	public static void main(String[] args) {
		
		//Object blank=Document.parse("");报错
		//Object n=Document.parse(null);报错
		Object noData=BsonArray.parse("[{\"a\":\"aa\"},{\"b\":\"bb\"}]");
		System.out.println("111222");
	}

}
