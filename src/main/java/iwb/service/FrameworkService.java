package iwb.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import iwb.cache.FrameworkCache;
import iwb.cache.FrameworkSetting;
import iwb.cache.LocaleMsgCache;
import iwb.custom.trigger.PostFormTrigger;
import iwb.dao.rdbms_impl.MetadataLoaderDAO;
import iwb.dao.rdbms_impl.PostgreSQL;
import iwb.domain.db.Log5Feed;
import iwb.domain.db.Log5GlobalNextval;
import iwb.domain.db.W5BIGraphDashboard;
import iwb.domain.db.W5Customization;
import iwb.domain.db.W5FileAttachment;
import iwb.domain.db.W5FormCell;
import iwb.domain.db.W5Project;
import iwb.domain.db.W5Query;
import iwb.domain.db.W5Table;
import iwb.domain.db.W5TableChild;
import iwb.domain.db.W5TableField;
import iwb.domain.db.W5TsMeasurement;
import iwb.domain.db.W5TsPortlet;
import iwb.domain.db.W5Tutorial;
import iwb.domain.db.W5UploadedImport;
import iwb.domain.db.W5VcsCommit;
import iwb.domain.db.W5VcsObject;
import iwb.domain.db.W5WsServer;
import iwb.domain.db.W5WsServerMethod;
import iwb.domain.helper.W5FormCellHelper;
import iwb.domain.helper.W5QueuedActionHelper;
import iwb.domain.helper.W5ReportCellHelper;
import iwb.domain.result.M5ListResult;
import iwb.domain.result.W5FormResult;
import iwb.domain.result.W5GlobalFuncResult;
import iwb.domain.result.W5PageResult;
import iwb.domain.result.W5QueryResult;
import iwb.domain.result.W5TableRecordInfoResult;
import iwb.domain.result.W5TutorialResult;
import iwb.engine.AccessControlEngine;
import iwb.engine.CRUDEngine;
import iwb.engine.ConversionEngine;
import iwb.engine.DebugEngine;
import iwb.engine.GlobalScriptEngine;
import iwb.engine.NotificationEngine;
import iwb.engine.QueryEngine;
import iwb.engine.RESTEngine;
import iwb.engine.UIEngine;
import iwb.engine.WorkflowEngine;
import iwb.exception.IWBException;
import iwb.util.DBUtil;
import iwb.util.GenericUtil;
import iwb.util.LogUtil;
import iwb.util.UserUtil;

@Service
@Transactional
public class FrameworkService {
	@Lazy
	@Autowired
	private PostgreSQL dao;


	@Lazy
	@Autowired
	private MetadataLoaderDAO metaDataDao;
	
	
	@Lazy
	@Autowired
	private CRUDEngine crudEngine;
	
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

	@Lazy
	@Autowired
	private UIEngine uiEngine;
	

	@Lazy
	@Autowired
	private RESTEngine restEngine;


	@Lazy
	@Autowired
	private DebugEngine debugEngine;
	

	
	
	public synchronized void reloadCache(int cid) {
		try {
			if (cid == -1)
				FrameworkSetting.systemStatus = 2; // suspended
			// dao.setEngine(this);
			metaDataDao.reloadFrameworkCaches(cid);
		} catch (Exception e) {
			if (FrameworkSetting.debug)
				e.printStackTrace();
		} finally {
			if (cid == -1)
				FrameworkSetting.systemStatus = 0;
		}
	}

	private boolean checkAccessRecordControlViolation(Map<String, Object> scd, int accessTip, int tableId,
			String tablePk) {
		Map<String, String> rm = new HashMap<String, String>();
		rm.put("xaccess_tip", "" + accessTip);
		rm.put("xtable_id", "" + tableId);
		rm.put("xtable_pk", tablePk);
		Map m = executeQuery2Map(scd, 588, rm);
		return (m != null && !GenericUtil.accessControl(scd, (short) accessTip, (String) m.get("access_roles"),
				(String) m.get("access_users")));
	}

	public W5FormResult getFormResultByQuery(Map<String, Object> scd, int formId, int queryId,
			Map<String, String> requestParams) {
		return uiEngine.getFormResultByQuery(scd, formId, queryId, requestParams);
	}

	public W5FormResult getFormResult(Map<String, Object> scd, int formId, int action,
			Map<String, String> requestParams) {
		return uiEngine.getFormResult(scd, formId, action, requestParams);
	}



	@SuppressWarnings({ "unused", "unchecked" })
	private List<W5QueuedActionHelper> postForm4Table(W5FormResult formResult, String paramSuffix,
			Set<String> checkedParentRecords) {
		return crudEngine.postForm4Table(formResult, paramSuffix, checkedParentRecords);
	}


	public Map<String, Object> getFormCellCode(Map<String, Object> scd, Map<String, String> requestParams,
			int formCellId, int count) {
		/*
		 * int formId = (Integer)dao.getCustomizedObject(
		 * "select t.formId from W5FormCell t where t.formCellId=? AND t.customizationId=?"
		 * , formCellId, (Integer)scd.get("customizationId"),"FormElement");
		 * W5FormResult formResult = dao.getFormResult(scd, formId, 2,
		 * requestParams); for(W5FormCell
		 * fc:formResult.getForm().get_formCells())if(fc.getFormCellId()==
		 * formCellId){
		 * if(GenericUtil.uInt(fc.getLookupIncludedParams())==0)break; String
		 * res=""; Map m = new HashMap(); m.put("success", true);
		 * for(W5FormCellCodeDetail
		 * fccd:fc.get_formCellCodeDetailList())switch(fccd.getCodeTip()){ case
		 * 1:res+=fccd.getCodeLength()>0 ?
		 * GenericUtil.lPad(fccd.getDefaultValue(),fccd.getCodeLength(),fccd.
		 * getFillCharacter()) : fccd.getDefaultValue();break; //sabit case 2://
		 * manuel(klavyeden) case 5:// manuel(combo) case 7:// keyword(combo)
		 * m.put("msg", LocaleMsgCache.get2(scd, "js_manual_entry")); return m;
		 * case 3://Otomatik Map<String, Object> qmz = dao.runSQLQuery2Map(
		 * "select iwb.fnc_form_cell_code_detail_auto(${scd.customizationId},"
		 * +fccd.getFormCellCodeDetailId()+",'"+res+"') dsc from dual", scd,
		 * requestParams, null); if(!GenericUtil.isEmpty(qmz)){ String val =
		 * qmz.values().toArray()[0].toString(); res+= fccd.getCodeLength()>0 ?
		 * GenericUtil.lPad(val,fccd.getCodeLength(),fccd.getFillCharacter()) :
		 * val; } else throw new IWBException("validation","FormElement",
		 * formCellId, null, "FormElementCode: wrong automatic definition",
		 * null); break; case 4://Formul/Advanced/SQL String sql =
		 * fccd.getDefaultValue(); switch(fccd.getSourceFcQueryFieldId()){ case
		 * 1:case 2:case 3: String[] qrs=new String[]{"yy","yymm","yymmdd"};
		 * sql="select to_char(current_date,'"
		 * +qrs[fccd.getSourceFcQueryFieldId()-1]+"')"; default: Map<String,
		 * Object> qm = dao.runSQLQuery2Map(sql, scd, requestParams, null);
		 * if(!GenericUtil.isEmpty(qm)){ String val =
		 * qm.values().toArray()[0].toString(); res+=fccd.getCodeLength()>0 ?
		 * GenericUtil.lPad(val,fccd.getCodeLength(),fccd.getFillCharacter()) :
		 * val; } else throw new IWBException("validation","FormElement",
		 * formCellId, null,
		 * "FormElementCode: wrong SQL code 4 (Formul/Advanced)", null); }
		 * break; case 6://formdan(combo) String fdsc=null;
		 * if(fccd.getSourceFcQueryFieldId()!=0){ List<String> lqf =
		 * (List<String>)dao.find(
		 * "select qf.dsc from W5QueryField qf where qf.queryFieldId=?"
		 * ,fccd.getSourceFcQueryFieldId()); if(GenericUtil.isEmpty(lqf)) return
		 * m; //TODO: not implemented (query'yi calistirip ordaki degeri almak
		 * lazim) fdsc=lqf.get(0); } boolean notFound=true; for(W5FormCell
		 * fc2:formResult.getForm().get_formCells())if(fc2.getFormCellId()==fccd
		 * .getSourceFormCellId()){ String sfcv =
		 * requestParams.get(fc2.getDsc()); if(GenericUtil.isEmpty(sfcv)){
		 * m.put("msg", LocaleMsgCache.get2(scd, fc2.getLocaleMsgKey()) +
		 * LocaleMsgCache.get2(scd, "js_value_not_set")); return m; }
		 * if(fdsc==null) res+=fccd.getCodeLength()>0 ?
		 * GenericUtil.lPad(sfcv,fccd.getCodeLength(),fccd.getFillCharacter()) :
		 * sfcv; else switch(fc2.getControlTip()){ case 7:case 10: Map m2 = new
		 * HashMap(); // m2.put("_qid", ""+fc2.getLookupQueryId());
		 * m2.put("xid", sfcv); W5QueryResult qr2 = executeQuery(scd,
		 * fc2.getLookupQueryId(), m2);
		 * if(GenericUtil.isEmpty(qr2.getErrorMap()) &&
		 * !GenericUtil.isEmpty(qr2.getData())){ List<Object[]> l3 =
		 * qr2.getData(); int rTabOrder = -1, iTabOrder = -1; for(W5QueryField
		 * qq:qr2.getNewQueryFields())if(qq.getDsc().equals(fdsc)){
		 * rTabOrder=qq.getTabOrder(); if(iTabOrder!=-1)break; } else
		 * if(qq.getDsc().equals("id")){ iTabOrder=qq.getTabOrder();
		 * if(rTabOrder!=-1)break; } if(rTabOrder == -1 || iTabOrder ==-1){
		 * throw new IWBException("validation","FormElement", formCellId, null,
		 * "FormElementCode: wrong SourceFormCell definition(Formdan/Combo) init (rTabOrder="
		 * +rTabOrder+",iTabOrder="+iTabOrder+")", null); } boolean found =
		 * false; for(Object[]
		 * o2:l3)if(o2[iTabOrder-1].toString().equals(sfcv)){
		 * res+=fccd.getCodeLength()>0 ?
		 * GenericUtil.lPad(o2[rTabOrder-1].toString(),fccd.getCodeLength(),fccd
		 * .getFillCharacter()) : o2[rTabOrder-1].toString(); found = true;
		 * break; } if(!found){ throw new
		 * IWBException("validation","FormElement", formCellId, null,
		 * "FormElementCode: (Formdan/Combo) value note found (rTabOrder="
		 * +rTabOrder+",iTabOrder="+iTabOrder+")", null); } } else return m;
		 * break; case 9: throw new IWBException("validation","FormElement",
		 * formCellId, null,
		 * "FormElementCode: wrong SourceFormCell definiton (Formdan/Combo) is remote"
		 * , null); default: res+=fccd.getCodeLength()>0 ?
		 * GenericUtil.lPad(sfcv,fccd.getCodeLength(),fccd.getFillCharacter()) :
		 * sfcv; } notFound = false; break; } if(notFound) throw new
		 * IWBException("validation","FormElement", formCellId, null,
		 * "FormElementCode: wrong SourceFormCell (Formdan/Combo)", null);
		 * break; } m.put("result", res); return m; } throw new
		 * IWBException("validation","FormElement", formCellId, null,
		 * "FormElementCode: wrong FormCellId", null);
		 */
		return null;
	}

	public W5FormResult postForm4Table(Map<String, Object> scd, int formId, int action,
			Map<String, String> requestParams, String prefix) {
		return crudEngine.postForm4Table(scd, formId, action, requestParams, prefix);
	}

	public List<W5ReportCellHelper> getGridReportResult(Map<String, Object> scd, int gridId, String gridColumns,
			Map<String, String> requestParams) {
		return queryEngine.getGridReportResult(scd, gridId, gridColumns, requestParams);
	}

	private Map<String, Object> executeQuery2Map(Map<String, Object> scd, int queryId,
			Map<String, String> requestParams) {

		return queryEngine.executeQuery2Map(scd, queryId, requestParams);
	}

	public W5QueryResult executeQuery(Map<String, Object> scd, int queryId, Map<String, String> requestParams) {
		return queryEngine.executeQuery(scd, queryId, requestParams);

	}

	/*
	 * public QueryResult executeInfluxQuery(Map<String, Object> scd, String
	 * sql, String dbName){ return
	 * influxDao.runQuery(FrameworkCache.wProjects.get((String)scd.get(
	 * "projectId")), sql, dbName); } public void insertInfluxRecord(Map<String,
	 * Object> scd, String measurement, Map<String, Object> tags, Map<String,
	 * Object> fields, String date){
	 * influxDao.insert(FrameworkCache.wProjects.get((String)scd.get("projectId"
	 * )), measurement, tags, fields, date); }
	 */

	public W5PageResult getPageResult(Map<String, Object> scd, int pageId, Map<String, String> requestParams) {
		return uiEngine.getPageResult(scd, pageId, requestParams);
	}

