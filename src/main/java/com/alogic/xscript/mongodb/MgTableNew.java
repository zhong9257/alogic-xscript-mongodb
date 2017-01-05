package com.alogic.xscript.mongodb;

import java.util.Map;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;

public class MgTableNew extends MgDbOperation {

	protected String table = "";
	protected String capped = "";
	protected String size = "";

	public MgTableNew(String tag, Logiclet p) {
		super(tag, p);
	}
	
	public void configure(Properties p) {
		super.configure(p);
		table = PropertiesConstants.getRaw(p, "table", "");
		capped = PropertiesConstants.getRaw(p, "capped", "");
		size = PropertiesConstants.getRaw(p, "size", "");
	}

	@Override
	protected void onExecute(MongoDatabase dataBase, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		
		boolean tableIsExist = false;//判断表是否存在
		for (String name : dataBase.listCollectionNames()) {
			if(name.equals(table)){
				log(String.format("The table %s already exist.",table));
				tableIsExist = true;
				break;
			}
		}
		
		if(!tableIsExist){
			if(getBoolean(capped, false)){
				dataBase.createCollection(table, new CreateCollectionOptions().
						capped(Boolean.parseBoolean(capped)).sizeInBytes(Long.parseLong(size)));
			} else {
				dataBase.createCollection(table);
			}
			
		}
	}
	

}
