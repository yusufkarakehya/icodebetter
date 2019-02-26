package iwb.engine;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import iwb.cache.FrameworkCache;
import iwb.cache.FrameworkSetting;
import iwb.cache.LocaleMsgCache;
import iwb.custom.trigger.PostFormTrigger;
import iwb.dao.rdbms_impl.MetadataLoaderDAO;
import iwb.dao.rdbms_impl.PostgreSQL;
import iwb.domain.db.Log5Feed;
import iwb.domain.db.Log5Notification;
import iwb.domain.db.Log5WorkflowRecord;
import iwb.domain.db.W5Comment;
import iwb.domain.db.W5Conversion;
import iwb.domain.db.W5ConvertedObject;
import iwb.domain.db.W5Email;
import iwb.domain.db.W5FormModule;
import iwb.domain.db.W5ObjectMailSetting;
import iwb.domain.db.W5Table;
import iwb.domain.db.W5TableChild;
import iwb.domain.db.W5TableEvent;
import iwb.domain.db.W5VcsObject;
import iwb.domain.db.W5Workflow;
import iwb.domain.db.W5WorkflowRecord;
import iwb.domain.db.W5WorkflowStep;
import iwb.domain.helper.W5QueuedActionHelper;
import iwb.domain.helper.W5SynchAfterPostHelper;
import iwb.domain.helper.W5TableRecordHelper;
import iwb.domain.result.W5FormResult;
import iwb.exception.IWBException;
import iwb.util.DBUtil;
import iwb.util.GenericUtil;
import iwb.util.ScriptUtil;
import iwb.util.UserUtil;

@Component
public class CRUDEngine {
	@Lazy
	@Autowired
	private PostgreSQL dao;

	@Lazy
	@Autowired
	private MetadataLoaderDAO metaDataDao;

	@Lazy
	@Autowired
	private ConversionEngine conversionEngine;

	@Lazy
	@Autowired
	private QueryEngine queryEngine;

	@Lazy
	@Autowired
	private WorkflowEngine workflowEngine;

	@Lazy
	@Autowired
	private GlobalScriptEngine scriptEngine;
	

	@Lazy
	@Autowired
	private NotificationEngine notificationEngine;
	
	
	@Lazy
	@Autowired 
	private AccessControlEngine acEngine;