	public void sendMail(Map<String, Object> scd, String mailTo, String mailCc, String mailBcc, String subject,
			String body, String fileIds) {
		/*
		 * List<W5FileAttachment> fileAttachments = null; String fas =
		 * parameterMap.get("pfile_attachment_ids"); if(fas!=null &&
		 * fas.length()>0){ String[] q = fas.split(","); if(q.length>0){
		 * parameterMap.put("pfile_attachment_ids", fas); Object[] ps = new
		 * Object[q.length+1]; String sql =
		 * "from W5FileAttachment t where t.customizationId=? and  t.fileAttachmentId in ("
		 * ; int i = 1; ps[0]=scd.get("customizationId"); for(String s:q){
		 * ps[i++] = GenericUtil.uInt(s); sql+="?,"; } fileAttachments =
		 * dao.find(sql.substring(0,sql.length()-1)+")", ps); } }
		 * W5ObjectMailSetting oms = (W5ObjectMailSetting) dao.find(
		 * "from W5ObjectMailSetting t where t.customizationId=? and t.mailSettingId=?"
		 * , (Integer)scd.get("customizationId"),GenericUtil.uInt((Object)
		 * parameterMap.get("pmail_setting_id"))).get(0); if(oms!=null){ W5Email
		 * email= new
		 * W5Email(parameterMap.get("pmail_to"),parameterMap.get("pmail_cc"),
		 * parameterMap.get("pmail_bcc"),parameterMap.get("pmail_subject"),
		 * parameterMap.get("pmail_body"),
		 * parameterMap.get("pmail_keep_body_original"), fileAttachments);
		 * result = MailUtil.sendMail(scd, oms, email); if(result!=null){
		 * //basarisiz, queue'ye at parameterMap.put("perror_msg", result); } }
		 * if(FrameworkCache.getAppSettingIntValue(scd, "feed_flag")!=0 &&
		 * result==null)try{ W5Feed feed = new W5Feed(scd);
		 * feed.setFeedTip((short)(dbFuncId==-631 ? 22:21)); //sms:mail
		 * feed.setTableId(GenericUtil.uInt(parameterMap.get("_tableId")));feed.
		 * setTablePk(GenericUtil.uInt(parameterMap.get("_tablePk")));
		 * if(dbFuncId!=-631){ List lx = new ArrayList();
		 * lx.add(scd.get("customizationId"));lx.add(GenericUtil.uInt((Object)
		 * parameterMap.get("pmail_setting_id"))); Map mx = dao.runSQLQuery2Map(
		 * "select cx.ACCESS_ROLES, cx.ACCESS_USERS from iwb.w5_access_control cx where cx.ACCESS_TIP=0 AND cx.customization_id=? AND cx.table_id=48 AND cx.table_pk=?"
		 * , lx,null); if(mx!=null &&
		 * !mx.isEmpty())feed.set_viewAccessControl(new
		 * W5AccessControlHelper((String)mx.get("access_roles"),(String)mx.get(
		 * "access_users"))); }
		 * feed.set_tableRecordList(dao.findRecordParentRecords(scd,feed.
		 * getTableId(),feed.getTablePk(), 0, true));
		 * if(feed.get_tableRecordList()!=null &&
		 * feed.get_tableRecordList().size()>0){
		 * feed.set_commentCount(feed.get_tableRecordList().get(0).
		 * getCommentCount()); if(feed.get_viewAccessControl()==null &&
		 * feed.get_tableRecordList().get(0).getViewAccessControl()!=null){
		 * feed.set_viewAccessControl(feed.get_tableRecordList().get(0).
		 * getViewAccessControl()); } } saveObject(feed);
		 * FrameworkCache.addFeed(scd, feed, true); } catch(Exception e){}
		 *
		 */
	}

	public W5GlobalFuncResult executeFunc(Map<String, Object> scd, int dbFuncId, Map<String, String> parameterMap,
			short accessSourceType) {
		return scriptEngine.executeGlobalFunc(scd, dbFuncId, parameterMap, accessSourceType);
		
	}

	public W5FormResult bookmarkForm(Map<String, Object> scd, int formId, int action,
			Map<String, String> parameterMap) {
		W5FormResult formResult = metaDataDao.getFormResult(scd, formId, 2, parameterMap);
		dao.bookmarkForm(parameterMap.get("_dsc"), action > 10 ? -formId : formId, (Integer) scd.get("userId"),
				(Integer) scd.get("customizationId"), formResult);

		return formResult;
	}

	public void saveObject(Object o) {
		dao.saveObject(o);
		if (o instanceof W5FileAttachment) { // bununla ilgili islemler
			W5FileAttachment fa = (W5FileAttachment) o;
			W5FormResult formResult = new W5FormResult(0);
			formResult.setRequestParams(new HashMap());
			formResult.setScd(new HashMap());
			formResult.getRequestParams().put("table_id", "" + fa.getTableId());
			formResult.getRequestParams().put("table_pk", "" + fa.getTablePk());
			formResult.getScd().put("userId", fa.getUploadUserId());
			formResult.getScd().put("customizationId", fa.getCustomizationId());
			formResult.getScd().put("roleId", 2);
			formResult.getScd().put("userTip", 2);
			if (fa.getTableId() == 336 && fa.getFileTypeId() != null && fa.getFileTypeId() == -999) { // profile
																										// picture
				makeProfilePicture(GenericUtil.uInt(fa.getTablePk()), fa);
			}
			PostFormTrigger.afterPostForm(formResult, dao, null);
		}
	}

	private boolean makeProfilePicture(int userId, W5FileAttachment fa) {
		if (FrameworkSetting.feed && FrameworkCache.getAppSettingIntValue(fa.getCustomizationId(), "feed_flag") != 0) {
			Map scd = new HashMap();
			scd.put("userId", fa.getUploadUserId());
			scd.put("roleId", 2);
			scd.put("customizationId", fa.getCustomizationId());
			scd.put("userTip", 2);
			Log5Feed feed = new Log5Feed(scd);
			feed.set_showFeedTip((short) 1);
			feed.setFeedTip((short) 24); // remove:master icin
			dao.saveObject(feed);
			FrameworkCache.addFeed(scd, feed, true);
		}
		UserUtil.setUserProfilePicture(userId, fa.getFileAttachmentId());

		return dao.executeUpdateSQLQuery("update iwb.w5_user set profile_picture_id=? where user_id=?",
				fa.getFileAttachmentId(), userId) == 1;
	}

	public void updateObject(Object o) {
		dao.updateObject(o);
	}

