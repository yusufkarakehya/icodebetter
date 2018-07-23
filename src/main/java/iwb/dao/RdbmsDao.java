package iwb.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import iwb.domain.db.W5Conversion;
import iwb.domain.db.W5Email;
import iwb.domain.db.W5FileAttachment;
import iwb.domain.db.W5FormCell;
import iwb.domain.db.W5FormSmsMail;
import iwb.domain.db.W5JasperReport;
import iwb.domain.db.W5QueryField;
import iwb.domain.db.W5Table;
import iwb.domain.db.W5TableChild;
import iwb.domain.db.W5WsMethod;
import iwb.domain.helper.W5FormCellHelper;
import iwb.domain.helper.W5TableChildHelper;
import iwb.domain.helper.W5TableRecordHelper;
import iwb.domain.result.M5ListResult;
import iwb.domain.result.W5DataViewResult;
import iwb.domain.result.W5DbFuncResult;
import iwb.domain.result.W5FormResult;
import iwb.domain.result.W5GridResult;
import iwb.domain.result.W5JasperResult;
import iwb.domain.result.W5ListViewResult;
import iwb.domain.result.W5QueryResult;
import iwb.domain.result.W5TableRecordInfoResult;
import iwb.domain.result.W5TemplateResult;

public interface RdbmsDao{

	void saveObject(Object o);

	void updateObject(Object o);

	<T> T getObject(Class<T> clazz, Serializable id);

	void removeObject(Class clazz, Serializable id);

	void removeObject(Object obj);

	void removeAllObjects(List<?> l);

	void saveOrUpdateAllObjects(List<?> l);

	List find(String query, Object... params);

	
	List executeSQLQuery(String sql, Object... params);

	List executeSQLQuery2(String sql, List params);

	List executeSQLQuery2Map(String sql, List params);

	int executeUpdateSQLQuery(String sql, Object... params);

	//void setEngine(FrameworkEngine engine);

	W5QueryResult executeQuery(Map<String, Object> scd, int queryId, Map<String, String> requestParams);



	W5FormResult getFormResult(Map<String, Object> scd, int formId, int action, Map<String, String> requestParams);

	Object getCustomizedObject(String hql, int objectId, int customizationId, String onErrorMsg);

	W5QueryResult getQueryResult(Map<String, Object> scd, int queryId);

	void runQuery(W5QueryResult queryResult);

	Map<String, Object> runSQLQuery2Map(String code, Map<String, Object> scd, Map<String, String> requestParams,
			Map<String, Object> obj);

	Map<String, Object> runSQLQuery2Map(String sql, List params, List<W5QueryField> queryFields);

	List runQuery2Map(Map<String, Object> scd, int queryId, Map<String, String> requestParams);

	Map<String, Object> runSQLQuery2Map(String sql, List params, List<W5QueryField> queryFields,
			boolean closeConnectionAfterRun);

	void loadFormCellLookups(Map<String, Object> scd, List<W5FormCellHelper> formCellResults,
			Map<String, String> requestParams, String tabId);

	Map<String, Object> loadRecordMapValue(Map<String, Object> scd, Map<String, String> requestParams, W5Table t,
			String prefix);

	void loadFormTable(W5FormResult formResult);

	W5TemplateResult getTemplateResult(Map<String, Object> scd, int templateId);

	W5GridResult getGridResultMinimal(Map<String, Object> scd, int gridId, Map<String, String> requestParams);

	W5DataViewResult getDataViewResult(Map<String, Object> scd, int dataViewId, Map<String, String> requestParams,
			boolean noSearchForm);

	W5GridResult getGridResult(Map<String, Object> scd, int gridId, Map<String, String> requestParams,
			boolean noSearchForm);

	String getInitialFormCellValue(Map<String, Object> scd, W5FormCell cell, Map<String, String> requestParams);

