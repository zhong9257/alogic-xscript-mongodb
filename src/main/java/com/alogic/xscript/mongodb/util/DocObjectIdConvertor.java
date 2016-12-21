package com.alogic.xscript.mongodb.util;

import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;


/**将文档里ObjectId转换成String类型的工具类
 * @author zhongyi
 *
 */
public class DocObjectIdConvertor {
	/**
	 * @param docs
	 * @param idKey
	 */
	public  static void convert(List<Document> docs,String idKey){
		if(null==docs) return;
		if(null==idKey||"".equals(idKey)){
			idKey="_id";
		}
		
		for (int i = 0; i < docs.size(); i++) {
			Document doc =docs.get(i);	
			ObjectId id=doc.getObjectId(idKey);
			doc.put(idKey, id==null?"":id.toString());		
		}
	};
	

	/**
	 * @param doc
	 * @param idKey
	 */
	public  static void convert(Document doc,String idKey){
		if(null==doc) return;
		if(null==idKey||"".equals(idKey)){
			idKey="_id";
		}		
		ObjectId id=doc.getObjectId(idKey);
		doc.put(idKey, id.toString());			
	};
}
