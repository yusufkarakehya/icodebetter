package iwb.engine;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import iwb.cache.FrameworkCache;
import iwb.cache.FrameworkSetting;
import iwb.cache.LocaleMsgCache;
import iwb.custom.trigger.GlobalFuncTrigger;
import iwb.dao.rdbms_impl.MetadataLoaderDAO;
import iwb.dao.rdbms_impl.PostgreSQL;
import iwb.domain.db.Log5GlobalFuncAction;
import iwb.domain.db.W5GlobalFuncParam;
import iwb.domain.db.W5LookUp;
import iwb.domain.db.W5LookUpDetay;
import iwb.domain.db.W5Query;
import iwb.domain.db.W5QueryField;
import iwb.domain.db.W5QueryParam;
import iwb.domain.db.W5Table;
import iwb.domain.db.W5TableEvent;
import iwb.domain.db.W5WsMethod;
import iwb.domain.db.W5WsMethodParam;
import iwb.domain.result.W5FormResult;
import iwb.domain.result.W5GlobalFuncResult;
import iwb.domain.result.W5QueryResult;
import iwb.exception.IWBException;
import iwb.script.RhinoScript;
import iwb.service.FrameworkService;
import iwb.util.GenericUtil;
import iwb.util.RhinoContextFactory;
import iwb.util.RhinoUtil;

@Component
public class ScriptEngine {
	@Lazy
	@Autowired
	private PostgreSQL dao;

	@Lazy
	@Autowired
	private MetadataLoaderDAO metaDataDao;

	
	@Lazy
	@Autowired
	private ConversionEngine conversionEngine;

	@Lazy
	@Autowired
	private QueryEngine queryEngine;

	@Lazy
	@Autowired
	private WorkflowEngine workflowEngine;
	
	
	@Lazy
	@Autowired 
	private AccessControlEngine acEngine;

	@Lazy
	@Autowired
	private CRUDEngine crudEngine;
	
	@Lazy
	@Autowired
	private RESTEngine restEngine;

	
	public PostgreSQL getDao() {
		return dao;
	}

	public ConversionEngine getConversionEngine() {
		return conversionEngine;
	}

	public QueryEngine getQueryEngine() {
		return queryEngine;
	}

	public CRUDEngine getCrudEngine() {
		return crudEngine;
	}

	public RESTEngine getRestEngine() {
		return restEngine;
	}

