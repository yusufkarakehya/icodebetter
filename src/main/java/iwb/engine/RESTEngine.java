package iwb.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import iwb.cache.FrameworkCache;
import iwb.cache.FrameworkSetting;
import iwb.dao.rdbms_impl.MetadataLoaderDAO;
import iwb.dao.rdbms_impl.PostgreSQL;
import iwb.domain.db.Log5WsMethodAction;
import iwb.domain.db.W5Param;
import iwb.domain.db.W5VcsObject;
import iwb.domain.db.W5Ws;
import iwb.domain.db.W5WsMethod;
import iwb.domain.db.W5WsMethodParam;
import iwb.domain.db.W5WsServer;
import iwb.domain.db.W5WsServerMethod;
import iwb.exception.IWBException;
import iwb.util.GenericUtil;
import iwb.util.HttpUtil;
import iwb.util.LogUtil;
import iwb.util.ScriptUtil;
import jdk.nashorn.api.scripting.ScriptObjectMirror;


@Component
public class RESTEngine {
	@Lazy
	@Autowired
	private PostgreSQL dao;


	@Lazy
	@Autowired
	private MetadataLoaderDAO metaDataDao;
	
	public Map<String, Object> getWsServerMethodObjects(W5WsServer wss) {
		Map<String, Object> wsmoMap = new HashMap();
		Map scd = new HashMap();
		scd.put("projectId", wss.getProjectUuid());
		for (W5WsServerMethod wsm : wss.get_methods())
			try {
				switch (wsm.getObjectTip()) {
				case 0:
				case 1:
				case 2:
				case 3: // form
					wsmoMap.put(wsm.getDsc(), metaDataDao.getFormResult(scd, wsm.getObjectId(),
							wsm.getObjectTip() == 0 ? 1 : wsm.getObjectTip(), new HashMap()));
					break;
				case 4:
					wsmoMap.put(wsm.getDsc(), metaDataDao.getGlobalFuncResult(scd, wsm.getObjectId()));
					break;
				case 19:
					wsmoMap.put(wsm.getDsc(), metaDataDao.getQueryResult(scd, wsm.getObjectId()));
					break;
				case 31:
				case 32:
				case 33:
					wsmoMap.put(wsm.getDsc(), FrameworkCache.getWorkflow(scd, wsm.getObjectId()));
					break;
				default:
					wsmoMap.put(wsm.getDsc(), "Wrong ObjectTip");
				}
			} catch (Exception e) {
				wsmoMap.put(wsm.getDsc(), "Invalid Object");
			}
		return wsmoMap;
	}

	private List recursiveParams2List(Map scd, int paramId, Object reqL, List<W5WsMethodParam> params,
			Map<String, String> errorMap, int minListSize) {
		List l = new ArrayList();
		if (GenericUtil.isEmpty(reqL) && minListSize < 1)
			return l;
		List requestList = null;
		if (reqL instanceof List)
			requestList = (List) reqL;
		else if (reqL instanceof ScriptObjectMirror)
			try {
				requestList = ScriptUtil.fromScriptObject2List(reqL);
			} catch (Exception ee) {
			}
		else if (reqL instanceof JSONArray)
			try {
				requestList = GenericUtil.fromJSONArrayToList((JSONArray) reqL);
			} catch (Exception ee) {
			}
		if (GenericUtil.isEmpty(requestList) && minListSize < 1)
			return l;
		if (requestList == null)
			requestList = new ArrayList();
		for (int qi = requestList.size(); qi < minListSize; qi++)
			requestList.add(new HashMap());
		for (Object reqO : requestList) {
			l.add(recursiveParams2Map(scd, paramId, reqO, params, errorMap, new HashMap()));
		}
		return l;
	}

