package com.alogic.xscript.mongodb;

import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.w3c.dom.Element;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.mongodb.util.DocObjectIdConvertor;
import com.alogic.xscript.mongodb.util.FilterBuilder;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndDeleteOptions;
import com.mongodb.client.model.Sorts;

/**
 * 查询并删除  version 1.0
 * @author cenwan
 *
 */
public class MgQueryOneAndDelete extends MgTableOperation{
	
	private Properties filterProperties;
	
    protected FilterBuilder fb = null;
    protected Bson filter = null;
    protected String projection = null;
    protected String sort = null;
    protected String maxTime = null;//查询超时设置,单位为秒
    protected String collation = null;//mongodb3.4+新增功能，预留
    protected String idKey = null;

	public MgQueryOneAndDelete(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
    public void configure(Element e, Properties p) {
        Properties props = new XmlElementProperties(e, p);
        filterProperties=props;
        
        tag = PropertiesConstants.getRaw(props, "tag", "$mg-queryoneanddelete");
        projection = PropertiesConstants.getRaw(props, "projection", "");
        sort = PropertiesConstants.getRaw(props, "sort", "");
        maxTime = PropertiesConstants.getRaw(props, "maxTime", "");
        sort = PropertiesConstants.getRaw(props, "sort", "");
        collation = PropertiesConstants.getRaw(props, "collation", "");
        idKey = PropertiesConstants.getRaw(props, "idKey", "");
        
        Element filter = XmlTools.getFirstElementByPath(e, "filter");
        if (filter != null) {
            FilterBuilder.TheFactory f = new FilterBuilder.TheFactory();
            try {
                fb = f.newInstance(filter, props, "module");
            } catch (Exception ex) {
                log("Can not create instance of FilterBuilder.", "error");
            }
        }
        configure(props);
    }

	@Override
	protected void onExecute(MongoCollection<Document> collection, Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		Document document = null;
		try {
			filter=fb.getFilter(filterProperties, ctx);
			FindOneAndDeleteOptions options =  new FindOneAndDeleteOptions();
			if(projection != ""){
				options.projection(fields(include((projection))));
			}
			if(sort != ""){
        		String str = sort;
        		if (str.indexOf(' ') == (-1)) {
        			options = options.sort(Sorts.ascending(MgQuery.stringToArray(str)));
        		} else {
        			while(true){
        				int index = str.indexOf(' ');
        				if(str.substring(index + 1, index + 2).equals("D")){
        					options = options.sort(Sorts.descending(MgQuery.stringToArray(str.substring(0, index))));
        					if(index + 6 > str.length()){
        						break;
        					}
        					str = str.substring(index + 6);
        				} else if(str.substring(index + 1, index + 2).equals("A")){
        					options = options.sort(Sorts.ascending(MgQuery.stringToArray(str.substring(0, index))));
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
			if(maxTime != ""){
				options.maxTime(Long.parseLong(maxTime),TimeUnit.SECONDS);
			}
			document = collection.findOneAndDelete(filter, options);
		} catch (Exception e) {
			log("filter Can not be empty", "error");
		}
		DocObjectIdConvertor.convert(document, idKey);
		current.put(tag, document);
    }
}
