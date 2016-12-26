package com.alogic.xscript.mongodb.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bson.types.ObjectId;

/**内容类型转换器。将字符串按照给定类型转换。
 * @author zhongyi
 *
 */
public class ValueConvertor {
	
	
	public  static Object convert(String type,String value,String pattten){
		if(null==type) return ValueType.TSTRING.getType();
		type=type.toLowerCase();
		
		if("".equals(type)||ValueType.TSTRING.getType().equals(type)){
			return value;
		}else if(ValueType.TINTEGER.getType().equals(type)){
			return Integer.parseInt(value);
		}else if(ValueType.TLONG.getType().equals(type)){
			return Long.parseLong(value);
		}else if(ValueType.TDOUBLE.getType().equals(type)){
			return Double.parseDouble(value);
		}else if(ValueType.TBOOLEAN.getType().equals(type)){
			return Boolean.parseBoolean(value);
		}else if(ValueType.TOBJECTID.getType().equals(type)){
			return new ObjectId(value);
		}else if(ValueType.TNULL.getType().equals(type)){
			return null;
		}else if(ValueType.TDATE.getType().equals(type)){
			SimpleDateFormat sdf = new SimpleDateFormat(pattten);
			Date date;
			try {
				date = sdf.parse(value);
			} catch (ParseException e) {
				throw new RuntimeException(e.getMessage());
			}
			return date;
		}
		
		return value;
	}

}

