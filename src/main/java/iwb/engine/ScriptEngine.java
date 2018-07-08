package iwb.engine;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.NativeObject;

import iwb.dao.RdbmsDao;
import iwb.dao.rdbms_impl.PostgreSQL;
import iwb.domain.db.W5Project;
import iwb.domain.db.W5Table;
import iwb.domain.result.W5DbFuncResult;
import iwb.domain.result.W5FormResult;
import iwb.exception.IWBException;
import iwb.util.FrameworkCache;
import iwb.util.FrameworkSetting;
import iwb.util.GenericUtil;
import iwb.util.LocaleMsgCache;
import iwb.util.UserUtil;

public class ScriptEngine {
	Map<String, Object> scd;
	Map<String,String> requestParams;
	private RdbmsDao dao;
	private FrameworkEngine engine;

	public Object[] sqlQuery(String sql){
		List l = dao.executeSQLQuery2Map(sql, null); 
		return GenericUtil.isEmpty(l) ? null : l.toArray();
	}
	
	/*public Object tsqlQuery(String sql, String dbName){
		return engine.executeInfluxQuery(scd, sql, dbName);
	}
	public void tsqlInsert(String measurement, NativeObject jsTags, NativeObject jsFields){
		tsqlInsert(measurement, jsTags, jsFields, null);
	}
	public void tsqlInsert(String measurement, NativeObject jsTags, NativeObject jsFields, String date){
		engine.insertInfluxRecord(scd, measurement, fromNativeObject2Map2(jsTags, true), fromNativeObject2Map2(jsFields, false), date);
	}
	public Object tsqlExecute(String sql, String dbName){
		return null;//TODO 
	}
	*/
	
	
	public	String getCurrentDate(){
		return dao.getCurrentDate((Integer)scd.get("customizationId"));
	}
	
	
	public	String getLocMsg(String key){
		return LocaleMsgCache.get2(scd, key);
	}
	
	public String md5hash(String s){
		return dao.getMd5Hash(s);
	}	
	public Object sqlFunc(String s){
		return dao.getSqlFunc(s);
	}
	
	public int compareDates(String date1, String date2){
//		if(date1==null && date2==null)return 0;
//		if(date1==null)return -1;if(date2==null)return 1;
		Date d1 = GenericUtil.uDate(date1), d2 = GenericUtil.uDate(date2);
		if(d1==null || d2==null)
			throw new IWBException("rhino","Invalid Date Format", 0,null, "compareDates("+date1+","+date2+")", null);
		return d1.equals(d2) ? 0 : (d1.after(d2) ? 1: -1);
		
	}
	
	private Map<String, String> fromNativeObject2Map(NativeObject jsRequestParams){
		Map<String, String> rp= new HashMap<String, String>();
		if(jsRequestParams!=null){
			Object[] ids = jsRequestParams.getAllIds();
			if(ids!=null)for(int qi=0;qi<ids.length;qi++){
				Object o  = GenericUtil.rhinoValue(jsRequestParams.get(ids[qi].toString(), null));
				if(o!=null){
					String res = o.toString();
					if(res.endsWith(".0") && GenericUtil.uInt(res.substring(0, res.length()-2))>0)res=res.substring(0, res.length()-2);
					rp.put(ids[qi].toString(), res);
				}
			}
		}
		if(requestParams.containsKey(".w") && !rp.containsKey(".w"))rp.put(".w", requestParams.get(".w"));
		return rp;
	}
	
	
	private Map<String, Object> fromNativeObject2Map2(NativeObject jsRequestParams, boolean forceInt){
		Map<String, Object> rp= new HashMap<String, Object>();
		if(jsRequestParams!=null){
			Object[] ids = jsRequestParams.getAllIds();
			if(ids!=null)for(int qi=0;qi<ids.length;qi++){
				Object o  = GenericUtil.rhinoValue(jsRequestParams.get(ids[qi].toString(), null));
				if(o!=null){
					String res = o.toString();
					if(res.endsWith(".0") && GenericUtil.uInt(res.substring(0, res.length()-2))>0)rp.put(ids[qi].toString(), GenericUtil.uInt(res.substring(0, res.length()-2)));
					else rp.put(ids[qi].toString(), o);
				}
			}
		}
		return rp;
	}
	