	Map<String, String> interprateSmsTemplate(W5FormSmsMail fsm, Map<String, Object> scd,
			Map<String, String> requestParams, int fsmTableId, int fsmTablePk);

	W5Email interprateMailTemplate(W5FormSmsMail fsm, Map<String, Object> scd, Map<String, String> requestParams, int fsmTableId, int fsmTablePk);

	void initializeForm(W5FormResult formResult, boolean onlyFreeFields);


	boolean updateFormTable(W5FormResult formResult, String schema, String paramSuffix);

	int copyFormTable(W5FormResult formResult, String schema, String paramSuffix, boolean copyFlag);


	Object[] listObjectCommentAndAttachUsers(Map<String, Object> scd, Map<String, String> requestParams);

	int insertFormTable(W5FormResult formResult, String schema, String paramSuffix);

	boolean deleteTableRecord(W5FormResult formResult, String paramSuffix);

	void organizeQueryFields(Map<String, Object> scd, int queryId, short insertFlag);

	void reloadJobsCache();

	void reloadLocaleMsgsCache2(int cid);

	void reloadErrorMessagesCache();

	void reloadTableAccessConditionSQLs();

	void reloadApplicationSettingsCache(int cid);

	void reloadProjectsCache(int cid);

	void reloadApplicationSettingsValues();

	void reloadPublishLookUpsCache();

	void reloadLookUpCache(int customizationId);


	void reloadRolesCache(int customizationId);

	void reloadMobileCache();

	void reloadConversionsCache(int customizationId);

	void reloadTableActionsCache(int customizationId);

	void reloadTablesCache(int customizationId);

	void reloadApprovalCache(int customizationId);

	void reloadUsersCache(int cid);

	void reloadTableParamListChildListParentListCache(int cid);

	void reloadTableFieldListCache(int customizationId);

	void reloadTableFilterCache(int customizationId);

	void reloadPromisCaches(int cid);

	void reloadWsClientsCache(int customizationId);

	W5DbFuncResult getDbFuncResult(Map<String, Object> scd, int dbFuncId);

	void executeDbFunc(W5DbFuncResult r, String paramSuffix);

	void bookmarkForm(String dsc, int formId, int userId, int customizationId, W5FormResult formResult);

	W5JasperResult getJasperResult(Map<String, Object> scd, W5JasperReport jasperreport,
			Map<String, String> parameterMap);

	W5JasperReport getJasperReport(Map<String, Object> scd, int jasperReportId);

	void copyTableRecord(int tableId, int tablePk, String srcSchema, String dstSchema);

	boolean copyFormTableDetail(W5FormResult masterFormResult, W5TableChild tc, String newMasterTablePk, String schema,
			String prefix);

	List<Map> getMainTableData(Map<String, Object> scd, int tableId, String tablePk);

	List<W5TableRecordHelper> findRecordParentRecords(Map<String, Object> scd, int tableId, int tablePk, int maxLevel,
			boolean includeSummarySqlFlag);

	Object interprateTemplateExpression(Map<String, Object> scd, Map<String, String> requestParams, int tableId,
			int tablePk, StringBuilder tmp);

	Map<String, String> interprateTemplate(Map<String, Object> scd, Map<String, String> requestParams, int tableId,
			int tablePk, StringBuilder tmp, boolean replace, int smsMailReplaceTip, int conversionTip);

	boolean accessUserFieldControl(W5Table t, String accessUserFields, Map<String, Object> scd,
			Map<String, String> requestParams, String paramSuffix);

	public boolean conditionRecordExistsCheck(Map<String, Object> scd, Map<String, String> requestParams, int objectId, int conversionTablePk, String conditionSqlCode);
	List getRecordPictures(Map<String, Object> scd, int tableId, String tablePk);

	List<Object[]> getFileType(Map<String, Object> scd, int image_flag);

