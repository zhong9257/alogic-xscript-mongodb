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

public class MgClient extends MgNS{
	
	/**
	 * 指定的client
	 */
	protected String cli = "";
	
	/**
	 * 上下文id
	 */
	protected String cid = "$mg-cli";


	public MgClient(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		cli = PropertiesConstants.getString(p,"cli","");
		cid = PropertiesConstants.getString(p, "cid", cid);
	}

	@Override
	protected void onExecute(Map<String, Object> root, Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		
		PoolNaming naming  = PoolNaming.get();
		Pool p=naming.lookup(cli);
		MongoClient mongoClient=p.borrowObject(0, 0);

		if (mongoClient == null){
			log(String.format("Can not find the schema[%s]",mongoClient), "error");
			return ;
		}
	
		try {
			ctx.setObject(cid, mongoClient);
			super.onExecute(root, current, ctx, watcher);
		}finally{
			ctx.removeObject(cid);
		}
	}
}