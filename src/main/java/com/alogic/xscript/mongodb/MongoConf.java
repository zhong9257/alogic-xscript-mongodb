package com.alogic.xscript.mongodb;

import java.util.Map;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

public class MongoConf extends Segment {
	protected String cid = "$mongo-conf";
	protected String uri = "";

	public MongoConf(String tag, Logiclet p) {
		super(tag, p);

	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		cid = PropertiesConstants.getString(p, "cid", cid);
	}

	@Override
	protected void onExecute(Map<String, Object> root, Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		// 此处获取Configuration的方式待优化
		
		
		
		/*try {
			ctx.setObject(cid, conf);
			super.onExecute(root, current, ctx, watcher);
		} finally {
			ctx.removeObject(cid);
		}*/
	}
}
