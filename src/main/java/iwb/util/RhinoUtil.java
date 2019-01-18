package iwb.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJavaArray;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

import iwb.cache.FrameworkSetting;
import iwb.cache.LocaleMsgCache;
import iwb.domain.db.W5Param;

public class RhinoUtil {

	public static NativeObject fromJSONObjectToNativeObject(JSONObject o) throws JSONException {
		if (o == null)
			return null;
		NativeObject no = new NativeObject();
		for (Iterator<String> it = o.keys(); it.hasNext();) {
			String key = it.next();
			Object val = o.get(key);
			if (val == null) {
				no.put(key, no, null);
			} else if (val instanceof JSONObject) {
				no.put(key, no, fromJSONObjectToNativeObject((JSONObject) val));
			} else if (val instanceof JSONArray) {
				no.put(key, no, fromJSONArrayToNativeArray((JSONArray) val));
			} else
				no.put(key, no, val.toString());
		}
		return no;
	}

	public static NativeArray fromJSONArrayToNativeArray(JSONArray a) throws JSONException {
		if (a == null)
			return null;
		NativeArray na = new NativeArray(a.length());
		for (int qi = 0; qi < a.length(); qi++) {
			Object val = a.get(qi);
			if (val == null) {
				na.putProperty(na, qi, null);
				;
			} else if (val instanceof JSONObject) {
				na.putProperty(na, qi, fromJSONObjectToNativeObject((JSONObject) val));
				;
				// no.put(key, null,
				// fromJSONObjectToNativeObject(o.getJSONObject(key)));
			} else if (val instanceof JSONArray) {
				na.putProperty(na, qi, fromJSONArrayToNativeArray((JSONArray) val));
				;
				// no.put(key, null,
				// fromJSONArrayToNativeArray(o.getJSONArray(key)));
			} else
				na.putProperty(na, qi, val.toString());
			;
			// no.put(key, null, val.toString());
		}
		return na;
	}
	

	private static Object getJavaObject(Object val) throws JSONException {
		if (val == null)
			return null;
		if (val instanceof NativeJavaArray) {
			NativeJavaArray ar = (NativeJavaArray) val;
			List ll = new ArrayList();
			for (int qi = 0; ar.has(qi, null); qi++) {
				ll.add(getJavaObject(ar.get(qi, null)));
			}

			return ll;
		}
		if (val instanceof NativeJavaObject) {
			return ((NativeJavaObject) val).unwrap();
		}

		if (val instanceof NativeObject) {
			return fromNativeObjectToMap((NativeObject) val);
		} else if (val instanceof NativeArray) {
			return fromNativeArrayToList((NativeArray) val);
		} else if (val instanceof Integer || val instanceof Double || val instanceof BigDecimal
				|| val instanceof Boolean || val instanceof Map || val instanceof List)
			return val;
		else
			return val.toString();
	}

	public static Map fromNativeObjectToMap(NativeObject o) throws JSONException {
		if (o == null)
			return null;
		Map no = new HashMap();
		for (Object id : o.getIds()) {
			String key = id.toString();
			no.put(key, getJavaObject(o.get(key, null)));

		}
		return no;
	}

	public static List fromNativeArrayToList(NativeArray o) throws JSONException {
		if (o == null)
			return null;
		List no = new ArrayList();
		for (int qi = 0; qi < o.getLength(); qi++) {
			no.add(getJavaObject(o.get(qi, null)));
		}
		return no;
	}
	

	public static Object rhinoValue(Object o) {
		if (o == null)
			return null;
		if (o instanceof NativeJavaObject) {
			o = ((NativeJavaObject) o).unwrap();
		} else if (o instanceof NativeObject) { // TODO
			// o = ((NativeObject) o).getAttributes(0);
		} else if (o.toString().length() == 0)
			return null;
		return o;
	}

	public static Object rhinoValue2(Object o) throws JSONException {
		if (o == null)
			return null;
		if (o instanceof NativeJavaObject) {
			o = ((NativeJavaObject) o).unwrap();
		} else if (o instanceof NativeArray) {
			return fromNativeArrayToList((NativeArray) o);
		} else if (o instanceof NativeObject) {
			return fromNativeObjectToMap((NativeObject) o);
		} else if (o.toString().length() == 0)
			return null;
		return o;
	}

