package com.alogic.xscript.mongodb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.BsonArray;
import org.bson.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.mongodb.util.DocObjectIdConvertor;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;
import com.mongodb.operation.AggregateOperation;
import com.mongodb.operation.GroupOperation;




/**
 * @author zhongyi
 *
 */
public class MgInsert extends MgTableOperation{
	
	protected String doc="";
	protected String many="false";
	protected String idKey="_id";
	protected String docNode="doc";
	protected String docResultNode="doc";
	
	protected static JsonProvider provider = null;	
	static {
		provider = JsonProviderFactory.createProvider();
	}
	/**
	 * 子节点
	 */
	protected List<Logiclet> children = new ArrayList<Logiclet>(); // NOSONAR

	public MgInsert(String tag, Logiclet p) {
		super(tag, p);
	}
	
	
	
	@Override
	public void configure(Element element, Properties props) {
		XmlElementProperties p = new XmlElementProperties(element, props);
		
		doc = PropertiesConstants.getRaw(p, "doc", "");
		docNode = PropertiesConstants.getRaw(p, "docNode", docNode);
		docResultNode = PropertiesConstants.getRaw(p, "docResultNode", docResultNode);
		many = PropertiesConstants.getRaw(p, "many", many);
		idKey = PropertiesConstants.getRaw(p, "idKey", idKey);
		tag = PropertiesConstants.getRaw(p, "tag", "$mg-insert");
		
		
		if("".equals(doc)){			
			//约定子标签doc包含的标签是用来构建doc数据
			Element docE = XmlTools.getFirstElementByPath(element, docNode);
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
					statement.configure(e, p);
					children.add(statement);
				}
			}
		}	
		
		configure(p);
	}


	@Override
	protected void onExecute(MongoCollection<Document> collection, Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		String _doc=doc;

		
		Map<String,Object> jsonData = null;		
		if("".equals(doc)){
			jsonData = new HashMap<String,Object>();	
			for (Logiclet child:children){
				child.execute(jsonData, jsonData, ctx, watcher);
			}
			
			//约定子标签构造的数据放到doc节点下
			Object o=jsonData.get(docResultNode);
			doc=provider.toJson(o);
						
		}else{
			doc=ctx.transform(doc);
		}
		
		
		if(getBoolean(many, false)){
			BsonArray bsonArray= BsonArray.parse(doc);
			List<Document> docs = new ArrayList<Document>();
			for (int i = 0; i < bsonArray.size(); i++) {
				docs.add(Document.parse(bsonArray.get(i).asDocument().toJson()));
			}
			
			
			collection.insertMany(docs);
			DocObjectIdConvertor.convert(docs, idKey);
			current.put(tag,docs );
			
		}else{
			Document d = Document.parse(doc);
			collection.insertOne(d);
			DocObjectIdConvertor.convert(d, idKey);
			current.put(tag, d);
		}
		
		log(String.format("insert doc[%s] success!", collection.getNamespace()), "info");
		
	}
	
	public static void main(String[] args) {
		
		//Object blank=Document.parse("");报错
		//Object n=Document.parse(null);报错
		Object noData=BsonArray.parse("[{\"a\":\"aa\"},{\"b\":\"bb\"}]");
		System.out.println("111222");
	}

}
