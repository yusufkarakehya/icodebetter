package iwb.domain.db;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="w5_object_mail_setting",schema="iwb")
public class W5ObjectMailSetting implements java.io.Serializable {

	private int mailSettingId;
	
	private int tableId;
	private int customizationId;
	private int tablePk;
	private String dsc;
	private short activeFlag;
	private short outboxRequestDeliveryFlag;
	private short outboxRequestReadFlag;
	private short notifyOnNewMailFlag;
	private String userName;
	private String passWord;
	private short inboxServerTip;
	private String inboxServer;
	private short inboxServerPort;
	private short inboxServerSslFlag;
	private short inboxLeaveCopyFlag;
	private short serverTimeout;
	private short lastErrorTip;
	private Timestamp lastErrorDttm;
	private short lastCheckedErrorTip;
	private Timestamp lastCheckedDttm;
	
	private String outboxServer;
	private short outboxServerPort;
	private short outboxServerSslFlag;
	private String outboxServerUserName;
	private String outboxServerPassWord;
	
	private String emailAddress;
//	private String emailSignature;
	private short outboxAuthTip;
	private short errorRetryCount;
	private short deleteMsgAfterClientFlag;
	private short deleteMsgAfterXDayFlag;
	private short deleteMsgAfterXDay;

	private short defaultFlag;
	private String smsNotificationFrom;
	
	/*  USER_NAME               VARCHAR2(64) not null,
  PASS_WORD               VARCHAR2(64) not null,
  INBOX_SERVER_TIP        NUMBER(4) not null,
  INBOX_SERVER            VARCHAR2(64),
  INBOX_SERVER_PORT       NUMBER(4) default 25 not null,
  INBOX_SERVER_SSL_FLAG   NUMBER(1) default 0 not null,
  OUTBOX_SERVER           VARCHAR2(64),
  OUTBOX_SERVER_PORT      NUMBER(4) default 110 not null,
  OUTBOX_SERVER_USER_NAME VARCHAR2(32),
  OUTBOX_SERVER_PASS_WORD VARCHAR2(32),
  OUTBOX_SERVER_SSL_FLAG  NUMBER(1) default 0 not null,
  SERVER_TIMEOUT          NUMBER(9) not null,
  VERSION_NO              NUMBER(9) default 1 not null,
  INSERT_USER_ID          NUMBER(9) default 5 not null,
  INSERT_DTTM             TIMESTAMP(6) default sysdate not null,
  VERSION_USER_ID         NUMBER(9) default 5 not null,
  VERSION_DTTM            TIMESTAMP(6) default sysdate not null,
  EMAIL_SIGNATURE         VARCHAR2(2048),
  EMAIL_ADDRESS           VARCHAR2(128) not null,
  OUTBOX_AUTH_TIP         NUMBER(4) default 1 not null,
  INBOX_LEAVE_COPY_FLAG   NUMBER(1) default 0 not null,
  LAST_CHECKED_DTTM       DATE,
  LAST_CHECK_ERROR_TIP    NUMBER(4) default 0 not null,
  LAST_CHECK_ERROR_DTTM   DATE
  */	
	@Id
	@Column(name="mail_setting_id")
	public int getMailSettingId() {
		return mailSettingId;
	}
	public void setMailSettingId(int mailSettingId) {
		this.mailSettingId = mailSettingId;
	}
	@Column(updatable=false,name="TABLE_PK")
	public int getTablePk() {
		return tablePk;
	}
	public void setTablePk(int tablePk) {
		this.tablePk = tablePk;
	}
	@Id
	@Column(updatable=false,name="CUSTOMIZATION_ID")
	public int getCustomizationId() {
		return customizationId;
	}
	public void setCustomizationId(int customizationId) {
		this.customizationId = customizationId;
	}
	@Column(updatable=false,name="DSC")
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
	@Column(updatable=false,name="TABLE_ID")
	public int getTableId() {
		return tableId;
	}
	public void setTableId(int tableId) {
		this.tableId = tableId;
	}
	@Column(updatable=false,name="USER_NAME")
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@Column(updatable=false,name="PASS_WORD")
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	@Column(updatable=false,name="INBOX_SERVER")
	public String getInboxServer() {
		return inboxServer;
	}
	public void setInboxServer(String inboxServer) {
		this.inboxServer = inboxServer;
	}
	@Column(updatable=false,name="INBOX_SERVER_PORT")
	public short getInboxServerPort() {
		return inboxServerPort;
	}
	public void setInboxServerPort(short inboxServerPort) {
		this.inboxServerPort = inboxServerPort;
	}
	@Column(updatable=false,name="INBOX_SERVER_SSL_FLAG")
	public short getInboxServerSslFlag() {
		return inboxServerSslFlag;
	}
	public void setInboxServerSslFlag(short inboxServerSslFlag) {
		this.inboxServerSslFlag = inboxServerSslFlag;
	}
	@Column(updatable=false,name="INBOX_LEAVE_COPY_FLAG")
	public short getInboxLeaveCopyFlag() {
		return inboxLeaveCopyFlag;
	}
	public void setInboxLeaveCopyFlag(short inboxLeaveCopyFlag) {
		this.inboxLeaveCopyFlag = inboxLeaveCopyFlag;
	}

