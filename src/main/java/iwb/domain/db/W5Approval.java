package iwb.domain.db;

import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Immutable;

import iwb.util.GenericUtil;


@Entity
@Immutable
@Table(name="w5_approval", schema="iwb")
public class W5Approval implements java.io.Serializable {

	private int approvalId;
	
	private int customizationId;

	private String dsc;

	private short activeFlag;
	private int tableId;
	private int approvalTableFieldId;
	private short actionTip;
	private short approvalStrategyTip;
	private short approvalFlowTip;
	private short onRejectTip;
	private String advancedBeginSql;
	private short approvalRequestTip;
	private short eSignFlag;
	private int eSignReportId;
	private String manualAppRoleIds; 
	private String manualAppUserIds; 
	private String manualAppTableFieldIds;
	private String dynamicStepUserIds;
	private int dynamicStepMinUserCount;
	private int dynamicStepMaxUserCount;
	private String afterFinUpdateUserIds;	
	private short manualDemandStartAppFlag;
	private short sendMailOnManualStepFlag;
	private short sendSmsOnManualStepFlag;
	private int onApproveDbFuncId;
	private String approvalRequestMsg;		
	private String hierarchicalAppMsg;		
	private String dynamicAppMsg;		
	private String approvedMsg;		
	private String rejectedMsg;	
	private int hierarchicalLevelLimit;
	private short sendMailFlag;
	private short sendSmsFlag;
			

	private List<W5ApprovalStep> _approvalStepList;
	private Map<Integer,W5ApprovalStep> _approvalStepMap;
	
