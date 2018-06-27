package iwb.domain.result;

import java.util.List;
import java.util.Map;

import iwb.domain.db.W5DbFunc;
import iwb.domain.db.W5Table;
import iwb.domain.db.W5TempLogRecord;
import iwb.domain.helper.W5QueuedDbFuncHelper;
import iwb.domain.helper.W5ReportCellHelper;



public class W5DbFuncResult implements W5MetaResult{
	
	private	int	dbFuncId;
	private W5DbFunc dbFunc;
	
	private String executedSql;
	private	List<Object> sqlParams;
	private	W5Table approvalTable;
	private Map<String, Object> scd;
	private Map<String,String>	requestParams;
	private Map<String,String>	resultMap;
	private Map<String,String>	errorMap;
	private	boolean success;
	private List<W5ReportCellHelper> reportList;
	private List<W5QueuedDbFuncHelper> queuedDbFuncList;
	private List<W5TempLogRecord> logRecordList;
	private int processTime;
	
	

	public W5DbFuncResult(int dbFuncId) {
		this.dbFuncId = dbFuncId;
	}
	public int getDbFuncId() {
		return dbFuncId;
	}
	public void setDbFuncId(int dbFuncId) {
		this.dbFuncId = dbFuncId;
	}
	public W5DbFunc getDbFunc() {
		return dbFunc;
	}
	public void setDbFunc(W5DbFunc dbFunc) {
		this.dbFunc = dbFunc;
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
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public Map<String, String> getResultMap() {
		return resultMap;
	}
	public void setResultMap(Map<String, String> resultMap) {
		this.resultMap = resultMap;
	}
	public Map<String, String> getErrorMap() {
		return errorMap;
	}
	public void setErrorMap(Map<String, String> errorMap) {
		this.errorMap = errorMap;
	}
	public String getExecutedSql() {
		return executedSql;
	}
	public void setExecutedSql(String executedSql) {
		this.executedSql = executedSql;
	}
	public List<Object> getSqlParams() {
		return sqlParams;
	}
	public void setSqlParams(List<Object> sqlParams) {
		this.sqlParams = sqlParams;
	}
	public List<W5ReportCellHelper> getReportList() {
		return reportList;
	}
	public void setReportList(List<W5ReportCellHelper> reportList) {
		this.reportList = reportList;
	}
	public W5Table getApprovalTable() {
		return approvalTable;
	}
	public void setApprovalTable(W5Table approvalTable) {
		this.approvalTable = approvalTable;
	}

	public List<W5QueuedDbFuncHelper> getQueuedDbFuncList() {
		return queuedDbFuncList;
	}
	public void setQueuedDbFuncList(List<W5QueuedDbFuncHelper> queuedDbFuncList) {
		this.queuedDbFuncList = queuedDbFuncList;
	}

	public List<W5TempLogRecord> getLogRecordList() {
		return logRecordList;
	}
	public void setLogRecordList(List<W5TempLogRecord> logRecordList) {
		this.logRecordList = logRecordList;
	}
	public int getProcessTime() {
		return processTime;
	}
	public void setProcessTime(int processTime) {
		this.processTime = processTime;
	}
	private boolean dev = false;
	
	public boolean isDev() {
		return dev;
	}
	public void setDev(boolean dev) {
		this.dev = dev;
	}
}
