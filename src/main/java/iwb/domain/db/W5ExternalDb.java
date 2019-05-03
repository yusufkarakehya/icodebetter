package iwb.domain.db;

import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Immutable;

// Generated Feb 5, 2007 3:58:07 PM by Hibernate Tools 3.2.0.b9

@Entity
@Immutable
@Table(name="w5_external_db",schema="iwb")
public class W5ExternalDb implements java.io.Serializable, W5Base{

	private int externalDbId;

	private short lkpDbType;// //oracle, postgre, mssql
	 
	private String dbUrl;

	private String dbUsername;

	private String dbPassword;

	private short activeFlag;// //oracle, postgre, mssql
	 

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
	@Column(name="external_db_id")
	public int getExternalDbId() {
		return externalDbId;
	}

	public void setExternalDbId(int externalDbId) {
		this.externalDbId = externalDbId;
	}

	@Column(name="lkp_db_type")
	public short getLkpDbType() {
		return lkpDbType;
	}

	public void setLkpDbType(short lkpDbType) {
		this.lkpDbType = lkpDbType;
	}

	@Column(name="db_url")
	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	@Column(name="db_username")
	public String getDbUsername() {
		return dbUsername;
	}

	public void setDbUsername(String dbUsername) {
		this.dbUsername = dbUsername;
	}

	@Column(name="db_password")
	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	@Column(name="active_flag")
	public short getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(short activeFlag) {
		this.activeFlag = activeFlag;
	}
	
	
	@Transient
	public boolean safeEquals(W5Base q){
		return false;
	}	
}
