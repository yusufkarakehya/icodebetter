package iwb.domain.db;

// Generated Feb 4, 2007 3:49:13 PM by Hibernate Tools 3.2.0.b9

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import iwb.domain.result.W5QueryResult;
import iwb.util.GenericUtil;

@Entity
@Table(name="log5_query_action",schema="iwb")
public class Log5QueryAction implements java.io.Serializable, Log5Base {

	private int logId;

	private int userId;
	
	private int queryId;
	private int customizationId;

	private String dsc;


	private int processTime;
	private long startTime;

	public String toInfluxDB() {
		StringBuilder s=new StringBuilder();
		switch(getQueryId()){
		case -999:s.append("sql_query duration=").append(getProcessTime()).append("i,sql=\"").append(GenericUtil.stringToJS2(getDsc())).append("\"");
			break;
		case -998:s.append("sql_execute duration=").append(getProcessTime()).append("i,sql=\"").append(GenericUtil.stringToJS2(getDsc())).append("\"");
			break;
		default:
			s.append("query,query_id=").append(getQueryId()).append(" user_id=").append(getUserId()).append("i,duration=").append(getProcessTime()).append("i,sql=\"").append(GenericUtil.stringToJS2(getDsc())).append("\"");

		}
		return s.toString();
	}

	
	
	@Column(name="dsc")
	public String getDsc() {
		return dsc;
	}

	public void setDsc(String dsc) {
		this.dsc = dsc;
	}
	
	public Log5QueryAction() {
	}

	public Log5QueryAction(W5QueryResult queryResult) {
		this.queryId = queryResult.getQueryId();
		this.userId = (Integer)queryResult.getScd().get("userId");
		this.startTime=System.currentTimeMillis();
		this.processTime = -1;
		if(queryResult.getScd().get("customizationId")!=null)
			this.customizationId = (Integer)queryResult.getScd().get("customizationId");
	}
    @SequenceGenerator(name="sex_log_query_action",sequenceName="iwb.seq_log_query_action",allocationSize=1)
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="sex_log_query_action")
	@Column(name="log_id")
	public int getLogId() {
		return this.logId;
	}


	@Column(name="query_id")
	public int getQueryId() {
		return queryId;
	}

	public void setQueryId(int queryId) {
		this.queryId = queryId;
	}

	public void setLogId(int logId) {
		this.logId = logId;
	}

	@Column(name="user_id")
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	@Column(name="process_time")
	public int getProcessTime() {
		return this.processTime;
	}

	public void setProcessTime(int processTime) {
		this.processTime = processTime;
	}

	@Column(name="customization_id")
	public int getCustomizationId() {
		return customizationId;
	}

	public void setCustomizationId(int customizationId) {
		this.customizationId = customizationId;
	}

	public void calcProcessTime() {
		this.processTime = (int)(System.currentTimeMillis() - this.startTime);
	}

	@Transient
	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

}
