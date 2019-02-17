package iwb.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import iwb.cache.FrameworkCache;
import iwb.cache.FrameworkSetting;
import iwb.cache.LocaleMsgCache;
import iwb.custom.trigger.GetFormTrigger;
import iwb.dao.rdbms_impl.MetadataLoaderDAO;
import iwb.dao.rdbms_impl.PostgreSQL;
import iwb.domain.db.W5BIGraphDashboard;
import iwb.domain.db.W5Conversion;
import iwb.domain.db.W5ConvertedObject;
import iwb.domain.db.W5Detay;
import iwb.domain.db.W5Email;
import iwb.domain.db.W5FormCell;
import iwb.domain.db.W5FormModule;
import iwb.domain.db.W5FormSmsMail;
import iwb.domain.db.W5FormSmsMailAlarm;
import iwb.domain.db.W5Grid;
import iwb.domain.db.W5GridColumn;
import iwb.domain.db.W5LookUp;
import iwb.domain.db.W5LookUpDetay;
import iwb.domain.db.W5PageObject;
import iwb.domain.db.W5Table;
import iwb.domain.db.W5TableField;
import iwb.domain.db.W5Workflow;
import iwb.domain.db.W5WorkflowStep;
import iwb.domain.helper.W5FormCellHelper;
import iwb.domain.result.M5ListResult;
import iwb.domain.result.W5CardResult;
import iwb.domain.result.W5FormResult;
import iwb.domain.result.W5GridResult;
import iwb.domain.result.W5ListViewResult;
import iwb.domain.result.W5PageResult;
import iwb.domain.result.W5QueryResult;
import iwb.exception.IWBException;
import iwb.util.DBUtil;
import iwb.util.GenericUtil;
import iwb.util.UserUtil;

@Component
public class UIEngine {
	@Lazy
	@Autowired
	private PostgreSQL dao;

	@Lazy
	@Autowired
	private MetadataLoaderDAO metaDataDao;
	
	@Lazy
	@Autowired
	private QueryEngine queryEngine;

	

	@Lazy
	@Autowired
	private CRUDEngine crudEngine;
	
	
	@Lazy
	@Autowired 
	private AccessControlEngine acEngine;
	
	

	@Lazy
	@Autowired
	private GlobalScriptEngine scriptEngine;
	
	
	@Lazy
	@Autowired
	private ConversionEngine conversionEngine;

	
	public W5FormResult getFormResultByQuery(Map<String, Object> scd, int formId, int queryId,
			Map<String, String> requestParams) {
		W5FormResult formResult = metaDataDao.getFormResult(scd, formId, 1, requestParams);
		// formResult.getForm().get_formCells().clear();
		if (formId != 1622)
			formResult.getForm().get_moduleList().clear(); // TODO: neden
															// yapilmis???

		W5QueryResult queryResult = queryEngine.executeQuery(scd, queryId, requestParams);
		formResult.setFormCellResults(new ArrayList<W5FormCellHelper>(queryResult.getData().size()));

		short tabOrder = 1;
		for (Object[] d : queryResult.getData()) {
			W5FormCellHelper result = GenericUtil.getFormCellResultByQueryRecord(d);
			if (result.getFormCell().getTabOrder() == 0)
				result.getFormCell().setTabOrder(tabOrder);
			tabOrder++;
			formResult.getFormCellResults().add(result);
		}
		if (queryResult.getQuery().get_queryFields().get(7).getPostProcessTip() == 10) { // tipi
																							// lookup
																							// ise
																							// o
																							// zaman
																							// modulleri
																							// buraya
																							// koy
			for (W5LookUpDetay d : FrameworkCache
					.getLookUp(scd, queryResult.getQuery().get_queryFields().get(7).getLookupQueryId())
					.get_detayList()) {
				W5FormModule m = new W5FormModule();
				m.setFormModuleId(GenericUtil.uInt(d.getVal()));
				m.setLocaleMsgKey(d.getDsc());
				formResult.getForm().get_moduleList().add(m);
			}
		}
		dao.loadFormCellLookups(scd, formResult.getFormCellResults(), requestParams, null);
		return formResult;
	}

