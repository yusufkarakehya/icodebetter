package iwb.domain.result;

import java.util.Map;

import iwb.cache.FrameworkCache;
import iwb.domain.db.W5DataView;



public class W5DataViewResult implements W5MetaResult{
	
	private	int	dataViewId;
    private int	action;
    private Map<String, Object> scd;    
	private Map<String,String>	requestParams;	
	private	W5DataView dataView;
	private	W5FormResult searchFormResult;
	
	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}


	
	public Map<String, Object> getScd() {
		return scd;
	}

	public void setScd(Map<String, Object> scd) {
		this.scd = scd;
	}




    public	String toJsonGridDetailView(){
    	StringBuilder html = new StringBuilder();
//    	html.append("new Ext.Template('").append(PromisUtil.stringToJS(detailTemplate.getCode())).append("')");
    	
    	return html.toString();
    }

	public Map<String, String> getRequestParams() {
		return requestParams;
	}

	public void setRequestParams(Map<String, String> requestParams) {
		this.requestParams = requestParams;
	}

	public W5FormResult getSearchFormResult() {
		return searchFormResult;
	}

	public void setSearchFormResult(W5FormResult searchFormResult) {
		this.searchFormResult = searchFormResult;
	}

	public int getDataViewId() {
		return dataViewId;
	}

	public void setDataViewId(int dataViewId) {
		this.dataViewId = dataViewId;
	}

	public W5DataView getDataView() {
		return dataView;
	}

	public void setDataView(W5DataView dataView) {
		this.dataView = dataView;
	}

	public W5DataViewResult(int dataViewId) {
		super();
		this.dataViewId = dataViewId;
		if(FrameworkCache.wDevEntityKeys.contains("930."+dataViewId)){
			this.dev=true;
		}
	}

	private boolean dev = false;
	
	public boolean isDev() {
		return dev;
	}
	public void setDev(boolean dev) {
		this.dev = dev;
	}

}
