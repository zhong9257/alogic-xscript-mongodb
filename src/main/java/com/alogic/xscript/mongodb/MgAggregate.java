package com.alogic.xscript.mongodb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bson.BsonArray;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;


public class MgAggregate extends MgTableOperation{
	
	protected String doc="";
	protected String idKey="_id";
	protected String tagValue="";
	
	protected static JsonProvider provider = null;	
	static {
		provider = JsonProviderFactory.createProvider();
	}
	/**
	 * 子节点
	 */
	protected List<Logiclet> children = new ArrayList<Logiclet>(); // NOSONAR

	public MgAggregate(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Element element, Properties p) {
		XmlElementProperties props = new XmlElementProperties(element, p);
		doc = PropertiesConstants.getRaw(props, "doc", "");
		idKey = PropertiesConstants.getRaw(props, "idKey", idKey);
		tagValue = PropertiesConstants.getRaw(props, "tagValue", tagValue);
		
		if("".equals(doc)){
			
			
			//约定子标签doc包含的标签是用来构建doc数据
			Element docE = XmlTools.getFirstElementByPath(element, "doc");
			NodeList nodeList = docE.getChildNodes();
			
			for (int i = 0 ; i < nodeList.getLength() ; i ++){
				Node n = nodeList.item(i);
				
				if (n.getNodeType() != Node.ELEMENT_NODE){
					//只处理Element节点
					continue;
				}
				
				Element e = (Element)n;
				String xmlTag = e.getNodeName();		
				Logiclet statement = createLogiclet(xmlTag, this);
				
				if (statement != null){
					statement.configure(e, props);
					children.add(statement);
				}
			}
		}	
		configure(props);
	}

	@Override
	protected void onExecute(MongoCollection<Document> collection, Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		Map<String,Object> jsonData = null;		
		if("".equals(doc)){
			jsonData = new HashMap<String,Object>();	
			for (Logiclet child:children){
				child.execute(jsonData, jsonData, ctx, watcher);
			}
			
			//约定子标签构造的数据放到doc节点下
			Object o=jsonData.get("doc");
			doc=provider.toJson(o);
						
		}else{
			doc=ctx.transform(doc);
		}
		
		BsonArray bsonArray= BsonArray.parse(doc);
		List<Document> docs = new ArrayList<Document>();
		for (int i = 0; i < bsonArray.size(); i++) {
			docs.add(Document.parse(bsonArray.get(i).asDocument().toJson()));
		}
		
		AggregateIterable<Document> iter = collection.aggregate(docs);
		MongoCursor<Document> cursor = iter.iterator();
		List<Map<String, Object>> list = new LinkedList<>();
		while (cursor.hasNext()) {
			Document doc = cursor.next();
			Map<String, Object> map = new LinkedHashMap<>();
			for(String s :doc.keySet()){
				if(s.equals("_id" )){
					if((doc.get(s) instanceof ObjectId)){
						map.put(s,doc.getObjectId("_id").toString());
					} else {
						map.put(s,doc.get(s));
					}
				} else {
					map.put(s,doc.get(s));
				}
			}	
			list.add(map);
		}
		current.put(tagValue,list);
	}
}
