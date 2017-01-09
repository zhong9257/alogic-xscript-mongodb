package com.alogic.xscript.mongodb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.BsonArray;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.mongodb.util.DocObjectIdConvertor;
import com.alogic.xscript.mongodb.util.FilterBuilder;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

/**
 * 替换文档 version 1.0
 * @author cenwan
 *
 */
public class MgReplaceOne extends MgTableOperation{
	
	private Properties filterProperties;
	
    protected FilterBuilder fb = null;
    protected Bson filter = null;
    protected String idKey = "";
    protected String doc="";
	protected String docNode="doc";
	protected String docResultNode="doc";
    protected String upsert = "";//是否在没有匹配到文档的时候插入doc
    protected String bypassDocumentValidation = "";//是否启用文档验证
    protected String collation = "";
    	
	protected static JsonProvider provider = null;	
	static {
		provider = JsonProviderFactory.createProvider();
	}
	/**
	 * 子节点
	 */
	protected List<Logiclet> children = new ArrayList<Logiclet>(); // NOSONAR

	public MgReplaceOne(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
    public void configure(Element element, Properties props) {
		XmlElementProperties p = new XmlElementProperties(element, props);
        
        tag = PropertiesConstants.getRaw(p, "tag", "$mg-replaceone");
        idKey = PropertiesConstants.getRaw(props, "idKey", "");
        doc = PropertiesConstants.getRaw(p, "doc", "");
		docNode = PropertiesConstants.getRaw(p, "docNode", docNode);
		docResultNode = PropertiesConstants.getRaw(p, "docResultNode", docResultNode);
		upsert = PropertiesConstants.getRaw(p, "upsert", "");
        bypassDocumentValidation = PropertiesConstants.getRaw(p, "bypassDocumentValidation", "");
        collation = PropertiesConstants.getRaw(p, "collation", "");
        
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
		
		if (fb != null) {
			filter = fb.getFilter(filterProperties, ctx);	
		}
		
		UpdateResult result = null;
		try {
			UpdateOptions updateOptions = new UpdateOptions();
        	if(upsert != ""){
        		updateOptions.upsert(getBoolean(upsert, false));
        	}
        	
        	if(bypassDocumentValidation != ""){
        		updateOptions.bypassDocumentValidation(getBoolean(bypassDocumentValidation, false));
			}
			result = collection.replaceOne(filter, Document.parse(doc), updateOptions);
			
		} catch (Exception e) {
			log("filter Can not be empty", "error");
		}	
		current.put(tag, result);
    }
}