	public Object[] runQuery(int queryId, NativeObject jsRequestParams){
		List l = dao.runQuery2Map(scd, queryId, fromNativeObject2Map(jsRequestParams)); 
		return GenericUtil.isEmpty(l) ? null : l.toArray();
	}
	
	
	public void mqBasicPublish(String msg) throws IOException{
		W5Project po = FrameworkCache.wProjects.get(scd.get("projectId"));
		//po.get_mqChannel().basicPublish(po.getProjectUuid(), "", null, msg.toString().getBytes());
	}
	
	
	public void console(Object oMsg){
		console(oMsg, null, null);
	}

	
	public int globalNextval(String seq){
		return GenericUtil.getGlobalNextval(seq);
	}
	
	public void console(String oMsg, String title){
		if(!FrameworkSetting.debug)return;
		console(oMsg, title, null);
	}
	
	public void console(Object oMsg, String title, String level){
		if(!FrameworkSetting.debug)return;
		String s = "(null)";
		if(oMsg!=null){
			if(oMsg instanceof String) s= (String)oMsg;
			else {
				oMsg = GenericUtil.rhinoValue(oMsg);
				if(oMsg!=null){
					if(oMsg instanceof String || oMsg instanceof Integer || oMsg instanceof Long || oMsg instanceof Float || oMsg instanceof Double || oMsg instanceof BigDecimal){
						s = oMsg.toString();
					} else  if(oMsg instanceof Date || oMsg instanceof Timestamp){
						s = oMsg instanceof Timestamp ? GenericUtil.uFormatDateTime((Timestamp)oMsg) : GenericUtil.uFormatDate((Date)oMsg);
					} else  if(oMsg instanceof Object[] || oMsg instanceof List){
						List l;
						if(oMsg instanceof Object[]){
							Object[] oz = (Object[])oMsg;
							l = new ArrayList<>();
							for(int qi=0;qi<oz.length;qi++){
								l.add(GenericUtil.rhinoValue(oz[qi]));								
							}
						} else l = (List)oMsg; 
						s = GenericUtil.fromListToJsonString2Recursive(l);
					} else  if(oMsg instanceof Map){
						s = GenericUtil.fromMapToJsonString2Recursive((Map)oMsg);
					} else {
						s = "Undefined Object Type: "  + oMsg.toString();						
					}
				}
			}
		}
		System.out.println(s);
		if(scd!=null && scd.containsKey("customizationId") && scd.containsKey("userId") && scd.containsKey("sessionId") && requestParams!=null && requestParams.containsKey(".w"))try {
			Map m = new HashMap();
			m.put("success", true);m.put("console", s);
			if(!GenericUtil.isEmpty(title))m.put("title", title);
			if(!GenericUtil.isEmpty(level) && GenericUtil.hasPartInside2("log,warn,error", level))m.put("level", level);
			UserUtil.broadCast((Integer)scd.get("customizationId"), (Integer)scd.get("userId"), (String)scd.get("sessionId"), (String)requestParams.get(".w"), m);
		}catch(Exception e){}
	}
	
	public Object execDbFunc(int dbFuncId, NativeObject jsRequestParams){
		return execDbFunc(dbFuncId, jsRequestParams, (short)0, true, null);
	}
	
	public int getAppSettingInt(String key){
		return FrameworkCache.getAppSettingIntValue(scd, key);
	}
	public int getAppSettingInt(int customizationId, String key){
		return FrameworkCache.getAppSettingIntValue(customizationId, key);
	}
	
