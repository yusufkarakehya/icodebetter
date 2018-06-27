package iwb.domain.helper;

import java.util.Map;


public class W5QueuedDbFuncHelper implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4172465145521537411L;
	private Map<String, Object> scd;
	private int dbFuncId;
	private Map<String, String> parameterMap;
	private short execRestrictTip;
	
	public Map<String, Object> getScd() {
		return scd;
	}
	public void setScd(Map<String, Object> scd) {
		this.scd = scd;
	}
	public int getDbFuncId() {
		return dbFuncId;
	}
	public void setDbFuncId(int dbFuncId) {
		this.dbFuncId = dbFuncId;
	}
	public Map<String, String> getParameterMap() {
		return parameterMap;
	}
	public void setParameterMap(Map<String, String> parameterMap) {
		this.parameterMap = parameterMap;
	}
	public short getExecRestrictTip() {
		return execRestrictTip;
	}
	public void setExecRestrictTip(short execRestrictTip) {
		this.execRestrictTip = execRestrictTip;
	}

	public W5QueuedDbFuncHelper(Map<String, Object> scd, int dbFuncId,
			Map<String, String> parameterMap, short execRestrictTip) {
		this.scd = scd;
		this.dbFuncId = dbFuncId;
		this.parameterMap = parameterMap;
		this.execRestrictTip = execRestrictTip;
	}

}
