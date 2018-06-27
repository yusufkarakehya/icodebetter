package iwb.domain.db;

// Generated Feb 4, 2007 3:49:13 PM by Hibernate Tools 3.2.0.b9

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
@Entity
@Table(name="log5_check_mail")
public class Log5CheckMail implements java.io.Serializable {

	private int checkMailId;

	private int mailSettingId;

	private short errorTip;

	private int mailCount;
	
	private int customizationId;
	
	public Log5CheckMail() {}
	
    @SequenceGenerator(name="sex_check_mail",sequenceName="seq_check_mail",allocationSize=1)
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="sex_check_mail")
	@Column(name="check_mail_id")
	public int getCheckMailId() {
		return checkMailId;
	}

	public void setCheckMailId(int checkMailId) {
		this.checkMailId = checkMailId;
	}
	@Column(name="mail_setting_id")
	public int getMailSettingId() {
		return mailSettingId;
	}

	public void setMailSettingId(int mailSettingId) {
		this.mailSettingId = mailSettingId;
	}

	@Column(name="error_tip")
	public short getErrorTip() {
		return errorTip;
	}
	public void setErrorTip(short errorTip) {
		this.errorTip = errorTip;
	}

	public Log5CheckMail(int mailSettingId, short errorTip,int mailCount,int customizationId) {
		super();
		this.mailSettingId = mailSettingId;
		this.errorTip = errorTip;
		this.mailCount = mailCount;
		this.customizationId=customizationId;
	}
	@Column(name="mail_count")
	public int getMailCount() {
		return mailCount;
	}

	public void setMailCount(int mailCount) {
		this.mailCount = mailCount;
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