	private Map recursiveParams2Map(Map scd, int paramId, Object reqP, List<W5WsMethodParam> params,
			Map<String, String> errorMap, Map<String, String> reqPropMap) {
		Map m = new HashMap();
		if (GenericUtil.isEmpty(params))
			return m;
		Map requestParams = null;
		if (reqP instanceof Map)
			requestParams = (Map) reqP;
		else if (reqP instanceof ScriptObjectMirror)
			try {// TODO
				requestParams = ScriptUtil.fromScriptObject2Map(reqP);
			} catch (Exception ee) {
				return null;
			}
		else if (reqP instanceof JSONObject)
			try {// TODO
				requestParams = GenericUtil.fromJSONObjectToMap((JSONObject) reqP);
			} catch (Exception ee) {
				return null;
			}
		else
			requestParams = new HashMap();

		for (W5WsMethodParam p : params)
			if (p.getOutFlag() == 0 && p.getParentWsMethodParamId() == paramId) {
				if (p.getParamTip() == 9 || p.getParamTip() == 8) { // object/json
					m.put(p.getDsc(), recursiveParams2Map(scd, p.getWsMethodParamId(), requestParams.get(p.getDsc()),
							params, errorMap, reqPropMap));
				} else if (p.getParamTip() == 10) {// array
					if (p.getSourceTip() == 0) { // constant ise altini da
													// doldur
						m.put(p.getDsc(),
								recursiveParams2List(scd, p.getWsMethodParamId(), requestParams.get(p.getDsc()), params,
										errorMap, GenericUtil.uInt(p.getDefaultValue())));
					} else { // aksi halde oldugu gibi yaz
						Object res = null;
						if (requestParams.containsKey(p.getDsc()))
							res = requestParams.get(p.getDsc());
						if (GenericUtil.isEmpty(res))
							res = p.getDefaultValue();
						if (!GenericUtil.isEmpty(res)) {
							if (res instanceof String || res instanceof List)
								m.put(p.getDsc(), res);
							else
								try {
									m.put(p.getDsc(), GenericUtil.fromJSONArrayToList(new JSONArray(res.toString())));
								} catch (Exception ee) {
									m.put(p.getDsc(), null);
								}
						}
					}
				} else {
					Object o = GenericUtil.prepareParam((W5Param) p, scd, requestParams, p.getSourceTip(), null,
							p.getNotNullFlag(), null, null, errorMap, dao);
					if (o != null && o.toString().length() > 0) {
						if (p.getCredentialsFlag() != 0)
							reqPropMap.put(p.getDsc(), o.toString());
						else {
							m.put(p.getDsc(), o);
						}
					}
				}
			}

		return m;
	}

