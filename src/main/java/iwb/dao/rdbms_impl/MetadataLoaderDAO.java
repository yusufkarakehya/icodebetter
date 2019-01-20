package iwb.dao.rdbms_impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import iwb.cache.FrameworkCache;
import iwb.cache.FrameworkSetting;
import iwb.cache.LocaleMsgCache;
import iwb.domain.db.Log5Feed;
import iwb.domain.db.M5List;
import iwb.domain.db.W5Card;
import iwb.domain.db.W5Component;
import iwb.domain.db.W5Conversion;
import iwb.domain.db.W5ConversionCol;
import iwb.domain.db.W5Customization;
import iwb.domain.db.W5Form;
import iwb.domain.db.W5FormCell;
import iwb.domain.db.W5FormModule;
import iwb.domain.db.W5FormSmsMail;
import iwb.domain.db.W5GlobalFunc;
import iwb.domain.db.W5GlobalFuncParam;
import iwb.domain.db.W5Grid;
import iwb.domain.db.W5GridColumn;
import iwb.domain.db.W5Jasper;
import iwb.domain.db.W5JasperReport;
import iwb.domain.db.W5JobSchedule;
import iwb.domain.db.W5List;
import iwb.domain.db.W5ListBase;
import iwb.domain.db.W5ListColumn;
import iwb.domain.db.W5LookUp;
import iwb.domain.db.W5LookUpDetay;
import iwb.domain.db.W5ObjectToolbarItem;
import iwb.domain.db.W5Page;
import iwb.domain.db.W5PageObject;
import iwb.domain.db.W5Project;
import iwb.domain.db.W5Query;
import iwb.domain.db.W5QueryField;
import iwb.domain.db.W5QueryParam;
import iwb.domain.db.W5Table;
import iwb.domain.db.W5TableChild;
import iwb.domain.db.W5TableEvent;
import iwb.domain.db.W5TableField;
import iwb.domain.db.W5TableParam;
import iwb.domain.db.W5Workflow;
import iwb.domain.db.W5WorkflowStep;
import iwb.domain.db.W5Ws;
import iwb.domain.db.W5WsMethod;
import iwb.domain.db.W5WsServer;
import iwb.domain.db.W5WsServerMethod;
import iwb.domain.db.W5WsServerMethodParam;
import iwb.domain.helper.W5FormCellHelper;
import iwb.domain.helper.W5TableRecordHelper;
import iwb.domain.result.M5ListResult;
import iwb.domain.result.W5CardResult;
import iwb.domain.result.W5FormResult;
import iwb.domain.result.W5GlobalFuncResult;
import iwb.domain.result.W5GridResult;
import iwb.domain.result.W5JasperResult;
import iwb.domain.result.W5ListViewResult;
import iwb.domain.result.W5PageResult;
import iwb.domain.result.W5QueryResult;
import iwb.enums.FieldDefinitions;
import iwb.exception.IWBException;
import iwb.util.GenericUtil;
import iwb.util.UserUtil;

@Repository
public class MetadataLoaderDAO extends BaseDAO {
	private static Logger logger = Logger.getLogger(MetadataLoaderDAO.class);
	@Lazy
	@Autowired
	private PostgreSQL dao;

	
	private boolean loadForm(W5FormResult f) {
		// f.setForm((W5Form)loadObject(W5Form.class, f.getFormId()));

		// int customizationId = (Integer)f.getScd().get("customizationId");
		String projectId = FrameworkCache.getProjectId(f.getScd(), "40." + f.getFormId());

		W5Form form = (W5Form) getCustomizedObject("from W5Form t where t.formId=? and t.projectUuid=?", f.getFormId(),
				projectId, "Form"); // ozel bir client icin varsa
		f.setForm(form);

		f.getForm().set_formCells(
				find("from W5FormCell t where t.formId=? AND t.projectUuid=? order by t.tabOrder, t.xOrder, t.dsc",
						f.getFormId(), projectId));

		for (W5FormCell fc : f.getForm().get_formCells())
			switch (fc.getControlTip()) {
			case 31: // code_list
				if (GenericUtil.uInt(fc.getLookupIncludedParams()) == 0) {
					fc.setControlTip((short) 1);
				} else
					fc.set_formCellCodeDetailList(
							find("from W5FormCellCodeDetail t where t.formCellId=? AND t.projectUuid=? order by t.tabOrder",
									fc.getFormCellId(), projectId));
				break;
			}
		if (form.getRenderTip() != 0) { // eger baska turlu render edilecekse
			form.set_moduleList(find("from W5FormModule t where t.formId=? AND t.projectUuid=? order by t.tabOrder",
					form.getFormId(), projectId));
		}

		HashMap hlps = new HashMap();

		f.getForm().set_toolbarItemList(
				find("from W5ObjectToolbarItem t where t.objectTip=? AND t.objectId=? AND t.projectUuid=? order by t.tabOrder",
						(short) 15, f.getFormId(), projectId));
		f.getForm().set_formHintList(
				find("from W5FormHint h where h.activeFlag=1 AND h.formId=? AND h.projectUuid=? order by h.tabOrder",
						f.getFormId(), projectId));
		if (f.getForm().get_formHintList().isEmpty())
			f.getForm().set_formHintList(null);

		if (f.getForm().getObjectTip() != 1 && f.getForm().getRenderTemplateId() != 0) { // grid(seachForm)
																							// degilse
																							// ve
																							// templateId
																							// varsa
			W5Page template = (W5Page) getCustomizedObject("from W5Page t where t.templateId=? and t.projectUuid=?",
					f.getForm().getRenderTemplateId(), projectId, null); // ozel
																			// bir
																			// client
																			// icin
																			// varsa
			f.getForm().set_renderTemplate(template);
		}
		Map<Short, W5Workflow> mam = null;
		W5Table mt = null;
		switch (f.getForm().getObjectTip()) {
		case 6: // conversion icin
			W5Conversion c = FrameworkCache.getConversion(projectId, f.getForm().getObjectId());
			W5Table srct = FrameworkCache.getTable(projectId, c.getSrcTableId());
			W5Table dstt = FrameworkCache.getTable(projectId, c.getDstTableId());
			// f.getForm().set_sourceTable(dstt);
			for (W5FormCell fc : f.getForm().get_formCells())
				if (fc.getObjectDetailId() != 0) {
					fc.set_sourceObjectDetail(c.get_conversionColMap().get(fc.getObjectDetailId()));
				}

			break;
		case 2: // table icin ise
			// f.setTable((W5Table)loadObject(W5Table.class,
			// f.getForm().getObjectId()));
			W5Table t = FrameworkCache.getTable(projectId, f.getForm().getObjectId());
			// f.getForm().set_sourceTable(t);
			Map<String, W5TableField> fieldMap1 = new HashMap();
			for (W5TableField tf : (List<W5TableField>) t.get_tableFieldList()) {
				fieldMap1.put(tf.getDsc(), tf);
			}
			for (W5FormCell fc : f.getForm().get_formCells())
				if (fc.getObjectDetailId() != 0) {
					fc.set_sourceObjectDetail(t.get_tableFieldMap().get(fc.getObjectDetailId()));
				}
			if ((fieldMap1.get("INSERT_USER_ID") != null || fieldMap1.get("insert_user_id") != null)
					&& (fieldMap1.get("VERSION_USER_ID") != null || fieldMap1.get("version_user_id") != null)) {
				f.getForm().set_versioningFlag(true);
			}
			if (FrameworkSetting.sms || FrameworkSetting.mail) {
				f.getForm().set_formSmsMailList(
						find("from W5FormSmsMail t where t.projectUuid=? AND t.formId=? AND t.activeFlag=1 order by t.smsMailSentTip,t.tabOrder",
								projectId, f.getFormId()));
				if (f.getForm().get_formSmsMailList().isEmpty())
					f.getForm().set_formSmsMailList(null);
				else {
					f.getForm().set_formSmsMailMap(new HashMap());
					for (W5FormSmsMail fsm : f.getForm().get_formSmsMailList()) {
						// if(fsm.getSmsMailTip()==0)f.getForm().set_mailFlag(true);
						// else
						// f.getForm().set_smsFlag(true);
						f.getForm().get_formSmsMailMap().put(fsm.getFormSmsMailId(), fsm);
					}
				}
			}

			f.getForm().set_conversionList(
					find("from W5Conversion t where t.projectUuid=? AND ((t.srcDstTip=0 AND t.srcFormId=?) OR (t.srcDstTip=1 AND t.srcTableId=?)) AND t.activeFlag=1 order by t.tabOrder",
							projectId, f.getFormId(), f.getForm().getObjectId()));
			for (W5Conversion cnv : f.getForm().get_conversionList()) {
				cnv.set_conversionColList(
						find("from W5ConversionCol t where t.projectUuid=? AND t.conversionId=? order by t.tabOrder",
								projectId, cnv.getConversionId()));
				FrameworkCache.addConversion(projectId, cnv);
			}

			break;
		case 1: // grid icin ise
			Object[] ooo = (Object[]) find(
					"select t.queryId,(select q.mainTableId from W5Query q where q.queryId=t.queryId AND q.projectUuid=t.projectUuid) from W5Grid t where t.projectUuid=? AND t.gridId=?",
					projectId, f.getForm().getObjectId()).get(0);
			int queryId = (Integer) ooo[0];
			int mainTableId = (Integer) ooo[1];
			if (mainTableId > 0)
				mt = FrameworkCache.getTable(projectId, mainTableId); // f.getForm().set_sourceTable()
			W5Query query = new W5Query(queryId);
			query.set_queryParams(find("from W5QueryParam t where t.projectUuid=? AND t.queryId=? order by t.tabOrder",
					projectId, queryId));
			f.getForm().set_sourceQuery(query);
			Map<Integer, W5QueryParam> fieldMap2 = new HashMap();
			for (W5QueryParam tf : f.getForm().get_sourceQuery().get_queryParams()) {
				fieldMap2.put(tf.getQueryParamId(), tf);
			}
			for (W5FormCell fc : f.getForm().get_formCells())
				if (fc.getObjectDetailId() != 0) {
					if (fc.getObjectDetailId() > 0)
						fc.set_sourceObjectDetail(fieldMap2.get(fc.getObjectDetailId())); // queryField'dan
					else if (mt != null) {
						fc.set_sourceObjectDetail(mt.get_tableFieldMap().get(-fc.getObjectDetailId()));
					}
				}
			// onay mekanizmasi icin
			if (mt != null)
				mam = mt.get_approvalMap();

			break;
		case 3:
		case 4: // db func
			W5GlobalFunc dbf = FrameworkCache.getGlobalFunc(projectId, f.getForm().getObjectId());
			if (dbf == null) {
				dbf = (W5GlobalFunc) getCustomizedObject("from W5GlobalFunc t where t.dbFuncId=? AND t.projectUuid=?",
						f.getForm().getObjectId(), projectId, "GlobalFunc");
				dbf.set_dbFuncParamList((List<W5GlobalFuncParam>) find(
						"from W5GlobalFuncParam t where t.projectUuid=? AND t.dbFuncId=? order by t.tabOrder",
						projectId, f.getForm().getObjectId()));
				FrameworkCache.addGlobalFunc(projectId, dbf);
			}
			Map<Integer, W5GlobalFuncParam> fieldMap3 = new HashMap();
			for (W5GlobalFuncParam tf : dbf.get_dbFuncParamList()) {
				fieldMap3.put(tf.getDbFuncParamId(), tf);
			}
			for (W5FormCell fc : f.getForm().get_formCells())
				if (fc.getObjectDetailId() != 0) {
					fc.set_sourceObjectDetail(fieldMap3.get(fc.getObjectDetailId()));
				}
		}

		// StringBuilder autoExtraJSConstructor = new StringBuilder();
		for (W5FormCell fc : f.getForm().get_formCells())
			switch (fc.getControlTip()) {
			case 99: // grid
				W5GridResult gridResult = new W5GridResult(fc.getLookupQueryId());
				gridResult.setRequestParams(f.getRequestParams());
				gridResult.setScd(f.getScd());

				W5Grid g = FrameworkCache.getGrid(projectId, fc.getLookupQueryId());
				if (g != null) {
					gridResult.setGrid(g);
				} else {
					loadGrid(gridResult);
					g = gridResult.getGrid();
					FrameworkCache.addGrid(projectId, g);
				}
				fc.set_sourceObjectDetail(g);
				break;
			case 31: // code_list
				fc.set_formCellCodeDetailList(
						find("from W5FormCellCodeDetail t where t.formCellId=? AND t.projectUuid=? order by t.tabOrder",
								fc.getFormCellId(), projectId));
				break;
			}

		if (mam != null && !mam.isEmpty()) { // map of ApprovalManagement
			int maxFirstColumnTabOrder = 0;
			for (W5FormCell c : f.getForm().get_formCells())
				if (c.getFormModuleId() == 0 && c.getTabOrder() < 1000) {
					maxFirstColumnTabOrder++;
				}
			for (short actionTip : mam.keySet()) {
				W5FormCell approvalCell = new W5FormCell();
				approvalCell.setTabOrder((short) (990 + actionTip));
				approvalCell.setDsc("_approval_step_ids" + actionTip);
				approvalCell.setControlTip((short) 15); // low-combo query
				approvalCell.setLookupQueryId(606); // approval steps
				approvalCell.setLookupIncludedParams("xapproval_id=" + mam.get(actionTip).getApprovalId());
				approvalCell.setControlWidth((short) 250);
				approvalCell.setLocaleMsgKey("approval_status"); // mam.get(actionTip).getDsc()
				approvalCell.setInitialSourceTip((short) 10); // approvalStates
				// approvalCell.setInitialValue(""+mam.get(actionTip).getApprovalId());//approvalId
				approvalCell.setActiveFlag((short) 1);
				f.getForm().get_formCells().add(/* maxFirstColumnTabOrder, */ approvalCell);
			}
		}
		return true;
	}