	@Id
	@Column(name="approval_id")
	public int getApprovalId() {
		return approvalId;
	}
	public void setApprovalId(int approvalId) {
		this.approvalId = approvalId;
	}
	@Id
	@Column(name="customization_id")
	public int getCustomizationId() {
		return customizationId;
	}
	public void setCustomizationId(int customizationId) {
		this.customizationId = customizationId;
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
	@Column(name="table_id")
	public int getTableId() {
		return tableId;
	}
	public void setTableId(int tableId) {
		this.tableId = tableId;
	}
	@Column(name="action_tip")
	public short getActionTip() {
		return actionTip;
	}
	public void setActionTip(short actionTip) {
		this.actionTip = actionTip;
	}
	@Transient
	public List<W5ApprovalStep> get_approvalStepList() {
		return _approvalStepList;
	}
	public void set_approvalStepList(List<W5ApprovalStep> approvalStepList) {
		_approvalStepList = approvalStepList;
	}
	
	@Column(name="approval_strategy_tip")
	public short getApprovalStrategyTip() {
		return approvalStrategyTip;
	}
	public void setApprovalStrategyTip(short approvalStrategyTip) {
		this.approvalStrategyTip = approvalStrategyTip;
	}
	@Column(name="approval_flow_tip")
	public short getApprovalFlowTip() {
		return approvalFlowTip;
	}
	public void setApprovalFlowTip(short approvalFlowTip) {
		this.approvalFlowTip = approvalFlowTip;
	}
	@Column(name="on_reject_action_tip")
	public short getOnRejectTip() {
		return onRejectTip;
	}
	public void setOnRejectTip(short onRejectTip) {
		this.onRejectTip = onRejectTip;
	}
	@Column(name="advanced_begin_sql")
	public String getAdvancedBeginSql() {
		return advancedBeginSql;
	}
	public void setAdvancedBeginSql(String advancedBeginSql) {
		this.advancedBeginSql = advancedBeginSql;
	}
	@Transient
	public Map<Integer, W5ApprovalStep> get_approvalStepMap() {
		return _approvalStepMap;
	}
	public void set_approvalStepMap(Map<Integer, W5ApprovalStep> approvalStepMap) {
		_approvalStepMap = approvalStepMap;
	}
	
	@Column(name="approval_request_tip")
	public short getApprovalRequestTip() {
		return approvalRequestTip;
	}
	public void setApprovalRequestTip(short approvalRequestTip) {
		this.approvalRequestTip = approvalRequestTip;
	}
	@Column(name="e_sign_flag")
	public short geteSignFlag() {
		return eSignFlag;
	}
	public void seteSignFlag(short eSignFlag) {
		this.eSignFlag = eSignFlag;
	}
	
	@Column(name="e_sign_report_id")
	public int geteSignReportId() {
		return eSignReportId;
	}
	public void seteSignReportId(int eSignReportId) {
		this.eSignReportId = eSignReportId;
	}

	/*private String ; 
	private String manual_app_user_ids; 
	private String manual_app_table_field_ids;
	private short dynamic_step_flag;
	private String dynamic_step_user_ids;
	private int dynamic_step_min_user_count;
	private int dynamic_step_max_user_count;*/

	@Column(name="manual_app_role_ids")
	public String getManualAppRoleIds() {
		return manualAppRoleIds;
	}
	public void setManualAppRoleIds(String manualAppRoleIds) {
		this.manualAppRoleIds = manualAppRoleIds;
	}
	
	@Column(name="manual_app_user_ids")
	public String getManualAppUserIds() {
		return manualAppUserIds;
	}
	public void setManualAppUserIds(String manualAppUserIds) {
		this.manualAppUserIds = manualAppUserIds;
	}
	
	@Column(name="manual_app_table_field_ids")
	public String getManualAppTableFieldIds() {
		return manualAppTableFieldIds;
	}
	public void setManualAppTableFieldIds(String manualAppTableFieldIds) {
		this.manualAppTableFieldIds = manualAppTableFieldIds;
	}
	
	@Column(name="dynamic_step_user_ids")
	public String getDynamicStepUserIds() {
		return dynamicStepUserIds;
	}
	public void setDynamicStepUserIds(String dynamicStepUserIds) {
		this.dynamicStepUserIds = dynamicStepUserIds;
	}
	
	@Column(name="dynamic_step_min_user_count")
	public int getDynamicStepMinUserCount() {
		return dynamicStepMinUserCount;
	}
	public void setDynamicStepMinUserCount(int dynamicStepMinUserCount) {
		this.dynamicStepMinUserCount = dynamicStepMinUserCount;
	}
	
	@Column(name="dynamic_step_max_user_count")
	public int getDynamicStepMaxUserCount() {
		return dynamicStepMaxUserCount;
	}
	public void setDynamicStepMaxUserCount(int dynamicStepMaxUserCount) {
		this.dynamicStepMaxUserCount = dynamicStepMaxUserCount;
	}
	
	@Column(name="after_fin_update_user_ids")
	public String getAfterFinUpdateUserIds() {
		return afterFinUpdateUserIds;
	}
	public void setAfterFinUpdateUserIds(String afterFinUpdateUserIds) {
		this.afterFinUpdateUserIds = afterFinUpdateUserIds;
	}
	
	@Column(name="manual_demand_start_app_flag")
	public short getManualDemandStartAppFlag() {
		return manualDemandStartAppFlag;
	}
	public void setManualDemandStartAppFlag(short manualDemandStartAppFlag) {
		this.manualDemandStartAppFlag = manualDemandStartAppFlag;
	}
	
	@Column(name="send_mail_on_manual_step_flag")
	public short getSendMailOnManualStepFlag() {
		return sendMailOnManualStepFlag;
	}
	public void setSendMailOnManualStepFlag(short sendMailOnManualStepFlag) {
		this.sendMailOnManualStepFlag = sendMailOnManualStepFlag;
	}
	
	@Column(name="send_sms_on_manual_step_flag")
	public short getSendSmsOnManualStepFlag() {
		return sendSmsOnManualStepFlag;
	}
	public void setSendSmsOnManualStepFlag(short sendSmsOnManualStepFlag) {
		this.sendSmsOnManualStepFlag = sendSmsOnManualStepFlag;
	}
	

	
	@Column(name="on_approve_db_func_id")
	public int getOnApproveDbFuncId() {
		return onApproveDbFuncId;
	}
	public void setOnApproveDbFuncId(int onApproveDbFuncId) {
		this.onApproveDbFuncId = onApproveDbFuncId;
	}	
	
	@Column(name="approval_request_msg")
	public String getApprovalRequestMsg() {
		return approvalRequestMsg;
	}
	public void setApprovalRequestMsg(String approvalRequestMsg) {
		this.approvalRequestMsg = approvalRequestMsg;
	}
	
	@Column(name="hierarchical_app_msg")
	public String getHierarchicalAppMsg() {
		return hierarchicalAppMsg;
	}
	public void setHierarchicalAppMsg(String hierarchicalAppMsg) {
		this.hierarchicalAppMsg = hierarchicalAppMsg;
	}
	
	@Column(name="dynamic_app_msg")
	public String getDynamicAppMsg() {
		return dynamicAppMsg;
	}
	public void setDynamicAppMsg(String dynamicAppMsg) {
		this.dynamicAppMsg = dynamicAppMsg;
	}
	
	@Column(name="approved_msg")
	public String getApprovedMsg() {
		return approvedMsg;
	}
	public void setApprovedMsg(String approvedMsg) {
		this.approvedMsg = approvedMsg;
	}
	
	@Column(name="rejected_msg")
	public String getRejectedMsg() {
		return rejectedMsg;
	}
	public void setRejectedMsg(String rejectedMsg) {
		this.rejectedMsg = rejectedMsg;
	}
	
	@Column(name="hierarchical_level_limit")
	public int getHierarchicalLevelLimit() {
		return hierarchicalLevelLimit;
	}
		
	public void setHierarchicalLevelLimit(int hierarchicalLevelLimit) {
		this.hierarchicalLevelLimit = hierarchicalLevelLimit;
	}
	
	@Column(name="send_mail_flag")
	public short getSendMailFlag() {
		return sendMailFlag;
	}
	public void setSendMailFlag(short sendMailFlag) {
		this.sendMailFlag = sendMailFlag;
	}
	
	@Column(name="send_sms_flag")
	public short getSendSmsFlag() {
		return sendSmsFlag;
	}
	public void setSendSmsFlag(short sendSmsFlag) {
		this.sendSmsFlag = sendSmsFlag;
	}

	@Column(name="approval_table_field_id")
	public int getApprovalTableFieldId() {
		return approvalTableFieldId;
	}
	public void setApprovalTableFieldId(int approvalTableFieldId) {
		this.approvalTableFieldId = approvalTableFieldId;
	}
	@Transient
	public boolean safeEquals(W5Base q){
		if(q==null)return false;
		W5Approval a = (W5Approval)q;
		boolean b =
			this.approvalId==a.approvalId &&
			GenericUtil.safeEquals(this.dsc,a.dsc) &&
			this.activeFlag==a.approvalId &&
			this.tableId==a.tableId &&
			this.approvalTableFieldId==a.approvalTableFieldId &&
			this.actionTip==a.actionTip &&
			this.approvalStrategyTip==a.approvalStrategyTip &&
			this.approvalFlowTip==a.approvalFlowTip &&
			this.onRejectTip==a.onRejectTip &&
			GenericUtil.safeEquals(this.advancedBeginSql,a.advancedBeginSql) &&
			this.approvalRequestTip==a.approvalRequestTip &&
			this.eSignFlag==a.eSignFlag &&
			this.eSignReportId==a.eSignReportId &&
			GenericUtil.safeEquals(this.manualAppRoleIds,a.manualAppRoleIds) && 
			GenericUtil.safeEquals(this.manualAppUserIds,a.manualAppUserIds) && 
			GenericUtil.safeEquals(this.manualAppTableFieldIds,a.manualAppTableFieldIds) &&
			GenericUtil.safeEquals(this.dynamicStepUserIds,a.dynamicStepUserIds) &&
			this.dynamicStepMinUserCount==a.dynamicStepMinUserCount &&
			this.dynamicStepMaxUserCount==a.dynamicStepMaxUserCount &&
			GenericUtil.safeEquals(this.afterFinUpdateUserIds,a.afterFinUpdateUserIds) &&	
			this.manualDemandStartAppFlag==a.manualDemandStartAppFlag &&
			this.sendMailOnManualStepFlag==a.sendMailOnManualStepFlag &&
			this.sendSmsOnManualStepFlag==a.sendSmsOnManualStepFlag &&
			this.onApproveDbFuncId==a.onApproveDbFuncId &&
			GenericUtil.safeEquals(this.approvalRequestMsg,a.approvalRequestMsg) &&		
			GenericUtil.safeEquals(this.hierarchicalAppMsg,a.hierarchicalAppMsg) &&		
			GenericUtil.safeEquals(this.dynamicAppMsg,a.dynamicAppMsg) &&		
			GenericUtil.safeEquals(this.approvedMsg,a.approvedMsg) &&		
			GenericUtil.safeEquals(this.rejectedMsg,a.rejectedMsg) &&	
			this.hierarchicalLevelLimit==a.approvalId &&
			this.sendMailFlag==a.sendMailFlag &&
			this.sendSmsFlag==a.sendSmsFlag;
		if(!b)return false;

		if(!GenericUtil.safeEquals(this._approvalStepList, a._approvalStepList))return false;
		
		return true;
	}	
}
