package com.alogic.xscript.mongodb;

import com.alogic.xscript.Logiclet;
import com.alogic.xscript.plugins.Segment;

public class MongoNS extends Segment {

	public MongoNS(String tag, Logiclet p) {
		
		super(tag, p);
		registerModule("mongo-conf",MongoConf.class);
		registerModule("mongo-table",MongoTable.class);
		registerModule("mongo-insert",MongoInsert.class);
		registerModule("mongo-delete",MongoDelete.class);
		registerModule("mongo-query",MgQuery.class);
		registerModule("mongo-textsearch",MgTextSearch.class);
	}

}