	public W5FormResult getFormResult(Map<String, Object> scd, int formId, int action,
			Map<String, String> requestParams) {
		W5FormResult formResult = new W5FormResult(formId);
		formResult.setScd(scd);
		formResult.setErrorMap(new HashMap());
		formResult.setAction(action);
		formResult.setRequestParams(requestParams);
		formResult.setOutputFields(new HashMap());
		formResult.setPkFields(new HashMap());
		formResult.setOutputMessages(new ArrayList());
		formResult.setExtraFormCells(new ArrayList());
		W5Form f = FrameworkCache.getForm(scd, formId);
		if (f == null) {
			loadForm(formResult);
			f = formResult.getForm();
			FrameworkCache.addForm(scd, formResult.getForm());
		}
		formResult.setForm(f);

		if (formResult.getForm().get_moduleList() != null) { // eger baska turlu
																// render
																// edilecekse
			for (W5FormModule m : formResult.getForm().get_moduleList())
				if (GenericUtil.accessControl(scd, m.getAccessViewTip(), m.getAccessViewRoles(),
						m.getAccessViewUsers())) { // form
					switch (m.getModuleTip()) {
					case 5: // grid
						if (formResult.getModuleGridMap() == null)
							formResult.setModuleGridMap(new HashMap());
						formResult.getModuleGridMap().put(m.getObjectId(),
								getGridResult(scd, m.getObjectId(), requestParams, true));
						break;
					case 6: // query4formcell
						W5QueryResult queryResult4FormCell = dao.executeQuery(scd, m.getObjectId(), requestParams);
						if (formResult.getFormCellResults() == null)
							formResult.setFormCellResults(
									new ArrayList<W5FormCellHelper>(queryResult4FormCell.getData().size()));
						for (Object[] d : queryResult4FormCell.getData()) {
							W5FormCellHelper result = GenericUtil.getFormCellResultByQueryRecord(d);
							if (m.getTabOrder() != 0)
								result.getFormCell()
										.setTabOrder((short) (result.getFormCell().getTabOrder() + m.getTabOrder()));
							formResult.getFormCellResults().add(result);
						}
						break;
					case 10:// mobile list
						if (formResult.getModuleListMap() == null)
							formResult.setModuleListMap(new HashMap());
						formResult.getModuleListMap().put(m.getObjectId(),
								getMListResult(scd, m.getObjectId(), requestParams, true));

					}
				}
		}

		return formResult;
	}

	private void loadQuery(W5QueryResult queryResult, String projectId) {
		W5Query query = (W5Query) find("from W5Query t where t.queryId=? AND t.projectUuid=?", queryResult.getQueryId(),
				projectId).get(0); // ozel bir
									// client
									// icin
									// varsa
		queryResult.setQuery(query);
		queryResult.getQuery().set_queryFields(
				find("from W5QueryField t where t.queryId=? AND t.tabOrder>0 AND t.postProcessTip!=99 AND t.projectUuid=? order by t.tabOrder",
						queryResult.getQueryId(), projectId));
		queryResult.getQuery()
				.set_queryParams(find("from W5QueryParam t where t.queryId=? AND t.projectUuid=? order by t.tabOrder",
						queryResult.getQueryId(), projectId));

		if (queryResult.getQuery().getShowParentRecordFlag() != 0)
			for (W5QueryField field : queryResult.getQuery().get_queryFields()) {
				if (field.getDsc().equals("table_id"))
					query.set_tableIdTabOrder(field.getTabOrder());
				if (field.getDsc().equals("table_pk"))
					query.set_tablePkTabOrder(field.getTabOrder());
			}
	}

	public W5QueryResult getQueryResult(Map<String, Object> scd, int queryId) {
		if (scd != null && (Integer) scd.get("customizationId") > 0)
			switch (queryId) { // tenant user and role conversion
			case 43:
				queryId = 4511;
				break; // lookup_user1
			case 554:
				queryId = 4512;
				break; // lookup_role1
			}
		W5QueryResult queryResult = new W5QueryResult(queryId);
		queryResult.setScd(scd);
		String projectId = FrameworkCache.getProjectId(scd, "8." + queryId);
		queryResult.setQuery(FrameworkCache.getQuery(projectId, queryId));
		if (queryResult.getQuery() == null) {
			loadQuery(queryResult, projectId);
			FrameworkCache.addQuery(projectId, queryResult.getQuery());
		}

		switch (queryResult.getQuery().getQuerySourceTip()) {
		case 0:
		case 15:
			if (queryResult.getQuery().getMainTableId() != 0) {
				queryResult.setMainTable(FrameworkCache.getTable(projectId, queryResult.getQuery().getMainTableId()));
			}
		}
		return queryResult;
	}

	public W5PageResult getPageResult(Map<String, Object> scd, int pageId) {

		W5PageResult pr = new W5PageResult(pageId);
		pr.setScd(scd);

		pr.setPage(FrameworkCache.getPage(scd, pageId));
		if (pr.getPage() == null) {
			loadPage(pr);
			FrameworkCache.addPage(scd, pr.getPage());
		}

		return pr;
	}

