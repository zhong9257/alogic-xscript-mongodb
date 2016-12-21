package com.alogic.xscript.mongodb.util;

/**
 * {@link com.alogic.xscript.mongodb.util.ValueConvertor}支持的类型。
 * @author zhongyi
 *
 */
public enum ValueType {
	TSTRING("string"),
	TDOUBLE("double"),TLONG("long"),TINTEGER("integer"),
	TBOOLEAN("boolean"),
	TOBJECTID("objectId"),
	TNULL("null"),
	TDATE("Date");
	
	private ValueType(String type){
		this.type=type;
	}
	
	private String type;

	public String getType() {
		return type;
	}
}
