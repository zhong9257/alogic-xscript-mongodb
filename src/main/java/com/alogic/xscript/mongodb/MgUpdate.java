package com.alogic.xscript.mongodb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.mongodb.util.FilterBuilder;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;

public class MgUpdate extends MgTableOperation {

	protected String doc = "";
	protected String many = "false";
	protected String idKey = "_id";
	
	protected String docNode="doc";
	protected String docResultNode="doc";

	/**
	 * Filter Builder
	 */
	protected FilterBuilder fb = null;
	protected Bson filter = null;
	private Properties filterProperties;
	
	/**
	 * 更新内容来源的子节点
	 */
	protected List<Logiclet> children = new ArrayList<Logiclet>(); // NOSONAR
	
	
	protected static JsonProvider provider = null;	
	static {
		provider = JsonProviderFactory.createProvider();
	}

	public MgUpdate(String tag, Logiclet p) {
		super(tag, p);
	}


	@Override
	public void configure(Element element, Properties props) {
		
		Properties p = new XmlElementProperties(element,props);
		filterProperties = props;
		
		
		doc = PropertiesConstants.getRaw(p, "doc", "");
		many = PropertiesConstants.getRaw(p, "many", many);
		idKey = PropertiesConstants.getRaw(p, "idKey", idKey);
		tag = PropertiesConstants.getRaw(p, "tag", "$mg-update");
		docNode = PropertiesConstants.getRaw(p, "docNode", docNode);
		docResultNode = PropertiesConstants.getRaw(p, "docResultNode", docResultNode);
		
		
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



		Element filter = XmlTools.getFirstElementByPath(element, "filter");
		if (filter != null) {
			FilterBuilder.TheFactory f = new FilterBuilder.TheFactory();
			try {
				fb = f.newInstance(filter, props, "module");
			} catch (Exception ex) {
				log("Can not create instance of FilterBuilder.", "error");
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
		

		if (fb != null) {
			filter = fb.getFilter(filterProperties, ctx);	
		}else{
			filter=Document.parse("{}");
		}
		

		if (getBoolean(many, false)) {
			UpdateResult r = collection.updateOne(filter, Document.parse(doc));
			current.put(tag, updateResultConvert(r));
		} else {
			UpdateResult r = collection.updateMany(filter,Document.parse(doc));
			current.put(tag, updateResultConvert(r));
		}

		log(String.format("update doc[%s] success!", collection.getNamespace()), "info");

	}
	

	private Map<String,Object> updateResultConvert(UpdateResult r){
		
		Map<String,Object> m = new HashMap<String,Object>();
		if(null==r) return m;
		
		m.put("matchedCount", r.getMatchedCount());
		m.put("modifiedCount", r.getModifiedCount());
		if(null!=r.getUpsertedId()){
			m.put("upsertedId", r.getUpsertedId().toString());
			
		}else{
			m.put("upsertedId", "");
		}	
		return m;
	}

}