	@SuppressWarnings("unchecked")
	public static String fromNativeArrayToJsonString2Recursive(NativeArray s) {
		if (s == null || s.getLength() == 0)
			return "[]";
		StringBuilder html = new StringBuilder();
		boolean b = false;
		html.append("[");
		for (int ji = 0; ji < s.getLength(); ji++) {
			Object o = s.get(ji, null);
			if (b)
				html.append("\n,");
			else
				b = true;
			if (o == null) {
				html.append("\"\"");
				continue;
			}
			if (o instanceof NativeJavaObject) {
				o = ((NativeJavaObject) o).unwrap();
			}
			if (o instanceof NativeObject)
				html.append(fromNativeObjectToJsonString2Recursive((NativeObject) o));
			else if (o instanceof NativeArray) {
				html.append(fromNativeArrayToJsonString2Recursive((NativeArray) o));
			} else if (o instanceof Object[]) {
				NativeArray lx = new NativeArray(((Object[]) o).length);
				for (int qi = 0; qi < ((Object[]) o).length; qi++)
					lx.put(qi, null, ((Object[]) o)[qi]);
				html.append(fromNativeArrayToJsonString2Recursive(lx));
			} else if (o instanceof Integer || o instanceof Double || o instanceof BigDecimal || o instanceof Boolean)
				html.append(o);
			else if (o instanceof Map)
				html.append(GenericUtil.fromMapToJsonString2Recursive((Map<String, Object>) o));
			else if (o instanceof List) {
				html.append(GenericUtil.fromListToJsonString2Recursive((List<Object>) o));
			} else
				html.append("\"").append(GenericUtil.stringToJS2(o.toString())).append("\"");
		}
		html.append("]");
		return html.toString();
	}

	@SuppressWarnings("unchecked")
	public static String fromNativeObjectToJsonString2Recursive(NativeObject s) {
		if (s == null || s.getIds().length == 0)
			return "{}";
		StringBuilder html = new StringBuilder();
		boolean b = false;
		html.append("{");
		for (Object q : s.getIds()) {
			if (b)
				html.append("\n,");
			else
				b = true;
			Object o = s.get(q.toString(), null);
			if (o == null) {
				html.append("\"").append(q).append("\":\"\"");
				continue;
			}
			if (o instanceof NativeJavaObject) {
				o = ((NativeJavaObject) o).unwrap();
			}
			if (o instanceof NativeObject)
				html.append("\"").append(q).append("\":")
						.append(fromNativeObjectToJsonString2Recursive((NativeObject) o));
			else if (o instanceof NativeArray) {
				html.append("\"").append(q).append("\":")
						.append(fromNativeArrayToJsonString2Recursive((NativeArray) o));
			} else if (o instanceof Object[]) {
				NativeArray lx = new NativeArray(((Object[]) o).length);
				for (int qi = 0; qi < ((Object[]) o).length; qi++)
					lx.put(qi, null, ((Object[]) o)[qi]);
				html.append("\"").append(q).append("\":").append(fromNativeArrayToJsonString2Recursive(lx));
			} else if (o instanceof Map)
				html.append("\"").append(q).append("\":")
						.append(GenericUtil.fromMapToJsonString2Recursive((Map<String, Object>) o));
			else if (o instanceof List) {
				html.append("\"").append(q).append("\":").append(GenericUtil.fromListToJsonString2Recursive((List<Object>) o));
			} else if (o instanceof Integer || o instanceof Double || o instanceof BigDecimal || o instanceof Boolean
					|| o instanceof Short || o instanceof Long || o instanceof Float)
				html.append("\"").append(q).append("\":").append(o);
			else
				html.append("\"").append(q).append("\":\"").append(GenericUtil.stringToJS2(o.toString())).append("\"");
		}
		html.append("}");
		return html.toString();
	}
	
	public static Object prepareParam(String code, W5Param param, Map<String, Object> scd, Map<String, String> requestParams, Map<String, String> errorMap){
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
			/*
			 * if(defaultValue.contains("$iwb.")){ ScriptEngine se = new
			 * ScriptEngine(scd, requestParams, dao, null); Object
			 * wrappedOut = Context.javaToJS( se, scope);
			 * ScriptableObject.putProperty(scope, "$iwb", wrappedOut); }
			 */

			StringBuilder sc = new StringBuilder();
			sc.append("\nvar _scd=").append(GenericUtil.fromMapToJsonString(scd));
			sc.append("\nvar _request=").append(GenericUtil.fromMapToJsonString(requestParams));
			sc.append("\n").append(code);

			// sc.append("'})';");
			// Now evaluate the string we've colected.
			cx.evaluateString(scope, sc.toString(), null, 1, null);

			if (scope.has("errorMsg", scope)) {
				Object em = scope.get("errorMsg", scope);
				if (em != null) {
					errorMap.put(param.getDsc(), LocaleMsgCache.get2(0, (String) scd.get("locale"), em.toString()));
				}
			}


			Object res = scope.get("result", scope);
			if (res != null && res instanceof NativeArray) { // donen sonuc array ise
				int len = (int) ((NativeArray) res).getLength();
				Object[] pvalues = new Object[len];
				for (int q7 = 0; q7 < pvalues.length; q7++) {
					pvalues[q7] = GenericUtil.getObjectByTip(
							requestParams.get(((NativeArray) res).get(q7, scope)),
							param.getParamTip());
				}
				return pvalues;
			}
			if (res != null && res instanceof Undefined)
				res = null;
			if (res != null && param.getParamTip() == 4)
				res = "" + new BigDecimal(res.toString()).intValue();
			return res == null ? null : res.toString();
		} catch (Exception e) {
			if (FrameworkSetting.debug)
				e.printStackTrace();
			errorMap.put(param.getDsc(), e.getMessage());
		} finally {
			// Exit from the context.
			cx.exit();
		}
		return null;
	}
}
