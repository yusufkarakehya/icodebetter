package iwb.domain.result;

import java.util.List;
import java.util.Map;

import iwb.domain.db.W5Template;
import iwb.domain.helper.W5TableRecordHelper;
import iwb.util.FrameworkCache;



public class W5TemplateResult implements W5MetaResult{
	
	private	int	templateId;
	private W5Template template;
	private	List<Object> templateObjectList;
	
	private int processTime;
	private Map<String, Object> scd;
	private Map<String,String>	requestParams;
	private List<W5TableRecordHelper> masterRecordList;

	public W5TemplateResult(int templateId) {
		this.templateId = templateId;
		if(FrameworkCache.wDevEntityKeys.contains("63."+templateId)){
			this.dev=true;
		}
	}
	public int getTemplateId() {
		return templateId;
	}
	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}
	public W5Template getTemplate() {
		return template;
	}
	public void setTemplate(W5Template template) {
		this.template = template;
	}
	public int getProcessTime() {
		return processTime;
	}
	public void setProcessTime(int processTime) {
		this.processTime = processTime;
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
	public void setRequestParams(Map<String, String> requestParams) {
		this.requestParams = requestParams;
	}
	public List<Object> getTemplateObjectList() {
		return templateObjectList;
	}
	public void setTemplateObjectList(List<Object> templateObjectList) {
		this.templateObjectList = templateObjectList;
	}
	public List<W5TableRecordHelper> getMasterRecordList() {
		return masterRecordList;
	}
	public void setMasterRecordList(List<W5TableRecordHelper> masterRecordList) {
		this.masterRecordList = masterRecordList;
	}
	private boolean dev = false;
	
	public boolean isDev() {
		return dev;
	}
	public void setDev(boolean dev) {
		this.dev = dev;
	}
}