	public W5GlobalFuncResult executeFunc(Map<String, Object> scd, int globalFuncId, Map<String, String> parameterMap, short accessSourceType) {

		W5GlobalFuncResult r = metaDataDao.getGlobalFuncResult(scd, globalFuncId);
		if (!GenericUtil.isEmpty(r.getGlobalFunc().getAccessSourceTypes())
				&& !GenericUtil.hasPartInside2(r.getGlobalFunc().getAccessSourceTypes(), accessSourceType))
			throw new IWBException("security", "GlobalFunc", globalFuncId, null, "Access Source Type Control", null);
		/*
		 * if(execRestrictTip!=4 && checkAccessRecordControlViolation(scd, 4,
		 * 20, ""+dbFuncId)) throw new PromisException("security",
		 * "DbProc Execute2", dbFuncId, null, "Access Execute Control", null);
		 */
		dao.checkTenant(scd);
		r.setErrorMap(new HashMap());
		r.setRequestParams(parameterMap);
		GlobalFuncTrigger.beforeExec(r);

		if (r.getGlobalFunc().getLkpCodeType() == 1) { //rhino code
			Log5GlobalFuncAction action = new Log5GlobalFuncAction(r);
			String error = null;

			ContextFactory factory = RhinoContextFactory.getGlobal();
			Context cx = factory.enterContext();

			// Context cx = Context.enter();
			String script = null;
			try {
				cx.setOptimizationLevel(-1);
				if (FrameworkSetting.rhinoInstructionCount > 0)
					cx.setInstructionObserverThreshold(FrameworkSetting.rhinoInstructionCount);
				// Initialize the standard objects (Object, Function, etc.)
				// This must be done before scripts can be executed. Returns
				// a scope object that we use in later calls.
				Scriptable scope = cx.initStandardObjects();

				script = r.getGlobalFunc().getRhinoScriptCode();
				if (script.charAt(0) == '!')
					script = script.substring(1);
				// Collect the arguments into a single string.
				StringBuilder sc = new StringBuilder();

				boolean hasOutParam = false;
				if (r.getGlobalFunc().get_dbFuncParamList().size() > 0) {
					sc.append("var ");
					for (W5GlobalFuncParam p1 : r.getGlobalFunc().get_dbFuncParamList())
						if (p1.getOutFlag() == 0) {
							if (sc.length() > 4)
								sc.append(", ");
							sc.append(p1.getDsc()).append("=");
							Object o = GenericUtil.prepareParam(p1, r.getScd(), r.getRequestParams(), (short) -1, null,
									(short) 0, p1.getSourceTip() == 1 ? p1.getDsc(): null, null,
									r.getErrorMap());
							if (o == null)
								sc.append("null");
							else if ((o instanceof Integer) || (o instanceof Double) || (o instanceof BigDecimal)
									|| (o instanceof Boolean))
								sc.append(o);
							else if ((o instanceof Date))
								sc.append("'").append(GenericUtil.uFormatDate((Date) o)).append("'");
							else
								sc.append("'").append(o).append("'");
						} else
							hasOutParam = true;
					if (sc.length() > 4)
						sc.append(";\n");
					else
						sc.setLength(0);
				}
				Object requestJson = r.getRequestParams().get("_json");
				if (requestJson != null && requestJson instanceof JSONObject) {
					sc.append("var json=").append(((JSONObject) requestJson).toString()).append(";\n");
					r.getRequestParams().remove("_json");
				}
				if (script.contains("$.") || script.contains("$.")) {
					RhinoScript se = new RhinoScript(r.getScd(), r.getRequestParams(), this);
					Object wrappedOut = Context.javaToJS(se, scope);
					ScriptableObject.putProperty(scope, "$", wrappedOut);
				}
				sc.append("\nvar _scd=").append(GenericUtil.fromMapToJsonString2(r.getScd())).append(";\nvar _request=")
						.append(GenericUtil.fromMapToJsonString2(r.getRequestParams())).append(";\n").append(script);

				script = sc.toString();

				// Now evaluate the string we've colected.
				cx.evaluateString(scope, script, null, 1, null);
				/*
				 * if(scope.has("errorMsg", scope)){ Object em =
				 * RhinoUtil.rhinoValue(scope.get("errorMsg", scope));
				 * if(em!=null)throw new PromisException("rhino","GlobalFuncId",
				 * r.getGlobalFuncId(), script,
				 * LocaleMsgCache.get2(0,(String)r.getScd().get("locale"),em.
				 * toString()), null); }
				 */
				if (hasOutParam) {
					// JSONObject jo=new JSONObject();
					Map<String, String> res = new HashMap<String, String>();
					for (W5GlobalFuncParam p1 : r.getGlobalFunc().get_dbFuncParamList())
						if (p1.getOutFlag() != 0 && scope.has(p1.getDsc(), scope)) {
							Object em = RhinoUtil.rhinoValue(scope.get(p1.getDsc(), scope));
							if (em != null)
								res.put(p1.getDsc(), em.toString());
						}
					r.setResultMap(res);
				}
				if (scope.has("outMsgs", scope)) { // TODO
					Object em = scope.get("outMsgs", scope);
				}
				r.setSuccess(true);
			} catch (Exception e) {
				error = e.getMessage();
				throw new IWBException("rhino", "GlobalFunc", r.getGlobalFuncId(), script,
						"[20," + r.getGlobalFuncId() + "] " + r.getGlobalFunc().getDsc(), e);
			} finally {
				// Exit from the context.
				cx.exit();
				dao.logGlobalFuncAction(action, r, error);
			}
		}
			else dao.executeDbFunc(r, "");

		if (r.getErrorMap().isEmpty()) { // sorun yok
			// post sms
			if (!GenericUtil.isEmpty(r.getResultMap()))
				parameterMap.putAll(r.getResultMap()); // veli TODO
																	// acaba
																	// hata
																	// olabilir
																	// mi? baska
																	// bir map'e
																	// mi atsak
			// sadece burasi icin?
		}
		GlobalFuncTrigger.afterExec(r);

		switch (globalFuncId) {
		case -478: // reload locale msg cache
			for (Object[] m : (List<Object[]>) dao.executeSQLQuery(
					"select locale, locale_msg_key, dsc from iwb.w5_locale_msg where locale_msg_key=? AND customization_id=?",
					parameterMap.get("plocale_msg_key"), scd.get("customizationId"))) {
				LocaleMsgCache.set2((Integer) scd.get("customizationId"), (String) m[0], (String) m[1], (String) m[2]);
			}
		}


		return r;
	}

