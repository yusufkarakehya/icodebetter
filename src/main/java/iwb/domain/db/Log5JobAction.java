package iwb.domain.db;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="log5_job_action",schema="iwb")
public class Log5JobAction implements Serializable, Log5Base{
	
	private int logId;
	private int jobScheduleId;
	private String projectUuid;
	private String error;
	private long execTime;
	
	
	public String toInfluxDB() {
		StringBuilder s=new StringBuilder();
		return s.toString();
	}
	
	public Log5JobAction() {
	}
	

	public Log5JobAction(int jobScheduleId, String projectUuid) {
		super();
		this.jobScheduleId = jobScheduleId;
		this.projectUuid = projectUuid;
		this.execTime = System.currentTimeMillis();
	}

	@SequenceGenerator(name="sex_log5_job_action",sequenceName="iwb.seq_log5_job_action",allocationSize=1)
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="sex_log5_job_action")
	@Column(name="log_id")  
	public int getLogId() {
		return logId;
	}
	public void setLogId(int logId) {
		this.logId = logId;
	}

	@Column(name="job_schedule_id")  
	public int getJobScheduleId() {
		return jobScheduleId;
	}

	public void setJobScheduleId(int jobScheduleId) {
		this.jobScheduleId = jobScheduleId;
	}

	@Column(name="project_uuid")  
	public String getProjectUuid() {
		return projectUuid;
	}

	public void setProjectUuid(String projectUuid) {
		this.projectUuid = projectUuid;
	}

	@Column(name="error")  
	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Column(name="exec_time")  
	public long getExecTime() {
		return execTime;
	}

	public void setExecTime(long execTime) {
		this.execTime = execTime;
	}
	
	public void calcExecTime() {
		this.execTime = System.currentTimeMillis() - this.execTime;		
	}
	
}