	public String getAppSettingString(String key){
		return FrameworkCache.getAppSettingStringValue(scd, key);
	}
	public Object execDbFunc(int dbFuncId, NativeObject jsRequestParams, short execRestrictTip, boolean throwOnError, String throwMessage){
		W5DbFuncResult result = engine.executeDbFunc(scd, dbFuncId, fromNativeObject2Map(jsRequestParams), execRestrictTip); 
		if(throwOnError && !result.getErrorMap().isEmpty()){
			throw new IWBException("rhino","DbFuncId", dbFuncId,null, throwMessage!=null ? LocaleMsgCache.get2(scd, throwMessage) : "Validation Error: " + GenericUtil.fromMapToJsonString2(result.getErrorMap()), null);
		}
		return result;
	}
	
	
	public int sqlExecute(String sql){
		return dao.executeUpdateSQLQuery(sql, null); 
	}
	
	public Object postForm(int formId, int action, NativeObject jsRequestParams){
		return postForm(formId, action, jsRequestParams, "", true, null);
	}
	
	public Object postForm(int formId, int action, NativeObject jsRequestParams, String prefix){
		return postForm(formId, action, jsRequestParams, prefix, true, null);
	}

	public Object postForm(int formId, int action, NativeObject jsRequestParams, String prefix, boolean throwOnError){
		return postForm(formId, action, jsRequestParams, prefix, throwOnError, null);
	}

	
	public Object postForm(int formId, int action, NativeObject jsRequestParams, String prefix, boolean throwOnError, String throwMessage){
		

		W5FormResult result = engine.postForm4Table(scd, formId, action, fromNativeObject2Map(jsRequestParams), prefix);
		if(throwOnError && !result.getErrorMap().isEmpty()){
			throw new IWBException("rhino","FormId", formId,null, throwMessage!=null ? LocaleMsgCache.get2(scd, throwMessage) : "Validation Error: " + GenericUtil.fromMapToJsonString2(result.getErrorMap()), null);
		}
		return result; 
	}
	
	public Map getTableJSON(String tableDsc, String tablePk){
		List<Integer> l = dao.find("select t.tableId from W5Table t where t.dsc=? AND t.customizationId in (0,?) order by t.customizationId desc", tableDsc, scd.get("customizationId"));
		if(l.isEmpty())
			throw new IWBException("rhino","getTableJSON", 0, tableDsc, "table_not_found", null);

		return getTableJSON(l.get(0), tablePk, 0);
	}
	
	public Map getTableJSON(int tableId, String tablePk, int forAction){
		return getTableJSON(tableId, tablePk, forAction, false, null);
	}
	