	public Map REST(Map<String, Object> scd, String name, Map requestParams) throws IOException {
		String[] u = name.replace('.', ',').split(",");
		if (u.length < 2)
			throw new IWBException("ws", "Wrong ServiceName", 0, null, "Call should be [serviceName].[methodName]",
					null);
		W5Ws ws = FrameworkCache.getWsClient(scd, u[0]);
		if (ws == null)
			throw new IWBException("ws", "Wrong ServiceName", 0, null, "Could find [" + u[0] + "]", null);
		W5WsMethod wsm = null;
		for (W5WsMethod twm : ws.get_methods())
			if (twm.getDsc().equals(u[1])) {
				wsm = twm;
				break;
			}
		if (wsm == null)
			throw new IWBException("ws", "Wrong MethodName", 0, null, "Could find [" + u[1] + "]", null);

		if (!GenericUtil.accessControl(scd, ws.getAccessExecuteTip(), ws.getAccessExecuteRoles(),
				ws.getAccessExecuteUsers())
				|| !GenericUtil.accessControl(scd, wsm.getAccessExecuteTip(), wsm.getAccessExecuteRoles(),
						wsm.getAccessExecuteUsers())) {
			throw new IWBException("security", "WS Method Call", wsm.getWsMethodId(), null, "Access Forbidden", null);
		}
		try {
			String projectId = (String) scd.get("projectId");
			if (wsm.get_params() == null) {
				wsm.set_params(
						dao.find("from W5WsMethodParam t where t.wsMethodId=? AND t.projectUuid=? order by t.tabOrder",
								wsm.getWsMethodId(), projectId));
				wsm.set_paramMap(new HashMap());
				for (W5WsMethodParam wsmp : wsm.get_params())
					wsm.get_paramMap().put(wsmp.getWsMethodParamId(), wsmp);
			}
			String tokenKey = null;
			Map m = new HashMap();
			Map errorMap = new HashMap();
			if (ws.getWssTip() == 2) { // token ise ve token yok ise
				if (ws.getWssLoginMethodId() == null || ws.getWssLoginMethodParamId() == null
						|| ws.getWssLoginTimeout() == null)
					throw new IWBException("security", "WS Method Call", wsm.getWsMethodId(), null,
							"WSS: Token Properties Not Defined", null);
				if (ws.getWssLoginMethodId() != wsm.getWsMethodId() && ws.getWssLoginMethodParamId() != null
						&& (ws.getWssLogoutMethodId() == null || ws.getWssLogoutMethodId() == wsm.getWsMethodId())) {
					tokenKey = (String) ws.loadValue("tokenKey");
					Long tokenTimeout = (Long) ws.loadValue("tokenKey.timeOut");
					W5WsMethod loginMethod = FrameworkCache.getWsMethod(scd, ws.getWssLoginMethodId());
					if (loginMethod.get_params() == null) {
						loginMethod.set_params(dao.find(
								"from W5WsMethodParam t where t.wsMethodId=? AND t.projectUuid=? order by t.tabOrder",
								loginMethod.getWsMethodId(), projectId));
						loginMethod.set_paramMap(new HashMap());
						for (W5WsMethodParam wsmp : loginMethod.get_params())
							loginMethod.get_paramMap().put(wsmp.getWsMethodParamId(), wsmp);
					}
					W5WsMethodParam tokenParam = loginMethod.get_paramMap().get(ws.getWssLoginMethodParamId());
					if (tokenKey == null || tokenTimeout == null || tokenTimeout <= System.currentTimeMillis()) { // yeni
																													// bir
																													// token
																													// alinacak
						if (tokenParam != null) {
							Map tokenResult = REST(scd, ws.getDsc() + "." + loginMethod.getDsc(), new HashMap());
							Object o = tokenResult.get(tokenParam.getDsc());
							if (o == null)
								throw new IWBException("security", "WS Method Call", wsm.getWsMethodId(), null,
										"WSS: Auto-Login Failed", null);
							tokenKey = o.toString();
							ws.storeValue("tokenKey", tokenKey);
							ws.storeValue("tokenKey.timeOut",
									System.currentTimeMillis() + ws.getWssLoginTimeout().longValue());
						}
					}
					requestParams.put(tokenParam.getDsc(), tokenKey);
				}
			}

			Map<String, Object> result = new HashMap();
			switch (ws.getWsTip()) {
			case 1: // soap
				break;
			case 2: // rest
				String url = ws.getWsUrl();
				if (url.indexOf("${") > -1) {// has special char
					url = GenericUtil.filterExt(url, scd, requestParams, null).toString();
				}
				if (!url.endsWith("/"))
					url += "/";
				url += GenericUtil.isEmpty(wsm.getRealDsc()) ? wsm.getDsc() : wsm.getRealDsc();
				String params = null;
				Map<String, String> reqPropMap = new HashMap();
				reqPropMap.put("Content-Language", "tr-TR");
				if (wsm.getHeaderAcceptTip() != null) {
					reqPropMap.put("Accept", new String[] { "text/plain", "application/json", "application/xml" }[wsm
							.getHeaderAcceptTip()]);
				}
				if (ws.getWssTip() == 1 && !GenericUtil.isEmpty(ws.getWssCredentials())) { // credentials
					String cr = ws.getWssCredentials();
					if (cr.indexOf("${") > -1) {// has special char
						cr = GenericUtil.filterExt(cr, scd, requestParams, null).toString();
					}
					String[] lines = cr.split("\n");
					for (int qi = 0; qi < lines.length; qi++) {
						int ii = lines[qi].indexOf(':');
						if (ii > 0) {
							reqPropMap.put(lines[qi].substring(0, ii).trim(), lines[qi].substring(ii + 1).trim());
						}
					}
				}
				if (!GenericUtil.isEmpty(wsm.get_params()) && wsm.getParamSendTip() > 0) {
					m = recursiveParams2Map(scd, 0, requestParams, wsm.get_params(), errorMap, reqPropMap);
					if (!errorMap.isEmpty()) {
						throw new IWBException("validation", "WS Method Call", wsm.getWsId(), null,
								"Wrong Parameters: + " + GenericUtil.fromMapToJsonString2(errorMap), null);
					}
					switch (wsm.getParamSendTip()) {
					case 1: // form
					case 3: // form as post_url
						params = GenericUtil.fromMapToURI(m);
						if (wsm.getParamSendTip() == 3) {
							if (!GenericUtil.isEmpty(params)) {
								if (url.indexOf('?') == -1)
									url += "?";
								url += params;
							}
							params = null;
						}
						reqPropMap.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
						break;
					case 2: // json
						params = GenericUtil.fromMapToJsonString2Recursive(m);
						reqPropMap.put("Content-Type", "application/json;charset=UTF-8");
						break;
					case 6:// yaml
						params = GenericUtil.fromMapToYamlString2Recursive(m, 0);
						reqPropMap.put("Content-Type", "application/yaml;charset=UTF-8");
						break;
					}
				}
				if (wsm.getPostUrlFlag() != 0) { // post_url_flag
					String postUrl = (String) requestParams.get("_post_url");
					if (!GenericUtil.isEmpty(postUrl))
						url += postUrl;
				}

				Log5WsMethodAction log = new Log5WsMethodAction(scd, wsm.getWsMethodId(), url, params);
				String x = HttpUtil.send(url, params,
						new String[] { "GET", "POST", "PUT", "PATCH", "DELETE" }[wsm.getCallMethodTip()], reqPropMap);
				if (!GenericUtil.isEmpty(x))
					try {// System.out.println(x);
						log.setResponse(x);
						String xx = x.trim();
						if (xx.length() > 0)
							switch (xx.charAt(0)) {
							case '{':
								JSONObject jo = new JSONObject(x);
								result.putAll(GenericUtil.fromJSONObjectToMap(jo));
								break;
							case '[':
								JSONArray ja = new JSONArray(x);
								result.put("data", GenericUtil.fromJSONArrayToList(ja));
								break;
							default:
								if (x.indexOf('\r') > -1)
									x = x.replace('\r', '\n');
								result.put("data", x);
							}
						if (GenericUtil.uInt(requestParams.get("_iwb_cfg")) != 0) {
							result.put("_iwb_cfg_rest_method", wsm);
						}
					} catch (JSONException e) {
						throw new RuntimeException(e);
					}
				if (FrameworkSetting.log2tsdb) {
					log.calcProcessTime();
					LogUtil.logObject(log);
				}
				break;
			}

			return result;
		} catch (Exception e) {
			throw new IWBException("framework", "RESTService_Method", wsm.getWsMethodId(), null,
					"[1376," + wsm.getWsMethodId() + "] " + name, e);
		}
	}


