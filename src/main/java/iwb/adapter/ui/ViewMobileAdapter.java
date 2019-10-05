package iwb.adapter.ui;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import iwb.domain.db.W5BIGraphDashboard;
import iwb.domain.result.M5ListResult;
import iwb.domain.result.W5FormResult;
import iwb.domain.result.W5PageResult;

public interface ViewMobileAdapter {
//	public	StringBuilder serializeQueryData(W5QueryResult queryResult);
	public	StringBuilder	serializeList(M5ListResult	listResult);
	public	StringBuilder	serializePage(W5PageResult	pageResult);
	public	StringBuilder serializeGetForm(W5FormResult formResult);
	public	StringBuilder serializeFormFromJSON(JSONObject formBuilder) throws JSONException;
	public StringBuilder serializeGraphDashboard(W5BIGraphDashboard gd, Map<String, Object> scd);
//	public StringBuilder serializeException(Map<String, Object> scd, PromisException ex);
}
