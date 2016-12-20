package com.alogic.xscript.mongodb;

import java.util.Map;

import org.bson.Document;

import com.alogic.pool.Pool;
import com.alogic.pool.PoolNaming;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MgTable extends MgNS {

	protected String pid = "$mg-db";// 可能会变
	protected String cid = "$mg-table";
	
	protected String cli = "";
	protected String db = "";
	protected String table = "";

	public MgTable(String tag, Logiclet p) {
		super(tag, p);

	}

	public void configure(Properties p) {
		super.configure(p);
		pid = PropertiesConstants.getString(p, "pid", pid);
		cid = PropertiesConstants.getString(p, "cid", cid);
		cli = PropertiesConstants.getString(p, "cli", cli);
		db = PropertiesConstants.getString(p, "db", db);
		table = PropertiesConstants.getString(p, "table", table);

	}

	@Override
	protected void onExecute(Map<String, Object> root, Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		MongoClient mongoClient=null;
		MongoDatabase database = ctx.getObject(pid);
		if (database == null){
			PoolNaming naming  = PoolNaming.get();
			Pool p=naming.lookup(cli);
			mongoClient=p.borrowObject(0, 0);
			
			if (mongoClient == null){
				log(String.format("Can not find the schema[%s]",cli), "error");
				return ;
			}
			
				
			database = mongoClient.getDatabase(db);
			if (database == null){
				log(String.format("Can not find the table [%s/%s]",cli,db),"error");
				return ;
			}
		}
		
		
		MongoCollection<Document> doc=database.getCollection(table);
	
		try {
			ctx.setObject(cid, doc);
			super.onExecute(root, current, ctx, watcher);
		}finally{
			ctx.removeObject(cid);
		}
	}

}