	@SuppressWarnings({ "unused", "unchecked" })
	public List<W5QueuedActionHelper> postForm4Table(W5FormResult formResult, String paramSuffix,
			Set<String> checkedParentRecords) {
		try {
			dao.checkTenant(formResult.getScd());
			List<W5QueuedActionHelper> result = new ArrayList<W5QueuedActionHelper>();
			int formId = formResult.getFormId();
			int action = formResult.getAction();
			int realAction = action;
			Map<String, Object> scd = formResult.getScd();
			Map<String, String> requestParams = formResult.getRequestParams();

			PostFormTrigger.beforePostForm(formResult, dao, paramSuffix);
			boolean dev = scd.get("roleId") != null && (Integer) scd.get("roleId") == 0
					&& GenericUtil.uInt(requestParams, "_dev") != 0;
			String projectId = dev ? FrameworkSetting.devUuid : (String) scd.get("projectId");
			W5Table t = FrameworkCache.getTable(projectId, formResult.getForm().getObjectId()); // formResult.getForm().get_sourceTable();

			String schema = null;
			W5Workflow approval = null;
			W5WorkflowRecord appRecord = null;
			W5WorkflowStep approvalStep = null;
			boolean accessControlSelfFlag = true; // kendisi VEYA kendisi+master
			if (accessControlSelfFlag) {
				int outCnt = formResult.getOutputMessages().size();
				acEngine.accessControl4FormTable(formResult, paramSuffix);
				if (formResult.isViewMode()) {
					throw new IWBException("security", "Form", formId, null,
							formResult.getOutputMessages().size() > outCnt ? formResult.getOutputMessages().get(outCnt)
									: LocaleMsgCache.get2(0, (String) scd.get("locale"),
											"fw_guvenlik_tablo_kontrol_guncelleme"),
							null);
				}
				if (FrameworkSetting.workflow) {
					appRecord = formResult.getApprovalRecord();
					if (appRecord != null) {
						approval = FrameworkCache.getWorkflow(scd, appRecord.getApprovalId()); // dao.loadObject(W5Workflow.class,
						// formResult.getApprovalRecord().getApprovalId());
						boolean canCancel = GenericUtil.hasPartInside2(approval.getAfterFinUpdateUserIds(),
								scd.get("userId")) && appRecord.getApprovalActionTip() == 5
								&& appRecord.getApprovalStepId() == 998 ? true : false;
						approvalStep = approval.get_approvalStepMap().get(appRecord.getApprovalStepId())
								.getNewInstance();
						if (approvalStep != null && approvalStep.getApprovalStepId() != 901
								&& approvalStep.getUpdatableFields() == null && !canCancel) {
							throw new IWBException("security", "Form", formId, null,
									LocaleMsgCache.get2(0, (String) scd.get("locale"),
											"fw_onay_sureci_icerisinde_bu_kaydin_alanlarini_guncelleyemezsiniz"),
									null);
						}
					}
				}
			}
			boolean mobile = GenericUtil.uInt(formResult.getScd().get("mobile")) != 0;
			int sourceStepId = -1;
			String ptablePk = null; // accessControl islemi icin
			String pcopyTablePk = null; // accessControl islemi icin
			Log5Feed feed = null;
			int feedTableId = 0, feedTablePk = 0;
			// once load edilmis eski objeye gerek var mi? wdiget'lar icin
			// gerekir, condition olan yerler
			// icin de gerekebilir
			Map<String, Object> oldObj = null;

			if (action == 9 /* edit (if not insert) */) {
				action = dao.checkIfRecordsExists(scd, requestParams, t) ? 1 : 2;
			}

			List<W5TableEvent> tla = FrameworkCache.getTableEvents(projectId, t.getTableId());
			/* tableTrigger Before Action start */
			if (tla != null)
				extFormTableEvent(formResult, new String[] { "_", "bu", "bi", "bd", "_", "bi" }[action], scd,
						requestParams, t, requestParams.get(t.get_tableParamList().get(0).getDsc() + paramSuffix),
						paramSuffix);
			/* end of tableTrigger */

			switch (action) {
			case 1: // update
				ptablePk = requestParams.get(t.get_tableParamList().get(0).getDsc() + paramSuffix);
				if (GenericUtil.isEmpty(paramSuffix)) {
					formResult.setPkFields(new HashMap());
					formResult.getPkFields().put(t.get_tableParamList().get(0).getDsc(), ptablePk);
				}
				if (FrameworkSetting.workflow && accessControlSelfFlag) {

					if (appRecord == null && t.get_approvalMap() != null) { // su
																			// anda
																			// bir
																			// onay
																			// icinde
																			// degil
																			// ve
																			// onay
																			// mekanizmasi
																			// var
																			// mi
																			// bunda?
						approval = t.get_approvalMap().get((short) 1); // action=1
																		// for
																		// update

						if (approval != null && approval.getActiveFlag() != 0) { // update
																					// approval
																					// mekanizmasi
																					// var
							Map<String, Object> advancedStepSqlResult = null;
							if (approval.getAdvancedBeginSql() != null
									&& approval.getAdvancedBeginSql().length() > 10) { // calisacak
								advancedStepSqlResult = dao.runSQLQuery2Map(approval.getAdvancedBeginSql(), scd,
										requestParams, null);
								// donen bir cevap var, aktive_flag deger olarak
								// var ve onun degeri 0 ise o zaman
								// girmeyecek
								if (advancedStepSqlResult != null && advancedStepSqlResult.get("active_flag") != null
										&& GenericUtil.uInt(advancedStepSqlResult.get("active_flag")) == 0) { // girmeyecek
									approval = null; // approval olmayacak
								}
							}
							if (approval != null) { // eger approval olacaksa
								approvalStep = null;
								if (approval.getApprovalFlowTip() == 0) { // simple
									approvalStep = approval.get_approvalStepList().get(0).getNewInstance();
								} else { // complex
									if (advancedStepSqlResult != null
											&& advancedStepSqlResult.get("approval_step_id") != null
											&& GenericUtil.uInt(advancedStepSqlResult.get("approval_step_id")) != 0)
										approvalStep = approval.get_approvalStepMap()
												.get(GenericUtil.uInt(advancedStepSqlResult.get("approval_step_id")));
									else
										approvalStep = approval.get_approvalStepList().get(0).getNewInstance();
								}
								if (approvalStep != null) { // step hazir
									appRecord = new W5WorkflowRecord(scd, approvalStep.getApprovalStepId(),
											approval.getApprovalId(), formResult.getForm().getObjectId(), (short) 0,
											approvalStep.getReturnFlag());
									boolean bau = advancedStepSqlResult != null
											&& advancedStepSqlResult.get("approval_users") != null;
									appRecord
											.setApprovalUsers(bau ? (String) advancedStepSqlResult.get("approval_users")
													: approvalStep.getApprovalUsers());
									appRecord
											.setApprovalRoles(bau ? (String) advancedStepSqlResult.get("approval_roles")
													: approvalStep.getApprovalRoles());
									boolean bavt = advancedStepSqlResult != null
											&& advancedStepSqlResult.get("access_view_tip") != null;
									appRecord
											.setAccessViewTip(bavt
													? (short) GenericUtil
															.uInt(advancedStepSqlResult.get("access_view_tip"))
													: approvalStep.getAccessViewTip());
									appRecord.setAccessViewRoles(
											bavt ? (String) advancedStepSqlResult.get("access_view_roles")
													: approvalStep.getAccessViewRoles());
									appRecord.setAccessViewUsers(
											bavt ? (String) advancedStepSqlResult.get("access_view_users")
													: approvalStep.getAccessViewUsers());
									if (appRecord.getAccessViewTip() != 0 && !GenericUtil
											.hasPartInside2(appRecord.getAccessViewUsers(), scd.get("userId"))) // goruntuleme
																												// kisiti
																												// var
																												// ve
																												// kendisi
																												// goremiyorsa,
										// kendisini de ekle
										appRecord.setAccessViewUsers(appRecord.getAccessViewUsers() != null
												? appRecord.getAccessViewUsers() + "," + scd.get("userId")
												: scd.get("userId").toString());
								} else {
									throw new IWBException("framework", "Workflow", approval.getApprovalId(), null,
											LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_hatali_onay_tanimi"),
											null);
								}
							}
						}

						if (approval == null) {
							approval = t.get_approvalMap().get((short) 2); // action=2
																			// insert
																			// mode
																			// ile
																			// başlatılıyor
																			// ayrı
																			// bi
																			// şov
																			// tabii
							// düzeltilmesi lazım
							if (approval != null && approval.getApprovalRequestTip() == 2
									&& approval.getManualDemandStartAppFlag() == 0)
								approval = null;
						}

						if (appRecord == null && t.get_approvalMap() != null
								&& formResult.getRequestParams().get("_aa") != null
								&& GenericUtil.uInt(formResult.getRequestParams().get("_aa")) == -1) { // Insertle
																										// ilgili
																										// bir
																										// onay
																										// başlatma
																										// isteği
																										// var
																										// ve
																										// böylece
																										// artık
							// 901'e giriyor
							Map<String, Object> advancedStepSqlResult = null;
							if (approval.getAdvancedBeginSql() != null
									&& approval.getAdvancedBeginSql().length() > 10) {
								Object[] oz = DBUtil.filterExt4SQL(approval.getAdvancedBeginSql(), scd, requestParams,
										null);
								advancedStepSqlResult = dao.runSQLQuery2Map(oz[0].toString(), (List) oz[1], null);
								if (advancedStepSqlResult != null) {
									if (advancedStepSqlResult.get("active_flag") != null
											&& GenericUtil.uInt(advancedStepSqlResult.get("active_flag")) == 0)
										approval = null;
									else {
										approvalStep = new W5WorkflowStep();
										approvalStep.setApprovalUsers("" + (Integer) scd.get("userId"));
										approvalStep.setApprovalStepId(901);
									}
									if (advancedStepSqlResult.get("error_msg") != null)
										throw new IWBException("security", "Workflow", approval.getApprovalId(), null,
												(String) advancedStepSqlResult.get("error_msg"), null);
								}
							} else {
								approvalStep = new W5WorkflowStep();
								approvalStep.setApprovalRoles(approval.getManualAppRoleIds());
								approvalStep.setApprovalUsers(approval.getManualAppUserIds());
								if (approval.getManualAppTableFieldIds() != null) {
								} else if (approvalStep.getApprovalUsers() == null)
									approvalStep.setApprovalUsers("" + (Integer) scd.get("userId"));

								approvalStep.setApprovalStepId(901);
							}

							if (approvalStep != null) { // step hazir
								appRecord = new W5WorkflowRecord(scd, approvalStep.getApprovalStepId(),
										approval.getApprovalId(), formResult.getForm().getObjectId(), (short) 0,
										approvalStep.getReturnFlag());
								appRecord.setApprovalUsers(advancedStepSqlResult != null
										&& advancedStepSqlResult.get("approval_users") != null
												? (String) advancedStepSqlResult.get("approval_users")
												: approvalStep.getApprovalUsers());
								appRecord.setApprovalRoles(advancedStepSqlResult != null
										&& advancedStepSqlResult.get("approval_roles") != null
												? (String) advancedStepSqlResult.get("approval_roles")
												: approvalStep.getApprovalRoles());
								appRecord.setAccessViewTip(advancedStepSqlResult != null
										&& advancedStepSqlResult.get("access_view_tip") != null
												? (short) GenericUtil.uInt(advancedStepSqlResult.get("access_view_tip"))
												: approvalStep.getAccessViewTip());
								appRecord.setAccessViewRoles(advancedStepSqlResult != null
										&& advancedStepSqlResult.get("access_view_roles") != null
												? (String) advancedStepSqlResult.get("access_view_roles")
												: approvalStep.getAccessViewRoles());
								appRecord.setAccessViewUsers(advancedStepSqlResult != null
										&& advancedStepSqlResult.get("access_view_users") != null
												? (String) advancedStepSqlResult.get("access_view_users")
												: approvalStep.getAccessViewUsers());
								if (appRecord.getAccessViewTip() != 0 && !GenericUtil
										.hasPartInside2(appRecord.getAccessViewUsers(), scd.get("userId"))) // goruntuleme
																											// kisiti
																											// var
																											// ve
																											// kendisi
																											// goremiyorsa,
									// kendisini de ekle
									appRecord.setAccessViewUsers(appRecord.getAccessViewUsers() != null
											? appRecord.getAccessViewUsers() + "," + scd.get("userId")
											: scd.get("userId").toString());
							} else {
								throw new IWBException("framework", "Workflow", formId, null,
										LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_hatali_onay_tanimi"),
										null);
							}
						}
					} else {
						if (appRecord != null) {
							String noUpdateVersionNo = FrameworkCache.getAppSettingStringValue(scd,
									"approval_no_update_version_no");
							if (GenericUtil.isEmpty(noUpdateVersionNo)
									|| !GenericUtil.hasPartInside(noUpdateVersionNo, "" + t.getTableId())) {
								appRecord.setVersionNo(appRecord.getVersionNo() + 1);
								dao.updateObject(appRecord);
							}
							if (FrameworkSetting.liveSyncRecord)
								formResult.addSyncRecord(new W5SynchAfterPostHelper((String) scd.get("projectId"),
										392 /* w5_approval_record */, "" + appRecord.getApprovalRecordId(),
										(Integer) scd.get("userId"), requestParams.get(".w"), (short) 1));
							appRecord = null; // bu kaydedilmeyecek
						}
					}
				}

				dao.updateFormTable(formResult, paramSuffix);

				//
				// if(formResult.getErrorMap().isEmpty())FrameworkCache.removeTableCacheValue(t.getCustomizationId(),
				// t.getTableId(),GenericUtil.uInt(ptablePk));//caching icin

				if (FrameworkSetting.workflow && accessControlSelfFlag && formResult.getErrorMap().isEmpty()
						&& appRecord != null) { // aproval baslanmis
					int tablePk = GenericUtil.uInt(formResult.getOutputFields()
							.get(/* formResult.getForm().get_sourceTable() */ FrameworkCache
									.getTable(scd, formResult.getForm().getObjectId()).get_tableFieldList().get(0)
									.getDsc()));
					if (tablePk == 0) {
						tablePk = GenericUtil.uInt(ptablePk);
					}
					appRecord.setTablePk(tablePk);
					String summaryText = dao.getSummaryText4Record(scd, t.getTableId(), tablePk);
					appRecord.setDsc(GenericUtil.uStrMax(summaryText, 512));
					dao.saveObject(appRecord);
					if (FrameworkSetting.liveSyncRecord)
						formResult.addSyncRecord(new W5SynchAfterPostHelper((String) scd.get("projectId"),
								392 /* w5_approval_record */, "" + appRecord.getApprovalRecordId(),
								(Integer) scd.get("userId"), requestParams.get(".w"), (short) 2));
					Log5WorkflowRecord logRecord = new Log5WorkflowRecord();
					logRecord.setApprovalActionTip((short) 0); // start,
																// approve,
																// return,
																// reject,
																// time_limit_cont
																// ,final_approve
					logRecord.setUserId((Integer) scd.get("userId"));
					logRecord.setApprovalRecordId(appRecord.getApprovalRecordId());
					logRecord.setApprovalStepId(sourceStepId);
					logRecord.setApprovalId(appRecord.getApprovalId());
					dao.saveObject(logRecord);
					formResult.getOutputMessages()
							.add(t.get_approvalMap().get((short) 2).getDsc() + " "
									+ LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_onaya_sunulmustur") + " ("
									+ summaryText + ")");

					// Mail ve SMS işlemleri _aa=-1 gelirse //

					String appRecordUserList = null;
					String appRecordRoleList = null;
					String mesajBody = "";

					List<Object> notificationUsers = dao.executeSQLQuery(
							"select distinct gu.user_id from iwb.w5_User gu where gu.customization_id= ? and gu.user_Id != ? and (gu.user_Id in (select ur.user_Id from iwb.w5_User_Role ur where ur.role_id in ((select x.satir from table(tool_parse_numbers(?,\',\'))x))) or gu.user_id in ((select x.satir from table(tool_parse_numbers(?,\',\'))x)))",
							scd.get("customizationId"), scd.get("userId"), appRecord.getApprovalRoles(),
							appRecord.getApprovalUsers());
					if (notificationUsers != null)
						for (Object o : notificationUsers) {
							dao.saveObject2(
									new Log5Notification(scd, GenericUtil.uInt(o),
											(short) (appRecord.getApprovalStepId() == 901
													&& approval.getApprovalFlowTip() == 3 ? 903 : 6),
									approval.getTableId(), appRecord.getTablePk(), GenericUtil.uInt(scd.get("userId")),
									null, 1), scd);
						}

					if ((approval != null && approval.getActiveFlag() != 0
							&& ((appRecord.getApprovalStepId() < 900
									&& (approvalStep.getSendMailOnEnterStepFlag() != 0)))
							|| appRecord.getApprovalStepId() > 900)) {
						appRecordUserList = appRecord.getApprovalUsers();
						appRecordRoleList = appRecord.getApprovalRoles();
						List<String> emailList = null;
						if (approvalStep.getSendMailOnEnterStepFlag() != 0)
							emailList = dao.executeSQLQuery(
									"select gu.email from iwb.w5_user gu where gu.customization_Id=? and gu.user_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and gu.user_id <> ? union select (select gu.email from iwb.w5_user gu where gu.customizationId=? and gu.user_id = ur.user_id and gu.user_status=1) from iwb.w5_user_role ur where ur.role_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and ur.user_id <> ?",
									scd.get("customizationId"), appRecordUserList, scd.get("userId"),
									scd.get("customizationId"), appRecordRoleList, scd.get("userId"));

						/*
						 * if(emailList == null &&
						 * (approval.getApprovalStrategyTip() == 1 ||
						 * approval.getSendMailOnManualStepFlag() == 1)){ //
						 * Eğer manual başlat varsa appRecordUserList =
						 * approval.getManualAppUserIds(); appRecordRoleList =
						 * approval.getManualAppRoleIds(); emailList =
						 * dao.executeSQLQuery(
						 * "select gu.email from iwb.w5_user gu where gu.customization_id= ? and gu.user_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and gu.user_id <> ? union select (select gu.email from iwb.w5_user gu where gu.customization_id= ? and gu.user_id = ur.user_id and gu.user_status=1) from iwb.w5_user_role ur where ur.role_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and ur.user_id <> ?"
						 * ,scd.get("customizationId"),appRecordUserList,scd.get
						 * ("userId"),scd.get("customizationId"),
						 * appRecordRoleList,scd.get("userId")); }
						 */

						if (emailList != null && emailList.size() > 0) {
							String pemailList = "";
							Object[] m = emailList.toArray();
							for (int i = 0; i < m.length; i++)
								pemailList += "," + m[i];
							pemailList = pemailList.substring(1);

							int mail_setting_id = GenericUtil.uInt((Object) scd.get("mailSettingId"));
							if (mail_setting_id == 0)
								mail_setting_id = FrameworkCache.getAppSettingIntValue(scd, "default_outbox_id");

							W5ObjectMailSetting oms = (W5ObjectMailSetting) dao
									.find("from W5ObjectMailSetting x where x.customizationId=? and x.mailSettingId=?",
											(Integer) scd.get("customizationId"), mail_setting_id)
									.get(0);
							if (appRecord.getApprovalStepId() == 901) {
								mesajBody = "'" + scd.get("completeName") + "' " + LocaleMsgCache.get2(0,
										(String) scd.get("locale"), "onay_surecini_baslatmanizi_istiyor");
							} else {
								mesajBody = "'" + scd.get("completeName") + "' " + LocaleMsgCache.get2(0,
										(String) scd.get("locale"), "tarafindan_onaya_sunulmustur");
							}

							if (oms != null) {
								W5Email email = new W5Email(pemailList, null, null,
										t.get_approvalMap().get((short) 2).getDsc(),
										" (" + summaryText + ") " + mesajBody, null); // mail_keep_body_original
																						// ?
								email.set_oms(oms);
								String sonuc = notificationEngine.sendMail(scd, email);
								if (FrameworkCache.getAppSettingIntValue(0, "mail_debug_flag") != 0) {
									if (sonuc != null) { // basarisiz, queue'ye
															// at//
										System.out.println(LocaleMsgCache.get2(0, (String) scd.get("locale"),
												"onay_mekanizmasi_akisinda_mail_gonderilemedi"));
									} else {
										System.out.println(LocaleMsgCache.get2(0, (String) scd.get("locale"),
												"onay_mekanizmasi_akisinda_mail_gonderildi"));
									}
								}
							}
						}
					}

					if ((approval != null && approval.getActiveFlag() != 0
							&& ((appRecord.getApprovalStepId() < 900 && approvalStep.getSendSmsOnEnterStepFlag() != 0))
							|| appRecord.getApprovalStepId() > 900)) {
						if (FrameworkCache.getAppSettingIntValue(scd, "sms_flag") != 0) {
							appRecordUserList = appRecord.getApprovalUsers();
							appRecordRoleList = appRecord.getApprovalRoles();

							List<String> gsmList = dao.executeSQLQuery(
									"select gu.gsm from iwb.w5_user gu where gu.customization_id=? and gu.user_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and gu.user_id <> ? union select (select gu.gsm from iwb.w5_user gu where gu.customization_id=? and gu.user_id = ur.user_id and gu.user_status=1) from iwb.w5_user_role ur where ur.role_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and ur.user_id <> ?",
									scd.get("customizationId"), appRecordUserList, scd.get("userId"),
									scd.get("customizationId"), appRecordRoleList, scd.get("userId"));

							/*
							 * if(gsmList == null &&
							 * (approval.getApprovalStrategyTip() == 1 ||
							 * approval.getSendSmsOnManualStepFlag() == 1)){ //
							 * Eğer manuel başlat varsa appRecordUserList =
							 * approval.getManualAppUserIds(); appRecordRoleList
							 * = approval.getManualAppRoleIds(); gsmList =
							 * dao.executeSQLQuery(
							 * "select gu.gsm from iwb.w5_user gu where gu.customization_id=? and gu.user_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and gu.user_id <> ? union select (select gu.gsm from iwb.w5_user gu where gu.customization_id=? and gu.user_id = ur.user_id and gu.user_status=1) from iwb.w5_user_role ur where ur.role_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and ur.user_id <> ?"
							 * ,scd.get("customizationId"),appRecordUserList,scd
							 * .get("userId"),scd.get("customizationId"),
							 * appRecordRoleList,scd.get("userId")); }
							 */
							if (gsmList.size() > 0) {
								String phoneNumber = "";
								for (String gsm : gsmList)
									phoneNumber = phoneNumber + (GenericUtil.isEmpty(phoneNumber) ? "" : ",") + gsm;

								notificationEngine.sendSms(GenericUtil.uInt(scd.get("customizationId")),
										GenericUtil.uInt(scd.get("userId")), phoneNumber,
										t.get_approvalMap().get((short) 2).getDsc() + " (" + summaryText + ") "
												+ mesajBody,
										392, appRecord.getApprovalRecordId());
							}
						}
					}
				}
				break;
			case 5: // copy
			case 2: // insert
				if (FrameworkSetting.workflow && accessControlSelfFlag && t.get_approvalMap() != null) { // onay
																											// mekanizmasi
																											// var
																											// mi
																											// bunda?
					approval = t.get_approvalMap().get((short) 2); // action=2
																	// for
																	// insert
					if (approval != null && approval.getActiveFlag() != 0 && approval.getApprovalRequestTip() >= 1) { // insert
																														// approval
																														// mekanizmasi
																														// var
																														// ve
																														// automatic
						Map<String, Object> advancedStepSqlResult = null;
						switch (approval.getApprovalRequestTip()) { // eger
																	// approval
																	// olacaksa
						case 1: // automatic approval
							if (approval.getAdvancedBeginSql() != null
									&& approval.getAdvancedBeginSql().trim().length() > 2) { // calisacak
								
								Object oz = scriptEngine.executeScript(scd, requestParams, approval.getAdvancedBeginSql(), null, "wf_"+approval.getApprovalId()+"_abs");
								if(oz!=null) {
									if(oz instanceof Boolean) {
										if(!((Boolean)oz))approval=null;
									} else
										advancedStepSqlResult = ScriptUtil.fromScriptObject2Map(oz); 
								}
//								Object[] oz = DBUtil.filterExt4SQL(approval.getAdvancedBeginSql(), scd, requestParams, null);
//								advancedStepSqlResult = dao.runSQLQuery2Map(oz[0].toString(), (List) oz[1], null);
								// donen bir cevap var, aktive_flag deger olarak
								// var ve onun degeri 0 ise o
								// zaman girmeyecek

							}
							approvalStep = null;
							if(approval!=null)switch (approval.getApprovalFlowTip()) { // simple
							case 0: // basit onay
								approvalStep = approval.get_approvalStepList().get(0).getNewInstance();
								break;
							case 1: // complex onay
								if (advancedStepSqlResult != null
										&& advancedStepSqlResult.get("approval_step_id") != null
										&& GenericUtil.uInt(advancedStepSqlResult.get("approval_step_id")) != 0)
									approvalStep = approval.get_approvalStepMap()
											.get(GenericUtil.uInt(advancedStepSqlResult.get("approval_step_id")))
											.getNewInstance();
								else
									approvalStep = approval.get_approvalStepList().get(0).getNewInstance();
								break;
						/*	case 2: // hierarchical onay DEPRECATED
								int mngUserId = GenericUtil.uInt(scd.get("mngUserId"));
								if (mngUserId != 0) {
									approvalStep = new W5WorkflowStep();
									approvalStep.setApprovalUsers("" + mngUserId);
									approvalStep.setApprovalStepId(902);
									approvalStep.setSendMailOnEnterStepFlag(approval.getSendMailFlag());
									approvalStep.setSendSmsOnEnterStepFlag(approval.getSendSmsFlag());
								} else { // direk duz approval kismine
											// geciyor:TODO
									if (approval.get_approvalStepList() != null
											&& !approval.get_approvalStepList().isEmpty())
										approvalStep = approval.get_approvalStepList().get(0).getNewInstance();
									else
										approvalStep = null;
								}

								break;*/
							}

							break;
						case 2: // manual after action
							if (approval.getManualDemandStartAppFlag() == 0
									|| (approval.getManualDemandStartAppFlag() == 1
											&& GenericUtil.uInt(formResult.getRequestParams().get("_aa")) == -1)) { // Eğer
																													// onay
																													// mekanizması
																													// elle
																													// başlatılmayacaksa
																													// burada
																													// 901'e
								// girmesi sağlanır
							/*	if (approval.getAdvancedBeginSql() != null
										&& approval.getAdvancedBeginSql().length() > 10) { // calisacak
									Object oz = scriptEngine.executeScript(scd, requestParams, approval.getAdvancedBeginSql(), null, "wf_"+approval.getApprovalId()+"_abs");
									if(oz!=null) {
										
										advancedStepSqlResult = ScriptUtil.fromScriptObject2Map(oz); 
									}
									//Object[] oz = DBUtil.filterExt4SQL(approval.getAdvancedBeginSql(), scd,requestParams, null);
									//advancedStepSqlResult = dao.runSQLQuery2Map(oz[0].toString(), (List) oz[1], null);
									// donen bir cevap var, aktive_flag deger
									// olarak var ve onun degeri 0 ise o
									// zaman girmeyecek
									if (advancedStepSqlResult != null) {
										if (advancedStepSqlResult.get("active_flag") != null
												&& GenericUtil.uInt(advancedStepSqlResult.get("active_flag")) == 0) // girmeyecek
											approval = null; // approval
																// olmayacak
										else {
											approvalStep = new W5WorkflowStep();
											if (approval.getManualAppUserIds() == null) {
												approvalStep.setApprovalUsers("" + (Integer) scd.get("userId"));
											} else {
												approvalStep.setApprovalUsers(approval.getManualAppUserIds());
											}
											approvalStep.setApprovalRoles(approval.getManualAppRoleIds());
											approvalStep.setApprovalStepId(901); // wait
																					// for
																					// starting
																					// approval
											approvalStep.setSendMailOnEnterStepFlag(approval.getSendMailFlag());
											approvalStep.setSendSmsOnEnterStepFlag(approval.getSendSmsFlag());
										}
										if (advancedStepSqlResult.get("error_msg") != null) // girmeyecek
											throw new IWBException("security", "Workflow", approval.getApprovalId(),
													null, (String) advancedStepSqlResult.get("error_msg"), null);
									}
								} */
								if(approvalStep==null) {
									approvalStep = new W5WorkflowStep();
									// if(approval.getDynamicStepFlag()!=0))
									approvalStep.setApprovalRoles(approval.getManualAppRoleIds());
									approvalStep.setApprovalUsers(approval.getManualAppUserIds());
									if (approval.getManualAppTableFieldIds() != null) { // TODO:
																						// burda
																						// fieldlardan
																						// userlar
																						// alinacak
																						// ve
																						// approvalUsersa
										// eklenecek
										// Object o =
										// formResult.getOutputFields().get(t.get_tableParamList().get(0).getDsc().substring(1));
										// dao.getUsersFromUserFields(PromisCache.getTable(scd,
										// approval.getTableId()),
										// approval.getManualAppTableFieldIds(),
										// scd,
										// o.toString());
									} else if (approvalStep.getApprovalUsers() == null) // TODO:
																						// yanlis
										approvalStep.setApprovalUsers("" + (Integer) scd.get("userId"));

									approvalStep.setApprovalStepId(901); // wait
																			// for
																			// starting
																			// approval
									approvalStep.setSendMailOnEnterStepFlag(approval.getSendMailFlag());
									approvalStep.setSendSmsOnEnterStepFlag(approval.getSendSmsFlag());
								}
							}
							break;
						}
						if (approval != null && (approval.getManualDemandStartAppFlag() == 0
								|| (approval.getManualDemandStartAppFlag() == 1
										&& GenericUtil.uInt(formResult.getRequestParams().get("_aa")) == -1))) { // Onay
																													// Mek
																													// Başlat
							if (approvalStep != null) { // step hazir
								// if(approval.getApprovalStrategyTip()==0)schema
								// =
								// FrameworkCache.getAppSettingStringValue(scd,
								// "approval_schema");
								appRecord = new W5WorkflowRecord((String) scd.get("projectId"));
								appRecord.setApprovalId(approval.getApprovalId());
								appRecord.setApprovalStepId(approvalStep.getApprovalStepId());
								appRecord.setApprovalActionTip((short) 0); // start,approve,return,reject,time_limit_exceed
								appRecord.setTableId(formResult.getForm().getObjectId());
								appRecord.setReturnFlag(approvalStep.getReturnFlag());
								appRecord.setApprovalUsers(advancedStepSqlResult != null
										&& advancedStepSqlResult.get("approval_users") != null
												? (String) advancedStepSqlResult.get("approval_users")
												: approvalStep.getApprovalUsers());
								appRecord.setApprovalRoles(advancedStepSqlResult != null
										&& advancedStepSqlResult.get("approval_roles") != null
												? (String) advancedStepSqlResult.get("approval_roles")
												: approvalStep.getApprovalRoles());
								appRecord.setAccessViewTip(advancedStepSqlResult != null
										&& advancedStepSqlResult.get("access_view_tip") != null
												? (short) GenericUtil.uInt(advancedStepSqlResult.get("access_view_tip"))
												: approvalStep.getAccessViewTip());
								appRecord.setAccessViewRoles(advancedStepSqlResult != null
										&& advancedStepSqlResult.get("access_view_roles") != null
												? (String) advancedStepSqlResult.get("access_view_roles")
												: approvalStep.getAccessViewRoles());
								appRecord.setAccessViewUsers(advancedStepSqlResult != null
										&& advancedStepSqlResult.get("access_view_users") != null
												? (String) advancedStepSqlResult.get("access_view_users")
												: approvalStep.getAccessViewUsers());
								if (appRecord.getAccessViewTip() != 0 && !GenericUtil
										.hasPartInside2(appRecord.getAccessViewUsers(), scd.get("userId"))) // goruntuleme
																											// kisiti
																											// var
																											// ve
																											// kendisi
																											// goremiyorsa,
									// kendisini de ekle
									appRecord.setAccessViewUsers(appRecord.getAccessViewUsers() != null
											? appRecord.getAccessViewUsers() + "," + scd.get("userId")
											: scd.get("userId").toString());
								appRecord.setInsertUserId((Integer) scd.get("userId"));
								appRecord.setVersionUserId((Integer) scd.get("userId"));
								// appRecord.setCustomizationId((Integer)scd.get("customizationId"));
								appRecord.setHierarchicalLevel(0);
							} else {
								throw new IWBException("framework", "Workflow", formId, null,
										LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_hatali_onay_tanimi"),
										null);
							}
						}
					}
				}
				if (action == 2) // 2:insert
					dao.insertFormTable(formResult, paramSuffix);
				else { // 5:copy
					dao.copyFormTable(formResult, schema, paramSuffix, paramSuffix != null && paramSuffix.length() > 0);
					pcopyTablePk = requestParams.get(t.get_tableParamList().get(0).getDsc());
					action = 2;
				}

				if (formResult.getOutputFields() != null && !formResult.getOutputFields().isEmpty()) {
					Object o = formResult.getOutputFields().get(t.get_tableParamList().get(0).getDsc().substring(1));
					// user fieldlardan gelen alanlar.
					/*
					 * if(approval.getManualAppTableFieldIds() != null){
					 * dao.getUsersFromUserFields(PromisCache.getTable(scd,
					 * approval.getTableId()),
					 * approval.getManualAppTableFieldIds(), scd, o.toString());
					 * }
					 */
					if (o != null) {
						ptablePk = o.toString();
						requestParams.put(t.get_tableParamList().get(0).getDsc(), ptablePk);
						if (!GenericUtil.isEmpty(paramSuffix))
							requestParams.put(t.get_tableParamList().get(0).getDsc() + paramSuffix, ptablePk);
					}
				}

				if (FrameworkSetting.workflow && accessControlSelfFlag && formResult.getErrorMap().isEmpty()
						&& appRecord != null) { // aproval baslanmis
					int tablePk = GenericUtil.uInt(formResult.getOutputFields()
							.get(/* formResult.getForm().get_sourceTable() */ FrameworkCache
									.getTable(scd, formResult.getForm().getObjectId()).get_tableFieldList().get(0)
									.getDsc()));
					appRecord.setTablePk(tablePk);
					String summaryText = dao.getSummaryText4Record(scd, t.getTableId(), tablePk);
					appRecord.setDsc(summaryText);
					dao.saveObject(appRecord);
					if (FrameworkSetting.liveSyncRecord)
						formResult.addSyncRecord(new W5SynchAfterPostHelper((String) scd.get("projectId"),
								392 /* w5_approval_record */, "" + appRecord.getApprovalRecordId(),
								(Integer) scd.get("userId"), requestParams.get(".w"), (short) 1));
					Log5WorkflowRecord logRecord = new Log5WorkflowRecord();
					logRecord.setApprovalActionTip((short) 0); // start,
																// approve,
																// return,
																// reject,
																// time_limit_cont
																// ,final_approve,
																// deleted
					logRecord.setUserId((Integer) scd.get("userId"));
					logRecord.setApprovalRecordId(appRecord.getApprovalRecordId());
					logRecord.setApprovalStepId(sourceStepId);
					logRecord.setApprovalId(appRecord.getApprovalId());
					dao.saveObject(logRecord);

					approval = t.get_approvalMap().get((short) 2); // action=1
																	// for
																	// update
					String appRecordUserList = null;
					String appRecordRoleList = null;
					String mesajBody = "";

					List<Object> notificationUsers = dao.executeSQLQuery(
							"select distinct gu.user_id from iwb.w5_User gu where gu.customization_id= ? and gu.user_Id != ? and (gu.user_Id in (select ur.user_Id from iwb.w5_User_Role ur where ur.role_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x)) or gu.user_id in ((select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x)))",
							scd.get("customizationId"), scd.get("userId"), appRecord.getApprovalRoles(),
							appRecord.getApprovalUsers());
					if (notificationUsers != null)
						for (Object o : notificationUsers) {
							dao.saveObject2(
									new Log5Notification(scd, GenericUtil.uInt(o),
											(short) (appRecord.getApprovalStepId() == 901
													&& approval.getApprovalFlowTip() == 3 ? 903 : 6),
									approval.getTableId(), appRecord.getTablePk(), GenericUtil.uInt(scd.get("userId")),
									null, 1), scd);
						}
					/*
					 * Ekstra Bildirim Bilgileri, SMS, EMail ve Notification
					 * yükleniyor SMS Mail Tip -> 0 SMS , 1 E-Mail, 2
					 * Notification
					 */

					Map<Integer, Map> extraInformData = new HashMap<Integer, Map>();

					/* Ekstra bildirim sonu */

					if ((approval != null && approval.getActiveFlag() != 0
							&& ((appRecord.getApprovalStepId() < 900
									&& (approvalStep.getSendMailOnEnterStepFlag() != 0)))
							|| appRecord.getApprovalStepId() > 900)) {
						appRecordUserList = appRecord.getApprovalUsers();
						appRecordRoleList = appRecord.getApprovalRoles();
						List<String> emailList = null;

						/* Bu onay aşamasında mail gönderilecek mi ? */

						if (approvalStep.getSendMailOnEnterStepFlag() != 0) {
							emailList = dao.executeSQLQuery(
									"select gu.email from iwb.w5_user gu where gu.customization_id = ?::integer and gu.user_id in ((select x.satir::integer from iwb.tool_parse_numbers(?,\',\')x)) and gu.user_status = 1 union "
											+ "select (select gu.email from iwb.w5_user gu where gu.customization_id=ur.customization_id and gu.user_id = ur.user_id and gu.user_status=1) from iwb.w5_user_role ur where ur.customization_id = ?::integer and ur.role_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\')x) and "
											+ "((select u.user_tip from iwb.w5_role u where u.role_id = ur.role_id and u.customization_id = ur.customization_id) <> 3 or "
											+ "((select u.user_tip from iwb.w5_role u where u.role_id = ur.role_id and u.customization_id = ur.customization_id) = 3))",
									scd.get("customizationId"), appRecordUserList, scd.get("customizationId"),
									appRecordRoleList);

							/*
							 * if(emailList == null &&
							 * (approval.getApprovalStrategyTip() == 1 ||
							 * approval.getSendMailOnManualStepFlag() == 1)){ //
							 * Eğer manual başlat varsa appRecordUserList =
							 * approval.getManualAppUserIds(); appRecordRoleList
							 * = approval.getManualAppRoleIds(); emailList =
							 * dao.executeSQLQuery(
							 * "select gu.email from iwb.w5_user gu where gu.customization_id=?::integer and gu.user_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\')x) and gu.user_id <> ?::integer union select (select gu.email from iwb.w5_user gu where gu.customization_id=?::integer and gu.user_id = ur.user_id and gu.user_status=1) from iwb.w5_user_role ur where ur.role_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\')x) and ur.user_id <> ?::integer"
							 * ,scd.get("customizationId"),appRecordUserList,scd
							 * .get("userId"),scd.get("customizationId"),
							 * appRecordRoleList,scd.get("userId")); }
							 */

							if (extraInformData.get(1) != null) { // eğer ekstra
																	// bilgilendirilecek
																	// birileri
																	// varsa
								if (emailList == null)
									emailList = new ArrayList<String>();
								List<Object> usersToInform = dao.executeSQLQuery(
										"select gu.email from iwb.w5_user gu where gu.customization_id=?::integer gu.email is not null and gu.user_id in "
												+ "(select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) or gu.user_id in "
												+ "(select ur.user_id from iwb.w5_user_role ur where ur.customization_id = gu.customization_id and ur.role_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\')x))",
										scd.get("customizationId"), extraInformData.get(1).get("users"),
										extraInformData.get(1).get("roles"));
								if (usersToInform != null && usersToInform.size() > 0) {
									for (Object address : usersToInform) {
										if (address != null)
											emailList.add(address.toString());
									}
								}
							}

							if (!GenericUtil.isEmpty(emailList)) {
								String pemailList = "";
								Object[] m = emailList.toArray();
								for (int i = 0; i < m.length; i++) {
									if (m[i] != null)
										pemailList += "," + m[i];
								}
								pemailList = pemailList.substring(1);

								int mail_setting_id = GenericUtil.uInt((Object) scd.get("mailSettingId"));
								if (mail_setting_id == 0)
									mail_setting_id = FrameworkCache.getAppSettingIntValue(scd, "default_outbox_id");

								if (appRecord.getApprovalStepId() == 901) {
									mesajBody = "'" + scd.get("completeName") + "' " + LocaleMsgCache.get2(0,
											(String) scd.get("locale"), "onay_surecini_baslatmanizi_istiyor");
								} else {
									mesajBody = "'" + scd.get("completeName") + "' " + LocaleMsgCache.get2(0,
											(String) scd.get("locale"), "tarafindan_onaya_sunulmustur");
								}

								W5Email email = new W5Email(pemailList, null, null,
										t.get_approvalMap().get((short) 2).getDsc(),
										" (" + summaryText + ") " + mesajBody, null); // mail_keep_body_original
																						// ?
								W5ObjectMailSetting oms = (W5ObjectMailSetting) dao.getCustomizedObject(
										"from W5ObjectMailSetting w where w.mailSettingId=? AND w.customizationId=?",
										(Integer) scd.get("mailSettingId"), scd.get("customizationId"), "MailSetting");
								email.set_oms(oms);

								String sonuc = notificationEngine.sendMail(scd, email);
								if (FrameworkCache.getAppSettingIntValue(0, "mail_debug_flag") != 0) {
									if (sonuc != null) { // basarisiz, queue'ye
															// at//
										System.out.println(LocaleMsgCache.get2(0, (String) scd.get("locale"),
												"onay_mekanizmasi_akisinda_mail_gonderilemedi"));
									} else {
										System.out.println(LocaleMsgCache.get2(0, (String) scd.get("locale"),
												"onay_mekanizmasi_akisinda_mail_gonderildi"));
									}
								}
							}

							// Comment Yazma
							if (!GenericUtil.isEmpty((String) requestParams.get("_adsc"))) {
								W5Comment comment = new W5Comment((String) scd.get("projectId"));
								comment.setTableId(appRecord.getTableId());
								comment.setTablePk(appRecord.getTablePk());
								comment.setDsc(requestParams.get("_adsc") + "");
								comment.setCommentUserId((Integer) scd.get("userId"));
								comment.setCommentDttm(new java.sql.Timestamp(new Date().getTime()));
								dao.saveObject(comment);
							}
						}
					}

					if (approvalStep.getSendSmsOnEnterStepFlag() != 0
							&& (approval != null && approval.getActiveFlag() != 0
									&& (appRecord.getApprovalStepId() < 900) || appRecord.getApprovalStepId() > 900)) {

						/* Bu onay aşamasında sms gönderilecek mi ? */
						if (FrameworkCache.getAppSettingIntValue(scd, "sms_flag") != 0) {
							appRecordUserList = appRecord.getApprovalUsers();
							appRecordRoleList = appRecord.getApprovalRoles();

							List<String> gsmList = dao.executeSQLQuery(
									"select gu.gsm from iwb.w5_user gu where gu.customization_id=? and gu.user_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and gu.user_id <> ? union select (select gu.gsm from iwb.w5_user gu where gu.customization_id=? and gu.user_id = ur.user_id and gu.user_status=1) from iwb.w5_user_role ur where ur.role_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and ur.user_id <> ?",
									scd.get("customizationId"), appRecordUserList, scd.get("userId"),
									scd.get("customizationId"), appRecordRoleList, scd.get("userId"));

							if (gsmList == null && approval.getSendSmsOnManualStepFlag() == 1) { // Eğer
																									// manuel
																									// başlat
																									// varsa
								appRecordUserList = approval.getManualAppUserIds();
								appRecordRoleList = approval.getManualAppRoleIds();
								gsmList = dao.executeSQLQuery(
										"select gu.gsm from iwb.w5_user gu where gu.customization_id=? and gu.user_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and gu.user_id <> ? union select (select gu.gsm from iwb.w5_user gu where gu.customization_id=? and gu.user_id = ur.user_id and gu.user_status=1) from iwb.w5_user_role ur where ur.role_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and ur.user_id <> ?",
										scd.get("customizationId"), appRecordUserList, scd.get("userId"),
										scd.get("customizationId"), appRecordRoleList, scd.get("userId"));
							}

							if (extraInformData.get(0) != null) { // eğer ekstra
																	// sms
																	// gönderilecek
																	// birileri
																	// varsa
								if (gsmList == null)
									gsmList = new ArrayList<String>();
								List<Object> usersToInform = dao.executeSQLQuery(
										"select gu.gsm from iwb.w5_user gu where gu.customization_id=? gu.gsm is not null and gu.user_id in "
												+ "(select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) or gu.user_id in "
												+ "(select ur.user_id from iwb.w5_user_role ur where ur.customization_id = gu.customization_id and ur.role_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x))",
										scd.get("customizationId"), extraInformData.get(0).get("users"),
										extraInformData.get(0).get("roles"));
								if (usersToInform != null && usersToInform.size() > 0) {
									for (Object gsm : usersToInform) {
										if (gsm != null)
											gsmList.add(gsm.toString());
									}
								}
							}

							if (gsmList != null) {
								Object[] m = gsmList.toArray();
								for (int i = 0; i < m.length; i++) {
									notificationEngine.sendSms(Integer.valueOf(String.valueOf(scd.get("customizationId"))),
											Integer.valueOf(String.valueOf(scd.get("userId"))),
											(String) m[i], t.get_approvalMap().get((short) 2).getDsc() + " ("
													+ summaryText + ") " + mesajBody,
											392, appRecord.getApprovalRecordId());
								}
							}
						}
					}

					if (appRecord.getApprovalStepId() != 901)
						formResult.getOutputMessages()
								.add(t.get_approvalMap().get((short) 2).getDsc() + ", "
										+ LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_onaya_sunulmustur")
										+ " (" + summaryText + ")");
					else
						formResult.getOutputMessages()
								.add(t.get_approvalMap().get((short) 2).getDsc() + ", "
										+ LocaleMsgCache.get2(0, (String) scd.get("locale"),
												"manuel_olarak_onay_surecini_baslatabilirsiniz")
										+ " (" + summaryText + ")");
				}
				break;
			case 3: // delete
				ptablePk = requestParams.get(t.get_tableParamList().get(0).getDsc() + paramSuffix);
				if (FrameworkSetting.vcs && t.getVcsFlag() != 0) {
					requestParams.put("_iwb_vcs_dsc",
							dao.getTableRecordSummary(scd, t.getTableId(), GenericUtil.uInt(ptablePk), 32));
				}
				if (FrameworkSetting.workflow && accessControlSelfFlag) {
					if (appRecord != null) { // eger bir approval sureci
												// icindeyse
						Log5WorkflowRecord logRecord = new Log5WorkflowRecord();
						logRecord.setApprovalActionTip((short) 6); // start,
																	// approve,
																	// return,
																	// reject,
																	// time_limit_cont
																	// ,final_approve,
																	// deleted
						logRecord.setUserId((Integer) scd.get("userId"));
						logRecord.setApprovalRecordId(appRecord.getApprovalRecordId());
						logRecord.setApprovalStepId(appRecord.getApprovalStepId());
						logRecord.setApprovalId(appRecord.getApprovalId());
						dao.saveObject(logRecord);
						dao.removeObject(appRecord); // TODO:aslinda bir de loga
														// atmali bunu
						appRecord = null;
					} else if (t.get_approvalMap() != null) { // onay
																// mekanizmasi
																// var mi bunda?
						approval = t.get_approvalMap().get((short) 3); // action=2
																		// for
																		// delete
						if (approval != null && approval.getActiveFlag() != 0
								&& approval.getApprovalRequestTip() >= 1) { // insert
																			// approval
																			// mekanizmasi
																			// var
																			// ve
																			// automatic
							Map<String, Object> advancedStepSqlResult = null;
							switch (approval.getApprovalRequestTip()) { // eger
																		// approval
																		// olacaksa
							case 1: // automatic approval
								if (approval.getAdvancedBeginSql() != null
										&& approval.getAdvancedBeginSql().length() > 10) { // calisacak
									Object[] oz = DBUtil.filterExt4SQL(approval.getAdvancedBeginSql(), scd,
											requestParams, null);
									advancedStepSqlResult = dao.runSQLQuery2Map(oz[0].toString(), (List) oz[1], null);
									// donen bir cevap var, aktive_flag deger
									// olarak var ve onun degeri 0 ise o
									// zaman girmeyecek
									if (advancedStepSqlResult != null) {
										if (advancedStepSqlResult.get("active_flag") != null
												&& GenericUtil.uInt(advancedStepSqlResult.get("active_flag")) == 0) // girmeyecek
											approval = null; // approval
																// olmayacak
										if (advancedStepSqlResult.get("error_msg") != null) // girmeyecek
											throw new IWBException("security", "Workflow", approval.getApprovalId(),
													null, (String) advancedStepSqlResult.get("error_msg"), null);
									}
								}
								approvalStep = null;
								switch (approval.getApprovalFlowTip()) { // simple
								case 0: // basit onay
									approvalStep = approval.get_approvalStepList().get(0).getNewInstance();
									break;
								case 1: // complex onay
									if (advancedStepSqlResult != null
											&& advancedStepSqlResult.get("approval_step_id") != null
											&& GenericUtil.uInt(advancedStepSqlResult.get("approval_step_id")) != 0)
										approvalStep = approval.get_approvalStepMap()
												.get(GenericUtil.uInt(advancedStepSqlResult.get("approval_step_id")));
									else
										approvalStep = approval.get_approvalStepList().get(0).getNewInstance();
									break;
								case 2: // hierarchical onay
									int mngUserId = GenericUtil.uInt(scd.get("mngUserId"));
									if (mngUserId != 0) {
										approvalStep = new W5WorkflowStep();
										approvalStep.setApprovalUsers("" + mngUserId);
										approvalStep.setApprovalStepId(902);
									} else { // direk duz approval kismine
												// geciyor:TODO
										if (approval.get_approvalStepList() != null
												&& !approval.get_approvalStepList().isEmpty())
											approvalStep = approval.get_approvalStepList().get(0).getNewInstance();
										else
											approvalStep = null;
									}

									break;
								}

								break;
							}

							if (approvalStep != null) { // step hazir
								appRecord = new W5WorkflowRecord();
								appRecord.setApprovalId(approval.getApprovalId());
								appRecord.setApprovalStepId(approvalStep.getApprovalStepId());
								appRecord.setApprovalActionTip((short) 0); // start,approve,return,reject,time_limit_exceed
								appRecord.setTableId(formResult.getForm().getObjectId());
								appRecord.setReturnFlag(approvalStep.getReturnFlag());
								appRecord.setApprovalUsers(advancedStepSqlResult != null
										&& advancedStepSqlResult.get("approval_users") != null
												? (String) advancedStepSqlResult.get("approval_users")
												: approvalStep.getApprovalUsers());
								appRecord.setApprovalRoles(advancedStepSqlResult != null
										&& advancedStepSqlResult.get("approval_roles") != null
												? (String) advancedStepSqlResult.get("approval_roles")
												: approvalStep.getApprovalRoles());
								appRecord.setAccessViewTip(advancedStepSqlResult != null
										&& advancedStepSqlResult.get("access_view_tip") != null
												? (short) GenericUtil.uInt(advancedStepSqlResult.get("access_view_tip"))
												: approvalStep.getAccessViewTip());
								appRecord.setAccessViewRoles(advancedStepSqlResult != null
										&& advancedStepSqlResult.get("access_view_roles") != null
												? (String) advancedStepSqlResult.get("access_view_roles")
												: approvalStep.getAccessViewRoles());
								appRecord.setAccessViewUsers(advancedStepSqlResult != null
										&& advancedStepSqlResult.get("access_view_users") != null
												? (String) advancedStepSqlResult.get("access_view_users")
												: approvalStep.getAccessViewUsers());
								if (appRecord.getAccessViewTip() != 0 && !GenericUtil
										.hasPartInside2(appRecord.getAccessViewUsers(), scd.get("userId"))) // goruntuleme
																											// kisiti
																											// var
																											// ve
																											// kendisi
																											// goremiyorsa,
									// kendisini de ekle
									appRecord.setAccessViewUsers(appRecord.getAccessViewUsers() != null
											? appRecord.getAccessViewUsers() + "," + scd.get("userId")
											: scd.get("userId").toString());
								appRecord.setInsertUserId((Integer) scd.get("userId"));
								appRecord.setVersionUserId((Integer) scd.get("userId"));
								// appRecord.setCustomizationId((Integer)scd.get("customizationId"));
							} else {
								throw new IWBException("framework", "Workflow", formId, null,
										LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_hatali_onay_tanimi"),
										null);
							}
						}
					}
				}

				if (appRecord == null && FrameworkSetting.feed && t.getShowFeedTip() != 0
						&& (t.getTableId() != 671 && t.getTableId() != 329 && t.getTableId() != 44)) { // TODO:
																										// delete
																										// icin
																										// onceden
																										// bakmak
																										// lazim
																										// yoksa
																										// gidecek
																										// kayit
					feed = new Log5Feed(scd);
					feed.set_showFeedTip(t.getShowFeedTip());
					switch (feed.get_showFeedTip()) {
					case 2: // master
						feed.setFeedTip((short) 3); // remove:master icin
						feed.setTableId(feedTableId = t.getTableId());
						feed.setTablePk(feedTablePk = GenericUtil.uInt(ptablePk));
						break;
					case 1: // detail
						feed.setFeedTip((short) 4); // edit:detaya gore
						feed.setDetailTableId(feedTableId = t.getTableId());
						feed.setDetailTablePk(feedTablePk = GenericUtil.uInt(ptablePk));
						break;
					}
					if (feed != null) {
						feed.set_tableRecordList(dao.findRecordParentRecords(scd, feedTableId, feedTablePk, 0, true));
						if (feed.get_tableRecordList() != null && feed.get_tableRecordList().size() > 1
								&& feed.get_showFeedTip() == 1) {
							W5TableRecordHelper trh = feed.get_tableRecordList().get(1);
							feed.setTableId(trh.getTableId());
							feed.setTablePk(trh.getTablePk());
							feed.set_commentCount(trh.getCommentCount());
						} else if (feed.get_tableRecordList() != null && feed.get_tableRecordList().size() > 0)
							feed.set_commentCount(feed.get_tableRecordList().get(0).getCommentCount());
					}
				}
				Map<String, String> mz = new HashMap();
				mz.put("ptable_id", "" + t.getTableId());
				mz.put("ptable_pk", ptablePk);
				// W5GlobalFuncResult dfr = executeFunc(scd, 690, mz,
				// (short)2);//bu kaydin child
				// kayitlari var mi? iwb.w5_table_field'daki default_control_tip
				// ve
				// default_lookup_table_id'ye bakiliyor
				if (ptablePk != null && appRecord == null) {
					boolean b = dao.deleteTableRecord(formResult, paramSuffix);
					if (!b)
						formResult.getOutputMessages().add(LocaleMsgCache.get2(scd, "record_not_found"));
				}
				if (formResult.getErrorMap().isEmpty()) {
					// FrameworkCache.removeTableCacheValue(t.getCustomizationId(),
					// t.getTableId(),GenericUtil.uInt(requestParams.get(t.get_tableParamList().get(0).getDsc()+paramSuffix)));//caching
					// icin

					if (FrameworkSetting.workflow && appRecord != null) { // aproval
																			// baslanmis
						int tablePk = GenericUtil.uInt(ptablePk);
						appRecord.setTablePk(tablePk);
						String summaryText = dao.getSummaryText4Record(scd, t.getTableId(), tablePk);
						appRecord.setDsc(summaryText);
						dao.saveObject(appRecord);
						if (FrameworkSetting.liveSyncRecord)
							formResult.addSyncRecord(new W5SynchAfterPostHelper((String) scd.get("projectId"),
									392 /* w5_approval_record */, "" + appRecord.getApprovalRecordId(),
									(Integer) scd.get("userId"), requestParams.get(".w"), (short) 1));

						Log5WorkflowRecord logRecord = new Log5WorkflowRecord();
						logRecord.setApprovalActionTip((short) 0); // start,
																	// approve,
																	// return,
																	// reject,
																	// time_limit_cont
																	// ,final_approve,
																	// deleted
						logRecord.setUserId((Integer) scd.get("userId"));
						logRecord.setApprovalRecordId(appRecord.getApprovalRecordId());
						logRecord.setApprovalStepId(sourceStepId);
						logRecord.setApprovalId(appRecord.getApprovalId());
						dao.saveObject(logRecord);

						approval = t.get_approvalMap().get((short) 3); // action=3
																		// for
																		// delete
						String appRecordUserList = null;
						String appRecordRoleList = null;
						String mesajBody = "";

						if ((approval != null && approval.getActiveFlag() != 0
								&& ((appRecord.getApprovalStepId() < 900
										&& approvalStep.getSendMailOnEnterStepFlag() != 0))
								|| appRecord.getApprovalStepId() > 900)) {

							appRecordUserList = appRecord.getApprovalUsers();
							appRecordRoleList = appRecord.getApprovalRoles();
							List<String> emailList = dao.executeSQLQuery(
									"select gu.email from iwb.w5_user gu where gu.customization_id=? and gu.user_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and gu.user_id <> ? union select (select gu.email from iwb.w5_user gu where gu.customization_id=? and gu.user_id = ur.user_id and gu.user_status=1) from iwb.w5_user_role ur where ur.role_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and ur.user_id <> ?",
									scd.get("customizationId"), appRecordUserList, scd.get("userId"),
									scd.get("customizationId"), appRecordRoleList, scd.get("userId"));

							if (emailList.size() > 0) {
								String pemailList = "";
								Object[] m = emailList.toArray();
								for (int i = 0; i < m.length; i++)
									pemailList += "," + m[i];
								pemailList = pemailList.substring(1);

								int mail_setting_id = GenericUtil.uInt((Object) scd.get("mailSettingId"));
								if (mail_setting_id == 0)
									mail_setting_id = FrameworkCache.getAppSettingIntValue(scd, "default_outbox_id");

								if (appRecord.getApprovalStepId() == 901) {
									mesajBody = "'" + scd.get("completeName") + "' " + LocaleMsgCache.get2(0,
											(String) scd.get("locale"), "onay_surecini_baslatmanizi_istiyor");
								} else {
									mesajBody = "'" + scd.get("completeName") + "' " + LocaleMsgCache.get2(0,
											(String) scd.get("locale"), "tarafindan_onaya_sunulmustur");
								}

								W5Email email = new W5Email(pemailList, null, null,
										t.get_approvalMap().get((short) 2).getDsc(),
										" (" + summaryText + ") " + mesajBody, null); // mail_keep_body_original
																						// ?
								W5ObjectMailSetting oms = (W5ObjectMailSetting) dao.getCustomizedObject(
										"from W5ObjectMailSetting w where w.mailSettingId=? AND w.customizationId=?",
										(Integer) scd.get("mailSettingId"), scd.get("customizationId"), "MailSetting");
								email.set_oms(oms);
								String sonuc = notificationEngine.sendMail(scd, email);
								if (FrameworkCache.getAppSettingIntValue(0, "mail_debug_flag") != 0) {
									if (sonuc != null) { // basarisiz, queue'ye
															// at//
										System.out.println(LocaleMsgCache.get2(0, (String) scd.get("locale"),
												"onay_mekanizmasi_akisinda_mail_gonderilemedi"));
									} else {
										System.out.println(LocaleMsgCache.get2(0, (String) scd.get("locale"),
												"onay_mekanizmasi_akisinda_mail_gonderildi"));
									}
								}
							}
						}

						if ((approval != null && approval.getActiveFlag() != 0
								&& ((appRecord.getApprovalStepId() < 900
										&& approvalStep.getSendSmsOnEnterStepFlag() != 0))
								|| appRecord.getApprovalStepId() > 900)) {
							if (FrameworkCache.getAppSettingIntValue(scd, "sms_flag") != 0) {
								appRecordUserList = appRecord.getApprovalUsers();
								appRecordRoleList = appRecord.getApprovalRoles();

								List<Object[]> gsmList = dao.executeSQLQuery(
										"select gu.gsm from iwb.w5_user gu where gu.customization_id=? and gu.user_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and gu.user_id <> ? union select (select gu.gsm from iwb.w5_user gu where gu.customization_id=? and gu.user_id = ur.user_id and gu.user_status=1) from iwb.w5_user_role ur where ur.role_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and ur.user_id <> ?",
										scd.get("customizationId"), appRecordUserList, scd.get("userId"),
										scd.get("customizationId"), appRecordRoleList, scd.get("userId"));
								if (gsmList != null) {
									Object[] m = gsmList.toArray();
									for (int i = 0; i < m.length; i++) {
										notificationEngine.sendSms(Integer.valueOf(String.valueOf(scd.get("customizationId"))),
												Integer.valueOf(String.valueOf(scd.get("userId"))),
												(String) m[i], t.get_approvalMap().get((short) 2).getDsc() + " ("
														+ summaryText + ") " + mesajBody,
												392, appRecord.getApprovalRecordId());
									}
								}
							}
						}

						if (appRecord.getApprovalStepId() != 901)
							formResult.getOutputMessages()
									.add(t.get_approvalMap().get((short) 3).getDsc() + ", "
											+ LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_onaya_sunulmustur")
											+ " (" + summaryText + ")");
						else
							formResult.getOutputMessages()
									.add(t.get_approvalMap().get((short) 2).getDsc() + ", "
											+ LocaleMsgCache.get2(0, (String) scd.get("locale"),
													"islemlerinizi_tamamlayip_manuel_olarak_onay_surecini_baslatabilirsiniz")
									+ " (" + summaryText + ")");
					}

					// TODO dao.executeUpdateSQLQuery("delete from
					// iwb.w5_converted_object co where
					// co.customization_id=? AND co.DST_TABLE_PK=? AND
					// exists(select 1 from
					// iwb.w5_conversion c where
					// c.customization_id=co.customization_id AND
					// c.DST_TABLE_ID=?
					// AND co.conversion_id=c.conversion_id)",
					// scd.get("customizationId"), ptablePk,
					// t.getTableId());
				}

				break;
			default: // sorun var
				throw new IWBException("validation", "Action", action, null,
						LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_wrong_form_action"), null);
			}

			if (formResult.getErrorMap().isEmpty()) { // sorun yok

				/*
				 * if((action==1 || action==2) && t.getTableId()==15){ //TODO:
				 * simdilik daha yavas calistigi tespit edildi, o yuzden
				 * vazgecildi
				 * dao.createTableAuditDefinition(scd,PromisUtil.uInt(ptablePk))
				 * ; }
				 */

				boolean bc = false; // boolean copy
				if (realAction == 5 && formResult.getForm().getObjectTip() == 2
						&& /* formResult.getForm().get_sourceTable() */ FrameworkCache
								.getTable(scd, formResult.getForm().getObjectId()).get_tableChildList() != null) { // copy
																													// ise
																													// o
																													// zaman
																													// detay
																													// var
																													// ise
																													// onlari
																													// da
																													// kopyala
					String copyTblIds = requestParams.get("_copy_tbl_ids");
					for (W5TableChild tc : /*
											 * formResult.getForm().
											 * get_sourceTable()
											 */
					FrameworkCache.getTable(scd, formResult.getForm().getObjectId()).get_tableChildList())
						if (tc.getCopyStrategyTip() != 0 && (tc.getCopyStrategyTip() == 1
								|| GenericUtil.hasPartInside2(copyTblIds, "" + tc.getRelatedTableId()))) {
							dao.copyFormTableDetail(formResult, tc, null, schema, paramSuffix);
							bc = true;
						}
				}

				// detail records kaydet
				boolean bd = false;
				if (paramSuffix.length() == 0 && (action == 1 || action == 2)) { // detail
																					// kaydet
					for (int qi = 1; GenericUtil.uInt(requestParams.get("_fid" + qi)) != 0; qi++)
						try {
							if (qi == 1) {
								requestParams.putAll((Map) formResult.getOutputFields());
								checkedParentRecords.add(formResult.getForm().getObjectId() + "^" + ptablePk);
							}
							int detailFormId = GenericUtil.uInt(requestParams.get("_fid" + qi));
							int dirtyCount = GenericUtil.uInt(requestParams.get("_cnt" + qi));
							if (dirtyCount == 0)
								continue;
							Map<String, String> fieldConMap = new HashMap();
							for (String s1 : requestParams.keySet())
								if (s1.startsWith("_fid" + qi + "_con_")) {
									fieldConMap.put(s1.substring(10), requestParams.get(s1));
								}
							if (detailFormId > 0) {
								if (!fieldConMap.isEmpty()) {
									for (String key : fieldConMap.keySet())
										if (formResult.getOutputFields().containsKey(fieldConMap.get(key)))
											for (int qi2 = 1; qi2 <= dirtyCount; qi2++)
												requestParams.put(key + qi + "." + qi2, formResult.getOutputFields()
														.get(fieldConMap.get(key)).toString());
								}
								if (formResult.getOutputFields() != null) {
									for (String key : formResult.getOutputFields().keySet())
										for (int qi2 = 1; qi2 <= dirtyCount; qi2++)
											requestParams.put(key + qi + "." + qi2,
													formResult.getOutputFields().get(key).toString());
								}
								W5FormResult detailForm = postEditGrid4Table(scd, detailFormId, dirtyCount,
										requestParams, qi + ".", checkedParentRecords);
								if (!GenericUtil.isEmpty(detailForm.getQueueActionList()))
									result.addAll(detailForm.getQueueActionList());
								if (!GenericUtil.isEmpty(detailForm.getOutputFields()))
									formResult.getOutputFields().put("_fid" + qi, detailForm.getOutputFields());
								if (FrameworkSetting.liveSyncRecord)
									formResult.addSyncRecordAll(detailForm.getListSyncAfterPostHelper());
								bd = true;
							} else {
								scriptEngine.postEditGridGlobalFunc(scd, -formId, dirtyCount, requestParams, qi + ".");
								bd = true;
							}
						} catch (Exception e) {
							throw new IWBException("framework", "postForm(detail)",
									GenericUtil.uInt(requestParams.get("_fid" + qi)), null,
									"[40," + GenericUtil.uInt(requestParams.get("_fid" + qi)) + "]", e);
						}
				}
				/*
				 * if(t.getCrudGlobalFuncId()!=0 && ((action==2 &&
				 * GenericUtil.hasPartInside(t.getCrudActions(),"xi") ||
				 * (action==1 &&
				 * GenericUtil.hasPartInside(t.getCrudActions(),"xu"))))){
				 * W5GlobalFuncResult dbFuncResult =
				 * dao.getGlobalFuncResult(formResult.getScd(),
				 * t.getCrudGlobalFuncId()); dbFuncResult.setErrorMap(new
				 * HashMap()); Map m = new HashMap();
				 * m.putAll(formResult.getRequestParams()); for(String
				 * key:formResult.getOutputFields().keySet())m.put("t"+key,
				 * formResult.getOutputFields().get(key).toString());
				 * m.put("triggerAction", action==2 ? "xi":"xu");//trigger
				 * action dbFuncResult.setRequestParams(m);
				 * dao.executeGlobalFunc(dbFuncResult,"");
				 * if(dbFuncResult.getErrorMap().isEmpty() &&
				 * dbFuncResult.getResultMap()!=null)formResult.getOutputFields(
				 * ).putAll(dbFuncResult.getResultMap()); }
				 */
				// approval
				if (FrameworkSetting.workflow && GenericUtil.uInt(requestParams.get("_arid" + paramSuffix)) != 0) { // kaydet
																													// ve
																													// approve
																													// et???
					workflowEngine.approveRecord(scd, GenericUtil.uInt(requestParams.get("_arid" + paramSuffix)),
							GenericUtil.uInt(requestParams.get("_aa" + paramSuffix)), requestParams);
				}

				/* tableTrigger After Action start */
				if (tla != null)
					extFormTableEvent(formResult, new String[] { "_", "au", "ai", "ad", "_", "ai" }[action], scd,
							requestParams, t, ptablePk, paramSuffix);
						/* end of tableTrigger */

				/* form conversion */
				conversionEngine.extFormConversion(formResult, paramSuffix, action, scd, requestParams, t, ptablePk,
						false);
						/* end of form conversion */

				/* alarm start */
				notificationEngine.extFormAlarm(formResult, action, scd, requestParams, t, mobile, ptablePk);
				/* end of alarm */

				/* sms/mail customized templates */
				notificationEngine.extFormSmsMail(formResult, result, action, scd, requestParams, t, mobile, ptablePk);
				/* end of sms/mail customized templates */

				/* vcs control */
				extFormVcsControl(formResult, action, scd, requestParams, t, ptablePk);
				/* end of vcs */

				if (action == 2) { // bir sorun yoksa, o zaman conversion kaydi
									// yap
					if (GenericUtil.isEmpty(paramSuffix) && requestParams.containsKey("_cnvId")
							&& requestParams.containsKey("_cnvTblPk")) { // conversion
																			// var
																			// burda
						int conversionId = GenericUtil.uInt(requestParams.get("_cnvId"));
						int conversionTablePk = GenericUtil.uInt(requestParams.get("_cnvTblPk"));
						List<W5Conversion> lcnv = dao.find(
								"from W5Conversion x where x.conversionId=? AND x.projectUuid=?", conversionId,
								(String) scd.get("projectId"));
						if (lcnv.size() == 1 && lcnv.get(0).getDstFormId() == formId) { // bu
																						// form'a
																						// aitmis
																						// conversion
							W5Conversion cnv = lcnv.get(0);
							W5ConvertedObject co = new W5ConvertedObject(scd, conversionId, conversionTablePk,
									GenericUtil.uInt(ptablePk));
							dao.saveObject(co);
							if (cnv.getIncludeFileAttachmentFlag() != 0) {
								dao.executeUpdateSQLQuery("{call pcopy_file_attach( ?, ? , ?, ?, ?); }",
										(Integer) scd.get("userRoleId"), cnv.getSrcTableId(), conversionTablePk,
										cnv.getDstTableId(), GenericUtil.uInt(ptablePk));
							}
							if (!GenericUtil.isEmpty(cnv.getRhinoCode())) {
								scriptEngine.executeScript(scd, requestParams, cnv.getRhinoCode(), null, "707r"+cnv.getConversionId());
							}
						}
					}
				}

				/* feed */
//				extFormFeed(formResult, scd, requestParams, t, ptablePk, feed, feedTableId, feedTablePk);
				/* end of feed */
			}
			PostFormTrigger.afterPostForm(formResult, dao, paramSuffix);

			if (FrameworkSetting.liveSyncRecord && formResult.getErrorMap().isEmpty() && formResult.getForm() != null
					&& formResult.getForm().getObjectTip() == 2) {
				int userId = (Integer) formResult.getScd().get("userId");
				// int customizationId =
				// (Integer)formResult.getScd().get("customizationId");
				t = FrameworkCache.getTable(formResult.getScd(), formResult.getForm().getObjectId());
				String webPageId = formResult.getRequestParams().get(".w");
				if (t.getLiveSyncFlag() != 0 && webPageId != null) {
					String key = "";
					if (formResult.getAction() == 1 || formResult.getAction() == 3) {
						for (String k : formResult.getPkFields().keySet())
							if (!k.startsWith("customization"))
								key += "*" + formResult.getPkFields().get(k);
						key = formResult.getForm().getObjectId() + "-" + key.substring(1);
						formResult.setLiveSyncKey(key);
					}

					// formResult.addSyncRecord(new
					// W5SynchAfterPostHelper(customizationId,
					// t.getTableId(), key, userId, webPageId,
					// (short)formResult.getAction())); //TODO

				}
			}

			if (false && !GenericUtil.isEmpty(formResult
					.getMapWidgetCount()) /*
											 * && !PromisUtil.isEmpty(request.
											 * getParameter("_promis_token"))
											 */) {
				UserUtil.publishWidgetStatus(scd, formResult.getMapWidgetCount());
			}

			return result;
		} catch (Exception e) {
			throw new IWBException("framework", "postForm", formResult.getFormId(), null,
					"[40," + formResult.getFormId() + "] " + formResult.getForm().getDsc(), e);
		}
	}


	public W5FormResult postEditGrid4Table(Map<String, Object> scd, int formId, int dirtyCount,
			Map<String, String> requestParams, String prefix, Set<String> checkedParentRecords) {
		List<W5QueuedActionHelper> queuedGlobalFuncList = new ArrayList<W5QueuedActionHelper>();

		W5FormResult formResult = metaDataDao.getFormResult(scd, formId, 2, requestParams);
		W5Table t = FrameworkCache.getTable(scd, formResult.getForm().getObjectId()); // formResult.getForm().get_sourceTable();
		if (t.getAccessViewTip() == 0 && !FrameworkCache.roleAccessControl(scd, 0)) {
			throw new IWBException("security", "Module", 0, null,
					LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_modul_kontrol"), null);
		}
		if (t.getAccessViewUserFields() == null && !GenericUtil.accessControl(scd, t.getAccessViewTip(),
				t.getAccessViewRoles(), t.getAccessViewUsers())) {
			throw new IWBException("security", "Form", formId, null,
					LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_tablo_kontrol_goruntuleme"), null);
		}
		Map<String, Object> tmpOutputFields = new HashMap<String, Object>();
		for (int id = 1; id <= dirtyCount; id++) {
			formResult.setAction(GenericUtil.uInt(requestParams.get("a" + prefix + id)));
			queuedGlobalFuncList.addAll(postForm4Table(formResult, prefix + id, checkedParentRecords));

			if (!formResult.getErrorMap().isEmpty()) {
				throw new IWBException("validation", "Form", formId, null,
						"Detay Mazgal Veri Geçerliliği("
								+ LocaleMsgCache.get2((Integer) scd.get("customizationId"), (String) scd.get("locale"),
										formResult.getForm().getLocaleMsgKey())
								+ "): " + GenericUtil.fromMapToJsonString(formResult.getErrorMap()),
						null);
			} else if (!GenericUtil.isEmpty(formResult.getOutputFields()))
				for (String key : formResult.getOutputFields().keySet()) {
					tmpOutputFields.put(key + prefix + id, formResult.getOutputFields().get(key));
					// Burada değişiklik var 29.06.2016
					tmpOutputFields.put(key + prefix + id, formResult.getOutputFields().get(key));
					// Detayın detayı kaydediliyor
					for (int i = 1; requestParams.containsKey("_fid" + prefix + id + "_" + i + "." + 1); i++) {
						String subPrefix = prefix + id + "_" + i + ".";
						int fid = GenericUtil.uInt(requestParams.get("_fid" + subPrefix + 1));
						// Master Primary Key
						requestParams.put(key + subPrefix + 1, formResult.getOutputFields().get(key).toString());
						// Master of Master Primary Key //
						if (requestParams.containsKey("root_pk")) {
							String pk = "";
							Object rootPk = requestParams.get(requestParams.get("root_pk"));
							if (rootPk instanceof BigDecimal) {
								pk = String.valueOf((BigDecimal) rootPk);
							} else {
								pk = rootPk.toString();
							}
							requestParams.put(requestParams.get("root_pk") + subPrefix + 1, pk);
						}
						W5FormResult fr = postEditGrid4Table(scd, fid, 1, requestParams, subPrefix,
								new HashSet<String>());
						if (!fr.getErrorMap().isEmpty()) {
							throw new IWBException("validation", "Form", fid, null,
									"Detay Mazgal Veri Geçerliliği("
											+ LocaleMsgCache.get2((Integer) scd.get("customizationId"),
													(String) scd.get("locale"), fr.getForm().getLocaleMsgKey())
											+ "): " + GenericUtil.fromMapToJsonString(fr.getErrorMap()),
									null);
						}
					}
				}
		}
		if (!GenericUtil.isEmpty(tmpOutputFields))
			formResult.setOutputFields(tmpOutputFields);
		/*
		 * if(t.getCrudGlobalFuncId()!=0 &&
		 * GenericUtil.hasPartInside(t.getCrudActions(),"ap")){
		 * W5GlobalFuncResult dbFuncResult =
		 * dao.getGlobalFuncResult(formResult.getScd(),
		 * t.getCrudGlobalFuncId()); dbFuncResult.setErrorMap(new HashMap());
		 * Map m = new HashMap(); m.putAll(formResult.getRequestParams());
		 * for(String key:formResult.getOutputFields().keySet())m.put("t"+key,
		 * formResult.getOutputFields().get(key).toString());
		 * m.put("triggerAction", "ap");//trigger action
		 * dbFuncResult.setRequestParams(m);
		 * dao.executeGlobalFunc(dbFuncResult,"");
		 * if(dbFuncResult.getErrorMap().isEmpty() &&
		 * dbFuncResult.getResultMap()!=null)formResult.getOutputFields().putAll
		 * (dbFuncResult.getResultMap()); }
		 */
		formResult.setQueuedActionList(queuedGlobalFuncList);
		if (formResult.getOutputMessages() != null && formResult.getOutputMessages().isEmpty())
			formResult.getOutputMessages().add("Toplam " + dirtyCount + " adet işlem gerçekleşti.");
		return formResult;
	}

	public void extFormTableEvent(W5FormResult formResult, String action, Map<String, Object> scd,
			Map<String, String> requestParams, W5Table t, String ptablePk, String prefix) {
		List<W5TableEvent> tla = FrameworkCache.getTableEvents(scd, t.getTableId());
		if (tla == null)
			return;
		Map<String, String> newRequestParam = GenericUtil.isEmpty(prefix) ? requestParams : null;
		for (W5TableEvent ta : tla)
			if (GenericUtil.hasPartInside2(ta.getLkpTriggerActions(), action)) {
				if (ta.getLkpCodeType() == 1) { // javascript
					if (newRequestParam == null) {
						newRequestParam = new HashMap();
						if (!GenericUtil.isEmpty(requestParams))
							for (String key : requestParams.keySet())
								if (key != null && key.endsWith(prefix)) {
									newRequestParam.put(key.substring(0, key.length() - prefix.length()),
											requestParams.get(key));
								}
					}
					scriptEngine.executeTableEvent(ta, formResult, action, scd, newRequestParam, t, ptablePk);
					
				} else if (ta.getLkpCodeType() == 4)
					try { // sql
						Map<String, Object> obj = new HashMap();
						obj.put("triggerAction", action);
						Map<String, Object> m = dao.runSQLQuery2Map(ta.getTriggerCode(), scd, requestParams, obj);
						if (m != null) {
							String msg = LocaleMsgCache.get2(scd, ta.getDsc());
							if (m.get("result") != null)
								msg = m.get("result").toString();
							short resultAction = ta.getLkpResultAction();
							if (m.containsKey("resultAction"))
								resultAction = (short) GenericUtil.uInt(m.get("resultAction"));
							switch (resultAction) {
							case 1: // readonly
								formResult.setViewMode(true);
							case 0: // continue
								formResult.getOutputMessages().add(msg);
								break;
							case 2: // confirm & continue
								if (!requestParams.containsKey("_confirmId_" + ta.getTableTriggerId()))
									throw new IWBException("confirm", "ConfirmId", ta.getTableTriggerId(), null, msg,
											null);
								break;
							case 3: // stop with message
								throw new IWBException("security", "TableTrigger", ta.getTableTriggerId(), null, msg,
										null);
							}
						}
					} catch (Exception e) {
						throw new IWBException("sql", "Event", ta.getTableTriggerId(), ta.getTriggerCode(),
								"[1209," + ta.getTableTriggerId() + "] " + ta.getDsc(), e);
					}
			}
	}


	public W5FormResult postForm4Table(Map<String, Object> scd, int formId, int action,
			Map<String, String> requestParams, String prefix) {
		if (formId == 0 && GenericUtil.uInt(requestParams.get("_tb_id")) != 0) {
			W5Table t = FrameworkCache.getTable(scd, GenericUtil.uInt(requestParams.get("_tb_id")));
			formId = t.getDefaultUpdateFormId();
			requestParams.put(t.get_tableParamList().get(0).getDsc(), requestParams.get("_tb_pk"));
			action = 1;
			requestParams.put("a", "1");
		}
		W5FormResult mainFormResult = metaDataDao.getFormResult(scd, formId, action, requestParams);
		boolean dev = scd.get("roleId") != null && (Integer) scd.get("roleId") == 0
				&& GenericUtil.uInt(requestParams, "_dev") != 0;
		W5Table t = FrameworkCache.getTable(scd, mainFormResult.getForm().getObjectId()); // mainFormResult.getForm().get_sourceTable();
		if (t.getAccessViewTip() == 0
				&& (!FrameworkCache.roleAccessControl(scd, 0) || !FrameworkCache.roleAccessControl(scd, action))) {
			throw new IWBException("security", "Module", 0, null,
					LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_modul_kontrol"), null);
		}
		if (t.getAccessViewUserFields() == null && !GenericUtil.accessControl(scd, t.getAccessViewTip(),
				t.getAccessViewRoles(), t.getAccessViewUsers())) {
			throw new IWBException("security", "Form", formId, null,
					LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_tablo_kontrol_goruntuleme"), null);
		}
		Set<String> checkedParentRecords = new HashSet<String>();
		mainFormResult.setQueuedActionList(postForm4Table(mainFormResult, prefix, checkedParentRecords));
		if (!mainFormResult.getErrorMap().isEmpty())
			return mainFormResult;
		requestParams.remove("_fid1"); // TODO: daha iyisi yapılana kadar en
										// iyisi bu. Form extended işleminde
										// a1.1 gibi
		// değerler bir kez daha gönderiliyordu.
		if (mainFormResult.getForm().get_moduleList() != null) {
			for (W5FormModule m : mainFormResult.getForm().get_moduleList())
				if (m.getModuleTip() == 4 && GenericUtil.accessControl(scd, m.getAccessViewTip(),
						m.getAccessViewRoles(), m.getAccessViewUsers())) { // form
																			// imis
					if (m.getModuleViewTip() == 0 || (m.getModuleViewTip() == 1 && action == 1)
							|| (m.getModuleViewTip() == 2 && action == 2)) {
						int newAction = GenericUtil.uInt(requestParams.get("a" + m.getTabOrder()));
						if (newAction == 0)
							newAction = action;
						W5FormResult subFormResult = metaDataDao.getFormResult(scd, m.getObjectId(), newAction, requestParams);
						t = FrameworkCache.getTable(scd, mainFormResult.getForm().getObjectId()); // mainFormResult.getForm().get_sourceTable();
						if ((t.getAccessViewTip() == 0 && !FrameworkCache.roleAccessControl(scd, 0))
								|| (!GenericUtil.accessControl(scd, t.getAccessViewTip(), t.getAccessViewRoles(),
										t.getAccessViewUsers()))) {
							// nothing
						} else {
							postForm4Table(subFormResult, prefix, checkedParentRecords);
							if (!subFormResult.getErrorMap().isEmpty()) {
								throw new IWBException("validation", "Form", m.getObjectId(), null,
										LocaleMsgCache.get2(0, (String) scd.get("locale"),
												"fw_validation_error_detail_form") + ": "
										+ GenericUtil.fromMapToJsonString(subFormResult.getErrorMap()), null);
							}
							if (!GenericUtil.isEmpty(subFormResult.getOutputFields())
									&& !GenericUtil.isEmpty(mainFormResult.getOutputFields()))
								mainFormResult.getOutputFields().putAll(subFormResult.getOutputFields());
						}
					}
				}
		}
		if ((action == 1 || action == 3) && FrameworkSetting.liveSyncRecord) { // TODO
																				// edit
																				// eden
																				// diger
																				// kullanicilara
																				// bildirilmesi
																				// gerekiyor,
																				// boyle
			// bir kaydın guncellendigi/sildigi ve user'in kapattigi
			String webPageId = requestParams.get(".w");
			String tabId = requestParams.get(".t ");
		}
		return mainFormResult;
	}

	private void extFormVcsControl(W5FormResult formResult, int action, Map<String, Object> scd,
			Map<String, String> requestParams, W5Table t, String ptablePk) {
		if (!FrameworkSetting.vcs || t.getVcsFlag() == 0)
			return;
		int tablePk = GenericUtil.uInt(ptablePk);
		if (tablePk == 0)
			return;
		switch (action) {
		case 5: // copy
		case 2: // insert
			W5VcsObject ivo = new W5VcsObject(scd, t.getTableId(), tablePk);
			dao.saveObject(ivo);
			break;
		case 1: // update
		case 3: // delete
			List l = dao.find("from W5VcsObject t where t.tableId=? AND t.tablePk=? AND t.projectUuid=?",
					t.getTableId(), tablePk, scd.get("projectId"));
			if (l.isEmpty())
				break;
			W5VcsObject vo = (W5VcsObject) l.get(0);
			vo.setVersionDttm(new Timestamp(new Date().getTime()));
			vo.setVersionUserId((Integer) scd.get("userId"));
			switch (vo.getVcsObjectStatusTip()) { // zaten insert ise
			case 0:// ignored
			case 2: // insert: direk sil
			case 3: // zaten silinmisse boyle birsey olmamali
				if (action == 3) {
					dao.removeObject(vo);
				}
				if (vo.getVcsObjectStatusTip() == 3)
					formResult.getOutputMessages().add("VCS WARNING: Already Deleted VCS Object????");
				break;

			case 1:
			case 9: // synched ve/veya edit durumunda ise
				if (action == 3) { // delete edilidliyse
					vo.setVcsObjectStatusTip((short) 3);
					vo.setVcsCommitRecordHash(requestParams.get("_iwb_vcs_dsc").toString());
				} else { // update edildise simdi
					String newHash = dao.getObjectVcsHash(scd, t.getTableId(), tablePk);
					vo.setVcsObjectStatusTip((short) (vo.getVcsCommitRecordHash().equals(newHash) ? 9 : 1));
				}
				dao.updateObject(vo);
				break;
			}
			break;
		}
	}
	
	public W5FormResult postFormAsJson(Map<String, Object> scd, int mainFormId, int action, JSONObject mainFormData,
			int detailFormId, JSONArray detailFormData) {
		Map<String, String> requestParams = new HashMap<String, String>();
		Iterator<String> ik = mainFormData.keys();
		while (ik.hasNext())
			try {
				String k = ik.next();
				if (!GenericUtil.isEmpty(mainFormData.get(k)))
					requestParams.put(k, mainFormData.get(k).toString());
			} catch (Exception e) {
				throw new IWBException("framework", "Json2FormPost(FormId)", mainFormId, null, e.getMessage(), null);
			}
		requestParams.put("_fid1", "" + detailFormId);
		requestParams.put("_cnt1", "" + detailFormData.length());
		for (int i = 0; i < detailFormData.length(); i++)
			try {
				JSONObject d = (JSONObject) detailFormData.get(i);
				ik = d.keys();
				requestParams.put("a1." + (i + 1), "2");
				while (ik.hasNext())
					try {
						String k = ik.next();
						if (!GenericUtil.isEmpty(d.get(k)))
							requestParams.put(k + "1." + (i + 1), d.get(k).toString());
					} catch (Exception e) {
						throw new IWBException("framework", "Json2FormPost(FormId)", mainFormId, null, e.getMessage(),
								e);
					}
			} catch (Exception e) {
				throw new IWBException("framework", "Json2FormPostDetail(FormId)", detailFormId, null, e.getMessage(),
						e);
			}
		return postForm4Table(scd, mainFormId, action, requestParams, "");
	}
}