	public W5GlobalFuncResult postEditGridGlobalFunc(Map<String, Object> scd, int dbFuncId, int dirtyCount,
			Map<String, String> requestParams, String prefix) {

		W5GlobalFuncResult dbFuncResult = metaDataDao.getGlobalFuncResult(scd, dbFuncId);
		if (!GenericUtil.isEmpty(dbFuncResult.getGlobalFunc().getAccessSourceTypes())
				&& !GenericUtil.hasPartInside2(dbFuncResult.getGlobalFunc().getAccessSourceTypes(), 1))
			throw new IWBException("security", "DbProc", dbFuncId, null, "Access Restrict Type Control", null);
		if (acEngine.checkAccessRecordControlViolation(scd, 4, 20, "" + dbFuncId))
			throw new IWBException("security", "DbProc Execute3", dbFuncId, null, "Access Execute Control", null);

		dbFuncResult.setErrorMap(new HashMap());
		dbFuncResult.setRequestParams(requestParams);
		for (int id = 1; id <= dirtyCount; id++) {

			GlobalFuncTrigger.beforeExec(dbFuncResult);
			dao.executeDbFunc(dbFuncResult, prefix + id);
			GlobalFuncTrigger.afterExec(dbFuncResult);

			if (!dbFuncResult.getErrorMap().isEmpty() || !dbFuncResult.isSuccess()) {
				throw new IWBException("validation", "GlobalFunc", -dbFuncId, null, "Detail Grid Validation", null);
			}
		}

		return dbFuncResult;
	}
	
	public void executeTableEvent(W5TableEvent ta, W5FormResult formResult, String action, Map<String, Object> scd,
			Map<String, String> requestParams, W5Table t, String ptablePk){
		
		ContextFactory factory = RhinoContextFactory.getGlobal();
		Context cx = factory.enterContext();

		// Context cx = Context.enter();
		StringBuilder sc = new StringBuilder();
		try {
			cx.setOptimizationLevel(-1);
			if (FrameworkSetting.rhinoInstructionCount > 0)
				cx.setInstructionObserverThreshold(FrameworkSetting.rhinoInstructionCount);
			// Initialize the standard objects (Object, Function,
			// etc.)
			// This must be done before scripts can be executed.
			// Returns
			// a scope object that we use in later calls.
			Scriptable scope = cx.initStandardObjects();
			if (ta.getTriggerCode().indexOf("$.") > -1) {
				RhinoScript se = new RhinoScript(scd, requestParams, this);
				Object wrappedOut = Context.javaToJS(se, scope);
				ScriptableObject.putProperty(scope, "$", wrappedOut);
			}
			// Collect the arguments into a single string.
			sc.append("\nvar _scd=").append(GenericUtil.fromMapToJsonString(scd));
			sc.append("\nvar _request=").append(GenericUtil.fromMapToJsonString(requestParams));
			sc.append("\nvar triggerAction='").append(action).append("';");
			if (!GenericUtil.isEmpty(ptablePk))
				sc.append("\n").append(t.get_tableFieldList().get(0).getDsc()).append("='").append(ptablePk)
						.append("';");
			sc.append("\n").append(ta.getTriggerCode());

			// sc.append("'})';");
			// Now evaluate the string we've colected.
			cx.evaluateString(scope, sc.toString(), null, 1, null);

			Object result = null;
			if (scope.has("result", scope))
				result = scope.get("result", scope);

			String msg = LocaleMsgCache.get2(scd, ta.getDsc());
			boolean b = false;
			if (result != null && result instanceof Undefined)
				result = null;
			else if (result != null && result instanceof Boolean)
				if ((Boolean) result == false)
					result = null;

			if (result != null) {
				msg = result.toString();
				short resultAction = ta.getLkpResultAction();
				if (scope.has("resultAction", scope))
					resultAction = (short) GenericUtil.uInt(scope.get("resultAction", scope).toString());
				switch (resultAction) {
				case 1: // readonly
					formResult.setViewMode(true);
				case 0: // continue
					formResult.getOutputMessages().add(msg);
					break;
				case 2: // confirm & continue
					if (!requestParams.containsKey("_confirmId_" + ta.getTableTriggerId()))
						throw new IWBException("confirm", "ConfirmId", ta.getTableTriggerId(), null, msg,
								null);
					break;
				case 3: // stop with message
					throw new IWBException("security", "TableTrigger", ta.getTableTriggerId(), null, msg,
							null);
				}
			}
		} catch (Exception e) {
			throw new IWBException("rhino", "TableEvent", ta.getTableTriggerId(), sc.toString(),
					"[1209," + ta.getTableTriggerId() + "] " + ta.getDsc(), e);
		} finally {
			// Exit from the context.
			cx.exit();
		}
	}
	
