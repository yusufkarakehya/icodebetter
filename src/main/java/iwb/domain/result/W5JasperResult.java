package iwb.domain.result;

import java.util.Collection;
import java.util.Map;

import iwb.domain.db.W5Jasper;

public class W5JasperResult  implements W5MetaResult{

	private	int jasperId;
	private	W5Jasper jasper;
	private Map<String, Object> scd;
	private	Map<String, String>	errorMap;
	private Map<String,String> requestParams;
	private String file_name;

	private Map<String,Object> resultMap;
	private Collection resultDetail;
	
	public W5JasperResult(int jasperId) {
		super();
		this.jasperId = jasperId;
	}
	public int getJasperId() {
		return jasperId;
	}
	public void setJasperId(int jasperId) {
		this.jasperId = jasperId;
	}
	public W5Jasper getJasper() {
		return jasper;
	}
	public void setJasper(W5Jasper jasper) {
		this.jasper = jasper;
	}
	public Map<String, Object> getScd() {
		return scd;
	}
	public void setScd(Map<String, Object> scd) {
		this.scd = scd;
	}
	public Map<String, String> getErrorMap() {
		return errorMap;
	}
	public void setErrorMap(Map<String, String> errorMap) {
		this.errorMap = errorMap;
	}
	public Map<String, String> getRequestParams() {
		return requestParams;
	}
	public void setRequestParams(Map<String, String> requestParams) {
		this.requestParams = requestParams;
	}
	public Map<String, Object> getResultMap() {
		return resultMap;
	}
	public void setResultMap(Map<String, Object> resultMap) {
		this.resultMap = resultMap;
	}
	public Collection getResultDetail() {
		return resultDetail;
	}
	public void setResultDetail(Collection resultDetail) {
		this.resultDetail = resultDetail;
	}
	public String getFile_name() {
		return file_name;
	}
	public void setFile_name(String fileName) {
		file_name = fileName;
	}
	private boolean dev = false;
	
	public boolean isDev() {
		return dev;
	}
	public void setDev(boolean dev) {
		this.dev = dev;
	}
}