	private void loadPage(W5PageResult tr) {

		String projectId = FrameworkCache.getProjectId(tr.getScd(), "63." + tr.getTemplateId());
		W5Page page = (W5Page) getCustomizedObject("from W5Page t where t.templateId=? and t.projectUuid=?",
				tr.getTemplateId(), projectId, "Page"); // ozel bir client icin
														// varsa
		tr.setPage(page);
		page.set_pageObjectList(
				find("from W5PageObject t where t.activeFlag=1 AND t.templateId=? AND t.projectUuid=? order by t.tabOrder",
						tr.getTemplateId(), projectId));

		if (GenericUtil.isEmpty(page.getCode()) || page.getCode().startsWith("!"))
			for (W5PageObject to : page.get_pageObjectList())
				if (to.getSrcQueryFieldId() != null && to.getDstQueryParamId() != null) {
					List p = new ArrayList();
					p.add(projectId);
					p.add(to.getSrcQueryFieldId());
					p.add(projectId);
					p.add(to.getDstQueryParamId());
					String sql = "select (select f1.dsc from iwb.w5_query_field f1 where f1.project_uuid=? AND f1.query_field_id=?) f_dsc, "
							+ "(select p1.dsc from iwb.w5_query_param p1 where p1.project_uuid=? AND p1.query_param_id=?) p_dsc";
					if (to.getDstStaticQueryParamId() != null && !GenericUtil.isEmpty(to.getDstStaticQueryParamVal())) {
						p.add(projectId);
						p.add(to.getDstStaticQueryParamId());
						sql += ",(select p1.dsc from iwb.w5_query_param p1 where p1.project_uuid=? AND p1.query_param_id=?) ps_dsc";
					}
					List l = executeSQLQuery2Map(sql, p);
					if (!GenericUtil.isEmpty(l)) {
						Map m = (Map) l.get(0);
						to.set_srcQueryFieldName((String) m.get("f_dsc"));
						to.set_dstQueryParamName((String) m.get("p_dsc"));
						if (m.containsKey("ps_dsc"))
							to.set_dstStaticQueryParamName((String) m.get("ps_dsc"));
					}
				}
	}

	public W5CardResult getDataViewResult(Map<String, Object> scd, int dataViewId, Map<String, String> requestParams,
			boolean noSearchForm) {
		W5CardResult cr = new W5CardResult(dataViewId);
		String projectId = FrameworkCache.getProjectId(scd, "930." + dataViewId);
		cr.setRequestParams(requestParams);
		cr.setScd(scd);

		cr.setCard(FrameworkCache.getCard(projectId, dataViewId));
		if (cr.getCard() == null) {
			loadCard(cr);
			FrameworkCache.addCard(projectId, cr.getCard());
		}
		// search Form
		if (!noSearchForm && cr.getCard().get_searchFormId() != 0) {
			W5FormResult searchForm = getFormResult(scd, cr.getCard().get_searchFormId(), 2, requestParams);
			dao.initializeForm(searchForm, false);
			dao.loadFormCellLookups(cr.getScd(), searchForm.getFormCellResults(), cr.getRequestParams(), null);
			cr.setSearchFormResult(searchForm);
		}
		return cr;
	}

	private void loadCard(W5CardResult dr) {
		String projectId = FrameworkCache.getProjectId(dr.getScd(), "930." + dr.getDataViewId());
		W5Card c = (W5Card) getCustomizedObject("from W5Card t where t.dataViewId=? and t.projectUuid=?",
				dr.getDataViewId(), projectId, "DataView"); // ozel bir client
															// icin varsa
		dr.setCard(c);

		if (!GenericUtil.isEmpty(c.getOrderQueryFieldIds()))
			c.set_orderQueryFieldNames(
					find("select qf.dsc from W5QueryField qf where qf.queryId=? and qf.projectUuid=? AND qf.queryFieldId in ("
							+ c.getOrderQueryFieldIds() + ") order by qf.tabOrder", c.getQueryId(), projectId));

		W5Query query = null;
		query = getQueryResult(dr.getScd(), c.getQueryId()).getQuery();

		if (query == null) {
			query = new W5Query();
			List<W5QueryField> queryFields = find(
					"from W5QueryField t where t.queryId=? and t.projectUuid=? order by t.tabOrder", c.getQueryId(),
					projectId);
			c.set_query(query);
			query.set_queryFields(queryFields); // dataReader icin gerekli
			query.setMainTableId(
					(Integer) find("select t.mainTableId from W5Query t where t.queryId=? and t.projectUuid=?",
							c.getQueryId(), projectId).get(0));
		} else
			c.set_query(query);

		c.set_crudTable(FrameworkCache.getTable(projectId, query.getMainTableId()));

		Map<Integer, W5QueryField> fieldMap = new HashMap<Integer, W5QueryField>();
		Map<String, W5QueryField> fieldMapDsc = new HashMap<String, W5QueryField>();
		for (W5QueryField field : query.get_queryFields()) {
			fieldMap.put(field.getQueryFieldId(), field);
			fieldMapDsc.put(field.getDsc(), field);
		}

		c.set_queryFieldMap(fieldMap);

		c.set_queryFieldMapDsc(fieldMapDsc);
		c.set_pkQueryField(fieldMap.get(c.getPkQueryFieldId()));

		c.set_toolbarItemList(
				find("from W5ObjectToolbarItem t where t.objectTip=8 AND t.objectId=? AND t.projectUuid=? order by t.tabOrder",
						c.getDataViewId(), projectId));
		c.set_menuItemList(
				find("from W5ObjectMenuItem t where t.objectTip=8 AND t.objectId=? AND t.projectUuid=? order by t.tabOrder",
						c.getDataViewId(), projectId));
		Integer searchFormId = (Integer) getCustomizedObject(
				"select t.formId from W5Form t where t.objectTip=8 and t.objectId=? and t.projectUuid=? ",
				dr.getDataViewId(), projectId, null);
		if (searchFormId != null)
			c.set_searchFormId(searchFormId);
		if (c.getDefaultCrudFormId() != 0) {
			W5Form defaultCrudForm = (W5Form) getCustomizedObject("from W5Form t where t.formId=? and t.projectUuid=?",
					c.getDefaultCrudFormId(), projectId, "Form"); // ozel bir
																	// client
																	// icin
																	// varsa

			if (defaultCrudForm != null) {
				// defaultCrudForm.set_sourceTable(PromisCache.getTable(customizationId,
				// defaultCrudForm.getObjectId()));
				W5Table t = FrameworkCache.getTable(projectId, defaultCrudForm.getObjectId()); // PromisCache.getTable(f.getScd(),
																								// f.getForm().getObjectId())
				c.set_defaultCrudForm(defaultCrudForm);

				c.set_crudFormSmsMailList(
						find("from W5FormSmsMail t where t.activeFlag=1 AND t.actionTips like '%0%' AND t.formId=? AND t.projectUuid=? order by t.tabOrder",
								c.getDefaultCrudFormId(), projectId));
				c.set_crudFormConversionList(
						find("from W5Conversion t where t.activeFlag=1 AND t.actionTips like '%0%' AND t.srcFormId=? AND t.projectUuid=? order by t.tabOrder",
								c.getDefaultCrudFormId(), projectId));

				organizeListPostProcessQueryFields(dr.getScd(), t, c);

			}
		}
	}

	public W5GridResult getGridResult(Map<String, Object> scd, int gridId, Map<String, String> requestParams,
			boolean noSearchForm) {
		W5Grid g = null;
		try {
			String projectId = FrameworkCache.getProjectId(scd, "5." + gridId);
			W5GridResult gridResult = new W5GridResult(gridId);
			gridResult.setRequestParams(requestParams);
			gridResult.setScd(scd);
			g = FrameworkCache.getGrid(projectId, gridId);
			if (g != null) {
				gridResult.setGrid(g);
			} else {
				loadGrid(gridResult);
				g = gridResult.getGrid();
				FrameworkCache.addGrid(projectId, g);
			}

			// search Form
			if (!noSearchForm && g.get_searchFormId() != 0)
				try {
					W5FormResult searchForm = getFormResult(scd, g.get_searchFormId(), 2, requestParams);
					dao.initializeForm(searchForm, false);
					dao.loadFormCellLookups(scd, searchForm.getFormCellResults(), requestParams, null);
					gridResult.setSearchFormResult(searchForm);
				} catch (Exception e) {
					throw new IWBException("framework", "Load.SearchForm", g.get_searchFormId(), null,
							"[40," + g.get_searchFormId() + "]", e);
				}

			gridResult.setFormCellResultMap(new HashMap());

			for (W5GridColumn column : g.get_gridColumnList())
				if (column.get_formCell() != null) {
					W5FormCellHelper cellResult = new W5FormCellHelper(column.get_formCell());
					gridResult.getFormCellResultMap().put(column.get_formCell().getFormCellId(), cellResult);
				}

			if (!gridResult.getFormCellResultMap().isEmpty())
				dao.loadFormCellLookups(gridResult.getScd(), new ArrayList(gridResult.getFormCellResultMap().values()),
						gridResult.getRequestParams(), null);

			return gridResult;
		} catch (Exception e) {
			throw new IWBException("framework", "Load.Grid", gridId, null,
					"[5," + gridId + "]" + (g != null ? " " + g.getDsc() : ""), e);
		}
	}

