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
@Table(name="w5_approval_step_sms_mail")
public class W5ApprovalStepSmsMail implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	private int approvalStepSmsMailId;
	private int approvalStepId;
	private int approvalId;
	private short smsMailTip;
	private short approvalActionTip;
	private String rolesToInform;
	private String usersToInform;
	private int customizationId; 
	
    @SequenceGenerator(name="sex_approval_step_sms_mail_id",sequenceName="seq_approval_step_sms_mail_id",allocationSize=1)
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="sex_approval_step_sms_mail_id")
	@Column(name="approval_step_sms_mail_id")
	public int getApprovalStepSmsMailId() {
		return approvalStepSmsMailId;
	}
	public void setApprovalStepSmsMailId(int approvalStepSmsMailId) {
		this.approvalStepSmsMailId = approvalStepSmsMailId;
	}
	
	@Column(name="approval_step_id")
	public int getApprovalStepId() {
		return approvalStepId;
	}
	public void setApprovalStepId(int approvalStepId) {
		this.approvalStepId = approvalStepId;
	}
	
	@Column(name="approval_id")
	public int getApprovalId() {
		return approvalId;
	}
	public void setApprovalId(int approvalId) {
		this.approvalId = approvalId;
	}
	
	@Column(name="sms_mail_tip")
	public short getSmsMailTip() {
		return smsMailTip;
	}
	public void setSmsMailTip(short smsMailTip) {
		this.smsMailTip = smsMailTip;
	}
	
	@Column(name="approval_action_tip")
	public short getApprovalActionTip() {
		return approvalActionTip;
	}
	public void setApprovalActionTip(short approvalActionTip) {
		this.approvalActionTip = approvalActionTip;
	}
	
	@Column(name="roles_to_inform")
	public String getRolesToInform() {
		return rolesToInform;
	}
	public void setRolesToInform(String rolesToInform) {
		this.rolesToInform = rolesToInform;
	}
	
	@Column(name="users_to_inform")
	public String getUsersToInform() {
		return usersToInform;
	}
	public void setUsersToInform(String usersToInform) {
		this.usersToInform = usersToInform;
	}
	@Id
	@Column(name="customization_id")
	public int getCustomizationId() {
		return customizationId;
	}
	public void setCustomizationId(int customizationId) {
		this.customizationId = customizationId;
	}
	
	@Transient
	public boolean equals(W5ApprovalStepSmsMail a){
		return false;
	}
}