	public Object executeRhinoScript(Map<String, Object> scd, Map<String, String> requestParams, String script, Map obj,
			String result) {
		if (GenericUtil.isEmpty(script))
			return null;
		ContextFactory factory = RhinoContextFactory.getGlobal();
		Context cx = factory.enterContext();

		// Context cx = Context.enter();
		try {
			cx.setOptimizationLevel(-1);
			if (FrameworkSetting.rhinoInstructionCount > 0)
				cx.setInstructionObserverThreshold(FrameworkSetting.rhinoInstructionCount);
			// Initialize the standard objects (Object, Function, etc.)
			// This must be done before scripts can be executed. Returns
			// a scope object that we use in later calls.
			Scriptable scope = cx.initStandardObjects();

			// Collect the arguments into a single string.
			RhinoScript se = new RhinoScript(scd, requestParams, this);
			Object wrappedOut = Context.javaToJS(se, scope);
			ScriptableObject.putProperty(scope, "$", wrappedOut);

			StringBuilder sc = new StringBuilder();

			if (obj != null)
				sc.append("var _obj=").append(GenericUtil.fromMapToJsonString2Recursive(obj)).append(";\n");
			if (scd != null)
				sc.append("var _scd=").append(GenericUtil.fromMapToJsonString2(scd)).append(";\n");
			if (requestParams != null)
				sc.append("var _request=").append(GenericUtil.fromMapToJsonString2(requestParams)).append(";\n");
			if (script.charAt(0) == '!')
				script = script.substring(1);
			sc.append(script);

			script = sc.toString();

			cx.evaluateString(scope, script, null, 1, null);
			if (GenericUtil.isEmpty(result))
				result = "result";
			Object res = null;
			if (scope.has(result, scope)) {
				res = scope.get(result, scope);
				if (res != null && res instanceof org.mozilla.javascript.Undefined)
					res = null;
			}
			return res;

		} catch (Exception e) {
			throw new IWBException("rhino", "RhinoJS", 0, script, e.getMessage(), e);
		} finally {
			// Exit from the context.
			cx.exit();
		}
	}
	

	public void executeQueryAsRhino(W5QueryResult qr, String code) {
		ContextFactory factory = RhinoContextFactory.getGlobal();
		Context cx = factory.enterContext();

		// Context cx = Context.enter();
		W5Query q = qr.getQuery();
		String script = GenericUtil.uStrNvl(code, q.getSqlFrom());
		try {
			cx.setOptimizationLevel(-1);
			if (FrameworkSetting.rhinoInstructionCount > 0)
				cx.setInstructionObserverThreshold(FrameworkSetting.rhinoInstructionCount);
			// Initialize the standard objects (Object, Function, etc.)
			// This must be done before scripts can be executed. Returns
			// a scope object that we use in later calls.
			Scriptable scope = cx.initStandardObjects();

			if (script.charAt(0) == '!')
				script = script.substring(1);
			// Collect the arguments into a single string.
			RhinoScript se = new RhinoScript(qr.getScd(), qr.getRequestParams(), this);
			Object wrappedOut = Context.javaToJS(se, scope);
			ScriptableObject.putProperty(scope, "$", wrappedOut);

			StringBuilder sc = new StringBuilder();
			boolean hasOutParam = false;
			if (q.get_queryParams().size() > 0) {
				sc.append("var ");
				for (W5QueryParam p1 : q.get_queryParams()) {
					if (sc.length() > 4)
						sc.append(", ");
					sc.append(p1.getDsc()).append("=");
					String s = qr.getRequestParams().get(p1.getDsc());
					Object o = GenericUtil.isEmpty(s) ? null : GenericUtil.getObjectByTip(s, p1.getParamTip());
					if (o == null) {
						if (p1.getNotNullFlag() != 0)
							qr.getErrorMap().put(p1.getDsc(),
									LocaleMsgCache.get2(qr.getScd(), "validation_error_not_null"));
						sc.append("null");
					} else if ((o instanceof Integer) || (o instanceof Double) || (o instanceof BigDecimal)
							|| (o instanceof Boolean))
						sc.append(o);
					else if ((o instanceof Date))
						sc.append("'").append(GenericUtil.uFormatDateTime((Date) o)).append("'");
					else
						sc.append("'").append(o).append("'");
				}
				if (sc.length() > 4)
					sc.append(";\n");
				else
					sc.setLength(0);
			}

			if (!qr.getErrorMap().isEmpty()) {
				return;
			}
			sc.append("\nvar _scd=").append(GenericUtil.fromMapToJsonString2(qr.getScd())).append(";\nvar _request=")
					.append(GenericUtil.fromMapToJsonString2(qr.getRequestParams())).append(";\n").append(script);

			script = sc.toString();

			cx.evaluateString(scope, script, null, 1, null);
			if (scope.has("result", scope)) {
				Object r = scope.get("result", scope);
				if (r != null) {
					if (r instanceof NativeArray) {
						NativeArray ar = (NativeArray) r;
						int maxTabOrder = 0;
						qr.setNewQueryFields(new ArrayList());
						for (W5QueryField qf : q.get_queryFields()) {
							if (qf.getTabOrder() > maxTabOrder)
								maxTabOrder = qf.getTabOrder();
							qr.getNewQueryFields().add(qf);
						}
						for (W5QueryField qf : q.get_queryFields())
							if ((qf.getPostProcessTip() == 16 || qf.getPostProcessTip() == 17)
									&& qf.getLookupQueryId() != 0) {
								W5QueryResult queryFieldLookupQueryResult = metaDataDao.getQueryResult(qr.getScd(),
										qf.getLookupQueryId());
								if (queryFieldLookupQueryResult != null
										&& queryFieldLookupQueryResult.getQuery() != null) {
									W5QueryField field = new W5QueryField();
									field.setDsc(qf.getDsc() + "_qw_");
									field.setMainTableFieldId(qf.getMainTableFieldId());
									maxTabOrder++;
									field.setTabOrder((short) maxTabOrder);
									qr.getNewQueryFields().add(field);
									if (qf.getPostProcessTip() == 16
											&& queryFieldLookupQueryResult.getQuery().get_queryFields().size() > 2)
										for (int qi = 2; qi < queryFieldLookupQueryResult.getQuery().get_queryFields()
												.size(); qi++) {
											W5QueryField qf2 = queryFieldLookupQueryResult.getQuery().get_queryFields()
													.get(qi);
											field = new W5QueryField();
											field.setDsc(qf.getDsc() + "__" + qf2.getDsc());
											field.setMainTableFieldId(qf2.getMainTableFieldId());
											maxTabOrder++;
											field.setTabOrder((short) maxTabOrder);
											qr.getNewQueryFields().add(field);
										}
								}
							}
						qr.setData(new ArrayList((int) ar.getLength()));
						for (int qi = 0; qi < ar.getLength(); qi++) {
							NativeObject no = (NativeObject) (ar.get(qi, scope));
							Object[] o = new Object[maxTabOrder];
							qr.getData().add(o);
							for (W5QueryField qf : q.get_queryFields())
								if (no.has(qf.getDsc(), scope)) {
									Object o2 = no.get(qf.getDsc(), scope);
									if (o2 != null) {
										if (o2 instanceof NativeJavaObject) {
											o2 = ((NativeJavaObject) o2).unwrap();
										}
										switch (qf.getFieldTip()) {
										case 4: // integer
											o[qf.getTabOrder() - 1] = GenericUtil.uInt(RhinoUtil.rhinoValue(o2));
											break;
										case 8: // json
											if (o2 instanceof NativeArray) {
												NativeArray no2 = (NativeArray) o2;
												o[qf.getTabOrder() - 1] = RhinoUtil
														.fromNativeArrayToJsonString2Recursive(no2);
											} else if (o2 instanceof NativeObject) {
												NativeObject no2 = (NativeObject) o2;
												o[qf.getTabOrder() - 1] = RhinoUtil
														.fromNativeObjectToJsonString2Recursive(no2);
											} else if (o2 instanceof Map)
												o[qf.getTabOrder() - 1] = GenericUtil
														.fromMapToJsonString2Recursive((Map) o2);
											else if (o2 instanceof List)
												o[qf.getTabOrder() - 1] = GenericUtil
														.fromListToJsonString2Recursive((List) o2);
											else
												o[qf.getTabOrder() - 1] = "'" + GenericUtil.stringToJS(o2.toString())
														+ "'";
											break;
										default:
											o[qf.getTabOrder() - 1] = o2;
										}
									}
								}
						}
						qr.setFetchRowCount((int) ar.getLength());
						if (scope.has("extraOutMap", scope)) {
							Map extraOutMap = new HashMap();
							extraOutMap.put("rhino", scope.get("extraOutMap", scope));
							qr.setExtraOutMap(extraOutMap);
						}
					}
				}
			} else
				return;

		} catch (Exception e) {
			throw new IWBException("rhino", "Query", q.getQueryId(), script, "[8," + q.getQueryId() + "]", e);
		} finally {
			// Exit from the context.
			cx.exit();
		}
	}
	

