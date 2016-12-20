package com.alogic.xscript.mongodb;

import com.alogic.xscript.Logiclet;
import com.alogic.xscript.plugins.Segment;

public class MongoNS extends Segment {

	public MongoNS(String tag, Logiclet p) {
		super(tag, p);
		registerModule("mg-cli",MgClient.class);
		registerModule("mg-db",MgDB.class);
		registerModule("mg-table",MongoTable.class);		
		registerModule("mg-insert",MongoInsert.class);
		registerModule("mg-delete",MongoDelete.class);
	}

}
