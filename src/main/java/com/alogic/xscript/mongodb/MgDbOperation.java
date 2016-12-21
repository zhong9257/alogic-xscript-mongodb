package com.alogic.xscript.mongodb;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.mongodb.client.MongoDatabase;

public abstract class MgDbOperation extends AbstractLogiclet {
	  /**
     * hadmin的cid
     */
    private String pid = "$mg-db";
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

    public MgDbOperation(String tag, Logiclet p) {
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
    	MongoDatabase dataBase = ctx.getObject(pid);
        if (dataBase == null) {
            throw new BaseException("core.no_dataBase", "It must be in a mg-db context,check your script.");
        }
        if (StringUtils.isNotEmpty(id)) {
            onExecute(dataBase, root, current, ctx, watcher);
        }
    }

    protected abstract void onExecute(MongoDatabase dataBase, Map<String, Object> root, Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher);

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
