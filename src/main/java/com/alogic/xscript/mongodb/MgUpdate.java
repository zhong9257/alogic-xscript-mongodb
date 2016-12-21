package com.alogic.xscript.mongodb;

import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.w3c.dom.Element;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.mongodb.util.FilterBuilder;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;

public class MgUpdate extends MgTableOperation {

	protected String doc = "";
	protected String many = "false";
	protected String idKey = "_id";
	protected String tagValue = "$mg-update";

	/**
	 * Filter Builder
	 */
	protected FilterBuilder fb = null;
	protected Bson filter = null;
	private Properties filterProperties;

	public MgUpdate(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);

	}

	@Override
	public void configure(Element e, Properties p) {
		doc = PropertiesConstants.getRaw(p, "doc", "");
		many = PropertiesConstants.getRaw(p, "many", many);
		idKey = PropertiesConstants.getRaw(p, "idKey", idKey);
		tagValue = PropertiesConstants.getRaw(p, "tagValue", tagValue);

		Properties props = new XmlElementProperties(e, p);
		filterProperties = props;

		Element filter = XmlTools.getFirstElementByPath(e, "filter");
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

		if (fb != null) {
			filter = fb.getFilter(filterProperties, ctx);
		}
		Document d = Document.parse(ctx.transform(doc));
		if (getBoolean(many, false)) {
			UpdateResult r = collection.updateOne(filter, d);
			current.put(tagValue, r);
		} else {
			UpdateResult r = collection.updateMany(filter, d);
			current.put(tagValue, r);
		}

		log(String.format("update doc[%s] success!", collection.getNamespace()), "info");

	}

}
