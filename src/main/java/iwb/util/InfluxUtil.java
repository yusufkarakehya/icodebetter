package iwb.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import iwb.exception.IWBException;

public class InfluxUtil {
	public static List query(String url, String dbName, String influxQL){
		if(!url.endsWith("/"))url +="/";
		url +="query";
		boolean post = GenericUtil.isEmpty(dbName);
		if(!post && !dbName.equals("."))url +="?db="+dbName;
		String s = HttpUtil.send( url,"q="+influxQL,post ? "POST":"GET", null);
		if(GenericUtil.isEmpty(s))return null;
		JSONObject jo = new JSONObject(s);
		if(jo.has("error"))
			throw new IWBException("sql", "Influx.Query", 0, influxQL, jo.getString("error"), null);
		if(post) {
			return null;
		}
		if(jo.has("results")) {
			JSONArray jr = jo.getJSONArray("results");
			List<Map> r = new ArrayList<Map>();
			if(jr.length()>0 && jr.getJSONObject(0).has("error"))
				throw new IWBException("sql", "Influx.Query", 0, influxQL, jr.getJSONObject(0).getString("error"), null);
			
			for(int qi=0;qi<jr.length();qi++) try{
				JSONObject js = jr.getJSONObject(qi);
				if(js.has("series")) {
					JSONArray columns = js.getJSONArray("series").getJSONObject(0).getJSONArray("columns");
					JSONArray values = js.getJSONArray("series").getJSONObject(0).getJSONArray("values");
					for(int ji=0;ji<values.length();ji++) {
						JSONArray row = values.getJSONArray(ji);
						Map mrow = new HashMap();
						for(int vi=0;vi<row.length();vi++) {
							mrow.put(columns.getString(vi), row.get(vi));
						}
						r.add(mrow);
					}
				}
			} catch(Exception ee) {
				ee.printStackTrace();
			}
			return r;
			
		}
		return null;
	}
	public static String write(String url, String dbName, String influxQL){
		Map m = new HashMap();
		m.put("Content-Type", "application/json");
		if(!url.endsWith("/"))url +="/";
		return HttpUtil.send(url+"write?db="+dbName,influxQL,"POST", m);
	}

}
