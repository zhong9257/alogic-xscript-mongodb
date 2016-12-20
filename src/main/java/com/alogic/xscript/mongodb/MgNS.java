package com.alogic.xscript.mongodb;

import com.alogic.xscript.Logiclet;
import com.alogic.xscript.plugins.Segment;

public class MgNS extends Segment {

	public MgNS(String tag, Logiclet p) {
		super(tag, p);

		registerModule("mg-cli",MgClient.class);
		registerModule("mg-db",MgDB.class);
		registerModule("mg-table",MgTable.class);		
		registerModule("mg-insert",MgInsert.class);
		registerModule("mg-delete",MgDelete.class);
		registerModule("mg-query",MgQuery.class);
		registerModule("mg-textsearch",MgTextSearch.class);
		registerModule("mg-count",MgCount.class);
		registerModule("mg-2dspherequery",Mg2dsphereQuery.class);
	}

}
