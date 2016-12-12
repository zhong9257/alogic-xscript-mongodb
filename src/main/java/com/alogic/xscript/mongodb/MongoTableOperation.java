package com.alogic.xscript.mongodb;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.mongodb.client.MongoCollection;

public abstract class MongoTableOperation extends AbstractLogiclet {
	  /**
     * hadmin的cid
     */
    private String pid = "$mongo-table";
    /**
     * 数据集
     */
    protected String tag = "data";

    /**
     * 获取数据的默认编码
     */
    protected static String CHARSET_NAME = "UTF-8";

    /**
     * 返回结果的id
     */
    protected String id;

    public MongoTableOperation(String tag, Logiclet p) {
        super(tag, p);
    }

    @Override
    public void configure(Properties p) {
        super.configure(p);
        pid = PropertiesConstants.getString(p, "pid", pid, true);
        tag = PropertiesConstants.getString(p, "tag", tag, true);
        id = PropertiesConstants.getString(p, "id", "$" + getXmlTag(), true);

    }

    @Override
    protected void onExecute(Map<String, Object> root, Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
    	MongoCollection<Document> collection = ctx.getObject(pid);
        if (collection == null) {
            throw new BaseException("core.no_collection", "It must be in a mongo-table context,check your script.");
        }
        if (StringUtils.isNotEmpty(id)) {
            onExecute(collection, root, current, ctx, watcher);
        }
    }

    protected abstract void onExecute(MongoCollection<Document> collection, Map<String, Object> root, Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher);

	protected boolean getBoolean(String value,boolean dftValue){
		try{
			return Boolean.parseBoolean(value);
		}catch (NumberFormatException ex){
			return dftValue;
		}
	}
	
	protected long getLong(String value,long dftValue){
		try{
			return Long.parseLong(value);
		}catch (NumberFormatException ex){
			return dftValue;
		}
	}	
	
	protected int getInt(String value,int dftValue){
		try{
			return Integer.parseInt(value);
		}catch (NumberFormatException ex){
			return dftValue;
		}
	}	
	
	protected double getDouble(String value,double dftValue){
		try{
			return Double.parseDouble(value);
		}catch (NumberFormatException ex){
			return dftValue;
		}
	}	
	
	protected float getFloat(String value,float dftValue){
		try{
			return Float.parseFloat(value);
		}catch (NumberFormatException ex){
			return dftValue;
		}
	}	

}