	public W5FileAttachment loadFile(Map<String, Object> scd, int fileAttachmentId) { // +:fileId,
																						// -:userId
																						// :
																						// Map<String,
																						// Object>
																						// scd,
		if (fileAttachmentId < 0) {
			int newFileAttachmentId = UserUtil.getUserProfilePicture(-fileAttachmentId);
			if (newFileAttachmentId == 0) {
				List l = dao.executeSQLQuery("select t.profile_picture_id from iwb.w5_user t where t.user_id=?",
						-fileAttachmentId);
				if (!GenericUtil.isEmpty(l)) {
					fileAttachmentId = GenericUtil.uInt(l.get(0));
				}
			} else
				fileAttachmentId = newFileAttachmentId;
			if (fileAttachmentId == 1 || fileAttachmentId == 2) {
				W5FileAttachment fa2 = new W5FileAttachment();
				fa2.setFileAttachmentId(fileAttachmentId);
				return fa2;
			}
		}
		if (fileAttachmentId <= 0)
			return null;
		List<W5FileAttachment> fal = dao.find("from W5FileAttachment t where t.fileAttachmentId=?", fileAttachmentId);
		if (GenericUtil.isEmpty(fal))
			return null;
		W5FileAttachment fa = fal.get(0);
		// if(scd==null){
		scd = new HashMap();
		scd.put("customizationId", fa.getCustomizationId());
		// } else
		// if((Integer)scd.get("customizationId")!=fa.getCustomizationId()){
		// return null;
		// }
		if (fa != null) { // bununla ilgili islemler
			if (checkAccessRecordControlViolation(scd, 0, fa.getTableId(), fa.getTablePk())) {
				throw new IWBException("security", "FileAttachment", fa.getFileAttachmentId(), null,
						LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_dosya_yetki"), null);
			} else if (fa.getCustomizationId() != GenericUtil.uInt(scd.get("customizationId"))) {
				throw new IWBException("security", "File Attachment", fa.getFileAttachmentId(), null,
						LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_dosya_yetki"), null);
			}
		}
		return fa;
	}

	public W5FormResult postBulkConversion(Map<String, Object> scd, int conversionId, int dirtyCount,
			Map<String, String> requestParams, String prefix) {
		return conversionEngine.postBulkConversion(scd, conversionId, dirtyCount, requestParams, prefix);
	}

	public W5FormResult postEditGrid4Table(Map<String, Object> scd, int formId, int dirtyCount,
			Map<String, String> requestParams, String prefix, Set<String> checkedParentRecords) {
		return crudEngine.postEditGrid4Table(scd, formId, dirtyCount, requestParams, prefix, checkedParentRecords);
		
	}

	public W5GlobalFuncResult postEditGridGlobalFunc(Map<String, Object> scd, int dbFuncId, int dirtyCount,
			Map<String, String> requestParams, String prefix) {
		return scriptEngine.postEditGridGlobalFunc(scd, dbFuncId, dirtyCount, requestParams, prefix);
	}



	public Map<String, Object> userRoleSelect(int userId, int userRoleId, int customizationId, String projectId,
			String mobileDeviceId) {
		Map<String, Object> scd = new HashMap<String, Object>();
		scd.put("userId", userId);
		scd.put("userRoleId", userRoleId);
		scd.put("customizationId", customizationId);
		Map<String, String> rm = new HashMap();
		if (!GenericUtil.isEmpty(projectId))
			rm.put("projectId", projectId);
		Map<String, Object> m = executeQuery2Map(scd, 2, rm); // mainSessionQuery
		if (m == null)
			return null;
		if (false && !GenericUtil.isEmpty(mobileDeviceId)) {
			Map parameterMap = new HashMap();
			parameterMap.put("pmobile_device_id", mobileDeviceId);
			parameterMap.put("pactive_flag", 1);
			executeFunc(m, 673, parameterMap, (short) 4);
		}
		return m;
	}

	public Map<String, Object> userRoleSelect4App(W5Project po, int userId, int userRoleId, String mobileDeviceId) {
		Map<String, Object> scd = new HashMap<String, Object>();
		scd.put("userId", userId);
		scd.put("userRoleId", userRoleId);
		scd.put("customizationId", po.getCustomizationId());
		scd.put("projectId", po.getProjectUuid());
		Map<String, String> rm = new HashMap();
		Map<String, Object> m = executeQuery2Map(scd, po.getSessionQueryId(), rm); // mainSessionQuery
		if (m == null)
			return null;
		return m;
	}

	public Map<String, Object> userSession4Auth(int userId, int customizationId) {
		Map<String, Object> scd = new HashMap<String, Object>();
		scd.put("userId", userId);
		scd.put("customizationId", customizationId);
		Map<String, String> rm = new HashMap();
		return executeQuery2Map(scd, 4546, rm); // auth.SessionQuery
	}



	// TODO: onayda, iade'de, reject'te notification gitsin
	public Map<String, Object> approveRecord(Map<String, Object> scd, int approvalRecordId, int approvalAction,
			Map<String, String> parameterMap) {
		return workflowEngine.approveRecord(scd, approvalRecordId, approvalAction, parameterMap);
	}

	public W5FormResult importUploadedData(Map<String, Object> scd, int uploadedImportId,
			Map<String, String> requestParams) {
		W5UploadedImport ui = (W5UploadedImport) dao
				.find("from W5UploadedImport t where t.customizationId=? and t.uploadedImportId=?",
						scd.get("customizationId"), uploadedImportId)
				.get(0);
		W5FormResult formResult = metaDataDao.getFormResult(scd, ui.getFormId(), 2, requestParams);
		W5Table t = FrameworkCache.getTable(scd, formResult.getForm().getObjectId()); // formResult.getForm().get_sourceTable();
		String locale = (String) scd.get("locale");
		/*
		 * if(t.getAccessViewTip()==0 && !PromisCache.roleAccessControl(scd,
		 * 0)){ throw new PromisException("security","Module", 0, null,
		 * "Modul Kontrol: Erişim kontrolünüz yok", null); }
		 * if(!PromisUtil.accessControl(scd, t.getAccessViewTip(),
		 * t.getAccessViewRoles(), t.getAccessViewUsers())){ throw new
		 * PromisException("security","Form", ui.getFormId(), null,
		 * PromisLocaleMsg.get2(0,(String)scd.get("locale"),
		 * "fw_guvenlik_tablo_kontrol_goruntuleme"), null); }
		 */
		PostFormTrigger.beforePostForm(formResult, dao, "");

		// accessControl4FormTable(formResult);
		if (formResult.isViewMode()) {
			throw new IWBException("security", "Form", ui.getFormId(), null,
					LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_tablo_kontrol_guncelleme"), null);
		}
		List<Object[]> uicl = dao.find(
				"select x.mappingType,x.importColumn,x.formCellId,x.defaultValue,y.dsc from W5UploadedImportCol x,W5FormCell y where y.customizationId=x.customizationId and y.customizationId=? AND y.formCellId=x.formCellId AND x.uploadedImportId=? order by y.tabOrder",
				scd.get("customizationId"), uploadedImportId);
		List<Object> sqlParams = new ArrayList();
		String detailSql = "select x.row_no";
		for (Object[] o : uicl) {
			short mappingType = (Short) o[0];
			String importColumn = (String) o[1];
			int formCellId = (Integer) o[2];
			String dsc = (String) o[4];
			if (/* importColumn==null || */ dsc == null)
				continue;
			switch (mappingType) {
			case 0: // constant
				if (o[3] != null)
					requestParams.put(dsc, (String) o[3]);
				break;
			case 1: // row-data
				if (o[3] != null) {
					detailSql += ", coalesce(x." + importColumn + ",?) " + dsc;
					sqlParams.add(o[3]);
				} else
					detailSql += ", x." + importColumn + " " + dsc;
				break;
			case 2: // mapping
				short cellControlTip = 0;
				int cellLookupQueryId = 0;
				for (W5FormCell c : formResult.getForm().get_formCells()) {
					if (c.getFormCellId() == formCellId) {
						cellControlTip = c.getControlTip();
						cellLookupQueryId = c.getLookupQueryId();
						break;
					}
				}
				if (cellControlTip == 6) { // static lookup
					detailSql += ", (select ld.val from iwb.w5_look_up_detay ld where ld.customization_id=x.customization_id and ld.look_up_id="
							+ cellLookupQueryId + " and lower(iwb.fnc_locale_msg(ld.customization_id,ld.dsc,'" + locale
							+ "'))=lower(x." + importColumn + "))" + dsc;
				} else if (cellControlTip == 7 || cellControlTip == 9) { // lookup
																			// query
					W5QueryResult queryResult = metaDataDao.getQueryResult(scd, cellLookupQueryId);
					if (queryResult.getMainTable() != null) {
						boolean dscExists = false;
						for (W5TableField f : queryResult.getMainTable().get_tableFieldList()) {
							if (f.getDsc().equals("dsc")) {
								dscExists = true;
								break;
							}
						}
						if (dscExists) {
							String pk = queryResult.getMainTable().get_tableParamList().get(0).getExpressionDsc();
							detailSql += ", (select d." + pk + " from " + queryResult.getMainTable().getDsc()
									+ " d where d.customization_id=x.customization_id and lower(d.dsc)=lower(x."
									+ importColumn + "))" + dsc;
						} else
							detailSql += ", x." + importColumn + " " + dsc;
					}
				} else {
					/*
					 * sqlParams.add(ui.getFileAttachmentId());
					 * sqlParams.add(formCellId); if(o[3]!=null){ detailSql+=
					 * ", coalesce((select q.import_lookup_val from iwb.w5_uploaded_import_col_map q where q.customization_id=x.customization_id and q.file_attachment_id=? AND q.form_cell_id=? AND q.import_val=x."
					 * +importColumn+"),?)" + dsc; sqlParams.add(o[3]); } else
					 * detailSql+=
					 * ", (select q.import_lookup_val from iwb.w5_uploaded_import_col_map q where q.customization_id=x.customization_id and q.file_attachment_id=? AND q.form_cell_id=? AND q.import_val=x."
					 * +importColumn+")" + dsc;
					 */
					if (o[3] != null) {
						detailSql += ", x." + importColumn + " " + dsc;
					}
				}
				break;
			case 3: // auto-increment
				detailSql += ", rownum " + dsc;
				break;
			}
		}
		detailSql += " from iwb.w5_uploaded_data x where x.customization_id=? and x.file_attachment_id=? and x.row_no>="
				+ (ui.getStartRowNo() - 1);
		if (ui.getEndRowNo() != null) {
			detailSql += " and x.row_no<" + ui.getEndRowNo();
		}
		detailSql += " order by x.row_no";
		sqlParams.add(scd.get("customizationId"));
		sqlParams.add(ui.getFileAttachmentId());

		if (FrameworkSetting.debug)
			System.out.println("ERALP-XXX: " + GenericUtil.replaceSql(detailSql, sqlParams));
		List<Map> rl = dao.executeSQLQuery2Map(detailSql, sqlParams);
		int errorCount = 0, importedCount = 0;
		if (rl != null)
			for (Map m : rl) {
				m.putAll(requestParams);
				formResult.setRequestParams(m);
				formResult.setErrorMap(new HashMap());
				dao.insertFormTable(formResult, "");
				if (!formResult.getErrorMap().isEmpty()) { // hata var
					if (ui.getRowErrorStrategyTip() == 1) { // throw error
						formResult.getErrorMap().put(" [SATIR_NO]", m.get("row_no").toString());
						return formResult;
					} else
						errorCount++;
				} else
					importedCount++;
			}
		formResult.getOutputFields().put("errorCount", errorCount);
		formResult.getOutputFields().put("importedCount", importedCount);
		// if(true)throw new PromisException("security","Form", ui.getFormId(),
		// null,
		// PromisLocaleMsg.get2(0,(String)scd.get("locale"),"fw_guvenlik_tablo_kontrol_goruntuleme"),
		// null);
		return formResult;
	}

	public synchronized void scheduledFrameworkCacheReload() {

	}


	public W5TableRecordInfoResult getTableRecordInfo(Map<String, Object> scd, int tableId, int tablePk) {
		// if(t==null)
		W5TableRecordInfoResult result = dao.getTableRecordInfo(scd, tableId, tablePk);
		if (result != null) {
			result.setParentList(dao.findRecordParentRecords(scd, tableId, tablePk, 10, false));
			result.setChildList(dao.findRecordChildRecords(scd, tableId, tablePk));
			return result;
		} else
			return null;
	}

	public W5QueryResult getTableRelationData(Map<String, Object> scd, int tableId, int tablePk, int relId) {
		W5Table mt = FrameworkCache.getTable(scd, tableId); // master table
		if (!GenericUtil.accessControlTable(scd, mt))
			throw new IWBException("security", "Module", 0, null,
					LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_modul_kontrol"), null);
		if (relId == 0 || GenericUtil.isEmpty(mt.get_tableChildList()))
			throw new IWBException("security", "Table", tableId, null, "wrong relationId or no children data", null);
		W5TableChild tc = null;
		for (W5TableChild qi : mt.get_tableChildList())
			if (qi.getTableChildId() == relId) {
				tc = qi;
				break;
			}
		if (tc == null)
			throw new IWBException("security", "Table", tableId, null, "relation not found", null);

		W5Table t = FrameworkCache.getTable(scd, tc.getRelatedTableId()); // detail
																			// table
		if (GenericUtil.isEmpty(t.getSummaryRecordSql()))
			throw new IWBException("framework", "Table", tableId, null, "ERROR: summarySql not defined", null);
		W5Query q = new W5Query(t.getTableId());
		q.setSqlSelect("(" + t.getSummaryRecordSql() + ") dsc, x." + t.get_tableFieldList().get(0).getDsc() + " id");
		q.setSqlFrom(t.getDsc() + " x");
		StringBuilder sqlWhere = new StringBuilder();
		sqlWhere.append("x.").append(t.get_tableFieldMap().get(tc.getRelatedTableFieldId()).getDsc())
				.append("=${req.id}");
		if (tc.getRelatedStaticTableFieldId() != 0)
			sqlWhere.append("AND x.").append(t.get_tableFieldMap().get(tc.getRelatedStaticTableFieldId()).getDsc())
					.append("=").append(tc.getRelatedStaticTableFieldVal());
		if (t.get_tableParamList().size() > 1
				&& t.get_tableParamList().get(1).getExpressionDsc().equals("customization_id"))
			sqlWhere.append("AND x.customization_id=${scd.customizationId}");
		q.setSqlWhere(sqlWhere.toString());
		Map<String, String> requestParams = new HashMap();
		requestParams.put("id", "" + tablePk);

		q.set_queryFields(dao.find("from W5QueryField f where f.queryId=15 order by f.tabOrder")); // queryField'in
		// lookUp'i
		q.set_queryParams(new ArrayList());

		W5QueryResult qr = new W5QueryResult(t.getTableId());
		qr.setScd(scd);
		qr.setRequestParams(requestParams);
		qr.setErrorMap(new HashMap());
		qr.setMainTable(t);
		qr.setQuery(q);
		boolean tabOrderFlag = false;
		for (W5TableField tf : t.get_tableFieldList())
			if (tf.getDsc().equals("tab_order")) {
				tabOrderFlag = true;
				break;
			}
		qr.setOrderBy(tabOrderFlag ? "x.tab_order asc,x." + t.get_tableFieldList().get(0).getDsc() + " desc"
				: "x." + t.get_tableFieldList().get(0).getDsc() + " desc");
		qr.prepareQuery(null);

		if (qr.getErrorMap().isEmpty()) {
			qr.setFetchRowCount(10);
			qr.setStartRowNumber(0);
			dao.runQuery(qr);
		}

		return qr;
	}

	public String getFormCellCodeDetail(Map<String, Object> scd, Map<String, String> requestParams, int fccdId) {
		/*
		 * W5FormCellCodeDetail fccd =
		 * (W5FormCellCodeDetail)dao.getCustomizedObject(
		 * "from W5FormCellCodeDetail t where t.formCellCodeDetailId=? AND t.customizationId=?"
		 * , fccdId, (Integer)scd.get("customizationId"), null); if(fccd==null
		 * || fccd.getCodeTip()!=4)return ""; String sql =
		 * fccd.getDefaultValue(); switch(fccd.getSourceFcQueryFieldId()){ case
		 * 1:case 2:case 3: String[] qrs=new String[]{"yy","yymm","yymmdd"};
		 * sql="select to_char(current_date,'"
		 * +qrs[fccd.getSourceFcQueryFieldId()-1]+"')"; default: Map<String,
		 * Object> qm = dao.runSQLQuery2Map(sql, scd, requestParams, null);
		 * if(!GenericUtil.isEmpty(qm)){ String val =
		 * qm.values().toArray()[0].toString(); return fccd.getCodeLength()>0 ?
		 * GenericUtil.lPad(val,fccd.getCodeLength(),fccd.getFillCharacter()) :
		 * val; } else throw new IWBException("validation","FormElement",
		 * fccd.getFormCellId(), null,
		 * "FormElementCode: wrong SQL code 4 (Formul/Advanced)", null); }
		 */
		return "";
	}

	public void checkAlarms(Map<String, Object> scd) {
		if (true)
			return;
		/*
		 * List<W5FormSmsMailAlarm> l = dao.find(
		 * "from W5FormSmsMailAlarm t where t.status=1 AND t.customizationId=? order by t.alarmDttm"
		 * , scd.get("customizationId")); long d = new Date().getTime();
		 * for(W5FormSmsMailAlarm
		 * a:l)if(d-a.getAlarmDttm().getTime()>1000*30)try{ scd.put("userId",
		 * a.getInsertUserId()); scd.put("userTip", 2); List l2 = dao.find(
		 * "from W5FormSmsMail x where x.formSmsMailId=? AND x.customizationId=?"
		 * , a.getFormSmsMailId(), a.getCustomizationId()); W5GlobalFuncResult
		 * rdb = null; if(l2.size()==1){ W5FormSmsMail fsm =
		 * (W5FormSmsMail)l2.get(0); Map m = new HashMap(); m.put("ptable_id",
		 * a.getTableId());m.put("ptable_pk", a.getTablePk());
		 * switch(fsm.getSmsMailTip()){ case 0://sms
		 * //parameterMap.get("phone"),parameterMap.get("body") //
		 * m.putAll(dao.interprateSmsTemplate(fsm, scd, new HashMap(),
		 * a.getTableId(), a.getTablePk())); // rdb = executeFunc(scd, -631, m,
		 * (short)1); break; case 1://mail //W5Email email= new
		 * W5Email(parameterMap.get("pmail_to"),parameterMap.get("pmail_cc"),
		 * parameterMap.get("pmail_bcc"),parameterMap.get("pmail_subject"),
		 * parameterMap.get("pmail_body"),
		 * parameterMap.get("pmail_keep_body_original"), fileAttachments); //
		 * m.put("pmail_setting_id",
		 * FrameworkCache.getAppSettingStringValue(scd, "default_outbox_id"));
		 * // m.putAll(dao.interprateMailTemplate(fsm, scd, new HashMap(),
		 * a.getTableId(), a.getTablePk())); // rdb = executeFunc(scd, -650, m,
		 * (short)1); break; default: break; } Log5Notification n = new
		 * Log5Notification(a);
		 * n.set_tableRecordList(dao.findRecordParentRecords(scd,a.getTableId(),
		 * a.getTablePk(),0, true)); UserUtil.publishNotification(n, false);
		 * a.setStatus(rdb== null || rdb.isSuccess() ? (short)0 : (short)2); //
		 * 0:done, 1: active, 2:error sending, 3:canceled } else
		 * a.setStatus((short)2); } catch(Exception e) { a.setStatus((short)2);
		 * // 0:done, 1: active, 2:error sending, 3:canceled } finally{
		 * dao.updateObject(a); } else break;
		 */
	}

	public int getSubDomain2CustomizationId(String subDomain) {
		int res = 0;
		try {
			List l = dao.executeSQLQuery("select c.customization_id from iwb.w5_customization c where c.sub_domain=?",
					subDomain);
			if (!GenericUtil.isEmpty(l))
				for (Object o : l) {
					res = GenericUtil.uInt(o);
					break;
				}
		} catch (Exception e) {
			if (FrameworkSetting.debug)
				e.printStackTrace();
		}
		return res;
	}

	public HashMap<String, Object> getUser(int customizationId, int userId) {
		HashMap<String, Object> user = new HashMap<String, Object>();
		List<Object[]> userObject = dao.executeSQLQuery(
				"select x.dsc,x.user_id,x.gsm,x.email from iwb.w5_user x where x.customization_id=? and x.user_id = ?",
				customizationId, userId);
		if (userObject != null && userObject.size() > 0) {
			user.put("dsc", userObject.get(0)[0]);
			user.put("id", userObject.get(0)[1]);
			user.put("gsm", userObject.get(0)[2]);
			user.put("email", userObject.get(0)[3]);
		}
		return user;
	}

	/*
	 * public String fileToBase64Code(Map<String, Object> scd, int
	 * fileAttachmentId){ FileInputStream stream = null; String code=""; try{
	 * W5FileAttachment fa = loadFile(scd, fileAttachmentId); if(fa==null){
	 * return ""; } String customizationId=String.valueOf(
	 * (scd.get("customizationId")==null) ? 0 : scd.get("customizationId"));
	 * String file_path=FrameworkCache.getAppSettingStringValue(scd,
	 * "file_local_path"); File file = new File(file_path + "/" +
	 * customizationId + "/attachment/"+ fa.getSystemFileName()); byte[]
	 * fileData = new byte[(int) file.length()]; stream = new
	 * FileInputStream(file); stream.read(fileData); code =
	 * Base64.encodeBase64String(fileData); } catch (Exception e) {
	 * if(FrameworkSetting.debug)e.printStackTrace(); } finally { if
	 * (stream!=null) try {stream.close(); }catch (IOException e) {} } return
	 * code; }
	 */
	public String getCustomizationLogoFilePath(Map<String, Object> scd) {
		// firma logosu
		String fileLocalPath = FrameworkCache.getAppSettingStringValue(scd, "file_local_path");
		String logoFilePath = fileLocalPath + "/" + scd.get("customizationId") + "/jasper/logo.jpg";

		return logoFilePath;
	}

	public Map<String, String> sendMailForgotPassword(Map<String, Object> scd, Map<String, String> requestParams) {
		return null;
		/*
		 * Map<String, String> res = new HashMap<String, String>();
		 * res.put("success", "0"); res.put("msg", ""); try{ String email =
		 * requestParams.get("email"); String pcustomization_id =
		 * requestParams.get("pcustomization_id"); if
		 * (!GenericUtil.isEmpty(email)){ List<Object[]> userList =
		 * dao.executeSQLQuery(
		 * "select u.user_id, u.user_name, u.customization_id from iwb.w5_user u where u.email=? and (? is null or u.customization_id=?)"
		 * , email, pcustomization_id, pcustomization_id); if (userList!=null &&
		 * !userList.isEmpty()){ requestParams.put("pcustomization_id",
		 * userList.get(0)[2].toString()); requestParams.put("puser_id",
		 * userList.get(0)[0].toString()); requestParams.put("table_pk",
		 * userList.get(0)[0].toString()); }else{ res.put("msg",
		 * "mail_not_found_in_system"); return res; } } try{ W5GlobalFuncResult
		 * result = executeFunc(scd, 2, requestParams, (short)4); // user forgot
		 * pass if(result.isSuccess() &&
		 * !GenericUtil.isEmpty(result.getResultMap()) &&
		 * (GenericUtil.uInt(result.getResultMap().get("pout_user_id"))!=0)){
		 * res.put("success", "1"); //tanımlanmış mail varsa mail-sms
		 * gönderiliyor try{ W5FormSmsMail fsm =
		 * (W5FormSmsMail)dao.getCustomizedObject(
		 * "from W5FormSmsMail t where t.activeFlag=1 and t.formId=? AND t.customizationId=?"
		 * , 576, (Integer)scd.get("customizationId"), null); if (fsm!=null){
		 * requestParams.put("table_id","336"); sendFormSmsMail(scd,
		 * fsm.getFormSmsMailId(), requestParams); res.put("msg",
		 * "email_success"); }else{ res.put("msg",
		 * "mail_sms_setting_not_found"); } }catch(Exception e){
		 * if(FrameworkSetting.debug)e.printStackTrace(); res.put("msg",
		 * FrameworkSetting.debug? e.getMessage() : "mail_sending_error");
		 * return res; } }else{ res.put("msg", "forgot_pass_no_such_user"); }
		 * }catch(Exception e){ if(FrameworkSetting.debug)e.printStackTrace();
		 * res.put("msg", FrameworkSetting.debug? e.getMessage() : "error");
		 * return res; } }catch(Exception e){
		 * if(FrameworkSetting.debug)e.printStackTrace(); } return res;
		 */
	}

	public void sendSms(int customizationId, int userId, String phoneNumber, String message, int tableId, int tablePk) {
		Map<String, String> smsMap = new HashMap<String, String>();
		smsMap.put("customizationId", customizationId + "");
		smsMap.put("userId", userId + "");
		smsMap.put("tableId", tableId + "");
		smsMap.put("tablePk", tablePk + "");
		smsMap.put("phoneNumber", phoneNumber);
		smsMap.put("message", message);

		// messageSender.sendMessage("SEND_SMS","BMPADAPTER", smsMap);

	}

	public W5FormCellHelper reloadFormCell(Map<String, Object> scd, int fcId, String webPageId, String tabId) {
		return uiEngine.reloadFormCell(scd, fcId, webPageId, tabId);
	}

	public int notifyChatMsgRead(Map<String, Object> scd, int userId, int chatId) {
		int cnt1 = dao.executeUpdateSQLQuery(
				"update iwb.w5_chat set DELIVER_STATUS_TIP=2, DELIVER_DTTM=iwb.fnc_sysdate(?) where RECEIVER_USER_ID=? AND SENDER_USER_ID=? AND DELIVER_STATUS_TIP in (0,1)",
				scd.get("customizationId"), scd.get("userId"), userId);
		return 0;
	}

	public W5FormResult postBulkConversionMulti(Map<String, Object> scd, int conversionCount,
			Map<String, String> parameterMap) {
		return conversionEngine.postBulkConversionMulti(scd, conversionCount, parameterMap);
	}

	public Map sendFormSmsMail(Map<String, Object> scd, int formSmsMailId, Map<String, String> requestParams) {
		return notificationEngine.sendFormSmsMail(scd, formSmsMailId, requestParams);
	}

	public W5TutorialResult getTutorialResult(Map<String, Object> scd, int tutorialId,
			Map<String, String> requestParams) { // TODO

		W5Tutorial tutorial = (W5Tutorial) dao.getObject(W5Tutorial.class, tutorialId);
		if (!FrameworkCache.roleAccessControl(scd, 0))
			throw new IWBException("security", "Module", tutorial.getModuleId(), null,
					LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_modul_kontrol"), null);

		W5TutorialResult tr = new W5TutorialResult();
		/*
		 * tr.setTutorial(tutorial); tr.setScd(scd);
		 * tr.setRequestParams(requestParams); List<Object> listOfDoneTutorials
		 * = dao.executeSQLQuery(
		 * "select u.tutorial_id from iwb.w5_tutorial_user u where u.user_id=? AND u.customization_id=? AND u.FINISHED_FLAG=1"
		 * , scd.get("userId"),scd.get("customizationId"));
		 * tr.setDoneTutorials(new HashSet<Integer>());
		 * if(listOfDoneTutorials!=null)for(Object o:listOfDoneTutorials){
		 * tr.getDoneTutorials().add(GenericUtil.uInt(o)); } List<Object>
		 * listOfTutorial = dao.executeSQLQuery(
		 * "select u.FINISHED_FLAG from iwb.w5_tutorial_user u where u.user_id=? AND u.tutorial_id=? AND u.customization_id=?"
		 * , scd.get("userId"),tutorialId, scd.get("customizationId"));
		 * if(GenericUtil.isEmpty(listOfTutorial)){//0:daha kayit yok, 1. var ve
		 * bitmemmis, 2. var ve bitmis tr.setTutorialUserStatus((short)0); }
		 * else tr.setTutorialUserStatus((short)(1 +
		 * GenericUtil.uInt(listOfTutorial.get(0))));
		 * if(!GenericUtil.isEmpty(tutorial.getRecommendedTutorialIds()))
		 * tr.setRecommendedTutorialList(dao.find(
		 * "from W5Tutorial t where t.tutorialId in ("
		 * +tutorial.getRecommendedTutorialIds()+")"));
		 * tutorial.set_renderTemplate((W5Page)dao.getCustomizedObject(
		 * "from W5Template t where t.templateId=? AND t.customizationId=?",
		 * tutorial.getRenderTemplateId(), 0, null));
		 */
		return tr;
	}


	public boolean changeActiveProject(Map<String, Object> scd, String projectUuid) {
		List<Object> params = new ArrayList();
		params.add(projectUuid);
		params.add(scd.get(scd.containsKey("ocustomizationId") ? "ocustomizationId" : "customizationId"));
		params.add(scd.get("userId"));
		List list = dao.executeSQLQuery2Map(
				"select x.customization_id,(select 1 from iwb.w5_query q where q.query_id=session_query_id AND x.project_uuid=q.project_uuid) rbac from iwb.w5_project x where x.project_uuid=? AND (x.customization_id=? OR exists(select 1 from iwb.w5_user_related_project ur where ur.user_id=? AND x.project_uuid=ur.related_project_uuid))",
				params);
		if (GenericUtil.isEmpty(list))
			return false;
		Map p = (Map) list.get(0);
		int newCustomizationId = GenericUtil.uInt(p.get("customization_id"));

		if (newCustomizationId != (Integer) scd.get("customizationId")) { // TODO
																			// check
																			// for
																			// invited
																			// projects
			if (!scd.containsKey("ocustomizationId"))
				scd.put("ocustomizationId", (Integer) scd.get("customizationId"));
			scd.put("customizationId", newCustomizationId);
		}
		W5Project po = FrameworkCache.getProject(projectUuid);
		scd.put("projectId", projectUuid);
		scd.put("rbac", newCustomizationId > 0 ? GenericUtil.uInt(p.get("rbac")) : 1);
		scd.put("_renderer2", GenericUtil.getRenderer(po.getUiWebFrontendTip()));
		return true;
	}

	public int getGlobalNextval(String id, String projectUuid, int userId, int customizationId, String remoteAddr) {

		if (FrameworkSetting.log2tsdb) {
			LogUtil.logObject(new Log5GlobalNextval(userId, customizationId, id, remoteAddr, projectUuid));
		}

		/*
		 * l = dao.executeSQLQuery(
		 * "select 1 from iwb.w5_vcs_global_nextval u where u.seq_dsc=? AND u.active_flag=1"
		 * , id); if(false && GenericUtil.isEmpty(l))//TODO. hepsi tanimlaninca
		 * daha iyi olacak throw new IWBException("framework",
		 * "Wrong/Inactive Sequence Name for GlobalNextval", 0, id, id, null);
		 */
		List l = dao.executeSQLQuery("select nextval('" + id + "')");

		return GenericUtil.uInt(l.get(0));
	}

	public boolean organizeTableFields(Map<String, Object> scd, String tableName) {
		boolean b = dao.organizeTable(scd, tableName);
		FrameworkCache.clearPreloadCache(scd);
		return b;
	}

	public void organizeQueryFields(Map<String, Object> scd, int queryId, short insertFlag) {
		dao.checkTenant(scd);
		dao.organizeQueryFields(scd, queryId, insertFlag);
		FrameworkCache.clearPreloadCache(scd);
	}

	public boolean organizeDbFuncParams(Map<String, Object> scd, String dbFuncName) {
		boolean b = dao.organizeGlobalFunc(scd, dbFuncName);
		FrameworkCache.clearPreloadCache(scd);
		return b;
	}

	public int buildForm(Map<String, Object> scd, String parameter) throws JSONException {
		int customizationId = (Integer) scd.get("customizationId");

		String projectUuid = (String) scd.get("projectId");
		W5Project po = FrameworkCache.getProject(projectUuid);
		int userId = (Integer) scd.get("userId");
		// boolean vcs = FrameworkSetting.vcs && po.getVcsFlag()!=0;
		String createTableSql = "", tableName, fullTableName;
		JSONObject main;
		JSONArray detail;
		int parentTableId;
		boolean vcs = true;
		Locale en = new Locale("en");

		List p = new ArrayList();
		p.add(customizationId);

		JSONObject json = new JSONObject(parameter);
		String webPageId = json.has("_webPageId") ? json.getString("_webPageId") : null;
		int userTip = json.getInt("user_tip");
		main = json.getJSONObject("main");
		detail = json.getJSONArray("detail");

		StringBuilder s = new StringBuilder();
		String schema = po.getRdbmsSchema();
		if (GenericUtil.isEmpty(schema))
			schema = "";
		else
			schema += ".";
		String formName = main.getString("form_name");
		tableName = GenericUtil.uStr2Alpha2(GenericUtil.uStr2English(formName), "x").toLowerCase(en);
		String gridName = GenericUtil.uStr2Alpha2(GenericUtil.uStr2English(main.getString("grid_name")), "x")
				.toLowerCase(en);
		// gridName = main.getString("grid_name");
		String tablePrefix = FrameworkCache.getAppSettingStringValue(0, "form_builder_table_prefix", "x");
		if (!tablePrefix.endsWith("_"))
			tablePrefix += "_";
		String tableName2 = tablePrefix + tableName;
		fullTableName = schema + tableName2;
		parentTableId = main.has("parent_table_id") ? GenericUtil.uInt(main.get("parent_table_id")) : 0;
		s.append("create table ").append(tableName2).append(" (");
		s.append(tableName).append("_id integer not null");
		String relParentFieldName = null;
		if (parentTableId != 0) {
			W5Table pt = FrameworkCache.getTable(scd, parentTableId);
			if (pt != null) {
				relParentFieldName = pt.get_tableFieldList().get(0).getDsc();
				s.append(",\n ").append(relParentFieldName).append(" integer not null");
			} else
				parentTableId = 0;
		}
		for (int qi = 0; qi < detail.length(); qi++) {
			JSONObject d = detail.getJSONObject(qi);
			int controlTip = GenericUtil.uInt(d.get("real_control_tip"));
			if (controlTip == 102)
				continue;
			String fieldDsc = d.getString("real_dsc");
			if (GenericUtil.isEmpty(fieldDsc))
				fieldDsc = d.getString("dsc");
			fieldDsc = fieldDsc.toLowerCase();
			s.append(",\n ").append(fieldDsc);
			int maxLen = GenericUtil.uInt(d.get("max_length"));
			switch (controlTip) {
			case 2:
				if (!fieldDsc.endsWith("_dt")) {
					s.append("_dt");
					fieldDsc = fieldDsc + "_dt";
				}
				s.append(" date");
				break; // date
			case 3:
				s.append(" numeric");
				if (maxLen > 0) {
					if (maxLen > 22)
						maxLen = 22;
					s.append("(").append(maxLen);
					int decimalPrecision = d.has("decimal_precision") ? GenericUtil.uInt(d.get("decimal_precision"))
							: 0;
					if (decimalPrecision > 18)
						decimalPrecision = 18;
					s.append(",").append(decimalPrecision > 0 ? decimalPrecision : 2);
					s.append(")");
				}
				break; // float
			case 4:
				s.append(maxLen < 5 ? " smallint" : " integer");
				break; // integer
			case 5:
				if (!fieldDsc.endsWith("_flag")) {
					s.append("_flag");
					fieldDsc = fieldDsc + "_flag";
				}
				s.append(" smallint default 0");
				break; // checkbox
			case 6:
			case 8:
			case 58:
				if (GenericUtil.uInt(d.get("look_up_id")) > 0) {
					int lookUpId = GenericUtil.uInt(d.get("look_up_id"));
					if (FrameworkCache.getLookUp(scd, lookUpId) == null)
						throw new IWBException("framework", "Form+ Builder", lookUpId, null,
								"Wrong Static LookupID: " + lookUpId, null);
				} else {
					if (!d.has("list_of_values") || GenericUtil.isEmpty(d.get("list_of_values")))
						throw new IWBException("framework", "Form+ Builder", 0, null,
								"LookupID OR Combo Values Not Defined", null);
					String lov = d.getString("list_of_values");
					String[] vz = lov.split("\\r?\\n");

					int lookUpId = GenericUtil.getGlobalNextval("iwb.seq_look_up", projectUuid, userId,
							customizationId);
					dao.executeUpdateSQLQuery(
							"insert into iwb.w5_look_up "
									+ "(look_up_id, customization_id, dsc, version_no, insert_user_id, insert_dttm, version_user_id, version_dttm, project_uuid, oproject_uuid)"
									+ "values (?         , ?               , ?  , 1         , ?             , current_timestamp    , ?              , current_timestamp     , ?, ?)",
							lookUpId, scd.get("customizationId"), "lkp_" + fieldDsc, scd.get("userId"),
							scd.get("userId"), projectUuid, projectUuid);
					if (vcs)
						dao.saveObject(new W5VcsObject(scd, 13, lookUpId));
					int tabOrder = 1;
					for (String sx : vz)
						if (!GenericUtil.isEmpty(sx) && !GenericUtil.isEmpty(sx.trim())) {
							int lookUpIdDetail = GenericUtil.getGlobalNextval("iwb.seq_look_up_detay", projectUuid,
									userId, customizationId);
							dao.executeUpdateSQLQuery(
									"insert into iwb.w5_look_up_detay "
											+ "(look_up_detay_id, look_up_id, tab_order, val      , dsc, version_no, insert_user_id, insert_dttm, version_user_id, version_dttm, customization_id, project_uuid, oproject_uuid)"
											+ "values (?,        ?,                 ?        , ?        , ?  , 1         , ?             , current_timestamp    , ?              , current_timestamp     , ?, ?, ?)",
									lookUpIdDetail, lookUpId, tabOrder, "" + tabOrder, sx.trim(), scd.get("userId"),
									scd.get("userId"), scd.get("customizationId"), projectUuid, projectUuid);
							tabOrder++;
							if (vcs)
								dao.saveObject(new W5VcsObject(scd, 14, lookUpIdDetail));
						}
					d.put("look_up_id", "" + lookUpId);
				}
				s.append(controlTip == 6 || controlTip == 7 || controlTip == 10 || controlTip == 51 ? " integer"
						: " character varying(256)");
				break;
			case 7:
			case 10:
			case 15:
			case 59:
				if (GenericUtil.uInt(d.get("look_up_id")) > 0) {
					int queryId = GenericUtil.uInt(d.get("look_up_id"));
					if (dao.executeSQLQuery("select 1 from iwb.w5_query x where x.query_id=? AND x.query_tip=3",
							queryId) == null)
						throw new IWBException("framework", "Form+ Builder", queryId, null, "Wrong QueryID: " + queryId,
								null);
				} else
					throw new IWBException("framework", "Form+ Builder", 0, null, "QueryID not defined", null);
				s.append(controlTip == 6 || controlTip == 7 || controlTip == 10 || controlTip == 51 ? " integer"
						: " character varying(256)");
				break;

			default:
				// case 1:case 11:case 12:
				if (maxLen == 0 || maxLen > 3999)
					s.append(" text");
				else
					s.append(" character varying(").append(maxLen < 1024 ? 1024 : maxLen).append(")");
				break; // string, textarea, htmleditor
			}

			/*
			 * Object notNull = d.get("not_null_flag"); if(false &&
			 * notNull!=null){ //sonra degistirmek isteyebilir, o yzden koyma
			 * if(notNull instanceof Boolean){ if((Boolean)notNull)s.append(
			 * " not null"); } else if(GenericUtil.uInt(notNull)!=0)s.append(
			 * " not null"); }
			 */
			d.put("real_dsc", fieldDsc);
		}
		s.append(",\n version_no integer NOT NULL DEFAULT 1").append(",\n insert_user_id integer NOT NULL DEFAULT 1")
				.append(",\n  insert_dttm timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp without time zone")
				.append(",\n version_user_id integer NOT NULL DEFAULT 1,\n version_dttm timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp without time zone");

		s.append(",\n CONSTRAINT pk_").append(tableName).append(" PRIMARY KEY (").append(tableName).append("_id)");
		s.append(")");

		createTableSql = s.toString();
		dao.executeUpdateSQLQuery("set search_path=" + po.getRdbmsSchema());

		Map msg = null, nt = null;
		if (webPageId != null) {
			msg = new HashMap();
			msg.put("success", true);
			nt = new HashMap();
			msg.put("notification", nt);
		}

		try {
			dao.executeUpdateSQLQuery(createTableSql);
			String createSeqSql = "create sequence seq_" + tablePrefix + tableName;
			dao.executeUpdateSQLQuery(createSeqSql);

			if (vcs) {
				W5VcsCommit commit = new W5VcsCommit();
				commit.setCommitTip((short) 2);
				commit.setExtraSql(createTableSql + ";\n\n" + createSeqSql + ";");
				commit.setProjectUuid(projectUuid);
				commit.setComment("iWB. AutoCreate Scripts for Table: " + fullTableName);
				commit.setCommitUserId((Integer) scd.get("userId"));
				Object oi = dao.executeSQLQuery("select nextval('iwb.seq_vcs_commit')").get(0);
				commit.setVcsCommitId(-GenericUtil.uInt(oi));
				dao.saveObject(commit);
			}

			s.setLength(0);

			if (webPageId != null) {
				nt.put("_tmpStr", "Table Created on RDBMS");
				UserUtil.broadCast(projectUuid, (Integer) scd.get("userId"), (String) scd.get("sessionId"), webPageId,
						msg);
			}

		} catch (Exception e2) {
			throw new IWBException("framework", "Create Table&Seq", 0, createTableSql, e2.getMessage(), e2);
		}

		boolean b = dao.organizeTable(scd, fullTableName);
		if (!b)
			throw new IWBException("framework", "Define Table", 0, parameter, "Define Table", null);
		if (webPageId != null) {
			nt.put("_tmpStr", "Table Imported to iCodeBetter");
			UserUtil.broadCast(projectUuid, (Integer) scd.get("userId"), (String) scd.get("sessionId"), webPageId, msg);
		}

		int tableId = GenericUtil.uInt(dao.executeSQLQuery(
				"select t.table_id from iwb.w5_table t where t.customization_id=? AND t.dsc=? AND t.project_uuid=?",
				customizationId, tableName2, projectUuid).get(0));

		try {
			main.put("table_id", tableId);
		} catch (JSONException e) {
		}
		// dao.executeUpdateSQLQuery("supdate iwb.w5_table t set where
		// t.customization_id=? AND
		// t.table_id=?", customizationId, tableId);
		main.put("form_name", tableName);

		W5FormResult fr = postFormAsJson(scd, 181, 2, main, 182, detail);
		if (!fr.getErrorMap().isEmpty())
			throw new IWBException("framework", "Save FormBuilder Data", 0, parameter,
					GenericUtil.fromMapToJsonString(fr.getErrorMap()), null);

		int xformBuilderId = GenericUtil.uInt(fr.getOutputFields().get("xform_builder_id").toString());
		int parentTemplateId = parentTableId == 0 || !main.has("template_id") ? 0 : main.getInt("template_id");
		int parentTemplateObjectId = parentTableId == 0 || !main.has("parent_object_id") ? 0
				: main.getInt("parent_object_id");
		int formId = GenericUtil.getGlobalNextval("iwb.seq_form", projectUuid, userId, customizationId); // 1000000+GenericUtil.uInt(dao.executeSQLQuery("select
		// nextval('seq_form')").get(0));
		// XFORM_ID := nextval('seq_form');
		dao.executeUpdateSQLQuery(
				"INSERT INTO iwb.w5_form(" + "form_id, customization_id, object_tip, object_id, dsc, locale_msg_key, "
						+ "default_width, default_height, tab_order, render_tip, code, label_width,"
						+ "label_align_tip,  cont_entry_flag, "
						+ "version_no, insert_user_id, insert_dttm, version_user_id,"
						+ "version_dttm, render_template_id, project_uuid, oproject_uuid)"
						+ "\nselect ?, XFORM_BUILDER.customization_id, 2, XFORM_BUILDER.table_id, 'frm_'||XFORM_BUILDER.form_name, ? ,"
						+ "400, 300, 1, 1, null, XFORM_BUILDER.label_width," + "XFORM_BUILDER.label_align, 0,"
						+ "1, ?, current_timestamp, ?,"
						+ "current_timestamp, 0, XFORM_BUILDER.project_uuid,XFORM_BUILDER.project_uuid from iwb.w5_xform_builder XFORM_BUILDER where XFORM_BUILDER.xform_builder_id=? AND XFORM_BUILDER.customization_id=?",
				formId, formName, userId, userId, xformBuilderId, customizationId);
		if (vcs)
			dao.saveObject(new W5VcsObject(scd, 40, formId));

		List lp = new ArrayList();
		lp.add(xformBuilderId);
		List<Map> lm = dao.executeSQLQuery2Map(
				"select x.* from iwb.w5_xform_builder_detail x where x.xform_builder_id=? order by 1", lp);
		int tabOrder = 1;
		for (Map m : lm) {
			int formCellId = GenericUtil.getGlobalNextval("iwb.seq_form_cell", projectUuid, userId, customizationId); // 1000000+GenericUtil.uInt(dao.executeSQLQuery("select
			// nextval('seq_form_cell')").get(0));
			dao.executeUpdateSQLQuery(
					"INSERT INTO iwb.w5_form_cell(" + "form_cell_id, customization_id, form_id, dsc, locale_msg_key,"
							+ "control_tip, vtype, source_tip, not_null_flag, tab_order, control_width,"
							+ "max_length, nrd_tip, lookup_query_id, lookup_included_params,"
							+ "lookup_included_values, default_value, initial_value, initial_source_tip,"
							+ "extra_definition, object_id, object_detail_id, version_no, insert_user_id,"
							+ "insert_dttm, version_user_id, version_dttm, form_module_id, out_flag,"
							+ "dialog_grid_id, x_order, parent_form_cell_id, active_flag, lookup_edit_form_id,"
							+ "project_uuid, oproject_uuid)"
							+ "\nselect  ?, x.customization_id, ?, coalesce(x.real_dsc, x.dsc), x.label,"
							+ "case when x.real_control_tip!=0 then x.real_control_tip else  x.control_tip end, null, 1, x.not_null_flag, x.tab_order, x.width,"
							+ " 0, 0, x.look_up_id, null, " + " null, null, x.initial_value, 0,"
							+ " null, ?, (select f.table_field_id from iwb.w5_table_field f where f.customization_id=x.customization_id AND f.table_id=? AND f.dsc=coalesce(x.real_dsc, x.dsc)), 1, ?,"
							+ " current_timestamp, ?, current_timestamp, 0, 0," + " 0, 1, 0, 1, 0,"
							+ " x.project_uuid,x.project_uuid from iwb.w5_xform_builder_detail x where x.xform_builder_detail_id=? AND x.customization_id=?",
					formCellId, formId, tableId, tableId, userId, userId,
					GenericUtil.uInt(m.get("xform_builder_detail_id")), customizationId);
			if (vcs)
				dao.saveObject(new W5VcsObject(scd, 41, formCellId));
		}
		if (relParentFieldName != null) {
			int formCellId = GenericUtil.getGlobalNextval("iwb.seq_form_cell", projectUuid, userId, customizationId); // 1000000+GenericUtil.uInt(dao.executeSQLQuery("select
			// nextval('seq_form_cell')").get(0));
			dao.executeUpdateSQLQuery(
					"INSERT INTO iwb.w5_form_cell(" + "form_cell_id, customization_id, form_id, dsc, locale_msg_key,"
							+ "control_tip, vtype, source_tip, not_null_flag, tab_order, control_width,"
							+ "max_length, nrd_tip, lookup_query_id, lookup_included_params,"
							+ "lookup_included_values, default_value, initial_value, initial_source_tip,"
							+ "extra_definition, object_id, object_detail_id, version_no, insert_user_id,"
							+ "insert_dttm, version_user_id, version_dttm, form_module_id, out_flag,"
							+ "dialog_grid_id, x_order, parent_form_cell_id, active_flag, lookup_edit_form_id,"
							+ "project_uuid, oproject_uuid)" + "\nvalues(?, ?, ?, ?, ? ," + "0, null, 1, 1, 10*?, 100,"
							+ " 0, 0, 0, null, " + " null, null, null, 0,"
							+ " null, ?, (select f.table_field_id from iwb.w5_table_field f where f.customization_id=? AND f.table_id=? AND f.dsc=?), 1, ?,"
							+ " current_timestamp, ?, current_timestamp, 0, 0," + " 0, 1, 0, 1, 0," + " ?, ? )",
					formCellId, customizationId, formId, relParentFieldName, relParentFieldName, tabOrder++, tableId,
					customizationId, tableId, relParentFieldName, userId, userId, projectUuid, projectUuid);
			if (vcs)
				dao.saveObject(new W5VcsObject(scd, 41, formCellId));
		}
		if (webPageId != null) {
			nt.put("_tmpStr", "Form Created for CRUD Operations");
			UserUtil.broadCast(projectUuid, (Integer) scd.get("userId"), (String) scd.get("sessionId"), webPageId, msg);
		}

		// XQUERY_ID := nextval('seq_query');
		int queryId = GenericUtil.getGlobalNextval("iwb.seq_query", projectUuid, userId, customizationId); // 1000000+GenericUtil.uInt(dao.executeSQLQuery("select
		// nextval('seq_query')").get(0));
		dao.executeUpdateSQLQuery(
				"INSERT INTO iwb.w5_query(" + "query_id, dsc, main_table_id, sql_select, sql_from, sql_where,"
						+ "sql_groupby, sql_orderby, query_tip, log_level_tip, version_no,"
						+ "insert_user_id, insert_dttm, version_user_id, version_dttm,"
						+ "show_parent_record_flag, sql_post_select,"
						+ "data_fill_direction_tip, opt_query_field_ids, opt_tip, project_uuid,oproject_uuid, customization_id)"
						+ "select ?, 'qry_'||XFORM_BUILDER.form_name||'1', XFORM_BUILDER.table_id, 'x.*', (select t.dsc from iwb.w5_table t where t.table_id=XFORM_BUILDER.table_id AND t.customization_id=?)||' x', null,"
						+ "null, 1, 1, 1, 1," + "?, current_timestamp, ?, current_timestamp, " + "0,  null,"
						+ "0, null, 0, XFORM_BUILDER.project_uuid,XFORM_BUILDER.project_uuid, XFORM_BUILDER.customization_id from iwb.w5_xform_builder XFORM_BUILDER where XFORM_BUILDER.xform_builder_id=?",
				queryId, customizationId, userId, userId, xformBuilderId);
		if (vcs)
			dao.saveObject(new W5VcsObject(scd, 8, queryId));

		dao.organizeQueryFields(scd, queryId, (short) 1);

		if (webPageId != null) {
			nt.put("_tmpStr", "Query Created");
			UserUtil.broadCast(projectUuid, (Integer) scd.get("userId"), (String) scd.get("sessionId"), webPageId, msg);
		}

		dao.executeUpdateSQLQuery("set search_path=iwb");

		List llo = dao.find("from W5QueryFieldCreation t where queryFieldId<?", 500);
		// dao.getHibernateTemplate().flush();
		// XGRID_ID := nextval('seq_grid');
		// List lw = dao.executeSQLQuery("select min(qf.query_field_id) from
		// iwb.w5_query_field qf
		// where qf.query_id=? AND qf.customization_id=? AND qf.project_uuid=?",
		// queryId,
		// customizationId, projectUuid);
		int gridId = GenericUtil.getGlobalNextval("iwb.seq_grid", projectUuid, userId, customizationId); // 1000000+GenericUtil.uInt(dao.executeSQLQuery("select
		// nextval('seq_grid')").get(0));
		dao.executeUpdateSQLQuery("INSERT INTO iwb.w5_grid("
				+ "grid_id, customization_id, dsc, query_id, locale_msg_key, grid_tip,"
				+ "default_page_record_number, selection_mode_tip, pk_query_field_id," + "auto_expand_field_id, "
				+ "default_width, default_height, version_no, insert_user_id, insert_dttm,"
				+ "version_user_id, version_dttm, default_sql_order_by, default_crud_form_id,"
				+ "column_render_tip, grouping_field_id, " + "insert_edit_mode_flag, move_up_down_flag,"
				+ "tree_master_field_id, summary_tip, row_color_fx_tip, row_color_fx_query_field_id,"
				+ "row_color_fx_render_tip, row_color_fx_render_field_ids, code, project_uuid, oproject_uuid)"
				+ "select ?, XFORM_BUILDER.customization_id, ? , ?, XFORM_BUILDER.grid_name, 0,"
				+ "?, 1, (select min(qf.query_field_id) from iwb.w5_query_field qf where qf.query_id=?)," + "0, "
				+ "400, 300, 1, ?, current_timestamp," + "?, current_timestamp, null, ?," + "0, 0, " + "0, 0,"
				+ "0, 0, 0, 0," + "0, null, null, XFORM_BUILDER.project_uuid,XFORM_BUILDER.project_uuid "
				+ " from iwb.w5_xform_builder XFORM_BUILDER where XFORM_BUILDER.xform_builder_id=? AND XFORM_BUILDER.customization_id=?",
				gridId, "grd_" + gridName, queryId, parentTableId == 0 ? 20 : 0, queryId, userId, userId, formId,
				xformBuilderId, customizationId);
		if (vcs)
			dao.saveObject(new W5VcsObject(scd, 5, gridId));

		tabOrder = 1;
		for (Map m : lm) {
			int gridColumnId = GenericUtil.getGlobalNextval("iwb.seq_grid_column", projectUuid, userId,
					customizationId); // 1000000+GenericUtil.uInt(dao.executeSQLQuery("select
			// nextval('seq_grid_column')").get(0));
			dao.executeUpdateSQLQuery(
					"INSERT INTO iwb.w5_grid_column("
							+ "grid_column_id, query_field_id, grid_id, customization_id, locale_msg_key, tab_order,"
							+ "visible_flag, sortable_flag, width, renderer, align_tip, version_no,"
							+ "insert_user_id, insert_dttm, version_user_id, version_dttm, extra_definition,"
							+ "grid_module_id, form_cell_id, filter_flag, project_uuid, oproject_uuid)"
							+ "\nselect ?, (select f.query_field_id from iwb.w5_query_field f where f.dsc=coalesce(x.real_dsc, x.dsc) AND f.query_id=?), ?, x.customization_id, coalesce(x.grd_label, x.label), 10*?,"
							+ "x.grd_visible_flag, 1, x.grd_width, null, x.grd_align_tip, 1,"
							+ "?, current_timestamp, ?, current_timestamp, null,"
							+ "0, case when y.grid_edit=1 AND x.grd_editable_flag=1 then (select c.form_cell_id from iwb.w5_form_cell c where c.dsc=coalesce(x.real_dsc, x.dsc) AND c.form_id=? AND c.customization_id=x.customization_id) else 0 end, 0, x.project_uuid, x.project_uuid"
							+ " from iwb.w5_xform_builder_detail x,iwb.w5_xform_builder y "
							+ "where x.xform_builder_id = y.xform_builder_id AND x.customization_id=y.customization_id "
							+ "AND x.xform_builder_detail_id=?  AND x.customization_id=?",
					gridColumnId, queryId, gridId, tabOrder++, userId, userId, formId,
					GenericUtil.uInt(m.get("xform_builder_detail_id")), customizationId);
			if (vcs)
				dao.saveObject(new W5VcsObject(scd, 4, gridColumnId));
		}

		if (webPageId != null) {
			nt.put("_tmpStr", "Grid Created");
			UserUtil.broadCast(projectUuid, (Integer) scd.get("userId"), (String) scd.get("sessionId"), webPageId, msg);
		}

		// if(pmaster_flag=1 AND XFORM_BUILDER.grid_search=1 AND (select
		// count(1) from
		// iwb.w5_xform_builder_detail x where
		// x.xform_builder_id=pxform_builder_id AND
		// x.customization_id=XUSER_ROLE.customization_id AND
		// x.project_uuid=pproject_uuid AND
		// x.grd_search_flag=1)>0) then
		if (parentTableId == 0) {
			tabOrder = 1;
			for (Map m : lm)
				if (GenericUtil.uInt(m.get("grd_search_flag")) != 0) {
					int queryParamId = GenericUtil.getGlobalNextval("iwb.seq_query_param", projectUuid, userId,
							customizationId); // 1000000+GenericUtil.uInt(dao.executeSQLQuery("select
					// nextval('seq_query_param')").get(0));
					dao.executeUpdateSQLQuery(
							"INSERT INTO iwb.w5_query_param("
									+ "query_param_id, query_id, dsc, param_tip, expression_dsc, operator_tip,"
									+ "not_null_flag, tab_order, source_tip, default_value, min_length,"
									+ "max_length, version_no, insert_user_id, insert_dttm, version_user_id,"
									+ "version_dttm, related_table_field_id, min_value, max_value, project_uuid, oproject_uuid, customization_id)"
									+ "SELECT  ?, ?, 'x'||coalesce(x.real_dsc, x.dsc), case when x.control_tip in (1,2,3,4) then x.control_tip else 1 end, 'x.'||coalesce(x.real_dsc, x.dsc), 0,"
									+ "0, 10*?, 1, null, 0," + "0, 1, ?, current_timestamp, ?,"
									+ "current_timestamp, (select f.table_field_id from iwb.w5_table_field f where f.dsc=coalesce(x.real_dsc, x.dsc) AND f.table_id=? AND f.customization_id=x.customization_id), null, null, x.project_uuid, x.project_uuid, x.customization_id "
									+ "from iwb.w5_xform_builder_detail x where "
									+ "x.xform_builder_detail_id=? AND x.customization_id=?",
							queryParamId, queryId, tabOrder++, userId, userId, tableId,
							GenericUtil.uInt(m.get("xform_builder_detail_id")), customizationId);
					if (vcs)
						dao.saveObject(new W5VcsObject(scd, 10, queryParamId));
				}

			// XSFORM_ID := nextval('seq_form');
			int sformId = GenericUtil.getGlobalNextval("iwb.seq_form", projectUuid, userId, customizationId); // 1000000+GenericUtil.uInt(dao.executeSQLQuery("select
			// nextval('seq_form')").get(0));
			dao.executeUpdateSQLQuery("INSERT INTO iwb.w5_form("
					+ "form_id, customization_id, object_tip, object_id, dsc, locale_msg_key, "
					+ "default_width, default_height, tab_order, render_tip, code, label_width,"
					+ "label_align_tip, cont_entry_flag, " + "version_no, insert_user_id, insert_dttm, version_user_id,"
					+ "version_dttm, render_template_id, project_uuid, oproject_uuid)"
					+ "\nselect ?, XFORM_BUILDER.customization_id, 1, ?, 'sfrm_'||XFORM_BUILDER.form_name, 'search_criteria',"
					+ "400, 300, 1, 1, null, XFORM_BUILDER.label_width," + "XFORM_BUILDER.label_align, 0,"
					+ "1, ?, current_timestamp, ?, current_timestamp, 0, XFORM_BUILDER.project_uuid, XFORM_BUILDER.project_uuid from iwb.w5_xform_builder XFORM_BUILDER where XFORM_BUILDER.xform_builder_id=? AND XFORM_BUILDER.customization_id=?",
					sformId, gridId, userId, userId, xformBuilderId, customizationId);
			if (vcs)
				dao.saveObject(new W5VcsObject(scd, 40, sformId));

			for (Map m : lm)
				if (GenericUtil.uInt(m.get("grd_search_flag")) != 0) {
					int formCellId = GenericUtil.getGlobalNextval("iwb.seq_form_cell", projectUuid, userId,
							customizationId); // 1000000+GenericUtil.uInt(dao.executeSQLQuery("select
					// nextval('seq_form_cell')").get(0));
					int controlTip = GenericUtil.uInt(m.get("real_control_tip"));
					if (controlTip == 0)
						controlTip = GenericUtil.uInt(m.get("control_tip"));
					int lookUpId = GenericUtil.uInt(m.get("look_up_id"));
					switch (controlTip) {
					case 5:
						controlTip = 6;
						lookUpId = 143;
						break;
					case 11:
					case 12:
						controlTip = 1;
						break;
					}
					dao.executeUpdateSQLQuery(
							"INSERT INTO iwb.w5_form_cell("
									+ "form_cell_id, customization_id, form_id, dsc, locale_msg_key,"
									+ "control_tip, vtype, source_tip, not_null_flag, tab_order, control_width,"
									+ "max_length, nrd_tip, lookup_query_id, lookup_included_params,"
									+ "lookup_included_values, default_value, initial_value, initial_source_tip,"
									+ "extra_definition, object_id, object_detail_id, version_no, insert_user_id,"
									+ "insert_dttm, version_user_id, version_dttm, form_module_id, out_flag,"
									+ "dialog_grid_id, x_order, parent_form_cell_id, active_flag, lookup_edit_form_id, project_uuid, oproject_uuid)"
									+ "\nselect  ?, x.customization_id, ?, 'x'||coalesce(x.real_dsc, x.dsc), x.label,"
									+ "?, null, 1, 0, 10*?, 200," + "0, 0, ?, null," + "null, null, null, 0,"
									+ "null, ?, (select f.query_param_id from iwb.w5_query_param f where f.query_id=? AND f.dsc='x'||coalesce(x.real_dsc, x.dsc)), 1, ?,"
									+ "current_timestamp, ?, current_timestamp, 0, 0,"
									+ "0, 1, 0, 1, 0, x.project_uuid, x.project_uuid "
									+ "from iwb.w5_xform_builder_detail x where x.grd_search_flag=1 AND x.xform_builder_detail_id=? AND x.customization_id=?",
							formCellId, sformId, controlTip, tabOrder++, lookUpId, gridId, queryId, userId, userId,
							GenericUtil.uInt(m.get("xform_builder_detail_id")), customizationId);
					if (vcs)
						dao.saveObject(new W5VcsObject(scd, 41, formCellId));
				}

			if (webPageId != null) {
				nt.put("_tmpStr", "Form Created for Grid Search");
				UserUtil.broadCast(projectUuid, (Integer) scd.get("userId"), (String) scd.get("sessionId"), webPageId,
						msg);
			}
		}

		// if(pmaster_flag=1)then
		int templateId = 0, menuId = 0;
		if (parentTableId == 0) {
			// XTEMPLATE_ID := nextval('seq_template');
			templateId = GenericUtil.getGlobalNextval("iwb.seq_template", projectUuid, userId, customizationId); // 1000000+GenericUtil.uInt(dao.executeSQLQuery("select
			// nextval('seq_template')").get(0));
			dao.executeUpdateSQLQuery(
					"INSERT INTO iwb.w5_template(" + "template_id, customization_id, template_tip, dsc, object_id,"
							+ "object_tip, code, version_no, insert_user_id, insert_dttm, version_user_id,"
							+ "version_dttm, locale_msg_flag, project_uuid, oproject_uuid)"
							+ "VALUES (?, ?, 2, 'pg_'||?||'1', 0, " + "0, null, 1, ?, current_timestamp, ?,"
							+ "current_timestamp, 1, ?, ?)",
					templateId, customizationId, tableName, userId, userId, projectUuid, projectUuid);
			if (vcs)
				dao.saveObject(new W5VcsObject(scd, 63, templateId));

			int templateObjectId = GenericUtil.getGlobalNextval("iwb.seq_template_object", projectUuid, userId,
					customizationId); // 1000000+GenericUtil.uInt(dao.executeSQLQuery("select
			// nextval('seq_template_object')").get(0));
			dao.executeUpdateSQLQuery(
					"INSERT INTO iwb.w5_template_object("
							+ "template_object_id, template_id, customization_id, object_id, tab_order, object_tip,"
							+ "version_no, insert_user_id, insert_dttm, version_user_id, version_dttm,"
							+ "access_view_users, access_view_roles, access_view_tip, post_js_code,"
							+ "parent_object_id, src_query_field_id, dst_query_param_id,"
							+ "dst_static_query_param_val, dst_static_query_param_id, active_flag, project_uuid, oproject_uuid)"
							+ "VALUES (?, ?, ?, ?, 1, 1," + "1, ?, current_timestamp, ?, current_timestamp,"
							+ "null, null, 0, null," + "0, null, null,null, null, 1, ?, ?)",
					templateObjectId, templateId, customizationId, gridId, userId, userId, projectUuid, projectUuid);
			if (vcs)
				dao.saveObject(new W5VcsObject(scd, 64, templateObjectId));

			menuId = GenericUtil.getGlobalNextval("iwb.seq_menu", projectUuid, userId, customizationId); // 1000000+GenericUtil.uInt(dao.executeSQLQuery("select
			// nextval('seq_template_object')").get(0));
			dao.executeUpdateSQLQuery(
					"INSERT INTO iwb.w5_menu(" + "menu_id, parent_menu_id, user_tip, node_tip, locale_msg_key,"
							+ "tab_order, img_icon, url, version_no, insert_user_id, insert_dttm,"
							+ "version_user_id, version_dttm, customization_id, access_view_tip, project_uuid, oproject_uuid)"
							+ "VALUES (?, 0, ?, 4, ?, "
							+ "coalesce((select max(q.tab_order) from iwb.w5_menu q where q.customization_id=? AND q.user_tip=?),0)+10, null, 'showPage?_tid='||?::text, 1, ?, current_timestamp, "
							+ "?, current_timestamp, ?, 0, ?, ?)",
					menuId, userTip, gridName, customizationId, userTip, templateId, userId, userId, customizationId,
					projectUuid, projectUuid);
			if (vcs)
				dao.saveObject(new W5VcsObject(scd, 65, menuId));
		} else {
			Object[] loo = (Object[]) dao.executeSQLQuery(
					"select f.dsc, f.table_field_id "
							+ "from iwb.w5_table_field f where f.customization_id=? AND f.table_id=? AND f.tab_order=2",
					customizationId, tableId).get(0);
			dao.executeUpdateSQLQuery(
					"UPDATE iwb.w5_table_field f SET can_update_flag=0 WHERE f.customization_id=? AND f.table_id=? AND f.tab_order=2",
					customizationId, tableId);
			dao.executeUpdateSQLQuery(
					"UPDATE iwb.w5_grid f SET code=f.dsc||'._postInsert=function(sel,url,a){var m=getMasterGridSel(a,sel);if(m)return url+\"&"
							+ loo[0] + "=\" +(m." + loo[0] + " || m.get(\"" + loo[0]
							+ "\"));};' WHERE f.customization_id=? AND f.grid_id=?",
					customizationId, gridId);
			int tableChildId = GenericUtil.getGlobalNextval("iwb.seq_table_relation", projectUuid, userId,
					customizationId);
			dao.executeUpdateSQLQuery(
					"insert INTO iwb.w5_table_child "
							+ "(table_child_id, locale_msg_key, relation_tip, table_id, table_field_id, related_table_id, related_table_field_id, related_static_table_field_id, related_static_table_field_val, version_no, insert_user_id, insert_dttm, version_user_id, version_dttm, copy_strategy_tip, on_readonly_related_action, on_invisible_related_action, on_delete_action, tab_order, on_delete_action_value, child_view_tip, child_view_object_id, revision_flag, project_uuid, customization_id) "
							+ "values(?, ?, 2, ?     , ?             , ?               , ?                     , 0                            , 0                             , 1         , ?             , current_timestamp  , ?      , current_timestamp , 0          , 0                         , 0                          , 0               , 10       , null                  , 0             , 0                   , 0            , ?           , ?)",
					tableChildId, "rel_xxx2" + tableName, parentTableId,
					FrameworkCache.getTable(scd, parentTableId).get_tableFieldList().get(0).getTableFieldId(), tableId,
					loo[1], userId, userId, projectUuid, customizationId);
			if (vcs)
				dao.saveObject(new W5VcsObject(scd, 657, tableChildId));

			int queryParamId = GenericUtil.getGlobalNextval("iwb.seq_query_param", projectUuid, userId,
					customizationId); // 1000000+GenericUtil.uInt(dao.executeSQLQuery("select
			// nextval('seq_query_param')").get(0));
			dao.executeUpdateSQLQuery(
					"INSERT INTO iwb.w5_query_param("
							+ "query_param_id, query_id, dsc, param_tip, expression_dsc, operator_tip,"
							+ "not_null_flag, tab_order, source_tip, default_value, min_length,"
							+ "max_length, version_no, insert_user_id, insert_dttm, version_user_id,"
							+ "version_dttm, related_table_field_id, min_value, max_value, project_uuid, oproject_uuid, customization_id)"
							+ "values (" + "?, ?, 'x'||?, 4, 'x.'||?, 0, " + "1, 1, 1, null, 0,"
							+ "0, 1, ?, current_timestamp, ?," + "current_timestamp, ?, null, null, ?, ?, ?)",
					queryParamId, queryId, loo[0], loo[0], userId, userId, GenericUtil.uInt(loo[1]), projectUuid,
					projectUuid, customizationId);
			if (vcs)
				dao.saveObject(new W5VcsObject(scd, 10, queryParamId));

			int parentQueryId = GenericUtil.uInt(dao.executeSQLQuery(
					"select g.query_id from iwb.w5_template_object q, iwb.w5_grid g where q.template_object_id=? AND q.customization_id=? "
							+ " AND g.customization_id=q.customization_id AND q.object_id=g.grid_id",
					parentTemplateObjectId, customizationId).get(0));

			int templateObjectId = GenericUtil.getGlobalNextval("iwb.seq_template_object", projectUuid, userId,
					customizationId); // 1000000+GenericUtil.uInt(dao.executeSQLQuery("select
			// nextval('seq_template_object')").get(0));
			dao.executeUpdateSQLQuery(
					"INSERT INTO iwb.w5_template_object("
							+ "template_object_id, template_id, customization_id, object_id, tab_order, object_tip,"
							+ "version_no, insert_user_id, insert_dttm, version_user_id, version_dttm,"
							+ "access_view_users, access_view_roles, access_view_tip, post_js_code,"
							+ "parent_object_id, src_query_field_id, dst_query_param_id,"
							+ "dst_static_query_param_val, dst_static_query_param_id, active_flag, project_uuid, oproject_uuid)"
							+ "VALUES ("
							+ "?, ?, ?, ?, (select coalesce(max(q.tab_order),0)+1 from iwb.w5_template_object q where q.template_id=? AND q.customization_id=? ), 1,"
							+ "1, ?, current_timestamp, ?, current_timestamp," + "null, null, 0, null,"
							+ "?, (select min(r.query_field_id) from iwb.w5_query_field r where r.query_id=? AND  "
							+ "r.tab_order=(select min(f.tab_order) from iwb.w5_query_field f where f.query_id=?)), ?,"
							+ "null, null, 1, ?, ?)",
					templateObjectId, parentTemplateId, customizationId, gridId, parentTemplateId, customizationId,
					userId, userId, parentTemplateObjectId, parentQueryId, parentQueryId, queryParamId, projectUuid,
					projectUuid);
			if (vcs)
				dao.saveObject(new W5VcsObject(scd, 64, templateObjectId));

			if (webPageId != null) {
				nt.put("_tmpStr", "Page & Menu Created");
				UserUtil.broadCast(projectUuid, (Integer) scd.get("userId"), (String) scd.get("sessionId"), webPageId,
						msg);
			}
		}
		dao.executeUpdateSQLQuery(
				"update iwb.w5_table t "
						+ "set default_insert_form_id=?, default_update_form_id=?, default_view_grid_id=?, summary_record_sql='x.'||(select tf.dsc from iwb.w5_table_field tf where tf.tab_order=2 AND tf.table_id=t.table_id AND t.customization_id=tf.customization_id)||'::text' "
						+ "where t.table_id=? AND t.customization_id=? ",
				formId, formId, gridId, tableId, customizationId);

		dao.executeUpdateSQLQuery(
				"update iwb.w5_table_field tf "
						+ "set default_control_tip=(select fc.control_tip from iwb.w5_form_cell fc, iwb.w5_form f where fc.customization_id=f.customization_id AND f.form_id=fc.form_id AND f.object_tip=2 AND f.object_id=tf.table_id AND fc.dsc=tf.dsc), default_lookup_table_id=(select fc.lookup_query_id from iwb.w5_form_cell fc, iwb.w5_form f where fc.customization_id=f.customization_id AND f.form_id=fc.form_id AND f.object_tip=2 AND f.object_id=tf.table_id AND fc.dsc=tf.dsc) "
						+ "where tf.table_id=? AND tf.customization_id=? AND tf.tab_order>1 AND tf.dsc not in ('version_no','insert_user_id','insert_dttm','version_user_id','version_dttm') ",
				tableId, customizationId);

		dao.executeUpdateSQLQuery(
				"update iwb.w5_query_field tf "
						+ "set post_process_tip=(select case when f.default_control_tip in (6) then 10 when f.default_control_tip in (8) then 11 else 0 end from w5_table_field f where tf.customization_id=f.customization_id AND f.table_id=? AND f.dsc=tf.dsc)"
						+ ", lookup_query_id=(select case when f.default_control_tip in (6,8) then f.default_lookup_table_id else 0 end from w5_table_field f where tf.customization_id=f.customization_id AND f.table_id=? AND f.dsc=tf.dsc)  "
						+ ", main_table_field_id=(select f.table_field_id from w5_table_field f where tf.customization_id=f.customization_id AND f.table_id=? AND f.dsc=tf.dsc)  "
						+ "where tf.query_id=? AND tf.customization_id=?",
				tableId, tableId, tableId, queryId, customizationId);

		if (parentTableId == 0) { // main Template
			return templateId;
		} else {
			return gridId;
		}
	}

	public W5FormResult postFormAsJson(Map<String, Object> scd, int mainFormId, int action, JSONObject mainFormData,
			int detailFormId, JSONArray detailFormData) {
		return crudEngine.postFormAsJson(scd, mainFormId, action, mainFormData, detailFormId, detailFormData);
	}

	public M5ListResult getMListResult(Map<String, Object> scd, int listId, Map<String, String> parameterMap) {
		return uiEngine.getMListResult(scd, listId, parameterMap);
	}

	public Map getUserNotReadChatMap(Map<String, Object> scd) {
		String s = "select k.sender_user_id user_id , count(1) cnt from iwb.w5_chat k where k.receiver_user_id=${scd.userId}::integer AND k.deliver_status_tip in (0,1) "// AND
																																											// k.customization_id=${scd.customizationId}::integer
																																											// "
				+ "AND k.sender_user_id in (select u.user_id from iwb.w5_user u where ((u.customization_id=${scd.customizationId}::integer AND (u.global_flag=1 OR u.project_uuid='${scd.projectId}') AND u.user_status=1)) OR exists(select 1 from iwb.w5_user_related_project rp where rp.user_id=u.user_id AND rp.related_project_uuid='${scd.projectId}'))"
				+ " group by k.sender_user_id";
		Object[] oz = DBUtil.filterExt4SQL(s, scd, null, null);
		List<Object[]> l = dao.executeSQLQuery2(oz[0].toString(), (List) oz[1]);
		Map r = new HashMap();
		if (l != null)
			for (Object[] o : l)
				r.put(o[0], o[1]);
		return r;
	}

	public Map executeQuery4Stat(Map<String, Object> scd, int gridId, Map<String, String> requestParams) {
		requestParams.remove("firstLimit");
		requestParams.remove("limit");
		requestParams.remove("start");
		requestParams.remove("sort");
		return queryEngine.executeQuery4Stat(scd, gridId, requestParams);
	}

	public Map executeQuery4StatTree(Map<String, Object> scd, int gridId, Map<String, String> requestParams) {
		requestParams.remove("firstLimit");
		requestParams.remove("limit");
		requestParams.remove("start");
		requestParams.remove("sort");
		return queryEngine.executeQuery4StatTree(scd, gridId, requestParams);
	}

	public List<W5BIGraphDashboard> getGraphDashboards(Map<String, Object> scd) {
		String dashIds = (String) dao.executeSQLQuery(
				"select r.mobile_portlet_ids from iwb.w5_user_role r where r.customization_id=? AND r.user_role_id=?",
				(Integer) scd.get("customizationId"), (Integer) scd.get("userRoleId")).get(0);
		if (GenericUtil.isEmpty(dashIds))
			return null;
		String[] ds = dashIds.split(",");
		List<W5BIGraphDashboard> l = new ArrayList();
		for (String s : ds) {
			int id = GenericUtil.uInt(s);
			if (id == 0)
				continue;
			if (id < 0)
				id = -id;
			W5BIGraphDashboard gd = (W5BIGraphDashboard) dao.getCustomizedObject(
					"from W5BIGraphDashboard t where t.graphDashboardId=? AND t.projectUuid=?", id,
					(String) scd.get("projectId"), "GraphDashBoard");
			if (gd != null)
				l.add(gd);
		}
		return l;
	}

	/*
	 * public String projectAccessUrl(String instanceUuid, String remoteAddr) {
	 * List l = dao.executeSQLQuery(
	 * "select p.val from iwb.w5_app_setting p where p.customization_id=0 AND p.dsc=?"
	 * , "iwb_active_projects4"+remoteAddr); if(GenericUtil.isEmpty(l))return
	 * null; return l.get(0).toString(); }
	 */

	public Object executeQuery4Debug(Map<String, Object> scd, int queryId, Map<String, String> requestParams) {
		return debugEngine.executeQuery4Debug(scd, queryId, requestParams);
	}

	public List executeQuery4DataList(Map<String, Object> scd, int tableId, Map<String, String> requestParams) {
		return queryEngine.executeQuery4DataList(scd, tableId, requestParams);
	}

	public List executeQuery4Pivot(Map<String, Object> scd, int tableId, Map<String, String> requestParams) {
		return queryEngine.executeQuery4Pivot(scd, tableId, requestParams);
	}

	public W5GlobalFuncResult executeGlobalFunc4Debug(Map<String, Object> scd, int dbFuncId,
			Map<String, String> parameterMap) {
		return debugEngine.executeGlobalFunc4Debug(scd, dbFuncId, parameterMap);
	}

	public Map<String, Object> getWsServerMethodObjects(W5WsServer wss) {
		Map<String, Object> wsmoMap = new HashMap();
		Map scd = new HashMap();
		scd.put("projectId", wss.getProjectUuid());
		for (W5WsServerMethod wsm : wss.get_methods())
			try {
				switch (wsm.getObjectTip()) {
				case 0:
				case 1:
				case 2:
				case 3: // form
					wsmoMap.put(wsm.getDsc(), metaDataDao.getFormResult(scd, wsm.getObjectId(),
							wsm.getObjectTip() == 0 ? 1 : wsm.getObjectTip(), new HashMap()));
					break;
				case 4:
					wsmoMap.put(wsm.getDsc(), metaDataDao.getGlobalFuncResult(scd, wsm.getObjectId()));
					break;
				case 19:
					wsmoMap.put(wsm.getDsc(), metaDataDao.getQueryResult(scd, wsm.getObjectId()));
					break;
				case 31:
				case 32:
				case 33:
					wsmoMap.put(wsm.getDsc(), FrameworkCache.getWorkflow(scd, wsm.getObjectId()));
					break;
				default:
					wsmoMap.put(wsm.getDsc(), "Wrong ObjectTip");
				}
			} catch (Exception e) {
				wsmoMap.put(wsm.getDsc(), "Invalid Object");
			}
		return wsmoMap;
	}


	public Map REST(Map<String, Object> scd, String name, Map requestParams) throws IOException {
		return restEngine.REST(scd, name, requestParams);
	}

	private W5TsMeasurement getTsMeasurement(Map<String, Object> scd, int measurementId) { // TODO
		W5TsMeasurement m = null;
		/*
		 * if(FrameworkSetting.preloadWEngine==0 &&
		 * (m=FrameworkCache.getTsMeasurement(scd,measurementId))==null){ m =
		 * (W5TsMeasurement)dao.getCustomizedObject(
		 * "from W5TsMeasurement t where t.measurementId=? AND t.customizationId=?"
		 * , measurementId, (Integer)scd.get("customizationId"),
		 * "TSMeasurement"); m.set_measurementFields(dao.find(
		 * "from W5TsMeasurementField t where t.portletId=? AND t.customizationId=? order by t.tabOrder, t.portletObjectId"
		 * , measurementId, (Integer)scd.get("customizationId")));
		 * if(FrameworkSetting.preloadWEngine!=0)FrameworkCache.wTsMeasurements.
		 * get((Integer)scd.get("customizationId")).put(measurementId, m); }
		 */
		return m;
	}

	public String getTsDashResult(Map<String, Object> scd, Map<String, String> requestParams, int porletId) { // TODO
		W5TsPortlet p = null;
		/*
		 * if(FrameworkSetting.preloadWEngine==0 ||
		 * (p=FrameworkCache.getTsPortlet(scd,porletId))==null){ p =
		 * (W5TsPortlet)dao.getCustomizedObject(
		 * "from W5TsPortlet t where t.portletId=? AND t.customizationId=?",
		 * porletId, (Integer)scd.get("customizationId"), "TSPorlet");
		 * p.set_portletObjects(dao.find(
		 * "from W5TsPortletObject t where t.portletId=? AND t.customizationId=? order by t.tabOrder, t.portletObjectId"
		 * , porletId, (Integer)scd.get("customizationId")));
		 * if(FrameworkSetting.preloadWEngine!=0)FrameworkCache.wTsPortlets.get(
		 * (Integer)scd.get("customizationId")).put(porletId, p); }
		 * StringBuilder s = new StringBuilder(); for(W5TsPortletObject
		 * po:p.get_portletObjects())switch(po.getObjectTip()){ case
		 * 2709://measurement po.set_sourceObject(getTsMeasurement(scd,
		 * po.getObjectId())); // Object o =
		 * influxDao.runQuery(po.getExtraCode()); //
		 * resultMap.put(po.getPortletObjectId(), o); break; case 8://query
		 * po.set_sourceObject(dao.getQueryResult(scd, po.getObjectId()));
		 * break; } switch(p.getCodeTip()){ case 1://A*B/C
		 * s.append("\nfunction(){\n").append(p.getExtraCode()) .append(
		 * "\nvar result=[];for(var qi=0;qi<)"); break; case 2://
		 * function(A,B,C){} s.append(p.getExtraCode()); break; case 3:
		 * s.append(p.getExtraCode()); break; } return s.toString();
		 */
		return null;
	}

	public boolean copyTable2Tsdb(Map<String, Object> scd, int tableId, int measurementId) {
		W5TsMeasurement tsm = getTsMeasurement(scd, measurementId);
		if (!GenericUtil.accessControl(scd, tsm.getAccessViewTip(), tsm.getAccessViewRoles(),
				tsm.getAccessViewUsers())) {
			throw new IWBException("security", "W5TsMeasurement", measurementId, null,
					LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_access_control_ts_measurement"), null);
		}
		if (tsm.getMeasurementObjectId() != 2 || tsm.getMeasurementObjectId() == 0)
			throw new IWBException("framework", "W5TsMeasurement", measurementId, null, "Source Not RDB Table or X",
					null);
		W5Table t = FrameworkCache.getTable(scd, tableId);
		W5TableField timeField = t.get_tableFieldMap().get(tsm.getMeasurementObjectId());
		StringBuilder s = new StringBuilder();
		s.append("SELECT x.").append(timeField.getDsc()).append(" xtime");
		if (!GenericUtil.isEmpty(tsm.getTagCode()))
			s.append(",").append(tsm.getTagCode());
		if (!GenericUtil.isEmpty(tsm.getExtraCode()))
			s.append(",").append(tsm.getExtraCode());
		s.append(" from ").append(t.getDsc()).append(" x");
		if (t.get_tableParamList().size() > 1 && t.get_tableParamList().get(1).getDsc().equals("customizationId"))
			s.append(" WHERE x.customization_id=${scd.customizationId}");
		Object[] oz = DBUtil.filterExt4SQL(s.toString(), scd, new HashMap(), null);
		List<Map> lm = dao.executeSQLQuery2Map(oz[0].toString(), (List) oz[1]);

		return false;
	}

	public Map generateScdFromAuth(int socialCon, String token) {
		List<Object[]> list = dao.executeSQLQuery(
				"select u.user_id, u.customization_id from iwb.w5_user u"
						+ " where u.lkp_auth_external_source=? AND u.user_status=1 AND u.auth_external_id=?",
				socialCon, token);
		if (!GenericUtil.isEmpty(list)) {
			Object[] oz = list.get(0);
			Map<String, Object> scd = userSession4Auth(GenericUtil.uInt(oz[0]), GenericUtil.uInt(oz[1]));
			if (scd != null) {
				dao.executeUpdateSQLQuery(
						"update iwb.w5_user set last_succesful_login_dttm=current_timestamp, succesful_login_count=succesful_login_count+1 where user_id=?",
						scd.get("userId"));
			}
			return scd;
		} else {
			return null;
		}
	}

	public void saveCredentials(int cusId, int userId, String picUrl, String fullName, int socialNet, String email,
			String nickName, List<Map> projects, List<Map> userTips) {
		if (dao.find("select 1 from W5Customization t where t.customizationId=?", cusId).isEmpty())
			dao.executeUpdateSQLQuery(
					"insert into iwb.w5_customization(customization_id, dsc, sub_domain) values (?,?,?)", cusId,
					socialNet, nickName.replace('.', '_').replace('-', '_'));
		FrameworkCache.wCustomizationMap.put(cusId,
				(W5Customization) dao.find("from W5Customization t where t.customizationId=?", cusId).get(0));

		FrameworkSetting.projectSystemStatus.put(projects.get(0).get("project_uuid").toString(), 0);
		if (GenericUtil.isEmpty(dao.executeSQLQuery("select 1 from iwb.w5_user u where u.user_id=?", userId))) {
			dao.executeUpdateSQLQuery(
					"insert into iwb.w5_user(user_id, customization_id, user_name, email, pass_word, user_status, dsc,login_rule_id, lkp_auth_external_source, auth_external_id, project_uuid) values (?,?,?,?,iwb.md5hash(?),?,?,?,?,?,?)",
					userId, cusId, nickName, email, nickName + 1, 1, nickName, 1, socialNet, email,
					projects.get(0).get("project_uuid"));
			int userRoleId = GenericUtil.getGlobalNextval("iwb.seq_user_role",
					(String) projects.get(0).get("project_uuid"), userId, cusId);
			dao.executeUpdateSQLQuery(
					"insert into iwb.w5_user_role(user_role_id, user_id, role_id, customization_id,unit_id, project_uuid) values(?, ?, 0, ?,?, ?)",
					userRoleId, userId, cusId, 0, projects.get(0).get("project_uuid"));
		}

		for (Map p : projects) {
			String projectId = (String) p.get("project_uuid");
			String oprojectId = (String) p.get("oproject_uuid");
			if (oprojectId == null)
				oprojectId = projectId;
			String vcsUrl = (String) p.get("vcs_url");

			if (GenericUtil
					.isEmpty(dao.executeSQLQuery("select 1 from iwb.w5_project p where p.project_uuid=?", projectId))) {
				String schema = "c" + GenericUtil.lPad(cusId + "", 5, '0') + "_" + projectId.replace('-', '_');
				dao.executeUpdateSQLQuery(
						"insert into iwb.w5_project(project_uuid, customization_id, dsc, access_users,  rdbms_schema, vcs_url, vcs_user_name, vcs_password, oproject_uuid)"
								+ " values (?,?,?, ?, ?,?,?,?, ?)",
						projectId, cusId, p.get("dsc"), "" + userId, schema, vcsUrl, nickName, "1", oprojectId);
				dao.executeUpdateSQLQuery("create schema " + schema + " AUTHORIZATION iwb");
			}

			dao.addProject2Cache(projectId);
			FrameworkSetting.projectSystemStatus.put(projectId, 0);
		}

		for (Map t : userTips) {
			String projectId = (String) t.get("project_uuid");
			String oprojectId = (String) t.get("oproject_uuid");
			if (oprojectId == null)
				oprojectId = projectId;
			int userTip = GenericUtil.uInt(t.get("user_tip"));
			// List list = dao.executeSQLQuery("select 1 from iwb.w5_user_tip p
			// where
			// p.user_tip=?",userTip);
			if (GenericUtil.isEmpty(dao.executeSQLQuery(
					"select 1 from iwb.w5_user_tip p where p.user_tip=? AND p.project_uuid=?", userTip, projectId))) {
				dao.executeUpdateSQLQuery(
						"insert into iwb.w5_user_tip(user_tip, dsc, customization_id, project_uuid, oproject_uuid, web_frontend_tip, default_main_template_id)"
								+ " values (?,?,?, ?, ?, 5, 2307)",
						userTip, "Role Group 1", cusId, projectId, oprojectId);
				Map newScd = new HashMap();
				newScd.put("projectId", projectId);
				newScd.put("customizationId", cusId);
				newScd.put("userId", userId);
				W5VcsObject vo = new W5VcsObject(newScd, 369, userTip);
				vo.setVcsObjectStatusTip((short) 9);
				dao.saveObject(vo);
				if (GenericUtil.isEmpty(dao.executeSQLQuery(
						"select 1 from iwb.w5_role p where p.role_id=0 AND customization_id=?", cusId))) {
					dao.executeUpdateSQLQuery(
							"insert into iwb.w5_role(role_id, customization_id, dsc, user_tip, project_uuid) values (0,?,?,?,?)",
							cusId, "Role " + System.currentTimeMillis(), userTip, projectId);
				}
			}
		}
		metaDataDao.reloadFrameworkCaches(cusId);
		saveImage(picUrl, userId, cusId, null);
	}

	public void saveImage(String imageUrl, int userId, int cusId, String projectUuid) {
		try {
			List lf = dao.find(
					"select t.fileAttachmentId from W5FileAttachment t where t.tableId=336 AND t.fileTypeId=-999 AND t.tablePk=? AND t.customizationId=? AND t.orijinalFileName=?",
					"" + userId, cusId, imageUrl);
			if (!lf.isEmpty()) {
				if (UserUtil.getUserProfilePicture(userId) == (Integer) lf.get(0))
					return;
			}
			URL url = new URL(imageUrl);
			int length;
			int totalBytesRead = 0;
			InputStream is = url.openStream();
			long fileId = new Date().getTime();
			W5FileAttachment fa = new W5FileAttachment();

			fa.setSystemFileName(fileId + "." + GenericUtil.strUTF2En(FilenameUtils.getBaseName(url.getPath())));
			String testPath = FrameworkCache.getAppSettingStringValue(0, "file_local_path") + File.separator + cusId;
			File f = new File(testPath);

			if (!f.exists()) {
				boolean cDir = new File(testPath).mkdirs();
				boolean aDir = new File(testPath + File.separator + "attachment").mkdirs();
			}

			String filePath = FrameworkCache.getAppSettingStringValue(0, "file_local_path") + File.separator + cusId
					+ File.separator + "attachment" + File.separator + fa.getSystemFileName();

			OutputStream os = new FileOutputStream(filePath);
			byte[] b = new byte[2048];

			while ((length = is.read(b)) != -1) {
				totalBytesRead += length;
				os.write(b, 0, length);
			}
			is.close();
			os.close();

			fa.setCustomizationId(cusId);
			fa.setOrijinalFileName(imageUrl);
			fa.setUploadUserId(userId);
			fa.setFileSize(totalBytesRead);
			fa.setFileTypeId(-999);
			fa.setTabOrder((short) 1);
			fa.setActiveFlag((short) 1);
			fa.setTableId(336);
			fa.setTablePk("" + userId);
			fa.setProjectUuid(projectUuid == null ? "067e6162-3b6f-4ae2-a221-2470b63dff00": projectUuid);
			saveObject(fa);

		} catch (Exception io) {
			io.printStackTrace();
		}
	}

	public Map userExists(String email) {
		List<Object[]> list = dao.executeSQLQuery("select u.user_id, u.customization_id, (select min(r.user_role_id)"
				+ " from iwb.w5_user_role r where r.customization_id=u.customization_id AND r.user_id=u.user_id) user_role_id from iwb.w5_user u where "
				+ "u.user_status=1 AND u.auth_external_id=?", email);
		if (!GenericUtil.isEmpty(list)) {
			Object[] oz = list.get(0);
			return userRoleSelect(GenericUtil.uInt(oz[0]), GenericUtil.uInt(oz[2]), GenericUtil.uInt(oz[1]), null,
					null);
		} else {
			return null;
		}
	}

	public void addToProject(int userId, String projectId, String email) {
		List<Object[]> list = dao
				.executeSQLQuery("select u.user_id, u.related_project_uuid from iwb.w5_user_related_project u"
						+ " where u.user_id=? AND u.related_project_uuid=?", userId, projectId);
		if (!GenericUtil.isEmpty(list)) {
			dao.executeUpdateSQLQuery(
					"insert into iwb.w5_user_related_project(user_id, related_project_uuid) values (?,?)", userId,
					projectId);
			dao.executeUpdateSQLQuery("update iwb.w5_user set email=? where user_id=?", email, userId);
		}
	}

	public Map runTests(Map<String, Object> scd, String testIds, String webPageId) {
		String projectUuid = scd.get("projectId").toString();
		Map m = new HashMap();
		m.put("success", true);
		List<Object[]> l = null;
		if (GenericUtil.isEmpty(testIds))
			l = dao.executeSQLQuery(
					"select x.test_id, x.dsc, x.code from iwb.w5_test x where x.lkp_test_type=0 AND x.project_uuid=? order by x.tab_order ",
					projectUuid);
		else {
			List params = new ArrayList();
			params.add(projectUuid);
			String[] xx = testIds.split(",");
			StringBuilder sql = new StringBuilder();
			sql.append(
					"select x.test_id, x.dsc, x.code from iwb.w5_test x where x.lkp_test_type=0 AND x.project_uuid=? AND x.test_id in(-1");
			for (String s : xx) {
				params.add(GenericUtil.uInt(s));
				sql.append(",?");
			}
			sql.append(") order by x.tab_order ");
			l = dao.executeSQLQuery(sql.toString(), params);
		}

		if (l != null) {
			Map tmp = new HashMap();
			Map msg = null, nt = null;
			if (webPageId != null) {
				msg = new HashMap();
				msg.put("success", true);
				nt = new HashMap();
				msg.put("notification", nt);
			}
			for (Object[] o : l)
				try {
					Object result = scriptEngine.executeScript(scd, tmp, o[2].toString(), tmp, "3243t"+o[0]);
					if (result != null) {
						if (result instanceof Double || result instanceof Integer || result instanceof Float
								|| result instanceof BigDecimal) {
							if (GenericUtil.uInt(result) == 0)
								result = null;
						} else if (result instanceof Boolean) {
							if (!((Boolean) result))
								result = null;
						} else if (result instanceof String) {
							if (((String) result).length() == 0)
								result = null;
						}
					}
					if (result != null) {
						m.put("dsc", o[1]);
						m.put("failId", o[0]);
						m.put("msg", result);
						return m;
					}
					if (webPageId != null) {
						nt.put("_tmpStr", "Passed: " + o[1].toString());
						UserUtil.broadCast(projectUuid, (Integer) scd.get("userId"), (String) scd.get("sessionId"),
								webPageId, msg);
					}
				} catch (Exception e) {
					m.put("dsc", o[1]);
					m.put("failId", o[0]);
					m.put("msg", e.getMessage());
					return m;
				}
		}
		return m;
	}

	public boolean changeChangeProjectStatus(Map<String, Object> scd, String projectUuid, int newStatus) {
		List params = new ArrayList();
		params.add(projectUuid);
		List<Map<String, Object>> l = dao.executeSQLQuery2Map(
				"SELECT x.*,(select q.customization_id from iwb.w5_project q where q.project_uuid=x.oproject_uuid) qcus_id FROM iwb.w5_project x WHERE x.project_uuid=? ",
				params);
		if (GenericUtil.isEmpty(l))
			return false;
		Map m = l.get(0);
		if (GenericUtil.uInt(m.get("customization_id")) == 1) {
			if ((Integer) scd.get("customizationId") == 1
					|| (newStatus == 2 && GenericUtil.uInt(m.get("qcus_id")) == (Integer) scd.get("customizationId"))) {
				dao.executeUpdateSQLQuery("update iwb.w5_project set project_status_tip=? WHERE project_uuid=?",
						newStatus, projectUuid);
				dao.addProject2Cache(projectUuid);
				return true;
			}
		}
		return false;
	}



	public Map organizeREST(Map<String, Object> scd, String serviceName) {
		return restEngine.organizeREST(scd, serviceName);	}

	public String getServerDttm() {
		return dao.executeSQLQuery("select to_char(current_timestamp,'dd/mm/yyyy hh24:mi:ss')").get(0).toString();
	}
}