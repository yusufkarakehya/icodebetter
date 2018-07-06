package iwb.domain.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="log5_exception")
public class Log5Exception implements Serializable{
	
	private int logId;
	private String exceptionText;
	private int userRoleId;
	private int customizationId;  
	
	@SequenceGenerator(name="sex_log5_exception",sequenceName="iwb.seq_log5_exception",allocationSize=1)
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="sex_log5_exception")
	@Column(name="log_id")  
	public int getLogId() {
		return logId;
	}
	public void setLogId(int logId) {
		this.logId = logId;
	}
	
	@Column(name="exception_text") 
	public String getExceptionText() {
		return exceptionText;
	}	
	public void setExceptionText(String exceptionText) {
		this.exceptionText = exceptionText;
	}
	
	@Column(name="user_role_id")
	public int getUserRoleId() {
		return userRoleId;
	}
	public void setUserRoleId(int userRoleId) {
		this.userRoleId = userRoleId;
	}	
	@Id
	@Column(name="customization_id")
	public int getCustomizationId() {
		return customizationId;
	}
	public void setCustomizationId(int customizationId) {
		this.customizationId = customizationId;
	}
	
}
