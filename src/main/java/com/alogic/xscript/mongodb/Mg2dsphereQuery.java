package com.alogic.xscript.mongodb;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.w3c.dom.Element;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.mongodb.util.FilterBuilder;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

/**
 * 地理空间查询 version 1.0
 * @author cenwan
 *
 */
public class Mg2dsphereQuery extends MgTableOperation{
	
	protected String mode="";
	protected String field ="";
    protected FilterBuilder fb = null;
    protected Bson filter = null;

	public Mg2dsphereQuery(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
    public void configure(Element e, Properties p) {
        Properties props = new XmlElementProperties(e, p);
        
		mode = PropertiesConstants.getRaw(props, "mode", "");
		field = PropertiesConstants.getRaw(props, "field", "");
		tag = PropertiesConstants.getRaw(props, "tag", "$mg-2dspherequery");
		
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
		
		FindIterable<Document> iter = null;
		List<String> fieldsArray = MgQuery.stringToArray(field);
		
		switch(mode){
			case "inPolygon":
				List<List<Double>> polygon = new LinkedList<>();
				for(int i = 1; i < fieldsArray.size(); i += 2){
					List<Double> pt = new LinkedList<>();
					pt.add(i, Double.parseDouble(fieldsArray.get(i)));
					pt.add(i + 1, Double.parseDouble(fieldsArray.get(i + 1)));
					polygon.add(pt);
				}
				iter = collection.find(Filters.geoWithinPolygon(fieldsArray.get(0), polygon));
				break;
				
			case "inBox"://两个坐标点来表示，左下角--->右上角
				iter = collection.find(Filters.geoWithinBox(fieldsArray.get(0),
						getDouble(fieldsArray.get(1), 0), getDouble(fieldsArray.get(2), 0),
						getDouble(fieldsArray.get(3), 0), getDouble(fieldsArray.get(4), 0)));
				break;
				
			case "inCircle"://一个坐标点，半径
				iter = collection.find(Filters.geoWithinCenter(fieldsArray.get(0), 
						getDouble(fieldsArray.get(1), 0), getDouble(fieldsArray.get(2), 0), 
						getDouble(fieldsArray.get(3), 0)));
				break;
				
			case "near"://一个坐标点，minDistance，maxDistance
				iter = collection.find(Filters.near(fieldsArray.get(0), 
						getDouble(fieldsArray.get(1), 0), getDouble(fieldsArray.get(2), 0),
						getDouble(fieldsArray.get(2), 0), getDouble(fieldsArray.get(3), 0)));
				break;
		}
		
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
		current.put(tag,list);
    }
}
