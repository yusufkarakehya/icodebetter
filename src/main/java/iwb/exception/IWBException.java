package iwb.exception;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;

import iwb.domain.db.W5Table;
import iwb.domain.db.W5TempLogRecord;
import iwb.domain.helper.W5TableRecordHelper;
import iwb.enums.FieldDefinitions;
import iwb.util.FrameworkCache;
import iwb.util.FrameworkSetting;
import iwb.util.GenericUtil;
import iwb.util.LocaleMsgCache;


public class IWBException extends RuntimeException {
	private	String errorType;
	private	String objectType;
	private	int objectId;
	private	String sql;
	private List<W5TempLogRecord> logRecords;
	public IWBException(String errorType, String objectType, int objectId, String sql, String message, Throwable cause) {
		super(message, cause);
		this.errorType=errorType;//security, validation, framework, definition
		this.objectType=objectType;
		this.objectId=objectId;
		this.sql=sql;
	}

	public IWBException(String errorType, String objectType, int objectId, String sql, String message, List<W5TempLogRecord> logRecords, Throwable cause) {
		super(message, cause);
		this.errorType=errorType;//security, validation, framework, definition
		this.objectType=objectType;
		this.objectId=objectId;
		this.sql=sql;
		this.logRecords=logRecords;
	}
	public String toHtmlString(String locale){
		StringBuilder b = new StringBuilder();
		if(!GenericUtil.isEmpty(errorType) && errorType.equals("license")){
			b.append("<form action=\"validateLicense\"><table>");
			b.append("<tr><td><b>License Error</b></td><td>").append(LocaleMsgCache.get2(0, locale, objectType)).append("</td></tr>");
			b.append("<tr><td><b>Please Enter Valid License Key</b></td><td><input name=license_key size=100></td></tr>");
			b.append("<tr><td> </td><td><input type=submit value=\"GO\"></td></tr>");
			b.append("</table></form>");
			
		} else {
			b.append("<table>");
			b.append("<tr><td><b>Error Type</b></td><td>").append(errorType).append("</td></tr>");
			b.append("<tr><td><b>Error</b></td><td>").append(getMessage()).append("</td></tr>");
			if(FrameworkCache.getAppSettingIntValue(0, "debug")!=0){
				b.append("<tr><td><b>Object Type</b></td><td>").append(objectType).append("</td></tr>");
				b.append("<tr><td><b>Object Id</b></td><td>").append(objectId).append("</td></tr>");
			}
			b.append("</table>");
		}
		return b.toString();
	}

	public String toJsonString(Map<String, Object> scd){
		String locale = (scd == null) ? FrameworkCache.getAppSettingStringValue(0, "locale") : (String)scd.get("locale");
		int customizationId =(scd == null) ? FrameworkCache.getAppSettingIntValue(0, "default_customization_id") : GenericUtil.uInt(scd.get("customizationId")) ;
		StringBuilder b = new StringBuilder();
		b.append("{\"success\":false,\n\"errorType\":\"").append(errorType).append("\"");
		String msg = getMessage();
		String cause = getCause()==null ? null :getCause().getMessage();
		if(msg!=null){
			if(FrameworkCache.getAppSettingIntValue(customizationId, "debug")!=1){				
				if(msg.contains("ORA-")){
					String temp = msg.substring(0,9);
					if(!temp.equals("ORA-00001"))
					{
						msg = LocaleMsgCache.get2(0, locale, FrameworkCache.wExceptions.get(temp));
					}
					else
					{
						temp = msg.substring(msg.indexOf('(')+1, msg.indexOf(')'));
						msg = LocaleMsgCache.get2(0, locale, FrameworkCache.wExceptions.get(temp));
					}
				}
				else if(cause != null && cause.contains("ORA-")){
					String temp= cause.substring(0,9);
					if(temp.equals("ORA-02292")) // FOREIGN KEY
					{
						temp = cause.substring(cause.indexOf('(')+1, cause.indexOf(')'));
						msg = LocaleMsgCache.get2(0, locale, FrameworkCache.wExceptions.get(temp));
					}					
				}
			}
			
			else
			{
				int index = msg.indexOf("ORA-");
				if(index!=-1){
					int lastIndex = msg.indexOf(":",index+4);
					if(lastIndex==index+9){
						String dbErrorCode = msg.substring(index,lastIndex);
						String errorMsg = LocaleMsgCache.get2(0, locale, dbErrorCode);
						if(errorMsg!=null)
							msg = errorMsg + " (" + msg + ")";
					}
				}
			}
			
			
			if(msg==null){
				msg=getMessage();
				int index = msg.indexOf("ORA-");
				
				if(index!=-1){
					msg = LocaleMsgCache.filter2(0, locale, msg).toString(); 
					int lastIndex = msg.indexOf(":",index+4);
					msg = msg.substring(lastIndex+1);
					if(FrameworkCache.getAppSettingIntValue(customizationId, "debug")!=1){//debug modda değilse hatanın hangi satırda olduğu ile ilgili mesajlar kaldırılıyor
						index = msg.indexOf("ORA-");
						if(index!=-1) msg = msg.substring(0,index-1);
					}
				}												
			}
			b.append(",\n \"error\":\"").append(GenericUtil.stringToJS2(msg)).append("\"");
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
		
		if(logRecords!=null && !logRecords.isEmpty()){
			short logLevel = (short)FrameworkCache.getAppSettingIntValue(customizationId, "db_proc_log_level");
			b.append(",\n\"logErrors\":[");
			boolean bx = false;
			for(W5TempLogRecord tlr:logRecords)if(tlr.getLogLevel()>=logLevel){
				if(bx)b.append(",\n");else bx=true;
				b.append("{\"table_id\":").append(tlr.getTableId()).append(",\"table_pk\":").append(tlr.getTablePk()).append(",\"log_level\":").append(tlr.getLogLevel()).append(",\"dsc\":\"").append(GenericUtil.stringToJS2(tlr.getDsc())).append("\"");
				if(!GenericUtil.isEmpty(tlr.get_parentRecords())){
					b.append(",\"").append(FieldDefinitions.queryFieldName_HierarchicalData).append("\":").append(serializeTableHelperList(customizationId,locale,tlr.get_parentRecords()));
				}
				b.append("}");
			}

			b.append("]");
			
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


	public List<W5TempLogRecord> getLogRecords() {
		return logRecords;
	}
	
	
}