	@Column(updatable=false,name="SERVER_TIMEOUT")
	public short getServerTimeout() {
		return serverTimeout;
	}
	public void setServerTimeout(short serverTimeout) {
		this.serverTimeout = serverTimeout;
	}
	@Column(updatable=false,name="INBOX_SERVER_TIP")
	public short getInboxServerTip() {
		return inboxServerTip;
	}
	public void setInboxServerTip(short inboxServerTip) {
		this.inboxServerTip = inboxServerTip;
	}
	@Column(name="last_error_tip")
	public short getLastErrorTip() {
		return lastErrorTip;
	}
	public void setLastErrorTip(short lastErrorTip) {
		this.lastErrorTip = lastErrorTip;
	}
	@Column(name="last_checked_error_tip")
	public short getLastCheckedErrorTip() {
		return lastCheckedErrorTip;
	}
	public void setLastCheckedErrorTip(short lastCheckedErrorTip) {
		this.lastCheckedErrorTip = lastCheckedErrorTip;
	}
	@Column(name="last_error_dttm")
	public Timestamp getLastErrorDttm() {
		return lastErrorDttm;
	}
	public void setLastErrorDttm(Timestamp lastErrorDttm) {
		this.lastErrorDttm = lastErrorDttm;
	}
	@Column(name="last_checked_dttm")
	public Timestamp getLastCheckedDttm() {
		return lastCheckedDttm;
	}
	public void setLastCheckedDttm(Timestamp lastCheckedDttm) {
		this.lastCheckedDttm = lastCheckedDttm;
	}
	
	@Column(updatable=false,name="OUTBOX_SERVER")
	public String getOutboxServer() {
		return outboxServer;
	}
	public void setOutboxServer(String outboxServer) {
		this.outboxServer = outboxServer;
	}
	@Column(updatable=false,name="OUTBOX_SERVER_PORT")
	public short getOutboxServerPort() {
		return outboxServerPort;
	}
	public void setOutboxServerPort(short outboxServerPort) {
		this.outboxServerPort = outboxServerPort;
	}
	@Column(updatable=false,name="OUTBOX_SERVER_SSL_FLAG")
	public short getOutboxServerSslFlag() {
		return outboxServerSslFlag;
	}
	public void setOutboxServerSslFlag(short outboxServerSslFlag) {
		this.outboxServerSslFlag = outboxServerSslFlag;
	}
	@Column(updatable=false,name="OUTBOX_SERVER_USER_NAME")
	public String getOutboxServerUserName() {
		return outboxServerUserName;
	}
	public void setOutboxServerUserName(String outboxServerUserName) {
		this.outboxServerUserName = outboxServerUserName;
	}
	@Column(updatable=false,name="OUTBOX_SERVER_PASS_WORD")
	public String getOutboxServerPassWord() {
		return outboxServerPassWord;
	}
	public void setOutboxServerPassWord(String outboxServerPassWord) {
		this.outboxServerPassWord = outboxServerPassWord;
	}
	@Column(updatable=false,name="EMAIL_ADDRESS")
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
/*	@Column(updatable=false,name="EMAIL_SIGNATURE")
	public String getEmailSignature() {
		return emailSignature;
	}
	public void setEmailSignature(String emailSignature) {
		this.emailSignature = emailSignature;
	}*/
	