	public W5FormResult getFormResult(Map<String, Object> scd, int formId, int action,
			Map<String, String> requestParams) {
		if (
		/* formId==0 && */ GenericUtil.uInt(requestParams.get("_tb_id")) != 0
				&& GenericUtil.uInt(requestParams.get("_tb_pk")) != 0) { // isterse
																			// _tb_id,tb_pk
																			// degerleriyle
																			// de
																			// bir
																			// form
																			// acilabilir
			W5Table t = FrameworkCache.getTable(scd, GenericUtil.uInt(requestParams.get("_tb_id")));
			if (t == null) {
				throw new IWBException("framework", "Table", GenericUtil.uInt(requestParams.get("_tb_id")), null,
						LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_such_table"), null);
			}
			formId = GenericUtil.uInt(requestParams, "_fid");
			if (formId == 0)
				formId = t.getDefaultUpdateFormId();
			if (formId == 0) {
				List ll = dao.executeSQLQuery(
						"select min(f.form_id) from iwb.w5_form f where f.project_uuid=? AND f.object_tip=2 AND f.object_id=?",
						scd.get("projectId"), t.getTableId());
				if (GenericUtil.isEmpty(ll)) {
					throw new IWBException("framework", "Table", t.getTableId(), null,
							LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_no_form_for_table"), null);
				}
				formId = GenericUtil.uInt(ll.get(0));
			}
			requestParams.put(t.get_tableParamList().get(0).getDsc(), requestParams.get("_tb_pk"));
			action = 1;
			requestParams.put("a", "1");
		}
		W5FormResult formResult = null;
		try {
			formResult = metaDataDao.getFormResult(scd, formId, action, requestParams);
			dao.checkTenant(formResult.getScd());
			formResult.setUniqueId(GenericUtil.getNextId("fi"));
			/*
			 * if(requestParams.containsKey("_log5_log_id")){
			 * if(!FrameworkCache.wTemplates.containsKey(scd.get(
			 * "customizationId")))FrameworkCache.wTemplates.put((Integer)scd.
			 * get("customizationId"),new HashMap());
			 * FrameworkCache.wTemplates.get((Integer)scd.get("customizationId")
			 * ).put(668, (W5Template)dao.find(
			 * "from W5Template t where t.templateId=668 AND t.customizationId=?"
			 * , scd.get("customizationId")).get(0)); }
			 */
			// boolean dev = scd.get("roleId")!=null &&
			// (Integer)scd.get("roleId")==0 &&
			// GenericUtil.uInt(requestParams,"_dev")!=0;
			String projectId = FrameworkCache.getProjectId(scd, "40." + formId);
			W5Table t = null;
			switch (formResult.getForm().getObjectTip()) {
			case 2: // table
				t = FrameworkCache.getTable(projectId, formResult.getForm().getObjectId()); // formResult.getForm().get_sourceTable();
				boolean accessControlSelfFlag = true; // kendisi VEYA
														// kendisi+master
				switch (t.getAccessViewTip()) {
				case 1:
					if (t.getAccessViewUserFields() == null && !GenericUtil.accessControl(scd, t.getAccessViewTip(),
							t.getAccessViewRoles(), t.getAccessViewUsers())) {
						throw new IWBException("security", "Form", formId, null, LocaleMsgCache.get2(0,
								(String) scd.get("locale"), "fw_guvenlik_tablo_kontrol_goruntuleme"), null);
					}
				}

				if (accessControlSelfFlag)
					acEngine.accessControl4FormTable(formResult, null);
				if (formResult.getForm().get_moduleList() != null) {
					for (W5FormModule m : formResult.getForm().get_moduleList())
						if (m.getModuleTip() == 4 && GenericUtil.accessControl(scd, m.getAccessViewTip(),
								m.getAccessViewRoles(), m.getAccessViewUsers())) { // form
							if (m.getModuleViewTip() == 0 || (m.getModuleViewTip() == 1 && action == 1)
									|| (m.getModuleViewTip() == 2 && action == 2)) {
								int newAction = GenericUtil.uInt(requestParams.get("a" + m.getTabOrder()));
								if (newAction == 0)
									newAction = action;
								if (formResult.getModuleFormMap() == null)
									formResult.setModuleFormMap(new HashMap());
								formResult.getModuleFormMap().put(m.getObjectId(),
										getFormResult(scd, m.getObjectId(), newAction, requestParams));
							}
						}
				}
				break;
			case 5: // formByQuery:
				formResult
						.setQueryResult4FormCell(queryEngine.executeQuery(scd, formResult.getForm().getObjectId(), requestParams));
				formResult.setFormCellResults(new ArrayList());
				for (Object[] d : formResult.getQueryResult4FormCell().getData()) {
					W5FormCellHelper result = GenericUtil.getFormCellResultByQueryRecord(d);
					// result.getFormCell().setTabOrder(tabOrder++);
					formResult.getFormCellResults().add(result);
				}
				break;
			}

			GetFormTrigger.beforeGetForm(formResult);
			if (formResult.getForm().getObjectTip() != 2)
				action = 2; // eger table degilse sadece initializeForm olabilir

			if (formResult.getForm().getObjectTip() != 5
					&& action == 9 /* edit (if not insert) */) {
				action = dao.checkIfRecordsExists(scd, requestParams, t) ? 1 : 2;
			}

			/* tableTrigger before Show start */
			if (formResult.getForm().getObjectTip() == 2 && action != 3)
				crudEngine.extFormTableEvent(formResult, new String[] { "_", "su", "si", "_", "_", "si" }[action], scd,
						requestParams, t, null, null);
			/* end of tableTrigger */

			if (formResult.getForm().getObjectTip() != 5)
				switch (action) {
				case 5: // copy
				case 1: // edit
					if (formResult.getForm().getObjectTip() == 2 && action == 1) {

						if (!GenericUtil.isEmpty(formResult.getForm().get_conversionList())) { // conversion
																								// olan
																								// bir
																								// form?
																								// o
																								// zaman
																								// sync
																								// olan
							// covnerted objeleri bul
							String inStr = "";
							for (W5Conversion cnv : formResult.getForm().get_conversionList())
								if (GenericUtil.hasPartInside2(cnv.getActionTips(), action)
										|| cnv.getSynchOnUpdateFlag() != 0) { // synch
																				// varsa
									inStr += "," + cnv.getConversionId();
								}
							if (inStr.length() > 1) {
								List<W5ConvertedObject> lco = dao.find(
										"from W5ConvertedObject x where x.projectUuid=? AND x.conversionId in ("
												+ inStr.substring(1) + ") and x.srcTablePk=?",
										projectId,
										GenericUtil.uInt(requestParams, t.get_tableParamList().get(0).getDsc()));
								if (!lco.isEmpty()) {
									Map<Integer, List<W5ConvertedObject>> m = new HashMap();
									formResult.setMapConvertedObject(m);
									List<W5ConvertedObject> orphanCol = new ArrayList();
									for (W5ConvertedObject co : lco) {
										int dstTableId = 0;
										for (W5Conversion cnv : formResult.getForm().get_conversionList())
											if (cnv.getConversionId() == co.getConversionId()) {
												dstTableId = cnv.getDstTableId();
												break;
											}
										co.set_relatedRecord(dao.findRecordParentRecords(scd, dstTableId,
												co.getDstTablePk(), 10, false));
										if (GenericUtil.isEmpty(co.get_relatedRecord())) {
											orphanCol.add(co);
										} else {
											List<W5ConvertedObject> l = m.get(co.getConversionId());
											if (l == null) {
												l = new ArrayList();
												m.put(co.getConversionId(), l);
											}
											l.add(co);
										}
									}
									if (!orphanCol.isEmpty()) {
										dao.removeAllObjects(orphanCol);
									}
								}
							}
						}
					}
					dao.loadFormTable(formResult);

					// eralp istedi, amaci freefieldlarda initial query deger
					// verebilsin
					for (W5FormCellHelper fcx : formResult.getFormCellResults())
						if (fcx.getFormCell().getObjectDetailId() == 0) { // bir
																			// tane
																			// freefield
																			// bulabilirse
																			// gir
							int initialQueryId = GenericUtil.uInt(requestParams, "_iqid");
							if (initialQueryId != 0) {
								Map<String, Object> m = queryEngine.executeQuery2Map(scd, initialQueryId, requestParams);
								if (m != null) {
									for (W5FormCellHelper fc : formResult.getFormCellResults())
										if (fc.getFormCell().getObjectDetailId() == 0) {
											Object s = m.get(
													fc.getFormCell().getDsc().toLowerCase(FrameworkSetting.appLocale));
											if (s != null)
												fc.setValue(s.toString());
										}
								}
							}
							for (W5FormCellHelper fcx2 : formResult.getFormCellResults())
								if (fcx2.getFormCell().getObjectDetailId() == 0)
									switch (fcx2.getFormCell().getInitialSourceTip()) {
									case 0: // yok-sabit
										fcx2.setValue(fcx2.getFormCell().getInitialValue());
										break;
									case 1: // request
										fcx2.setValue(formResult.getRequestParams()
												.get(fcx2.getFormCell().getInitialValue()));
										break;
									case 2:
										Object o = formResult.getScd().get(fcx2.getFormCell().getInitialValue());
										fcx2.setValue(o == null ? null : o.toString());
										break;
									case 3: // app_setting
										fcx2.setValue(FrameworkCache.getAppSettingStringValue(formResult.getScd(),
												fcx2.getFormCell().getInitialValue()));
										break;
									case 4: // SQL
										Object[] oz = DBUtil.filterExt4SQL(fcx2.getFormCell().getInitialValue(),
												formResult.getScd(), formResult.getRequestParams(), null);
										if (oz[1] != null && ((List) oz[1]).size() > 0) {
											Map<String, Object> m = dao.runSQLQuery2Map(oz[0].toString(), (List) oz[1],
													null);
											if (m != null && m.size() > 0)
												fcx2.setValue(m.values().iterator().next().toString());
										} else {
											List l = dao.executeSQLQuery(oz[0].toString());
											if (l != null && l.size() > 0 && l.get(0) != null)
												fcx2.setValue(l.get(0).toString());
										}
										break;
									}

							break;
						}
					for (W5FormCellHelper fcx : formResult.getFormCellResults())
						if (fcx.getFormCell().getFormCellId() == 6060) { // mail
																			// sifre
																			// icin,
																			// baska
																			// seyler
																			// icin
																			// de
																			// kullanilabilir
							fcx.setValue("************");
						}

					if (action == 1)
						break;
				case 2: // insert
					if (action == 2) {
						dao.initializeForm(formResult, false);
						Map mq = null;
						boolean convb = false;
						if (requestParams.containsKey("_cnvId") && requestParams.containsKey("_cnvTblPk")) { // conversion
							int conversionId = GenericUtil.uInt(requestParams, "_cnvId");
							int conversionTablePk = GenericUtil.uInt(requestParams, "_cnvTblPk");
							if (conversionId != 0 && conversionTablePk != 0) {
								String prjId = FrameworkCache.getProjectId(scd, "707." + conversionId);
								W5Conversion c = (W5Conversion) dao.getCustomizedObject(
										"from W5Conversion t where t.conversionId=? AND t.projectUuid=?", conversionId,
										prjId, "Conversion");
								if (c == null || c.getDstFormId() != formResult.getFormId()) {
									throw new IWBException("framework", "Conversion", conversionId, null,
											LocaleMsgCache.get2(0, (String) scd.get("locale"), "wrong_conversion"),
											null);
								}
								if (c.getMaxNumofConversion() > 0) {
									List l = dao.find(
											"select 1 from W5ConvertedObject c where c.conversionId=? AND c.projectUuid=? AND c.srcTablePk=?",
											conversionId, scd.get("projectId"), conversionTablePk);
									if (l.size() >= c.getMaxNumofConversion()) {
										throw new IWBException("framework", "Conversion", conversionId, null,
												LocaleMsgCache.get2(0, (String) scd.get("locale"),
														"max_number_of_conversion_reached") + " (" + l.size() + ")",
												null);
									}
								}
								W5Table srcTable = FrameworkCache.getTable(scd, c.getSrcTableId());
								if (!GenericUtil.isEmpty(srcTable.get_approvalMap())) { // burda
																						// bir
																						// approval
																						// olabilir,
																						// kontrol
																						// etmek
																						// lazim
									List l = dao.find(
											"select 1 from W5WorkflowRecord c where c.projectUuid=? AND c.finishedFlag=0 AND c.tableId=? AND c.tablePk=?",
											scd.get("projectId"), c.getSrcTableId(), conversionTablePk);
									if (!l.isEmpty()) {
										throw new IWBException("framework", "Conversion", conversionId, null,
												LocaleMsgCache.get2(0, (String) scd.get("locale"),
														"record_must_be_approved_before_conversion"),
												null);
									}
								}
								mq = conversionEngine.interprateConversionTemplate(c, formResult, conversionTablePk, true, false);
								if (mq != null) {
									convb = true;
									formResult.getOutputMessages()
											.add(LocaleMsgCache.get2(scd, "fw_converted_from")
													+ " <b><a href=# onclick=\"return mainPanel.loadTab({attributes:{href:'showForm?a=1&_tb_id="
													+ mq.get("_cnv_src_tbl_id") + "&_tb_pk=" + mq.get("_cnv_src_tbl_pk")
													+ "&_fid=" + mq.get("_cnv_src_frm_id") + "'}})\">" + mq
															.get("_cnv_record")
													+ "</a></b> --> " + LocaleMsgCache.get2(scd,
															mq.get("_cnv_name").toString() + " --> ?"));
								}
							}
						} else if ((formId == 650 /* || formId==631 */) && requestParams.containsKey("_fsmId")) { // formId=650/631
																													// ise
																													// buna
																													// gore
																													// mail/sms
																													// hazirlanacak
							int fsmFrmId = GenericUtil.uInt(requestParams, "_fsmFrmId");
							W5FormResult fsmformResult = getFormResult(formResult.getScd(), fsmFrmId, (short) 2,
									requestParams);
							int fsmId = GenericUtil.uInt(requestParams, "_fsmId");
							W5FormSmsMail fsm = fsmformResult.getForm().get_formSmsMailMap().get(fsmId);
							int fsmTableId = GenericUtil.uInt(requestParams, "_tableId");
							int fsmTablePk = GenericUtil.uInt(requestParams, "_tablePk");
							W5Email email = /*
											 * formId==631 ?
											 * dao.interprateSmsTemplate(fsm,
											 * formResult.getScd(),requestParams
											 * ,fsmTableId,fsmTablePk) :
											 */
							dao.interprateMailTemplate(fsm, formResult.getScd(), requestParams, fsmTableId, fsmTablePk);
							if (formId == 650 && !GenericUtil.isEmpty(scd.get("mailSettingId"))) {
								Map<String, Object> mapMailSign = dao.runSQLQuery2Map(
										"select x.EMAIL_SIGNATURE s from iwb.w5_object_mail_setting x where x.MAIL_SETTING_ID=${scd.mailSettingId} AND x.customization_id in (0,${scd.customizationId})",
										scd, requestParams, null);
								if (!GenericUtil.isEmpty(mapMailSign) && !GenericUtil.isEmpty(mapMailSign.get("s"))) {
									if (email == null || GenericUtil.isEmpty(email.getMailBody()))
										mq.put("pmail_body",
												FrameworkSetting.mailSeperator + (String) mapMailSign.get("s"));
									else
										mq.put("pmail_body", email.getMailBody() + FrameworkSetting.mailSeperator
												+ (String) mapMailSign.get("s"));
								}
							}
							formResult.getOutputMessages()
									.add(LocaleMsgCache.get2(scd, "fw_mail_converted_from")
											+ " <b><a href=# onclick=\"return mainPanel.loadTab({attributes:{href:'showForm?a=1&_tb_id="
											+ fsmTableId + "&_tb_pk=" + fsmTablePk + "&_fid=" + fsmFrmId + "'}})\">"
											+ dao.getSummaryText4Record(scd, fsmTableId, fsmTablePk) + "</a></b> --> "
											+ fsm.getDsc());
						} else if (requestParams.containsKey("_iqid")) {
							mq = queryEngine.executeQuery2Map(scd, GenericUtil.uInt(requestParams, "_iqid"), requestParams);
						}
						if (mq != null) {
							for (W5FormCellHelper fc : formResult.getFormCellResults()) {
								Object s = mq.get(fc.getFormCell().getDsc().toLowerCase(FrameworkSetting.appLocale));
								if (s != null) {
									fc.setValue(s.toString());
									// conversion esnasinda, request'ten gelen
									// degerleri simule ediyor, request'e
									// manuel koyarak
									if (convb && (fc.getFormCell().getControlTip() == 0
											|| (fc.getFormCell().getNrdTip() == 2
													&& fc.getFormCell().getInitialSourceTip() == 1
													&& fc.getFormCell().getInitialValue() != null && fc.getFormCell()
															.getInitialValue().equals(fc.getFormCell().getDsc()))))
										formResult.getRequestParams().put(fc.getFormCell().getDsc(), s.toString());
								}
							}
						}
					} else { // copy
						if (formResult.getForm().getObjectTip() == 2)
							for (W5FormCellHelper fcr : formResult.getFormCellResults())
								if (fcr.getFormCell().get_sourceObjectDetail() != null)
									switch (((W5TableField) fcr.getFormCell().get_sourceObjectDetail())
											.getCopySourceTip()) {
									case 1: // request
										fcr.setValue(
												dao.getInitialFormCellValue(scd, fcr.getFormCell(), requestParams));
										break;
									case 6: // object_source:demek ki sorulacak
											// degisecek mi diye?
										break;
									case 7: // object_source(readonly)
										fcr.setHiddenValue("1");
									}
						Map mq = null;
						if (requestParams.containsKey("_iqid")) {
							mq = queryEngine.executeQuery2Map(scd, GenericUtil.uInt(requestParams, "_iqid"), requestParams);
						}
						if (mq != null) {
							for (W5FormCellHelper fc : formResult.getFormCellResults())
								if (fc.getValue() == null || fc.getValue().length() == 0) {
									Object s = mq
											.get(fc.getFormCell().getDsc().toLowerCase(FrameworkSetting.appLocale));
									if (s != null)
										fc.setValue(s.toString());
								}
						}
						action = 2;
						formResult.setAction(2);
					}

					break;
				default:
					throw new IWBException("framework", "Form", formId, null,
							LocaleMsgCache.get2(0, (String) scd.get("locale"), "wrong_use_of_action") + " (" + action
									+ ")",
							null);
				}

			// form cell lookup load
			dao.loadFormCellLookups(formResult.getScd(), formResult.getFormCellResults(), formResult.getRequestParams(),
					FrameworkSetting.liveSyncRecord && formResult.getForm().getObjectTip() == 2
							? formResult.getUniqueId() : null);

			for (W5FormCellHelper cr : formResult.getFormCellResults())
				if (cr.getFormCell().getControlTip() == 99) { // grid ise bunun
																// icinde var mi
																// editableFormCell
					W5Grid g = (W5Grid) cr.getFormCell().get_sourceObjectDetail();
					W5GridResult gr = new W5GridResult(g.getGridId());
					gr.setRequestParams(formResult.getRequestParams());
					gr.setScd(formResult.getScd());
					gr.setFormCellResultMap(new HashMap());

					for (W5GridColumn column : g.get_gridColumnList())
						if (column.get_formCell() != null) {
							gr.getFormCellResultMap().put(column.get_formCell().getFormCellId(),
									new W5FormCellHelper(column.get_formCell()));
						}

					gr.setGrid(g);
					if (formResult.getModuleGridMap() == null)
						formResult.setModuleGridMap(new HashMap());
					formResult.getModuleGridMap().put(g.getGridId(), gr);

					if (!gr.getFormCellResultMap().isEmpty())
						dao.loadFormCellLookups(gr.getScd(), new ArrayList(gr.getFormCellResultMap().values()),
								gr.getRequestParams(), null);
				}

			if (GenericUtil.uInt(formResult.getRequestParams().get("viewMode")) != 0)
				formResult.setViewMode(true);

			W5WorkflowStep approvalStep = null;
			if (formResult.getForm().getObjectTip() == 2 && action == 1
					&& /* formResult.getForm().get_sourceTable() */ FrameworkCache.getTable(scd,
							formResult.getForm().getObjectId()) != null
					&& formResult.getApprovalRecord() != null) {
				W5Workflow approval = FrameworkCache.getWorkflow(projectId,
						formResult.getApprovalRecord().getApprovalId());
				if (approval != null) {
					approvalStep = approval.get_approvalStepMap()
							.get(formResult.getApprovalRecord().getApprovalStepId()).getNewInstance();
					if (approvalStep != null) {
						boolean canCancel = GenericUtil.hasPartInside2(approval.getAfterFinUpdateUserIds(),
								scd.get("userId")) && formResult.getApprovalRecord().getApprovalActionTip() == 5
								&& formResult.getApprovalRecord().getApprovalStepId() == 998 ? true : false;
						if (approvalStep.getApprovalStepId() != 901 && approvalStep.getUpdatableFields() == null
								&& !canCancel)
							formResult.setViewMode(true);
						formResult.setApprovalStep(approvalStep);
					}
				}
			}
			// normal/readonly/disabled show control/ozel kodlama
			int updatableFieldsCount = 0;
			for (W5FormCellHelper cr : formResult.getFormCellResults())
				if (cr.getFormCell().getControlTip() != 0 && cr.getFormCell().getControlTip() != 13
						&& cr.getFormCell().getControlTip() != 100) { // yok ve
																		// hidden
																		// ve
																		// buttondan
																		// baska
					W5TableField tf = formResult.getForm().getObjectTip() == 2
							&& cr.getFormCell().get_sourceObjectDetail() instanceof W5TableField
									? (W5TableField) cr.getFormCell().get_sourceObjectDetail() : null;
					if (formResult.isViewMode() || cr.getHiddenValue() != null
							|| (action == 1 && cr.getFormCell()
									.getControlTip() == 31 /* ozel kodlama */
							&& GenericUtil.uInt(cr.getFormCell().getLookupIncludedValues()) == 1
							&& !GenericUtil.hasPartInside(cr.getFormCell().getLookupIncludedParams(),
									"" + formResult.getScd().get("userId")))
							|| cr.getFormCell().getNrdTip() != 0 // readonly/disabled
							|| (approvalStep != null
									&& cr.getFormCell()
											.get_sourceObjectDetail() != null
									&& !GenericUtil.hasPartInside(approvalStep.getUpdatableFields(),
											"" + ((W5TableField) cr.getFormCell().get_sourceObjectDetail())
													.getTableFieldId())) // approvalStepUpdatable
																			// Table
																			// Fields
							|| (formResult.getForm().getObjectTip() == 2 && action == 1 && tf != null
									&& (tf.getCanUpdateFlag() == 0 || (tf.getAccessUpdateTip() != 0
											&& !GenericUtil.accessControl(scd, tf.getAccessUpdateTip(),
													tf.getAccessUpdateRoles(), tf.getAccessUpdateUsers())
											&& (GenericUtil.isEmpty(tf.getAccessUpdateUserFields())
													|| dao.accessUserFieldControl(t, tf.getAccessUpdateUserFields(),
															scd, requestParams, null)))))) { // ction=edit'te
																								// edti
																								// hakki
																								// yok

						String value = cr.getValue();
						cr.setHiddenValue(value == null || value.length() == 0 ? "_" : GenericUtil.stringToJS(value));
						switch (cr.getFormCell().getControlTip()) {
						case 5: // checkbox
							break;
						case 9:
						case 16:
						case 60: // remote Lookups
							// cr.setHiddenValue(null);
							break;
						case 2: // date
							cr.setValue(GenericUtil.uDateStr(value));
							break;
						case 18: // timestamp
							cr.setValue(value);
							break;
						case 10:
						case 61: // autoselect,
									// superboxselect-combo-query-advanced
							if (cr.getLookupQueryResult() != null && cr.getLookupQueryResult().getData() != null
									&& cr.getLookupQueryResult().getData().size() != 0) {
								cr.setValue((String) cr.getLookupQueryResult().getData().get(0)[0]);
								// cr.setHiddenValue(value);
							}
							break;
						case 6: // combo static
							// cr.setHiddenValue(cr.getValue());
							if (cr.getLookupListValues() != null)
								for (W5Detay d : (List<W5Detay>) cr.getLookupListValues()) {
									if (d.getVal().equals(cr.getValue())) {
										cr.setValue(LocaleMsgCache.get2((Integer) scd.get("customizationId"),
												(String) scd.get("locale"), d.getDsc()));
										break;
									}
								}
							break;
						case 7: // combo query
						case 23: // treecombo query
							// cr.setHiddenValue(cr.getValue());
							if (cr.getLookupQueryResult() != null && cr.getLookupQueryResult().getData() != null)
								for (Object[] d : (List<Object[]>) cr.getLookupQueryResult().getData()) {
									if (d[1] != null && d[1].toString().equals(cr.getValue())) {
										cr.setValue(d[0].toString());
										break;
									}
								}
							break;
						case 59: // superboxselect query
						case 15: // lov combo query
							String xval = "";
							if (cr.getLookupQueryResult() != null && cr.getValue() != null) {
								Vector<String> vals = new Vector<String>();
								for (String str : cr.getValue().split(",")) {
									vals.add(str);
								}
								for (Object[] d : (List<Object[]>) cr.getLookupQueryResult().getData()) {
									if (vals.contains(d[1].toString())) {
										xval += cr.getLookupQueryResult().getQuery().get_queryFields().get(0)
												.getPostProcessTip() == 2
														? LocaleMsgCache.get2((Integer) scd.get("customizationId"),
																(String) scd.get("locale"), d[0].toString()) + ", "
														: d[0].toString() + ", ";
									}
								}
							}
							cr.setValue(xval.length() > 2 ? xval.substring(0, xval.length() - 2) : xval);
							break;

						case 58: // superboxselect static
						case 8: // lov combo static
							if (cr.getValue() != null) {
								String xval2 = "";
								String[] arr = cr.getValue().split(",");
								for (int sindex = 0; sindex < arr.length; sindex++) {
									int no = GenericUtil.getIndexNo(arr[sindex], cr.getLookupListValues());
									W5LookUpDetay ld = (W5LookUpDetay) cr.getLookupListValues().get(no);
									xval2 += LocaleMsgCache.get2((Integer) scd.get("customizationId"),
											(String) scd.get("locale"), ld.getDsc()) + " , ";
								}
								cr.setValue(xval2.substring(0, xval2.length() - 2));
							}
							break;
						}

					} else
						updatableFieldsCount++;
				} else if (cr.getFormCell().getControlTip() == 100) { // Buton
																		// ise
																		// butonun
																		// extra
																		// kodunda
																		// local
																		// mesajları
																		// cevirereceğiz
																		// inşallah
					cr.getFormCell().setExtraDefinition(GenericUtil.filterExt(cr.getFormCell().getExtraDefinition(),
							formResult.getScd(), formResult.getRequestParams(), null).toString());
				} else
					cr.setHiddenValue(null);

			if (formResult.getForm().getObjectTip() == 2) { // table ise
				if (updatableFieldsCount == 0)
					formResult.setViewMode(true);
				if (action == 1) { // eidt mode'da ise
					if (FrameworkSetting.alarm /* && !formResult.isViewMode() */
							&& !GenericUtil.isEmpty(formResult.getForm().get_formSmsMailList())) { // readonly
																									// degil
																									// ise
						boolean alarm = false;
						for (W5FormSmsMail i : formResult.getForm().get_formSmsMailList())
							if (i.getAlarmFlag() != 0) {
								alarm = true;
								break;
							}
						if (alarm) {
							formResult.setFormAlarmList((List<W5FormSmsMailAlarm>) dao.find(
									"from W5FormSmsMailAlarm a where a.projectUuid=? AND a.insertUserId=? AND a.tableId=? AND a.tablePk=? ",
									projectId, scd.get("userId"), formResult.getForm().getObjectId(),
									GenericUtil.uInt(requestParams, t.get_tableParamList().get(0).getDsc())));
						}
					}
				}
			}

			GetFormTrigger.afterGetForm(formResult);

			return formResult;
		} catch (Exception e) {
			throw new IWBException("framework", "Form", formId, null, "[40," + formId + "]"
					+ (formResult != null && formResult.getForm() != null ? " " + formResult.getForm().getDsc() : ""),
					e);
		}
	}
	
	public W5PageResult getPageResult(Map<String, Object> scd, int pageId, Map<String, String> requestParams) {
		W5PageResult pr = null;
		try {
			boolean developer = scd.get("roleId") != null && (Integer) scd.get("roleId") == 0;
			boolean debugAndDeveloper = FrameworkSetting.debug && developer;
			pr = metaDataDao.getPageResult(scd, pageId);
			pr.setRequestParams(requestParams);
			pr.setPageObjectList(new ArrayList<Object>());
			List<W5PageObject> templateObjectListExt = new ArrayList<W5PageObject>(
					pr.getPage().get_pageObjectList().size() + 5);
			templateObjectListExt.addAll(pr.getPage().get_pageObjectList());

			requestParams.put("_dont_throw", "1");
			if (pageId == 238) { // Record Bazlı yetkilendirme gridi
				int objectId = GenericUtil.uInt(requestParams.get("_gid1"));
				if (objectId == 477) {
					W5Table t = FrameworkCache.getTable(scd, GenericUtil.uInt(requestParams.get("crud_table_id")));
					if (t.getAccessPermissionUserFields() != null)
						requestParams.put(t.get_tableParamList().get(0).getDsc(), requestParams.get("_table_pk"));
					// Kontrol atanmışsa ve kullanıcının yetkisi yoksa
					if (t.getAccessPermissionTip() == 1
							&& !GenericUtil.accessControl(scd, t.getAccessPermissionTip(), t.getAccessPermissionRoles(),
									t.getAccessPermissionUsers())
							&& (t.getAccessPermissionUserFields() == null || dao.accessUserFieldControl(t,
									t.getAccessPermissionUserFields(), scd, requestParams, null))) {
						throw new IWBException("security", "Table",
								GenericUtil.uInt(requestParams.get("crud_table_id")), null,
								LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_yetkilendirme_yetkisi"),
								null);
					}
				}
			}

			for (int i = 1; requestParams.containsKey("_gid" + i) || requestParams.containsKey("_fid" + i)
					|| requestParams.containsKey("_dvid" + i) || requestParams.containsKey("_lvid" + i); i++) { // extra
																												// olarak
																												// _gid1=12&_gid=2
																												// gibi
																												// seyler
																												// soylenebilir
				int objectId = GenericUtil.uInt(requestParams.get("_gid" + i)); // grid
				short objectTip = -1;
				if (objectId == 0) {
					objectId = GenericUtil.uInt(requestParams.get("_fid" + i)); // form
					objectTip = -3;
				}
				if (objectId == 0) {
					objectId = GenericUtil.uInt(requestParams.get("_dvid" + i)); // data
																					// view
					objectTip = -2;
				}
				if (objectId == 0) {
					objectId = GenericUtil.uInt(requestParams.get("_lvid" + i)); // list
																					// view
					objectTip = -7;
				}
				W5PageObject o = new W5PageObject();
				o.setObjectTip(objectTip);
				o.setObjectId(objectId);
				templateObjectListExt.add(o);
			}

			int objectCount = 0;
			if (pr.getPage().getTemplateTip() != 8) { // wizard'dan farkli ise
				for (W5PageObject o : templateObjectListExt) {
					boolean accessControl = debugAndDeveloper ? true
							: GenericUtil.accessControl(scd, o.getAccessViewTip(), o.getAccessViewRoles(),
									o.getAccessViewUsers());
					Object obz = null;
					W5Table mainTable = null;
					switch (Math.abs(o.getObjectTip())) {
					case 1: // grid
						W5GridResult gridResult = metaDataDao.getGridResult(scd, o.getObjectId(), requestParams,
								pageId == 298 /* || objectCount!=0 */);
						if (pageId == 298) { // log template
							gridResult.setViewLogMode(true);
						}
						if (o.getObjectTip() < 0) {
							if (GenericUtil.uInt(requestParams, "_gid" + gridResult.getGridId() + "_a") != 0)
								gridResult.setAction(
										GenericUtil.uInt(requestParams, "_gid" + gridResult.getGridId() + "_a"));
							gridResult.setGridId(-gridResult.getGridId());
						}
						mainTable = gridResult.getGrid() != null && gridResult.getGrid().get_query() != null
								? FrameworkCache.getTable(scd, gridResult.getGrid().get_query().getMainTableId())
								: null;
						if (!debugAndDeveloper && mainTable != null
								&& (((mainTable.getAccessTips() == null || mainTable.getAccessTips().indexOf("0") == -1)
										&& mainTable.getAccessViewUserFields() == null
										&& !GenericUtil.accessControl(scd, mainTable.getAccessViewTip(),
												mainTable.getAccessViewRoles(), mainTable.getAccessViewUsers()))))
							obz = gridResult.getGrid().getDsc();
						else {
							if (GenericUtil.uInt(requestParams, "_viewMode") != 0)
								gridResult.setViewReadOnlyMode(true);
							else if (GenericUtil.uInt(requestParams, "_viewMode" + o.getObjectId()) != 0)
								gridResult.setViewReadOnlyMode(true);
							obz = accessControl ? gridResult : gridResult.getGrid().getDsc();
						}
						if (obz instanceof W5GridResult) {
							Map m = new HashMap();
							gridResult.setTplObj(o);
							gridResult.setExtraOutMap(m);
							m.put("tplId", o.getTemplateId());
							m.put("tplObjId", o.getTemplateObjectId());
						}
						break;
					case 2: // card view
						W5CardResult cardResult = metaDataDao.getCardResult(scd, o.getObjectId(), requestParams,
								objectCount != 0);
						if (o.getObjectTip() < 0)
							cardResult.setDataViewId(-cardResult.getDataViewId());
						mainTable = cardResult.getCard() != null && cardResult.getCard().get_query() != null
								? FrameworkCache.getTable(scd, cardResult.getCard().get_query().getMainTableId())
								: null;
						if (!debugAndDeveloper && mainTable != null
								&& (((mainTable.getAccessTips() == null || mainTable.getAccessTips().indexOf("0") == -1)
										&& mainTable.getAccessViewUserFields() == null
										&& !GenericUtil.accessControl(scd, mainTable.getAccessViewTip(),
												mainTable.getAccessViewRoles(), mainTable.getAccessViewUsers()))))
							obz = cardResult.getCard().getDsc();
						else {
							obz = accessControl ? cardResult : cardResult.getCard().getDsc();
						}
						if (obz instanceof W5CardResult) {
							Map m = new HashMap();
							cardResult.setTplObj(o);
							cardResult.setExtraOutMap(m);
							m.put("tplId", o.getTemplateId());
							m.put("tplObjId", o.getTemplateObjectId());
						}
						break;
					case 7: // list view
						W5ListViewResult listViewResult = metaDataDao.getListViewResult(scd, o.getObjectId(), requestParams,
								objectCount != 0);
						if (o.getObjectTip() < 0)
							listViewResult.setListId(-listViewResult.getListId());
						mainTable = listViewResult.getListView() != null
								&& listViewResult.getListView().get_query() != null
										? FrameworkCache.getTable(scd,
												listViewResult.getListView().get_query().getMainTableId())
										: null;
						if (!debugAndDeveloper && mainTable != null
								&& (((mainTable.getAccessTips() == null || mainTable.getAccessTips().indexOf("0") == -1)
										&& mainTable.getAccessViewUserFields() == null
										&& !GenericUtil.accessControl(scd, mainTable.getAccessViewTip(),
												mainTable.getAccessViewRoles(), mainTable.getAccessViewUsers()))))
							obz = listViewResult.getListView().getDsc();
						else {
							obz = accessControl ? listViewResult : listViewResult.getListView().getDsc();
						}
						break;
					case 3: // form
						W5FormResult formResult = getFormResult(scd, o.getObjectId(),
								requestParams.get("a") != null ? GenericUtil.uInt(requestParams, "a") : 2,
								requestParams);
						if (o.getObjectTip() < 0)
							formResult.setFormId(-formResult.getFormId());
						formResult.setObjectTip(o.getObjectTip()); // render
																	// icin
																	// gerekecek
						/*
						 * if(PromisSetting.moduleAccessControl!=0 &&
						 * formResult.getForm()!=null &&
						 * formResult.getForm().get_sourceTable()!=null &&
						 * !PromisCache.roleAccessControl(scd,
						 * formResult.getForm().get_sourceTable().getModuleId())
						 * ) obz = formResult.getForm().getDsc(); else
						 */
						obz = accessControl ? formResult : formResult.getForm().getDsc();
						break;
					case 4: // query
						Map paramMap = new HashMap();
						paramMap.putAll(requestParams);
						if (!GenericUtil.isEmpty(o.getPostJsCode())) {
							String[] ar1 = o.getPostJsCode().split("&");
							for (int it4 = 0; it4 < ar1.length; it4++) {
								String[] ar2 = ar1[it4].split("=");
								if (ar2.length == 2 && ar2[0] != null && ar2[1] != null)
									paramMap.put(ar2[0], ar2[1]);
							}
						}
						obz = queryEngine.executeQuery(scd, o.getObjectId(), paramMap);
						break;
					case 8:// component
						obz = FrameworkCache.getComponent(scd, o.getObjectId());//metaDataDao.loadComponent(scd, o.getObjectId(), new HashMap());
						break;
					case 10: // KPI Single Card
						obz = queryEngine.executeQuery(scd, o.getObjectId(), new HashMap());
						break;
					case 5: // dbFunc
						obz = scriptEngine.executeGlobalFunc(scd, o.getObjectId(), requestParams, (short) 1);
						break;
					case 9: // graph dashboard
						W5BIGraphDashboard obz2 = (W5BIGraphDashboard) dao.getCustomizedObject(
								"from W5BIGraphDashboard t where t.graphDashboardId=? AND t.projectUuid=?",
								o.getObjectId(), scd.get("projectId"), null);
						if (accessControl) {
							obz = obz2;
						} else {
							obz = "graph" + o.getObjectId();
						}
					}
					if (pr.getPage().getTemplateTip() != 9 && objectCount == 0) { // daha
																					// ilk
																					// objede
																					// sorun
																					// varsa
																					// exception
																					// ver
						if (obz instanceof String)
							throw new IWBException("security", "Module", o.getObjectId(), null,
									"Role Access Control(Page Object)", null);

					}
					if (obz != null)
						pr.getPageObjectList().add(obz);
					objectCount++;
				}
			}
			if ((Integer) scd.get("customizationId") == 1 && GenericUtil.uInt(scd.get("mainTemplateId")) == pageId) {
				List<Object> params = new ArrayList();
				params.add(scd.get("projectId"));
				dao.executeUpdateSQLQuery(
						"update iwb.w5_project p set preview_count=preview_count+1 where p.project_uuid=?", params);
			}
			return pr;
		} catch (Exception e) {
			throw new IWBException("framework", "Load.Page", pageId, null,
					"[63," + pageId + "]" + (pr != null && pr.getPage() != null ? " " + pr.getPage().getDsc() : ""), e);
		}
	}
	

	public W5FormCellHelper reloadFormCell(Map<String, Object> scd, int fcId, String webPageId, String tabId) {
		String projectId = FrameworkCache.getProjectId(scd, null);
		int customizationId = (Integer) scd.get("customizationId");
		// W5Customization cus =
		// FrameworkCache.wCustomizationMap.get(customizationId);
		int userId = (Integer) scd.get("userId");
		W5FormCell c = (W5FormCell) dao.getCustomizedObject(
				"from W5FormCell fc where fc.formCellId=? AND fc.projectUuid=?", fcId, projectId, null);
		if (c == null)
			return null;
		W5FormCellHelper rc = new W5FormCellHelper(c);
		String includedValues = c.getLookupIncludedValues();
		Map<String, String> requestParams = null;
		switch (c.getControlTip()) {
		case 58: // superboxselect
		case 8: // lovcombo static
		case 6: // eger static combobox ise listeyi load et
			W5LookUp lookUp = FrameworkCache.getLookUp(scd, c.getLookupQueryId());
			rc.setLocaleMsgFlag((short) 1);
			requestParams = UserUtil.getTableGridFormCellReqParams(projectId, -c.getLookupQueryId(), userId,
					(String) scd.get("sessionId"), webPageId, tabId, -fcId);
			List<W5LookUpDetay> oldList = (List<W5LookUpDetay>) dao.find(
					"from W5LookUpDetay t where t.projectUuid=? AND t.lookUpId=? order by t.tabOrder", projectId,
					c.getLookupQueryId());

			List<W5LookUpDetay> newList = null;
			if (includedValues != null && includedValues.length() > 0) {
				// List<W5LookUpDetay> oldList = lookUp.get_detayList();
				boolean notInFlag = false;
				if (includedValues.charAt(0) == '!') {
					notInFlag = true;
					includedValues = includedValues.substring(1);
				}
				String[] ar1 = includedValues.split(",");
				newList = new ArrayList<W5LookUpDetay>(oldList.size());
				for (W5LookUpDetay p : oldList)
					if ((rc.getValue() != null && p.getVal().equals(rc.getValue())) || p.getActiveFlag() != 0) {
						boolean in = false;
						for (int it4 = 0; it4 < ar1.length; it4++)
							if (ar1[it4].equals(p.getVal())) {
								in = true;
								break;
							}
						if (in ^ notInFlag)
							newList.add(p);
					}
			} else if (requestParams != null && requestParams.get("_lsc" + c.getFormCellId()) != null) {
				String[] lsc = requestParams.get("_lsc" + c.getFormCellId()).split(",");
				newList = new ArrayList<W5LookUpDetay>();
				for (String q : lsc) {
					newList.add(lookUp.get_detayMap().get(q));
				}
			} else {
				newList = new ArrayList<W5LookUpDetay>(oldList.size());
				for (W5LookUpDetay p : oldList)
					if ((rc.getValue() != null && p.getVal().equals(rc.getValue())) || p.getActiveFlag() != 0)
						newList.add(p);
				// newList = lookUp.get_detayList();
			}
			List<W5LookUpDetay> newList2 = new ArrayList<W5LookUpDetay>(newList.size());
			for (W5LookUpDetay ld : newList) {
				newList2.add(ld);
			}
			rc.setLookupListValues(newList2);
			break;
		case 7:
		case 15:
		case 59: // dynamic query, lovcombo, superbox
		case 23:
		case 24:
		case 55: // tree combo and treepanel
			Map paramMap = new HashMap();
			requestParams = UserUtil.getTableGridFormCellReqParams((String) scd.get("projectId"), c.getLookupQueryId(),
					userId, (String) scd.get("sessionId"), webPageId, tabId, -fcId);
			String includedParams = GenericUtil.filterExt(c.getLookupIncludedParams(), scd, requestParams, null)
					.toString();
			if (includedParams != null && includedParams.length() > 2) {
				String[] ar1 = includedParams.split("&");
				for (int it4 = 0; it4 < ar1.length; it4++) {
					String[] ar2 = ar1[it4].split("=");
					if (ar2.length == 2 && ar2[0] != null && ar2[1] != null)
						paramMap.put(ar2[0], ar2[1]);
				}
			}

			W5QueryResult lookupQueryResult = metaDataDao.getQueryResult(scd, c.getLookupQueryId());
			lookupQueryResult.setErrorMap(new HashMap());
			lookupQueryResult.setRequestParams(requestParams);
			lookupQueryResult.setOrderBy(lookupQueryResult.getQuery().getSqlOrderby());
			if (rc.getValue() != null && rc.getValue().length() > 0
					&& GenericUtil.hasPartInside("7,10,61", Short.toString(c.getControlTip())))
				paramMap.put("pmust_load_id", rc.getValue());
			switch (lookupQueryResult.getQuery().getQueryTip()) {
			case 12:
				lookupQueryResult.prepareTreeQuery(paramMap);
				break; // lookup tree query
			default:
				lookupQueryResult.prepareQuery(paramMap);
			}
			rc.setLookupQueryResult(lookupQueryResult);

			if (lookupQueryResult.getErrorMap().isEmpty()) {
				dao.runQuery(lookupQueryResult);
				if (tabId != null && lookupQueryResult.getQuery().getMainTableId() != 0) {
					Set<Integer> keys = UserUtil.getTableGridFormCellCachedKeys((String) scd.get("projectId"),
							lookupQueryResult.getQuery().getMainTableId(), userId, (String) scd.get("sessionId"),
							requestParams.get(".w"), tabId, -c.getFormCellId(), requestParams, true);
					for (Object[] o : lookupQueryResult.getData())
						keys.add(GenericUtil.uInt(o[1]));
				}
			}

			break;
		}
		return rc;
	}
	
	public M5ListResult getMListResult(Map<String, Object> scd, int listId, Map<String, String> parameterMap) {
		return metaDataDao.getMListResult(scd, listId, parameterMap, false);
	}
}
