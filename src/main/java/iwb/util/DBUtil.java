package iwb.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import iwb.exception.IWBException;

public class DBUtil {
	public static Object[] filterExt4SQL(String code, Map<String, Object> scd, Map<String, String> requestParams, Map<String, Object> obj) {
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList();
		Object[] res = new Object[2];
		res[0]=sql;res[1]=params;
		if(code==null || code.length()==0)return res;
		sql.append(code);
		for(int bas = sql.indexOf("${"); bas>=0; bas=sql.indexOf("${",bas+2)){
			int bit = sql.indexOf("}", bas+2);
			String subStr = sql.substring(bas+2, bit);
			String o = null;
			if(subStr.startsWith("scd.")){
				if(scd!=null && scd.get(subStr.substring(4))!=null)o=scd.get(subStr.substring(4)).toString(); // session
			} else if(subStr.startsWith("req.")){
				if(requestParams!=null && requestParams.get(subStr.substring(4))!=null)o=requestParams.get(subStr.substring(4)).toString(); // request
			} else if(subStr.startsWith("obj.")){
				if(obj!=null && obj.get(subStr.substring(4))!=null)o=obj.get(subStr.substring(4)).toString(); // object
			} else if(subStr.startsWith("app.")){
				o = FrameworkCache.getAppSettingStringValue(scd, subStr.substring(4));
			} else {
				o = LocaleMsgCache.get2((Integer)scd.get("customizationId"),(String)scd.get("locale"), subStr); // getMsgHTML de olabilirdi
			}
			if(bas>0 &&  sql.charAt(bas-1)=='\'' && bit<sql.length()-1 && sql.charAt(bit+1)=='\''){ // bu string'tir
				params.add(o);bas--;
				sql.replace(bas, bit+2, "?");
			} else try{ //sayidir
				
				params.add(o==null || o.length()==0 ? null : new BigDecimal(o));
				sql.replace(bas, bit+1, "?");
			} catch (Exception e){
				throw new IWBException("sql","filterExt4SQL", 0, sql.toString(), "String2BigDecimal("+o+")", null);
			}
		}
		return res;
	}
	
	public static Object[] filterExt4InfluxQL(String code, Map<String, Object> scd, Map<String, String> requestParams, Map<String, Object> obj) {
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList();
		Object[] res = new Object[2];
		res[0]=sql;res[1]=params;
		if(code==null || code.length()==0)return res;
		sql.append(code);
		for(int bas = sql.indexOf("${"); bas>=0; bas=sql.indexOf("${",bas+2)){
			int bit = sql.indexOf("}", bas+2);
			String subStr = sql.substring(bas+2, bit);
			String o = null;
			if(subStr.startsWith("scd.")){
				if(scd!=null && scd.get(subStr.substring(4))!=null)o=scd.get(subStr.substring(4)).toString(); // session
			} else if(subStr.startsWith("req.")){
				if(requestParams!=null && requestParams.get(subStr.substring(4))!=null)o=requestParams.get(subStr.substring(4)).toString(); // request
			} else if(subStr.startsWith("obj.")){
				if(obj!=null && obj.get(subStr.substring(4))!=null)o=obj.get(subStr.substring(4)).toString(); // object
			} else if(subStr.startsWith("app.")){
				o = FrameworkCache.getAppSettingStringValue(scd, subStr.substring(4));
			} else {
				o = LocaleMsgCache.get2((Integer)scd.get("customizationId"),(String)scd.get("locale"), subStr); // getMsgHTML de olabilirdi
			}
			params.add(o==null || o.length()==0 ? null : o);
			sql.replace(bas, bit+1, "?");
		}
		return res;
	}
	
	public static int java2iwbType(int javaType){
		switch(javaType){
		case java.sql.Types.VARCHAR:
			return 1;
		case java.sql.Types.DATE: 
		case java.sql.Types.TIMESTAMP:
		case java.sql.Types.TIME:
			return 2;
		case java.sql.Types.DOUBLE:
		case java.sql.Types.FLOAT:
		case java.sql.Types.DECIMAL:
			return 3;
		case java.sql.Types.INTEGER:
		case java.sql.Types.SMALLINT:
		case java.sql.Types.TINYINT:
		case java.sql.Types.NUMERIC:
			return 4;
		case java.sql.Types.BOOLEAN:
			return 5;
		default :return 0;
		}
	}
}
