package iwb.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.graalvm.polyglot.Value;

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
		return rp;
	}

	public static Map fromGraalValue2Map(Object reqP) {
		Map<String, Object> rp = new HashMap<String, Object>();
		if(reqP==null)return rp;
		if(reqP instanceof Value) {
			Value jsRequestParams = (Value)reqP;
			if(jsRequestParams.hasArrayElements()) {
				rp.put("rhino", fromGraalValue2List(jsRequestParams));
			} else for (String key : jsRequestParams.getMemberKeys()) {
				Value o = jsRequestParams.getMember(key);
				if (o != null) {
					if(o.hasArrayElements()) {
						rp.put(key, fromGraalValue2List(o));
					} else if(o.hasMembers()) {
						rp.put(key, fromGraalValue2Map(o));
					} else {
						String res = o.toString();
						if (res.endsWith(".0") && GenericUtil.uInt(res.substring(0, res.length() - 2)) > 0)
							res = res.substring(0, res.length() - 2);
						rp.put(key, res);
					}
				}
			}
		} else if(reqP instanceof Map) {
			Map jsRequestParams = (Map)reqP;
			for (Object key : jsRequestParams.keySet()) {
				Object o = jsRequestParams.get(key.toString());
				if (o != null) {
					if(o  instanceof List) {
						rp.put(key.toString(), o); //fromGraalValue2List(o)
					} else if(o  instanceof Map) {
						rp.put(key.toString(), o); //fromGraalValue2Map(o)
					} else {
						String res = o.toString();
						if (res.endsWith(".0") && GenericUtil.uInt(res.substring(0, res.length() - 2)) > 0)
							res = res.substring(0, res.length() - 2);
						rp.put(key.toString(), res);
					}
				}
			}
		}
		return rp;
	}

	public static List fromGraalValue2List(Value o) {
		List<Object> ll = new ArrayList();
		if(o==null)return ll;
		if(o.hasArrayElements())for(int qi=0;qi<o.getArraySize();qi++) {
			Value oo = o.getArrayElement(qi);
			if(oo==null) {
				ll.add(oo);
			} else if(oo.hasArrayElements()){
				ll.add(fromGraalValue2List(oo));
			} else if(oo.hasMembers())  {
				ll.add(fromGraalValue2Map(oo));
			} else {
				String res = oo.toString();
				if (res.endsWith(".0") && GenericUtil.uInt(res.substring(0, res.length() - 2)) > 0)
					res = res.substring(0, res.length() - 2);
				ll.add(res);
			}
			
		}
		return ll;
	}

}
