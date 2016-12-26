package com.alogic.xscript.mongodb;

import java.util.HashMap;
import java.util.Map;

import org.bson.Document;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.mongodb.client.MongoCollection;

public class MgTableDrop extends MgTableOperation{

	public MgTableDrop(String tag, Logiclet p) {
		super(tag, p);
	}
	
	
	@Override
	public void configure(Properties p) {
		super.configure(p);
		tag = PropertiesConstants.getRaw(p, "tag", "$mg-drop");
		
	}
	
	@Override
	protected void onExecute(MongoCollection<Document> collection, Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		Map<String,Object> result=new HashMap<String,Object>();
		result.put("database", collection.getNamespace().getDatabaseName());
		result.put("collection", collection.getNamespace().getCollectionName());
		try {
			collection.drop();
			result.put("drop", true);
		} catch (Exception e) {
			result.put("drop", false);
			throw new RuntimeException(e);
		}
		
		current.put(tag, result);
	}

}