	public Map executeQuery4StatWS(W5QueryResult queryResult) {

		W5WsMethod wsm = FrameworkCache.getWsMethod(queryResult.getScd(), queryResult.getQuery().getMainTableId());
		Map<String, Object> scd = queryResult.getScd();
/*		if (wsm.get_params() == null) {
			wsm.set_params(find("from W5WsMethodParam t where t.wsMethodId=? AND t.projectUuid=? order by t.tabOrder",
					wsm.getWsMethodId(), scd.get("projectIdId")));
			wsm.set_paramMap(new HashMap());
			for (W5WsMethodParam wsmp : wsm.get_params())
				wsm.get_paramMap().put(wsmp.getWsMethodParamId(), wsmp);
		}*/
		W5WsMethodParam parentParam = null;
		for (W5WsMethodParam px : wsm.get_params())
			if (px.getOutFlag() != 0 && px.getParamTip() == 10) {
				parentParam = px;
				break;
			}
		Map<String, String> m2 = new HashMap();
		Map<String, String> requestParams = queryResult.getRequestParams();

		int statType = GenericUtil.uInt(requestParams, "_stat"); // 0:count,
																	// 1:sum,
																	// 2.avg
		String funcFields = requestParams.get("_ffids"); // statFunctionFields
		if (statType > 0 && GenericUtil.isEmpty(funcFields))
			throw new IWBException("framework", "Query", queryResult.getQueryId(), null,
					LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_grid_stat_func_fields"), null);

		int queryFieldId = GenericUtil.uInt(requestParams, "_qfid");
		int stackFieldId = GenericUtil.uInt(requestParams, "_sfid");
		if (stackFieldId > 0 && stackFieldId == queryFieldId)
			stackFieldId = 0;

		for (W5QueryParam qp : queryResult.getQuery().get_queryParams())
			if (!GenericUtil.isEmpty(requestParams.get(qp.getDsc()))) {
				m2.put(qp.getExpressionDsc(), requestParams.get(qp.getDsc()));
			}
		StringBuilder rc = new StringBuilder();
		rc.append("function _x_(x){\nreturn {").append(queryResult.getQuery().getSqlSelect())
				.append("\n}}\nvar result=[], q=$.REST('").append(wsm.get_ws().getDsc() + "." + wsm.getDsc())
				.append("',").append(GenericUtil.fromMapToJsonString2(m2))
				.append(");\nif(q && q.get('success')){q=q.get('").append(parentParam.getDsc())
				.append("');for(var i=0;i<q.size();i++)result.push(_x_(q.get(i)));}");
		executeQueryAsRhino(queryResult, rc.toString());
		Map result = new HashMap();
		result.put("success", true);
		if (queryResult.getErrorMap().isEmpty()) {
			List<Map> data = new ArrayList();
			Map<String, Map> mdata = new HashMap();
			W5QueryField statQF = null, funcQF = null;
			W5LookUp statLU = null;
			for (W5QueryField qf : queryResult.getQuery().get_queryFields())
				if (qf.getQueryFieldId() == queryFieldId) {
					statQF = qf;
					if (qf.getPostProcessTip() == 10)
						statLU = FrameworkCache.getLookUp(scd, qf.getLookupQueryId());
					break;
				}
			if (statType > 0) {
				int funcFieldId = GenericUtil.uInt(funcFields.split(",")[0]);
				for (W5QueryField qf : queryResult.getQuery().get_queryFields())
					if (qf.getQueryFieldId() == funcFieldId) {
						funcQF = qf;
						break;
					}
			}
			for (Object[] o : queryResult.getData()) {
				Object okey = o[statQF.getTabOrder() - 1];
				String key = "_";
				if (okey != null)
					key = okey.toString();
				Map mr = mdata.get(key);
				if (mr == null) {
					mr = new HashMap();
					mr.put("id", key);
					if (statLU != null) {
						W5LookUpDetay ld = statLU.get_detayMap().get(key.toString());
						mr.put("dsc", LocaleMsgCache.get2(scd, ld.getDsc()));
					} else
						mr.put("dsc", key);
					mr.put("xres", BigDecimal.ZERO);
					mr.put("xcnt", 0);
					mdata.put(key, mr);
					data.add(mr);
				}
				switch (statType) {
				case 0: // count
				case 2: // avg
					int cnt = (Integer) mr.get("xcnt");
					cnt++;
					mr.put("xcnt", cnt);
					if (statType == 0)
						break;
				case 1: // sum
					BigDecimal res = (BigDecimal) mr.get("xres");
					res = res.add(GenericUtil.uBigDecimal2(o[funcQF.getTabOrder() - 1]));
					mr.put("xres", res);
					break;
				}
			}
			for (Map mr : data)
				switch (statType) {
				case 0: // count
					mr.put("xres", mr.get("xcnt"));
					break;
				case 2: // avg
					int cnt = (Integer) mr.get("xcnt");
					if (cnt > 0)
						mr.put("xres", ((BigDecimal) mr.get("xres")).divide(new BigDecimal(cnt)));
					break;
				}

			result.put("data", data);
		}

		return result;
	}
	

	public W5GlobalFuncResult executeGlobalFunc4Debug(Map<String, Object> scd, int dbFuncId,
			Map<String, String> parameterMap) {
		W5GlobalFuncResult r = dbFuncId == -1 ? new W5GlobalFuncResult(-1) : metaDataDao.getGlobalFuncResult(scd, dbFuncId);
		r.setScd(scd);
		r.setErrorMap(new HashMap());
		r.setRequestParams(parameterMap);
		ContextFactory factory = RhinoContextFactory.getGlobal();
		Context cx = factory.enterContext();

		// Context cx = Context.enter();
		String script = null;
		try {
			cx.setOptimizationLevel(-1);
			if (FrameworkSetting.rhinoInstructionCount > 0)
				cx.setInstructionObserverThreshold(FrameworkSetting.rhinoInstructionCount);
			// Initialize the standard objects (Object, Function, etc.)
			// This must be done before scripts can be executed. Returns
			// a scope object that we use in later calls.
			Scriptable scope = cx.initStandardObjects();

			script = parameterMap.get("_rhino_script_code");
			if (script.charAt(0) == '!')
				script = script.substring(1);
			// Collect the arguments into a single string.
			StringBuilder sc = new StringBuilder();

			RhinoScript se = new RhinoScript(r.getScd(), r.getRequestParams(), this);
			Object wrappedOut = Context.javaToJS(se, scope);
			ScriptableObject.putProperty(scope, "$", wrappedOut);

			boolean hasOutParam = false;
			if (dbFuncId != -1 && r.getGlobalFunc().get_dbFuncParamList().size() > 0) {
				sc.append("var ");
				for (W5GlobalFuncParam p1 : r.getGlobalFunc().get_dbFuncParamList())
					if (p1.getOutFlag() == 0) {
						if (sc.length() > 4)
							sc.append(", ");
						sc.append(p1.getDsc()).append("=");
						String s = parameterMap.get(p1.getDsc());
						Object o = GenericUtil.isEmpty(s) ? null : GenericUtil.getObjectByTip(s, p1.getParamTip());
						if (o == null) {
							if (p1.getNotNullFlag() != 0)
								r.getErrorMap().put(p1.getDsc(), LocaleMsgCache.get2(scd, "validation_error_not_null"));
							sc.append("null");
						} else if ((o instanceof Integer) || (o instanceof Double) || (o instanceof BigDecimal)
								|| (o instanceof Boolean))
							sc.append(o);
						else if ((o instanceof Date))
							sc.append("'").append(GenericUtil.uFormatDate((Date) o)).append("'");
						else
							sc.append("'").append(o).append("'");
					} else
						hasOutParam = true;
				if (sc.length() > 4)
					sc.append(";\n");
				else
					sc.setLength(0);
			}
			if (!r.getErrorMap().isEmpty()) {
				r.setSuccess(false);
				return r;
			}
			sc.append("\nvar _scd=").append(GenericUtil.fromMapToJsonString2(r.getScd())).append(";\nvar _request=")
					.append(GenericUtil.fromMapToJsonString2(r.getRequestParams())).append(";\n").append(script);

			script = sc.toString();

			if (FrameworkSetting.debug)
				se.console("start: " + (r.getGlobalFunc() != null ? r.getGlobalFunc().getDsc() : "new"), "DEBUG",
						"info");
			long startTm = System.currentTimeMillis();
			cx.evaluateString(scope, script, null, 1, null);
			r.setProcessTime((int) (System.currentTimeMillis() - startTm));
			// if(FrameworkSetting.debug)se.console("end: " +
			// (r.getGlobalFunc()!=null ?
			// r.getGlobalFunc().getDsc(): "new"),"DEBUG","success");
			/*
			 * if(scope.has("errorMsg", scope)){ Object em =
			 * RhinoUtil.rhinoValue(scope.get("errorMsg", scope));
			 * if(em!=null)throw new PromisException("rhino","GlobalFuncId",
			 * r.getGlobalFuncId(), script,
			 * LocaleMsgCache.get2(0,(String)r.getScd().get("locale"),em.
			 * toString()), null); }
			 */
			if (hasOutParam) {
				// JSONObject jo=new JSONObject();
				Map<String, String> res = new HashMap<String, String>();
				r.setResultMap(res);
				for (W5GlobalFuncParam p1 : r.getGlobalFunc().get_dbFuncParamList())
					if (p1.getOutFlag() != 0 && scope.has(p1.getDsc(), scope)) {
						Object em = RhinoUtil.rhinoValue(scope.get(p1.getDsc(), scope));
						if (em != null)
							res.put(p1.getDsc(), em.toString());
					}
			}
			if (scope.has("outMsgs", scope)) { // TODO
				Object em = scope.get("outMsgs", scope);
			}
		} catch (Exception e) {
			throw new IWBException("rhino", "Debug Backend", r.getGlobalFuncId(), script,
					LocaleMsgCache.get2(0, (String) r.getScd().get("locale"), e.getMessage()), e);
		} finally {
			// Exit from the context.
			cx.exit();
		}
		r.setSuccess(true);
		return r;
	}


	public Map executeQueryAsRhino4Debug(W5QueryResult qr, String script) {
		ContextFactory factory = RhinoContextFactory.getGlobal();
		Context cx = factory.enterContext();

		// Context cx = Context.enter();
		W5Query q = qr.getQuery();
		Map m = new HashMap();
		m.put("success", true);
		// String script = q.getSqlFrom();
		try {
			cx.setOptimizationLevel(-1);
			if (FrameworkSetting.rhinoInstructionCount > 0)
				cx.setInstructionObserverThreshold(FrameworkSetting.rhinoInstructionCount);
			// Initialize the standard objects (Object, Function, etc.)
			// This must be done before scripts can be executed. Returns
			// a scope object that we use in later calls.
			Scriptable scope = cx.initStandardObjects();

			if (script.charAt(0) == '!')
				script = script.substring(1);
			// Collect the arguments into a single string.
			RhinoScript se = new RhinoScript(qr.getScd(), qr.getRequestParams(), this);
			Object wrappedOut = Context.javaToJS(se, scope);
			ScriptableObject.putProperty(scope, "$", wrappedOut);

			StringBuilder sc = new StringBuilder();

			boolean hasOutParam = false;
			if (q.get_queryParams().size() > 0) {
				sc.append("var ");
				for (W5QueryParam p1 : q.get_queryParams()) {
					if (sc.length() > 4)
						sc.append(", ");
					sc.append(p1.getDsc()).append("=");
					String s = qr.getRequestParams().get(p1.getDsc());
					Object o = GenericUtil.isEmpty(s) ? null : GenericUtil.getObjectByTip(s, p1.getParamTip());
					if (o == null) {
						if (p1.getNotNullFlag() != 0)
							qr.getErrorMap().put(p1.getDsc(),
									LocaleMsgCache.get2(qr.getScd(), "validation_error_not_null"));
						sc.append("null");
					} else if ((o instanceof Integer) || (o instanceof Double) || (o instanceof BigDecimal)
							|| (o instanceof Boolean))
						sc.append(o);
					else if ((o instanceof Date))
						sc.append("'").append(GenericUtil.uFormatDate((Date) o)).append("'");
					else
						sc.append("'").append(o).append("'");
				}
				if (sc.length() > 4)
					sc.append(";\n");
				else
					sc.setLength(0);
			}
			if (!qr.getErrorMap().isEmpty()) {
				throw new IWBException("rhino", "QueryId", q.getQueryId(), script,
						"Validation ERROR: " + GenericUtil.fromMapToJsonString2(qr.getErrorMap()), null);
			}
			sc.append("\nvar _scd=").append(GenericUtil.fromMapToJsonString2(qr.getScd())).append(";\nvar _request=")
					.append(GenericUtil.fromMapToJsonString2(qr.getRequestParams())).append(";\n").append(script);

			script = sc.toString();

			se.console("Start: " + (q.getDsc() != null ? q.getDsc() : "new"), "iWB-QUERY-DEBUG", "warn");
			long startTm = System.currentTimeMillis();
			cx.evaluateString(scope, script, null, 1, null);
			m.put("execTime", System.currentTimeMillis() - startTm);
			se.console("End: " + (q.getDsc() != null ? q.getDsc() : "new"), "iWB-QUERY-DEBUG", "warn");
			startTm = System.currentTimeMillis();
			List data = null;
			if (scope.has("result", scope)) {
				Object r = scope.get("result", scope);
				if (r != null) {
					if (r instanceof NativeArray) {
						NativeArray ar = (NativeArray) r;
						int maxTabOrder = 0;
						for (W5QueryField qf : q.get_queryFields()) {
							if (qf.getTabOrder() > maxTabOrder)
								maxTabOrder = qf.getTabOrder();
						}
						qr.setNewQueryFields(q.get_queryFields());
						data = new ArrayList((int) ar.getLength());
						for (int qi = 0; qi < ar.getLength(); qi++) {
							NativeObject no = (NativeObject) (ar.get(qi, scope));
							Object[] o = new Object[maxTabOrder];
							Map d = new HashMap();
							data.add(d);
							for (W5QueryField qf : q.get_queryFields())
								if (no.has(qf.getDsc(), scope)) {
									// o[qf.getTabOrder()-1] =
									// no.get(qf.getDsc(), scope);
									d.put(qf.getDsc(), RhinoUtil.rhinoValue(no.get(qf.getDsc(), scope)));
								}
						}
						m.put("fetchTime", System.currentTimeMillis() - startTm);
						Map m2 = new HashMap();
						m2.put("startRow", 0);
						m2.put("fetchCount", (int) ar.getLength());
						m2.put("totalCount", (int) ar.getLength());
						m.put("browseInfo", m2);
					}
				}
			}
			if (data == null)
				throw new IWBException("rhino", "QueryId", q.getQueryId(), script, "[result] object not found", null);
			m.put("data", data);
			List fields = new ArrayList();
			for (W5QueryField qf : q.get_queryFields()) {
				Map d = new HashMap();
				d.put("name", qf.getDsc());
				switch (qf.getFieldTip()) {
				case 3:
					d.put("type", "int");
					break;
				case 4:
					d.put("type", "float");
					break;
				case 2:
					d.put("type", "date");
					break;
				}
				fields.add(d);
			}
			m.put("fields", fields);
		} catch (Exception e) {
			throw new IWBException("rhino", "Debug Query", q.getQueryId(), script,
					LocaleMsgCache.get2(0, (String) qr.getScd().get("locale"), e.getMessage()), e);
		} finally {
			// Exit from the context.
			cx.exit();
		}
		return m;
	}
	
	
}
