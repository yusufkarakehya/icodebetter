package iwb.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class ScriptUtil {

	public static List fromScriptObject2List(Object reqL) {
		List ll = new ArrayList();
		if(reqL==null)return ll;
		ScriptObjectMirror jsRequestParams = (ScriptObjectMirror)reqL;
		for (Object oo : jsRequestParams.values()) {
			if (oo == null)
				ll.add(null);
			else if (oo instanceof ScriptObjectMirror)
				if (((ScriptObjectMirror) oo).isArray()) {
					ll.add(fromScriptObject2List((ScriptObjectMirror) oo));
				} else
					ll.add(fromScriptObject2Map((ScriptObjectMirror) oo));
			else
				ll.add(oo);
		}
		return ll;
	}

	public static Map fromScriptObject2Map(Object reqP) {
		Map<String, Object> rp = new HashMap<String, Object>();
		if(reqP==null)return rp;
		ScriptObjectMirror jsRequestParams = (ScriptObjectMirror)reqP;
		if (jsRequestParams != null) {
			if (jsRequestParams.isArray()) {
				rp.put("rhino", fromScriptObject2List(jsRequestParams));
				return rp;

			}

			for (String key : jsRequestParams.keySet()) {
				Object o = jsRequestParams.get(key);
				if (o != null) {
					String res = o.toString();
					if (res.endsWith(".0") && GenericUtil.uInt(res.substring(0, res.length() - 2)) > 0)
						res = res.substring(0, res.length() - 2);
					rp.put(key, res);
				}
			}
		}
		return rp;
	}

}
