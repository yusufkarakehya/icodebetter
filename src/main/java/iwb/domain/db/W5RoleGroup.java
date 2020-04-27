package iwb.domain.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Immutable;


@Entity
@Immutable
@Table(name="w5_user_tip", schema="iwb")
public class W5RoleGroup implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 333319124324231L;
	private int userTip;	
	private short webFrontendTip;	
	private short activeFlag;	
	private int defaultMainTemplateId;
	
	private String dsc;

	
	
	private String projectUuid;

	@Id
	@Column(name="project_uuid")
	public String getProjectUuid() {
		return projectUuid;
	}

	
	@Id
	@Column(name="user_tip")
	public int getUserTip() {
		return userTip;
	}

	public void setUserTip(int userTip) {
		this.userTip = userTip;
	}

	@Column(name="web_frontend_tip")
	public short getWebFrontendTip() {
		return webFrontendTip;
	}

	public void setWebFrontendTip(short webFrontendTip) {
		this.webFrontendTip = webFrontendTip;
	}

	@Column(name="default_main_template_id")
	public int getDefaultMainTemplateId() {
		return defaultMainTemplateId;
	}

	public void setDefaultMainTemplateId(int defaultMainTemplateId) {
		this.defaultMainTemplateId = defaultMainTemplateId;
	}

	@Column(name="dsc")
	public String getDsc() {
		return dsc;
	}

	public void setDsc(String dsc) {
		this.dsc = dsc;
	}

	@Column(name="active_flag")
	public short getActiveFlag() {
		return activeFlag;
	}


	public void setActiveFlag(short activeFlag) {
		this.activeFlag = activeFlag;
	}


	public void setProjectUuid(String projectUuid) {
		this.projectUuid = projectUuid;
	}

	public boolean equals(Object o) {
		if(o==null || !(o instanceof W5RoleGroup))return false;
		W5RoleGroup c = (W5RoleGroup)o;
		return c!=null && c.getUserTip()==getUserTip() && c.getProjectUuid().equals(projectUuid);
	}
	
	public int hashCode() {
		return projectUuid.hashCode() + 100*getUserTip();
	}

	public boolean safeEquals(W5Base q) {
		return false;
	}
	

}
