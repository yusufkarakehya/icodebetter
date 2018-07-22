package iwb.exception;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;

import iwb.domain.db.Log5Base;
import iwb.domain.db.W5Table;
import iwb.domain.helper.W5TableRecordHelper;
import iwb.util.FrameworkCache;
import iwb.util.FrameworkSetting;
import iwb.util.GenericUtil;
import iwb.util.LocaleMsgCache;


public class IWBException extends RuntimeException {
	private	String errorType;
	private	String objectType;
	private	int objectId;
	private	String sql;
	private Map<String, Object> scd;
	public IWBException(String errorType, String objectType, int objectId, String sql, String message, Throwable cause) {
		super(message, cause);
		this.errorType=errorType;//security, validation, framework, definition
		this.objectType=objectType;
		this.objectId=objectId;
		this.sql=sql;
	}

	public static IWBException convertToIWBException(Exception e){
		return null;
	}

	public String toHtmlString(){
		StringBuilder b = new StringBuilder();
		b.append("<table>");
		b.append("<tr><td><b>Error Type</b></td><td>").append(errorType).append("</td></tr>");
		b.append("<tr><td><b>Error</b></td><td>").append(getMessage()).append("</td></tr>");
		if(FrameworkCache.getAppSettingIntValue(0, "debug")!=0){
			b.append("<tr><td><b>Object Type</b></td><td>").append(objectType).append("</td></tr>");
			b.append("<tr><td><b>Object Id</b></td><td>").append(objectId).append("</td></tr>");
		}
		b.append("</table>");
		return b.toString();
	}

	public String toJsonString(Map<String, Object> scd){
		String locale = (scd == null) ? FrameworkCache.getAppSettingStringValue(0, "locale") : (String)scd.get("locale");
		int customizationId =(scd == null) ? 0 : GenericUtil.uInt(scd.get("customizationId")) ;
		StringBuilder b = new StringBuilder();
		b.append("{\"success\":false,\n\"errorType\":\"").append(errorType).append("\"");
		String msg = getMessage();
		String cause = getCause()==null ? null :getCause().getMessage();
		if(msg!=null){
			b.append(",\"error\":\"").append(GenericUtil.stringToJS2(msg)).append("\"");
		}
		
		if(objectType!=null){
			b.append(",\n\"objectType\":\"").append(GenericUtil.stringToJS2(objectType)).append("\"");
			if(objectId!=0){
				b.append(",\n\"objectId\":").append(objectId);
			}
		}

		if(FrameworkSetting.debug){
			b.append(",\n\"stack\":\"").append(GenericUtil.stringToJS2(ExceptionUtils.getFullStackTrace(this))).append("\"");
			if(sql!=null)b.append(",\n\"sql\":\"").append(GenericUtil.stringToJS2(sql)).append("\"");
		}
		
	

		return b.append("}").toString();
	}

    private	StringBuilder serializeTableHelperList (int customizationId, String xlocale, List<W5TableRecordHelper> ltrh){//TODO aynisi extjs de de var
    	StringBuilder buf = new StringBuilder();
    	boolean bq=false;
    	buf.append("[");
		if(ltrh!=null)for(W5TableRecordHelper trh:ltrh){
			W5Table dt=FrameworkCache.getTable(customizationId, trh.getTableId());
			if(dt==null)break;
			if(bq)buf.append(","); else bq=true;
			buf.append("{\"tid\":").append(trh.getTableId()).append(",\"tpk\":").append(trh.getTablePk()).append(",\"tcc\":").append(trh.getCommentCount()).append(",\"tdsc\":\"").append(LocaleMsgCache.get2(customizationId, xlocale,dt.getDsc())).append("\"").append(",\"dsc\":\"").append(GenericUtil.stringToJS2(trh.getRecordDsc())).append("\"}");			        			
		}
		buf.append("]");
    	return buf;
    }
	public String getErrorType() {
		return errorType;
	}


	public String getObjectType() {
		return objectType;
	}


	public int getObjectId() {
		return objectId;
	}


	public String getSql() {
		return sql;
	}



}
