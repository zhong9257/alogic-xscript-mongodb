package com.alogic.xscript.mongodb;

import java.util.Map;

import com.alogic.pool.Pool;
import com.alogic.pool.PoolNaming;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class MgDB extends MgNS{
	
	protected String pid = "$mg-cli";// 可能会变
	protected String cid = "$mg-db";
	
	protected String cli = "";
	protected String db = "";
	
	
	public MgDB(String tag, Logiclet p) {
		super(tag, p);

	}

	public void configure(Properties p) {
		super.configure(p);
		pid = PropertiesConstants.getString(p, "pid", pid);
		cid = PropertiesConstants.getString(p, "cid", cid);
		cli = PropertiesConstants.getString(p, "cli", cli);
		db = PropertiesConstants.getString(p, "db", db);

	}

	@Override
	protected void onExecute(Map<String, Object> root, Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {

	
		MongoClient mongoClient = ctx.getObject(pid);
		if (mongoClient == null){
			PoolNaming naming  = PoolNaming.get();
			Pool p=naming.lookup(cli);
			mongoClient=p.borrowObject(0, 0);
			
			if (mongoClient == null){
				log(String.format("Can not find the schema[%s]",cli), "error");
				return ;
			}
		}
		
		MongoDatabase t = mongoClient.getDatabase(db);
		if (t == null){
			log(String.format("Can not find the table [%s/%s]",cli,db),"error");
			return ;
		}
	
		try {
			ctx.setObject(cid, t);
			super.onExecute(root, current, ctx, watcher);
		}finally{
			ctx.removeObject(cid);
		}
	}
}
