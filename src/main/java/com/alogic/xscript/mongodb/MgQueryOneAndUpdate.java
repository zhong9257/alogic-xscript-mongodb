package com.alogic.xscript.mongodb;

import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Sorts;

/**
 * 查询更新 version 1.0
 * @author cenwan
 *
 */
public class MgQueryOneAndUpdate extends MgTableOperation{
	
	private Properties filterProperties;
	
    protected FilterBuilder fb = null;
    protected Bson filter = null;
    
    protected String idKey = "";
    protected String doc="";
	protected String docNode="doc";
	protected String docResultNode="doc";
    protected String projection = "";
    protected String sort = "";
    protected String returnDocument = "";//返回替换前的文档还是替换后的文档，BEFORE或AFTER
    protected String upsert = "";//是否在没有匹配到文档的时候插入doc
    protected String maxTime = "";//查询超时设置,单位为秒
    protected String bypassDocumentValidation = "";//是否启用文档验证
    protected String collation = "";
   
	/**
	 * 更新内容来源的子节点
	 */
	protected List<Logiclet> children = new ArrayList<Logiclet>(); // NOSONAR
	
	protected static JsonProvider provider = null;	
	static {
		provider = JsonProviderFactory.createProvider();
	}

	public MgQueryOneAndUpdate(String tag, Logiclet p) {
		super(tag, p);
	}
	@Override
    public void configure(Element element, Properties props) {
		XmlElementProperties p = new XmlElementProperties(element, props);
		filterProperties = props;
		
        tag = PropertiesConstants.getRaw(p, "tag", "$mg-queryoneandupdate");
        idKey = PropertiesConstants.getRaw(p, "idKey", "");
        doc = PropertiesConstants.getRaw(p, "doc", "");
		docNode = PropertiesConstants.getRaw(p, "docNode", docNode);
		docResultNode = PropertiesConstants.getRaw(p, "docResultNode", docResultNode);
        projection = PropertiesConstants.getRaw(p, "projection", "");
        sort = PropertiesConstants.getRaw(p, "sort", "");
        returnDocument = PropertiesConstants.getRaw(p, "returnDocument", "BEFORE");
        maxTime = PropertiesConstants.getRaw(p, "maxTime", "");
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
		
		if (fb != null) {
			filter = fb.getFilter(filterProperties, ctx);	
		}else{
			filter=Document.parse("{}");
		}
		
		FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();

		if(sort != ""){
    		String str = sort;
    		if (str.indexOf(' ') == (-1)) {
    			options.sort(Sorts.ascending(MgQuery.stringToArray(str)));
    		} else {
    			while(true){
    				int index = str.indexOf(' ');
    				if(str.substring(index + 1, index + 2).equals("D")){
    					options.sort(Sorts.descending(MgQuery.stringToArray(str.substring(0, index))));
    					if(index + 6 > str.length()){
    						break;
    					}
    					str = str.substring(index + 6);
    				} else if(str.substring(index + 1, index + 2).equals("A")){
    					options.sort(Sorts.ascending(MgQuery.stringToArray(str.substring(0, index))));
    					if(index + 5 > str.length()){
    						break;
    					}
    					str = str.substring(index + 5);
    				} else {
    					break;
    				}
    			}
    		}
    	}
		if(projection != ""){
			options.projection(fields(include((projection))));
		}
		if (upsert != "") {
			options.upsert(getBoolean(upsert, false));
		}
		ReturnDocument rd = null;
        if(returnDocument == "AFTER"){
        	rd = ReturnDocument.AFTER;
        } else {
        	rd = ReturnDocument.BEFORE;
        }
        options.returnDocument(rd);
        if(maxTime != ""){
			options.maxTime(Long.parseLong(maxTime),TimeUnit.SECONDS);
		}
		if (bypassDocumentValidation != "") {
			options.bypassDocumentValidation(getBoolean(bypassDocumentValidation, false));
		}
		Document result = collection.findOneAndUpdate(filter, Document.parse(doc), options);
		
        DocObjectIdConvertor.convert(result, idKey);
		current.put(tag, result);
    }
}
