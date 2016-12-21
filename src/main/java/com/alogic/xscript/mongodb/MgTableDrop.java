package com.alogic.xscript.mongodb;

import java.util.Map;

import org.bson.Document;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.mongodb.client.MongoCollection;

public class MgTableDrop extends MgTableOperation{


	protected String tagValue="$mg-tab-drop";
	
	public MgTableDrop(String tag, Logiclet p) {
		super(tag, p);
	}
	
	
	@Override
	public void configure(Properties p) {
		super.configure(p);
		tagValue = PropertiesConstants.getRaw(p, "tagValue", tagValue);
		
	}
	
	@Override
	protected void onExecute(MongoCollection<Document> collection, Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		collection.drop();
	}

}