	@Column(updatable=false,name="OUTBOX_AUTH_TIP")
	public short getOutboxAuthTip() {
		return outboxAuthTip;
	}
	public void setOutboxAuthTip(short outboxAuthTip) {
		this.outboxAuthTip = outboxAuthTip;
	}
	
	@Column(name="error_retry_count")
	public short getErrorRetryCount() {
		return errorRetryCount;
	}
	public void setErrorRetryCount(short errorRetryCount) {
		this.errorRetryCount = errorRetryCount;
	}
	@Column(updatable=false,name="DELETE_MSG_AFTER_CLIENT_FLAG")
	public short getDeleteMsgAfterClientFlag() {
		return deleteMsgAfterClientFlag;
	}
	public void setDeleteMsgAfterClientFlag(short deleteMsgAfterClientFlag) {
		this.deleteMsgAfterClientFlag = deleteMsgAfterClientFlag;
	}
	@Column(updatable=false,name="DELETE_MSG_AFTER_X_DAY_FLAG")
	public short getDeleteMsgAfterXDayFlag() {
		return deleteMsgAfterXDayFlag;
	}
	public void setDeleteMsgAfterXDayFlag(short deleteMsgAfterXDayFlag) {
		this.deleteMsgAfterXDayFlag = deleteMsgAfterXDayFlag;
	}
	@Column(updatable=false,name="DELETE_MSG_AFTER_X_DAY")
	public short getDeleteMsgAfterXDay() {
		return deleteMsgAfterXDay;
	}
	public void setDeleteMsgAfterXDay(short deleteMsgAfterXDay) {
		this.deleteMsgAfterXDay = deleteMsgAfterXDay;
	}
	@Column(updatable=false,name="NOTIFY_ON_NEW_MAIL_FLAG")
	public short getNotifyOnNewMailFlag() {
		return notifyOnNewMailFlag;
	}
	public void setNotifyOnNewMailFlag(short notifyOnNewMailFlag) {
		this.notifyOnNewMailFlag = notifyOnNewMailFlag;
	}
	@Column(name="default_flag")
	public short getDefaultFlag() {
		return defaultFlag;
	}
	public void setDefaultFlag(short defaultFlag) {
		this.defaultFlag = defaultFlag;
	}
	@Column(name="outbox_request_delivery_flag")
	public short getOutboxRequestDeliveryFlag() {
		return outboxRequestDeliveryFlag;
	}
	public void setOutboxRequestDeliveryFlag(short outboxRequestDeliveryFlag) {
		this.outboxRequestDeliveryFlag = outboxRequestDeliveryFlag;
	}	
	
	@Column(name="outbox_request_read_flag")
	public short getOutboxRequestReadFlag() {
		return outboxRequestReadFlag;
	}
	public void setOutboxRequestReadFlag(short outboxRequestReadFlag) {
		this.outboxRequestReadFlag = outboxRequestReadFlag;
	}
	
	
	@Column(name="sms_notification_from")
	public String getSmsNotificationFrom() {
		return smsNotificationFrom;
	}
	public void setSmsNotificationFrom(String smsNotificationFrom) {
		this.smsNotificationFrom = smsNotificationFrom;
	}	
	
}
