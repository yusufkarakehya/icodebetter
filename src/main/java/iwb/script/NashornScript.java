package iwb.script;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

/*
import org.bson.Document;
import org.redisson.api.RedissonClient;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;*/
import com.rabbitmq.client.Channel;

import iwb.cache.FrameworkCache;
import iwb.cache.FrameworkSetting;
import iwb.cache.LocaleMsgCache;
import iwb.domain.db.Log5Console;
import iwb.domain.db.Log5QueryAction;
import iwb.domain.db.W5ExternalDb;
import iwb.domain.db.W5LookUp;
import iwb.domain.db.W5LookUpDetay;
import iwb.domain.db.W5Table;
import iwb.domain.helper.W5QueuedActionHelper;
import iwb.domain.result.W5FormResult;
import iwb.domain.result.W5GlobalFuncResult;
import iwb.engine.GlobalScriptEngine;
import iwb.exception.IWBException;
import iwb.mq.MQTTCallback;
import iwb.timer.Action2Execute;
import iwb.util.DBUtil;
import iwb.util.GenericUtil;
import iwb.util.HttpUtil;
import iwb.util.InfluxUtil;
import iwb.util.LogUtil;
import iwb.util.MQUtil;
import iwb.util.ScriptUtil;
import iwb.util.UserUtil;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class NashornScript {
	Map<String, Object> scd;
	Map<String, String> requestParams;
	private GlobalScriptEngine scriptEngine;
	

	public Object[] sqlQuery(String sql) {
		scriptEngine.getDao().checkTenant(scd);
		Log5QueryAction log = null;
		if(FrameworkSetting.log2tsdb && requestParams.containsKey("_trid_")){
			log = new Log5QueryAction(requestParams.get("_trid_"), sql);
		}
		List l = scriptEngine.getDao().executeSQLQuery2Map(sql, null);
		if(log!=null)LogUtil.logObject(log, false);
		return GenericUtil.isEmpty(l) ? null : l.toArray();
	}

	public Object[] sqlQuery(String sql, Object jsRequestParams) {
		Map m = fromScriptObject2Map((ScriptObjectMirror)jsRequestParams);
		if (GenericUtil.isEmpty(m) || !sql.contains("${"))
			return sqlQuery(sql);
		Object[] oz = DBUtil.filterExt4SQL(sql, scd, m, null);
		scriptEngine.getDao().checkTenant(scd);
		Log5QueryAction log = null;
		if(FrameworkSetting.log2tsdb && requestParams.containsKey("_trid_")){
			log = new Log5QueryAction(requestParams.get("_trid_"), sql);
		}
		List l = scriptEngine.getDao().executeSQLQuery2Map(oz[0].toString(), (List) oz[1]);
		if(log!=null)LogUtil.logObject(log, false);
		return GenericUtil.isEmpty(l) ? null : l.toArray();
	}

	public void sleep(int millis) throws InterruptedException {
		Thread.sleep(millis);
	}

/*
	public RedissonClient redisClient(int externalDbId) {
		W5ExternalDb edb = FrameworkCache.getExternalDb(scd, externalDbId);
		return edb.getRedissonClient();
	}
	

	public MongoDatabase mongoDatabase(int externalDbId) {
		W5ExternalDb edb = FrameworkCache.getExternalDb(scd, externalDbId);
		return edb.getMongoDatabase();
	}
	

	public Object[] mongoQuery(int externalDbId, String collectionName, Object... jsSearchParams) {
		W5ExternalDb edb = FrameworkCache.getExternalDb(scd, externalDbId);
		MongoCollection mc = edb.getMongoDatabase().getCollection(collectionName);
		FindIterable iterDoc =  jsSearchParams.length==0 ? mc.find() : mc.find(new Document((Map)ScriptUtil.fromScriptObject2Map(jsSearchParams[0])));
		Iterator it = iterDoc.iterator();
		if(!it.hasNext())return null;
		List l = new ArrayList();
		while(it.hasNext()) {
			l.add(it.next());
		}
		return l.toArray();
	}
	

	public void mongoInsert(int externalDbId, String collectionName, Object jsValues) {
		W5ExternalDb edb = FrameworkCache.getExternalDb(scd, externalDbId);
		MongoCollection mc = edb.getMongoDatabase().getCollection(collectionName);
		mc.insertOne(new Document((Map)ScriptUtil.fromScriptObject2Map(jsValues)));
	}
	
	public void mongoUpdate(int externalDbId, String collectionName, Object jsValues, Object... jsKeys) {
		W5ExternalDb edb = FrameworkCache.getExternalDb(scd, externalDbId);
		MongoCollection mc = edb.getMongoDatabase().getCollection(collectionName);
		Map values = (Map)ScriptUtil.fromScriptObject2Map(jsValues);
		Map keys = null;
		if(jsKeys.length==0) {
			Object _id = values.get("_id");
			if(_id==null)
				throw new IWBException("framework", "MonguUpdate.PK", 0, null, "Keys not defined, at least _id must be given", null);
			keys = new HashMap();
			keys.put("_id",_id);
			values.remove("_id");
		} else keys = (Map)ScriptUtil.fromScriptObject2Map(jsKeys);
		if(keys.containsKey("_id"))
			mc.updateOne(new Document(keys), new Document(values));
		else
			mc.updateMany(new Document(keys), new Document(values));
	}
	
	public void mongoDelete(int externalDbId, String collectionName, Object jsKeys) {
		W5ExternalDb edb = FrameworkCache.getExternalDb(scd, externalDbId);
		MongoCollection mc = edb.getMongoDatabase().getCollection(collectionName);
		mc.deleteMany(new Document((Map)ScriptUtil.fromScriptObject2Map(jsKeys)));
	}
	*/


	public Object[]  influxQuery(int externalDbId, String query) {
		W5ExternalDb edb = FrameworkCache.getExternalDb(scd, externalDbId);
		return influxQuery(edb.getDbUrl(), edb.getDefaultSchema(), query);
	}
	
	
	public Object[]  influxQuery(String host, String dbName, String query) {
		if(host.equals("1"))host=FrameworkSetting.log2tsdbUrl;
		List l = InfluxUtil.query(host, dbName, query);
		return GenericUtil.isEmpty(l) ? null : l.toArray();
	}
	
	public Map  influxWriteRaw(int externalDbId, String query) {
		W5ExternalDb edb = FrameworkCache.getExternalDb(scd, externalDbId);
		return influxWriteRaw(edb.getDbUrl(), edb.getDefaultSchema(), query);
	}
	
	public Map  influxWriteRaw(String host, String dbName, String query) {
		if(host.equals("1"))host=FrameworkSetting.log2tsdbUrl;
		String s = InfluxUtil.write(host, dbName, query);
		if(GenericUtil.isEmpty(s))return null;
		return GenericUtil.fromJSONObjectToMap(new JSONObject(s));
	}
	
	public Map  influxWrite(int externalDbId, String measName, Object tagMap, Object fieldMap) {
		W5ExternalDb edb = FrameworkCache.getExternalDb(scd, externalDbId);
		return influxWrite(edb.getDbUrl(), edb.getDefaultSchema(), measName, tagMap, fieldMap);
	}
	
	public Map  influxWrite(String host, String dbName, String measName, Object tagMap, Object fieldMap) {
		if(host.equals("1"))host=FrameworkSetting.log2tsdbUrl;
		StringBuilder ss = new StringBuilder();
		ss.append(measName);
		if(tagMap instanceof ScriptObjectMirror) {
			tagMap = ScriptUtil.fromScriptObject2Map((ScriptObjectMirror)tagMap);
		}
		if(tagMap instanceof Map) {
			Map<String, Object> xtagMap = (Map)tagMap;
			for (String key : xtagMap.keySet()) {
				Object o = xtagMap.get(key);
				if (!GenericUtil.isEmpty(o)) {
					ss.append(",").append(key).append("=").append(o);
				}
			}
		}
		ss.append(" ");
		if(fieldMap instanceof ScriptObjectMirror) {
			fieldMap = ScriptUtil.fromScriptObject2Map2((ScriptObjectMirror)fieldMap);
		}
		if(fieldMap instanceof Map) {
			Map<String, Object> xfieldMap = (Map)fieldMap;
			for (String key : xfieldMap.keySet()) {
				Object o = xfieldMap.get(key);
				if (!GenericUtil.isEmpty(o)) {
					ss.append(key).append("=");
					if(o instanceof Integer || o instanceof Long || o instanceof Short)ss.append(o).append("i");
					else if(o instanceof Double || o instanceof Float)ss.append(o);
					else ss.append("\"").append(GenericUtil.stringToJS2(o.toString())).append("\"");
					ss.append(",");
				}
			}
		}
		if(ss.charAt(ss.length()-1)==',')ss.setLength(ss.length()-1);
		
		String s = InfluxUtil.write(host, dbName, ss.toString());
		if(GenericUtil.isEmpty(s))return null;
		return GenericUtil.fromJSONObjectToMap(new JSONObject(s));
	}
	
	
	public void mqttSend(int mqId, String topic, String message) {
		try {
			MQTTCallback.send((String)scd.get("projectId"), mqId, topic, message);
		} catch (Exception e) {
			if(FrameworkSetting.debug)e.printStackTrace();
			throw new IWBException("framework", "mqqtSend", mqId, null, e.getMessage(),
					e);
		}
	}
	
	
	public String mqBasicPublish(String host, String queueName, String msg) {
		Channel ch = MQUtil.getChannel4Queue(host, queueName);
		if (ch == null)
			return "Connection Error";
		try {
			ch.basicPublish("", queueName, null, msg.toString().getBytes("UTF-8"));
			return null;
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public int mqQueueMsgCount(String host, String queueName) {
		return MQUtil.getQueueMsgCount(host, queueName);
	}

	public void mqClose(String host, String queueName) {
		MQUtil.close(host, queueName);
	}

	public String getCurrentDate() {
		return scriptEngine.getDao().getCurrentDate((Integer) scd.get("customizationId"));
	}

	public String getLocMsg(String key) {
		return LocaleMsgCache.get2(scd, key);
	}
	
	public String getLookUpDetayText(int lookUpId, String value) {
		W5LookUp lookUp = FrameworkCache.getLookUp(scd, lookUpId);
		if(lookUp==null)
			throw new IWBException("rhino", "LookUp", lookUpId, null, "Wrong LookUpId",null);
		W5LookUpDetay d = lookUp.get_detayMap().get(value);
		if(d==null)return value+": ???";
		return LocaleMsgCache.get2(scd, d.getDsc());
	}

	public String md5hash(String s) {
		return GenericUtil.getMd5Hash(s);
	}

	public Object sqlFunc(String s) {
		return scriptEngine.getDao().getSqlFunc(s);
	}

	public int compareDates(String date1, String date2) {
		// if(date1==null && date2==null)return 0;
		// if(date1==null)return -1;if(date2==null)return 1;
		Date d1 = GenericUtil.uDate(date1), d2 = GenericUtil.uDate(date2);
		if (d1 == null || d2 == null)
			throw new IWBException("rhino", "Invalid Date Format", 0, null, "compareDates(" + date1 + "," + date2 + ")",
					null);
		return d1.equals(d2) ? 0 : (d1.after(d2) ? 1 : -1);
	}

	private Map<String, String> fromScriptObject2Map(ScriptObjectMirror jsRequestParams) {
		Map<String, String> rp = ScriptUtil.fromScriptObject2Map(jsRequestParams);
		if (requestParams.containsKey(".w") && !rp.containsKey(".w"))
			rp.put(".w", requestParams.get(".w"));
		if(requestParams.containsKey("_trid_") && !rp.containsKey("_trid_") )
			rp.put("_trid_", requestParams.get("_trid_"));
		return rp;
	}

	private Map<String, Object> fromScriptObject2Map2(ScriptObjectMirror jsRequestParams) {
		Map<String, Object> rp = new HashMap<String, Object>();
		if (jsRequestParams != null && !jsRequestParams.isArray()) {
			for (String key:jsRequestParams.keySet()) 
					try {
						Object o = jsRequestParams.get(key);
						if (o != null) {
							String res = o.toString();
							if (res.length() > 0)
								switch (res.charAt(0)) {
								case '{':
								case '[':
									if(o instanceof ScriptObjectMirror && ((ScriptObjectMirror)o).isArray()) {
										rp.put(key, ScriptUtil.fromScriptObject2List((ScriptObjectMirror)o));
									} else
										rp.put(key, o);
									break;
								default:
									if (res.endsWith(".0") && GenericUtil.uInt(res.substring(0, res.length() - 2)) > 0)
										res = res.substring(0, res.length() - 2);
									rp.put(key, res);
								}
						}
					} catch (Exception eq) {
					}
		}
		if (requestParams.containsKey(".w") && !rp.containsKey(".w"))
			rp.put(".w", requestParams.get(".w"));
		if(requestParams.containsKey("_trid_") && !rp.containsKey("_trid_") )
			rp.put("_trid_", requestParams.get("_trid_"));

		return rp;
	}

	public Object[] query(int queryId, ScriptObjectMirror jsRequestParams) {
		scriptEngine.getDao().checkTenant(scd);
		List l = scriptEngine.getDao().runQuery2Map(scd, queryId, fromScriptObject2Map(jsRequestParams));//
		return GenericUtil.isEmpty(l) ? null : l.toArray();
	}

	public void console(Object oMsg) {
		console(oMsg, null, null);
	}

	public int globalNextval(String seq) {
		return GenericUtil.getGlobalNextval(seq, scd != null ? (String) scd.get("projectId") : null,
				scd != null ? (Integer) scd.get("userId") : 0, scd != null ? (Integer) scd.get("customizationId") : 0);
	}

	public void console(Object oMsg, String title) {
		if (!FrameworkSetting.debug)
			return;
		console(oMsg, title, null);
	}

	public void console(Object oMsg, String title, String level) {
		if (!FrameworkSetting.debug)
			return;
		String s = "(null)";
		if (oMsg != null) {
			if (oMsg instanceof String)
				s = (String) oMsg;
			else {
//				oMsg = RhinoUtil.rhinoValue(oMsg);
				if (oMsg != null) {
					if (oMsg instanceof String || oMsg instanceof Integer || oMsg instanceof Long
							|| oMsg instanceof Float || oMsg instanceof Double || oMsg instanceof BigDecimal) {
						s = oMsg.toString();
					} else if (oMsg instanceof Date || oMsg instanceof Timestamp) {
						s = oMsg instanceof Timestamp ? GenericUtil.uFormatDateTime((Timestamp) oMsg)
								: GenericUtil.uFormatDate((Date) oMsg);
					} else if (oMsg instanceof Object[] || oMsg instanceof List) {
						List l;
						if (oMsg instanceof Object[]) {
							Object[] oz = (Object[]) oMsg;
							l = new ArrayList();
							for (int qi = 0; qi < oz.length; qi++) {
								l.add(oz[qi]);
							}
						} else
							l = (List) oMsg;
						s = GenericUtil.fromListToJsonString2Recursive(l);
					} else if (oMsg instanceof Map) {
						s = GenericUtil.fromMapToJsonString2Recursive((Map) oMsg);
					} else {
						s = "Undefined Object Type: " + oMsg.toString();
					}
				}
			}
		}
		if (FrameworkSetting.debug && !GenericUtil.isEmpty(s))System.out.println(GenericUtil.uStrMax(s, 100));
		if (scd != null && scd.containsKey("customizationId") && scd.containsKey("userId")
				&& scd.containsKey("sessionId") && requestParams != null && requestParams.containsKey(".w"))
			try {
				Map m = new HashMap();
				m.put("success", true);
				m.put("console", s);
				if (!GenericUtil.isEmpty(title))
					m.put("title", title);
				if (!GenericUtil.isEmpty(level)
						&& GenericUtil.hasPartInside2("log,info,success,warn,warning,error", level))
					m.put("level", level);
				UserUtil.broadCast((String) scd.get("projectId"), (Integer) scd.get("userId"),
						(String) scd.get("sessionId"), (String) requestParams.get(".w"), m);
			} catch (Exception e) {
			}
		if(FrameworkSetting.log2tsdb)LogUtil.logObject(new Log5Console(scd, s, level, (String)requestParams.get("_trid_")), true);
	}

	public Object execFunc(int dbFuncId, ScriptObjectMirror jsRequestParams) {
		return execFunc(dbFuncId, jsRequestParams, true, null);
	}

	public int getAppSettingInt(String key) {
		return FrameworkCache.getAppSettingIntValue(scd, key);
	}

	public int getAppSettingInt(int customizationId, String key) {
		return FrameworkCache.getAppSettingIntValue(customizationId, key);
	}

	public String getAppSettingString(String key) {
		return FrameworkCache.getAppSettingStringValue(scd, key);
	}

	public Object execFunc(int dbFuncId, ScriptObjectMirror jsRequestParams, boolean throwOnError, String throwMessage) {
		W5GlobalFuncResult result = scriptEngine.executeGlobalFunc(scd, dbFuncId, fromScriptObject2Map(jsRequestParams), (short) 5);
		if (throwOnError && !result.getErrorMap().isEmpty()) {
			throw new IWBException("rhino", "GlobalFunc", dbFuncId, null,
					throwMessage != null ? LocaleMsgCache.get2(scd, throwMessage)
							: "Validation Error: " + GenericUtil.fromMapToJsonString2(result.getErrorMap()),
					null);
		}
		return result;
	}

	public int sqlExecute(String sql) {
		if (scd != null && scd.get("customizationId") != null && (Integer) scd.get("customizationId") > 1) {
			String sql2 = sql.toLowerCase(FrameworkSetting.appLocale);
			if (DBUtil.checkTenantSQLSecurity(sql2)) {
				throw new IWBException("security", "SQL", 0, null,
						"Forbidden Command2. Please contact iCodeBetter team ;)", null);
			}
		}

		scriptEngine.getDao().checkTenant(scd);
		Log5QueryAction log = null;
		if(FrameworkSetting.log2tsdb && requestParams.containsKey("_trid_")){
			log = new Log5QueryAction(requestParams.get("_trid_"), sql);
		}
		
		int result = scriptEngine.getDao().executeUpdateSQLQuery(sql, null);
		if(log!=null)LogUtil.logObject(log, false);
		return result;
	}

	public int sqlExecute(String sql, ScriptObjectMirror jsRequestParams) {
		if (scd != null && scd.get("customizationId") != null && (Integer) scd.get("customizationId") > 1) {
			String sql2 = sql.toLowerCase(FrameworkSetting.appLocale);
			if (DBUtil.checkTenantSQLSecurity(sql2)) {
				throw new IWBException("security", "SQL", 0, null,
						"Forbidden Command2. Please contact iCodeBetter team ;)", null);
			}
		}

		Map<String, String> reqMap = fromScriptObject2Map(jsRequestParams);
		Object[] oz = DBUtil.filterExt4SQL(sql, scd, reqMap, null);
		Log5QueryAction log = null;
		if(FrameworkSetting.log2tsdb && requestParams.containsKey("_trid_")){
			log = new Log5QueryAction(requestParams.get("_trid_"), oz[0].toString());
		}
		scriptEngine.getDao().checkTenant(scd);
		int result = scriptEngine.getDao().executeUpdateSQLQuery(oz[0].toString(), oz.length > 1 ? (List) oz[1] : null);
		if(log!=null)LogUtil.logObject(log, false);
		return result;
	}

	public W5FormResult postForm(int formId, int action, ScriptObjectMirror jsRequestParams) {
		return postForm(formId, action, jsRequestParams, "", true, null);
	}

	public W5FormResult postForm(int formId, int action, ScriptObjectMirror jsRequestParams, String prefix) {
		return postForm(formId, action, jsRequestParams, prefix, true, null);
	}

	public W5FormResult postForm(int formId, int action, ScriptObjectMirror jsRequestParams, String prefix,
			boolean throwOnError) {
		return postForm(formId, action, jsRequestParams, prefix, throwOnError, null);
	}

	public W5FormResult postForm(int formId, int action, ScriptObjectMirror jsRequestParams, String prefix,
			boolean throwOnError, String throwMessage) {

		W5FormResult result = scriptEngine.getCrudEngine().postForm4Table(scd, formId, action, fromScriptObject2Map(jsRequestParams), prefix);
		if (throwOnError && !result.getErrorMap().isEmpty()) {
			throw new IWBException("rhino", "FormId", formId, null,
					throwMessage != null ? LocaleMsgCache.get2(scd, throwMessage)
							: "Validation Error: " + GenericUtil.fromMapToJsonString2(result.getErrorMap()),
					null);
		}
		if (result.getQueueActionList() != null)
			for (W5QueuedActionHelper o : result.getQueueActionList()) {
				Action2Execute eqf = new Action2Execute(o, scd);
//				taskExecutor.execute(eqf);
		}
		return result;
	}

	public Map getTableJSON(String tableDsc, String tablePk) {
		List<Integer> l = (List<Integer>) scriptEngine.getDao().find(
				"select t.tableId from W5Table t where t.dsc=? AND t.customizationId in (0,?) order by t.customizationId desc",
				tableDsc, scd.get("customizationId"));
		if (l.isEmpty())
			throw new IWBException("rhino", "getTableJSON", 0, tableDsc, "table_not_found", null);

		return getTableJSON(l.get(0), tablePk, 0);
	}

	public Map getTableJSON(int tableId, String tablePk, int forAction) {
		return getTableJSON(tableId, tablePk, forAction, false, null);
	}

	public Map getTableJSON(int tableId, String tablePk, int forAction, boolean throwOnError, String throwMessage) {

		W5Table t = FrameworkCache.getTable(scd, tableId);
		if (GenericUtil.isEmpty(tablePk) || t == null) {
			if (throwOnError)
				throw new IWBException("rhino", "getTableJSON", tableId, null,
						throwMessage != null ? LocaleMsgCache.get2(scd, throwMessage) : "table_or_key_not_valid", null);
			return null;
		}
		if (forAction != -1) { // -1:kontrol yok, 0: view, 1: edit, 3:delete
			if (t.getAccessViewTip() == 0 && (!FrameworkCache.roleAccessControl(scd, 0)
					|| !FrameworkCache.roleAccessControl(scd, forAction))) {
				throw new IWBException("security", "Module", 0, null,
						LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_modul_kontrol"), null);
			}
			if (t.getAccessViewUserFields() == null && !GenericUtil.accessControl(scd, t.getAccessViewTip(),
					t.getAccessViewRoles(), t.getAccessViewUsers())) {
				throw new IWBException("security", "Table", tableId, null,
						LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_tablo_kontrol_goruntuleme"),
						null);
			}
			if (forAction == 1 && t.getAccessUpdateUserFields() == null && !GenericUtil.accessControl(scd,
					t.getAccessUpdateTip(), t.getAccessUpdateRoles(), t.getAccessUpdateUsers())) {
				throw new IWBException("security", "Table", tableId, null,
						LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_tablo_kontrol_guncelleme"),
						null);
			}
			if (forAction == 3 && t.getAccessDeleteUserFields() == null && !GenericUtil.accessControl(scd,
					t.getAccessDeleteTip(), t.getAccessDeleteRoles(), t.getAccessDeleteUsers())) {
				throw new IWBException("security", "Table", tableId, null,
						LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_tablo_kontrol_silme"), null);
			}
		}

		StringBuilder s = new StringBuilder();
		s.append("select x.* from ").append(t.getDsc()).append(" x where x.")
				.append(t.get_tableParamList().get(0).getExpressionDsc()).append("=?");
		if (t.get_tableParamList().size() > 1) {
			s.append(DBUtil.includeTenantProjectPostSQL(scd, t, "x"));
		}
		List p = new ArrayList();
		p.add(t.get_tableParamList().get(0).getParamTip() == 1 ? tablePk : GenericUtil.uInt(tablePk));
		scriptEngine.getDao().checkTenant(scd);
		List l = scriptEngine.getDao().executeSQLQuery2Map(s.toString(), p);
		if (GenericUtil.isEmpty(l)) {
			if (throwOnError)
				throw new IWBException("rhino", "getTableJSON", tableId, null,
						throwMessage != null ? LocaleMsgCache.get2(scd, throwMessage) : "record_not_found", null);
			return null;
		}
		Map mo = (Map) l.get(0);

		return mo;
	}

	public Map REST(String serviceName, ScriptObjectMirror jsRequestParams) {
		return REST(serviceName, jsRequestParams, true);
	}

	public Map REST(String serviceName, ScriptObjectMirror jsRequestParams, boolean throwFlag) {
		Map result = new HashMap();
		result.put("success", true);
		try {
			Map newReqMap = fromScriptObject2Map2(jsRequestParams);
			Map m = scriptEngine.getRestEngine().REST(scd, serviceName, newReqMap);
			if (m != null) {
				if (m.containsKey("errorMsg")) {
					if (throwFlag)
						throw new IWBException("ws", "Error:REST", 0, serviceName, m.get("errorMsg").toString(), null);
					else
						result.put("success", false);
				}
				if (m.containsKey("faultcode") && m.containsKey("faultstring")) {
					if (throwFlag)
						throw new IWBException("ws", m.get("faultcode").toString(), 0, serviceName,
								m.get("faultstring").toString(), null);
					else {
						result.put("success", false);
						result.put("errorMsg", m.get("faultstring"));
					}
				}
				result.putAll(m);
			}
		} catch (Exception e) {
			throw new IWBException("ws", "REST", 0, null, "Error: " + serviceName, e);
		}
		return result;
	}
	
	public String download(String url) {
		return download(url, null);
		
	}

	public String download(String url, ScriptObjectMirror jsRequestParams) {
		String params="";
		if(jsRequestParams!=null) {
			Map newReqMap = fromScriptObject2Map2(jsRequestParams);
			if(!GenericUtil.isEmpty(newReqMap))for(Object k:newReqMap.keySet()) {
				if(params.length()>0)params+="&";
				params+=k+"="+newReqMap.get(k);
			}
			
		}
		String result = HttpUtil.send(url, params,
				"GET", null);
		return result;
	}

	public String formatDate(Object dt) {
		if (dt == null)
			return "";
		return "--";
	}

	public Map sendFormSmsMail(int formSmsMailId, ScriptObjectMirror jsRequestParams) {
		Map newReqMap = fromScriptObject2Map2(jsRequestParams);
		return scriptEngine.getNotyEngine().sendFormSmsMail(scd, formSmsMailId, newReqMap);
	}


	public NashornScript(Map<String, Object> scd, Map<String, String> requestParams, GlobalScriptEngine scriptEngine) {
		super();
		this.scd = scd;
		this.requestParams = requestParams;
		this.scriptEngine = scriptEngine;
	}
}
