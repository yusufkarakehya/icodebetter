package iwb.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class InfluxUtil {
	public static List query(String url, String dbName, String influxQL){
		String s = HttpUtil.send( url +"/query?db="+dbName,"q="+influxQL,"GET", null);
		if(GenericUtil.isEmpty(s))return null;
		JSONObject jo = new JSONObject(s);
		if(jo.has("results")) {
			JSONArray jr = jo.getJSONArray("results");
			List<Map> r = new ArrayList<Map>();
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
}