	/*	
	public boolean logException(String exceptionText,int customizationId,int userRoleId){		
		Log5Exception ex=new Log5Exception();
		ex.setCustomizationId(customizationId);
		ex.setExceptionText(exceptionText.substring(0,exceptionText.length()< 3999 ? exceptionText.length():3999));
		ex.setUserRoleId(userRoleId);
		*/
	/*exceptionText=exceptionText.substring(0,exceptionText.length()< 3999 ? exceptionText.length():3999);
	try { 
		executeSQLWithoutTransaction("insert into log5_exception(log_id, exception_text, user_role_id,  customization_id)   values  (seq_log5_exception.nextval, ?,?, ?)",exceptionText,userRoleId,customizationId);
		//saveObject(ex); 
		return true;}
	catch (Exception e) {
		return false;
	}
	return true;
	}*/


	Object executeRhinoScript(Map<String, Object> scd, Map<String, String> requestParams, String script, Map obj,
			String result);

	Map interprateConversionTemplate(W5Conversion c, W5FormResult dstFormResult, int conversionTablePk,
			boolean checkCondition, boolean onlyForSynch);

	Map interprateConversionTemplate4WsMethod(Map<String, Object> scd, Map<String, String> requestParams,
			W5Conversion c, int conversionTablePk, W5WsMethod wsm);

	void saveObject2(Object o, Map<String, Object> scd);

	M5ListResult getMListResult(Map<String, Object> scd, int listId, Map<String, String> requestParams,
			boolean noSearchForm);

	W5ListViewResult getListViewResult(Map<String, Object> scd, int listViewId, Map<String, String> requestParams,
			boolean noSearchForm);

	List<W5TableChildHelper> findRecordChildRecords(Map<String, Object> scd, int tableId, int tablePk);

	boolean accessControlTable(Map<String, Object> scd, W5Table t, Integer tablePk);

	W5TableRecordInfoResult getTableRecordInfo(Map<String, Object> scd, int tableId, int tablePk);

	String getSummaryText4Record(Map<String, Object> scd, int tableId, int tablePk);


	boolean checkIfRecordsExists(Map scd, Map<String, String> requestParams, W5Table t);

	void removeTableChildRecords(Map<String, Object> scd, int tableId, int tablePk, String dstDetailTableIds);

	W5FileAttachment getFileAttachment(int fileAttachmentId);

	String getObjectVcsHash(Map<String, Object> scd, int tableId, int tablePk);

	Map getTableRecordJson(Map<String, Object> scd, int tableId, int tablePk, int recursiveLevel);

	boolean saveVcsObject(Map<String, Object> scd, int tableId, int tablePk, int action, JSONObject o);

	String getTableRecordSummary(Map scd, int tableId, int tablePk, int maxLength);

	boolean organizeTable(Map<String, Object> scd, String fullTableName);

	void makeDirtyVcsObject(Map<String, Object> scd, int tableId, int tablePk);

	boolean organizeDbFunc(Map<String, Object> scd, String fullDbFuncName);

	String getCurrentDate(int customizationId);

	String getMd5Hash(String s);

	Object getSqlFunc(String s);

	Map executeSQLQuery2Map4Debug(Map<String, Object> scd, W5Table t, String sql, List params, int limit,
			int startOffset);

	W5DbFuncResult executeDbFunc4Debug(Map<String, Object> scd, int dbFuncId, Map<String, String> parameterMap);

	void executeQueryAsRhino(W5QueryResult qr, String code);

	Map executeQueryAsRhino4Debug(W5QueryResult qr, String script);

	Map executeQuery4Stat(Map<String, Object> scd, int gridId, Map<String, String> requestParams);
	Map executeQuery4StatTree(Map<String, Object> scd, int gridId, Map<String, String> requestParams);
	
	List executeQuery4DataList(Map<String, Object> scd, int tableId, Map<String, String> requestParams);
	List executeQuery4Pivot(Map<String, Object> scd, int tableId, Map<String, String> requestParams);
	
}