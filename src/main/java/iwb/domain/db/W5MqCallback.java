package iwb.domain.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Immutable;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

// Generated Feb 5, 2007 3:58:07 PM by Hibernate Tools 3.2.0.b9

@Entity
@Immutable
@Table(name="w5_mq_callback",schema="iwb")
public class W5MqCallback implements java.io.Serializable, W5Base{

	private int mqCallbackId;

	private int mqId;

	private String topic;

	private int funcId;
	 
	private short activeFlag;
	 

	private String projectUuid;
	
	
	@Id
	@Column(name="project_uuid")
	public String getProjectUuid() {
		return projectUuid;
	}

	public void setProjectUuid(String projectUuid) {
		this.projectUuid = projectUuid;
	}

	@Id
	@Column(name="mq_callback_id")
	public int getMqCallbackId() {
		return mqCallbackId;
	}

	public void setMqCallbackId(int mqCallbackId) {
		this.mqCallbackId = mqCallbackId;
	}

	
	@Transient
	public boolean safeEquals(W5Base q){
		return false;
	}

	@Column(name="mq_id")
	public int getMqId() {
		return mqId;
	}

	public void setMqId(int mqId) {
		this.mqId = mqId;
	}


	@Column(name="active_flag")
	public short getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(short activeFlag) {
		this.activeFlag = activeFlag;
	}


	@Column(name="topic")
	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	@Column(name="func_id")
	public int getFuncId() {
		return funcId;
	}

	public void setFuncId(int funcId) {
		this.funcId = funcId;
	}
	
	
	
}