	private W5WsMethodParam findWSMethodParamByName(List<W5WsMethodParam> l, String name) {
		if (GenericUtil.isEmpty(l))
			return null;
		for (W5WsMethodParam p : l)
			if (p.getDsc().equals(name) && p.getOutFlag() != 0) {
				return p;
			}
		return null;
	}
	
	public Map organizeREST(Map<String, Object> scd, String serviceName) {
		Map result = new HashMap();
		result.put("success", true);
		try {
			Map rm = new HashMap();
			rm.put("_iwb_cfg", 1);
			Map<String, Object> r = REST(scd, serviceName, rm);
			W5WsMethod wsm = (W5WsMethod) r.get("_iwb_cfg_rest_method");
			W5WsMethodParam p = null;
			List<Object> dataObject = null;
			if (r.containsKey("data")) {
				Object data = r.get("data");
				p = findWSMethodParamByName(wsm.get_params(), "data");
				if (data != null && data instanceof List) {
					if (p == null) {
						p = new W5WsMethodParam();
						p.setWsMethodParamId(
								GenericUtil.getGlobalNextval("iwb.seq_ws_method_param", scd.get("projectId").toString(),
										(Integer) scd.get("userId"), (Integer) scd.get("customizationId")));
						p.setWsMethodId(wsm.getWsMethodId());
						p.setDsc("data");
						p.setOutFlag((short) 1);
						p.setProjectUuid(scd.get("projectId").toString());
						p.setParamTip((short) 10);
						p.setTabOrder((short) 100);
						dao.saveObject(p);
						dao.saveObject(new W5VcsObject(scd, 1377, p.getWsMethodParamId()));
					}
					dataObject = (List) data;
				}

			} else
				for (String key : r.keySet())
					if (r.get(key) instanceof List) {
						dataObject = (List) r.get(key);
						p = findWSMethodParamByName(wsm.get_params(), key);
						if (p == null) {
							p = new W5WsMethodParam();
							p.setWsMethodParamId(GenericUtil.getGlobalNextval("iwb.seq_ws_method_param",
									scd.get("projectId").toString(), (Integer) scd.get("userId"),
									(Integer) scd.get("customizationId")));
							p.setWsMethodId(wsm.getWsMethodId());
							p.setDsc(key);
							p.setOutFlag((short) 1);
							p.setProjectUuid(scd.get("projectId").toString());
							p.setParamTip((short) 10);
							p.setTabOrder((short) 100);
							dao.saveObject(p);
							dao.saveObject(new W5VcsObject(scd, 1377, p.getWsMethodParamId()));
						}
					}
			if (dataObject != null)
				for (Object o : dataObject)
					if (o != null) {
						if (o instanceof Map) {
							short tabOrder = 110;
							Map<String, Object> om = (Map<String, Object>) o;
							for (String key : om.keySet())
								if (findWSMethodParamByName(wsm.get_params(), key) == null) {
									W5WsMethodParam p2 = new W5WsMethodParam();
									p2.setWsMethodParamId(GenericUtil.getGlobalNextval("iwb.seq_ws_method_param",
											scd.get("projectId").toString(), (Integer) scd.get("userId"),
											(Integer) scd.get("customizationId")));
									p2.setWsMethodId(wsm.getWsMethodId());
									p2.setParentWsMethodParamId(p.getWsMethodParamId());
									p2.setDsc(key);
									p2.setOutFlag((short) 1);
									p2.setProjectUuid(scd.get("projectId").toString());
									p2.setParamTip((short) 1);
									if (om.get(key) != null && om.get(key) instanceof List)
										p2.setParamTip((short) 10);
									if (om.get(key) != null && om.get(key) instanceof Map)
										p2.setParamTip((short) 9);
									p2.setTabOrder(tabOrder);
									tabOrder += 10;
									dao.saveObject(p2);
									dao.saveObject(new W5VcsObject(scd, 1377, p2.getWsMethodParamId()));

									if (om.get(key) != null && om.get(key) instanceof Map) {
										short tabOrder2 = (short) (10 * tabOrder);
										Map<String, Object> om2 = (Map<String, Object>) om;
										for (String key2 : om2.keySet())
											if (findWSMethodParamByName(wsm.get_params(), key2) == null) {
												W5WsMethodParam p22 = new W5WsMethodParam();
												p22.setWsMethodParamId(GenericUtil.getGlobalNextval(
														"iwb.seq_ws_method_param", scd.get("projectId").toString(),
														(Integer) scd.get("userId"),
														(Integer) scd.get("customizationId")));
												p22.setWsMethodId(wsm.getWsMethodId());
												p22.setParentWsMethodParamId(p2.getWsMethodParamId());
												p22.setDsc(key2);
												p22.setOutFlag((short) 1);
												p22.setProjectUuid(scd.get("projectId").toString());
												p22.setParamTip((short) 1);
												if (om2.get(key2) != null && om2.get(key2) instanceof List)
													p22.setParamTip((short) 10);
												p22.setTabOrder(tabOrder2);
												tabOrder2 += 10;
												dao.saveObject(p22);
												dao.saveObject(new W5VcsObject(scd, 1377, p22.getWsMethodParamId()));
											}
									}
								}
						}
						break;
					}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}
