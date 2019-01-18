package iwb.engine;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.springframework.core.task.TaskExecutor;

import com.rabbitmq.client.Channel;

import iwb.cache.FrameworkCache;
import iwb.cache.FrameworkSetting;
import iwb.cache.LocaleMsgCache;
import iwb.dao.rdbms_impl.PostgreSQL;
import iwb.domain.db.W5Table;
import iwb.domain.helper.W5QueuedActionHelper;
import iwb.domain.result.W5FormResult;
import iwb.domain.result.W5GlobalFuncResult;
import iwb.exception.IWBException;
import iwb.service.FrameworkService;
import iwb.timer.Action2Execute;
import iwb.util.DBUtil;
import iwb.util.GenericUtil;
import iwb.util.MQUtil;
import iwb.util.RedisUtil;
import iwb.util.RhinoUtil;
import iwb.util.UserUtil;

public class RhinoEngine {
	Map<String, Object> scd;
	Map<String, String> requestParams;
	private PostgreSQL dao;
	private FrameworkService engine;
	public static TaskExecutor taskExecutor = null;

	public Object[] sqlQuery(String sql) {
		List l = dao.executeSQLQuery2Map(sql, null);
		return GenericUtil.isEmpty(l) ? null : l.toArray();
	}

	public Object[] sqlQuery(String sql, NativeObject jsRequestParams) {
		Map m = fromNativeObject2Map(jsRequestParams);
		if (GenericUtil.isEmpty(m) || !sql.contains("${"))
			return sqlQuery(sql);
		Object[] oz = DBUtil.filterExt4SQL(sql, scd, m, null);
		List l = dao.executeSQLQuery2Map(oz[0].toString(), (List) oz[1]);
		return GenericUtil.isEmpty(l) ? null : l.toArray();
	}

	/*
	 * public Object tsqlQuery(String sql, String dbName){ return
	 * engine.executeInfluxQuery(scd, sql, dbName); } public void
	 * tsqlInsert(String measurement, NativeObject jsTags, NativeObject
	 * jsFields){ tsqlInsert(measurement, jsTags, jsFields, null); } public void
	 * tsqlInsert(String measurement, NativeObject jsTags, NativeObject
	 * jsFields, String date){ engine.insertInfluxRecord(scd, measurement,
	 * fromNativeObject2Map2(jsTags, true), fromNativeObject2Map2(jsFields,
	 * false), date); } public Object tsqlExecute(String sql, String dbName){
	 * return null;//TODO }
	 */

	public void sleep(int millis) throws InterruptedException {
		Thread.sleep(millis);
	}

	public NativeObject redisGetJSON(String host, String k) throws JSONException {

		String v = RedisUtil.get(host, k);
		if (v != null) {
			JSONObject o = new JSONObject(v);
			return RhinoUtil.fromJSONObjectToNativeObject(o);
		}
		return null;
	}

	public String redisPut(String host, String k, Object v) {
		if (v == null)
			return RedisUtil.put(host, k, null);

		if (v instanceof NativeArray)
			return RedisUtil.put(host, k, RhinoUtil.fromNativeArrayToJsonString2Recursive((NativeArray) v));

		if (v instanceof NativeObject)
			return RedisUtil.put(host, k, RhinoUtil.fromNativeObjectToJsonString2Recursive((NativeObject) v));

		return RedisUtil.put(host, k, v.toString());
	}

	public String redisGet(String host, String k) {
		return RedisUtil.get(host, k);
	}

	public long redisLlen(String host, String k) {
		return RedisUtil.llen(host, k);
	}

	public void redisClose(String host) {
		RedisUtil.close(host);
	}

	public String redisInfo(String host, String section) {
		return RedisUtil.info(host, section);
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
		return dao.getCurrentDate((Integer) scd.get("customizationId"));
	}

	public String getLocMsg(String key) {
		return LocaleMsgCache.get2(scd, key);
	}

	public String md5hash(String s) {
		return dao.getMd5Hash(s);
	}