	public Map getTableJSON(int tableId, String tablePk, int forAction, boolean throwOnError, String throwMessage){

		W5Table t = FrameworkCache.getTable(scd, tableId);
		if(GenericUtil.isEmpty(tablePk) || t==null){
			if(throwOnError)
				throw new IWBException("rhino","getTableJSON", tableId,null, throwMessage!=null ? LocaleMsgCache.get2(scd, throwMessage) : "table_or_key_not_valid", null);
			return null;
		}
		if(forAction!=-1){ // -1:kontrol yok, 0: view, 1: edit, 3:delete
			if(t.getAccessViewTip()==0 && (!FrameworkCache.roleAccessControl(scd,  0) || !FrameworkCache.roleAccessControl(scd,  forAction))){
				throw new IWBException("security","Module", 0, null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_guvenlik_modul_kontrol"), null);
			}
			if(t.getAccessViewUserFields()==null && !GenericUtil.accessControl(scd, t.getAccessViewTip(), t.getAccessViewRoles(), t.getAccessViewUsers())){
				throw new IWBException("security","Table", tableId, null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_guvenlik_tablo_kontrol_goruntuleme"), null);
			}
			if(forAction==1 && t.getAccessUpdateUserFields()==null && !GenericUtil.accessControl(scd, t.getAccessUpdateTip(), t.getAccessUpdateRoles(), t.getAccessUpdateUsers())){
				throw new IWBException("security","Table", tableId, null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_guvenlik_tablo_kontrol_guncelleme"), null);
			}
			if(forAction==3 && t.getAccessDeleteUserFields()==null && !GenericUtil.accessControl(scd, t.getAccessDeleteTip(), t.getAccessDeleteRoles(), t.getAccessDeleteUsers())){
				throw new IWBException("security","Table", tableId, null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_guvenlik_tablo_kontrol_silme"), null);
			}
		}

		StringBuilder s = new StringBuilder();
		s.append("select x.* from ").append(t.getDsc()).append(" x where x.").append(t.get_tableParamList().get(0).getExpressionDsc()).append("=?");
		if(t.get_tableParamList().size()>1)s.append(" AND x.customization_id=").append(scd.get("customizationId"));
		List p= new ArrayList();p.add(t.get_tableParamList().get(0).getParamTip()==1 ? tablePk : GenericUtil.uInt(tablePk));
		List l =dao.executeSQLQuery2Map(s.toString(), p);
		if(GenericUtil.isEmpty(l)){
			if(throwOnError)
				throw new IWBException("rhino","getTableJSON", tableId,null, throwMessage!=null ? LocaleMsgCache.get2(scd, throwMessage) : "record_not_found", null);
			return null;
		}		
		Map mo =(Map)l.get(0);
		
		return mo; 
	}

	public Map callWs(String serviceName, NativeObject jsRequestParams){
		return callWs(serviceName, jsRequestParams, true);		
	}
	
	public Map callWs(String serviceName, NativeObject jsRequestParams, boolean throwFlag){
		Map result = new HashMap();
		result.put("success", true);
		try {
			Map m = engine.callWs(scd, serviceName, fromNativeObject2Map(jsRequestParams));
			if(m!=null){
				if(m.containsKey("errorMsg")){
					if(throwFlag)throw new IWBException("ws", "Error:CallWs", 0, serviceName, m.get("errorMsg").toString(), null);else result.put("success", false); 
				}
				if(m.containsKey("faultcode") && m.containsKey("faultstring")){
					if(throwFlag)throw new IWBException("ws", m.get("faultcode").toString(), 0, serviceName, m.get("faultstring").toString(), null);
					else {
						result.put("success", false);
						result.put("errorMsg", m.get("faultstring"));
					}
				}
				result.putAll(m);
			}
		} catch (IWBException e) {
			throw e;
		} catch (Exception e) {
			if(FrameworkSetting.debug)e.printStackTrace();
			if(e.getCause()!=null && e.getCause() instanceof IWBException)throw (IWBException)e.getCause();
			throw new IWBException("ws", "CallWs", 0, serviceName, "Unhandled Exception: "+e.getMessage(), e.getCause());
		}
		return result;
	}
	
	public String postRecord(int formId, int action, String params){
		return "{success:true}";
	}
	
	public String getRecord(int formId, int action, int tablePk, String params){
		return "{success:true}";
	}

	public Map<String, Object> getScd() {
		return scd;
	}

	public void setScd(Map<String, Object> scd) {
		this.scd = scd;
	}

	public Map<String, String> getRequestParams() {
		return requestParams;
	}

	public void setRequestParams(Map<String, String> requestParams) {
		this.requestParams = requestParams;
	}

	public RdbmsDao getDao() {
		return dao;
	}

	public void setDao(PostgreSQL dao) {
		this.dao = dao;
	}

	public FrameworkEngine getEngine() {
		return engine;
	}

	public void setEngine(FrameworkEngine engine) {
		this.engine = engine;
	}

	public ScriptEngine(Map<String, Object> scd, Map<String, String> requestParams, RdbmsDao dao, FrameworkEngine engine) {
		super();
		this.scd = scd;
		this.requestParams = requestParams;
		this.dao = dao;
		this.engine = engine;
	}
	
	
	
}