	private void loadGrid(W5GridResult gr) {
		String projectId = FrameworkCache.getProjectId(gr.getScd(), "5." + gr.getGridId());

		W5Grid grid = (W5Grid) getCustomizedObject("from W5Grid t where t.gridId=? and t.projectUuid=?", gr.getGridId(),
				projectId, "Grid"); // ozel bir client icin varsa
		gr.setGrid(grid);

		grid.set_gridColumnList(find("from W5GridColumn t where t.projectUuid=? AND t.gridId=? order by t.tabOrder",
				projectId, gr.getGridId()));
		switch (grid.getRowColorFxTip()) {
		case 1:
			if (grid.getRowColorFxQueryFieldId() != 0) { // lookup eslesme
				grid.set_listCustomGridColumnRenderer(
						find("from W5CustomGridColumnRenderer t where t.projectUuid=? AND t.gridId=? AND t.queryFieldId=?",
								projectId, gr.getGridId(), grid.getRowColorFxQueryFieldId()));
			}
			break;
		case 2:
		case 3:
			if (grid.getRowColorFxQueryFieldId() != 0 || grid.getRowColorFxTip() == 3) { // kosul
				grid.set_listCustomGridColumnCondition(
						find("from W5CustomGridColumnCondition t where t.projectUuid=? AND t.gridId=? AND t.queryFieldId=? order by t.tabOrder",
								projectId, gr.getGridId(), grid.getRowColorFxQueryFieldId()));
			}
			break;
		}

		W5Query query = getQueryResult(gr.getScd(), grid.getQueryId()).getQuery();

		if (query == null) {
			query = new W5Query();
			List<W5QueryField> queryFields = find(
					"from W5QueryField t where t.projectUuid=? AND t.queryId=? order by t.tabOrder", projectId,
					grid.getQueryId());
			grid.set_query(query);
			query.set_queryFields(queryFields); // dataReader icin gerekli
			query.setMainTableId((Integer) getCustomizedObject(
					"select t.mainTableId from W5Query t where t.queryId=? AND t.projectUuid=?", grid.getQueryId(),
					projectId, null));
		} else
			grid.set_query(query);

		grid.set_viewTable(FrameworkCache.getTable(projectId, query.getMainTableId()));

		Map<Integer, W5QueryField> fieldMap = new HashMap<Integer, W5QueryField>();
		Map<String, W5QueryField> fieldMapDsc = new HashMap<String, W5QueryField>();
		for (W5QueryField field : query.get_queryFields()) {
			fieldMap.put(field.getQueryFieldId(), field);
			fieldMapDsc.put(field.getDsc(), field);
		}
		if (grid.get_viewTable() != null) { // extended fields
			int qi = 0;
			for (qi = 0; qi < grid.get_viewTable().get_tableFieldList().size(); qi++) {
				W5TableField tf = grid.get_viewTable().get_tableFieldList().get(qi);
			}
		}
		grid.set_queryFieldMap(fieldMap);

		grid.set_queryFieldMapDsc(fieldMapDsc);
		grid.set_autoExpandField(fieldMap.get(grid.getAutoExpandFieldId()));
		grid.set_pkQueryField(fieldMap.get(grid.getPkQueryFieldId()));
		if (grid.get_pkQueryField() == null) {
			if (FrameworkSetting.debug)
				throw new IWBException("framework", "Grid", grid.getGridId(), null, "Grid PK Missing", null);
			grid.set_pkQueryField(query.get_queryFields().get(0));
		}
		grid.set_groupingField(fieldMap.get(grid.getGroupingFieldId()));
		grid.set_fxRowField(fieldMap.get(grid.getRowColorFxQueryFieldId()));

		int formCellCounter = 1;
		for (W5GridColumn column : grid.get_gridColumnList()) {
			column.set_queryField(fieldMap.get(column.getQueryFieldId()));
			if (column.getFormCellId() > 0) { // form_cell
				W5FormCell cell = (W5FormCell) getCustomizedObject(
						"from W5FormCell t where t.formCellId=? and t.projectUuid=?", column.getFormCellId(), projectId,
						null);
				if (cell != null) {
					column.set_formCell(cell);
				}
			} else if (column.getFormCellId() < 0) { // control
				W5FormCell cell = new W5FormCell(-formCellCounter++);
				cell.setControlTip((short) -column.getFormCellId());
				cell.setDsc(column.get_queryField().getDsc());
				cell.setFormCellId(column.getQueryFieldId());
				column.set_formCell(cell);
			}
		}

		Integer searchFormId = (Integer) getCustomizedObject(
				"select t.formId from W5Form t where t.objectTip=1 and t.objectId=? AND t.projectUuid=?",
				gr.getGridId(), projectId, null);
		if (searchFormId != null)
			grid.set_searchFormId(searchFormId);

		grid.set_toolbarItemList(
				find("from W5ObjectToolbarItem t where t.objectTip=? AND t.objectId=? AND t.projectUuid=? order by t.tabOrder",
						(short) 5, gr.getGridId(), projectId));
		for (W5ObjectToolbarItem c : grid.get_toolbarItemList())
			switch (c.getItemTip()) { // TODO:toolbar icine bisey konulacaksa
			case 10:
			case 7:
			case 15:
			case 9:
				break;
			case 14:
			case 8:
			case 6:
				break;
			}
		grid.set_menuItemList(
				find("from W5ObjectMenuItem t where t.objectTip=? AND t.objectId=? AND t.projectUuid=? order by t.tabOrder",
						(short) 5, gr.getGridId(), projectId));
		if (grid.getColumnRenderTip() != 0)
			grid.set_gridModuleList(find("from W5GridModule t where t.gridId=? AND t.projectUuid=? order by t.tabOrder",
					gr.getGridId(), projectId));
		// if(grid.getSelectionModeTip()==4)

		if (grid.getDefaultCrudFormId() != 0) {
			W5Form defaultCrudForm = (W5Form) getCustomizedObject("from W5Form t where t.formId=? and t.projectUuid=?",
					grid.getDefaultCrudFormId(), projectId, "Form"); // ozel bir
																		// client
																		// icin
																		// varsa

			if (defaultCrudForm != null) {
				// defaultCrudForm.set_sourceTable(PromisCache.getTable(customizationId,
				// defaultCrudForm.getObjectId()));
				W5Table t = FrameworkCache.getTable(projectId, defaultCrudForm.getObjectId()); // PromisCache.getTable(f.getScd(),
																								// f.getForm().getObjectId())
				grid.set_crudTable(t);
				grid.set_defaultCrudForm(defaultCrudForm);

				grid.set_crudFormSmsMailList(
						find("from W5FormSmsMail t where t.activeFlag=1 AND t.actionTips like '%0%' AND t.formId=? AND t.projectUuid=? order by t.tabOrder",
								grid.getDefaultCrudFormId(), projectId));
				grid.set_crudFormConversionList(
						find("from W5Conversion t where t.activeFlag=1 AND t.actionTips like '%0%' AND t.srcFormId=? AND t.projectUuid=? order by t.tabOrder",
								grid.getDefaultCrudFormId(), projectId));
				if (grid.get_crudFormSmsMailList().isEmpty())
					grid.set_crudFormSmsMailList(null);

				// Gridle ilgili onay mekanizması ataması
				organizeListPostProcessQueryFields(gr.getScd(), t, grid);
			}
		}
	}

	private void organizeListPostProcessQueryFields(Map<String, Object> scd, W5Table t, W5ListBase l) {
		// Gridle ilgili onay mekanizması ataması
		List<W5Workflow> a = find("from W5Workflow t where t.activeFlag=1 AND t.tableId = ? AND t.projectUuid = ?",
				t.getTableId(), t.getProjectUuid());
		if (!a.isEmpty()) {
			l.set_workflow(a.get(0));
		}

		// extra islemler
		if (FrameworkCache.getAppSettingIntValue(scd, "approval_flag") != 0 && !a.isEmpty()) { // table
																								// Record
																								// Approvals
			if (l.get_postProcessQueryFields() == null)
				l.set_postProcessQueryFields(new ArrayList());
			W5QueryField f = new W5QueryField();
			f.setDsc(FieldDefinitions.queryFieldName_Approval);
			f.setFieldTip((short) 5); // comment
			f.setTabOrder((short) 22); // aslinda width
			f.setPostProcessTip((short) 49); // approvalPostProcess
			l.get_postProcessQueryFields().add(f);
			W5QueryField f2 = new W5QueryField();
			f2.setDsc(FieldDefinitions.queryFieldName_ArVersionNo);
			f2.setFieldTip((short) 4); // comment
			f2.setTabOrder((short) 22); // aslinda width
			l.get_postProcessQueryFields().add(f2);
		}

		if (FrameworkCache.getAppSettingIntValue(scd, "file_attachment_flag") != 0 && t.getFileAttachmentFlag() != 0) {
			if (l.get_postProcessQueryFields() == null)
				l.set_postProcessQueryFields(new ArrayList());
			W5QueryField f = new W5QueryField();
			f.setDsc(FieldDefinitions.queryFieldName_FileAttachment);
			f.setFieldTip((short) 2); // file attachment
			f.setTabOrder((short) 22); // aslinda width
			l.get_postProcessQueryFields().add(f);
		}
		if (FrameworkCache.getAppSettingIntValue(scd, "make_comment_flag") != 0 && t.getMakeCommentFlag() != 0) { // table
																													// Comment
			if (l.get_postProcessQueryFields() == null)
				l.set_postProcessQueryFields(new ArrayList());
			W5QueryField f = new W5QueryField();
			f.setDsc(FieldDefinitions.queryFieldName_Comment);
			f.setFieldTip((short) 3); // comment
			f.setTabOrder((short) 22); // aslinda width
			// if(PromisSetting.commentSummary)f.set
			l.get_postProcessQueryFields().add(f);
		}
		if (FrameworkSetting.vcs && t.getVcsFlag() != 0) {
			if (l.get_postProcessQueryFields() == null)
				l.set_postProcessQueryFields(new ArrayList());
			W5QueryField f = new W5QueryField();
			f.setDsc(FieldDefinitions.queryFieldName_Vcs);
			f.setFieldTip((short) 9); // vcs
			f.setTabOrder((short) 32); // aslinda width
			l.get_postProcessQueryFields().add(f);
		}
	}