	public Object sqlFunc(String s) {
		return dao.getSqlFunc(s);
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

	private Map<String, String> fromNativeObject2Map(NativeObject jsRequestParams) {
		Map<String, String> rp = new HashMap<String, String>();
		if (jsRequestParams != null) {
			Object[] ids = jsRequestParams.getAllIds();
			if (ids != null)
				for (int qi = 0; qi < ids.length; qi++) {
					Object o = RhinoUtil.rhinoValue(jsRequestParams.get(ids[qi].toString(), null));
					if (o != null) {
						String res = o.toString();
						if (res.endsWith(".0") && GenericUtil.uInt(res.substring(0, res.length() - 2)) > 0)
							res = res.substring(0, res.length() - 2);
						rp.put(ids[qi].toString(), res);
					}
				}
		}
		if (requestParams.containsKey(".w") && !rp.containsKey(".w"))
			rp.put(".w", requestParams.get(".w"));
		return rp;
	}

	private Map<String, Object> fromNativeObject2Map2(NativeObject jsRequestParams) {
		Map<String, Object> rp = new HashMap<String, Object>();
		if (jsRequestParams != null) {
			Object[] ids = jsRequestParams.getAllIds();
			if (ids != null)
				for (int qi = 0; qi < ids.length; qi++)
					try {
						Object o = RhinoUtil.rhinoValue2(jsRequestParams.get(ids[qi].toString(), null));
						if (o != null) {
							String res = o.toString();
							if (res.length() > 0)
								switch (res.charAt(0)) {
								case '{':
								case '[':
									rp.put(ids[qi].toString(), o);
									break;
								default:
									if (res.endsWith(".0") && GenericUtil.uInt(res.substring(0, res.length() - 2)) > 0)
										res = res.substring(0, res.length() - 2);
									rp.put(ids[qi].toString(), res);
								}
						}
					} catch (Exception eq) {
					}
		}
		if (requestParams.containsKey(".w") && !rp.containsKey(".w"))
			rp.put(".w", requestParams.get(".w"));
		return rp;
	}

	public Object[] runQuery(int queryId, NativeObject jsRequestParams) {
		List l = dao.runQuery2Map(scd, queryId, fromNativeObject2Map(jsRequestParams));
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
				oMsg = RhinoUtil.rhinoValue(oMsg);
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
								l.add(RhinoUtil.rhinoValue(oz[qi]));
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
		if (FrameworkSetting.debug)
			System.out.println(s);
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
	}

	public Object execFunc(int dbFuncId, NativeObject jsRequestParams) {
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

	public Object execFunc(int dbFuncId, NativeObject jsRequestParams, boolean throwOnError, String throwMessage) {
		W5GlobalFuncResult result = engine.executeFunc(scd, dbFuncId, fromNativeObject2Map(jsRequestParams), (short) 5);
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

		return dao.executeUpdateSQLQuery(sql, null);
	}

	public int sqlExecute(String sql, NativeObject jsRequestParams) {
		if (scd != null && scd.get("customizationId") != null && (Integer) scd.get("customizationId") > 1) {
			String sql2 = sql.toLowerCase(FrameworkSetting.appLocale);
			if (DBUtil.checkTenantSQLSecurity(sql2)) {
				throw new IWBException("security", "SQL", 0, null,
						"Forbidden Command2. Please contact iCodeBetter team ;)", null);
			}
		}

		Map<String, String> reqMap = fromNativeObject2Map(jsRequestParams);
		Object[] oz = DBUtil.filterExt4SQL(sql, scd, reqMap, null);

		return dao.executeUpdateSQLQuery((String) oz[0], oz.length > 1 ? (List) oz[1] : null);
	}

	public W5FormResult postForm(int formId, int action, NativeObject jsRequestParams) {
		return postForm(formId, action, jsRequestParams, "", true, null);
	}

	public W5FormResult postForm(int formId, int action, NativeObject jsRequestParams, String prefix) {
		return postForm(formId, action, jsRequestParams, prefix, true, null);
	}

	public W5FormResult postForm(int formId, int action, NativeObject jsRequestParams, String prefix,
			boolean throwOnError) {
		return postForm(formId, action, jsRequestParams, prefix, throwOnError, null);
	}

	public W5FormResult postForm(int formId, int action, NativeObject jsRequestParams, String prefix,
			boolean throwOnError, String throwMessage) {

		W5FormResult result = engine.postForm4Table(scd, formId, action, fromNativeObject2Map(jsRequestParams), prefix);
		if (throwOnError && !result.getErrorMap().isEmpty()) {
			throw new IWBException("rhino", "FormId", formId, null,
					throwMessage != null ? LocaleMsgCache.get2(scd, throwMessage)
							: "Validation Error: " + GenericUtil.fromMapToJsonString2(result.getErrorMap()),
					null);
		}
		if (result.getQueueActionList() != null)
			for (W5QueuedActionHelper o : result.getQueueActionList()) {
				Action2Execute eqf = new Action2Execute(o, scd);
				taskExecutor.execute(eqf);
			}
		return result;
	}

	public Map getTableJSON(String tableDsc, String tablePk) {
		List<Integer> l = (List<Integer>) dao.find(
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
		List l = dao.executeSQLQuery2Map(s.toString(), p);
		if (GenericUtil.isEmpty(l)) {
			if (throwOnError)
				throw new IWBException("rhino", "getTableJSON", tableId, null,
						throwMessage != null ? LocaleMsgCache.get2(scd, throwMessage) : "record_not_found", null);
			return null;
		}
		Map mo = (Map) l.get(0);

		return mo;
	}

	public Map REST(String serviceName, NativeObject jsRequestParams) {
		return REST(serviceName, jsRequestParams, true);
	}

	public Map REST(String serviceName, NativeObject jsRequestParams, boolean throwFlag) {
		Map result = new HashMap();
		result.put("success", true);
		try {
			Map m = engine.REST(scd, serviceName, fromNativeObject2Map2(jsRequestParams));
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

	public String postRecord(int formId, int action, String params) {
		return "{success:true}";
	}

	public String getRecord(int formId, int action, int tablePk, String params) {
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

	public String formatDate(Object dt) {
		if (dt == null)
			return "";
		return "--";
	}

	public void setRequestParams(Map<String, String> requestParams) {
		this.requestParams = requestParams;
	}

	public PostgreSQL getDao() {
		return dao;
	}

	public void setDao(PostgreSQL dao) {
		this.dao = dao;
	}

	public FrameworkService getEngine() {
		return engine;
	}

	public void setEngine(FrameworkService engine) {
		this.engine = engine;
	}

	public RhinoEngine(Map<String, Object> scd, Map<String, String> requestParams, PostgreSQL dao,
			FrameworkService engine) {
		super();
		this.scd = scd;
		this.requestParams = requestParams;
		this.dao = dao;
		this.engine = engine;
	}
}
