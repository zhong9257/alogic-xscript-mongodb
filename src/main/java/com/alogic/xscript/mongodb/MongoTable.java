package com.alogic.xscript.mongodb;

import java.util.Map;

import org.bson.Document;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;

public class MongoTable extends MongoNS {

	protected String pid = "$mongo-conf";// 可能会变
	protected String cid = "$mongo-table";
	protected String table = "";
	protected String db = "";
	protected String uri = "mongodb://localhost:27017";

	public MongoTable(String tag, Logiclet p) {
		super(tag, p);

	}

	public void configure(Properties p) {
		super.configure(p);
		uri = PropertiesConstants.getString(p, "uri", uri);
		db = PropertiesConstants.getString(p, "db", db);
		table = PropertiesConstants.getString(p, "table", table);
		pid = PropertiesConstants.getString(p, "pid", pid);
		cid = PropertiesConstants.getString(p, "cid", cid);

	}

	@Override
	protected void onExecute(Map<String, Object> root, Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		// 此处获取Configuration的方式待优化
		MongoCollection<Document> t = ctx.getObject(pid);
		if (t == null){
			//自己创建schema和table
			/*Schema s = KValueSource.getSchema(schema);
			if (s == null){
				log(String.format("Can not find the schema[%s]",schema), "error");
				return ;
			}
			
			t = s.getTable(table);
			if (t == null){
				log(String.format("Can not find the table [%s/%s]",schema,table),"error");
				return ;
			}*/
			MongoClientURI connectionString = new MongoClientURI(uri);
			MongoClient mongoClient=null;
			try {
				mongoClient = new MongoClient(connectionString);
				t=mongoClient.getDatabase(db).getCollection(table);
			} catch (Exception e) {
				if(null!=mongoClient) mongoClient.close();
			}
			
		}
		
		try {
			ctx.setObject(cid, t);
			super.onExecute(root, current, ctx, watcher);
		}finally{
			ctx.removeObject(cid);
		}
	}

}