	public void reloadJobsCache() {
		// Job Schedule
		try {
			FrameworkCache.wJobs.clear();
			FrameworkCache.wJobs.addAll(find("from W5JobSchedule x where x.activeFlag=1 and x.actionStartTip=1"));
			for (W5JobSchedule data : FrameworkCache.wJobs) {
				String userId = Integer.toString(data.getExecuteUserId());
				String roleId = Integer.toString(data.getExecuteRoleId());
				int customization_id = data.getCustomizationId();
				List<Map<String, Object>> res = executeSQLQuery2Map(
						"select r.user_role_id, u.customization_id from iwb.w5_user u, iwb.w5_user_role r where u.customization_id=r.customization_id and u.user_id=r.user_id and u.user_id="
								+ userId + " and r.role_id=" + roleId + " and r.customization_id=" + customization_id,
						null);
				if (res != null)
					for (Map<String, Object> usr : res) {
						data.set_userRoleId(GenericUtil.uInteger((String) usr.get("user_role_id")));
					}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reloadLocaleMsgsCache2(int cid) {
		String whereSql = cid != -1 ? (" where x.customization_id=" + cid) : "";
		List<Object[]> l = (List<Object[]>) executeSQLQuery(
				"select x.customization_id, x.locale, x.locale_msg_key, x.dsc from iwb.w5_locale_msg x " + whereSql
						+ " order by customization_id, locale, locale_msg_key, dsc");
		if (l != null)
			for (Object[] m : l) {
				LocaleMsgCache.set2((int) m[0], (String) m[1], (String) m[2], (String) m[3]);
			}

		// Publish Locale Msgs
		if (cid == -1 || cid == 0) {
			List<String> publishSet = executeSQLQuery(
					"select x.locale_msg_key from iwb.w5_locale_msg x where  x.customization_id=0 AND x.publish_flag = 1 AND x.locale='en' order by locale, locale_msg_key, dsc");
			if (publishSet != null)
				for (String m : publishSet)
					LocaleMsgCache.add2publish(m);
		}
	}

	public void reloadErrorMessagesCache(int customizationId) { // TODO
		FrameworkCache.wExceptions.clear();
		/*
		 * List l = executeSQLQuery(
		 * "select exc_code, locale_msg_key from iwb.w5_exception_filter order by exc_id"
		 * ); if(l!=null)for(Object[] m : (List<Object[]>)l){
		 * FrameworkCache.wExceptions.put((String)m[0], (String)m[1]); }
		 */
	}

	public void reloadTableAccessConditionSQLs(String projectId) { // TODO
		FrameworkCache.wAccessConditionSqlMap.clear();
		/*
		 * try{ List<W5TableAccessConditionSql> l = find(
		 * "from W5TableAccessConditionSql t"); for(W5TableAccessConditionSql
		 * s:l){
		 * FrameworkCache.wAccessConditionSqlMap.put(s.getAccessConditionSqlId()
		 * , s); } } catch (Exception e){}
		 */
	}

	public void reloadApplicationSettingsCache(int cid) {
		if (cid == -1)
			FrameworkCache.appSettings.clear();
		else
			FrameworkCache.appSettings.remove(cid);
		List<Object[]> lm = (List<Object[]>) executeSQLQuery(
				"select customization_id, dsc, val from iwb.w5_app_setting where ?=-1 OR customization_id=?", cid, cid);
		if (lm != null)
			for (Object[] m : lm) {
				int customizationId = (int) m[0];
				Map<String, String> subMap = FrameworkCache.appSettings.get(customizationId);
				if (subMap == null) {
					subMap = new HashMap<String, String>();
					FrameworkCache.appSettings.put(customizationId, subMap);
				}
				subMap.put((String) m[1], (String) m[2]);
			}
		int default_customization_id = Integer
				.parseInt(FrameworkCache.appSettings.get(0).get("default_customization_id"));
		FrameworkCache.publishAppSettings.clear();
		List<Object> lm2 = (List<Object>) executeSQLQuery(
				"select dsc from iwb.w5_app_setting where customization_id=? AND publish_flag!=0",
				default_customization_id);
		if (lm2 != null)
			for (Object m : lm2) {
				FrameworkCache.publishAppSettings.add((String) m);
			}
	}

	public void addProject2Cache(String projectId) {
		List l = find("from W5Project t where t.projectUuid=?", projectId);
		if (l.isEmpty())
			throw new IWBException("framework", "Not Valid Project", 0, projectId, "Not Valid Project", null);
		W5Project p = (W5Project) l.get(0);
		List ll = executeSQLQuery(
				"select min(t.user_tip) from iwb.w5_user_tip t where t.user_tip!=122 AND t.active_flag=1 AND t.project_uuid=?",
				projectId);
		if (!GenericUtil.isEmpty(ll))
			p.set_defaultUserTip(GenericUtil.uInt(ll.get(0)));
		FrameworkCache.addProject(p);
	}

	public List<W5Project> reloadProjectsCache(int cid) {
		// if(cid==-1)FrameworkCache.wProjects.clear();

		List<W5Project> lp = cid == -1 ? (List<W5Project>) find("from W5Project t")
				: (List<W5Project>) find("from W5Project t where t.customizationId=?", cid);
		if (lp != null)
			for (W5Project p : lp) {
				List ll = executeSQLQuery(
						"select min(t.user_tip) from iwb.w5_user_tip t where t.user_tip!=122 AND t.active_flag=1 AND t.project_uuid=?",
						p.getProjectUuid());
				if (!GenericUtil.isEmpty(ll))
					p.set_defaultUserTip(GenericUtil.uInt(ll.get(0)));
				FrameworkCache.addProject(p);
				FrameworkSetting.projectSystemStatus.put(p.getProjectUuid(), 0);

				/*
				 * if(FrameworkSetting.tsdbFlag && p.getTsdbFlag()!=0 &&
				 * !GenericUtil.isEmpty(p.getTsdbUrl()))try{
				 * p.set_tsdb(InfluxDBFactory.connect(p.getTsdbUrl(),
				 * p.getTsdbUserName(), p.getTsdbPassWord())); }catch(Exception
				 * e){ p.setTsdbFlag((short)0);
				 * if(FrameworkSetting.debug)e.printStackTrace();
				 * 
				 * }
				 */
			}
		return lp;
	}

	public void setApplicationSettingsValues() {
		FrameworkSetting.debug = FrameworkCache.getAppSettingIntValue(0, "debug") != 0;

		// preload olmamasinin sebebi: approval'da herkesin farkli kayitlarinin
		// gelmesi search formlarda
		FrameworkSetting.monaco = FrameworkCache.getAppSettingIntValue(0, "monaco") != 0;
		FrameworkSetting.mq = FrameworkCache.getAppSettingIntValue(0, "mq_flag") != 0;
		// FrameworkSetting.preloadWEngine =
		// FrameworkCache.getAppSettingIntValue(0, "preload_engine");
		FrameworkSetting.chat = FrameworkCache.getAppSettingIntValue(0, "chat_flag") != 0;
		// FrameworkSetting.allowMultiLogin =
		// FrameworkCache.getAppSettingIntValue(0,
		// "allow_multi_login_flag")!=0;
		// FrameworkSetting.profilePicture =
		// FrameworkCache.getAppSettingIntValue(0,
		// "profile_picture_flag")!=0;
		FrameworkSetting.alarm = FrameworkCache.getAppSettingIntValue(0, "alarm_flag") != 0;
		FrameworkSetting.sms = FrameworkCache.getAppSettingIntValue(0, "sms_flag") != 0;
		FrameworkSetting.mail = FrameworkCache.getAppSettingIntValue(0, "mail_flag") != 0;

		FrameworkSetting.vcs = FrameworkCache.getAppSettingIntValue(0, "vcs_flag") != 0;
		FrameworkSetting.vcsServer = FrameworkCache.getAppSettingIntValue(0, "vcs_server_flag") != 0;

		// if(FrameworkSetting.preloadWEngine!=0)FrameworkCache.clearPreloadCache();
		// //TODO

		FrameworkSetting.advancedSelectShowEmptyText = FrameworkCache.getAppSettingIntValue(0,
				"advanced_select_show_empty_text") != 0;
		FrameworkSetting.simpleSelectShowEmptyText = FrameworkCache.getAppSettingIntValue(0,
				"simple_select_show_empty_text") != 0;
		FrameworkSetting.cacheTimeoutRecord = FrameworkCache.getAppSettingIntValue(0, "cache_timeout_record") * 1000;
		FrameworkSetting.crudLogSchema = FrameworkCache.getAppSettingStringValue(0, "log_crud_schema",
				FrameworkSetting.crudLogSchema);
		FrameworkSetting.mailSchema = FrameworkCache.getAppSettingStringValue(0, "mail_schema",
				FrameworkSetting.mailSchema);
		FrameworkSetting.asyncTimeout = FrameworkCache.getAppSettingIntValue(0, "async_timeout", 100);
		//
		// if(MVAUtil.appSettings.get("file_local_path")!=null)MVAUtil.localPath=MVAUtil.appSettings.get("file_local_path");

		FrameworkSetting.onlineUsersAwayMinute = 1000 * 60
				* FrameworkCache.getAppSettingIntValue(0, "online_users_away_minute", 3);
		FrameworkSetting.onlineUsersLimitMinute = 1000 * 60
				* FrameworkCache.getAppSettingIntValue(0, "online_users_limit_minute", 10);
		FrameworkSetting.onlineUsersLimitMobileMinute = 1000 * 60
				* FrameworkCache.getAppSettingIntValue(0, "online_users_limit_mobile_minute", 7 * 24 * 60); // 7
																											// gun
		FrameworkSetting.tableChildrenMaxRecordNumber = FrameworkCache.getAppSettingIntValue(0,
				"table_children_max_record_number", 100);

		FrameworkSetting.mailPassEncrypt = FrameworkCache.getAppSettingIntValue(0, "encrypt_mail_pass") != 0;

		FrameworkSetting.mobilePush = FrameworkCache.getAppSettingIntValue(0, "mobile_push_flag") != 0;
		FrameworkSetting.mobilePushProduction = FrameworkCache.getAppSettingIntValue(0,
				"mobile_push_production_flag") != 0;

		FrameworkSetting.workflow = FrameworkCache.getAppSettingIntValue(0, "approval_flag") != 0;
		FrameworkSetting.liveSyncRecord = FrameworkCache.getAppSettingIntValue(0, "live_sync_record") != 0;

		FrameworkSetting.lookupEditFormFlag = FrameworkCache.getAppSettingIntValue(0, "lookup_edit_form_flag") != 0;
		// PromisSetting.replaceSqlSelectX =
		// PromisCache.getAppSettingIntValue(0,
		// "replace_sql_select_x")!=0;;
	}

	public void reloadLookUpCache(String projectId) {
		Map<Integer, W5LookUp> subMap = new HashMap<Integer, W5LookUp>();
		for (W5LookUp lookUp : (List<W5LookUp>) find("from W5LookUp t where t.projectUuid=? order by  t.lookUpId",
				projectId)) {
			lookUp.set_detayList(new ArrayList());
			lookUp.set_detayMap(new HashMap());
			subMap.put(lookUp.getLookUpId(), lookUp);
		}

		for (W5LookUpDetay lookUpDetay : (List<W5LookUpDetay>) find(
				"from W5LookUpDetay t where t.projectUuid=? order by t.lookUpId, t.tabOrder", projectId)) {
			W5LookUp lookUp = subMap.get(lookUpDetay.getLookUpId());
			lookUp.get_detayList().add(lookUpDetay);
			lookUp.get_detayMap().put(lookUpDetay.getVal(), lookUpDetay);
		}
		FrameworkCache.setLookUpMap(projectId, subMap);
	}

	public void reloadRolesCache(String projectId) { // deprecated
		/*
		 * if(FrameworkCache.wRoles.get(customizationId)!=null)FrameworkCache.
		 * wRoles.get(customizationId).clear(); Map<Integer, String> subRoleMap
		 * = new HashMap<Integer, String>();
		 * FrameworkCache.wRoles.put(customizationId, subRoleMap); List l =
		 * executeSQLQuery(
		 * "select r.role_id, r.dsc from iwb.w5_role r where customization_id=?"
		 * , customizationId); if(l!=null)for(Object[] o:(List<Object[]>)l){ int
		 * roleId = GenericUtil.uInt(o[0]); subRoleMap.put(roleId,
		 * (String)o[1]); }
		 */
	}

	public void reloadMobileCache() { // TODO
		if (true)
			return;
		UserUtil.clearDevices();
		List<Object[]> l = (List<Object[]>) executeSQLQuery(
				"select r.mobile_device_id, r.user_id, r.customization_id, r.device_tip, r.last_login_dttm  from iwb.w5_mobile_device r where r.active_flag=1");
		if (l != null)
			for (Object[] o : l) {
				// UserUtil.addDevice(o[0].toString(), GenericUtil.uInt(o[1]),
				// GenericUtil.uInt(o[2]),
				// (short)GenericUtil.uInt(o[3]), ((Date)o[4]).getTime());
			}
	}

	public void reloadConversionsCache(String projectId) {
		Map<Integer, W5Conversion> conversionMap = new HashMap<Integer, W5Conversion>();
		List<W5Conversion> cnvAll = (List<W5Conversion>) find("from W5Conversion t where t.projectUuid=?", projectId);
		for (W5Conversion cnv : cnvAll) {
			W5Table t = FrameworkCache.getTable(projectId, cnv.getSrcTableId());
			if (t != null) {
				conversionMap.put(cnv.getConversionId(), cnv);
				if (t.get_tableConversionList() != null)
					t.set_tableConversionList(new ArrayList());
				t.get_tableConversionList().add(cnv);
				cnv.set_conversionColList((List<W5ConversionCol>) find(
						"from W5ConversionCol t where t.conversionId=? AND t.projectUuid=? order by t.conversionId, t.tabOrder",
						cnv.getConversionId(), projectId));
				cnv.set_conversionColMap(new HashMap());
				for (W5ConversionCol cnvCol : cnv.get_conversionColList()) {
					cnv.get_conversionColMap().put(cnvCol.getConversionColId(), cnvCol);
				}
			}
		}
		FrameworkCache.setConversionMap(projectId, conversionMap);
	}

	public void reloadTableEventsCache(String projectId) {
		Map<Integer, List<W5TableEvent>> tableMap = new HashMap<Integer, List<W5TableEvent>>();
		List<W5TableEvent> l = find(
				"from W5TableEvent r where r.projectUuid=? AND r.activeFlag=1 order by r.tableId, r.tabOrder, r.tableTriggerId",
				projectId);
		for (W5TableEvent r : l) {
			List<W5TableEvent> l2 = tableMap.get(r.getTableId());
			if (l2 == null) {
				l2 = new ArrayList();
				tableMap.put(r.getTableId(), l2);
			}
			l2.add(r);
		}
		FrameworkCache.setTableEventMap(projectId, tableMap);
	}

	public void reloadTablesCache(String projectId) {
		// if (PromisCache.wTables.get(customizationId)!=null)
		// PromisCache.wTables.get(customizationId).clear();
		List<W5Table> lt = (List<W5Table>) find("from W5Table t where t.projectUuid=? order by t.tableId", projectId);
		Map<Integer, W5Table> tableMap = new HashMap<Integer, W5Table>(lt.size() * 14 / 10);
		for (W5Table t : lt) {
			// t.set_cachedObjectMap(new HashMap());
			tableMap.put(t.getTableId(), t);
		}
		FrameworkCache.setTableMap(projectId, tableMap);

		reloadTableFieldsCache(projectId);
		// reloadTableFiltersCache(projectId);

		reloadWorkflowCache(projectId);
	}

	public void reloadWorkflowCache(String projectId) {
		if (!FrameworkSetting.workflow)
			return;
		// Approval ın bağlantılı olduğu w5table ların approval maplari
		// temizleniyor
		FrameworkCache.clearProjectWorkflows(projectId);

		// approval cache yenileniyor ve ilgili w5table lara ekleniyor
		List<W5Workflow> al = (List<W5Workflow>) find(
				"from W5Workflow t where t.activeFlag!=0 AND t.projectUuid=? order by t.tableId", projectId);
		for (W5Workflow ta : al) {
			FrameworkCache.addWorkflow(projectId, ta);
			W5Table t = FrameworkCache.getTable(projectId, ta.getTableId());
			if (t.get_approvalMap() == null) {
				t.set_approvalMap(new HashMap<Short, W5Workflow>());
			}
			t.get_approvalMap().put(ta.getActionTip(), ta);
			ta.set_approvalStepList((List<W5WorkflowStep>) find(
					"from W5WorkflowStep t where t.projectUuid=? and t.approvalId=? order by approvalStepId", projectId,
					ta.getApprovalId()));

			if (ta.getApprovalRequestTip() != 1) {// automatic degilse
				W5WorkflowStep approvedStep = new W5WorkflowStep();
				approvedStep.setApprovalStepId(901);
				approvedStep.setDsc(ta.getApprovalRequestMsg());// setDsc("onay_talep_edilecek");
				ta.get_approvalStepList().add(approvedStep);
			}

			W5WorkflowStep approvedStep = new W5WorkflowStep();
			approvedStep.setApprovalStepId(998);
			approvedStep.setDsc(ta.getApprovedMsg());// setDsc("onaylandi");
			ta.get_approvalStepList().add(approvedStep);

			if (ta.getOnRejectTip() == 1) {// make status rejected
				W5WorkflowStep rejectedStep = new W5WorkflowStep();
				rejectedStep.setApprovalStepId(999);
				rejectedStep.setDsc(ta.getRejectedMsg());// setDsc("reddedildi");
				ta.get_approvalStepList().add(rejectedStep);
			}
			ta.set_approvalStepMap(new HashMap());
			for (W5WorkflowStep step : ta.get_approvalStepList()) {
				if (step.getAccessViewTip() != 0) {
					t.set_hasApprovalViewControlFlag((short) 1);
				}
				ta.get_approvalStepMap().put(step.getApprovalStepId(), step);
			}
			getCurrentSession().flush();
		}
	}


	public void reloadTableParamListChildListParentListCache(String projectId) { // TODO

		List<W5TableParam> tplAll = (List<W5TableParam>) find(
				"from W5TableParam t where t.projectUuid=? order by t.tableId, t.tabOrder", projectId);
		// Map<Integer, List<W5TableParam>> tplMap = new HashMap<Integer,
		// List<W5TableParam>>();
		int lastTableId = -1;
		List<W5TableParam> x = null;
		for (W5TableParam tp : tplAll) {
			if (lastTableId != tp.getTableId()) {
				if (x != null) {
					W5Table tx = FrameworkCache.getTable(projectId, lastTableId);
					if (tx != null)
						tx.set_tableParamList(x);
					// FrameworkCache.tableParamListMap.put(lastTableId, x);
				}
				x = new ArrayList();
				lastTableId = tp.getTableId();
			}
			x.add(tp);
		}
		if (x != null) {
			W5Table tx = FrameworkCache.getTable(projectId, lastTableId);
			if (tx != null)
				tx.set_tableParamList(x);
		}

		List<W5TableChild> tcAll = (List<W5TableChild>) find(
				"from W5TableChild t where t.projectUuid=? order by t.tableId", projectId);
		// Map<Integer, List<W5TableChild>> tcMap = new HashMap<Integer,
		// List<W5TableChild>>();//copy
		// Map<Integer, List<W5TableChild>> tpMap = new HashMap<Integer,
		// List<W5TableChild>>();//watch,feed
		lastTableId = -1;
		List<W5TableChild> ltc = null, tpx = null;
		for (W5TableChild tc : tcAll) {
			W5Table pr = FrameworkCache.getTable(projectId, tc.getRelatedTableId());
			if (pr == null)
				continue;
			if (lastTableId != tc.getTableId()) {
				W5Table tx = FrameworkCache.getTable(projectId, lastTableId == -1 ? tc.getTableId() : lastTableId);
				if (tx != null) {
					ltc = tx.get_tableChildList();
					if (ltc == null) {
						ltc = new ArrayList<W5TableChild>();
						tx.set_tableChildList(ltc);
					}
				} else
					continue;
				lastTableId = tc.getTableId();
			}
			ltc.add(tc);

			if (pr != null) {
				tpx = pr.get_tableParentList();
				if (tpx == null) {
					tpx = new ArrayList<W5TableChild>();
					pr.set_tableParentList(tpx);
				}
				tpx.add(tc);
			}
		}
	}

	public void reloadTableFieldsCache(String projectId) {
		List<W5TableField> tfl = (List<W5TableField>) find(
				"from W5TableField t where t.projectUuid=? AND t.tableFieldId>0 order by t.tableId, t.tabOrder",
				projectId);
		W5Table t = null;
		for (W5TableField tf : tfl) {
			if (t == null || tf.getTableId() != t.getTableId())
				t = FrameworkCache.getTable(projectId, tf.getTableId()); // tableMap.get(tf.getTableId());
			if (t != null) {
				if (t.get_tableFieldList() == null) {
					t.set_tableFieldList(new ArrayList<W5TableField>());
					t.set_tableFieldMap(new HashMap<Integer, W5TableField>());
					/*
					 * t.set_tableParamList(FrameworkCache.tableParamListMap.get
					 * (tf.getTableId()));
					 * t.set_tableChildList(FrameworkCache.tableChildListMap.get
					 * (tf.getTableId()));
					 * t.set_tableParentList(FrameworkCache.tableParentListMap.
					 * get(tf.getTableId()));
					 */
				}
				t.get_tableFieldList().add(tf);
				t.get_tableFieldMap().put(tf.getTableFieldId(), tf);
			}
		}
		/*
		 * if(customizationId==0){ FrameworkCache.wTableFieldMap.clear();
		 * for(W5TableField
		 * tf:tfl)FrameworkCache.wTableFieldMap.put(tf.getTableFieldId(),
		 * tf.getTableId()); }
		 */
	}

	public void reloadTableFiltersCache(String projectId) { // customizationID
															// ??
		/*
		 * for(W5TableFilter tf:(List<W5TableFilter>)find(
		 * "from W5TableFilter t where t.projectUuid=?", projectId)){ W5Table t
		 * = FrameworkCache.getTable(tf.getCustomizationId(), tf.getTableId());
		 * if(t!=null){
		 * if(t.get_tableFilterList()==null)t.set_tableFilterList(new
		 * ArrayList()); t.get_tableFilterList().add(tf); } }
		 */
	}

	private void reloadDeveloperEntityKeys() {
		Set<String> m = new HashSet();
		m.add("20.1"); // login form
		for (Object[] x : (List<Object[]>) executeSQLQuery(
				"select x.table_id, x.dsc, (select tp.expression_dsc from iwb.w5_table_param tp where tp.table_id=x.table_id AND x.project_uuid=tp.project_uuid AND tp.tab_order=1) tp_dsc from iwb.w5_table x where x.project_uuid='067e6162-3b6f-4ae2-a221-2470b63dff00' AND x.vcs_flag=1 AND x.table_id in (4,5,8,9,10,13,14,15,16,20,40,41,42,63,64,230,231,254,707,930,936,1345,3351)")) {
			List<Object> lo = executeSQLQuery("select t." + x[2] + " from " + x[1]
					+ " t where t.project_uuid='067e6162-3b6f-4ae2-a221-2470b63dff00'");
			if (lo != null)
				for (Object o : lo) {
					m.add(x[0] + "." + o);
				}
		}
		FrameworkCache.setDevEntityKeys(m);
	}

	public void reloadFrameworkCaches(int customizationId) {
		logger.info("Caching Started (users, jobs, localeMsgs, errorMsgs, appSettings, lookUps, tableParams)");
		reloadErrorMessagesCache(customizationId);
		// Application Settings
		List<W5Project> lp = reloadProjectsCache(customizationId);

		// Locale Msgs
		reloadLocaleMsgsCache2(customizationId);

		// Application Settings
		reloadApplicationSettingsCache(customizationId);

		List<W5Customization> customizationList;
		if (customizationId == -1) {
			customizationList = (List<W5Customization>) find("from W5Customization t order by t.customizationId");
			reloadMobileCache();
			setApplicationSettingsValues();
			FrameworkCache.cachedOnlineQueryFields = (List<W5QueryField>) find(
					"from W5QueryField f where f.queryId=142 AND f.projectUuid='067e6162-3b6f-4ae2-a221-2470b63dff00' order by f.tabOrder");
			reloadDeveloperEntityKeys();
		} else
			customizationList = (List<W5Customization>) find("from W5Customization t where t.customizationId=?",
					customizationId);

		for (W5Customization c : customizationList)
			FrameworkCache.wCustomizationMap.put(c.getCustomizationId(), c);

		dao.reloadUsersCache(customizationId);

		for (W5Project p : lp) {
			FrameworkCache.clearPreloadCache(p.getProjectUuid());
			reloadProjectCaches(p.getProjectUuid());
		}

		List<Object[]> ll = executeSQLQuery(
				"select x.related_project_uuid, string_agg(x.user_id::text,',') from iwb.w5_user_related_project x group by x.related_project_uuid");
		if (ll != null)
			for (Object[] oo : ll)
				UserUtil.addProjectUsers(oo[0].toString(), oo[1].toString());
		ll = executeSQLQuery(
				"select x.project_uuid, string_agg(x.user_id::text,',') from iwb.w5_user x group by x.project_uuid");
		if (ll != null)
			for (Object[] oo : ll)
				UserUtil.addProjectUsers(oo[0].toString(), oo[1].toString());
	}

	public void reloadProjectCaches(String projectId) {
		try {
			logger.info("Caching Started (users, jobs, localeMsgs, errorMsgs, appSettings, lookUps, tableParams)");
			FrameworkSetting.projectSystemStatus.put(projectId, 2); // suspended
			logger.info("Caching project = " + projectId);

			reloadRolesCache(projectId);

			// Job Schedule
			// if(cid==-1 || cid==0)reloadJobsCache();

			// Error Messages

			// Publish LookUps
			// if(cid==-1 || cid==0)reloadPublishLookUpsCache();

			// condition SQLs

			// int defaultCustomizationId = PromisCache.getAppSettingIntValue(0,
			// "default_customization_id");

			// Conversions
			// reloadConversionsCache(cid);

			// Table Params
			// if(cid==-1 ||
			// cid==0)reloadTableParamListChildListParentListCache();

			reloadLookUpCache(projectId);
			reloadTablesCache(projectId);
			reloadTableParamListChildListParentListCache(projectId);
			reloadTableAccessConditionSQLs(projectId);
			reloadTableEventsCache(projectId);
			reloadWsServersCache(projectId);
			reloadWsClientsCache(projectId);
			reloadComponentCache(projectId);

			FrameworkSetting.projectSystemStatus.put(projectId, 0); // working
			FrameworkCache.clearReloadCache(projectId);
			// if(FrameworkSetting.feedLoadAtStartupDepth>0 &&
			// cid==-1)reloadFeed(customizationId);

		} catch (Exception e) {
			logger.error("Error for project = " + projectId);
			e.printStackTrace();
		}

		logger.info("Cache Loaded.");
	}

	private void reloadComponentCache(String projectId) {
		Map<Integer, String> wComponentCss = new HashMap<Integer, String>();
		Map<Integer, String> wComponentJs = new HashMap<Integer, String>();
		List<W5Component> l = find("from W5Component t where t.projectUuid=?", projectId);
		for (W5Component c : l) {
			wComponentCss.put(c.getComponentId(), c.getCssCode());
			wComponentJs.put(c.getComponentId(), c.getCode());
		}
		FrameworkCache.setProjectComponents(projectId, wComponentCss, wComponentJs);
	}

	private void reloadWsServersCache(String projectId) {
		List<W5WsServer> lt = (List<W5WsServer>) find("from W5WsServer t where t.projectUuid=?", projectId);
		Map<String, W5WsServer> wssMap = new HashMap<String, W5WsServer>(lt.size() * 14 / 10);
		FrameworkCache.setWsServersMap(projectId, wssMap);
		for (W5WsServer o : lt) {
			wssMap.put(o.getWsUrl(), o);
			o.set_methods((List<W5WsServerMethod>) find(
					"from W5WsServerMethod t where t.projectUuid=? AND t.wsServerId=? order by t.tabOrder", projectId,
					o.getWsServerId()));
			o.get_methods().add(0, new W5WsServerMethod("login", (short) 4, 3));
			// o.get_methods().add(1,new W5WsServerMethod("logout", (short)4,
			// 5));
			for (W5WsServerMethod wsm : o.get_methods())
				if (wsm.getObjectTip() == 19) { // QueryResult
					wsm.set_params((List<W5WsServerMethodParam>) find(
							"from W5WsServerMethodParam t where t.projectUuid=? AND t.wsServerMethodId=? order by t.tabOrder",
							projectId, wsm.getWsServerMethodId()));
					if (wsm.get_params().isEmpty())
						wsm.set_params(null);
					else {
						W5WsServerMethodParam tokenKey = new W5WsServerMethodParam(-998, "tokenKey", (short) 1);
						tokenKey.setOutFlag((short) 0);
						tokenKey.setNotNullFlag((short) 1);
						wsm.get_params().add(0, tokenKey);
					}
				}
		}
	}

	public void reloadWsClientsCache(String projectId) {
		// if(true)return;
		List<W5Ws> l = find("from W5Ws x where x.projectUuid=?", projectId);
		Map<Integer, W5Ws> mm = new HashMap();
		Map<String, W5Ws> mm2 = new HashMap();
		for (W5Ws w : l) {
			mm.put(w.getWsId(), w);
			mm2.put(w.getDsc(), w);
		}
		FrameworkCache.setWsClientsMap(projectId, mm2);

		List<W5WsMethod> lm = find("from W5WsMethod x where x.projectUuid=? order by x.wsId, x.wsMethodId", projectId);
		for (W5WsMethod m : lm) {
			W5Ws c = mm.get(m.getWsId());
			if (c != null) {
				m.set_ws(c);
				if (c.get_methods() == null)
					c.set_methods(new ArrayList());
				FrameworkCache.addWsMethod(projectId, m);
				c.get_methods().add(m);
			}
		}
	}


	public W5GlobalFuncResult getGlobalFuncResult(Map<String, Object> scd, int dbFuncId) {
		String projectId = FrameworkCache.getProjectId(scd, "20." + dbFuncId);
		if (dbFuncId < -1) {
			dbFuncId = (Integer) find(
					"select t.objectId from W5Form t where t.objectTip in (3,4) AND t.projectUuid=? AND t.formId=?",
					projectId, -dbFuncId).get(0);
		}

		W5GlobalFuncResult r = new W5GlobalFuncResult(dbFuncId);
		r.setGlobalFunc(FrameworkCache.getGlobalFunc(projectId, dbFuncId));
		if (r.getGlobalFunc() == null) {
			r.setGlobalFunc((W5GlobalFunc) getCustomizedObject(
					"from W5GlobalFunc t where t.dbFuncId=? AND t.projectUuid=?", dbFuncId, projectId, "GlobalFunc"));
			r.getGlobalFunc().set_dbFuncParamList(
					find("from W5GlobalFuncParam t where t.projectUuid=? AND t.dbFuncId=? order by t.tabOrder",
							projectId, dbFuncId));

			FrameworkCache.addGlobalFunc(projectId, r.getGlobalFunc());
		}

		r.setScd(scd);

		return r;
	}

	public W5JasperResult getJasperResult(Map<String, Object> scd, W5JasperReport jasperreport,
			Map<String, String> parameterMap) {
		W5JasperResult jasperResult = new W5JasperResult(jasperreport.getJasperId());
		jasperResult.setScd(scd);
		/*
		 * if(PromisSetting.preloadWEngine!=0 &&
		 * PromisCache.wTemplates.get(templateId)!=null){ //
		 * jasperResult.setJasper(PromisCache.wTemplates.get(templateId)); }
		 * else { loadJasper(jasperResult);
		 * if(PromisSetting.preloadWEngine!=0)PromisCache.wTemplates.put(
		 * templateId, templateResult.getTemplate()); }
		 */

		W5Jasper jasper = (W5Jasper) find("from W5Jasper t where t.jasperId=?", jasperreport.getJasperId()).get(0); // ozel
																													// bir
																													// client
																													// icin
																													// varsa
		jasperResult.setJasper(jasper);
		jasper.set_jasperObjects(
				find("from W5JasperObject t where t.jasperId=? order by t.tabOrder", jasperreport.getJasperId()));
		jasper.set_jasperReport(jasperreport);
		jasperResult.setResultMap(new HashMap());
		return jasperResult;
	}

	public W5JasperReport getJasperReport(Map<String, Object> scd, int jasperReportId) {
		W5JasperReport jasperreport = (W5JasperReport) find(
				"from W5JasperReport t where t.customizationId=? and  t.jasperReportId=?", scd.get("customizationId"),
				jasperReportId).get(0);
		return jasperreport;
	}

	public M5ListResult getMListResult(Map<String, Object> scd, int listId, Map<String, String> requestParams,
			boolean noSearchForm) {
		String projectId = FrameworkCache.getProjectId(scd, "1345." + listId);
		M5ListResult mlistResult = new M5ListResult(listId);
		mlistResult.setRequestParams(requestParams);
		mlistResult.setScd(scd);

		M5List d = FrameworkCache.getMListView(projectId, listId);
		if (d != null) {
			mlistResult.setList(d);
		} else {
			d = (M5List) getCustomizedObject("from M5List t where t.listId=? and t.projectUuid=?", listId, projectId,
					"MobileList"); // ozel bir client icin varsa
			W5Query q = (W5Query) getCustomizedObject("from W5Query t where t.queryId=? and t.projectUuid=?",
					d.getQueryId(), projectId, "QueryID"); // ozel bir client
															// icin varsa
			d.set_query(q);
			if (q.getMainTableId() != 0)
				d.set_mainTable(FrameworkCache.getTable(projectId, q.getMainTableId()));
			/*
			 * if(d.getDefaultCrudFormId()!=0){ W5Form f =
			 * (W5Form)getCustomizedObject(
			 * "from W5Form t where t.formId=? and t.projectUuid=?",
			 * d.getDefaultCrudFormId(), projectUuid); // ozel bir client icin
			 * varsa f.set_formCells(find(
			 * "from W5FormCell t where t.formId=? and t.projectUuid=? order by t.tabOrder"
			 * , d.getDefaultCrudFormId(), projectUuid)); d.set_crudForm(f); }
			 */
			Integer searchFormId = (Integer) getCustomizedObject(
					"select t.formId from W5Form t where t.objectTip=10 and t.objectId=? AND t.projectUuid=?", listId,
					projectId, null);
			if (searchFormId != null)
				d.set_searchFormId(searchFormId);
			d.set_detailMLists(find("from M5List l where l.projectUuid=? AND l.parentListId=? order by l.listId",
					projectId, listId));
			mlistResult.setList(d);
			if (!GenericUtil.isEmpty(d.getOrderQueryFieldIds()))
				d.set_orderQueryFieldNames(
						find("select qf.dsc from W5QueryField qf where qf.queryId=? and qf.projectUuid=? AND qf.queryFieldId in ("
								+ d.getOrderQueryFieldIds() + ") order by qf.tabOrder", d.getQueryId(), projectId));
			d.set_toolbarItemList(
					find("from W5ObjectToolbarItem t where t.objectTip=1345 AND t.objectId=? AND t.projectUuid=? order by t.tabOrder",
							listId, projectId));

			d.set_menuItemList(
					find("from W5ObjectMenuItem t where t.objectTip=1345 AND t.objectId=? AND t.projectUuid=? order by t.tabOrder",
							listId, projectId));

			// d = mlistResult.getList();
			//
			// if(FrameworkSetting.preloadWEngine!=0)FrameworkCache.wListViews.get(d.getCustomizationId()).put(listId,
			// d);
		}
		// search Form
		if (!noSearchForm && d.get_searchFormId() != 0) {
			W5FormResult searchForm = getFormResult(scd, d.get_searchFormId(), 10, requestParams);
			dao.initializeForm(searchForm, false);
			dao.loadFormCellLookups(mlistResult.getScd(), searchForm.getFormCellResults(), mlistResult.getRequestParams(),
					null);
			mlistResult.setSearchFormResult(searchForm);
		}
		return mlistResult;
	}

	public W5ListViewResult getListViewResult(Map<String, Object> scd, int listViewId,
			Map<String, String> requestParams, boolean noSearchForm) {

		W5ListViewResult listViewResult = new W5ListViewResult(listViewId);
		String projectId = FrameworkCache.getProjectId(scd, "936." + listViewId);
		listViewResult.setRequestParams(requestParams);
		listViewResult.setScd(scd);

		W5List d = FrameworkCache.getListView(projectId, listViewId);
		if (d != null) {
			listViewResult.setListView(d);
		} else {
			loadListView(listViewResult);
			d = listViewResult.getListView();
			FrameworkCache.addListView(projectId, d);
		}
		// search Form
		if (!noSearchForm && d.get_searchFormId() != 0) {
			W5FormResult searchForm = getFormResult(scd, d.get_searchFormId(), 72, requestParams);
			dao.initializeForm(searchForm, false);
			dao.loadFormCellLookups(listViewResult.getScd(), searchForm.getFormCellResults(),
					listViewResult.getRequestParams(), null);
			listViewResult.setSearchFormResult(searchForm);
		}
		return listViewResult;
	}

	private void loadListView(W5ListViewResult lr) {
		String projectId = FrameworkCache.getProjectId(lr.getScd(), "936." + lr.getListId());
		W5List d = (W5List) getCustomizedObject("from W5List t where t.listId=? and t.projectUuid=?", lr.getListId(),
				projectId, "List"); // ozel bir client icin varsa
		lr.setListView(d);

		W5Query query = getQueryResult(lr.getScd(), d.getQueryId()).getQuery();

		if (query == null) {
			query = new W5Query();
			List<W5QueryField> queryFields = find(
					"from W5QueryField t where t.queryId=? and t.projectUuid=? order by t.tabOrder", d.getQueryId(),
					projectId);
			d.set_query(query);
			query.set_queryFields(queryFields); // dataReader icin gerekli
			query.setMainTableId(
					(Integer) find("select t.mainTableId from W5Query t where t.queryId=? and t.projectUuid=?",
							d.getQueryId(), projectId).get(0));
		} else
			d.set_query(query);

		d.set_mainTable(FrameworkCache.getTable(projectId, query.getMainTableId()));
		d.set_listColumnList(find("from W5ListColumn t where t.projectUuid=? AND t.listId=? order by t.tabOrder",
				projectId, lr.getListId()));

		Map<Integer, W5QueryField> fieldMap = new HashMap<Integer, W5QueryField>();
		Map<String, W5QueryField> fieldMapDsc = new HashMap<String, W5QueryField>();
		for (W5QueryField field : query.get_queryFields()) {
			fieldMap.put(field.getQueryFieldId(), field);
			fieldMapDsc.put(field.getDsc(), field);
		}

		d.set_queryFieldMap(fieldMap);

		d.set_queryFieldMapDsc(fieldMapDsc);
		d.set_pkQueryField(fieldMap.get(d.getPkQueryFieldId()));
		int totalWidth = 0;
		for (W5ListColumn lc : d.get_listColumnList()) {
			lc.set_queryField(fieldMap.get(lc.getQueryFieldId()));
			totalWidth += lc.getWidth();
		}
		d.set_totalWidth(totalWidth);

		List<Integer> formIdz = find(
				"select t.formId from W5Form t where t.projectUuid=? AND t.objectTip=7 and t.objectId=?", projectId,
				lr.getListId());
		if (formIdz.size() > 0 && formIdz.get(0) != null)
			d.set_searchFormId(formIdz.get(0));

		d.set_toolbarItemList(
				find("from W5ObjectToolbarItem t where t.objectTip=8 AND t.objectId=? AND t.projectUuid=? order by t.tabOrder",
						lr.getListId(), projectId));
		for (W5ObjectToolbarItem c : d.get_toolbarItemList())
			switch (c.getItemTip()) { // TODO:toolbar icine bisey konulacaksa
			case 10:
			case 7:
			case 15:
			case 9:
				break;
			case 14:
			case 8:
			case 6:
				break;
			}
	}

	public W5Component loadComponent(Map<String, Object> scd, int componentId, Map paramMap) {
		W5Component c = FrameworkCache.getComponent(scd, componentId);
		if (c == null) {
			String projectId = FrameworkCache.getProjectId(scd, "3351." + componentId);
			c = (W5Component) getCustomizedObject("from W5Component t where t.componentId=? and t.projectUuid=?",
					componentId, projectId, "Component");
			FrameworkCache.addComponent(scd, c);
		}
		return c;
	}

}
