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
@Table(name="w5_backup_setting")
public class W5BackupSetting implements Serializable{

	private int backupSettingId;
	private String dsc;
	private String filePrefix;
	private short intervalDay;
	private short overwriteFlag;
	private String folder;
	private String expdpCmd;
	private String impdpCmd;
	
	@SequenceGenerator(name="sex_backup_setting",sequenceName="seq_w5_backup_setting_id",allocationSize=1)
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="sex_backup_setting")
	
	@Column(name="backup_setting_id") 
	public int getBackupSettingId() {
		return backupSettingId;
	}
	public void setBackupSettingId(int backupSettingId) {
		this.backupSettingId = backupSettingId;
	}
	@Column(name="dsc") 
	public String getDsc() {
		return dsc;
	}
	public void setDsc(String dsc) {
		this.dsc = dsc;
	}
	@Column(name="file_prefix") 
	public String getFilePrefix() {
		return filePrefix;
	}
	public void setFilePrefix(String filePrefix) {
		this.filePrefix = filePrefix;
	}
	@Column(name="interval_day") 
	public short getIntervalDay() {
		return intervalDay;
	}
	public void setIntervalDay(short intervalDay) {
		this.intervalDay = intervalDay;
	}
	@Column(name="overwrite_flag") 
	public short getIncrementalFlag() {
		return overwriteFlag;
	}
	public void setIncrementalFlag(short overwriteFlag) {
		this.overwriteFlag = overwriteFlag;
	}
	@Column(name="folder") 
	public String getFolder() {
		return folder;
	}
	public void setFolder(String folder) {
		this.folder = folder;
	}
	@Column(name="expdp_cmd") 
	public String getExpdpCmd() {
		return expdpCmd;
	}
	public void setExpdpCmd(String expdpCmd) {
		this.expdpCmd = expdpCmd;
	}
	@Column(name="impdp_cmd") 
	public String getImpdpCmd() {
		return impdpCmd;
	}
	public void setImpdpCmd(String impdpCmd) {
		this.impdpCmd = impdpCmd;
	}	
}
