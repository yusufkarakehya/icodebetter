package iwb.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import iwb.cache.FrameworkCache;
import iwb.cache.LocaleMsgCache;
import iwb.dao.rdbms_impl.PostgreSQL;
import iwb.domain.db.W5Table;
import iwb.domain.db.W5Workflow;
import iwb.domain.db.W5WorkflowRecord;
import iwb.domain.db.W5WorkflowStep;
import iwb.domain.result.W5FormResult;
import iwb.exception.IWBException;
import iwb.util.GenericUtil;

@Component
public class AccessControlEngine {
	@Lazy
	@Autowired
	private PostgreSQL dao;


	@Lazy
	@Autowired
	private QueryEngine queryEngine;
	
	public void accessControl4FormTable(W5FormResult formResult, String paramSuffix) { // TODO:
		// yukariya
		// yonlendirilmesi
		// lazim
		int formId = formResult.getFormId();
		int action = formResult.getAction();
		Map<String, Object> scd = formResult.getScd();
		Map<String, String> requestParams = formResult.getRequestParams();

		W5Table t = FrameworkCache.getTable(scd, formResult.getForm().getObjectId()); // formResult.getForm().get_sourceTable();
		if (t == null)
			return; // TODO

		W5WorkflowRecord appRecord = null;
		W5WorkflowStep approvalStep = null;
		if ((action == 1 || action == 3) && t.get_approvalMap() != null && !t.get_approvalMap().isEmpty()) {
			List<W5WorkflowRecord> ll = dao.find(
					"from W5WorkflowRecord t where t.projectUuid=? AND t.tableId=? AND t.tablePk=?",
					scd.get("projectId"), t.getTableId(), GenericUtil.uInt(requestParams
							.get(t.get_tableParamList().get(0).getDsc() + (paramSuffix != null ? paramSuffix : ""))));
			if (!ll.isEmpty()) {
				appRecord = ll.get(0);

				if (appRecord.getApprovalStepId() > 0 && appRecord.getApprovalStepId() < 900) {
					W5Workflow app = FrameworkCache.getWorkflow(scd, appRecord.getApprovalId());
					if (app != null)
						approvalStep = app.get_approvalStepMap().get(appRecord.getApprovalStepId()).getNewInstance();
				}
			}
		}
		// edit veya delete isleminde, accessViewControl by userFields control
		// var mi?
		if ((action == 1 || action == 3)
				&& (t.getAccessViewTip() != 0 && t.getAccessViewUserFields() != null
						&& !GenericUtil.accessControl(scd, t.getAccessViewTip(), t.getAccessViewRoles(),
								t.getAccessViewUsers())
				&& (t.getAccessViewUserFields() != null && t.getAccessViewUserFields().length() > 0 && dao
						.accessUserFieldControl(t, t.getAccessViewUserFields(), scd, requestParams, paramSuffix)))) { // bu
			// field'da
			throw new IWBException("security", "Form", formId, null,
					LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_onay_kontrol_goruntuleme"), null);
		}
		boolean updatableUserFieldFlag = false;
		boolean deletableUserFieldFlag = false;
		switch (action) {
		case 1: // update
			if (t.getAccessUpdateTip() != 0 && t.getAccessUpdateUserFields() != null
					&& t.getAccessUpdateUserFields().length() > 0 && !GenericUtil.accessControl(scd,
							t.getAccessUpdateTip(), t.getAccessUpdateRoles(), t.getAccessUpdateUsers())) { // bu
				// field'da
				if (dao.accessUserFieldControl(t, t.getAccessUpdateUserFields(), scd, requestParams, paramSuffix)) {
					formResult.getOutputMessages().add(
							LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_onay_kontrol_guncelleme"));
					formResult.setViewMode(true);
				} else
					updatableUserFieldFlag = true;
			}

			if (appRecord != null && (appRecord.getApprovalStepId() < 900 || appRecord.getApprovalStepId() == 999)) { // eger
				// bir
				// approval
				// sureci
				// icindeyse
				if (appRecord.getApprovalStepId() == 999) {
					formResult.getOutputMessages().add(
							LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_onay_kontrol_red_kayit_guncelleme"));
					formResult.setViewMode(true);
				} else if (!GenericUtil.accessControl(scd, appRecord.getAccessViewTip(), appRecord.getAccessViewRoles(),
						appRecord.getAccessViewUsers())
						|| (approvalStep != null && ((!GenericUtil.accessControl(scd, approvalStep.getAccessUpdateTip(),
								approvalStep.getAccessUpdateRoles(), approvalStep.getAccessUpdateUsers())
								&& approvalStep.getAccessUpdateUserFields() == null)
								|| (approvalStep.getAccessUpdateUserFields() != null && dao.accessUserFieldControl(t,
										approvalStep.getAccessUpdateUserFields(), scd, requestParams, paramSuffix))))) {
					// if(appRecord.getApprovalStepId()!=1 ||
					// appRecord.getInsertUserId()!=(Integer)scd.get("userId")){//eger
					// daha ilk asamada ve
					// kaydi kaydeden bunu yapmaya calisirsa, Veli Abi bu ne ya
					// yapma yapma
					// throw new PromisException("security","Workflow",
					// appRecord.getApprovalId(),
					// null, "Onay süreci içerisinde bu kaydı
					// Güncelleyemezsiniz", null);
					formResult.getOutputMessages()
							.add(LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_onay_kontrol_surec_devam"));
					formResult.setViewMode(true);
					// }
				}
				formResult.setApprovalRecord(appRecord);
			} else {
				// bu table'a update hakki var mi?
				if (!updatableUserFieldFlag && !GenericUtil.accessControl(scd, t.getAccessUpdateTip(),
						t.getAccessUpdateRoles(), t.getAccessUpdateUsers())) {
					// throw new PromisException("security","Form", formId,
					// null,
					// PromisLocaleMsg.get2(0,(String)scd.get("locale"),"fw_guvenlik_tablo_kontrol_guncelleme"),
					// null);
					formResult.getOutputMessages().add(
							LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_tablo_kontrol_guncelleme"));
					formResult.setViewMode(true);
				}

				if (t.getAccessTips() != null && t.getAccessTips().indexOf("0") > -1) { // kayit
					// bazli
					// kontrol
					// var
					if (checkAccessRecordControlViolation(scd, 0, t.getTableId(),
							requestParams.get(t.get_tableParamList().get(0).getDsc()))) {
						throw new IWBException("security", "Form", formId, null, LocaleMsgCache.get2(0,
								(String) scd.get("locale"), "fw_guvenlik_kayit_bazli_kontrol_goruntuleme"), null);
					}
				}
				if (t.getAccessTips() != null && t.getAccessTips().indexOf("1") > -1) { // kayit
					// bazli
					// kontrol
					// var
					if (checkAccessRecordControlViolation(scd, 1, t.getTableId(),
							requestParams.get(t.get_tableParamList().get(0).getDsc()))) {
						// throw new PromisException("security","Form", formId,
						// null, "Kayıt Bazlı Kontrol:
						// Güncelleyemezsiniz", null);

						formResult.getOutputMessages().add(LocaleMsgCache.get2(0, (String) scd.get("locale"),
								"fw_guvenlik_kayit_bazli_kontrol_guncelleme"));
						formResult.setViewMode(true);
					}
				}
				formResult.setApprovalRecord(appRecord);
			}
			break;
		case 2: // insert
			if (!GenericUtil.accessControl(scd, t.getAccessInsertTip(), t.getAccessInsertRoles(),
					t.getAccessInsertUsers())) { // Table access insert control
				throw new IWBException("security", "Form", formId, null,
						LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_tablo_kontrol_kayit_ekleme"),
						null);
			}

			break;
		case 3: // delete
			if (t.getAccessDeleteTip() != 0 && !GenericUtil.isEmpty(t.getAccessDeleteUserFields()) && !GenericUtil
					.accessControl(scd, t.getAccessDeleteTip(), t.getAccessDeleteRoles(), t.getAccessDeleteUsers())) { // bu
				// field'da
				if (dao.accessUserFieldControl(t, t.getAccessDeleteUserFields(), scd, requestParams, paramSuffix)) {
					formResult.getOutputMessages()
							.add(LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_onay_kontrol_silme"));
					formResult.setViewMode(true);
				} else
					deletableUserFieldFlag = true;
			}
			/*
			 * if(t.getAccessDeleteTip()!=0 &&
			 * t.getAccessDeleteUserFields()!=null &&
			 * t.getAccessDeleteUserFields().length()>0){ // bu field'da
			 * if(dao.accessUserFieldControl(t, t.getAccessDeleteUserFields(),
			 * scd, requestParams, paramSuffix)) throw new
			 * PromisException("security","Form", formId, null,
			 * PromisLocaleMsg.get2(0,(String)scd.get("locale"),
			 * "fw_guvenlik_silinemez_kullanici_alan_kisit"), null); }
			 */

			if (appRecord != null) { // eger bir approval sureci icindeyse
				if (!GenericUtil.accessControl(scd, appRecord.getAccessViewTip(), appRecord.getAccessViewRoles(),
						appRecord.getAccessViewUsers())
						|| (approvalStep != null && !GenericUtil.accessControl(scd, approvalStep.getAccessDeleteTip(),
								approvalStep.getAccessDeleteRoles(), approvalStep.getAccessDeleteUsers()))) {
					throw new IWBException("security",
							"Workflow", appRecord.getApprovalId(), null, LocaleMsgCache.get2(0,
									(String) scd.get("locale"), "fw_onay_sureci_icerisindeki_kaydi_silemezsiniz"),
							null);
				}
			} else {
				// bu table'a delete hakki var mi?
				if (!deletableUserFieldFlag && !GenericUtil.accessControl(scd, t.getAccessDeleteTip(),
						t.getAccessDeleteRoles(), t.getAccessDeleteUsers())) {
					throw new IWBException("security", "Form", formId, null,
							LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_tablo_kontrol_silme"),
							null);
				} /*
					 * if(!PromisUtil.accessControl(scd, t.getAccessDeleteTip(),
					 * t.getAccessDeleteRoles(), t.getAccessDeleteUsers())){
					 * throw new PromisException("security","Form", formId,
					 * null, PromisLocaleMsg.get2(0,(String)scd.get("locale"),
					 * "fw_guvenlik_tablo_kontrol_silme"), null); }
					 */

				// kayit bazli kontrol var
				if (t.getAccessTips() != null && t.getAccessTips().indexOf("0") > -1) { // show
					if (checkAccessRecordControlViolation(scd, 0, t.getTableId(),
							requestParams.get(t.get_tableParamList().get(0).getDsc()))) {
						throw new IWBException("security", "Form", formId, null, LocaleMsgCache.get2(0,
								(String) scd.get("locale"), "fw_guvenlik_kayit_bazli_kontrol_goruntuleme"), null);
					}
				}
				if (t.getAccessTips() != null && t.getAccessTips().indexOf("3") > -1) { // delete
					if (checkAccessRecordControlViolation(scd, 3, t.getTableId(),
							requestParams.get(t.get_tableParamList().get(0).getDsc()))) {
						throw new IWBException("security", "Form", formId, null, LocaleMsgCache.get2(0,
								(String) scd.get("locale"), "fw_guvenlik_kayit_bazli_kontrol_silme"), null);
					}
				}
			}
		}
		formResult.setApprovalRecord(appRecord);
	}

	public boolean checkAccessRecordControlViolation(Map<String, Object> scd, int accessTip, int tableId,
			String tablePk) {
		Map<String, String> rm = new HashMap<String, String>();
		rm.put("xaccess_tip", "" + accessTip);
		rm.put("xtable_id", "" + tableId);
		rm.put("xtable_pk", tablePk);
		Map m = queryEngine.executeQuery2Map(scd, 588, rm);
		return (m != null && !GenericUtil.accessControl(scd, (short) accessTip, (String) m.get("access_roles"),
				(String) m.get("access_users")));
	}
	
}
