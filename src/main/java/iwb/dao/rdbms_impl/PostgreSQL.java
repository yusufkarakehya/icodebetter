package iwb.dao.rdbms_impl;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.jdbc.ReturningWork;
import org.hibernate.jdbc.Work;
//import org.influxdb.InfluxDBFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import iwb.cache.FrameworkCache;
import iwb.cache.FrameworkSetting;
import iwb.cache.LocaleMsgCache;
import iwb.custom.trigger.QueryTrigger;
import iwb.dao.RdbmsDao;
//import iwb.dao.tsdb_impl.InfluxDao;
import iwb.domain.db.Log5DbFuncAction;
import iwb.domain.db.Log5Feed;
import iwb.domain.db.Log5Notification;
import iwb.domain.db.Log5QueryAction;
import iwb.domain.db.M5List;
import iwb.domain.db.W5Approval;
import iwb.domain.db.W5ApprovalStep;
import iwb.domain.db.W5Conversion;
import iwb.domain.db.W5ConversionCol;
import iwb.domain.db.W5Customization;
import iwb.domain.db.W5DataView;
import iwb.domain.db.W5DbFunc;
import iwb.domain.db.W5DbFuncParam;
import iwb.domain.db.W5Email;
import iwb.domain.db.W5FileAttachment;
import iwb.domain.db.W5Form;
import iwb.domain.db.W5FormCell;
import iwb.domain.db.W5FormModule;
import iwb.domain.db.W5FormSmsMail;
import iwb.domain.db.W5FormValue;
import iwb.domain.db.W5FormValueCell;
import iwb.domain.db.W5Grid;
import iwb.domain.db.W5GridColumn;
import iwb.domain.db.W5Jasper;
import iwb.domain.db.W5JasperReport;
import iwb.domain.db.W5JobSchedule;
import iwb.domain.db.W5List;
import iwb.domain.db.W5ListColumn;
import iwb.domain.db.W5LookUp;
import iwb.domain.db.W5LookUpDetay;
import iwb.domain.db.W5ObjectToolbarItem;
import iwb.domain.db.W5Param;
import iwb.domain.db.W5Project;
import iwb.domain.db.W5Query;
import iwb.domain.db.W5QueryField;
import iwb.domain.db.W5QueryFieldCreation;
import iwb.domain.db.W5QueryParam;
import iwb.domain.db.W5Table;
import iwb.domain.db.W5TableAccessConditionSql;
import iwb.domain.db.W5TableChild;
import iwb.domain.db.W5TableField;
import iwb.domain.db.W5TableFieldCalculated;
import iwb.domain.db.W5TableFilter;
import iwb.domain.db.W5TableParam;
import iwb.domain.db.W5TableTrigger;
import iwb.domain.db.W5Template;
import iwb.domain.db.W5TemplateObject;
import iwb.domain.db.W5VcsObject;
import iwb.domain.db.W5Ws;
import iwb.domain.db.W5WsMethod;
import iwb.domain.db.W5WsMethodParam;
import iwb.domain.db.W5WsServer;
import iwb.domain.db.W5WsServerMethod;
import iwb.domain.db.W5WsServerMethodParam;
import iwb.domain.helper.W5AccessControlHelper;
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
import iwb.engine.FrameworkEngine;
import iwb.engine.ScriptEngine;
import iwb.enums.FieldDefinitions;
import iwb.exception.IWBException;
import iwb.util.DBUtil;
import iwb.util.GenericUtil;
import iwb.util.LogUtil;
import iwb.util.MailUtil;
import iwb.util.UserUtil;

@SuppressWarnings({"unchecked","unused"})
@Repository
public class PostgreSQL extends BaseDAO implements RdbmsDao {

	private static Logger logger = Logger.getLogger(PostgreSQL.class);
	@Autowired
	private FrameworkEngine engine;
	
	/*@Override
	public void setEngine(FrameworkEngine engine) {
		this.engine = engine;
	}*/
	
	@Override
	public W5QueryResult executeQuery(Map<String, Object> scd,int queryId,	Map<String,String> requestParams){
		W5QueryResult queryResult = getQueryResult(scd,queryId);
		if(queryId!=1 && queryId!=824 && queryResult.getMainTable()!=null && (!FrameworkSetting.debug || (scd.get("roleId")!=null && GenericUtil.uInt(scd.get("roleId"))!=0))){
			W5Table t = queryResult.getMainTable();
			if(t.getAccessViewUserFields()==null && !GenericUtil.accessControl(scd, t.getAccessViewTip(), t.getAccessViewRoles(), t.getAccessViewUsers())){
				throw new IWBException("security","Query", queryId, null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_guvenlik_tablo_kontrol_goruntuleme"), null);
			}

		}
		queryResult.setErrorMap(new HashMap());
		queryResult.setRequestParams(requestParams);


		
//		queryResult.setOrderBy(PromisUtil.uStrNvl(requestParams.get(PromisUtil.uStrNvl(PromisSetting.appSettings.get("sql_sort"),"sort")), queryResult.getQuery().getSqlOrderby()));
		if(requestParams.get("sort")!=null){
			queryResult.setOrderBy(requestParams.get("sort"));
	        if(requestParams.get("dir")!=null)queryResult.setOrderBy(queryResult.getOrderBy() + " " + requestParams.get("dir"));
		} else
			queryResult.setOrderBy(queryResult.getQuery().getSqlOrderby());		
		switch(queryResult.getQuery().getQueryTip()){
		case	9:case	10:queryResult.prepareTreeQuery(null);break;
		default:queryResult.prepareQuery(null);
		}
		if(queryResult.getErrorMap().isEmpty()){
			QueryTrigger.beforeExecuteQuery(queryResult, this);
	        queryResult.setFetchRowCount(GenericUtil.uIntNvl(requestParams, "limit", GenericUtil.uInt(requestParams,"firstLimit")));
	        queryResult.setStartRowNumber(GenericUtil.uInt(requestParams,"start"));
        	runQuery(queryResult);
        	if(queryResult.getQuery().getShowParentRecordFlag()!=0 && queryResult.getData()!=null){
        		for(Object[] oz:queryResult.getData()){
        			int tableId = GenericUtil.uInt(oz[queryResult.getQuery().get_tableIdTabOrder()]);
        			int tablePk = GenericUtil.uInt(oz[queryResult.getQuery().get_tablePkTabOrder()]);
        			oz[oz.length-1]=findRecordParentRecords(scd, tableId, tablePk, 0, true);
        		}
        		
        	}
	    	QueryTrigger.afterExecuteQuery(queryResult, this);
		}
		return queryResult;
	}
	
	private	boolean	loadForm(W5FormResult	f){
//       f.setForm((W5Form)loadObject(W5Form.class, f.getFormId()));
		
		
//		int customizationId = (Integer)f.getScd().get("customizationId");
		int customizationId = f.isDev() ? 0 : FrameworkCache.getCustomizationId(f.getScd());

		W5Form form = (W5Form)getCustomizedObject("from W5Form t where t.formId=? and t.customizationId=?", f.getFormId(), customizationId, "Form"); // ozel bir client icin varsa
		f.setForm(form);
		
		f.getForm().set_formCells(find("from W5FormCell t where t.formId=? AND t.customizationId=? order by t.tabOrder, t.xOrder, t.dsc", f.getFormId(), customizationId));
		
		for(W5FormCell fc:f.getForm().get_formCells())switch(fc.getControlTip()){
		case 31://code_list
			if(GenericUtil.uInt(fc.getLookupIncludedParams())==0){
				fc.setControlTip((short)1);
			} else
				fc.set_formCellCodeDetailList(find("from W5FormCellCodeDetail t where t.formCellId=? AND t.customizationId=? order by t.tabOrder", fc.getFormCellId(), customizationId));
			break;
		}
		if(form.getRenderTip()!=0){ // eger baska turlu render edilecekse
			form.set_moduleList(find("from W5FormModule t where t.formId=? AND t.customizationId=? order by t.tabOrder", form.getFormId(), customizationId));
		}
		
		HashMap hlps = new HashMap();
		
		f.getForm().set_toolbarItemList(find("from W5ObjectToolbarItem t where t.objectTip=? AND t.objectId=? AND t.customizationId=? order by t.tabOrder",(short)15,f.getFormId(),customizationId));
		f.getForm().set_formHintList(find("from W5FormHint h where h.activeFlag=1 AND h.formId=? AND h.customizationId=? order by h.tabOrder",f.getFormId(),customizationId));
		if(f.getForm().get_formHintList().isEmpty())f.getForm().set_formHintList(null);
		
		
		if(f.getForm().getObjectTip()!=1 && f.getForm().getRenderTemplateId()!=0){//grid(seachForm) degilse ve templateId varsa
			W5Template template = (W5Template)getCustomizedObject("from W5Template t where t.templateId=? and t.customizationId=?", f.getForm().getRenderTemplateId(), customizationId, null); // ozel bir client icin varsa
			f.getForm().set_renderTemplate(template);
		}
		Map<Short,W5Approval> mam= null;
		W5Table mt = null;
		switch(f.getForm().getObjectTip()){
		case	6://conversion icin
			W5Conversion c = FrameworkCache.wConversions.get(f.getForm().getObjectId());
			W5Table srct = FrameworkCache.getTable(customizationId, c.getSrcTableId());
			W5Table dstt = FrameworkCache.getTable(customizationId, c.getDstTableId());
//			f.getForm().set_sourceTable(dstt);
			for(W5FormCell fc:f.getForm().get_formCells())if(fc.getObjectDetailId()!=0){
				fc.set_sourceObjectDetail(c.get_conversionColMap().get(fc.getObjectDetailId()));
			}
			
			
			break;
		case	2:// table icin ise
		//           f.setTable((W5Table)loadObject(W5Table.class, f.getForm().getObjectId()));
			W5Table t = FrameworkCache.getTable(customizationId, f.getForm().getObjectId());
//			f.getForm().set_sourceTable(t);
			Map<String,W5TableField> fieldMap1 = new HashMap();
			for(W5TableField tf:(List<W5TableField>)t.get_tableFieldList()){
			   	fieldMap1.put(tf.getDsc(),tf);
			}
			for(W5FormCell fc:f.getForm().get_formCells())if(fc.getObjectDetailId()!=0){
				fc.set_sourceObjectDetail(t.get_tableFieldMap().get(fc.getObjectDetailId()));
			}
			if((fieldMap1.get("INSERT_USER_ID")!=null || fieldMap1.get("insert_user_id")!=null )&& (fieldMap1.get("VERSION_USER_ID")!=null || fieldMap1.get("version_user_id")!=null)){
				f.getForm().set_versioningFlag(true);
			}
			if(((FrameworkSetting.sms && FrameworkCache.getAppSettingIntValue(customizationId, "sms_flag")!=0) || (FrameworkSetting.mail && FrameworkCache.getAppSettingIntValue(customizationId, "mail_flag")!=0)) &&FrameworkCache.getAppSettingIntValue(customizationId, "form_sms_mail_flag")!=0){
				f.getForm().set_formSmsMailList(find("from W5FormSmsMail t where t.customizationId=? AND t.formId=? AND t.activeFlag=1 order by t.smsMailSentTip,t.tabOrder", customizationId, f.getFormId()));
				if(f.getForm().get_formSmsMailList().isEmpty())f.getForm().set_formSmsMailList(null);
				else {
					f.getForm().set_formSmsMailMap(new HashMap());
					for(W5FormSmsMail fsm:f.getForm().get_formSmsMailList()){
//						if(fsm.getSmsMailTip()==0)f.getForm().set_mailFlag(true); else f.getForm().set_smsFlag(true);
						f.getForm().get_formSmsMailMap().put(fsm.getFormSmsMailId(), fsm);
					}
				}
			}
			if(FrameworkCache.getAppSettingIntValue(customizationId, "form_conversion_flag")!=0){
				f.getForm().set_conversionList(find("from W5Conversion t where t.customizationId=? AND ((t.srcDstTip=0 AND t.srcFormId=?) OR (t.srcDstTip=1 AND t.srcTableId=?)) AND t.activeFlag=1 order by t.tabOrder", customizationId, f.getFormId(), f.getForm().getObjectId()));
				for(W5Conversion cnv:f.getForm().get_conversionList()){
					cnv.set_conversionColList(find("from W5ConversionCol t where t.customizationId=? AND t.conversionId=? order by t.tabOrder", customizationId,  cnv.getConversionId()));
				}
			}
			break;
		case	1: // grid icin ise
		//       	WGrid grid = (WGrid)loadObject(WGrid.class, f.getForm().getObjectId());
			Object[] ooo =(Object[])find("select t.queryId,(select q.mainTableId from W5Query q where q.queryId=t.queryId) from W5Grid t where t.customizationId=? AND t.gridId=?", customizationId, f.getForm().getObjectId()).get(0);
			int queryId = (Integer)ooo[0];
			int mainTableId = (Integer)ooo[1];
			if(mainTableId>0)mt = FrameworkCache.getTable(customizationId, mainTableId); //f.getForm().set_sourceTable()
			W5Query query = new W5Query(queryId);
			query.set_queryParams(find("from W5QueryParam t where t.queryId=? order by t.tabOrder",queryId));
			f.getForm().set_sourceQuery(query);
			Map<Integer,W5QueryParam> fieldMap2 = new HashMap();
			for(W5QueryParam tf:f.getForm().get_sourceQuery().get_queryParams()){
				fieldMap2.put(tf.getQueryParamId(),tf);
			}
			for(W5FormCell fc:f.getForm().get_formCells())if(fc.getObjectDetailId()!=0){
				if(fc.getObjectDetailId()>0)fc.set_sourceObjectDetail(fieldMap2.get(fc.getObjectDetailId()));//queryField'dan
				else if(mt!=null){
					fc.set_sourceObjectDetail(mt.get_tableFieldMap().get(-fc.getObjectDetailId()));
				}
			}
			// onay mekanizmasi icin
			if(mt!=null)mam=mt.get_approvalMap();

			break;
		case	3:case	4://db func
			Map<Integer,W5DbFuncParam> fieldMap3 = new HashMap();
			for(W5DbFuncParam tf:(List<W5DbFuncParam>)find("from W5DbFuncParam t where t.dbFuncId=? order by t.tabOrder",f.getForm().getObjectId())){
				fieldMap3.put(tf.getDbFuncParamId(),tf);
			}
			for(W5FormCell fc:f.getForm().get_formCells())if(fc.getObjectDetailId()!=0){
				fc.set_sourceObjectDetail(fieldMap3.get(fc.getObjectDetailId()));
			}
			W5DbFunc dbf= (W5DbFunc)find("from W5DbFunc t where t.dbFuncId=?",f.getForm().getObjectId()).get(0);

		}

//        StringBuilder autoExtraJSConstructor = new StringBuilder();
		for(W5FormCell fc:f.getForm().get_formCells())switch(fc.getControlTip()){
		case	99://grid
			W5GridResult gridResult = new W5GridResult(fc.getLookupQueryId());
			gridResult.setRequestParams(f.getRequestParams());
			gridResult.setScd(f.getScd());

			W5Grid g = null;
			if(FrameworkSetting.preloadWEngine!=0 && (g=FrameworkCache.getGrid(customizationId,fc.getLookupQueryId()))!=null){
				gridResult.setGrid(g);
			} else {
				loadGrid(gridResult);
				g = gridResult.getGrid();
				if(FrameworkSetting.preloadWEngine!=0)FrameworkCache.wGrids.get(g.getCustomizationId()).put(fc.getLookupQueryId(), g);
			}
			fc.set_sourceObjectDetail(g);
			break;
		case 31://code_list
			fc.set_formCellCodeDetailList(find("from W5FormCellCodeDetail t where t.formCellId=? AND t.customizationId=? order by t.tabOrder", fc.getFormCellId(), customizationId));
			break;
				
		}
		
		if(mam!=null && !mam.isEmpty()){ //map of ApprovalManagement
			int maxFirstColumnTabOrder=0;
			for(W5FormCell c:f.getForm().get_formCells())if(c.getFormModuleId()==0 && c.getTabOrder()<1000){
				maxFirstColumnTabOrder++;
			}
			for(short actionTip:mam.keySet()){
				W5FormCell approvalCell = new W5FormCell();
				approvalCell.setTabOrder((short)(990+actionTip));
				approvalCell.setDsc("_approval_step_ids"+actionTip);
				approvalCell.setControlTip((short)15);//low-combo query
				approvalCell.setLookupQueryId(606);//approval steps
				approvalCell.setLookupIncludedParams("xapproval_id="+mam.get(actionTip).getApprovalId());
				approvalCell.setControlWidth((short)250);
				approvalCell.setLocaleMsgKey("approval_status");//mam.get(actionTip).getDsc()
				approvalCell.setInitialSourceTip((short)10);//approvalStates
				//approvalCell.setInitialValue(""+mam.get(actionTip).getApprovalId());//approvalId
				approvalCell.setActiveFlag((short)1);
				f.getForm().get_formCells().add(/*maxFirstColumnTabOrder,*/approvalCell);
			}
		}
    	return	true;
    }
	
	@Override
	public W5FormResult getFormResult(Map<String, Object> scd, int formId,	int action, Map<String,String> requestParams){
		W5FormResult	formResult = new W5FormResult(formId);
		formResult.setScd(scd);
		formResult.setErrorMap(new HashMap());
		formResult.setAction(action);
		formResult.setRequestParams(requestParams);
		formResult.setOutputFields(new HashMap());
		formResult.setPkFields(new HashMap());
		formResult.setOutputMessages(new ArrayList());
		formResult.setExtraFormCells(new ArrayList());
		int customizationId = formResult.isDev() ? 0 : (Integer)scd.get("customizationId");
		W5Form f = null;
		if(FrameworkSetting.preloadWEngine!=0 && (f=FrameworkCache.getForm(customizationId,formId))!=null){
			formResult.setForm(f);
		} else {
			loadForm(formResult);
			f = formResult.getForm();
			if(FrameworkSetting.preloadWEngine!=0){
				/*if(customizationId!=0){
					if((PromisCache.wForms.get(0)==null || PromisCache.wForms.get(0).get(formId)==null)){//0'da yoksa oraya koy
						if(PromisCache.wForms.get(0)==null)PromisCache.wForms.put(0,new HashMap());
						PromisCache.wForms.get(0).put(formId, formResult.getForm());
						PromisCache.wForms.get(customizationId).put(formId, formResult.getForm());
					} else {
						W5Form baseForm = PromisCache.wForms.get(0).get(formId);
						PromisCache.wForms.get(customizationId).put(formId, baseForm.safeEquals(formResult.getForm()) ? baseForm : formResult.getForm());
					}
				} else {
					PromisCache.wForms.get(0).put(formId, formResult.getForm());
				}*/
				FrameworkCache.addForm(customizationId,formResult.getForm());
			}
		}

		
		if(formResult.getForm().get_moduleList()!=null){ // eger baska turlu render edilecekse
			for(W5FormModule m:formResult.getForm().get_moduleList())if(GenericUtil.accessControl(scd, m.getAccessViewTip(), m.getAccessViewRoles(), m.getAccessViewUsers())){//form
				switch(m.getModuleTip()){
				case	5:
					if(formResult.getModuleGridMap()==null)formResult.setModuleGridMap(new HashMap());
					formResult.getModuleGridMap().put(m.getObjectId(),getGridResult(scd, m.getObjectId(), requestParams, true));
					break;
				case	6:
					W5QueryResult queryResult4FormCell = executeQuery(scd, m.getObjectId(), requestParams);
					if(formResult.getFormCellResults()==null)formResult.setFormCellResults(new ArrayList<W5FormCellHelper>(queryResult4FormCell.getData().size()));
					for(Object[] d:queryResult4FormCell.getData()){
						W5FormCellHelper result = GenericUtil.getFormCellResultByQueryRecord(d);
						if(m.getTabOrder()!=0)result.getFormCell().setTabOrder((short)(result.getFormCell().getTabOrder()+m.getTabOrder()));
						formResult.getFormCellResults().add(result);
					}
					break;
				}
			}
		}
		
		return formResult;
	}

	private void logTableRecord(W5FormResult fr, String paramSuffix){
		W5Table t = FrameworkCache.getTable(fr.getScd(), fr.getForm().getObjectId());
		StringBuilder sql = new StringBuilder();
		int userId =(Integer)fr.getScd().get("userId");
		int action = fr.getAction();
		String table = t.getDsc();

    	if(FrameworkSetting.log2tsdb){
    		String schema = FrameworkCache.wProjects.get((String)fr.getScd().get("projectId")).getRdbmsSchema();
    		sql.append("select * from ").append(table).append(" t ");
    		List<Object> whereParams = new ArrayList<Object>(fr.getPkFields().size());
    		
    		if(fr.getPkFields().size() > 0){		
    			sql.append(" where ");
    			boolean b = false;
    			StringBuilder startQL= new StringBuilder();
    			startQL.append(schema).append("_").append(t.getDsc().replace('.', '_'));
    			for(W5TableParam px: t.get_tableParamList()){
    				if(b)sql.append(" AND "); else b=true;
    				sql.append("t.").append(px.getExpressionDsc()).append("=?");
    				whereParams.add(fr.getPkFields().get(px.getDsc()));
    				startQL.append(",").append(px.getExpressionDsc()).append("=").append(fr.getPkFields().get(px.getDsc()));
    			}
    			List<Map> l = executeSQLQuery2Map(sql.toString(), whereParams);
    			if(!GenericUtil.isEmpty(l)){
    				Map m = l.get(0);
        			for(W5TableParam px: t.get_tableParamList())m.remove(px.getExpressionDsc());
        			m.put("_action", fr.getAction());
    				startQL.append(" ").append(GenericUtil.fromMapToInfluxFields(m));
    				LogUtil.logCrud(startQL.toString());
    	//			influxDao.insert(pr, "log_table.."+table, p, l.get(0), null); TODO
    			}
    		}
    		return;
    	}
    /*	
		String logTable = table;
		if(logTable.contains(".")){
			logTable = logTable.substring(logTable.lastIndexOf('.')+1);
		}
		Session session = getCurrentSession();

		sql.append("select count(*)  from information_schema.tables qx where lower(qx.table_name) = '").append(logTable.toLowerCase()).append("' and lower(qx.table_schema) = '").append(FrameworkSetting.crudLogSchema).append("'");
		int count = GenericUtil.uInt(session.createSQLQuery(sql.toString()).uniqueResult());

		sql.setLength(0);
		sql.append(" select nextval('seq_log') ").append(FieldDefinitions.tableFieldName_LogId).append(",")
		.append(userId).append(" ").append(FieldDefinitions.tableFieldName_LogUserId).append(",")
		.append(action).append(" ").append(FieldDefinitions.tableFieldName_LogAction)
		.append(",iwb.fnc_sysdate(").append(fr.getScd().get("customizationId")).append(") ").append(FieldDefinitions.tableFieldName_LogDateTime).append(",t.*");

		sql.append(" from ").append(table).append(" t");
		
		List<Object> whereParams = new ArrayList<Object>(fr.getPkFields().size());
		
		if(fr.getPkFields().size() > 0){		
			sql.append(" where ");
			boolean b = false;
			
			for(W5TableParam p: t.get_tableParamList()){
				if(b)sql.append(" AND "); else b=true;
				sql.append("t.").append(p.getExpressionDsc()).append("=?");
				whereParams.add(fr.getPkFields().get(p.getDsc()));
			}
		}
		
		
		final String flogTable = logTable;
		getCurrentSession().doWork(new Work() {
			@Override
			public void execute(Connection conn) throws SQLException {
				try {
					String createSql = GenericUtil.replaceSql("create table "+FrameworkSetting.crudLogSchema+"."+FrameworkSetting.crudLogTablePrefix+flogTable + " as " + sql.toString(),whereParams);
					if(count==0){
						PreparedStatement s = conn.prepareStatement(createSql);
						s.execute();
						s.close();
					} else {
						Savepoint savepoint = conn.setSavepoint("spx-1");
						PreparedStatement s = conn.prepareStatement("insert into "+FrameworkSetting.crudLogSchema+"."+FrameworkSetting.crudLogTablePrefix+flogTable+ GenericUtil.replaceSql(sql.toString(),whereParams));
						try{
							s.execute();
							s.close();
						} catch(SQLException e){
							if(conn != null && savepoint != null) {
						        conn.rollback(savepoint);
						    }
							
							s = conn.prepareStatement("alter table "+FrameworkSetting.crudLogSchema+"." +FrameworkSetting.crudLogTablePrefix+ flogTable + " rename to lt5_" + fr.getForm().getObjectId() + "_"+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
							s.execute();
							s.close();
							
							s = conn.prepareStatement(createSql);
							s.execute();
							s.close();
						}
						
					}
				} catch(Exception e){
					if(FrameworkSetting.debug)e.printStackTrace();
					throw new IWBException("sql","Form Log",fr.getFormId(),GenericUtil.replaceSql(sql.toString(), whereParams), e.getMessage(), e.getCause());
				}
			}
		});
*/
	}
	
	@Override
	public Object getCustomizedObject(String hql, int objectId, int customizationId, String onErrorMsg){
		List list = find(hql, objectId,customizationId);
		if(list.size()==0){
			if(onErrorMsg==null)return null;
			throw new IWBException("framework",onErrorMsg, objectId,null, "Wrong ID", null);
		}
		else return list.get(0);
	}
	
	@Override
	public W5QueryResult getQueryResult(Map<String, Object> scd, int queryId) {
		W5QueryResult queryResult = new W5QueryResult(queryId);
		queryResult.setScd(scd);
		int customizationId = queryResult.isDev() ? 0 : (Integer)scd.get("customizationId");
		if(FrameworkSetting.preloadWEngine!=0 && FrameworkCache.wQueries.get(queryId)!=null){
			queryResult.setQuery(FrameworkCache.wQueries.get(queryId));
		} else {
			W5Query query = (W5Query)find("from W5Query t where t.queryId=? AND t.customizationId=?", queryResult.getQueryId(), customizationId).get(0); // ozel bir client icin varsa
			queryResult.setQuery(query);
			queryResult.getQuery().set_queryFields(find("from W5QueryField t where t.queryId=? AND t.tabOrder>0 AND t.postProcessTip!=99 AND t.customizationId=? order by t.tabOrder", queryResult.getQueryId(), customizationId));
	    	queryResult.getQuery().set_queryParams(find("from W5QueryParam t where t.queryId=? AND t.customizationId=? order by t.tabOrder", queryResult.getQueryId(), customizationId));

			if(queryResult.getQuery().getShowParentRecordFlag()!=0)for(W5QueryField field:queryResult.getQuery().get_queryFields()){
				if(field.getDsc().equals("table_id"))query.set_tableIdTabOrder(field.getTabOrder());
				if(field.getDsc().equals("table_pk"))query.set_tablePkTabOrder(field.getTabOrder());
			}
	  
			if(FrameworkSetting.preloadWEngine!=0)FrameworkCache.wQueries.put(queryId, query);
		}
		
		switch(queryResult.getQuery().getQuerySourceTip()){
		case	0:case 15:
			if(queryResult.getQuery().getMainTableId()!=0){
				queryResult.setMainTable(FrameworkCache.getTable(customizationId, queryResult.getQuery().getMainTableId()));
			}
			
		}
		return queryResult;
	}
	
	private void prepareLookupTableQuery(W5QueryResult queryResult, StringBuilder sql2, AtomicInteger paramIndex){
		List<Object> preParams = new ArrayList<Object>();
		for(W5QueryField qf:queryResult.getQuery().get_queryFields())if(qf.getPostProcessTip()==12 && qf.getLookupQueryId()!=0  && (queryResult.getQueryColMap()==null || queryResult.getQueryColMap().containsKey(qf.getDsc()))){//queryField'da postProcessTip=lookupTable olanlar saptaniyor
   			W5Table tqf = FrameworkCache.getTable(queryResult.getScd(), qf.getLookupQueryId());
   			if(tqf!=null && tqf.getSummaryRecordSql()!=null){	   				
   				sql2.append(",(select ");
   				
   	    		if(tqf.getSummaryRecordSql().contains("${")){
   	    			Object[] oz = DBUtil.filterExt4SQL(tqf.getSummaryRecordSql(),queryResult.getScd(), queryResult.getRequestParams(), null);
   	    			sql2.append(oz[0]);
					if(oz[1]!=null)preParams.addAll((List)oz[1]);
   	    		} else
   	    			sql2.append(tqf.getSummaryRecordSql());

   				sql2.append(" from ").append(tqf.getDsc()).append(" x where x.").append(tqf.get_tableParamList().get(0).getExpressionDsc()).append("=z.").append(qf.getDsc());

   				if(tqf.get_tableParamList().size()==2 && tqf.get_tableParamList().get(1).getDsc().equals("customizationId")){
	   				sql2.append(" AND x.customization_id=").append(queryResult.getScd().get("customizationId"));
   				}
   				sql2.append(") ").append(qf.getDsc()).append("_qw_ ");
   				W5QueryField field = new W5QueryField();
   				field.setDsc(qf.getDsc()+"_qw_");
   				field.setMainTableFieldId(qf.getMainTableFieldId());
   				if(queryResult.getPostProcessQueryFields()==null)queryResult.setPostProcessQueryFields(new ArrayList());
   				queryResult.getPostProcessQueryFields().add(field);
   			}
   		} else if(qf.getPostProcessTip()==13 && qf.getLookupQueryId()!=0  && (queryResult.getQueryColMap()==null || queryResult.getQueryColMap().containsKey(qf.getDsc()))){//queryField'da postProcessTip=lookupTable olanlar saptaniyor
   			W5Table tqf = FrameworkCache.getTable(queryResult.getScd(), qf.getLookupQueryId());
   			if(tqf!=null && tqf.getSummaryRecordSql()!=null){	   				
   				sql2.append(",(select ");
   				
   	    		if(tqf.getSummaryRecordSql().contains("${")){
   	    			Object[] oz = DBUtil.filterExt4SQL(tqf.getSummaryRecordSql(),queryResult.getScd(), queryResult.getRequestParams(), null);
   	    			sql2.append("string_agg(").append(oz[0]).append(",',')");
					if(oz[1]!=null)preParams.addAll((List)oz[1]);
   	    		} else
   	    			sql2.append("string_agg(").append(tqf.getSummaryRecordSql()).append(",',')");

   				sql2.append(" from ").append(tqf.getDsc()).append(" x where x.").append(tqf.get_tableParamList().get(0).getExpressionDsc()).append(" in (select q.satir::int from iwb.tool_parse_numbers(z.").append(qf.getDsc()).append(",',') q)");

   				if(tqf.get_tableParamList().size()==2 && tqf.get_tableParamList().get(1).getDsc().equals("customizationId")){
	   				sql2.append(" AND x.customization_id=").append(queryResult.getScd().get("customizationId"));
   				}
   				sql2.append(") ").append(qf.getDsc()).append("_qw_ ");
   				W5QueryField field = new W5QueryField();
   				field.setDsc(qf.getDsc()+"_qw_");
   				field.setMainTableFieldId(qf.getMainTableFieldId());
   				if(queryResult.getPostProcessQueryFields()==null)queryResult.setPostProcessQueryFields(new ArrayList());
   				queryResult.getPostProcessQueryFields().add(field);
   			}
   		}
		if(preParams.size()>0)queryResult.getSqlParams().addAll(paramIndex.intValue(), preParams);
	}

	@Override
	public void runQuery(W5QueryResult queryResult) {

		W5Query query = queryResult.getQuery();
		Log5QueryAction queryAction = new Log5QueryAction(queryResult);
		
    	
//    	String sql = null;
		W5Table mainTable = queryResult.getMainTable();
		StringBuilder sql2 = new StringBuilder();
		AtomicInteger paramIndex = new AtomicInteger(0);
		String error = null;

		try {
			
			getCurrentSession().doWork(new Work() {
				
				@Override
				public void execute(Connection conn) throws SQLException {
					PreparedStatement s = null;
					ResultSet rs = null;
					Set<String> errorFieldSet = new HashSet();
					if(queryResult.getFetchRowCount()!=0){
						if(false && !GenericUtil.isEmpty(queryResult.getExecutedSqlFrom())){
							s = conn.prepareStatement("select count(1) " + queryResult.getExecutedSqlFrom());
				        	applyParameters(s, queryResult.getExecutedSqlFromParams());
						} else {
							s = conn.prepareStatement("select count(1) from (" + queryResult.getExecutedSql() + " ) x");
				        	applyParameters(s, queryResult.getSqlParams());
						}
			        	rs = s.executeQuery();
			        	rs.next();
			        	
						int resultRowCount = rs.getBigDecimal(1).intValue();
						rs.close();
						s.close();
						if(resultRowCount<queryResult.getStartRowNumber()){
							queryResult.setStartRowNumber(0);
						}
						queryResult.setResultRowCount(resultRowCount);
						if(resultRowCount<queryResult.getStartRowNumber()+queryResult.getFetchRowCount()){
							queryResult.setFetchRowCount((int)(resultRowCount - queryResult.getStartRowNumber()));
						}
			
						queryResult.getSqlParams().add(queryResult.getFetchRowCount());//queryResult.getStartRowNumber()+
						if(queryResult.getStartRowNumber()>0)queryResult.getSqlParams().add(queryResult.getStartRowNumber());
						
						sql2.append("select z.*");
						if(query.getSqlPostSelect()!=null && query.getSqlPostSelect().trim().length()>2){				
							if(query.getSqlPostSelect().contains("${")){
								Object[] oz = DBUtil.filterExt4SQL(query.getSqlPostSelect(), queryResult.getScd(), queryResult.getRequestParams(), null);
								sql2.append(", ").append(oz[0]);
								if(oz[1]!=null){
									queryResult.getSqlParams().addAll(0,(List)oz[1]);
									paramIndex.getAndAdd(((List)oz[1]).size());
								}
								
							} else
								sql2.append(", ").append(query.getSqlPostSelect());
							sql2.append(" ");
						}
				   		if(queryResult.getPostProcessQueryFields()!=null && mainTable!=null && queryResult.getViewLogModeTip()==0){
				   			addPostQueryFields(queryResult, sql2, paramIndex);
				   		}

				   		for(W5QueryField qf:queryResult.getQuery().get_queryFields())if((qf.getPostProcessTip()==12 || qf.getPostProcessTip()==13 ) && qf.getLookupQueryId()!=0){//queryField'da postProcessTip=lookupTable olanlar saptaniyor
				   			prepareLookupTableQuery(queryResult, sql2, paramIndex);
				   			break;
				   		}
			   			for(W5QueryField qf:queryResult.getQuery().get_queryFields())if((qf.getPostProcessTip()==16 || qf.getPostProcessTip()==17) && qf.getLookupQueryId()!=0){//queryField'da postProcessTip=lookupQuery olanlar saptaniyor
							W5QueryResult queryFieldLookupQueryResult = getQueryResult(queryResult.getScd(), qf.getLookupQueryId());
							if(queryFieldLookupQueryResult!=null && queryFieldLookupQueryResult.getQuery()!=null){
				   				W5QueryField field = new W5QueryField();
				   				field.setDsc(qf.getDsc()+"_qw_");errorFieldSet.add(field.getDsc());
				   				field.setMainTableFieldId(qf.getMainTableFieldId());
				   				if(queryResult.getPostProcessQueryFields()==null)queryResult.setPostProcessQueryFields(new ArrayList());
				   				queryResult.getPostProcessQueryFields().add(field);
				   				if(qf.getPostProcessTip()==16 && queryFieldLookupQueryResult.getQuery().get_queryFields().size()>2)for(int qi=2;qi<queryFieldLookupQueryResult.getQuery().get_queryFields().size();qi++){
				   					W5QueryField qf2 = queryFieldLookupQueryResult.getQuery().get_queryFields().get(qi);
					   				field = new W5QueryField();
					   				field.setDsc(qf.getDsc()+"__"+qf2.getDsc());errorFieldSet.add(field.getDsc());
					   				field.setMainTableFieldId(qf2.getMainTableFieldId());
					   				queryResult.getPostProcessQueryFields().add(field);
				   				}
							}
			   			}

						sql2.append("from (").append(queryResult.getExecutedSql()).append(" limit ?");
						if(queryResult.getStartRowNumber()>0){
							sql2.append(" offset ?");
						}
						sql2.append(") z");

					} else {
						if(queryResult.getPostProcessQueryFields()!=null && mainTable!=null && queryResult.getViewLogModeTip()==0){
							sql2.append("select z.*");//					
							if(query.getSqlPostSelect()!=null && query.getSqlPostSelect().trim().length()>2){
								if(query.getSqlPostSelect().contains("${")){
									Object[] oz = DBUtil.filterExt4SQL(query.getSqlPostSelect(), queryResult.getScd(), queryResult.getRequestParams(), null);
									sql2.append(", ").append(oz[0]);
									if(oz[1]!=null){
										queryResult.getSqlParams().addAll(0,(List)oz[1]);
										paramIndex.getAndAdd(((List)oz[1]).size());
									}
								} else
									sql2.append(", ").append(query.getSqlPostSelect());
								if(queryResult.getQueryColMap()!=null)for(W5QueryField qf:queryResult.getQuery().get_queryFields())if(qf.getPostProcessTip()==99){
									queryResult.getQueryColMap().put(qf.getDsc(), qf);							
								}
							}
				   			addPostQueryFields(queryResult, sql2, paramIndex);

				   			for(W5QueryField qf:queryResult.getQuery().get_queryFields())if((qf.getPostProcessTip()==12 || qf.getPostProcessTip()==13) && qf.getLookupQueryId()!=0){//queryField'da postProcessTip=lookupTable olanlar saptaniyor
				   				prepareLookupTableQuery(queryResult, sql2, paramIndex);		
					   			break;
				   			}

				   			for(W5QueryField qf:queryResult.getQuery().get_queryFields())if((qf.getPostProcessTip()==16 || qf.getPostProcessTip()==17) && qf.getLookupQueryId()!=0){//queryField'da postProcessTip=lookupQuery olanlar saptaniyor
								W5QueryResult queryFieldLookupQueryResult = getQueryResult(queryResult.getScd(), qf.getLookupQueryId());
								if(queryFieldLookupQueryResult!=null && queryFieldLookupQueryResult.getQuery()!=null){
					   				W5QueryField field = new W5QueryField();
					   				field.setDsc(qf.getDsc()+"_qw_");errorFieldSet.add(field.getDsc());
					   				field.setMainTableFieldId(qf.getMainTableFieldId());
					   				if(queryResult.getPostProcessQueryFields()==null)queryResult.setPostProcessQueryFields(new ArrayList());
					   				queryResult.getPostProcessQueryFields().add(field);
					   				if(qf.getPostProcessTip()==16 && queryFieldLookupQueryResult.getQuery().get_queryFields().size()>2)for(int qi=2;qi<queryFieldLookupQueryResult.getQuery().get_queryFields().size();qi++){
					   					W5QueryField qf2 = queryFieldLookupQueryResult.getQuery().get_queryFields().get(qi);
						   				field = new W5QueryField();
						   				field.setDsc(qf.getDsc()+"__"+qf2.getDsc());errorFieldSet.add(field.getDsc());
						   				field.setMainTableFieldId(qf2.getMainTableFieldId());
						   				queryResult.getPostProcessQueryFields().add(field);
					   				}
								}
				   			}
				   			
							sql2.append(" from (").append(queryResult.getExecutedSql()).append(" ) z");

						} else {
							sql2.append("select z.*");
							if(query.getSqlPostSelect()!=null && query.getSqlPostSelect().trim().length()>2){
								if(query.getSqlPostSelect().contains("${")){
									Object[] oz = DBUtil.filterExt4SQL(query.getSqlPostSelect(), queryResult.getScd(), queryResult.getRequestParams(), null);
									sql2.append(", ").append(oz[0]);
									if(oz[1]!=null)queryResult.getSqlParams().addAll(0,(List)oz[1]);
								} else
									sql2.append(", ").append(query.getSqlPostSelect());
							}
							for(W5QueryField qf:queryResult.getQuery().get_queryFields())if(qf.getPostProcessTip()==12 && qf.getLookupQueryId()!=0){//queryField'da postProcessTip=lookupTable olanlar saptaniyor				   			
				   				prepareLookupTableQuery(queryResult, sql2, paramIndex);
					   			break;
				   			}
				   			for(W5QueryField qf:queryResult.getQuery().get_queryFields())if((qf.getPostProcessTip()==16) && qf.getLookupQueryId()!=0){//queryField'da postProcessTip=lookupQuery olanlar saptaniyor
								W5QueryResult queryFieldLookupQueryResult = getQueryResult(queryResult.getScd(), qf.getLookupQueryId());
								if(queryFieldLookupQueryResult!=null && queryFieldLookupQueryResult.getQuery()!=null){
					   				W5QueryField field = new W5QueryField();
					   				field.setDsc(qf.getDsc()+"_qw_");errorFieldSet.add(field.getDsc());
					   				field.setMainTableFieldId(qf.getMainTableFieldId());
					   				if(queryResult.getPostProcessQueryFields()==null)queryResult.setPostProcessQueryFields(new ArrayList());
					   				queryResult.getPostProcessQueryFields().add(field);
					   				if(qf.getPostProcessTip()==16 && queryFieldLookupQueryResult.getQuery().get_queryFields().size()>2)for(int qi=2;qi<queryFieldLookupQueryResult.getQuery().get_queryFields().size();qi++){
					   					W5QueryField qf2 = queryFieldLookupQueryResult.getQuery().get_queryFields().get(qi);
						   				field = new W5QueryField();
						   				field.setDsc(qf.getDsc()+"__"+qf2.getDsc());errorFieldSet.add(field.getDsc());
						   				field.setMainTableFieldId(qf2.getMainTableFieldId());
						   				queryResult.getPostProcessQueryFields().add(field);
					   				}
								}
				   			}

							sql2.append(" from (").append(queryResult.getExecutedSql()).append(" ) z");
						}
					}


			    	List<Object[]> resultData = queryResult.getFetchRowCount()==0 ? new ArrayList<Object[]>():new ArrayList<Object[]>(queryResult.getFetchRowCount());
//					sql = sql2.toString();
		        	s = conn.prepareStatement(sql2.toString());
		        	applyParameters(s, queryResult.getSqlParams());
					queryResult.setExecutedSql(sql2.toString());
//					if(PromisSetting.debug)logger.info(PromisUtil.replaceSql(sql2.toString(),queryResult.getSqlParams()));
					rs = s.executeQuery();
					int	maxTabOrder = 0;
					Set liveSyncKeys = null;
					if(FrameworkSetting.liveSyncRecord && mainTable!=null && mainTable.getLiveSyncFlag()!=0 && queryResult.getRequestParams()!=null && queryResult.getScd()!=null && queryResult.getRequestParams().containsKey(".t") && queryResult.getRequestParams().containsKey(".w")){
						int	 grdOrFcId = GenericUtil.uInt(queryResult.getRequestParams().get("_gid"));
						if(grdOrFcId==0)grdOrFcId = -GenericUtil.uInt(queryResult.getRequestParams().get("_fdid"));
						if(grdOrFcId!=0){
							boolean mobile = GenericUtil.uInt(queryResult.getScd().get("mobile"))!=0;
							String sessionId =  mobile ? (String)queryResult.getScd().get("mobileDeviceId"):(String)queryResult.getScd().get("sessionId");
							liveSyncKeys = UserUtil.getTableGridFormCellCachedKeys((Integer)queryResult.getScd().get("customizationId"), mainTable.getTableId(), (Integer)queryResult.getScd().get("userId"), sessionId, 
									mobile ? sessionId : queryResult.getRequestParams().get(".w"),queryResult.getRequestParams().get(".t"), grdOrFcId, queryResult.getRequestParams(), true);
//							if(liveSyncKeys!=null)liveSyncKeys.clear(); !queryResult.getScd().containsKey("mobile") ||
						}
					}
					List<W5QueryField> newQueryFields = null;
					while(rs.next()/* && (maxFetchedCount==0 || totalFetchedCount<maxFetchedCount )*/){
						if(newQueryFields==null){
							newQueryFields = new ArrayList(queryResult.getQuery().get_queryFields().size()+ (queryResult.getPostProcessQueryFields()!=null ? queryResult.getPostProcessQueryFields().size():0));
							if(queryResult.getQueryColMap()!=null){
								for(W5QueryField qf : queryResult.getQuery().get_queryFields())if(queryResult.getQueryColMap().containsKey(qf.getDsc())){
									newQueryFields.add(qf);
			    					if(maxTabOrder<qf.getTabOrder())maxTabOrder=qf.getTabOrder();
								}
							} else {
								for(W5QueryField qf : queryResult.getQuery().get_queryFields())if(qf.getTabOrder()>0){
									W5TableField tf = queryResult.getMainTable()!=null && qf.getMainTableFieldId()>0 ? queryResult.getMainTable().get_tableFieldMap().get(qf.getMainTableFieldId()) : null;
				    				if(tf==null || 
											(
											(tf.getAccessViewUserFields()!=null || GenericUtil.accessControl(queryResult.getScd(), tf.getAccessViewTip(), tf.getAccessViewRoles(),tf.getAccessViewUsers())))){//access control
				    					newQueryFields.add(qf);
				    					if(maxTabOrder<qf.getTabOrder())maxTabOrder=qf.getTabOrder();
				    				}
								}
							}
						
							//post process fields:comment, file attachment, access_control, approval
							
							if(queryResult.getPostProcessQueryFields()!=null){
								for(W5QueryField qf:queryResult.getPostProcessQueryFields()){
									qf.setTabOrder((short)(++maxTabOrder));
									newQueryFields.add(qf);
								}
							}
							if(queryResult.getQuery().getShowParentRecordFlag()!=0)maxTabOrder++;
							
							queryResult.setNewQueryFields(newQueryFields);
						}
						
						Object[] o = new Object[maxTabOrder];
						for(W5QueryField field : newQueryFields)if(!errorFieldSet.contains(field.getDsc()))try{
							Object obj = rs.getObject(field.getDsc());
							if(obj!=null){					
								if (obj instanceof java.sql.Timestamp) {
			        				try{ obj = (queryResult.getQuery().getQueryTip() == 2 && field.getFieldTip() == 2) ? 
			        						 (java.sql.Timestamp) obj : GenericUtil.uFormatDateTime((java.sql.Timestamp) obj);
			        				}catch (Exception e) {obj="java.sql.Timestamp";}
								} else if(obj instanceof java.sql.Date){
									try{ obj = (queryResult.getQuery().getQueryTip() == 2 && field.getFieldTip() == 2) ? 
											rs.getTimestamp(field.getDsc()) : GenericUtil.uFormatDateTime(rs.getTimestamp(field.getDsc()));
									} catch (Exception e) {obj="java.sql.Date";}
								}
							}
		   					o[field.getTabOrder()-1] = obj;
						} catch(Exception ez){
							if(FrameworkSetting.debug)throw ez;
							errorFieldSet.add(field.getDsc());
						} 
						if(query.getDataFillDirectionTip()!=0)
							resultData.add(0,o);
						else
							resultData.add(o);
						if(liveSyncKeys!=null)liveSyncKeys.add(GenericUtil.uInt(o[query.getQueryTip()==3 ? 1:0]));
					}
					if(queryResult.getFetchRowCount()==0 && resultData!=null){
						queryResult.setResultRowCount(resultData.size());
					}
					queryResult.setData(resultData);
					
					
					
					
					if(rs!=null)rs.close();
					if(s!=null)s.close();
					if(FrameworkSetting.hibernateCloseAfterWork)if(conn!=null)conn.close();
				}
			});
    	} catch(IWBException pe){
    		error = pe.getMessage();
    		throw pe;
		} catch(Exception e){
			if(FrameworkSetting.debug)e.printStackTrace();
			if(e.getCause()!=null && e.getCause() instanceof SQLException){
	    		error = ((SQLException)(e.getCause())).getLocalizedMessage();
				throw new IWBException("sql","Query Execute",queryResult.getQueryId(),GenericUtil.replaceSql(sql2.length()==0 ? queryResult.getExecutedSql() : sql2.toString(),queryResult.getSqlParams()), ((SQLException)(e.getCause())).getLocalizedMessage(), e.getCause());
			}
    		error = e.getMessage();
			throw new IWBException("sql","Query Execute",queryResult.getQueryId(),GenericUtil.replaceSql(sql2.length()==0 ? queryResult.getExecutedSql() : sql2.toString(),queryResult.getSqlParams()), e.getMessage(), e.getCause());
		} finally {
			logQueryAction(queryAction, queryResult, error);
		}
	}
	
	@Override
	public Map<String, Object> runSQLQuery2Map(String code, Map<String, Object> scd, Map<String, String> requestParams, Map<String, Object> obj) {
		Object[] oz = DBUtil.filterExt4SQL(code, scd, requestParams, obj);
		return runSQLQuery2Map(oz[0].toString(),(List)oz[1],null);
	}
	@Override
	public Map<String, Object> runSQLQuery2Map(String sql,List params, List<W5QueryField> queryFields) {
		return runSQLQuery2Map(sql, params, queryFields, true);
	}
	@Override
	public List runQuery2Map(Map<String, Object> scd,int queryId,	Map<String,String> requestParams) {
		W5QueryResult queryResult = getQueryResult(scd, queryId);
		if(queryId!=1 && queryId!=824 && queryResult.getMainTable()!=null && (!FrameworkSetting.debug || (scd.get("roleId")!=null && GenericUtil.uInt(scd.get("roleId"))!=0))){
			W5Table t = queryResult.getMainTable();
			if(t.getAccessViewTip()==0 && !FrameworkCache.roleAccessControl(scd,  0)){
				throw new IWBException("security","Module", 0, null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_guvenlik_modul_kontrol"), null);
			}
			if(t.getAccessViewUserFields()==null && !GenericUtil.accessControl(scd, t.getAccessViewTip(), t.getAccessViewRoles(), t.getAccessViewUsers())){
				throw new IWBException("security","Query", queryId, null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_guvenlik_tablo_kontrol_goruntuleme"), null);
			}
			
		}
/*		StringBuilder tmpx = new StringBuilder("ali baba ${obj.dsc} ve 40 haramiler ${lnk.pk_query_field_id.dsc} olmus");
		dao.interprateTemplate(scd, 5,1294, tmpx, true); */
		queryResult.setErrorMap(new HashMap());
		queryResult.setRequestParams(requestParams);
		queryResult.setViewLogModeTip((short)GenericUtil.uInt(requestParams,"_vlm"));
		
//		queryResult.setOrderBy(PromisUtil.uStrNvl(requestParams.get(PromisUtil.uStrNvl(PromisSetting.appSettings.get("sql_sort"),"sort")), queryResult.getQuery().getSqlOrderby()));
		if(!GenericUtil.isEmpty(requestParams.get("sort"))){
			if (requestParams.get("sort").equals(FieldDefinitions.queryFieldName_Comment)){
			//	queryResult.setOrderBy("coalesce((select qz.last_comment_id from iwb.w5_comment_summary qz where qz.table_id= AND qz.table_pk= AND qz.customization_id=),0) DESC");
				queryResult.setOrderBy(FieldDefinitions.queryFieldName_Comment);//+ " " + requestParams.get("dir")
			} else if (!requestParams.get("sort").contains("_qw_")){
				queryResult.setOrderBy(requestParams.get("sort"));						
				if(requestParams.get("dir")!=null){
					if(queryResult.getMainTable()!=null)for(W5QueryField f:queryResult.getQuery().get_queryFields())if(queryResult.getOrderBy().equals(f.getDsc())){
						if(f.getMainTableFieldId()!=0 && queryResult.getMainTable().get_tableFieldMap().containsKey(f.getMainTableFieldId())){
							queryResult.setOrderBy("x."+queryResult.getOrderBy());
						}
						break;
					}
//					queryResult.setOrderBy(((!queryResult.getQuery().getSqlFrom().contains(",") && !queryResult.getQuery().getSqlFrom().contains("join") && queryResult.getQuery().getSqlFrom().contains(" x")) ? "x." : "") + queryResult.getOrderBy() + " " + requestParams.get("dir"));
					queryResult.setOrderBy(queryResult.getOrderBy() + " " + requestParams.get("dir"));
				}
			}else
				queryResult.setOrderBy(queryResult.getQuery().getSqlOrderby());	
		} else
			queryResult.setOrderBy(queryResult.getQuery().getSqlOrderby());		
		switch(queryResult.getQuery().getQueryTip()){
		case	9:case	10:queryResult.prepareTreeQuery(null);break;
		case	15:queryResult.prepareDataViewQuery(null);break;
		default:queryResult.prepareQuery(null);
		}
		List l = null;
		if(queryResult.getErrorMap().isEmpty()){
			QueryTrigger.beforeExecuteQuery(queryResult, this);
	        queryResult.setFetchRowCount(GenericUtil.uIntNvl(requestParams, "limit", GenericUtil.uInt(requestParams,"firstLimit")));
	        queryResult.setStartRowNumber(GenericUtil.uInt(requestParams,"start"));
        	l = executeSQLQuery2Map(queryResult.getExecutedSql(), queryResult.getSqlParams());
	    	QueryTrigger.afterExecuteQuery(queryResult, this);
		}
		
		return l;
	}
	@Override
	public Map<String, Object> runSQLQuery2Map(String sql,List params, List<W5QueryField> queryFields, boolean closeConnectionAfterRun) {
		try {
			return getCurrentSession().doReturningWork(new ReturningWork<Map<String, Object>>() {
				@Override
				public Map<String, Object> execute(Connection conn) throws SQLException {
					PreparedStatement s = conn.prepareStatement("select x.* from (" + sql + ")  x  limit 1");
					if(params!=null)applyParameters(s, params);
					ResultSet rs = s.executeQuery();
					Map<String, Object>result =null;
					if(rs.next()){
						result = new HashMap<String, Object>();
						if(queryFields==null || queryFields.size()==0){
							ResultSetMetaData rsm = rs.getMetaData();
							for(int columnIndex=1;columnIndex<=rsm.getColumnCount();columnIndex++){
								String columnName = rsm.getColumnName(columnIndex).toLowerCase(FrameworkSetting.appLocale);
								Object obj =rs.getObject(columnIndex);
								if(obj==null)continue;
								if (obj instanceof java.sql.Timestamp) {
			        				try{ result.put(columnName, GenericUtil.uFormatDateTime((java.sql.Timestamp) obj));
			        				}catch (Exception e) {}
								} 
								else if (obj instanceof java.sql.Date) {
									try{ 
										obj = rs.getTimestamp(columnIndex);
										result.put(columnName, GenericUtil.uFormatDateTime((java.sql.Timestamp) obj));
									}catch (Exception e) {
										obj =rs.getObject(columnIndex);
				        				try{ result.put(columnName, GenericUtil.uFormatDateSade((java.sql.Date) obj));
				        				}catch (Exception e2) {}
									}
								}
			        			 else result.put(columnName, obj);
							}
						} else for(W5QueryField f:queryFields){
							Object obj =rs.getObject(f.getDsc());
							if(obj==null)continue;
							String columnName = f.getDsc();
							if (obj instanceof java.sql.Timestamp) {
		        				try{ result.put(columnName, GenericUtil.uFormatDateTimeSade((java.sql.Timestamp) obj));
		        				}catch (Exception e) {}
							} else if (obj instanceof java.sql.Date) {
		        				try{ result.put(columnName, GenericUtil.uFormatDateSade((java.sql.Date) obj));
		        				}catch (Exception e) {}
							}
							else {
								Object o = GenericUtil.getObjectByTip(obj.toString(),f.getFieldTip());
								switch(f.getPostProcessTip()){
								case	9://_fieldName
									result.put("_"+columnName, o);
									break;
								case	2://locale
									result.put(columnName, LocaleMsgCache.get2(0,FrameworkCache.getAppSettingStringValue(0, "locale"), (String)o));//TODO. burasi scd'deki degerler olacak
									break;
								default:result.put(columnName, o);
								}
								
							}
						}
					}
					if(rs!=null)rs.close();
					if(s!=null)s.close();
					if(FrameworkSetting.hibernateCloseAfterWork)if(closeConnectionAfterRun && conn!=null)conn.close();
					return result;
				}
			});
			//s = conn.prepareStatement("select x.* from ("+sql+") x where rownum=1");
        	
		} catch(Exception e){
			if(FrameworkSetting.debug)e.printStackTrace();
//			logException(PromisUtil.replaceSql(sql, params)+"\n"+ e.getMessage(),PromisUtil.uInt(PromisCache.appSettings.get(0).get("default_customization_id")),0);
			throw new IWBException("sql","Manuel Query Execute2Map",0, GenericUtil.replaceSql(sql, params), e.getMessage(), e.getCause());	
		} 
		
	}
	
    private void logQueryAction(Log5QueryAction action, W5QueryResult queryResult, String error){
    	if(queryResult.getQuery()==null || !FrameworkSetting.logQueryAction)return;
    	action.calcProcessTime();

		queryResult.setProcessTime(action.getProcessTime());
    	if(queryResult.getQuery().getLogLevelTip()==2 ||
    			(queryResult.getQuery().getLogLevelTip()==1 && FrameworkSetting.logQueryActionMinTime<=action.getProcessTime())){
        	if(FrameworkSetting.log2tsdb){
				action.setDsc(GenericUtil.replaceSql(queryResult.getExecutedSql(), queryResult.getSqlParams()));
        	} else {
				action.setDsc(GenericUtil.uStrMax(GenericUtil.replaceSql(queryResult.getExecutedSql(), queryResult.getSqlParams()), 3999));
        	}
			saveObject(action);
    	}
    }
	
    private void logDbFuncAction(Log5DbFuncAction action, W5DbFuncResult dbFuncResult, String error){
    	if(dbFuncResult.getDbFunc().getLogLevelTip()==0 || FrameworkCache.getAppSettingIntValue(dbFuncResult.getScd(),"log_db_func_action")==0)return;
    	action.calcProcessTime();
    	if((dbFuncResult.getDbFunc().getLogLevelTip()==1) ||
    			(dbFuncResult.getDbFunc().getLogLevelTip()==2 && FrameworkCache.getAppSettingIntValue(dbFuncResult.getScd(),"log_db_func_action")!=0 && FrameworkCache.getAppSettingIntValue(dbFuncResult.getScd(), "log_db_func_action_mintime")<=action.getProcessTime())){
			action.setDsc(GenericUtil.replaceSql(dbFuncResult.getExecutedSql(), dbFuncResult.getSqlParams()));
			saveObject(action);
    	}
    }
	
	@Override
	public void loadFormCellLookups(Map<String, Object> scd, List<W5FormCellHelper> formCellResults, Map<String, String> requestParams, String tabId) {
		String	includedValues;
		int customizationId = (Integer)scd.get("customizationId");
		W5Customization cus = FrameworkCache.wCustomizationMap.get(customizationId);
				
    	for(W5FormCellHelper rc : formCellResults)try{
    		W5FormCell c = rc.getFormCell();
			includedValues = c.getLookupIncludedValues();
	    	Map<String, String> paramMap = new HashMap<String, String>();
			Set<Integer> keys = null;
    		switch(c.getControlTip()){
    		case	100://button
    			if(c.getInitialSourceTip()==4){ //sql ise
    				rc.setExtraValuesMap(runSQLQuery2Map(GenericUtil.filterExt(c.getInitialValue(), scd, requestParams, null).toString(), null, null));
    			}
    			break;
    		case	60://remote superboxselect	
    		case	16://remote query
    		case	9://remote query
    			rc.setLookupQueryResult(getQueryResult(scd,c.getLookupQueryId()));
    			//c.set_lookupListCount(c.getLookupQueryId()); // Fake: Normalde Query Id tutulur, ama su anda kac adet column tutuyor
    			break;
    		
    		case 	58: // superboxselect
    		case    8://lovcombo static
    		case	6: //eger static combobox ise listeyi load et
				W5LookUp lookUp = FrameworkCache.getLookUp(scd,c.getLookupQueryId(), "Form("+c.getFormId()+")."+c.getDsc()+"-> LookUp not found: "+c.getLookupQueryId());
				rc.setLocaleMsgFlag((short)1);
				List<W5LookUpDetay> oldList = !FrameworkCache.reloadCacheQueue.containsKey("3-"+customizationId) ? lookUp.get_detayList() : (List<W5LookUpDetay>)find("from W5LookUpDetay t where t.customizationId=? AND t.lookUpId=? order by t.tabOrder", customizationId, c.getLookupQueryId());

				List<W5LookUpDetay> newList = null;
				if(includedValues!=null && includedValues.length()>0){
    				boolean notInFlag = false;
    				if(includedValues.charAt(0)=='!'){
    					notInFlag = true;
    					includedValues = includedValues.substring(1);
    				}
    				String[]	ar1 = includedValues.split(",");
    				newList = new ArrayList<W5LookUpDetay>(oldList.size());
    				for(W5LookUpDetay p :oldList)if((rc.getValue()!=null && p.getVal().equals(rc.getValue())) || p.getActiveFlag()!=0){
    					boolean in = false;
        				for(int it4=0;it4<ar1.length;it4++)if(ar1[it4].equals(p.getVal())){
    						in = true;
    						break;
    					}
        				if(in ^ notInFlag)
        					newList.add(p);
    				}
    			} else if(requestParams.get("_lsc"+c.getFormCellId())!=null){
    				String[] lsc = requestParams.get("_lsc"+c.getFormCellId()).split(",");
    				newList = new ArrayList<W5LookUpDetay>();
    				for(String q:lsc){ 
    					newList.add(lookUp.get_detayMap().get(q));
    				}
    			} else {
    				newList = new ArrayList<W5LookUpDetay>(oldList.size());
    				for(W5LookUpDetay p :oldList)if((rc.getValue()!=null && p.getVal().equals(rc.getValue())) || p.getActiveFlag()!=0)newList.add(p);
//    				newList = lookUp.get_detayList();
    			}
				List<W5LookUpDetay> newList2 = new ArrayList<W5LookUpDetay>(newList.size());
				if(tabId!=null)keys = UserUtil.getTableGridFormCellCachedKeys((Integer)scd.get("customizationId"), -c.getLookupQueryId(), (Integer)scd.get("userId"),(String)scd.get("sessionId"), 
						requestParams.get(".w"),tabId, -c.getFormCellId(), requestParams, false);
				for(W5LookUpDetay ld:newList){
					newList2.add(ld);
				}
				rc.setLookupListValues(newList2);
    			break;
    		case	10: case 61: // advanced select, advancedselect w/ button
    			paramMap.put("xid", rc.getValue());
    		case	7: case 15: case 59: // dynamic query, lovcombo, superbox
    		case	23:   case 24: case 26: case	55://tree combo and treepanel
    			String	includedParams = GenericUtil.filterExt(c.getLookupIncludedParams(), scd, requestParams, null).toString();
    			if(includedParams!=null && includedParams.length()>2){
    				String[]	ar1 = includedParams.split("&");
    				for(int it4=0;it4<ar1.length;it4++){
    					String[]	ar2=ar1[it4].split("=");
    					if(ar2.length==2 && ar2[0]!=null && ar2[1]!=null)paramMap.put(ar2[0], ar2[1]);
    				}
    			}
    			
    			W5QueryResult lookupQueryResult = getQueryResult(scd,c.getLookupQueryId());
    			lookupQueryResult.setErrorMap(new HashMap());
    			lookupQueryResult.setRequestParams(requestParams);
    			lookupQueryResult.setOrderBy(lookupQueryResult.getQuery().getSqlOrderby());
    			if(lookupQueryResult.getQuery().getQuerySourceTip()!=15)switch(lookupQueryResult.getQuery().getQuerySourceTip()){
    			case	1376://WS Method
    				W5WsMethod wsm = FrameworkCache.wWsMethods.get(lookupQueryResult.getQuery().getMainTableId());
    				if(wsm.get_params()==null){
    					wsm.set_params(find("from W5WsMethodParam t where t.wsMethodId=? AND t.customizationId=? order by t.tabOrder", wsm.getWsMethodId(), (Integer)scd.get("customizationId")));
    					wsm.set_paramMap(new HashMap());
    					for(W5WsMethodParam wsmp:wsm.get_params())
    						wsm.get_paramMap().put(wsmp.getWsMethodParamId(), wsmp);
    				}
    				W5WsMethodParam parentParam = null;
    				for(W5WsMethodParam px:wsm.get_params())if(px.getOutFlag()!=0 && px.getParamTip()==10){parentParam=px;break;}
    				Map<String, String> m2 = new HashMap();
    				if(requestParams.get("filter[value]")!=null){
    					requestParams.put("xdsc",requestParams.get("filter[value]"));
    					requestParams.remove("filter[value]");
    				}
    				for(W5QueryParam qp:lookupQueryResult.getQuery().get_queryParams())if(!GenericUtil.isEmpty(requestParams.get(qp.getDsc()))){
    					m2.put(qp.getExpressionDsc(), requestParams.get(qp.getDsc()));
    				}
    				StringBuilder rc2 = new StringBuilder();
    				rc2.append("function _x_(x){\nreturn {").append(lookupQueryResult.getQuery().getSqlSelect()).append("\n}}\nvar result=[], q=$iwb.callWs('")
    				  .append(wsm.get_ws().getDsc()+"."+wsm.getDsc()).append("',")
    				  .append(GenericUtil.fromMapToJsonString2(m2)).append(");\nif(q && q.get('success')){q=q.get('").append(parentParam.getDsc()).append("');for(var i=0;i<q.size();i++)result.push(_x_(q.get(i)));}");
    				executeQueryAsRhino(lookupQueryResult, rc2.toString());
    				rc.setLookupQueryResult(lookupQueryResult);  
    				continue;
    			default:
    				rc.setLookupQueryResult(lookupQueryResult);    	
    				continue;//burda sadece table icin olur
    			}
    			if(rc.getValue()!=null && rc.getValue().length()>0 && GenericUtil.hasPartInside("7,10,61", Short.toString(c.getControlTip())))paramMap.put("pmust_load_id", rc.getValue());
    			switch(lookupQueryResult.getQuery().getQueryTip()){
    			case	12:lookupQueryResult.prepareTreeQuery(paramMap);break;//lookup tree query
    			default:lookupQueryResult.prepareQuery(paramMap);
    			}
    	    	rc.setLookupQueryResult(lookupQueryResult);    			
    			if(c.getControlTip()==10 || c.getControlTip()==23 || c.getControlTip()==7){
    				if(c.getDialogGridId()!=0){
    					if(rc.getExtraValuesMap()==null)rc.setExtraValuesMap(new HashMap());
    					rc.getExtraValuesMap().put("dialogGrid", getGridResult(scd, c.getDialogGridId(), requestParams, true));
    				}
    					
    				if(c.getControlTip()==10 && GenericUtil.isEmpty(rc.getValue()))break;//advanced select ise ve degeri yoksa hicbirsey koyma
    			}
    			

    	    	if(lookupQueryResult.getErrorMap().isEmpty()){
    	    		runQuery(lookupQueryResult);
    				if(tabId!=null && lookupQueryResult.getQuery().getMainTableId()!=0 && requestParams.get(".w")!=null){
    					keys = UserUtil.getTableGridFormCellCachedKeys((Integer)scd.get("customizationId"), lookupQueryResult.getQuery().getMainTableId(), (Integer)scd.get("userId"),(String)scd.get("sessionId"), 
    						requestParams.get(".w"),tabId, -c.getFormCellId(), requestParams, true);
    						if(keys!=null)for(Object[] o:lookupQueryResult.getData())keys.add(GenericUtil.uInt(o[1]));
    				}
    	    		if(paramMap.get("xapproval_id")!=null && c.getLookupQueryId()==606){//onaylanmis ve reddedildiyi koy
    	    			W5Approval ta = FrameworkCache.wApprovals.get(GenericUtil.uInt(paramMap.get("xapproval_id")));
    	    			if(ta.getApprovalFlowTip()==2){//hiyerarsik onay ise
    	    				lookupQueryResult.getData().add(0,new Object[]{LocaleMsgCache.get2((Integer)scd.get("customizationId"),(String)scd.get("locale"),ta.getHierarchicalAppMsg()),902});
    	    			}
    	    			if(ta.getApprovalFlowTip()==3){//dinamik onay ise
    	    				lookupQueryResult.getData().add(0,new Object[]{LocaleMsgCache.get2((Integer)scd.get("customizationId"),(String)scd.get("locale"),ta.getDynamicAppMsg()),903});
    	    			}
    	    			if(ta.getApprovalRequestTip()!=1){//gercek veri uzerinde?
    	    				lookupQueryResult.getData().add(0,new Object[]{LocaleMsgCache.get2((Integer)scd.get("customizationId"),(String)scd.get("locale"),ta.getApprovalRequestMsg()),901});
    	    			}
    	    			if(ta.getApprovalStrategyTip()!=0){//gercek veri uzerinde?
    	    				lookupQueryResult.getData().add(new Object[]{LocaleMsgCache.get2((Integer)scd.get("customizationId"),(String)scd.get("locale"),ta.getApprovedMsg()),998});
    	    			}
    	    			if(ta.getOnRejectTip()==1){//make status rejected
    	    				lookupQueryResult.getData().add(new Object[]{LocaleMsgCache.get2((Integer)scd.get("customizationId"),(String)scd.get("locale"),ta.getRejectedMsg()),999});
    	    			}
    	    		}
    	    	}
//    			paramMap.clear();
    		}
    	} catch (Exception e){
    		throw new IWBException("framework", "FormElement", rc.getFormCell().getFormCellId(), null, "[41,"+rc.getFormCell().getFormCellId()+"]", e);
    	}    		
	}
	
	@Override
	public Map<String, Object> loadRecordMapValue(Map<String, Object> scd, Map<String,String> requestParams, W5Table t, String prefix){
		StringBuilder	sql = new StringBuilder();
    	sql.append("select ");

    	List<W5TableField> realFields = new ArrayList();
		for(W5TableField tf:t.get_tableFieldList())if(tf.getTabOrder()>0){
			if(!GenericUtil.accessControl(scd, tf.getAccessViewTip(), tf.getAccessViewRoles(), tf.getAccessViewUsers())/* ||
					!PromisUtil.accessControl(formResult.getScd(), tf.getAccessUpdateTip(), tf.getAccessUpdateRoles(), tf.getAccessUpdateUsers())*/)continue;
    		sql.append("t.").append(tf.getDsc()).append(",");
    		realFields.add(tf);
    	}
    	sql.replace(sql.length()-1, sql.length(), " from ");

    	sql.append(t.getDsc()).append(" t");
    	boolean	b = false;
    	sql.append(" where ");
    	final	List<Object>	realParams = new ArrayList<Object>();
    	Object pkField = null;
    	Map errorMap = new HashMap();
    	for(W5TableParam x: t.get_tableParamList()){
    		if(b){
    			sql.append(" AND ");
    		}else 
    			b=true;
    		sql.append("t.").append(x.getExpressionDsc()).append(" = ? ");
    		Object psonuc = GenericUtil.prepareParam((W5Param)x, scd, requestParams, (short)-1, null, (short)1, null, null, errorMap);
    		if(pkField==null)pkField=psonuc;
    		realParams.add(psonuc);
    	}
    	if(!errorMap.isEmpty())return null;
    
    	try {
    		return getCurrentSession().doReturningWork(new ReturningWork<Map<String, Object>>() {
				@Override
				public Map<String, Object> execute(Connection conn) throws SQLException {
					PreparedStatement s = conn.prepareStatement(sql.toString());
			    	applyParameters(s, realParams);
			    	ResultSet rs = s.executeQuery();
			    	if(!rs.next()) return null;
			    	Map<String, Object> result = new HashMap<String, Object>();
			    	
					for(W5TableField tf:realFields)if(tf.getTabOrder()>0){
						Object obj = rs.getObject(tf.getDsc());
						if(obj!=null){
							if (obj instanceof java.sql.Timestamp) {
		        				try{ obj = GenericUtil.uFormatDateTime((java.sql.Timestamp) obj);
		        				}catch (Exception e) {}
							} else if (obj instanceof java.sql.Date) {
		        				try{
		        					obj = rs.getTimestamp(tf.getDsc());
		    						obj = GenericUtil.uFormatDateTime((java.sql.Timestamp) obj);
		        				}catch (Exception e) {
		    						obj = GenericUtil.uFormatDate((java.util.Date) obj);        					
		        				}
							}
		        			if(obj!=null)result.put(tf.getDsc(), obj.toString());
						}
			    	}
					if(rs!=null)rs.close();
					if(s!=null)s.close();
					return result;
				}
    		});
	    	
    	} catch (Exception e) {
    		if(FrameworkSetting.debug)e.printStackTrace();
    		return null;
		}


	}

	@Override
	public void loadFormTable(W5FormResult formResult){
		W5Form f = formResult.getForm();
		W5Table t = FrameworkCache.getTable(formResult.getScd(), f.getObjectId());
		StringBuilder	sql = new StringBuilder();
    	sql.append("select ");
    	String log5LogId=null;//logdan gosterilecekse
    	if(formResult.getRequestParams()!=null && formResult.getRequestParams().containsKey("_log5_log_id")){
    		log5LogId=formResult.getRequestParams().get("_log5_log_id");
    	}
		if(formResult.getFormCellResults()==null)formResult.setFormCellResults(new ArrayList<W5FormCellHelper>(formResult.getForm().get_formCells().size()));
		for(W5FormCell cell:formResult.getForm().get_formCells()){
			if(cell.getObjectDetailId()!=0){ 
				W5TableField tf = ((W5TableField)cell.get_sourceObjectDetail());
				if(tf!=null && tf.getTabOrder()>0){
		    		if(tf.getAccessViewTip()!=0 && tf.getAccessViewUserFields()!=null){
						if((!GenericUtil.accessControl(formResult.getScd(), tf.getAccessViewTip(), tf.getAccessViewRoles(), tf.getAccessViewUsers()) &&
								(!GenericUtil.isEmpty(tf.getAccessViewUserFields()) && accessUserFieldControl(t, tf.getAccessViewUserFields(), formResult.getScd(), formResult.getRequestParams(), null))))continue;
		    		}

					W5FormCellHelper result = new W5FormCellHelper(cell);
					formResult.getFormCellResults().add(result);
					if(log5LogId!=null){
			    		sql.append("t.").append(((W5TableField)cell.get_sourceObjectDetail()).getDsc()).append(",");
					} else {
			    		sql.append("t.").append(((W5TableField)cell.get_sourceObjectDetail()).getDsc()).append(",");
					}
		    	}
			} else {
				W5FormCellHelper result = new W5FormCellHelper(cell);
				formResult.getFormCellResults().add(result);
			}
		}
    	sql.replace(sql.length()-1, sql.length(), " from ");
    	if(log5LogId!=null){
    		sql.append(FrameworkSetting.crudLogSchema).append(".");
    	}
    	sql.append(t.getDsc()).append(" t");
    	boolean	b = false;
    	sql.append(" where ");
    	final	List<Object>	realParams = new ArrayList<Object>();
    	Object pkField = null;
    	if(log5LogId!=null){
    		sql.append(" t.log5_log_id = ? ");
    		realParams.add(GenericUtil.uInt(log5LogId));
    		formResult.getPkFields().put("log5_log_id", log5LogId);
    	} else {
    	
	    	for(W5TableParam x: t.get_tableParamList()){
	    		if(b){
	    			sql.append(" AND ");
	    		}else 
	    			b=true;
	    		sql.append("t.").append(x.getExpressionDsc()).append(" = ? ");
	    		Object psonuc = GenericUtil.prepareParam((W5Param)x, formResult.getScd(), formResult.getRequestParams(), (short)-1, null, (short)1, null, null, formResult.getErrorMap());
	    		if(pkField==null)pkField=psonuc;
	    		realParams.add(psonuc);
	    		formResult.getPkFields().put(x.getDsc(), psonuc);
	    	}
    	}


    	final Object pkField2 = pkField;
    	try {
    		getCurrentSession().doWork(new Work() {
				
				@Override
				public void execute(Connection conn) throws SQLException {
					PreparedStatement s = conn.prepareStatement(sql.toString());
			    	applyParameters(s, realParams);
			    	ResultSet rs = s.executeQuery();
			    	if(!rs.next())				
			    		throw new IWBException("sql","Form Load",formResult.getFormId(),GenericUtil.replaceSql(sql.toString(),realParams), "No Record Found", null);

					for(W5FormCellHelper cellResult:formResult.getFormCellResults())if(cellResult.getFormCell().getObjectDetailId()!=0){
						Object obj = rs.getObject(((W5TableField)cellResult.getFormCell().get_sourceObjectDetail()).getDsc());
						if(obj!=null){
							if (obj instanceof java.sql.Timestamp) {
		        				try{ obj = GenericUtil.uFormatDateTime((java.sql.Timestamp) obj);
		        				}catch (Exception e) {}
							} else if (obj instanceof java.sql.Date) {
		        				try{
		        					if(cellResult.getFormCell().getControlTip()==18){//date time
		            					obj = rs.getTimestamp(((W5TableField)cellResult.getFormCell().get_sourceObjectDetail()).getDsc());
		        						obj = GenericUtil.uFormatDateTime((java.sql.Timestamp) obj);
		        					} else //date
		        						obj = GenericUtil.uFormatDate((java.util.Date) obj);
		        				}catch (Exception e) {}
							}
							cellResult.setValue(obj.toString());
						}
					} else if(cellResult.getFormCell().getControlTip()==101){
						switch(cellResult.getFormCell().getInitialSourceTip()){
						case 0://yok-sabit
							cellResult.setValue(cellResult.getFormCell().getInitialValue());break;
						case 1://request
							cellResult.setValue(formResult.getRequestParams().get(cellResult.getFormCell().getInitialValue()));break;
						case 2:
							Object o = formResult.getScd().get(cellResult.getFormCell().getInitialValue());
							cellResult.setValue(o == null ? null: o.toString());
							break;
						case 3://app_setting
							cellResult.setValue(FrameworkCache.getAppSettingStringValue(formResult.getScd(), cellResult.getFormCell().getInitialValue()));
							break;
						case	4://SQL
							Object[] oz = DBUtil.filterExt4SQL(cellResult.getFormCell().getInitialValue(), formResult.getScd(), formResult.getRequestParams(), null);
							if(oz[1]!=null && ((List)oz[1]).size()>0){
								Map<String, Object> m = runSQLQuery2Map(oz[0].toString(),(List)oz[1],null, false);
								if(m!=null)cellResult.setValue(m.values().iterator().next().toString());
							} else {
								List l = executeSQLQuery(oz[0].toString());
								if(l!=null && l.size()>0 && l.get(0)!=null)cellResult.setValue(l.get(0).toString());
								
							}
							if(GenericUtil.isEmpty(cellResult.getValue()))cellResult.setValue(" ");					
							break;
						case	5://CustomJS(Rhino)
							Context cx = Context.enter();
							StringBuilder sc = new StringBuilder(); 
							try {
								// Initialize the standard objects (Object, Function, etc.)
								// This must be done before scripts can be executed. Returns
								// a scope object that we use in later calls.
								Scriptable scope = cx.initStandardObjects();

								// Collect the arguments into a single string.
								sc.append("\nvar _scd=").append(GenericUtil.fromMapToJsonString(formResult.getScd()));
								sc.append("\nvar _request=").append(GenericUtil.fromMapToJsonString(formResult.getRequestParams()));
								sc.append("\n").append(cellResult.getFormCell().getDefaultValue());

								//sc.append("'})';");
								// Now evaluate the string we've colected.
								cx.evaluateString(scope, sc.toString(), null, 1, null);
								Object em = scope.get("errorMsg", scope);
								if(em!=null){
									formResult.getErrorMap().put(cellResult.getFormCell().getDsc(), LocaleMsgCache.get2(0,(String)formResult.getScd().get("locale"), em.toString()));
									continue;
								}
				/*				Object exp = scope.get("expression", scope);
								if(exp!=null){
									
								} */
								Object res = scope.get("result", scope);
								if(res!=null && res instanceof org.mozilla.javascript.Undefined)res=null;
								if(res!=null && ((W5Param)cellResult.getFormCell().get_sourceObjectDetail()).getParamTip()==4)
									res = ""+new BigDecimal(res.toString()).intValue();
								cellResult.setValue(res == null ? null : res.toString());
				 
							} catch(Exception e){
								throw new IWBException("rhino", "FormElement", cellResult.getFormCell().getFormCellId(), sc.toString(), "[41,"+cellResult.getFormCell().getFormCellId()+"]", e);
							} finally {
					             // Exit from the context.
				 	             Context.exit();
					        }
							break;
						}
					}
					if(rs.next())//fazladan kayit geldi
						throw new IWBException("sql","Form Load",formResult.getFormId(),GenericUtil.replaceSql(sql.toString(),realParams), "Fazladan Kayit var", null);
					if(rs!=null)rs.close();
					if(s!=null)s.close();
					
					if(pkField2!=null){
						int extraSqlCount=0;
						StringBuilder extraSql=new StringBuilder();
						extraSql.append("select ");
						Set<String> extrInfoSet = new HashSet();
						if(FrameworkCache.getAppSettingIntValue(formResult.getScd(), "file_attachment_flag")!=0 && t.getFileAttachmentFlag()!=0){
							extraSql.append("(select count(1) cnt from iwb.w5_file_attachment x where x.customization_id=? AND x.table_id=? AND x.table_pk=?::text) file_attach_count");
							extrInfoSet.add("file_attach_count");
							extraSqlCount++;
						}
						if(FrameworkCache.getAppSettingIntValue(formResult.getScd(), "make_comment_flag")!=0 && t.getMakeCommentFlag()!=0){
							if(extraSql.length()>10)extraSql.append(",");
							if(FrameworkCache.getAppSettingIntValue(formResult.getScd(), "make_comment_summary_flag")!=0){
								extraSql.append("(select cx.comment_count||';'||cxx.comment_user_id||';'||to_char(cxx.comment_dttm,'dd/mm/yyyy hh24:mi:ss')||';'||cx.view_user_ids||'-'||cxx.dsc from iwb.w5_comment_summary cx, iwb.w5_comment cxx where cx.customization_id=? AND cx.table_id=? AND cx.table_pk=?::text AND cxx.customization_id=cx.customization_id AND cxx.comment_id=cx.last_comment_id) comment_extra");
								extrInfoSet.add("comment_extra");
								extraSqlCount++;
							} else {
								extraSql.append("(select count(1) cnt from iwb.w5_comment x where x.customization_id=? AND x.table_id=? AND x.table_pk=?::text) comment_count");
								extrInfoSet.add("comment_count");
								extraSqlCount++;					
							}
						}
						if(FrameworkCache.getAppSettingIntValue(formResult.getScd(), "row_based_security_flag")!=0 && (Integer)formResult.getScd().get("userTip")!=3 && t.getAccessTips()!=null && t.getAccessTips().length()>0){
							if(extraSql.length()>10)extraSql.append(",");
							extraSql.append("(select count(1) cnt from iwb.w5_access_control x where x.customization_id=? AND x.table_id=? AND x.table_pk=?) access_count");
							extrInfoSet.add("access_count");
							extraSqlCount++;
						}


						if(extraSql.length()>10){
							s = conn.prepareStatement(extraSql.append(" ").toString());
							List<Object> params= new ArrayList(extraSqlCount*3+1);
							for(int qi=0;qi<extraSqlCount;qi++){
								params.add(formResult.getScd().get("customizationId"));
								params.add(t.getTableId());
								params.add(pkField2);
							}
							applyParameters(s, params);//romisUtil.replaceSql(extraSql.toString(),params)
							rs = s.executeQuery();
					    	if(rs.next()){
					    		if(extrInfoSet.contains("file_attach_count"))formResult.setFileAttachmentCount(GenericUtil.uInt(rs.getObject("file_attach_count")));
					    		if(extrInfoSet.contains("comment_count")){
					    			formResult.setCommentCount(GenericUtil.uInt(rs.getObject("comment_count")));
					    		} else if(extrInfoSet.contains("comment_extra")){
					    			formResult.setCommentExtraInfo((String)rs.getObject("comment_extra"));
					    		}
					    		if(extrInfoSet.contains("access_count"))formResult.setAccessControlCount(GenericUtil.uInt(rs.getObject("access_count")));
					    	}
							if(rs!=null)rs.close();
							if(s!=null)s.close();
						}
					}
					if(FrameworkSetting.hibernateCloseAfterWork)if(conn!=null)conn.close();
					
				}

    			
    		});
	    	
    	} catch (IWBException pe) {
    		throw pe;
			// TODO: handle exception
		} catch(Exception e){
			if(FrameworkSetting.debug)e.printStackTrace();
//			logException(PromisUtil.replaceSql(sql.toString(), realParams)+"\n"+ e.getMessage(),PromisUtil.uInt(PromisCache.appSettings.get(0).get("default_customization_id")),0);
			throw new IWBException("sql","Form Load",formResult.getFormId(),GenericUtil.replaceSql(sql.toString(),realParams), e.getMessage(), e.getCause());
		} finally {
//    		session.close();
    	}
	}

	@Override
	public W5TemplateResult getTemplateResult(Map<String, Object> scd, int templateId) {
		
		W5TemplateResult templateResult = new W5TemplateResult(templateId);
		templateResult.setScd(scd);
		
		W5Template tpl = null;
		if(FrameworkSetting.preloadWEngine!=0 && (tpl=FrameworkCache.getTemplate(templateResult.isDev() ? 0 :scd, templateId))!=null){
			templateResult.setTemplate(tpl);
		} else {
			loadTemplate(templateResult);
			if(FrameworkSetting.preloadWEngine!=0)FrameworkCache.wTemplates.get(templateResult.isDev() ? 0:templateResult.getTemplate().getCustomizationId()).put(templateId, templateResult.getTemplate());
		}
		
		return templateResult;
	}
	
	

	private void loadTemplate(W5TemplateResult templateResult) {

		int cid = templateResult.isDev() ? 0 : FrameworkCache.getCustomizationId(templateResult.getScd());
		if(cid!=0){
			W5Customization cus = FrameworkCache.wCustomizationMap.get(cid);
		}
		W5Template template = (W5Template)getCustomizedObject("from W5Template t where t.templateId=? and t.customizationId=?", templateResult.getTemplateId(), cid, "Page"); // ozel bir client icin varsa
		templateResult.setTemplate(template);
		template.set_templateObjectList(find("from W5TemplateObject t where t.activeFlag=1 AND t.templateId=? AND t.customizationId=? order by t.tabOrder",templateResult.getTemplateId(), cid));
		
		if(GenericUtil.isEmpty(template.getCode()) || template.getCode().startsWith("!"))for(W5TemplateObject to:template.get_templateObjectList())if(to.getSrcQueryFieldId()!=null && to.getDstQueryParamId()!=null){
			List p = new ArrayList(); p.add(cid);p.add(to.getSrcQueryFieldId());p.add(cid);p.add(to.getDstQueryParamId());
			String sql = "select (select f1.dsc from iwb.w5_query_field f1 where f1.customization_id=? AND f1.query_field_id=?) f_dsc, "
					+ "(select p1.dsc from iwb.w5_query_param p1 where p1.customization_id=? AND p1.query_param_id=?) p_dsc";
			if(to.getDstStaticQueryParamId()!=null && !GenericUtil.isEmpty(to.getDstStaticQueryParamVal())){
				p.add(cid);p.add(to.getDstStaticQueryParamId());
				sql+=",(select p1.dsc from iwb.w5_query_param p1 where p1.customization_id=? AND p1.query_param_id=?) ps_dsc";
			}
			List l = executeSQLQuery2Map(sql , p);
			if(!GenericUtil.isEmpty(l)){
				Map m = (Map)l.get(0);
				to.set_srcQueryFieldName((String)m.get("f_dsc"));
				to.set_dstQueryParamName((String)m.get("p_dsc"));
				if(m.containsKey("ps_dsc"))to.set_dstStaticQueryParamName((String)m.get("ps_dsc"));
			}
		}
	}

	@Override
	public W5GridResult getGridResultMinimal(Map<String, Object> scd, int gridId, Map<String,String> requestParams) {
		W5GridResult gridResult = new W5GridResult(gridId);
		gridResult.setRequestParams(requestParams);
		gridResult.setScd(scd);
		if(FrameworkSetting.preloadWEngine!=0 && FrameworkCache.getGrid(scd,gridId)!=null){
			gridResult.setGrid(FrameworkCache.getGrid(scd,gridId));
		} else {
			int customizationId = (Integer)gridResult.getScd().get("customizationId");
			W5Grid grid = (W5Grid)getCustomizedObject("from W5Grid t where t.gridId=? and t.customizationId=?", gridResult.getGridId(), customizationId, "Grid"); // ozel bir client icin varsa
			gridResult.setGrid(grid);
			grid.set_gridColumnList(find("from W5GridColumn t where t.gridId=? AND t.customizationId=? order by t.tabOrder",gridResult.getGridId(), customizationId));
		}
		
		return gridResult;
	}
	@Override
	public W5DataViewResult getDataViewResult(Map<String, Object> scd, int dataViewId, Map<String,String> requestParams, boolean noSearchForm) {
		W5DataViewResult dataViewResult = new W5DataViewResult(dataViewId);
		int customizationId = dataViewResult.isDev() ? 0 : (Integer)scd.get("customizationId");
		dataViewResult.setRequestParams(requestParams);
		dataViewResult.setScd(scd);
		
		W5DataView d = null;
		if(FrameworkSetting.preloadWEngine!=0 && (d=FrameworkCache.getDataView(customizationId,dataViewId))!=null){
			dataViewResult.setDataView(d);
		} else {
			loadDataView(dataViewResult);
			d = dataViewResult.getDataView();
			if(FrameworkSetting.preloadWEngine!=0)FrameworkCache.wDataViews.get(customizationId).put(dataViewId, d);
		}
		//search Form
		if(!noSearchForm && d.get_searchFormId()!=0){
			W5FormResult	searchForm = getFormResult(scd, d.get_searchFormId(), 2, requestParams);
			initializeForm(searchForm, false);
			loadFormCellLookups(dataViewResult.getScd(), searchForm.getFormCellResults(), dataViewResult.getRequestParams(), null);
			dataViewResult.setSearchFormResult(searchForm);
		}
		return dataViewResult;
	}
	
	private void loadDataView(W5DataViewResult dataViewResult) {
		int customizationId = dataViewResult.isDev() ? 0 : (Integer)dataViewResult.getScd().get("customizationId");
		W5DataView d = (W5DataView)getCustomizedObject("from W5DataView t where t.dataViewId=? and t.customizationId=?", dataViewResult.getDataViewId(), customizationId, "DataView"); // ozel bir client icin varsa
		dataViewResult.setDataView(d);

		W5Query query = null;
		if(FrameworkSetting.preloadWEngine!=0){
			query = getQueryResult(dataViewResult.getScd(), d.getQueryId()).getQuery(); 
		}
		
		if(query==null){
			query = new W5Query();
			List<W5QueryField> queryFields = find("from W5QueryField t where t.queryId=? order by t.tabOrder", d.getQueryId());
			d.set_query(query);
			query.set_queryFields(queryFields); // dataReader icin gerekli
			query.setMainTableId((Integer)find("select t.mainTableId from W5Query t where t.queryId=?", d.getQueryId()).get(0));
		} else
			d.set_query(query);
		
		d.set_mainTable(FrameworkCache.getTable(customizationId, query.getMainTableId()));
		
		
		Map<Integer, W5QueryField> fieldMap = new HashMap<Integer, W5QueryField>();
		Map<String, W5QueryField> fieldMapDsc = new HashMap<String, W5QueryField>();
		for(W5QueryField field:query.get_queryFields()){
			fieldMap.put(field.getQueryFieldId(), field);
			fieldMapDsc.put(field.getDsc(), field);
		}

		d.set_queryFieldMap(fieldMap);
		
		d.set_queryFieldMapDsc(fieldMapDsc);
		d.set_pkQueryField(fieldMap.get(d.getPkQueryFieldId()));
		
		d.set_toolbarItemList(find("from W5ObjectToolbarItem t where t.objectTip=? AND t.objectId=? AND t.customizationId=? order by t.tabOrder",(short)8, d.getDataViewId(),customizationId));

		Integer searchFormId = (Integer)getCustomizedObject("select t.formId from W5Form t where t.customizationId=? AND t.objectTip=8 and t.objectId=?", customizationId, dataViewResult.getDataViewId(), null); 
		if(searchFormId!=null)d.set_searchFormId(searchFormId);
	}
	@Override
	public W5GridResult getGridResult(Map<String, Object> scd, int gridId, Map<String,String> requestParams, boolean noSearchForm) {
		try{
			int customizationId = (Integer)scd.get("customizationId");
			W5GridResult gridResult = new W5GridResult(gridId);
			gridResult.setRequestParams(requestParams);
			gridResult.setScd(scd);
			W5Grid g = null;
			if(FrameworkSetting.preloadWEngine!=0 && (g=FrameworkCache.getGrid(customizationId,gridId))!=null){
				gridResult.setGrid(g);
			} else {
				loadGrid(gridResult);
				g = gridResult.getGrid();
				if(FrameworkSetting.preloadWEngine!=0)FrameworkCache.addGrid(customizationId, g);
			}
			
	
			//search Form
			if(!noSearchForm && g.get_searchFormId()!=0)try{
				W5FormResult	searchForm = getFormResult(scd, g.get_searchFormId(), 2, requestParams);
				initializeForm(searchForm, false);
				loadFormCellLookups(scd, searchForm.getFormCellResults(), requestParams, null);
				gridResult.setSearchFormResult(searchForm);
			} catch (Exception e){
				throw new IWBException("framework", "SearchForm", g.get_searchFormId(), null, "[40,"+g.get_searchFormId()+"]", e);
			}
			
			gridResult.setFormCellResultMap(new HashMap());
	
			for(W5GridColumn column:g.get_gridColumnList())if(column.get_formCell()!=null){
				W5FormCellHelper cellResult = new W5FormCellHelper(column.get_formCell());
				gridResult.getFormCellResultMap().put(column.get_formCell().getFormCellId(), cellResult);
			}
			
			if(!gridResult.getFormCellResultMap().isEmpty())
				loadFormCellLookups(gridResult.getScd(), new ArrayList(gridResult.getFormCellResultMap().values()), gridResult.getRequestParams(), null);
	
			return gridResult;
		} catch (Exception e){
			throw new IWBException("framework", "Grid", gridId, null, "[5,"+gridId+"]", e);
		}
	}

	@Override
	public String getInitialFormCellValue(Map<String, Object> scd,W5FormCell cell, Map<String,String> requestParams){
		String result = null;
		switch(cell.getInitialSourceTip()){
		case 0://yok-sabit
			result=(cell.getInitialValue());break;
		case 1://request
			result=(requestParams.get(cell.getInitialValue()));break;
		case 2:
			Object o = scd.get(cell.getInitialValue());
			result=(o == null ? null: o.toString());
			break;
		case 3://app_setting
			result=(FrameworkCache.getAppSettingStringValue(scd, cell.getInitialValue()));
			break;
		case	4://SQL
			Object[] oz = DBUtil.filterExt4SQL(cell.getInitialValue(), scd, requestParams, null);
			Map<String, Object> m = runSQLQuery2Map(oz[0].toString(),(List)oz[1],null);
			if(m!=null)result=(m.values().iterator().next().toString());
			break;
		case	5://CustomJS(Rhino)
			Context cx = Context.enter();
			try {
				// Initialize the standard objects (Object, Function, etc.)
				// This must be done before scripts can be executed. Returns
				// a scope object that we use in later calls.
				Scriptable scope = cx.initStandardObjects();
				if(cell.getDefaultValue().indexOf("$iwb.")>-1){
					ScriptEngine se = new ScriptEngine(scd, requestParams, this, null);
					Object wrappedOut = Context.javaToJS( se, scope);
					ScriptableObject.putProperty(scope, "$iwb", wrappedOut);
				}
				// Collect the arguments into a single string.
				StringBuffer sc = new StringBuffer(); 
				sc.append("\nvar _scd=").append(GenericUtil.fromMapToJsonString(scd));
				sc.append("\nvar _request=").append(GenericUtil.fromMapToJsonString(requestParams));
				sc.append("\n").append(cell.getDefaultValue());

				//sc.append("'})';");
				// Now evaluate the string we've colected.
				cx.evaluateString(scope, sc.toString(), null, 1, null);
				if(scope.has("errorMsg", scope)){
					Object em = scope.get("errorMsg", scope);
					if(em!=null){
						return null;
					}
				}
/*				Object exp = scope.get("expression", scope);
				if(exp!=null){
					
				} */
				Object res = scope.get("result", scope);
				if(res!=null && res instanceof org.mozilla.javascript.Undefined)res=null;
				if(res!=null && ((W5Param)cell.get_sourceObjectDetail()).getParamTip()==4)
					res = ""+new BigDecimal(res.toString()).intValue();
				result=(res == null ? null : res.toString());
 
			} finally {
	             // Exit from the context.
 	             Context.exit();
	        }
			break;
		case	10://approvalStates
			String selectedItems="";
			W5Approval app= FrameworkCache.wApprovals.get(GenericUtil.uInt(cell.getInitialValue()));
			for(W5ApprovalStep step:app.get_approvalStepList())if(GenericUtil.accessControl(scd, (short)1, step.getApprovalRoles(), step.getApprovalUsers()))
				selectedItems+=","+step.getApprovalStepId();
			if(selectedItems.length()>0)result=(selectedItems.substring(1));
		}	
		return result;
	}
	@Override
	public Map<String, String> interprateSmsTemplate(W5FormSmsMail fsm, Map<String, Object> scd, Map<String,String> requestParams, int fsmTableId, int fsmTablePk) {
		Map<String, String> m = new HashMap<String, String>();
		String phone = fsm.getSmsMailTo();
		if(phone!=null && phone.contains("${")){
			StringBuilder tmp1 = new StringBuilder(phone);
			interprateTemplate(scd, requestParams, fsmTableId, fsmTablePk, tmp1, true,1,0);
			phone = tmp1.toString();
		}
		m.put("phone", phone);
		String smsBody = fsm.getSmsMailBody();
		if(smsBody!=null && smsBody.contains("${")){
			StringBuilder tmp1 = new StringBuilder(smsBody);
			interprateTemplate(scd, requestParams, fsmTableId, fsmTablePk, tmp1, true,0,0);
			smsBody = tmp1.toString();
		}
		m.put("body", smsBody);
		return m;
	}
	@Override
	public W5Email interprateMailTemplate(W5FormSmsMail fsm, Map<String, Object> scd, Map<String,String> requestParams, int fsmTableId, int fsmTablePk) {
		
		W5Email email = new W5Email();
		String mailTo = fsm.getSmsMailTo();
		if(mailTo!=null && mailTo.contains("${")){
			StringBuilder tmp1 = new StringBuilder(mailTo);
			interprateTemplate(scd, requestParams, fsmTableId, fsmTablePk, tmp1, true,2,0);
			mailTo = MailUtil.organizeMailAdress(tmp1.toString());
		}
		email.setMailTo(mailTo);
		String mailCc = fsm.getSmsMailCc();
		if(mailCc!=null && mailCc.contains("${")){
			StringBuilder tmp1 = new StringBuilder(mailCc);
			interprateTemplate(scd, requestParams, fsmTableId, fsmTablePk, tmp1, true,2,0);
			mailCc = MailUtil.organizeMailAdress(tmp1.toString());
		}
		email.setMailCc(mailCc);
		String mailBcc = fsm.getSmsMailBcc();
		if(mailBcc!=null && mailBcc.contains("${")){
			StringBuilder tmp1 = new StringBuilder(mailBcc);
			interprateTemplate(scd, requestParams, fsmTableId, fsmTablePk, tmp1, true,2,0);
			mailBcc = MailUtil.organizeMailAdress(tmp1.toString());
		}
		email.setMailBcc(mailBcc);
		String mailSubject = fsm.getSmsMailSubject();
		if(mailSubject!=null && mailSubject.contains("${")){
			StringBuilder tmp1 = new StringBuilder(mailSubject);
			interprateTemplate(scd, requestParams, fsmTableId, fsmTablePk, tmp1, true,0,0);
			mailSubject = tmp1.toString();
		}
		email.setMailSubject(mailSubject);
		String mailBody = fsm.getSmsMailBody();
		if(mailBody!=null && mailBody.contains("${")){
			StringBuilder tmp1 = new StringBuilder(mailBody);
			interprateTemplate(scd, requestParams, fsmTableId, fsmTablePk, tmp1, true,0,0);
			mailBody = tmp1.toString();
		}
		email.setMailBody(mailBody);
		return email;
		
	}
	@Override
	public void initializeForm(W5FormResult formResult, boolean onlyFreeFields) {
		W5Form form = formResult.getForm();
		int customizationId = formResult.isDev() ? 0 : (Integer)formResult.getScd().get("customizationId");
		W5Table t = null;
		switch(form.getObjectTip()){
		case	2: t= FrameworkCache.getTable(customizationId, form.getObjectId()); break; //table
		case	1: 
			W5Grid g = null;
			if(FrameworkSetting.preloadWEngine==0 || (g=FrameworkCache.getGrid(customizationId,form.getObjectId()))==null){
				g = (W5Grid)getCustomizedObject("from W5Grid g where g.customizationId=? AND g.gridId=?", customizationId, form.getObjectId(), "Grid");
			}
			if(g!=null){
				W5Query q = null;
				if(FrameworkSetting.preloadWEngine==0 || (q = FrameworkCache.wQueries.get(g.getQueryId()))==null){					
					q = (W5Query)getCustomizedObject("from W5Query g where g.mainTableId>0 AND ?=1 AND g.queryId=?", 1, g.getQueryId(), "Query");
				}
				if(q!=null)t= FrameworkCache.getTable(customizationId, q.getMainTableId());  //grid
			}
			break;
//		case	3: t= PromisCache.getTable(formResult.getScd(), form.getObjectId()); break; 
		}
		if(formResult.getFormCellResults()==null)formResult.setFormCellResults(new ArrayList<W5FormCellHelper>(form.get_formCells().size()));
		if(form.get_formCells()!=null)formResult.getExtraFormCells().addAll(0, form.get_formCells());
		for(W5FormCell cell:formResult.getExtraFormCells())if(!onlyFreeFields || cell.getObjectDetailId()==0){
			if(t!=null){
				W5TableField tf = null;
				if(form.getObjectTip()==2 && cell.get_sourceObjectDetail()!=null && cell.get_sourceObjectDetail() instanceof W5TableField){
					tf = (W5TableField)cell.get_sourceObjectDetail();
				} else if(form.getObjectTip()==1 && cell.get_sourceObjectDetail()!=null){
					if(cell.get_sourceObjectDetail() instanceof W5QueryParam){
						W5QueryParam qp = (W5QueryParam)cell.get_sourceObjectDetail();
						if(qp.getRelatedTableFieldId()!=0 && t!=null){
							tf=t.get_tableFieldMap().get(qp.getRelatedTableFieldId());
						}
					} else if(cell.get_sourceObjectDetail() instanceof W5TableField){
						tf = (W5TableField)cell.get_sourceObjectDetail();
					}
				}
				if(tf!=null){
		    		if(!GenericUtil.accessControl(formResult.getScd(), tf.getAccessInsertTip(), tf.getAccessInsertRoles(), tf.getAccessInsertUsers()))continue;//access control
				}
			}
    		W5FormCellHelper result = new W5FormCellHelper(cell);
			switch(cell.getInitialSourceTip()){
			case 0://yok-sabit
				result.setValue(cell.getInitialValue());break;
			case 1://request
				result.setValue(formResult.getRequestParams().get(cell.getInitialValue()));break;
			case 2:
				Object o = formResult.getScd().get(cell.getInitialValue());
				result.setValue(o == null ? null: o.toString());
				break;
			case 3://app_setting
				result.setValue(FrameworkCache.getAppSettingStringValue(formResult.getScd(), cell.getInitialValue()));
				break;
			case	4://SQL
				Object[] oz = DBUtil.filterExt4SQL(cell.getInitialValue(), formResult.getScd(), formResult.getRequestParams(), null);
				if(oz[1]!=null && ((List)oz[1]).size()>0){
					Map<String, Object> m = runSQLQuery2Map(oz[0].toString(),(List)oz[1],null);
					if(m!=null && m.size()>0)result.setValue(m.values().iterator().next().toString());
				} else {
					List l = executeSQLQuery(oz[0].toString());
					if(l!=null && l.size()>0 && l.get(0)!=null)result.setValue(l.get(0).toString());
					
				}
				break;
			case	5://CustomJS(Rhino)
				Context cx = Context.enter();
				StringBuilder sc = new StringBuilder(); 
				try {
					// Initialize the standard objects (Object, Function, etc.)
					// This must be done before scripts can be executed. Returns
					// a scope object that we use in later calls.
					Scriptable scope = cx.initStandardObjects();
					if(cell.getInitialValue().indexOf("$iwb.")>-1){
						ScriptEngine se = new ScriptEngine(formResult.getScd(), formResult.getRequestParams(), this, null);
						Object wrappedOut = Context.javaToJS( se, scope);
						ScriptableObject.putProperty(scope, "$iwb", wrappedOut);
					}
					// Collect the arguments into a single string.
					sc.append("\nvar _scd=").append(GenericUtil.fromMapToJsonString(formResult.getScd()));
					sc.append("\nvar _request=").append(GenericUtil.fromMapToJsonString(formResult.getRequestParams()));
					sc.append("\n").append(cell.getInitialValue());

					//sc.append("'})';");
					// Now evaluate the string we've colected.
					cx.evaluateString(scope, sc.toString(), null, 1, null);
					if(scope.has("errorMsg", scope)){
						Object em = scope.get("errorMsg", scope);
						if(em!=null){
							formResult.getErrorMap().put(cell.getDsc(), LocaleMsgCache.get2(0, (String)formResult.getScd().get("locale"), em.toString()));
							continue;
						}
					}
	/*				Object exp = scope.get("expression", scope);
					if(exp!=null){
						
					} */
					Object res = scope.get("result", scope);
					if(res!=null && res instanceof org.mozilla.javascript.Undefined)res=null;
					if(res!=null && ((W5Param)cell.get_sourceObjectDetail()).getParamTip()==4)
						res = ""+new BigDecimal(res.toString()).intValue();
					result.setValue(res == null ? null : res.toString());
	 
				} catch(Exception e){
					throw new IWBException("rhino", "FormElement", cell.getFormCellId(), sc.toString(), "[41,"+cell.getFormCellId()+"]", e);
				} finally {
		             // Exit from the context.
	 	             Context.exit();
		        }
				break;
			case	10://approvalStates
				String selectedItems="";
				W5Approval app= FrameworkCache.wApprovals.get(GenericUtil.uInt(cell.getInitialValue()));
				if(app != null)
				for(W5ApprovalStep step:app.get_approvalStepList())if(GenericUtil.accessControl(formResult.getScd(), (short)1, step.getApprovalRoles(), step.getApprovalUsers()))
					selectedItems+=","+step.getApprovalStepId();
				if(selectedItems.length()>0)result.setValue(selectedItems.substring(1));
			}	
			formResult.getFormCellResults().add(result);
		}
	}
	

	private void loadGrid(W5GridResult gridResult) {
		int customizationId = gridResult.isDev() ? 0 : FrameworkCache.getCustomizationId(gridResult.getScd());
		
		
		W5Grid grid = (W5Grid)getCustomizedObject("from W5Grid t where t.gridId=? and t.customizationId=?", gridResult.getGridId(), customizationId, "Grid"); // ozel bir client icin varsa
		gridResult.setGrid(grid);

		grid.set_gridColumnList(find("from W5GridColumn t where t.customizationId=? AND t.gridId=? order by t.tabOrder", customizationId,gridResult.getGridId()));
		switch(grid.getRowColorFxTip()){
		case	1:if(grid.getRowColorFxQueryFieldId()!=0){ //lookup eslesme
			grid.set_listCustomGridColumnRenderer(find("from W5CustomGridColumnRenderer t where t.customizationId=? AND t.gridId=? AND t.queryFieldId=?", customizationId,gridResult.getGridId(),grid.getRowColorFxQueryFieldId()));
		}
		break;
		case	2:case	3:if(grid.getRowColorFxQueryFieldId()!=0 || grid.getRowColorFxTip()==3){//kosul
			grid.set_listCustomGridColumnCondition(find("from W5CustomGridColumnCondition t where t.customizationId=? AND t.gridId=? AND t.queryFieldId=? order by t.tabOrder", customizationId,gridResult.getGridId(),grid.getRowColorFxQueryFieldId()));
		}
		break;
		}
		
		W5Query query = null;
		if(FrameworkSetting.preloadWEngine!=0){
			query = getQueryResult(gridResult.getScd(), grid.getQueryId()).getQuery(); 
		}
		
		if(query==null){
			query = new W5Query();
			List<W5QueryField> queryFields = find("from W5QueryField t where t.queryId=? order by t.tabOrder", grid.getQueryId());
			grid.set_query(query);
			query.set_queryFields(queryFields); // dataReader icin gerekli
			query.setMainTableId((Integer)find("select t.mainTableId from W5Query t where t.queryId=?", grid.getQueryId()).get(0));
		} else
			grid.set_query(query);
		
		grid.set_viewTable(FrameworkCache.getTable(customizationId, query.getMainTableId()));
		
		Map<Integer, W5QueryField> fieldMap = new HashMap<Integer, W5QueryField>();
		Map<String, W5QueryField> fieldMapDsc = new HashMap<String, W5QueryField>();
		for(W5QueryField field:query.get_queryFields()){
			fieldMap.put(field.getQueryFieldId(), field);
			fieldMapDsc.put(field.getDsc(), field);
		}
		if(grid.get_viewTable()!=null){ //extended fields
			int qi=0;
			for(qi=0;qi<grid.get_viewTable().get_tableFieldList().size();qi++){
				W5TableField tf = grid.get_viewTable().get_tableFieldList().get(qi);

			}
		}
		grid.set_queryFieldMap(fieldMap);
		
		grid.set_queryFieldMapDsc(fieldMapDsc);
		grid.set_autoExpandField(fieldMap.get(grid.getAutoExpandFieldId()));
		grid.set_pkQueryField(fieldMap.get(grid.getPkQueryFieldId()));
		if(grid.get_pkQueryField()==null) {
			if(FrameworkSetting.debug)
				throw new IWBException("framework","Grid", grid.getGridId(), null, "Grid PK Missing", null);
			grid.set_pkQueryField(query.get_queryFields().get(0));
		}
		grid.set_groupingField(fieldMap.get(grid.getGroupingFieldId()));
		grid.set_fxRowField(fieldMap.get(grid.getRowColorFxQueryFieldId()));
		
		int formCellCounter = 1;
		for(W5GridColumn column:grid.get_gridColumnList()){
			column.set_queryField(fieldMap.get(column.getQueryFieldId()));
			if(column.getFormCellId()>0){ //form_cell
				W5FormCell cell = (W5FormCell)getCustomizedObject("from W5FormCell t where t.formCellId=? and t.customizationId=?", column.getFormCellId(), customizationId, null);
				if(cell!=null){
					column.set_formCell(cell);
				}
			} else if(column.getFormCellId()<0){//control
				W5FormCell cell = new W5FormCell(-formCellCounter++);
				cell.setControlTip((short)-column.getFormCellId());
				cell.setDsc(column.get_queryField().getDsc());
				cell.setFormCellId(column.getQueryFieldId());
				column.set_formCell(cell);
			}
		}


		Integer searchFormId = (Integer)getCustomizedObject("select t.formId from W5Form t where t.customizationId=? AND t.objectTip=1 and t.objectId=?", customizationId, gridResult.getGridId(), null); 
		if(searchFormId!=null)grid.set_searchFormId(searchFormId);

		grid.set_toolbarItemList(find("from W5ObjectToolbarItem t where t.objectTip=? AND t.objectId=? AND t.customizationId=? order by t.tabOrder",(short)5, gridResult.getGridId(),customizationId));
		for(W5ObjectToolbarItem c:grid.get_toolbarItemList())switch(c.getItemTip()){//TODO:toolbar icine bisey konulacaksa
		case	10:case	7:case	15:case	9:
			break;
		case	14:case	8:case	6:
			break;
		}
		grid.set_menuItemList(find("from W5ObjectMenuItem t where t.objectTip=? AND t.objectId=? AND t.customizationId=? order by t.tabOrder",(short)5, gridResult.getGridId(),customizationId));
		if(grid.getColumnRenderTip()!=0)grid.set_gridModuleList(find("from W5GridModule t where t.gridId=? AND t.customizationId=? order by t.tabOrder",gridResult.getGridId(),customizationId));
		//if(grid.getSelectionModeTip()==4)
		grid.set_detailView((W5Template)getCustomizedObject("from W5Template t where t.templateTip=1 AND t.objectId=? AND t.customizationId=?",gridResult.getGridId(),customizationId, null)); 



		if(grid.getDefaultCrudFormId()!=0){
			W5Form defaultCrudForm = (W5Form)getCustomizedObject("from W5Form t where t.formId=? and t.customizationId=?", grid.getDefaultCrudFormId(), customizationId, "Form"); // ozel bir client icin varsa
			
			if(defaultCrudForm!=null){
//				defaultCrudForm.set_sourceTable(PromisCache.getTable(customizationId, defaultCrudForm.getObjectId()));
				W5Table t = FrameworkCache.getTable(customizationId, defaultCrudForm.getObjectId()); //PromisCache.getTable(f.getScd(), f.getForm().getObjectId())
				grid.set_crudTable(t);
				grid.set_defaultCrudForm(defaultCrudForm);
				
				grid.set_crudFormSmsMailList(find("from W5FormSmsMail t where t.activeFlag=1 AND t.actionTips like '%0%' AND t.formId=? AND t.customizationId=? order by t.tabOrder", grid.getDefaultCrudFormId(),customizationId));
				grid.set_crudFormConversionList(find("from W5Conversion t where t.activeFlag=1 AND t.actionTips like '%0%' AND t.srcFormId=? AND t.customizationId=? order by t.tabOrder", grid.getDefaultCrudFormId(),customizationId));
				// Gridle ilgili onay mekanizması ataması
				List<W5Approval> a = find("from W5Approval t where t.activeFlag=1 AND t.tableId = ? AND t.customizationId = ?", grid.get_defaultCrudForm().getObjectId(), customizationId);
				if(a != null && a.size() > 0){
					grid.setApproval(a.get(0));
				}
				
				if(grid.get_crudFormSmsMailList().isEmpty())grid.set_crudFormSmsMailList(null);
				//extra islemler
				List ql =find("select 1 from W5Approval t where t.activeFlag=1 AND t.tableId=? AND t.customizationId=?", defaultCrudForm.getObjectId(), customizationId);
				if(FrameworkCache.getAppSettingIntValue(gridResult.getScd(), "approval_flag")!=0 && ql!=null && !ql.isEmpty()){//table Record Approvals
					if(grid.get_postProcessQueryFields()==null)grid.set_postProcessQueryFields(new ArrayList());
					W5QueryField f = new W5QueryField();
					f.setDsc(FieldDefinitions.queryFieldName_Approval);
					f.setFieldTip((short)5);//comment
					f.setTabOrder((short)22);//aslinda width
					f.setPostProcessTip((short)49);//approvalPostProcess
					grid.get_postProcessQueryFields().add(f);
					W5QueryField f2 = new W5QueryField();
					f2.setDsc(FieldDefinitions.queryFieldName_ArVersionNo);
					f2.setFieldTip((short)4);//comment
					f2.setTabOrder((short)22);//aslinda width
					grid.get_postProcessQueryFields().add(f2);
				}

				if(FrameworkCache.getAppSettingIntValue(gridResult.getScd(), "row_based_security_flag")!=0 && t.getAccessTips()!=null){ //var demek ki
					if(grid.get_postProcessQueryFields()==null)grid.set_postProcessQueryFields(new ArrayList());
					W5QueryField f = new W5QueryField();
					f.setDsc(FieldDefinitions.queryFieldName_RowBasedSecurity);
					f.setFieldTip((short)1);//access control
					f.setTabOrder((short)22);//aslinda width
					grid.get_postProcessQueryFields().add(f);
				}
				if(FrameworkCache.getAppSettingIntValue(gridResult.getScd(), "file_attachment_flag")!=0 && t.getFileAttachmentFlag()!=0){
					if(grid.get_postProcessQueryFields()==null)grid.set_postProcessQueryFields(new ArrayList());
					W5QueryField f = new W5QueryField();
					f.setDsc(FieldDefinitions.queryFieldName_FileAttachment);
					f.setFieldTip((short)2);//file attachment
					f.setTabOrder((short)22);//aslinda width
					grid.get_postProcessQueryFields().add(f);
				}
				if(FrameworkCache.getAppSettingIntValue(gridResult.getScd(), "make_comment_flag")!=0 && t.getMakeCommentFlag()!=0){//table Comment
					if(grid.get_postProcessQueryFields()==null)grid.set_postProcessQueryFields(new ArrayList());
					W5QueryField f = new W5QueryField();
					f.setDsc(FieldDefinitions.queryFieldName_Comment);
					f.setFieldTip((short)3);//comment
					f.setTabOrder((short)22);//aslinda width
//					if(PromisSetting.commentSummary)f.set
					grid.get_postProcessQueryFields().add(f);
				}
				if(FrameworkSetting.vcs && t.getVcsFlag()!=0){
					if(grid.get_postProcessQueryFields()==null)grid.set_postProcessQueryFields(new ArrayList());
					W5QueryField f = new W5QueryField();
					f.setDsc(FieldDefinitions.queryFieldName_Vcs);
					f.setFieldTip((short)9);//vcs
					f.setTabOrder((short)32);//aslinda width
					grid.get_postProcessQueryFields().add(f);
				}

			}
		}
		
	}


	@Override
	public boolean updateFormTable(W5FormResult formResult, String schema, String paramSuffix) {
		Map<String, Object> scd = formResult.getScd();
		W5Form f = formResult.getForm();
		W5Table t = FrameworkCache.getTable(scd, f.getObjectId());		
		if(FrameworkSetting.vcs && FrameworkSetting.vcsServer && t.getVcsFlag()!=0)
			throw new IWBException("vcs","Form Update",formResult.getFormId(),null, "VCS Server not allowed to update VCS Table", null);
    	StringBuilder	sql = new StringBuilder();
    	sql.append("update ");
    	if(schema!=null)sql.append(schema).append(".");
    	sql.append(t.getDsc()).append(" set ");
    	List<Object>	updateParams = new ArrayList<Object>();
    	List<Object>	whereParams = new ArrayList<Object>();
		Set<String> usedFields = new HashSet<String>();
		
		Map<Integer, W5FormModule> moduleMap = null;
		if(formResult.getForm().getRenderTip()!=0){
			moduleMap = new HashMap<Integer, W5FormModule>();
			if(formResult.getForm().get_moduleList()!=null)for(W5FormModule m : formResult.getForm().get_moduleList())
				moduleMap.put(m.getFormModuleId(), m);
		}
		W5ApprovalStep approvalStep = null;
		if(formResult.getApprovalRecord()!=null){
			approvalStep = FrameworkCache.wApprovals.get(formResult.getApprovalRecord().getApprovalId()).get_approvalStepMap().get(formResult.getApprovalRecord().getApprovalStepId());
		}
    	boolean	b = false;
    	boolean extendedFlag = false;
    	for(W5FormCell x: f.get_formCells())if(x.getNrdTip()!=1 && x.getObjectDetailId()!=0 && x.get_sourceObjectDetail()!=null && x.getControlTip()<100){ //normal ve readonly ise
    		W5TableField tf = (W5TableField)x.get_sourceObjectDetail();
    		if(tf.getCanUpdateFlag()==0 || tf.getTabOrder()<1)continue;//x.getCanUpdate()!=0
    		if(approvalStep!=null && approvalStep.getUpdatableFields()!=null && !GenericUtil.hasPartInside(approvalStep.getUpdatableFields(), ""+tf.getTableFieldId()))continue;
    		if(tf.getAccessViewTip()!=0 && !GenericUtil.accessControl(scd, tf.getAccessViewTip(), tf.getAccessViewRoles(), tf.getAccessViewUsers()) 
						&& (GenericUtil.isEmpty(tf.getAccessViewUserFields()) || accessUserFieldControl(t, tf.getAccessViewUserFields(), scd, formResult.getRequestParams(), paramSuffix)))continue;
    		if(tf.getAccessUpdateTip()!=0 && !GenericUtil.accessControl(scd, tf.getAccessUpdateTip(), tf.getAccessUpdateRoles(), tf.getAccessUpdateUsers()) 
						&& (GenericUtil.isEmpty(tf.getAccessUpdateUserFields()) || accessUserFieldControl(t, tf.getAccessUpdateUserFields(), scd, formResult.getRequestParams(), paramSuffix)))continue;
			
			
    		if(moduleMap!=null && moduleMap.get(x.getFormModuleId())!=null){
    			W5FormModule module = moduleMap.get(x.getFormModuleId());
    			if(!GenericUtil.accessControl(scd, module.getAccessViewTip(), module.getAccessViewRoles(), module.getAccessViewUsers()))
    				continue;
    		}


    		if(paramSuffix.length()>0 && formResult.getRequestParams().get(x.getDsc()+paramSuffix)==null)continue;
    		if(x.getControlTip()==31 && GenericUtil.uInt(x.getLookupIncludedValues())==1 && !GenericUtil.hasPartInside(x.getLookupIncludedParams(), ""+scd.get("userId")))continue;
    		Object psonuc = GenericUtil.prepareParam(tf, scd, formResult.getRequestParams(), x.getSourceTip(), null, x.getNotNullFlag(), x.getDsc()+paramSuffix, x.getDefaultValue(), formResult.getErrorMap());

			if(formResult.getErrorMap().isEmpty()){
	    		if(x.getFormCellId()==6060 || x.getFormCellId()==16327 || x.getFormCellId()==16866){//mail sifre icin
	    			if(psonuc!=null && psonuc.toString().startsWith("****"))continue;
	    			if(FrameworkSetting.mailPassEncrypt)psonuc=GenericUtil.PRMEncrypt(psonuc.toString());
	    		}
	    		
	    		if(b)sql.append(" , "); else b=true;
				sql.append(tf.getDsc()).append(" = ? ");
	    		updateParams.add(psonuc);
	    		usedFields.add(tf.getDsc());
	    	}
    	}

		if(formResult.getErrorMap().size()>0)return false;
		
    	for(W5TableField p1: t.get_tableFieldList())if(p1.getCanUpdateFlag()!=0 && !usedFields.contains(p1.getDsc()))switch(p1.getSourceTip()){ //geri kalan fieldlar icin
    	case	4: // calculated Fieldlar icin
    		if(b){sql.append(" , "); }else b=true;
    		usedFields.add(p1.getDsc());
    		sql.append(p1.getDsc()).append(" = ? ");
    		usedFields.add(p1.getDsc());
			break;
    	case	2:// session
    		Object psonuc = GenericUtil.prepareParam(p1, scd, formResult.getRequestParams(), (short)-1, null, (short)0, null, null, formResult.getErrorMap());
    		if(psonuc!=null){
	    		if(b){sql.append(" , "); }else b=true;
	    		usedFields.add(p1.getDsc());
	    		sql.append(p1.getDsc()).append(" = ? ");
	    		usedFields.add(p1.getDsc());
    		}

			break;
    	}
    	if(usedFields.isEmpty()){//sorun var
			throw new IWBException("validation","Form Update",formResult.getFormId(),null, "No Used Fields", null);
    	}
		if(f.get_versioningFlag()){ // eger versionin varsa, version'lari degistir ve version_no'yu arttir
			sql.append(", version_no=version_no+1, version_user_id=?, version_dttm=iwb.fnc_sysdate(?)");
			updateParams.add(scd.get("userId"));
			updateParams.add(scd.get("customizationId"));
		}

    	b = false;
    	sql.append(" where ");
    	for(W5TableParam x: t.get_tableParamList())if(x.getNotNullFlag()!=0){
    		if(b)sql.append(" AND "); else b=true;
    		sql.append(x.getExpressionDsc()).append(" = ? ");
    		Object psonuc = GenericUtil.prepareParam(x, scd, formResult.getRequestParams(), (short)-1, null, (short)0, x.getDsc() + paramSuffix, null, formResult.getErrorMap());
    		whereParams.add(psonuc);
    		formResult.getPkFields().put(x.getDsc(), psonuc);
    	}

    	updateParams.addAll(whereParams);

    	try {
     		final boolean extendedFlag2 = extendedFlag;
    		final Map<Integer, W5FormModule> moduleMap2 = moduleMap;
    		return getCurrentSession().doReturningWork(new ReturningWork<Boolean>() {
				@Override
				public Boolean execute(Connection conn) throws SQLException {
					PreparedStatement s = conn.prepareStatement(sql.toString());
					applyParameters(s, updateParams);
					int updateCount = s.executeUpdate();
					s.close();
					if(FrameworkSetting.hibernateCloseAfterWork)conn.close();
		    		if(t.getDoUpdateLogFlag()!=0)logTableRecord(formResult, paramSuffix);
		    		return updateCount==1;
				}
    		});
    		
		} catch(IWBException e){
			throw e;
		} catch(Exception e){
			if(e.getCause() instanceof IWBException)throw e;
			//if(PromisSetting.debug)e.printStackTrace();
//			logException(e.getMessage(),PromisUtil.uInt(PromisCache.appSettings.get(0).get("default_customization_id")),0);
			throw new IWBException("sql","Form Update",formResult.getFormId(),GenericUtil.replaceSql(sql.toString(), updateParams), e.getMessage(), e.getCause());
		} finally {
//			session.close();
		}
    	
	}

	@Override
	public int copyFormTable(W5FormResult formResult, String schema, String paramSuffix, boolean copyFlag) {
		W5Form f = formResult.getForm();
    	W5Table t= FrameworkCache.getTable(formResult.getScd(), f.getObjectId());//formResult.getForm().get_sourceTable();
		if(FrameworkSetting.vcs && FrameworkSetting.vcsServer && t.getVcsFlag()!=0)
			throw new IWBException("vcs","Form Insert",formResult.getFormId(),null, "VCS Server not allowed to insert VCS Table", null);
		StringBuilder	sql = new StringBuilder(), postSql = new StringBuilder();
    	sql.append("insert into ");
    	if(schema!=null)sql.append(schema).append(".");
    	sql.append(t.getDsc()).append(" ( ");
    	postSql.append(" select ");
    	final	List<Object>	copyParams = new ArrayList<Object>();
    	boolean	b = false;
		boolean extendedFlag = false;
		int	paramCount=0;
		Map<Integer, String> calculatedParams = new HashMap<Integer, String>();
		Map<Integer, String> calculatedParamNames = new HashMap<Integer, String>();
		Set<String> usedFields = new HashSet<String>();
		
		Map<Integer, W5FormModule> moduleMap = null;
		if(formResult.getForm().getRenderTip()!=0){
			moduleMap = new HashMap<Integer, W5FormModule>();
			if(formResult.getForm().get_moduleList()!=null)for(W5FormModule m : formResult.getForm().get_moduleList())
				moduleMap.put(m.getFormModuleId(), m);
		}
		
    	for(W5FormCell x: f.get_formCells())if(x.getNrdTip()!=1 && x.getObjectDetailId()!=0 && x.getControlTip()<100){//disabled(1) degil VE freeField(getObjectDetailId()!=0) degilse
    		W5TableField p1 = (W5TableField)x.get_sourceObjectDetail();
    		if(p1==null || p1.getCanInsertFlag()==0)continue; //x.getCanInsert()!=0
    		
    		//view AND update control
    		if(/*!PromisUtil.accessControl(formResult.getScd(), p1.getAccessViewTip(), p1.getAccessViewRoles(), p1.getAccessViewUsers()) ||*/
    			!GenericUtil.accessControl(formResult.getScd(), p1.getAccessInsertTip(), p1.getAccessInsertRoles(), p1.getAccessInsertUsers()))continue;//access control
    		
    		//module view control
    		if(moduleMap!=null && moduleMap.get(x.getFormModuleId())!=null){
    			W5FormModule module = moduleMap.get(x.getFormModuleId());
    			if(!GenericUtil.accessControl(formResult.getScd(), module.getAccessViewTip(), module.getAccessViewRoles(), module.getAccessViewUsers()))
    				continue;
    		}

    		Object psonuc = null;
    		switch(p1.getCopySourceTip()){
    		case	7://object_source (readonly)
    		case	6://object_source
    			if(copyFlag || p1.getCopySourceTip()==7){
    	    		if(b){sql.append(" , "); postSql.append(" , ");}else b=true;
    	    		usedFields.add(p1.getDsc());
    				sql.append(p1.getDsc());
    				postSql.append(p1.getDsc());
    				continue;
    			}
    		case	1://request
	    		psonuc = GenericUtil.prepareParam(p1, formResult.getScd(), formResult.getRequestParams(), x.getSourceTip(), null, x.getNotNullFlag(), x.getDsc()+paramSuffix, x.getDefaultValue(), formResult.getErrorMap());
	    		break;
    		default: 
    			continue;
    		}

    		if(formResult.getErrorMap().isEmpty()){
	    		if(b){sql.append(" , "); postSql.append(" , ");}else b=true;
	    		usedFields.add(p1.getDsc());
				sql.append(p1.getDsc());
				if(x.getOutFlag()!=0){ // bu field outputParam'a yazilacak
					if(x.getSourceTip()==4){ // calculated, sql calisacak sonra deger verilecek
	    				calculatedParams.put(paramCount, (String)psonuc);
	    				calculatedParamNames.put(paramCount, x.getDsc());
					} else {
						formResult.getOutputFields().put(x.getDsc(), psonuc);
					}
    				postSql.append(" ? ");
    				copyParams.add(null);
    				paramCount++;
				} else { //calculated, outputa yazilmadan direk
		    		if(x.getSourceTip()==4){ // calculated, sql calisacak sonra deger verilecek
	    				postSql.append(" ( ").append(psonuc).append(" ) ");
	    			} else {
	    				postSql.append(" ? ");
	    				copyParams.add(psonuc);
	    				paramCount++;
	    			}
				}
    		}
    	}

    	for(W5TableField p1: t.get_tableFieldList())if(p1.getCanInsertFlag()!=0 && !usedFields.contains(p1.getDsc()))switch(p1.getCopySourceTip()){
    	case	7://
    		if(p1.getSourceTip()!=4)break;
    	case	4: // calculated Fieldlar icin
    		if(b){sql.append(" , "); postSql.append(" , ");}else b=true;
    		usedFields.add(p1.getDsc());
			sql.append(p1.getDsc());
			calculatedParams.put(paramCount, GenericUtil.filterExt(p1.getDefaultValue(), formResult.getScd(), formResult.getRequestParams(), null).toString());
			calculatedParamNames.put(paramCount, p1.getDsc());
			postSql.append(" ? ");
			copyParams.add(null);
			paramCount++;
			break;
    	case	2:// session
    		Object psonuc = GenericUtil.prepareParam(p1, formResult.getScd(), formResult.getRequestParams(), (short)-1, null, (short)0, null, null, formResult.getErrorMap());
    		if(psonuc!=null){
	    		if(b){sql.append(" , "); postSql.append(" , ");}else b=true;
	    		usedFields.add(p1.getDsc());
				sql.append(p1.getDsc());
				postSql.append(" ? ");
				copyParams.add(psonuc);
				paramCount++;
    		}

			break;
    	case	9:// UUID
    		Object psonuc2 = GenericUtil.prepareParam(p1, formResult.getScd(), formResult.getRequestParams(), (short)-1, null, (short)0, null, null, formResult.getErrorMap());
    		if(psonuc2!=null){
	    		if(b){sql.append(" , "); postSql.append(" , ");}else b=true;
	    		usedFields.add(p1.getDsc());
				sql.append(p1.getDsc());
				postSql.append(" ? ");
				copyParams.add(psonuc2);
				paramCount++;
				
				formResult.getOutputFields().put(p1.getDsc(), psonuc2);
    		}

			break;
    	case	8:// Global Nextval
    		Object psonuc3 = GenericUtil.prepareParam(p1, formResult.getScd(), formResult.getRequestParams(), (short)-1, null, (short)0, null, null, formResult.getErrorMap());
    		if(psonuc3!=null){
	    		if(b){sql.append(" , "); postSql.append(" , ");}else b=true;
	    		usedFields.add(p1.getDsc());
				sql.append(p1.getDsc());
				postSql.append(" ? ");
				copyParams.add(psonuc3);
				paramCount++;
				
				formResult.getOutputFields().put(p1.getDsc(), psonuc3);
    		}

			break;			

    	}

		if(!formResult.getErrorMap().isEmpty())return 0;
		
    	if(usedFields.isEmpty()){//sorun var
			throw new IWBException("validation","Form Copy",formResult.getFormId(),null, "No Used Fields", null);
    	}


		if(f.get_versioningFlag()){ // eger versioning varsa, version'lari degistir ve version_no'yu arttir
			sql.append(", version_no, insert_user_id, version_user_id, insert_dttm, version_dttm ");
			postSql.append(" , 1, ?, ?, iwb.fnc_sysdate(?), iwb.fnc_sysdate(?)");
			copyParams.add(formResult.getScd().get("userId"));
			copyParams.add(formResult.getScd().get("userId"));
			copyParams.add(formResult.getScd().get("customizationId"));
			copyParams.add(formResult.getScd().get("customizationId"));
		}

    	sql.append(" ) ").append(postSql).append(" from ");
    	if(schema!=null)sql.append(schema).append(".");
    	sql.append(t.getDsc());
    	
    	b = false;
    	sql.append(" where ");
    	for(W5TableParam x: t.get_tableParamList())if(x.getNotNullFlag()!=0){
    		if(b)sql.append(" AND "); else b=true;
    		sql.append(x.getExpressionDsc()).append(" = ? ");
    		Object psonuc = GenericUtil.prepareParam(x, formResult.getScd(), formResult.getRequestParams(), (short)-1, null, (short)0, x.getDsc() + paramSuffix, null, formResult.getErrorMap());
    		copyParams.add(psonuc);
    		formResult.getPkFields().put(x.getDsc(), psonuc);
    	}
    	
    	b = false;
    	try {
	    	return getCurrentSession().doReturningWork(new ReturningWork<Integer>() {
				@Override
				public Integer execute(Connection conn) throws SQLException {
					PreparedStatement s = null;
					for(Integer o : calculatedParams.keySet()){ // calculated ve output edilecek parametreler hesaplaniyor once
			    		String seq = calculatedParams.get(o);
			    		if(seq.endsWith(".nextval"))seq="nextval('"+seq.substring(0,seq.length()-8)+"')";
			    		s = conn.prepareStatement("select " + seq + " ");//from dual
			    		ResultSet rs = s.executeQuery();
			    		rs.next();
			    		Object paramOut = rs.getObject(1);
						if(paramOut!=null){
							if (paramOut instanceof java.sql.Timestamp) {
		        				try{ 
		        					paramOut =(java.sql.Timestamp) paramOut;
		        				}catch (Exception e) {paramOut="java.sql.Timestamp";}
							} else if(paramOut instanceof java.sql.Date){
								try{
									paramOut = rs.getTimestamp(1);
								} catch (Exception e) {paramOut="java.sql.Date";}
							}
						}
			    		rs.close();
						s.close();
			    		copyParams.set(o, paramOut);
			    		formResult.getOutputFields().put(calculatedParamNames.get(o), paramOut);
			    	}
			    	int count = 0;

		    		s = conn.prepareStatement(sql.toString());
					applyParameters(s, copyParams);
					if(schema==null){
						count = s.executeUpdate();
					} else try{ //farkli bir yere koymaya calisiyor
						count = s.executeUpdate();
					} catch(Exception e){
							PreparedStatement s2 = conn.prepareStatement("create table "+schema+"."+t.getDsc()+" as select * from " +t.getDsc()+ " where 1=2" );
							s2.executeUpdate();
							s2.close();
							String pk ="";
							for(W5TableParam p:t.get_tableParamList()){
								if(pk.length()>0)pk+=",";
								pk+=p.getExpressionDsc();
							}
							s2 = conn.prepareStatement("alter table "+schema+"."+t.getDsc()+" add constraint PK_APPROVAL_"+t.getTableId()+" primary key ("+pk+")" );
							s2.executeUpdate();
							s2.close();
							count = s.executeUpdate();
					}
					s.close();
					if(FrameworkSetting.hibernateCloseAfterWork)conn.close();
					
			    	return count;
				}
	    	});
	    	

		} catch(Exception e){
			if(FrameworkSetting.debug)e.printStackTrace();
//			logException(e.getMessage(),PromisUtil.uInt(PromisCache.appSettings.get(0).get("default_customization_id")),0);
			throw new IWBException("sql","Form Copy",formResult.getFormId(),GenericUtil.replaceSql(sql.toString(), copyParams), e.getMessage(), e.getCause());
		} finally {
//			session.close();
		}
		
		
	}
	
	
	@Override
	public Object[] listObjectCommentAndAttachUsers(Map<String, Object> scd, Map<String,String> requestParams){
		int tableId = GenericUtil.uInt(requestParams.get("table_id"));
		int tablePk = GenericUtil.uInt(requestParams.get("table_pk"));
		int customizationId= (Integer)scd.get("customizationId");
		int sessionUserId=(Integer)scd.get("userId");
		int recordInsertUserId = 0, recordVersionUserId = 0, assignedUserId = 0;
		boolean recordInsertUserFlag = false, recordVersionUserFlag = false, assignedUserFlag = false, customizationFlag = false;
		Set<Integer> extraUserIds = new HashSet<Integer>();
		List<Object[]> l = executeSQLQuery("select t.dsc, tp.expression_Dsc" +
				",(select count(1) from iwb.w5_Table_Field tf where tf.table_Id=t.table_Id AND tf.customization_Id=t.customization_Id AND lower(tf.dsc)='insert_user_id') insert_user_id_count" +
				",(select count(1) from iwb.w5_Table_Field tf where tf.table_Id=t.table_Id AND tf.customization_Id=t.customization_Id AND lower(tf.dsc)='version_user_id') version_user_id_count" +
				",(select count(1) from iwb.w5_Table_Field tf where tf.table_Id=t.table_Id AND tf.customization_Id=t.customization_Id AND lower(tf.dsc)='assigned_user_id') assigned_user_id_count " +
				",(select count(1) from iwb.w5_Table_Field tf where tf.table_Id=t.table_Id AND tf.customization_Id=t.customization_Id AND lower(tf.dsc)='customization_id') customization_id_count " +
				"from iwb.w5_Table t, iwb.W5_Table_Param tp " +
				"where t.customization_Id=? AND t.table_Id=? AND t.table_Id=tp.table_Id AND tp.tab_Order=1", customizationId, tableId);
		if(!l.isEmpty()){
			String tableDsc = (l.get(0)[0]).toString();
			String tablePkDsc = (l.get(0)[1]).toString();
			recordInsertUserFlag = GenericUtil.uInt((l.get(0)[2]).toString())!=0;
			recordVersionUserFlag = GenericUtil.uInt((l.get(0)[3]).toString())!=0;
			assignedUserFlag = GenericUtil.uInt((l.get(0)[4]).toString())!=0;
			customizationFlag = GenericUtil.uInt((l.get(0)[5]).toString())!=0;
			if(recordInsertUserFlag || recordVersionUserFlag || assignedUserFlag){
				StringBuilder sql=new StringBuilder();
				sql.append("select ");
				int fieldCount = 0;
				if(recordInsertUserFlag){
					sql.append("x.insert_user_id,");
					fieldCount++;
				}
				if(recordVersionUserFlag){
					sql.append("x.version_user_id,");
					recordVersionUserId= recordInsertUserFlag ? 1:0;
					fieldCount++;
				}
				if(assignedUserFlag){
					sql.append("x.assigned_user_id,");
					assignedUserId=(recordInsertUserFlag ? 1:0) + (recordVersionUserFlag ? 1:0);
					fieldCount++;
				}
				sql.setLength(sql.length()-1);
				sql.append(" from ").append(tableDsc).append(" x where x.").append(tablePkDsc).append("=?");
				if(customizationFlag)sql.append(" AND x.customization_id=?");
				List<Object[]> l2 = customizationFlag ? executeSQLQuery(sql.toString(), tablePk, customizationId) : executeSQLQuery(sql.toString(), tablePk);
				if(l2!=null && !l2.isEmpty()){
					if(recordInsertUserFlag)extraUserIds.add(GenericUtil.uInt(fieldCount==1 ? l2.get(0):l2.get(0)[0]));
					if(recordVersionUserFlag)extraUserIds.add(GenericUtil.uInt(fieldCount==1 ? l2.get(0):l2.get(0)[recordVersionUserId]));
					if(assignedUserFlag)extraUserIds.add(GenericUtil.uInt(fieldCount==1 ? l2.get(0):l2.get(0)[assignedUserId]));
				}
			}
		}
		/* TODO
		List<Object> newCommentUsers = executeSQLQuery("select distinct c.comment_user_id from iwb.w5_comment c where c.customization_id=? and c.table_id=? AND c.table_pk=? AND not exists(select 1 from iwb.w5_notification n where n.customization_id=c.customization_id and n.active_flag=1 AND n.notification_tip=1 AND n.table_id=c.table_id AND n.table_pk=c.table_pk AND n.user_id=c.comment_user_id)", customizationId,tableId, tablePk);
		if(newCommentUsers!=null)for(Object o:newCommentUsers){
			extraUserIds.add(PromisUtil.uInt(o));
		}
		List<Object> newAttachUsers = executeSQLQuery("select distinct c.upload_user_id from iwb.w5_file_attachment c where c.customization_id=? and c.table_id=? AND c.table_pk=? AND not exists(select 1 from iwb.w5_notification n where n.customization_id=c.customization_id and n.active_flag=1 AND n.notification_tip=2 AND n.table_id=c.table_id AND n.table_pk=c.table_pk AND n.user_id=c.upload_user_id)", customizationId,tableId, tablePk);
		if(newAttachUsers!=null)for(Object o:newAttachUsers){
			extraUserIds.add(PromisUtil.uInt(o));
		} */
		extraUserIds.remove(sessionUserId);
		return extraUserIds.isEmpty() ? null : extraUserIds.toArray();
	}
	
	@Override
	public int insertFormTable(W5FormResult formResult, String schema, String paramSuffix) {
		W5Form f = formResult.getForm();
		int customizationId = formResult.isDev() ? 0 : (Integer)formResult.getScd().get("customizationId");
    	W5Table t= FrameworkCache.getTable(customizationId, f.getObjectId());//formResult.getForm().get_sourceTable();
		if(FrameworkSetting.vcs && FrameworkSetting.vcsServer && t.getVcsFlag()!=0)
			throw new IWBException("vcs","Form Insert",formResult.getFormId(),null, "VCS Server not allowed to insert VCS Table", null);
			
		StringBuilder	sql = new StringBuilder(), postSql = new StringBuilder();
    	sql.append("insert into ");
    	if(schema!=null)sql.append(schema).append(".");
    	sql.append(t.getDsc()).append(" ( ");
    	postSql.append(" values (");
    	final	List<Object>	insertParams = new ArrayList<Object>();
    	boolean	b = false;
		int	paramCount=0;
		Map<Integer, String> calculatedParams = new HashMap<Integer, String>();
		Map<Integer, String> calculatedParamNames = new HashMap<Integer, String>();
		Set<String> usedFields = new HashSet<String>();
		
		Map<Integer, W5FormModule> moduleMap = null;
		if(formResult.getForm().getRenderTip()!=0){
			moduleMap = new HashMap<Integer, W5FormModule>();
			if(formResult.getForm().get_moduleList()!=null)for(W5FormModule m : formResult.getForm().get_moduleList())
				moduleMap.put(m.getFormModuleId(), m);
		}
		
    	for(W5FormCell x: f.get_formCells())if(x.getNrdTip()!=1 && x.getObjectDetailId()!=0 && x.getControlTip()<100){//disabled(1) degil VE freeField(getObjectDetailId()!=0) degilse
    		W5TableField tf = (W5TableField)x.get_sourceObjectDetail();
    		if(tf==null)continue; //error. aslinda olmamasi lazim
    		if(tf.getCanInsertFlag()==0)continue; //x.getCanInsert()!=0

    		//view AND update control
    		if(/*!PromisUtil.accessControl(formResult.getScd(), p1.getAccessViewTip(), p1.getAccessViewRoles(), p1.getAccessViewUsers()) ||*/
    			!GenericUtil.accessControl(formResult.getScd(), tf.getAccessInsertTip(), tf.getAccessInsertRoles(), tf.getAccessInsertUsers()))continue;//access control
    		
    		//module view control
    		if(moduleMap!=null && moduleMap.get(x.getFormModuleId())!=null){
    			W5FormModule module = moduleMap.get(x.getFormModuleId());
    			if(!GenericUtil.accessControl(formResult.getScd(), module.getAccessViewTip(), module.getAccessViewRoles(), module.getAccessViewUsers()))
    				continue;
    		}
    		

    		Object psonuc = GenericUtil.prepareParam(tf, formResult.getScd(), formResult.getRequestParams(), x.getSourceTip(), null, x.getNotNullFlag(), x.getDsc()+paramSuffix, x.getDefaultValue(), formResult.getErrorMap());

    		if(formResult.getErrorMap().isEmpty()){
	    		if(b){sql.append(" , "); postSql.append(" , ");}else b=true;
	    		usedFields.add(tf.getDsc());
				sql.append(tf.getDsc());
				if(x.getOutFlag()!=0){ // bu field outputParam'a yazilacak
					if(x.getSourceTip()==4){ // calculated, sql calisacak sonra deger verilecek
	    				calculatedParams.put(paramCount, (String)psonuc);
	    				calculatedParamNames.put(paramCount, x.getDsc());
					} else {
						formResult.getOutputFields().put(x.getDsc(), psonuc);
					}
    				postSql.append(" ? ");
    				insertParams.add(null);
    				paramCount++;
				} else { //calculated, outputa yazilmadan direk
		    		if(x.getSourceTip()==4){ // calculated, sql calisacak sonra deger verilecek
	    				postSql.append(" ( ").append(psonuc).append(" ) ");
	    			} else {
	    				postSql.append(" ? ");
	    				insertParams.add(psonuc);
	    				paramCount++;
	    			}
				}
    		}
    	}

    	for(W5TableField p1: t.get_tableFieldList())if(p1.getCanInsertFlag()!=0 && !usedFields.contains(p1.getDsc()))switch(p1.getSourceTip()){
    	case	4: // calculated Fieldlar icin
    		if(b){sql.append(" , "); postSql.append(" , ");}else b=true;
    		usedFields.add(p1.getDsc());
			sql.append(p1.getDsc());
			calculatedParams.put(paramCount, GenericUtil.filterExt(p1.getDefaultValue(), formResult.getScd(), formResult.getRequestParams(), null).toString());
			calculatedParamNames.put(paramCount, p1.getDsc());
			postSql.append(" ? ");
			insertParams.add(null);
			paramCount++;
			break;
    	case	2:// session
    		Object psonuc = GenericUtil.prepareParam(p1, formResult.getScd(), formResult.getRequestParams(), (short)-1, null, (short)0, null, null, formResult.getErrorMap());
    		if(psonuc!=null){
	    		if(b){sql.append(" , "); postSql.append(" , ");}else b=true;
	    		usedFields.add(p1.getDsc());
				sql.append(p1.getDsc());
				postSql.append(" ? ");
				insertParams.add(psonuc);
				paramCount++;
    		}

			break;
    	case	9:// UUID
    		Object psonuc2 = GenericUtil.prepareParam(p1, formResult.getScd(), formResult.getRequestParams(), (short)-1, null, (short)0, null, null, formResult.getErrorMap());
    		if(psonuc2!=null){
	    		if(b){sql.append(" , "); postSql.append(" , ");}else b=true;
	    		usedFields.add(p1.getDsc());
				sql.append(p1.getDsc());
				postSql.append(" ? ");
				insertParams.add(psonuc2);
				paramCount++;
				
				formResult.getOutputFields().put(p1.getDsc(), psonuc2);
    		}

			break;
    	case	8:// Global Nextval
    		Object psonuc3 = GenericUtil.prepareParam(p1, formResult.getScd(), formResult.getRequestParams(), (short)-1, null, (short)0, null, null, formResult.getErrorMap());
    		if(psonuc3!=null){
	    		if(b){sql.append(" , "); postSql.append(" , ");}else b=true;
	    		usedFields.add(p1.getDsc());
				sql.append(p1.getDsc());
				postSql.append(" ? ");
				insertParams.add(psonuc3);
				paramCount++;
				
				formResult.getOutputFields().put(p1.getDsc(), psonuc3);
    		}

			break;			
    	}

		if(!formResult.getErrorMap().isEmpty())return 0;
		
    	if(usedFields.isEmpty()){//sorun var
			throw new IWBException("validation","Form Insert",formResult.getFormId(),null, "No Used Fields", null);
    	}


		if(f.get_versioningFlag()){ // eger versioning varsa, version'lari degistir ve version_no'yu arttir
			sql.append(", version_no, insert_user_id, version_user_id, insert_dttm, version_dttm ");
			postSql.append(" , 1, ?, ?, iwb.fnc_sysdate(?), iwb.fnc_sysdate(?) ");
			insertParams.add(formResult.getScd().get("userId"));
			insertParams.add(formResult.getScd().get("userId"));
			insertParams.add(formResult.getScd().get("customizationId"));
			insertParams.add(formResult.getScd().get("customizationId"));
		}

    	sql.append(" ) ").append(postSql).append(")");
	


    	try {
	    	final 		Map<Integer, W5FormModule> moduleMap2 = moduleMap;

	    	return getCurrentSession().doReturningWork(new ReturningWork<Integer>() {
				@Override
				public Integer execute(Connection conn) throws SQLException {
					PreparedStatement s = null;
			    	int count = 0;
					for(Integer o : calculatedParams.keySet()){ // calculated ve output edilecek parametreler hesaplaniyor once
			    		String seq = calculatedParams.get(o);
			    		if(seq.endsWith(".nextval"))seq="nextval('"+seq.substring(0,seq.length()-8)+"')";
			    		s = conn.prepareStatement("select " + seq + " ");//from dual
			    		ResultSet rs = s.executeQuery();
			    		rs.next();   			    		
			    		Object paramOut = rs.getObject(1);
						if(paramOut!=null){
							if (paramOut instanceof java.sql.Timestamp) {
		        				try{ 
		        					paramOut =(java.sql.Timestamp) paramOut;
		        				}catch (Exception e) {paramOut="java.sql.Timestamp";}
							} else if(paramOut instanceof java.sql.Date){
								try{
									paramOut = rs.getTimestamp(1);
								} catch (Exception e) {paramOut="java.sql.Date";}
							}
						}
			    		
			    		rs.close();
						s.close();
			    		insertParams.set(o, paramOut);
			    		formResult.getOutputFields().put(calculatedParamNames.get(o), paramOut);
			    	}
		    		s = conn.prepareStatement(sql.toString());
					applyParameters(s, insertParams);
					if(schema==null){
						count = s.executeUpdate();

					} else try{ //farkli bir yere koymaya calisiyor
						count = s.executeUpdate();
					} catch(Exception e){
							PreparedStatement s2 = conn.prepareStatement("create table "+schema+"."+t.getDsc()+" as select * from " +t.getDsc()+ " where 1=2" );
							s2.executeUpdate();
							s2.close();
							String pk ="";
							for(W5TableParam p:t.get_tableParamList()){
								if(pk.length()>0)pk+=",";
								pk+=p.getExpressionDsc();
							}
							s2 = conn.prepareStatement("alter table "+schema+"."+t.getDsc()+" add constraint PK_APPROVAL_"+t.getTableId()+" primary key ("+pk+")" );
							s2.executeUpdate();
							s2.close();
							count = s.executeUpdate();
					}
					s.close();
					int customizationId = (Integer)formResult.getScd().get("customizationId");

					
					if(t.getTableId()!=329 && FrameworkCache.getAppSettingIntValue(customizationId, "make_comment_flag")!=0 && t.getMakeCommentFlag()!=0){
						PreparedStatement s2 = conn.prepareStatement("update iwb.w5_comment set table_pk=?::text where customization_id=? AND table_id=? AND table_pk=?::text" );
						applyParameters(s2, formResult.getOutputFields().get(t.get_tableParamList().get(0).getExpressionDsc()),customizationId,t.getTableId(),formResult.getRequestParams().get("_tmpId"));
						s2.executeUpdate();
						s2.close();
					}
					
					if(t.getTableId()!=370 && FrameworkCache.getAppSettingIntValue(customizationId, "row_based_security_flag")!=0 && !GenericUtil.isEmpty(t.getAccessTips())){
						PreparedStatement s2 = conn.prepareStatement("update iwb.w5_access_control set table_pk=?::int where customization_id=? AND table_id=? AND table_pk=?::int" );
						applyParameters(s2, formResult.getOutputFields().get(t.get_tableParamList().get(0).getExpressionDsc()),customizationId,t.getTableId(),formResult.getRequestParams().get("_tmpId"));
						s2.executeUpdate();
						s2.close();
					}

					if(FrameworkSetting.hibernateCloseAfterWork)conn.close();
					
					return count;

				}
	    	});
	    	
    	} catch(IWBException pe){ 
    		throw pe;
		} catch(Exception e){
			if(FrameworkSetting.debug)e.printStackTrace();
			if(e.getCause()!=null && e.getCause() instanceof SQLException){
				throw new IWBException("sql","Form Insert",formResult.getFormId(),GenericUtil.replaceSql(sql.toString(), insertParams), ((SQLException)(e.getCause())).getLocalizedMessage(), e.getCause());
			}
//			logException(e.getMessage(),PromisUtil.uInt(PromisCache.appSettings.get(0).get("default_customization_id")),0);
			throw new IWBException("sql","Form Insert(Unhandled)",formResult.getFormId(),GenericUtil.replaceSql(sql.toString(), insertParams), e.getMessage(), e.getCause());
		} finally {
//			session.close();
		}
		
    	/*if(formResult.getForm().get_sourceTable() != null && formResult.getForm().get_sourceTable().getDoInsertLogFlag()!=0){ Bunun yapılabilmesi için önce logTableRecord'un değişmesi lazım.
        	for(W5TableParam x: t.get_tableParamList()){
        		if(x.getNotNullFlag()!=0){
	        		Object psonuc = PromisUtil.prepareParam(x, formResult.getScd(), formResult.getRequestParams(), (short)-1, null, (short)0, x.getDsc() + paramSuffix, null, formResult.getErrorMap());
	        		formResult.getPkFields().put(x.getDsc(), psonuc);
        		}
    		}
    		logTableRecord(formResult);
    	}*/
	}

	@Override
	public boolean deleteTableRecord(W5FormResult formResult, String paramSuffix) {
		W5Form f = formResult.getForm();
		W5Table t = FrameworkCache.getTable(formResult.getScd(), f.getObjectId());
		if(FrameworkSetting.vcs && FrameworkSetting.vcsServer && t.getVcsFlag()!=0)
			throw new IWBException("vcs","Form Record Update",formResult.getFormId(),null, "VCS Server not allowed to delete VCS Table", null);
		StringBuilder	sql = new StringBuilder();
    	sql.append("delete from ").append(t.getDsc()).append(" where ");
    	List<Object> realParams = new ArrayList<Object>();
    	boolean	b = false;
    	for(W5TableParam x: t.get_tableParamList())if(x.getNotNullFlag()!=0){
    		if(b)sql.append(" AND "); else b=true;
    		sql.append(x.getExpressionDsc()).append(" = ? ");
    		Object psonuc = GenericUtil.prepareParam(x, formResult.getScd(), formResult.getRequestParams(), (short)-1, null, (short)0, x.getDsc()+paramSuffix, null, formResult.getErrorMap());
    		realParams.add(psonuc);
    		formResult.getPkFields().put(x.getDsc(), psonuc);
    	}
    	
    	if(t.getDoDeleteLogFlag()!=0)logTableRecord(formResult, paramSuffix);
    	Session session = getCurrentSession();
    	try {
    		b = applyParameters(session.createSQLQuery(sql.toString()),realParams).executeUpdate()>0;
    		
    	} catch(Exception e){
    		throw new IWBException("sql","Form Delete",formResult.getFormId(),GenericUtil.replaceSql(sql.toString(),realParams), e.getMessage(), e.getCause());
    	} finally{
//    		session.close();
    	}
    	return b;
	}

	private void organizeQueryFields4WSMethod(Map<String, Object> scd,  final W5Query q, final short insertFlag) {
		final List<W5QueryFieldCreation> updateList = new ArrayList<W5QueryFieldCreation>();
		final List<W5QueryFieldCreation> insertList = new ArrayList<W5QueryFieldCreation>();
    	final	Map<String, W5QueryFieldCreation>	existField = new HashMap<String, W5QueryFieldCreation>();
    	final	List<Object> sqlParams = new ArrayList();
    	List<W5QueryFieldCreation>	existingQueryFields =find("from W5QueryFieldCreation t where t.queryId=?", q.getQueryId());
    	for(W5QueryFieldCreation	field: existingQueryFields){
    		existField.put(field.getDsc().toLowerCase(FrameworkSetting.appLocale), field);
    	}
    	if(q.getSqlSelect().equals("*")){
	    	W5WsMethodParam parentParam = (W5WsMethodParam)getCustomizedObject("from W5WsMethodParam p where p.outFlag=1 AND p.wsMethodId=? AND p.paramTip=10 AND p.customizationId=?", q.getMainTableId(), (Integer)scd.get("customizationId"), "Parent WSMethodParam");
	    	List<W5WsMethodParam> outParams = find("from W5WsMethodParam p where p.outFlag=1 AND p.wsMethodId=? AND p.parentWsMethodParamId=? AND p.customizationId=?", q.getMainTableId(), parentParam.getWsMethodParamId(), (Integer)scd.get("customizationId"));
	    	int j=0;
	    	for(W5WsMethodParam wsmp:outParams){
				String columnName = wsmp.getDsc().toLowerCase(FrameworkSetting.appLocale);
				if(insertFlag!=0 && existField.get(columnName)==null){
					W5QueryFieldCreation field = new W5QueryFieldCreation();
					field.setDsc(columnName);
					field.setTabOrder((short)(j+1));
					field.setQueryId(q.getQueryId());
					field.setFieldTip(wsmp.getParamTip());
					field.setInsertUserId((Integer)scd.get("userId"));
					field.setVersionUserId((Integer)scd.get("userId"));
					field.setVersionDttm(new java.sql.Timestamp(new java.util.Date().getTime()));
					field.setProjectUuid((String)scd.get("projectId"));
					field.setMainTableFieldId(wsmp.getWsMethodParamId());
					field.setQueryFieldId(GenericUtil.getGlobalNextval("iwb.seq_query_field", scd!=null ? (String)scd.get("projectId"):null, scd!=null ? (Integer)scd.get("userId"):0, scd!=null ? (Integer)scd.get("customizationId"):0));
					insertList.add(field);
					j++;
				}
				existField.remove(columnName);
	    	}
    	} else {
    		String[] lines=q.getSqlSelect().split("\n");
	    	int j=0;
    		for(String p:lines)if(!GenericUtil.isEmpty(p)){
    			String columnName = p.substring(0,p.indexOf(':'));
				if(insertFlag!=0 && existField.get(columnName)==null){
					W5QueryFieldCreation field = new W5QueryFieldCreation();
					field.setDsc(columnName);
					field.setTabOrder((short)(j+1));
					field.setQueryId(q.getQueryId());
					field.setFieldTip((short)1);
					field.setInsertUserId((Integer)scd.get("userId"));
					field.setVersionUserId((Integer)scd.get("userId"));
					field.setVersionDttm(new java.sql.Timestamp(new java.util.Date().getTime()));
					field.setProjectUuid((String)scd.get("projectId"));
					field.setMainTableFieldId(0);
					field.setQueryFieldId(GenericUtil.getGlobalNextval("iwb.seq_query_field", scd!=null ? (String)scd.get("projectId"):null, scd!=null ? (Integer)scd.get("userId"):0, scd!=null ? (Integer)scd.get("customizationId"):0));
					insertList.add(field);
					j++;
				}
				existField.remove(columnName);
    		}
    	}
		boolean vcs = FrameworkSetting.vcs;
		if(insertFlag!=0 && insertList!=null)for(W5QueryFieldCreation field:insertList){
			saveObject(field);
			if(vcs)saveObject(new W5VcsObject(scd, 9, field.getQueryFieldId()));
		}
		for(W5QueryFieldCreation field:updateList){
			updateObject(field);
			if(vcs)makeDirtyVcsObject(scd, 9, field.getQueryFieldId());
		}
	}
	
	private void organizeQueryFields4TSMeasurement(Map<String, Object> scd,  final W5Query q, final short insertFlag) {
    	final	Map<String, W5QueryFieldCreation>	existField = new HashMap<String, W5QueryFieldCreation>();
    	final	List<Object> sqlParams = new ArrayList();
    	List<W5QueryFieldCreation>	existingQueryFields =find("from W5QueryFieldCreation t where t.queryId=?", q.getQueryId());
    	for(W5QueryFieldCreation	field: existingQueryFields){
    		existField.put(field.getDsc().toLowerCase(FrameworkSetting.appLocale), field);
    	}
		
	}

	@Override
	public void organizeQueryFields(Map<String, Object> scd,  final int queryId, final short insertFlag) {
		String projectUuid = (String)scd.get("projectId");
		W5Project po = FrameworkCache.wProjects.get(projectUuid);
		
		if(po.getSetSearchPathFlag()!=0){
			executeUpdateSQLQuery("set search_path="+po.getRdbmsSchema());
		}
		final int userId = (Integer)scd.get("userId");
		final List<W5QueryFieldCreation> updateList = new ArrayList<W5QueryFieldCreation>();
		final List<W5QueryFieldCreation> insertList = new ArrayList<W5QueryFieldCreation>();
		
		for(final W5Query query:(List<W5Query>)find("from W5Query t where (?=-1 OR t.queryId=?)", queryId, queryId)){
			if(query.getQuerySourceTip()==1376){
				organizeQueryFields4WSMethod(scd, query, insertFlag);
				continue;
			} else if(query.getQuerySourceTip()==2709){
					organizeQueryFields4TSMeasurement(scd, query, insertFlag);
					continue;
			}
	    	final	Map<String, W5QueryFieldCreation>	existField = new HashMap<String, W5QueryFieldCreation>();
	    	final	List<Object> sqlParams = new ArrayList();
	    	List<W5QueryFieldCreation>	existingQueryFields =find("from W5QueryFieldCreation t where t.queryId=?", queryId);
	    	for(W5QueryFieldCreation	field: existingQueryFields){
	    		existField.put(field.getDsc().toLowerCase(FrameworkSetting.appLocale), field);
	    	}
	    	
	    	StringBuilder sql= new StringBuilder();
	    	sql.append("select ").append(query.getSqlSelect());
	    	sql.append(" from ").append(query.getSqlFrom());
	    	if(query.getSqlWhere()!=null && query.getSqlWhere().trim().length()>0)sql.append(" where ").append(query.getSqlWhere().trim());
	    	if(query.getSqlGroupby()!=null && query.getSqlGroupby().trim().length()>0 && query.getQueryTip()!=9) //group by connect olmayacak
	    		sql.append(" group by ").append(query.getSqlGroupby().trim());
	    	if(query.getSqlPostSelect()!=null && query.getSqlPostSelect().trim().length()>2){
	    		sql = new StringBuilder(sql.length()+100).append("select z.*,").append(query.getSqlPostSelect()).append(" from (").append(sql).append(") z");
	    	}
	    	Object[] oz = DBUtil.filterExt4SQL(sql.toString(), scd, null, null);
	    	final String sqlStr = ((StringBuilder)oz[0]).toString();
			if(oz[1]!=null)sqlParams.addAll((List)oz[1]); else
	    	for(int qi=0;qi<sqlStr.length();qi++)if(sqlStr.charAt(qi)=='?')sqlParams.add(null);
	    	
	    	try{
	    		getCurrentSession().doWork(new Work() {
	    			@Override
					public void execute(Connection conn) throws SQLException {
	    				PreparedStatement stmt = null;
	        			ResultSet rs = null;
	        			stmt = conn.prepareStatement(sqlStr);
	        			if(sqlParams.size()>0)applyParameters(stmt, sqlParams);
	        			rs = stmt.executeQuery();
	        			ResultSetMetaData meta = rs.getMetaData();
	        			Map<String, W5TableField> fieldMap = new HashMap<String, W5TableField>();
	        			W5Table t = FrameworkCache.getTable(0, query.getMainTableId());
	        			if(t!=null)for(W5TableField f:t.get_tableFieldList()){
	        				fieldMap.put(f.getDsc().toLowerCase(), f);
	        			}

	        			int	columnNumber = meta.getColumnCount();
        				for(int i=1, j=0;i<=columnNumber;i++){
        					String columnName = meta.getColumnName(i).toLowerCase(FrameworkSetting.appLocale);
        					if(insertFlag!=0 && existField.get(columnName)==null){ // eger daha onceden boyle tanimlanmis bir field yoksa
	        					W5QueryFieldCreation field = new W5QueryFieldCreation();
	        					field.setDsc(columnName);
	        					field.setCustomizationId((Integer)scd.get("customizationId"));
	        					if(columnName.equals("insert_user_id") || columnName.equals("version_user_id"))
	        						field.setPostProcessTip((short)53);
	        					field.setTabOrder((short)(i));
	        					field.setQueryId(query.getQueryId());
	        					field.setFieldTip((short)DBUtil.java2iwbType(meta.getColumnType(i)));
	        					if (field.getFieldTip() == 4) {
	        						//numeric değerde ondalık varsa tipi 3 yap
	            					int sc = meta.getScale(i);
	            					if (sc>0)field.setFieldTip((short)3);
	        				    }
	        					field.setInsertUserId(userId);
	        					field.setVersionUserId(userId);
	        					field.setVersionDttm(new java.sql.Timestamp(new java.util.Date().getTime()));
	        					field.setProjectUuid((String)scd.get("projectId"));
	        					if(fieldMap.containsKey(columnName.toLowerCase())) {
	        						W5TableField tf = fieldMap.get(columnName.toLowerCase());
	        						field.setMainTableFieldId(tf.getTableFieldId());
	        						if(tf.getDefaultLookupTableId()>0){
        								switch(tf.getDefaultControlTip()){
			        					case	6: field.setPostProcessTip((short)10);break;//combo static
			        					case	8: case 58: field.setPostProcessTip((short)11);break;//lov-combo static
			        					case	7: case 10: field.setPostProcessTip((short)12);break;//combo query
			        					case	15: case 59:field.setPostProcessTip((short)13);break;//lov-combo query
			        					case	51: case 52: field.setPostProcessTip(tf.getDefaultControlTip());break;//combo static
		        						}
		        						if(tf.getDefaultControlTip()!=0)field.setLookupQueryId(tf.getDefaultLookupTableId());
	        						}
	        					}
	        					field.setQueryFieldId(GenericUtil.getGlobalNextval("iwb.seq_query_field", (String)scd.get("projectId"), (Integer)scd.get("userId"), (Integer)scd.get("customizationId")));
	        					insertList.add(field);
	        					j++;
        					} else if(existField.get(columnName)!=null && (existField.get(columnName).getTabOrder()!=i || (existField.get(columnName).getMainTableFieldId()==0 && fieldMap.containsKey(columnName.toLowerCase())))){
        						W5QueryFieldCreation field = existField.get(columnName);
        						field.setTabOrder((short)(i));
        						field.setVersionUserId(userId);
	        					field.setVersionDttm(new java.sql.Timestamp(new java.util.Date().getTime()));
	        					if(field.getMainTableFieldId()==0 && fieldMap.containsKey(columnName.toLowerCase())) {
	        						field.setMainTableFieldId(fieldMap.get(columnName.toLowerCase()).getTableFieldId());
	        					}
        						updateList.add(field);
        					}
        					existField.remove(columnName);
        				}
	        			rs.close();
	        			stmt.close();
	        			if(FrameworkSetting.hibernateCloseAfterWork)conn.close();
	    			}
	    		});
			        			
				for(W5QueryFieldCreation field:existField.values()){ // icinde bulunmayanlari negatif olarak koy
					field.setTabOrder((short)-Math.abs(field.getTabOrder()));
					field.setPostProcessTip((short)99);
					field.setVersionUserId(userId);
					field.setVersionDttm(new java.sql.Timestamp(new java.util.Date().getTime()));
					updateList.add(field);
				}
	    	} catch(Exception e){
	    		if(FrameworkSetting.debug)e.printStackTrace();
	    		if(queryId!=-1)throw new IWBException("sql","Query Field Creation",queryId,sql.toString(), e.getMessage(), e.getCause());
	    	}
		}
		boolean vcs = FrameworkSetting.vcs;
		if(insertFlag!=0 && insertList!=null)for(W5QueryFieldCreation field:insertList){
			saveObject(field);
			if(vcs)saveObject(new W5VcsObject(scd, 9, field.getQueryFieldId()));
		}
		for(W5QueryFieldCreation field:updateList){
			updateObject(field);
			if(vcs)makeDirtyVcsObject(scd, 9, field.getQueryFieldId());
		}
	}
	
	@Override
	public void reloadJobsCache(){
		//Job Schedule
		try{
			FrameworkCache.wJobs.clear();
			FrameworkCache.wJobs.addAll(find("from W5JobSchedule x where x.activeFlag=1 and x.actionStartTip=1"));
			for (W5JobSchedule data : FrameworkCache.wJobs){
				String userId = Integer.toString(data.getExecuteUserId());
				String roleId = Integer.toString(data.getExecuteRoleId());
				int customization_id = data.getCustomizationId();
				List<Map<String, Object>> res = executeSQLQuery2Map("select r.user_role_id, u.customization_id from iwb.w5_user u, iwb.w5_user_role r where u.customization_id=r.customization_id and u.user_id=r.user_id and u.user_id=" + userId + " and r.role_id=" + roleId + " and r.customization_id=" + customization_id, null);			
				if(res!=null)for (Map<String, Object> usr : res){			
					data.set_userRoleId(GenericUtil.uInteger((String) usr.get("user_role_id")));
				}			
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void reloadLocaleMsgsCache2(int cid){
		String whereSql = cid!=-1 ? ( " where x.customization_id="+cid) :"";
		List<Object[]> l = (List<Object[]>)executeSQLQuery("select x.customization_id, x.locale, x.locale_msg_key, x.dsc from iwb.w5_locale_msg x "+whereSql+" order by customization_id, locale, locale_msg_key, dsc");
		if(l!=null)for(Object[] m : l){
			LocaleMsgCache.set2((int)m[0], (String)m[1], (String)m[2], (String)m[3]);
		}

		//Publish Locale Msgs 
		if(cid==-1 || cid==0){
			List<String> publishSet = executeSQLQuery("select x.locale_msg_key from iwb.w5_locale_msg x where  x.customization_id=0 AND x.publish_flag = 1 AND x.locale='en' order by locale, locale_msg_key, dsc");
			if(publishSet != null)for(String m : publishSet)LocaleMsgCache.add2publish(m);
		}
	}
	
	@Override
	public void reloadErrorMessagesCache(){
		FrameworkCache.wExceptions.clear();
		List l = executeSQLQuery("select exc_code, locale_msg_key from iwb.w5_exception_filter");
		if(l!=null)for(Object[] m : (List<Object[]>)l){
			FrameworkCache.wExceptions.put((String)m[0], (String)m[1]);
		}	
	}
	
	@Override
	public	void reloadTableAccessConditionSQLs(){
		FrameworkCache.wAccessConditionSqlMap.clear();
		try{
			List<W5TableAccessConditionSql> l = find("from W5TableAccessConditionSql t");
			for(W5TableAccessConditionSql s:l){
				FrameworkCache.wAccessConditionSqlMap.put(s.getAccessConditionSqlId(), s);
			}	
		} catch (Exception e){}
	}
	
	@Override
	public void reloadApplicationSettingsCache(int cid){
		if(cid==-1)FrameworkCache.appSettings.clear(); else FrameworkCache.appSettings.remove(cid);
		List<Object[]> lm = (List<Object[]>)executeSQLQuery("select customization_id, dsc, val from iwb.w5_app_setting where ?=-1 OR customization_id=?",cid,cid);
		if(lm!=null)for(Object[] m : lm){
			int customizationId = (int)m[0];
			Map<String, String> subMap = FrameworkCache.appSettings.get(customizationId);
			if(subMap==null){
				subMap = new HashMap<String, String>();
				FrameworkCache.appSettings.put(customizationId, subMap);
			}
			subMap.put((String)m[1], (String)m[2]);
		}
		int default_customization_id = Integer.parseInt(FrameworkCache.appSettings.get(0).get("default_customization_id"));		 
		FrameworkCache.publishAppSettings.clear();
		List<Object> lm2 = (List<Object>)executeSQLQuery("select dsc from iwb.w5_app_setting where customization_id=? AND publish_flag!=0",default_customization_id);
		if(lm2!=null)for(Object m : lm2){
			FrameworkCache.publishAppSettings.add((String)m);
		}
	}
	
	@Override
	public void reloadProjectsCache(int cid){
		if(cid==-1)FrameworkCache.wProjects.clear();

		
		List<W5Project> lp = cid==-1 ? (List<W5Project>)find("from W5Project t"):(List<W5Project>)find("from W5Project t where t.customizationId=?",cid); 
		if(lp!=null)for(W5Project p : lp){
			FrameworkCache.wProjects.put(p.getProjectUuid(), p);
			/*if(FrameworkSetting.tsdbFlag && p.getTsdbFlag()!=0 && !GenericUtil.isEmpty(p.getTsdbUrl()))try{
				p.set_tsdb(InfluxDBFactory.connect(p.getTsdbUrl(), p.getTsdbUserName(), p.getTsdbPassWord()));
			}catch(Exception e){
				p.setTsdbFlag((short)0);
				if(FrameworkSetting.debug)e.printStackTrace();
				
			}*/
		}
		
		String ap="";
	}
	
	@Override
	public void reloadApplicationSettingsValues(){
		FrameworkSetting.debug = FrameworkCache.getAppSettingIntValue(0, "debug")!=0;
		/*if(FrameworkCache.getAppSettingIntValue(0, "dealer_flag")!=0){
			FrameworkSetting.dealerTableIds.clear();
			String x=FrameworkCache.getAppSettingStringValue(0, "dealer_table_ids");
			if(x!=null){
				String[] xs=x.split(",");
				if(xs!=null && xs.length>0)for(String q:xs)FrameworkSetting.dealerTableIds.add(GenericUtil.uInt(q));
			}
			FrameworkSetting.dealerDetailTableIds.clear();
			x=FrameworkCache.getAppSettingStringValue(0, "dealer_detail_table_ids");
			if(x!=null){
				String[] xs=x.split(",");
				if(xs!=null && xs.length>0)for(String q:xs)FrameworkSetting.dealerDetailTableIds.add(GenericUtil.uInt(q));
			}
		}*/
		
		//preload olmamasinin sebebi: approval'da herkesin farkli kayitlarinin gelmesi search formlarda
		FrameworkSetting.mq = FrameworkCache.getAppSettingIntValue(0, "mq_flag")!=0;
		FrameworkSetting.preloadWEngine= FrameworkCache.getAppSettingIntValue(0, "preload_engine");
		FrameworkSetting.chat = FrameworkCache.getAppSettingIntValue(0, "chat_flag")!=0;
//		FrameworkSetting.allowMultiLogin = FrameworkCache.getAppSettingIntValue(0, "allow_multi_login_flag")!=0;
		FrameworkSetting.profilePicture = FrameworkCache.getAppSettingIntValue(0, "profile_picture_flag")!=0;
		FrameworkSetting.alarm= FrameworkCache.getAppSettingIntValue(0, "alarm_flag")!=0;
		FrameworkSetting.sms = FrameworkCache.getAppSettingIntValue(0, "sms_flag")!=0;
		FrameworkSetting.mail = FrameworkCache.getAppSettingIntValue(0, "mail_flag")!=0;

		FrameworkSetting.vcs = FrameworkCache.getAppSettingIntValue(0, "vcs_flag")!=0;
		FrameworkSetting.vcsServer = FrameworkCache.getAppSettingIntValue(0, "vcs_server_flag")!=0;

		if(FrameworkSetting.preloadWEngine!=0)FrameworkCache.clearPreloadCache();
		
		FrameworkSetting.advancedSelectShowEmptyText= FrameworkCache.getAppSettingIntValue(0, "advanced_select_show_empty_text")!=0;
		FrameworkSetting.simpleSelectShowEmptyText= FrameworkCache.getAppSettingIntValue(0, "simple_select_show_empty_text")!=0;
		FrameworkSetting.cacheTimeoutRecord= FrameworkCache.getAppSettingIntValue(0, "cache_timeout_record")*1000;
		FrameworkSetting.crudLogSchema= FrameworkCache.getAppSettingStringValue(0, "log_crud_schema", FrameworkSetting.crudLogSchema);
		FrameworkSetting.mailSchema= FrameworkCache.getAppSettingStringValue(0, "mail_schema", FrameworkSetting.mailSchema);
		FrameworkSetting.asyncTimeout = FrameworkCache.getAppSettingIntValue(0, "async_timeout",100);
//		if(MVAUtil.appSettings.get("file_local_path")!=null)MVAUtil.localPath=MVAUtil.appSettings.get("file_local_path");
		
		FrameworkSetting.onlineUsersAwayMinute = 1000*60*FrameworkCache.getAppSettingIntValue(0,"online_users_away_minute", 3);			
		FrameworkSetting.onlineUsersLimitMinute = 1000*60*FrameworkCache.getAppSettingIntValue(0,"online_users_limit_minute", 10);
		FrameworkSetting.onlineUsersLimitMobileMinute = 1000*60*FrameworkCache.getAppSettingIntValue(0,"online_users_limit_mobile_minute", 7*24*60); //7 gun
		FrameworkSetting.tableChildrenMaxRecordNumber = FrameworkCache.getAppSettingIntValue(0,"table_children_max_record_number", 100);

		FrameworkSetting.mailPassEncrypt= FrameworkCache.getAppSettingIntValue(0, "encrypt_mail_pass")!=0;
		FrameworkSetting.cacheObject= FrameworkCache.getAppSettingIntValue(0, "cache_object_flag")!=0;

		FrameworkSetting.mobilePush= FrameworkCache.getAppSettingIntValue(0, "mobile_push_flag")!=0;
		FrameworkSetting.mobilePushProduction= FrameworkCache.getAppSettingIntValue(0, "mobile_push_production_flag")!=0;
		
		FrameworkSetting.approval= FrameworkCache.getAppSettingIntValue(0, "approval_flag")!=0;
		FrameworkSetting.liveSyncRecord= FrameworkCache.getAppSettingIntValue(0, "live_sync_record")!=0;
		
		FrameworkSetting.lookupEditFormFlag = FrameworkCache.getAppSettingIntValue(0, "lookup_edit_form_flag")!=0;
//		PromisSetting.replaceSqlSelectX = PromisCache.getAppSettingIntValue(0, "replace_sql_select_x")!=0;;
	}
	
	@Override
	public void reloadPublishLookUpsCache(){
		FrameworkCache.publishLookUps.clear();
		try{
//			int default_customization_id = Integer.parseInt(PromisCache.appSettings.get(0).get("default_customization_id"));
			for(Object m : (List<Object>)executeSQLQuery("select look_up_id from iwb.w5_look_up where customization_id=0 AND publish_flag!=0")){
				FrameworkCache.publishLookUps.add((int)m);
			}
		}catch (Exception e) {
			if(FrameworkSetting.debug)e.printStackTrace();
		}
	}
	
	@Override
	public void reloadLookUpCache(int customizationId){
		if(FrameworkCache.wLookUps.get(customizationId)!=null)FrameworkCache.wLookUps.get(customizationId).clear();
		Map<Integer, W5LookUp> subMap = new HashMap<Integer, W5LookUp>();
		FrameworkCache.wLookUps.put(customizationId, subMap);
		for(W5LookUp lookUp:(List<W5LookUp>)find("from W5LookUp t where t.customizationId=? order by  t.lookUpId", customizationId)){
			lookUp.set_detayList(new ArrayList());
			lookUp.set_detayMap(new HashMap());
			subMap.put(lookUp.getLookUpId(), lookUp);
		}
		
		for(W5LookUpDetay lookUpDetay:(List<W5LookUpDetay>)find("from W5LookUpDetay t where t.customizationId=? order by t.lookUpId, t.tabOrder", customizationId)){
			W5LookUp lookUp = FrameworkCache.wLookUps.get(lookUpDetay.getCustomizationId()).get(lookUpDetay.getLookUpId());
			lookUp.get_detayList().add(lookUpDetay);
			lookUp.get_detayMap().put(lookUpDetay.getVal(), lookUpDetay);
		}
	}
	

	
	
	@Override
	public void reloadRolesCache(int customizationId){
		if(FrameworkCache.wRoles.get(customizationId)!=null)FrameworkCache.wRoles.get(customizationId).clear();
		Map<Integer, String> subRoleMap = new HashMap<Integer, String>();
		FrameworkCache.wRoles.put(customizationId, subRoleMap);
		List l = executeSQLQuery("select r.role_id, r.dsc from iwb.w5_role r where customization_id=?", customizationId);
		if(l!=null)for(Object[] o:(List<Object[]>)l){
			int roleId = GenericUtil.uInt(o[0]);
			subRoleMap.put(roleId, (String)o[1]);
		}
	}
	
	@Override
	public void reloadMobileCache(){
		UserUtil.clearDevices();
		List<Object[]> l = (List<Object[]>)executeSQLQuery("select r.mobile_device_id, r.user_id, r.customization_id, r.device_tip, r.last_login_dttm  from iwb.w5_mobile_device r where r.active_flag=1");
		if(l!=null)for(Object[] o:l){
			UserUtil.addDevice(o[0].toString(), GenericUtil.uInt(o[1]), GenericUtil.uInt(o[2]), (short)GenericUtil.uInt(o[3]), ((Date)o[4]).getTime());
		}
	}
	
	@Override
	public void reloadConversionsCache(int customizationId){
		FrameworkCache.wConversions.clear();	
		List<W5Conversion> cnvAll = (List<W5Conversion>)find("from W5Conversion t where t.activeFlag=1 AND (?=-1 OR t.customizationId=?)",customizationId,customizationId);
		for(W5Conversion cnv:cnvAll){
			W5Table t = FrameworkCache.getTable(cnv.getCustomizationId(), cnv.getSrcTableId());
			if(t!=null){
				FrameworkCache.wConversions.put(cnv.getConversionId(), cnv);
				if(t.get_tableConversionList()!=null)t.set_tableConversionList(new ArrayList());
				t.get_tableConversionList().add(cnv);
				cnv.set_conversionColList((List<W5ConversionCol>)find("from W5ConversionCol t where t.conversionId=? order by t.conversionId, t.tabOrder", cnv.getConversionId()));
				cnv.set_conversionColMap(new HashMap());
				for(W5ConversionCol cnvCol:cnv.get_conversionColList()){
					cnv.get_conversionColMap().put(cnvCol.getConversionColId(), cnvCol);	
				}
			}
		}
	}
	@Override
	public void reloadTableActionsCache(int customizationId){
		Map<Integer, List<W5TableTrigger>> tableMap = new HashMap<Integer, List<W5TableTrigger>>();
		FrameworkCache.wTableTriggers.put(customizationId, tableMap);
		List<W5TableTrigger> l = find("from W5TableTrigger r where r.customizationId=? AND r.activeFlag=1 order by r.tableId, r.tabOrder, r.tableTriggerId", customizationId);
		for(W5TableTrigger r:l){
			List<W5TableTrigger> l2 = tableMap.get(r.getTableId());
			if(l2==null){
				l2 = new ArrayList();
				tableMap.put(r.getTableId(), l2);
			}
			l2.add(r);
		}
	}
	
	@Override
	public void reloadTablesCache(int customizationId){
//		if (PromisCache.wTables.get(customizationId)!=null) PromisCache.wTables.get(customizationId).clear();
		List<W5Table> lt = (List<W5Table>)find("from W5Table t where t.customizationId=? order by t.tableId", customizationId);
		Map<Integer, W5Table> tableMap = new HashMap<Integer, W5Table>(lt.size()*14/10);
		FrameworkCache.wTables.put(customizationId, tableMap);
		for(W5Table t:lt){
//			t.set_cachedObjectMap(new HashMap());
			tableMap.put(t.getTableId(), t);
		}
		
	
		reloadTableFieldListCache(customizationId);
		reloadTableFilterCache(customizationId);

		if(FrameworkSetting.approval)reloadApprovalCache(customizationId);

	}
	
	
	

	@Override
	public void reloadApprovalCache(int customizationId){
		if(FrameworkCache.getAppSettingIntValue(customizationId, "approval_flag")==0)return;
		//Approval ın bağlantılı olduğu w5table ların approval maplari temizleniyor
		List<Integer> keyz = new ArrayList();
		for (Integer y : FrameworkCache.wApprovals.keySet()){
			W5Approval a = FrameworkCache.wApprovals.get(y);
			if(customizationId!=a.getCustomizationId())continue;
			keyz.add(y);
			W5Table t = FrameworkCache.wTables.get(customizationId).get(a.getTableId());
			if(t.get_approvalMap()!=null){
				t.set_approvalMap(null);				
			}
		}
		for(Integer y:keyz)FrameworkCache.wApprovals.remove(y);
		
		//approval cache yenileniyor ve ilgili w5table lara ekleniyor
		List<W5Approval> al = (List<W5Approval>)find("from W5Approval t where t.activeFlag!=0 AND t.customizationId=? order by t.tableId", customizationId);
		for(W5Approval ta:al){
			FrameworkCache.wApprovals.put(ta.getApprovalId(), ta);
			W5Table t = FrameworkCache.wTables.get(customizationId).get(ta.getTableId());
			if(t.get_approvalMap()==null){
				t.set_approvalMap(new HashMap<Short,W5Approval>());
			}
			t.get_approvalMap().put(ta.getActionTip(), ta);
			ta.set_approvalStepList((List<W5ApprovalStep>)find("from W5ApprovalStep t where t.customizationId=? and t.approvalId=? order by approvalStepId", ta.getCustomizationId(),ta.getApprovalId()));
			
			if(ta.getApprovalRequestTip()!=1){//automatic degilse
				W5ApprovalStep approvedStep = new W5ApprovalStep();
				approvedStep.setApprovalStepId(901);
				approvedStep.setDsc(ta.getApprovalRequestMsg());//setDsc("onay_talep_edilecek");
				ta.get_approvalStepList().add(approvedStep);
			}
			if(ta.getApprovalFlowTip()==2){//hierarchical
				W5ApprovalStep approvedStep = new W5ApprovalStep();
				approvedStep.setApprovalStepId(902);
				approvedStep.setDsc(ta.getHierarchicalAppMsg());//setDsc("onay_yonetici_bekleniyor");
				approvedStep.seteSignFlag(ta.geteSignFlag());
				ta.get_approvalStepList().add(approvedStep);
			}
			if(ta.getApprovalFlowTip()==3){//dynamic
				W5ApprovalStep approvedStep = new W5ApprovalStep();
				approvedStep.setApprovalStepId(903);
				approvedStep.setDsc(ta.getDynamicAppMsg());//setDsc("onay_dinamik_bekleniyor");
				approvedStep.seteSignFlag(ta.geteSignFlag());
				ta.get_approvalStepList().add(approvedStep);
			}
			if(ta.getApprovalStrategyTip()!=0){//gercek tablo uzerinde ise
				W5ApprovalStep approvedStep = new W5ApprovalStep();
				approvedStep.setApprovalStepId(998);
				approvedStep.setDsc(ta.getApprovedMsg());//setDsc("onaylandi");
				ta.get_approvalStepList().add(approvedStep);
			}
			if(ta.getOnRejectTip()==1){//make status rejected
				W5ApprovalStep rejectedStep = new W5ApprovalStep();
				rejectedStep.setApprovalStepId(999);
				rejectedStep.setDsc(ta.getRejectedMsg());//setDsc("reddedildi");
				ta.get_approvalStepList().add(rejectedStep);
			}
			ta.set_approvalStepMap(new HashMap());
			for(W5ApprovalStep step:ta.get_approvalStepList()){
				if(step.getAccessViewTip()!=0){
					t.set_hasApprovalViewControlFlag((short)1);
				}
				ta.get_approvalStepMap().put(step.getApprovalStepId(), step);
			}
			getCurrentSession().flush();
		}		
	}

	
	@Override
	public void reloadUsersCache(int cid){	 //customizationID ??
		List<Object[]> l = (List<Object[]>)executeSQLQuery("select x.customization_id, x.user_id, x.user_name, x.dsc, 1 allow_multi_login_flag"+ (FrameworkSetting.profilePicture ? ", x.profile_picture_id" : "")+" from iwb.w5_user x "+(cid!=-1 ? (" where x.customization_id="+cid):""));
		if(l!=null)for(Object[] m : l){
			if(FrameworkSetting.profilePicture)
				UserUtil.addUserWithProfilePicutre(GenericUtil.uInt(m[0]),GenericUtil.uInt(m[1]),(String)m[2],(String)m[3],GenericUtil.uInt(m[4])!=0,GenericUtil.uInt(m[5])); 
			else
				UserUtil.addUser(GenericUtil.uInt(m[0]),GenericUtil.uInt(m[1]),(String)m[2],(String)m[3],GenericUtil.uInt(m[4])!=0);
		}
	}
	
	@Override
	public void reloadTableParamListChildListParentListCache(int cusId){	
		
		
		List<W5TableParam> tplAll = (List<W5TableParam>)find("from W5TableParam t where t.customizationId=? order by t.tableId, t.tabOrder", cusId);
		//Map<Integer, List<W5TableParam>> tplMap = new HashMap<Integer, List<W5TableParam>>();
		int lastTableId = -1;
		List<W5TableParam> x = null;
		for(W5TableParam tp:tplAll){
			if(lastTableId != tp.getTableId()) {
				if(x!=null)FrameworkCache.tableParamListMap.put(lastTableId, x);
				x=new ArrayList();
				lastTableId = tp.getTableId();
			}
			x.add(tp);
		}
		if(x!=null)FrameworkCache.tableParamListMap.put(lastTableId, x);
		List<W5TableChild> tcAll = (List<W5TableChild>)find("from W5TableChild t where t.customizationId=?  order by t.tableId", cusId);
		//Map<Integer, List<W5TableChild>> tcMap = new HashMap<Integer, List<W5TableChild>>();//copy
		//Map<Integer, List<W5TableChild>> tpMap = new HashMap<Integer, List<W5TableChild>>();//watch,feed
		lastTableId = -1;
		for(W5TableChild tp:tcAll){
			List<W5TableChild> tc = (lastTableId != tp.getTableId()) ? null : FrameworkCache.tableChildListMap.get(tp.getTableId());
			if(tc==null){
				tc = new ArrayList<W5TableChild>();
				FrameworkCache.tableChildListMap.put(tp.getTableId(), tc);
			}
			tc.add(tp);

			List<W5TableChild> tpx = (lastTableId != tp.getTableId()) ? null : FrameworkCache.tableParentListMap.get(tp.getRelatedTableId());
			if(tpx==null){
				tpx = new ArrayList<W5TableChild>();
				FrameworkCache.tableParentListMap.put(tp.getRelatedTableId(), tpx);
			}
			tpx.add(tp);
			lastTableId = tp.getTableId();
		}		
	}
	
	@Override
	public void reloadTableFieldListCache(int customizationId){
		List<W5TableField> tfl = (List<W5TableField>)find("from W5TableField t where t.customizationId=? order by t.tableId, t.tabOrder", customizationId);
		for(W5TableField tf:tfl){
			W5Table t = FrameworkCache.getTable(customizationId, tf.getTableId());//tableMap.get(tf.getTableId());
			if(t!=null){
				if(t.get_tableFieldList()==null){
					t.set_tableFieldList(new ArrayList<W5TableField>());
					t.set_tableFieldMap(new HashMap<Integer,W5TableField>());
					t.set_tableParamList(FrameworkCache.tableParamListMap.get(tf.getTableId()));
					t.set_tableChildList(FrameworkCache.tableChildListMap.get(tf.getTableId()));
					t.set_tableParentList(FrameworkCache.tableParentListMap.get(tf.getTableId()));
				}
				t.get_tableFieldList().add(tf);
				t.get_tableFieldMap().put(tf.getTableFieldId(), tf);
			}
		}
		if(customizationId==0){
			FrameworkCache.wTableFieldMap.clear();
			for(W5TableField tf:tfl)FrameworkCache.wTableFieldMap.put(tf.getTableFieldId(), tf.getTableId());
		}
	}
	

	
	@Override
	public void reloadTableFilterCache(int customizationId){ //customizationID ??
		for(W5TableFilter tf:(List<W5TableFilter>)find("from W5TableFilter t where t.customizationId=?", customizationId)){
			W5Table t = FrameworkCache.getTable(tf.getCustomizationId(), tf.getTableId());
			if(t!=null){
				if(t.get_tableFilterList()==null)t.set_tableFilterList(new ArrayList());
				t.get_tableFilterList().add(tf);
			}
		}
	}
	
	private void reloadDeveloperEntityKeys(){
		FrameworkCache.wDevEntityKeys.clear();
		Map<Integer, W5Table> tableMap = FrameworkCache.wTables.get(0);
		for(W5Table t:tableMap.values())if(t.getVcsFlag()!=0 && GenericUtil.hasPartInside("4,5,8,9,10,13,14,15,16,40,41,42,63,64,230,231,254,707,930,936,1345", ""+t.getTableId())){
			List<Object> lo = executeSQLQuery("select t."+t.get_tableFieldList().get(0).getDsc()+" from " + t.getDsc() + " t where t.customization_id=0 AND t.project_uuid='067e6162-3b6f-4ae2-a221-2470b63dff00'");
			if(lo!=null)for(Object o:lo){
				FrameworkCache.wDevEntityKeys.add(t.getTableId()+"."+o);		
			}
		}
	}
	
	@Override
	public void reloadPromisCaches(int cid) {
		logger.info("Caching Started (users, jobs, localeMsgs, errorMsgs, appSettings, lookUps, tableParams)");
		
		reloadUsersCache(cid);
		reloadMobileCache();
		
		
		//Job Schedule
		if(cid==-1 || cid==0)reloadJobsCache();		
				
		//Locale Msgs
		reloadLocaleMsgsCache2(cid);
		
		//Error Messages		
		if(cid==-1 || cid==0)reloadErrorMessagesCache();	

		//Application Settings
		reloadApplicationSettingsCache(cid);
				 		
		//Application Settings
		reloadProjectsCache(cid);
				 		
		//Publish LookUps
		if(cid==-1 || cid==0)reloadPublishLookUpsCache();

		//condition SQLs
		if(cid==-1 || cid==0)reloadTableAccessConditionSQLs();
		

//		int defaultCustomizationId = PromisCache.getAppSettingIntValue(0, "default_customization_id");

		//Conversions
		//reloadConversionsCache(cid);
		
		//Table Params
//		if(cid==-1 || cid==0)reloadTableParamListChildListParentListCache();
		List<W5Customization> customizationList;
		if(cid==-1){
			FrameworkCache.wLookUps.clear();
			FrameworkCache.wTables.clear();	
			FrameworkCache.wApprovals.clear();
			FrameworkCache.wRoles.clear();
			FrameworkCache.wCustomization.clear();
			FrameworkCache.wCustomizationMap.clear();
			
			customizationList=(List<W5Customization>)find("from W5Customization t order by t.customizationId");
			FrameworkCache.wCustomization.clear();FrameworkCache.wCustomization.addAll(customizationList);
			for(W5Customization c:customizationList)FrameworkCache.wCustomizationMap.put(c.getCustomizationId(), c);
			
			
		} else {
			customizationList=(List<W5Customization>)find("from W5Customization t where t.customizationId=?", cid);
			//PromisCache.wLookUps.get(cid).clear();
			//PromisCache.wRoleModules.get(cid).clear();
			//PromisCache.wTables.get(cid).clear();
			/*for(Integer approvalId:PromisCache.wApprovals.keySet()){
				W5Approval ap = PromisCache.wApprovals.get(approvalId);
				if(ap.getCustomizationId()==cid){
					PromisCache.wApprovals.remove(approvalId);
				}
			}
			*/
		}

		if(cid==-1 || cid==0)reloadApplicationSettingsValues();
		
		for(W5Customization d:customizationList)try{
			int customizationId = d.getCustomizationId();
//			if(cid!=-1 && customizationId!=cid)continue;
			FrameworkSetting.customizationSystemStatus.put(customizationId, 2); //suspended
			logger.info("Caching Customization = "+customizationId);
			
			reloadLookUpCache(customizationId);	
			reloadRolesCache(customizationId);
			reloadTableParamListChildListParentListCache(customizationId);
			reloadTablesCache(customizationId);
			reloadTableActionsCache(customizationId);
			reloadWsServersCache(customizationId);
			reloadWsClientsCache(customizationId);

			FrameworkSetting.customizationSystemStatus.put(customizationId, 0); //working
			
			if(FrameworkSetting.feedLoadAtStartupDepth>0 && cid==-1)reloadFeed(customizationId);

							
		} catch (Exception e){
			logger.error("Error for customizationId = " + d.getCustomizationId());
			e.printStackTrace();
		}
		
		if(FrameworkCache.cachedOnlineQueryFields==null)FrameworkCache.cachedOnlineQueryFields = (List<W5QueryField>)find("from W5QueryField f where f.queryId=142 order by f.tabOrder");
		if(cid==-1)reloadDeveloperEntityKeys();
		
		
		logger.info("Cache Loaded.");
	}

	private void reloadWsServersCache(int customizationId) {
		List<W5WsServer> lt = (List<W5WsServer>)find("from W5WsServer t where t.customizationId=?", customizationId);
		Map<String, W5WsServer> wssMap = new HashMap<String, W5WsServer>(lt.size()*14/10);
		FrameworkCache.wWsServers.put(customizationId, wssMap);
		for(W5WsServer o:lt){
			wssMap.put(o.getWsUrl(), o);
			o.set_methods((List<W5WsServerMethod>)find("from W5WsServerMethod t where t.customizationId=? AND t.wsServerId=? order by t.tabOrder", customizationId, o.getWsServerId()));
			o.get_methods().add(0,new W5WsServerMethod("login", (short)4, 3));
//			o.get_methods().add(1,new W5WsServerMethod("logout", (short)4, 5));
			for(W5WsServerMethod wsm:o.get_methods())if(wsm.getObjectTip()==19){//QueryResult
				wsm.set_params((List<W5WsServerMethodParam>)find("from W5WsServerMethodParam t where t.customizationId=? AND t.wsServerMethodId=? order by t.tabOrder", customizationId, wsm.getWsServerMethodId()));
				if(wsm.get_params().isEmpty())wsm.set_params(null);
				else {
					W5WsServerMethodParam tokenKey =new W5WsServerMethodParam(-998, "tokenKey", (short)1);tokenKey.setOutFlag((short)0);tokenKey.setNotNullFlag((short)1);
					wsm.get_params().add(0, tokenKey);
				}
			}
		}
	}
	
	@Override
	public void reloadWsClientsCache(int customizationId){
//		if(true)return;
		List<W5Ws> l = find("from W5Ws x where x.customizationId=?", customizationId);
		Map<Integer, W5Ws> mm = new HashMap();
		Map<String, W5Ws> mm2 = new HashMap();
		for(W5Ws w:l){
			mm.put(w.getWsId(), w);
			mm2.put(w.getDsc(), w);
		}
		FrameworkCache.wWsClients.put(customizationId, mm2);
		
		FrameworkCache.wWsMethods.clear();
		List<W5WsMethod> lm = find("from W5WsMethod x where x.customizationId=? order by x.wsId, x.wsMethodId", customizationId);
		for(W5WsMethod m:lm){
			W5Ws c = mm.get(m.getWsId());
			if(c!=null){
				m.set_ws(c);
				if(c.get_methods()==null)c.set_methods(new ArrayList());
				FrameworkCache.wWsMethods.put(m.getWsMethodId(), m);
				c.get_methods().add(m);
			}
		}
	}

	private void reloadFeed(int customizationId) {
		Query q = getCurrentSession().createQuery("from Log5Feed t where t.customizationId=? order by t.feedId desc");
		q.setInteger(0, customizationId);
		List<Log5Feed> l = q.setFirstResult(0).setMaxResults(FrameworkSetting.feedLoadAtStartupDepth).list(); 
		FrameworkCache.wFeeds.remove(customizationId);
		Map scd = new HashMap();scd.put("customizationId", customizationId);
		Map<String, List> preloaded = new HashMap();
		for(int i=l.size()-1;i>=0;i--){
			Log5Feed feed = l.get(i);
			if(feed.getTableId()!=0 && feed.getTablePk()!=0){//detail icinse
				String key = feed.getTableId()+"-"+feed.getTablePk();
				List<W5TableRecordHelper> l2 = preloaded.get(key);
				if(l2==null){
					l2 = findRecordParentRecords(scd,feed.getTableId(),feed.getTablePk(),0, true);
					preloaded.put(key, l2);
				}
				feed.set_tableRecordList(l2);
				if(feed.get_tableRecordList()!=null){
					if(feed.get_tableRecordList().size()>0){
						W5TableRecordHelper trh = feed.get_tableRecordList().get(0);
						W5Table t = FrameworkCache.getTable(customizationId, trh.getTableId());
						if(t!=null)feed.set_showFeedTip(t.getShowFeedTip());						
					}
					if(feed.get_tableRecordList().size()>1 && feed.get_showFeedTip()==1){
						W5TableRecordHelper trh = feed.get_tableRecordList().get(1);
						feed.setTableId(trh.getTableId());
						feed.setTablePk(trh.getTablePk());
						feed.set_commentCount(trh.getCommentCount());
					} else if(feed.get_tableRecordList().size()>0)
						feed.set_commentCount(feed.get_tableRecordList().get(0).getCommentCount());
				}
			}
			feed.set_insertTime(feed.getInsertDttm().getTime());
			FrameworkCache.addFeed(scd, feed, false);
		}
		
	}
	@Override
	public W5DbFuncResult getDbFuncResult(Map<String, Object> scd,
			int dbFuncId) {
		int	customizationId = scd==null || scd.get("customizationId")==null ? 0 :(Integer)scd.get("customizationId");
		if(dbFuncId<-1){
			dbFuncId = (Integer)find("select t.objectId from W5Form t where t.objectTip in (3,4) AND t.customizationId=? AND t.formId=?", customizationId, -dbFuncId).get(0);
		}

		W5DbFuncResult r = new W5DbFuncResult(dbFuncId);
		if(FrameworkSetting.preloadWEngine!=0 && FrameworkCache.wDbFuncs.get(dbFuncId)!=null){
			r.setDbFunc(FrameworkCache.wDbFuncs.get(dbFuncId));
		} else {
			r.setDbFunc((W5DbFunc)find("from W5DbFunc t where t.dbFuncId=?", dbFuncId).iterator().next());
			r.getDbFunc().set_dbFuncParamList(find("from W5DbFuncParam t where t.dbFuncId=? order by t.tabOrder", dbFuncId));
			
			
			if(FrameworkSetting.preloadWEngine!=0){
				FrameworkCache.wDbFuncs.put(dbFuncId, r.getDbFunc());
			}
		}

		
		r.setScd(scd);

    	return r;

	}
	
	@Override
	public void executeDbFunc(W5DbFuncResult r, String paramSuffix){
		Log5DbFuncAction action = new Log5DbFuncAction(r);
		String error = null;
		if(r.getDbFunc().getLkpCodeType()==1){
			Context cx = Context.enter();
			String script = null;
			try {
				cx.setOptimizationLevel(-1);
				// Initialize the standard objects (Object, Function, etc.)
				// This must be done before scripts can be executed. Returns
				// a scope object that we use in later calls.
				Scriptable scope = cx.initStandardObjects();

				script = r.getDbFunc().getRhinoScriptCode();
				if(script.charAt(0)=='!')script = script.substring(1);
				// Collect the arguments into a single string.
				if(script.contains("$iwb.")){
					ScriptEngine se = new ScriptEngine(r.getScd(), r.getRequestParams(), this, engine);
					Object wrappedOut = Context.javaToJS( se, scope);
					ScriptableObject.putProperty(scope, "$iwb", wrappedOut);
				}

				
				StringBuilder sc = new StringBuilder();
				boolean hasOutParam = false;
				if(r.getDbFunc().get_dbFuncParamList().size()>0){
					sc.append("var ");
		    		for(W5DbFuncParam p1 : r.getDbFunc().get_dbFuncParamList())if (p1.getOutFlag()==0){
		    			if(sc.length()>4)sc.append(", ");
		            	sc.append(p1.getDsc()).append("=");
		            	Object o = GenericUtil.prepareParam(p1, r.getScd(), r.getRequestParams(), (short)-1, null, (short)0, p1.getSourceTip()== 1 ? p1.getDsc()+paramSuffix : null, null, r.getErrorMap());
		            	if(o==null)sc.append("null");
		            	else if ((o instanceof Integer) || (o instanceof Double) || (o instanceof BigDecimal)|| (o instanceof Boolean))
			            	sc.append(o);
		            	else if ((o instanceof Date))
		            		sc.append("'").append(GenericUtil.uFormatDate((Date)o)).append("'");
		            	else
		            		sc.append("'").append(o).append("'");
		    		} else hasOutParam = true;
	    			if(sc.length()>4)sc.append(";\n");else sc.setLength(0);
		    	}
				sc.append("\nvar _scd=").append(GenericUtil.fromMapToJsonString2(r.getScd()))
					.append(";\nvar _request=").append(GenericUtil.fromMapToJsonString2(r.getRequestParams())).append(";\n").append(script);
				
				script = sc.toString();

				// Now evaluate the string we've colected.
				cx.evaluateString(scope, script, null, 1, null);
				/*
				if(scope.has("errorMsg", scope)){
					Object em = GenericUtil.rhinoValue(scope.get("errorMsg", scope));
					if(em!=null)throw new PromisException("rhino","DbFuncId", r.getDbFuncId(), script, LocaleMsgCache.get2(0,(String)r.getScd().get("locale"),em.toString()), null);					
				} */
				if(hasOutParam){
					//JSONObject jo=new JSONObject();
					Map<String,String> res=new HashMap<String, String>();
					for(W5DbFuncParam p1 : r.getDbFunc().get_dbFuncParamList())if (p1.getOutFlag()!=0 && scope.has(p1.getDsc(), scope)){
						Object em = GenericUtil.rhinoValue(scope.get(p1.getDsc(), scope));
						if(em!=null)res.put(p1.getDsc(), em.toString());
		    		}
			    	r.setResultMap(res);
				}
				if(scope.has("outMsgs", scope)){//TODO
					Object em = scope.get("outMsgs", scope);
				}
				r.setSuccess(true);
			} catch(Exception e){
				error = e.getMessage();
				throw new IWBException("rhino", "GlobalFunc", r.getDbFuncId(), script, "[20,"+r.getDbFuncId()+"] " + r.getDbFunc().getDsc(), e);
			} finally {
	             // Exit from the context.
 	             cx.exit();
 		    	logDbFuncAction(action, r, error);
	        }
			return;
		} 
    	final List<Object> sqlParams = new ArrayList<Object>();
    	final List<String> sqlNames=new ArrayList<String>();
    	StringBuilder sql= new StringBuilder();
   		sql.append("{call ");
   		
    	sql.append(r.getDbFunc().getDsc());
		boolean hasOutParam=false;
    	if(r.getDbFunc().get_dbFuncParamList().size()>0){
            boolean b = false;
    		sql.append("( ");
    		for(W5DbFuncParam p1 : r.getDbFunc().get_dbFuncParamList()){
            	if(b)
            		sql.append(",");
            	else
            		b=true;
            	sql.append(" ? ");
				String pvalue  = null;
				if (p1.getOutFlag()!=0){
					sqlNames.add(p1.getDsc());
					sqlParams.add(null);
					hasOutParam = true;
				}else{
					Object psonuc = GenericUtil.prepareParam(p1, r.getScd(), r.getRequestParams(), (short)-1, null, (short)0, p1.getSourceTip()== 1 ? p1.getDsc()+paramSuffix : null, null, r.getErrorMap());
					sqlParams.add(psonuc);
					sqlNames.add(null);
				}
    		}
    		sql.append(" )");
    	}
    	sql.append("}");

    	r.setExecutedSql(sql.toString());
		r.setSqlParams(sqlParams);	

    	if(!r.getErrorMap().isEmpty()){
    		r.setSuccess(false);
    		return;
    	}
    	final boolean hasOutParam2 = hasOutParam;
    	try {
    		getCurrentSession().doWork(new Work() {
				
				@Override
				public void execute(Connection conn) throws SQLException {

					CallableStatement s = conn.prepareCall(sql.toString());
		    		int i = 1;
					for(int ix=0;ix<sqlParams.size();ix++){
						Object o=sqlParams.get(ix);
						if (sqlNames.get(ix)!=null){
							W5DbFuncParam p1 = r.getDbFunc().get_dbFuncParamList().get(i-1);
							short t = p1.getParamTip();
							int t1=java.sql.Types.VARCHAR;
							if (t==1) t1=java.sql.Types.VARCHAR;
							else if (t==2) t1=java.sql.Types.DATE;
							else if (t==3) t1=java.sql.Types.DECIMAL;
							else if (t==4) t1=java.sql.Types.INTEGER;
							else if (t==5) t1=java.sql.Types.SMALLINT;
							
							if (p1.getOutFlag() == 1)
								s.registerOutParameter(i, t1);
						}else{
							if(o==null) s.setObject(i, null);
							else if(o instanceof Date)s.setTimestamp(i, new java.sql.Timestamp(((Date)o).getTime()));
							else s.setObject(i, o);
						}
						i++;
					}
					s.execute();

					if (hasOutParam2){
						//JSONObject jo=new JSONObject();
						Map<String,String> res=new HashMap<String, String>();
						for (int ixx=0;ixx<sqlNames.size();ixx++)
				    		if (sqlNames.get(ixx)!=null){
				    			Object o = s.getObject(ixx+1);
				    			if(o!=null){
				    				if(o instanceof java.sql.Date){
				    					o = GenericUtil.uFormatDate((java.sql.Date)o);
				    				}
					    			res.put(sqlNames.get(ixx), o.toString());
					    		}
				    		}
				    	r.setResultMap(res);
					}
					if(s!=null)s.close();
					r.setSuccess(true);
					
				/*	if(r.getDbFunc().getExecRestrictTip()==3){ //report
						
						PreparedStatement st = conn.prepareStatement("select row_id, column_id, deger, row_tip, cell_tip, colspan, tag from w_temp_report order by row_id, column_id");
		            	ResultSet rs = st.executeQuery();
		            	List<W5ReportCellHelper> sonuc = new ArrayList();
		        		while(rs.next()){
		        			sonuc.add(new W5ReportCellHelper(rs.getInt(1),rs.getInt(2),rs.getString(3),
		        					rs.getShort(4),rs.getShort(5),rs.getShort(6),rs.getString(7)));
		        		}
		        		r.setReportList(sonuc);        		
		        		
		    			if(rs!=null)rs.close();
		    			if(st!=null)st.close();
					}*/

					if(FrameworkSetting.hibernateCloseAfterWork)if(conn!=null)conn.close();
					switch(r.getDbFunc().getDbFuncId()){
					case	925://reload locale msg cache
						LocaleMsgCache.set2((Integer)r.getScd().get("customizationId"),r.getRequestParams().get("plocale"+paramSuffix), r.getRequestParams().get("plocale_msg_key"+paramSuffix), r.getRequestParams().get("pdsc"+paramSuffix));
						break;
					}
				}
			});
		} catch(Exception e){
			String exceptionText=GenericUtil.replaceSql(r.getExecutedSql(), sqlParams);
			error = e.getLocalizedMessage();
//			logException(exceptionText+" "+e.getMessage(),  PromisUtil.uInt(r.getScd().get("customizationId")), PromisUtil.uInt(r.getScd().get("userRoleId")));
			throw new IWBException("sql","DbProc Execute1",r.getDbFuncId(),exceptionText, e.getMessage(), e.getCause());
		} finally {
	    	logDbFuncAction(action, r, error);
		}
		/*if(PromisCache.getAppSettingIntValue(r.getScd(), "bpm_flag")!=0){
			int nextBpmActionId = bpmControl(r.getScd(), r.getRequestParams(), r.getDbFunc().get_listBpmStartAction(), r.getDbFunc().get_listBpmEndAction(), 11, 0, 0);
			if (nextBpmActionId>-1)r.setNextBpmActions(find("select x from BpmAction x,BpmProcessStep s where x.customizationId=s.customizationId and x.customizationId=? and x.activeFlag=1 AND x.prerequisitActionId=? AND x.wizardStepFlag!=0 AND s.actionId=x.actionId", r.getScd().get("customizationId"),nextBpmActionId));
		}	*/
	}
	
	@Override
	public void bookmarkForm(String dsc, int formId, int userId, int customizationId, W5FormResult formResult) {
		W5FormValue formValue = new W5FormValue();
		formValue.setFormId(formId);
		formValue.setDsc(dsc);
		formValue.setInsertUserId(userId);
		formValue.setCustomizationId(customizationId);
		saveObject(formValue);
		
		for(W5FormCell c:formResult.getForm().get_formCells())if(c.getSourceTip()==1){
			String val = formResult.getRequestParams().get(c.getDsc());
			if(val!=null && val.length()>0 && (formId>0 || val.length()<=2048)){
				W5FormValueCell fvc = new W5FormValueCell();
				if(val.length()>2048)val = val.substring(0,2048);
				fvc.setVal(val);
				fvc.setFormCellId(c.getFormCellId());
				fvc.setFormValueId(formValue.getFormValueId());
				fvc.setCustomizationId((Integer)formResult.getScd().get("customizationId"));
				saveObject(fvc);
			}
		}
		if(formResult.getPkFields()==null)formResult.setPkFields(new HashMap());
		formResult.getPkFields().put("id", formValue.getFormValueId());
	}
	@Override
	public W5JasperResult getJasperResult(Map<String, Object> scd,
			W5JasperReport jasperreport, Map<String, String> parameterMap) {
		W5JasperResult jasperResult = new W5JasperResult(jasperreport.getJasperId());
		jasperResult.setScd(scd);
		/*
		if(PromisSetting.preloadWEngine!=0 && PromisCache.wTemplates.get(templateId)!=null){
//			jasperResult.setJasper(PromisCache.wTemplates.get(templateId));
		} else {
			loadJasper(jasperResult);
			if(PromisSetting.preloadWEngine!=0)PromisCache.wTemplates.put(templateId, templateResult.getTemplate());
		}
		*/
		
		W5Jasper jasper = (W5Jasper)find("from W5Jasper t where t.jasperId=?", jasperreport.getJasperId()).get(0); // ozel bir client icin varsa
		jasperResult.setJasper(jasper);
		jasper.set_jasperObjects(find("from W5JasperObject t where t.jasperId=? order by t.tabOrder",jasperreport.getJasperId()));
		jasper.set_jasperReport(jasperreport);
		jasperResult.setResultMap(new HashMap());
		return jasperResult;
	}

	@Override
	public W5JasperReport getJasperReport(Map<String, Object> scd,int jasperReportId){
		W5JasperReport jasperreport=(W5JasperReport) find("from W5JasperReport t where t.customizationId=? and  t.jasperReportId=?",scd.get("customizationId"),jasperReportId).get(0);
		return jasperreport;	
	}

	@Override
	public void copyTableRecord(int tableId, int tablePk, String srcSchema, String dstSchema) {
		W5Table t = FrameworkCache.getTable(0, tableId);
		W5TableParam tp = (W5TableParam)find("from W5TableParam t where t.tableId=?", tableId).get(0);
		StringBuilder b = new StringBuilder();
		b.append("insert into ").append(dstSchema).append(".").append(t.getDsc()).append(" select * from ").append(srcSchema).append(".").append(t.getDsc()).append(" where ").append(tp.getExpressionDsc()).append("=?");
		
		Session session = getCurrentSession();
		
		try {
			SQLQuery query = session.createSQLQuery(b.toString());
			query.setInteger(0, tablePk);
			query.executeUpdate();
		} catch(Exception e){
			if(FrameworkSetting.debug)e.printStackTrace();
			if(dstSchema!=null && !dstSchema.equals(FrameworkCache.getAppSettingStringValue(0, "default_schema"))){
				b.setLength(0);
				b.append("create table ").append(dstSchema).append(".").append(t.getDsc()).append(" as select * from ").append(srcSchema).append(".").append(t.getDsc()).append(" where ").append(tp.getExpressionDsc()).append("=?");
				try {
					SQLQuery query = session.createSQLQuery(b.toString());
					query.setInteger(0, tablePk);
					query.executeUpdate();
					session.createSQLQuery("alter table "+dstSchema+"."+t.getDsc()+" add constraint PK_T"+t.getTableId()+" primary key ("+tp.getExpressionDsc()+")").executeUpdate();
				} catch(Exception e2){
					throw new IWBException("sql","Copy(Insert) Table Record",tableId,b.toString() + " --> " +tablePk, e.getMessage(), e.getCause());
				}
			} else
				throw new IWBException("sql","Copy Table Record",tableId,b.toString() + " --> " +tablePk, e.getMessage(), e.getCause());
		} finally {
//			session.close();
		}
	}
	

	public String getTableFields4VCS(W5Table t, String prefix){
		StringBuilder s=new  StringBuilder();
		if(t==null || GenericUtil.isEmpty(t.get_tableFieldList()))
			return s.toString();
		for(W5TableField f:t.get_tableFieldList()){
			if(f.getTabOrder()<0 || f.getDsc().equals(t.get_tableFieldList().get(0).getDsc()))continue;
			if(f.getDsc().equals("version_no") || f.getDsc().equals("insert_user_id") || f.getDsc().equals("insert_dttm") 
					|| f.getDsc().equals("version_user_id") || f.getDsc().equals("version_dttm") || f.getDsc().equals("customization_id") || f.getDsc().equals("project_uuid"))
					continue;
			
			if(s.length()>0)s.append(" || '-iwb-' || ");
			switch(f.getParamTip()){
			case	1:s.append("coalesce(").append(prefix).append(".").append(f.getDsc()).append(",'')");break;
			case	4://integer
			case	5://boolean
			case	3://double
				if(f.getNotNullFlag()!=0)
					s.append(prefix).append(".").append(f.getDsc()).append("::text");
				else
					s.append("case when ").append(prefix).append(".").append(f.getDsc()).append(" is null then '' else ").append(prefix).append(".").append(f.getDsc()).append("::text end");
				break;
			case	2:
				s.append("case when ").append(prefix).append(".").append(f.getDsc()).append(" is null then '' else to_char(").append(prefix).append(".").append(f.getDsc()).append(",'ddmmyyy-hh24miss') end");break;
			default:s.append("coalesce(").append(prefix).append(".").append(f.getDsc()).append(",'')");break;
			}
		}
		
		return s.toString();
	}
	@Override
	public boolean copyFormTableDetail(W5FormResult masterFormResult, W5TableChild tc, String newMasterTablePk,
			String schema, String prefix) {
		Map<String, Object> scd = masterFormResult.getScd();
		W5Table t = FrameworkCache.getTable(scd, tc.getRelatedTableId());
		if(t==null)return false;
		Map<String,String> requestParams = new HashMap();requestParams.putAll(masterFormResult.getRequestParams());
		W5Table mt = FrameworkCache.getTable(scd, masterFormResult.getForm().getObjectId());//.get_sourceTable();
		W5FormResult	formResult = getFormResult(scd, t.getDefaultUpdateFormId(), 2, requestParams);
		if(t.getAccessViewTip()==0 && !FrameworkCache.roleAccessControl(scd,  0)){
			//throw new PromisException("security","Module", 0, null, "Modul Kontrol: Erişim kontrolünüz yok", null);
			return false;
		}
		if(!GenericUtil.accessControl(scd, t.getAccessViewTip(), t.getAccessViewRoles(), t.getAccessViewUsers())){
			//throw new PromisException("security","Form", t.getDefaultUpdateFormId(), null, "Tablo Kontrol: Veri görünteleyemezsiniz", null);
			return false;
		}
		/*
		if(!PromisUtil.accessControl(scd, t.getAccessInsertTip(), t.getAccessInsertRoles(), t.getAccessInsertUsers())){
			//throw new PromisException("security","Form", t.getDefaultUpdateFormId(), null, "Tablo Kontrol: Yeni kayit yapamazsiniz", null);
			return false;
		}*/
		
		List<Object> sqlParams = new ArrayList();
		String masterFieldDsc = mt.get_tableFieldMap().get(tc.getTableFieldId()).getDsc();
		String detailFieldDsc = t.get_tableFieldMap().get(tc.getRelatedTableFieldId()).getDsc();
		String detailStaticFieldDsc = tc.getRelatedStaticTableFieldId()>0 ? t.get_tableFieldMap().get(tc.getRelatedStaticTableFieldId()).getDsc() : null;
		
		String detailSql = "select x."+ t.get_tableParamList().get(0).getExpressionDsc();
		boolean multiKey = false;
		boolean cusFlag = false;
		if(t.get_tableParamList().size()>1){
			if(!t.get_tableParamList().get(1).getExpressionDsc().equals("customization_id")){
				for(W5TableParam tp:t.get_tableParamList())if(!t.get_tableParamList().get(1).getExpressionDsc().equals("customization_id")){
					if(!multiKey){
						multiKey = true;
						continue;
					} 
					detailSql+=",x."+tp.getExpressionDsc();
				} else cusFlag = true;
			} else cusFlag = true;
		}
		detailSql+=" from " + t.getDsc() 
		+ " x where ";
		if(cusFlag){
			detailSql+="x.customization_id=? AND ";
			sqlParams.add(scd.get("customizationId"));
		}
		detailSql+="exists(select 1 from " + mt.getDsc() 
		+ " q where q." + masterFieldDsc +" = x." + detailFieldDsc //detail ile iliski
		+" AND q." + mt.get_tableParamList().get(0).getExpressionDsc() + " = ?"; //master kayit ile iliski
		sqlParams.add(GenericUtil.uInt(masterFormResult.getPkFields().get("t"+masterFieldDsc)));
		//sqlParams.add(PromisUtil.uInt(masterPk));
		
		if(mt.get_tableParamList().size()>1 && mt.get_tableParamList().get(1).getExpressionDsc().equals("customization_id")){
			detailSql+=" AND q.customization_id=?";
			sqlParams.add(scd.get("customizationId"));
		}

		if(detailStaticFieldDsc!=null)
			detailSql+=" AND x."+ detailStaticFieldDsc+"="+tc.getRelatedStaticTableFieldVal();
		detailSql+=")";
		boolean bt = false;
		for(W5TableField tf:t.get_tableFieldList())if(tf.getDsc().equals("tab_order")){
			detailSql+=" order by x.tab_order";
			bt = true;
			break;			
		}
		if(!bt){
			detailSql+=" order by x."+t.get_tableFieldList().get(0).getDsc();
		}
		
		List<Object> rl = executeSQLQuery(detailSql, sqlParams.toArray());
		requestParams.put(detailFieldDsc, GenericUtil.isEmpty(newMasterTablePk) ? requestParams.get(mt.get_tableParamList().get(0).getDsc()): newMasterTablePk);
		if(rl!=null)for(Object o:rl){
			if(multiKey){
				int qi=0;
				for(W5TableParam tp:t.get_tableParamList())if(!t.get_tableParamList().get(1).getExpressionDsc().equals("customization_id")){
					requestParams.put(tp.getDsc(), ((Object[])o)[qi++].toString());
				}
			} else
				requestParams.put(t.get_tableParamList().get(0).getDsc(), o.toString());
			copyFormTable(formResult,schema, prefix, true);
			if(!GenericUtil.isEmpty(t.get_tableChildList())){
				if(!GenericUtil.isEmpty(formResult.getOutputFields()))for(String k:formResult.getOutputFields().keySet()){
					if(formResult.getOutputFields().get(k)!=null)requestParams.put(k, formResult.getOutputFields().get(k).toString());
				}
//				String pcopyTablePk = requestParams.get(t.get_tableParamList().get(0).getDsc());
				for(W5TableChild tc2:t.get_tableChildList())if(tc2.getCopyStrategyTip()==1){
					copyFormTableDetail(formResult, tc2, requestParams.get(t.get_tableFieldList().get(0).getDsc()), schema, prefix);
				}
			}
		}
		return true;
//		throw new PromisException("security","Form", t.getDefaultUpdateFormId(), null, "Tablo Kontrol: Veri görünteleyemezsiniz", null);
	}
	
	
	@Override
	public List<Map> getMainTableData(Map<String, Object> scd,int tableId, String  tablePk){ //TODO: yetki eksik

		int count=0;
		String customizationId=scd.get("customizationId").toString();
		W5Table table=FrameworkCache.getTable(customizationId, tableId);

	
		StringBuilder	sql = new StringBuilder();
		List lp = new ArrayList();
		lp.add(tablePk);
		sql.append("select t.* ,");
		
		sql.replace(sql.length()-1, sql.length(), " from "+table.getDsc()+"  t where");			
		for(W5TableParam x: table.get_tableParamList()){
    		sql.append(" t."+x.getExpressionDsc()+"= ? and");
    		if(x.getExpressionDsc().equals("customization_id")) lp.add(customizationId);
    	}			
		sql.replace(sql.length()-3,sql.length(),"");	    
		
		return  executeSQLQuery2Map(sql.toString(), lp);
	}
	@Override
	public List<W5TableRecordHelper> findRecordParentRecords(Map<String, Object> scd, int tableId,
			int tablePk, int maxLevel, boolean includeSummarySqlFlag) {//TODO:includeSummarySqlFlag ise yaramiyor
		List<W5TableRecordHelper> l = new ArrayList<W5TableRecordHelper>();
		//if(true)return l; bunu kim neden yapti ??
		W5Table t = FrameworkCache.getTable(scd, tableId);
		W5TableChild tc = null;
		long currentMillis = System.currentTimeMillis();
		Map<String, String> requestParams= new HashMap();
		int ptCount = 0;//parentCount
		int level = 0;
		if (maxLevel==0)maxLevel=10;
		while(t!=null && tablePk!=0 && level<maxLevel){
			level++;
			W5TableRecordHelper trh = FrameworkCache.getTableCacheValue(t.getCustomizationId(), tableId,tablePk);
			if(trh!=null && (currentMillis-trh.getLastAccessTime())<FrameworkSetting.cacheTimeoutRecord){//caching den aliyorum
				
				trh.setLastAccessTime(currentMillis);
				l.add(trh);
				t=FrameworkCache.getTable(scd,trh.getParentTableId());
				tablePk = trh.getParentTablePk();
			} else if(!GenericUtil.isEmpty(t.get_tableParamList())){
				requestParams.put(t.get_tableParamList().get(0).getDsc(), ""+tablePk);
				StringBuilder sql = new StringBuilder(512);
				boolean accessControlFlag=false;
				sql.append("select ");
				if(t.getSummaryRecordSql()==null)//bir tane degeriini koyacagiz
					sql.append("'WARNING!!! Not defined SummarySql' dsc");
				else { 
					sql.append(t.getSummaryRecordSql());
					if(!t.getSummaryRecordSql().contains(" dsc,") && !t.getSummaryRecordSql().endsWith(" dsc"))sql.append(" dsc");
				}
				if(t.get_tableParentList()!=null && t.get_tableParentList().size()>0){
					sql.append(",x.");
					tc = t.get_tableParentList().get(0);
					sql.append(t.get_tableFieldMap().get(tc.getRelatedTableFieldId()).getDsc()).append(" ptable_pk");
					if(tc.getRelatedStaticTableFieldId()!=0){
						sql.append(", x.").append(t.get_tableFieldMap().get(tc.getRelatedStaticTableFieldId()).getDsc()).append(" pobject_tip");
						ptCount=2;//multi:parent
					} else ptCount=1;//single:parent
					
				} else ptCount=0;
				if(t.getMakeCommentFlag()!=0)sql.append(", (select count(1) from iwb.w5_comment cx where cx.table_id=").append(t.getTableId()).append(" AND cx.customization_id=${scd.customizationId} AND cast(cx.table_pk as int8)=x.").append(t.get_tableParamList().get(0).getExpressionDsc()).append(") pcomment_count ");
				if(t.getAccessTips()!=null && GenericUtil.hasPartInside2(t.getAccessTips(), "0"))accessControlFlag=true;
				if(accessControlFlag)sql.append(", ac.access_roles, ac.access_users");
				sql.append(" from ").append(t.getDsc()).append(" x");
				if(accessControlFlag)sql.append(" left outer join iwb.w5_access_control ac on ac.ACCESS_TIP=0 AND ac.table_id=").append(t.getTableId()).append(" AND ac.customization_id=${scd.customizationId} AND cast(ac.table_pk as int8)=x.").append(t.get_tableParamList().get(0).getExpressionDsc());
				sql.append(" where x.").append(t.get_tableParamList().get(0).getExpressionDsc()).append("=${req.").append(t.get_tableParamList().get(0).getDsc()).append("}");
				if(t.get_tableParamList().size()==2){
					if(t.get_tableParamList().get(1).getDsc().equals("customizationId"))sql.append(" AND x.customization_id=${scd.customizationId}");
					else return l;
				}
				Object[] oz = DBUtil.filterExt4SQL(sql.toString(), scd, requestParams, null);
				Map<String, Object> m = runSQLQuery2Map(oz[0].toString(),(List)oz[1],null);
				trh = new W5TableRecordHelper();
				trh.setTableId(tableId);
				trh.setTablePk(tablePk);
				l.add(trh);

				if(m!=null){
					if(accessControlFlag)trh.setViewAccessControl(new W5AccessControlHelper((String)m.get("access_roles"),(String)m.get("access_users")));
					if(m.size()==1){
						Object o = m.values().iterator().next();
						if(o!=null)trh.setRecordDsc(o.toString());
					} else trh.setRecordDsc((String)m.get("dsc"));
					if(t.getMakeCommentFlag()!=0)trh.setCommentCount(GenericUtil.uInt(m.get("pcomment_count")));
					trh.setCachedTime(currentMillis);
					trh.setLastAccessTime(trh.getCachedTime());
					FrameworkCache.putTableCacheValue(t.getCustomizationId(), tableId, tablePk, trh);//cache le objeyi
					switch(ptCount){
					case	0:return l;
					case	1://single parent
						t=FrameworkCache.getTable(scd,tc.getTableId());
						trh.setParentTableId(tableId = t.getTableId());
						trh.setParentTablePk(tablePk = GenericUtil.uInt(m.get("ptable_pk")));
						break;
					default://multi parent
						tableId=-1;
						trh.setParentTablePk(tablePk = GenericUtil.uInt(m.get("ptable_pk")));
						int pobjectTip = GenericUtil.uInt(m.get("pobject_tip"));
						for(W5TableChild tc2:t.get_tableParentList())if(tc2.getRelatedStaticTableFieldVal()==pobjectTip){
							trh.setParentTableId(tableId = tc2.getTableId());
							t = FrameworkCache.getTable(scd, tableId);
							break;
						}
						if(tableId==-1){//sorun, parent bulamamis
							W5TableRecordHelper trhError = new W5TableRecordHelper();
							trhError.setRecordDsc("ERROR: parent not found");
							l.add(trhError);
							return l;
						}
					}
		        	
				} else t=null;
			}
		}
		return l;
		
	}
	
	@Override
	public Object interprateTemplateExpression(Map<String, Object> scd, Map<String, String> requestParams, int tableId, int tablePk, StringBuilder tmp){
		Map<String,String> res = new HashMap<String,String>();
		String result = "";
//		if(tmp.indexOf("${")<0)return res;
		Map<String,W5TableField> resField = new HashMap<String,W5TableField>();
		Set<String> invalidKeys = new HashSet<String>();
		StringBuilder tmp1 = new StringBuilder(); tmp1.append(tmp);
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList();
		sql.append("select ");
		List<W5TableFieldCalculated> ltfc = find("from W5TableFieldCalculated t where t.customizationId=? AND t.tableId=? order by t.tabOrder", scd.get("customizationId"), tableId);
		W5Table t = FrameworkCache.getTable(scd, tableId);
		for(int bas = tmp1.indexOf("${"); bas>=0; bas=tmp1.indexOf("${",bas+2)){
			int bit = tmp1.indexOf("}", bas+2);
			String subStr = tmp1.substring(bas+2, bit);
//			if(res.containsKey(subStr))continue; // daha once hesaplandiysa bir daha gerek yok TODO
			if(subStr.startsWith("scd.")){ //session
				Object o = scd.get(subStr.substring(4));
				tmp1.replace(bas, bit+1, "?");//.put(subStr, o.toString());
				params.add(o);
			} else if(subStr.startsWith("app.")){//application settingsden
				String appStr = FrameworkCache.getAppSettingStringValue(scd, subStr.substring(4));
				tmp1.replace(bas, bit+1, "?");//.put(subStr, o.toString());
				params.add(appStr);
			} else if(subStr.startsWith("req.")){//application settingsden
				String reqStr = requestParams.get(subStr.substring(4));
				tmp1.replace(bas, bit+1, "?");//.put(subStr, o.toString());
				params.add(reqStr);
			} else if(subStr.startsWith("tbl.") || (subStr.startsWith("lnk.") && subStr.substring(4).replace(".", "&").split("&").length==1)){//direk tablodan veya link'in ilk kaydi ise
				String newSubStr = subStr.substring(4);
				boolean fieldFound = false;
				for(W5TableField tf :t.get_tableFieldList())if(tf.getDsc().equals(newSubStr)){
					tmp1.replace(bas, bit+1, "x."+newSubStr);//.put(subStr, o.toString());
					fieldFound = true;
					break;
				}
				
				if(!fieldFound)invalidKeys.add(subStr);;
			} else if(subStr.startsWith("clc.")){
				boolean fieldFound = false;
				String newSubStr = subStr.substring(4);
				for(W5TableFieldCalculated tfc :ltfc)if(tfc.getDsc().equals(newSubStr)){
					String sqlCode = tfc.getSqlCode();
					W5TableField ntf = new W5TableField(tfc.getTableFieldCalculatedId());ntf.setDefaultControlTip((short)-1);
					if(sqlCode.contains("${")){
						Object[] oz = DBUtil.filterExt4SQL(sqlCode, scd, requestParams, null);
						sqlCode = (String)oz[0];
						if(oz.length>1)params.addAll((List)oz[1]);
					}
					tmp1.replace(bas, bit+1, "("+sqlCode+")");//.put(subStr, o.toString());
					fieldFound = true;
					break;
				}
				if(!fieldFound)invalidKeys.add(subStr);;			
			} else if(subStr.startsWith("lnk.") && subStr.substring(4).replace(".", "&").split("&").length>1){// burda bu field ile olan baglantiyi cozmek lazim TODO
				String newSubStr = subStr.substring(4);
				
				String[] sss = newSubStr.replace(".", "&").split("&");
				if(sss.length>1){ //TODO kaldirilmasi lazim
					W5Table newT = t;
					StringBuilder newSub = new StringBuilder();
					StringBuilder newSub2 = new StringBuilder();
					boolean foundSt = false;
					for(int isss=0;isss<sss.length-1;isss++){
						if(isss>0){
							newSub2.setLength(0);
							newSub2.append(newSub);
							newSub.setLength(0);
						}
						for(W5TableField tf :newT.get_tableFieldList())if(tf.getDsc().equals(sss[isss])){
							foundSt = false;
							if(tf.getDefaultControlTip()==7 || tf.getDefaultControlTip()==9 || tf.getDefaultControlTip()==10/* || tf.getDefaultControlTip()==15*/){//sub table
								W5Table st = FrameworkCache.getTable(scd, tf.getDefaultLookupTableId());
								if(st==null)break;//HATA: gerekli bir alt kademe tabloya ulasilamadi
								int dltId = 0;
								for(W5TableField stf :st.get_tableFieldList())if(stf.getDsc().equals(sss[isss+1])){
									dltId = stf.getDefaultLookupTableId();
									foundSt = true;
									break;
								}
								if(!foundSt)break;//HATA: bir sonraki field bulunamadi
								newSub.append("(select ");
								newSub.append("y").append(isss).append(".").append(sss[isss+1]);
								newSub.append(" from ").append(st.getDsc()).append(" y").append(isss)
								.append(" where y").append(isss).append(".").append(st.get_tableFieldList().get(0).getDsc())
								.append("=").append(isss==0 ? ("x."+sss[isss]):newSub2);
								if(st.get_tableFieldList().size()>1)for(W5TableField wtf :st.get_tableFieldList())if(wtf.getDsc().equals("customization_id")){
									newSub.append(" AND y").append(isss).append(".customization_id=?");
									params.add(scd.get("customizationId"));
									break;
								}
								
								newSub.append(")");
								newT = st;
							} 
							break;			
						}
						if(!foundSt){//bulamamis uygun sey
							break;
						}
					}
					if(foundSt && newSub.length()>0){
						tmp1.replace(bas, bit+1, "("+newSub.toString()+")");//.put(subStr, o.toString());
					}
				}

			}
		}
		sql.append(tmp1).append(" xxx from ").append(t.getDsc()).append(" x where x.").append(t.get_tableParamList().get(0).getExpressionDsc()).append("=?");
		params.add(tablePk);
		if(t.get_tableParamList().size()==2 && t.get_tableParamList().get(1).getExpressionDsc().equals("customization_id")){
			sql.append(" AND x.customization_id=?");
			params.add(scd.get("customizationId"));
		}
		Map<String,Object> qres = runSQLQuery2Map(sql.toString(), params, null);
		return GenericUtil.isEmpty(qres) ? "" : qres.get("xxx");
	}
	
	@Override
	public Map<String,String> interprateTemplate(Map<String, Object> scd, Map<String, String> requestParams, int tableId, int tablePk, StringBuilder tmp, boolean replace, int smsMailReplaceTip, int conversionTip){
		Map<String,String> res = new HashMap<String,String>();
		if(conversionTip==4){
			Object o = interprateTemplateExpression(scd, requestParams, tableId, tablePk, tmp);
			res.put("result", o.toString());
			if(replace){
				tmp.setLength(0);tmp.append(o);
			} 
			return res;
		}
		if(tmp.indexOf("${")<0)return res;
		Map<String,W5TableField> resField = new HashMap<String,W5TableField>();
		Set<String> invalidKeys = new HashSet<String>();
		//${req.param} -> request'ten ne geldiyse
		//${scd.param} -> session'dan ne geldiyse
		//${app.param} -> app_setting'ten ne geldiyse
		//${lcl.locale_msg_key} -> locale_msg tablosundan bakilacak
		//${tbl.field_name} -> direk field ismi
		//${lnk.field_name1.field_name2} -> link oldugu tablodaki field ismi
		//${lnk.field_name1.field_name2.field_name3} -> link oldugu tablodaki field in linkindeki diger vs.vs.
		//smsMailReplaceTip : 0->yok, 1:sms, 2:mail
		//conversionTip : 0->Serbest, 1: lookup-mapping, 2: sql, 3: javascript, 4: expression, 5: serbest&''
		Set<Integer> smsMailTableIds = null;
		if(smsMailReplaceTip!=0){
			smsMailTableIds = new HashSet<Integer>();
			String qs= FrameworkCache.getAppSettingStringValue(scd, "sms_mail_table_ids");
			if(qs!=null){
				String[] oqs=qs.split(",");
				if(oqs!=null)for(String toqs:oqs)smsMailTableIds.add(GenericUtil.uInteger(toqs));
			}
		}
		String fieldPrefix = "fx_q1_";
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList();
		sql.append("select ");
		int field_cnt=1;
		List<W5TableFieldCalculated> ltfc = find("from W5TableFieldCalculated t where t.customizationId=? AND t.tableId=? order by t.tabOrder", scd.get("customizationId"), tableId);
		W5Table t = FrameworkCache.getTable(scd, tableId);
		for(int bas = tmp.indexOf("${"); bas>=0; bas=tmp.indexOf("${",bas+2)){
			if(bas>0 && tmp.charAt(bas-1)=='$')continue;
			int bit = tmp.indexOf("}", bas+2);
			String subStr = tmp.substring(bas+2, bit);
			if(res.containsKey(subStr))continue; // daha once hesaplandiysa bir daha gerek yok
			if(subStr.startsWith("scd.")){ //session
				Object o = scd.get(subStr.substring(4));
				if(o!=null)res.put(subStr, o.toString());
			} else if(subStr.startsWith("app.")){//application settingsden
				String appStr = FrameworkCache.getAppSettingStringValue(scd, subStr.substring(4));
				if(appStr!=null)res.put(subStr, appStr);
			} else if(subStr.startsWith("req.")){//requestten
				String reqStr = requestParams.get(subStr.substring(4));
				if(reqStr!=null)res.put(subStr, reqStr);
			} else if(subStr.startsWith("tbl.") || (smsMailReplaceTip==0 && subStr.startsWith("lnk.") && conversionTip!=0 && subStr.substring(4).replace(".", "&").split("&").length==1)){//direk tablodan veya link'in ilk kaydi ise
				String newSubStr = subStr.substring(4);
				for(W5TableField tf :t.get_tableFieldList())if(tf.getDsc().equals(newSubStr)){
					if(tf.getDefaultControlTip()==15){
						W5Table st = FrameworkCache.getTable(scd, tf.getDefaultLookupTableId());
						if(st!=null && smsMailReplaceTip!=0 && smsMailTableIds.contains(st.getTableId())){// eger sms/mail isi ve bu bir link ise
							res.put(subStr, fieldPrefix+field_cnt);
							resField.put(subStr, tf);
							sql.append("iwb.fnc_sms_mail_adress_multi(?,").append(st.getTableId()).append(",").append("x.").append(newSubStr).append(",").append(smsMailReplaceTip).append(") ").append(fieldPrefix).append(field_cnt).append(",");;
							params.add(scd.get("userRoleId"));
							field_cnt++;
						}
					} else {
						res.put(subStr, fieldPrefix+field_cnt);
						resField.put(subStr, tf);
						sql.append("x.").append(newSubStr).append(" ").append(fieldPrefix).append(field_cnt).append(",");
						field_cnt++;
					}
					break;
				}
				if(!res.containsKey(subStr))invalidKeys.add(subStr);;
			} else if(subStr.startsWith("lcl.")){ //locale msg key
				continue;
			} else if(subStr.startsWith("clc.")){ // calculated field
				String newSubStr = subStr.substring(4);
				for(W5TableFieldCalculated tfc :ltfc)if(tfc.getDsc().equals(newSubStr)){
					String sqlCode = tfc.getSqlCode();
					res.put(subStr, fieldPrefix+field_cnt);
					W5TableField ntf = new W5TableField(tfc.getTableFieldCalculatedId());ntf.setDefaultControlTip((short)-1);
					resField.put(subStr, ntf);
					if(sqlCode.contains("${")){
						Object[] oz = DBUtil.filterExt4SQL(sqlCode, scd, requestParams, null);
						sqlCode = oz[0].toString();
						if(oz.length>1)params.addAll((List)oz[1]);
					}
					sql.append("(").append(sqlCode).append(") ").append(fieldPrefix).append(field_cnt).append(",");
					field_cnt++;
					break;
				}
				if(!res.containsKey(subStr))invalidKeys.add(subStr);
			} else if(subStr.equals("company_logo")){ //firma logosu
				String logoFileUrl = "";
				W5Customization c = FrameworkCache.wCustomizationMap.get(scd.get("customizationId"));
				String url = FrameworkCache.getAppSettingStringValue(scd, "url_remote");
				if (GenericUtil.isEmpty(url))requestParams.get("_ServerURL_");
				logoFileUrl = "http://"+ url +"/bmp/app/sf/iworkbetter.png?_fai=-1000";	
				res.put(subStr, logoFileUrl);
			} else if(subStr.startsWith("lnk.")){// burda bu field ile olan baglantiyi cozmek lazim
				String newSubStr = subStr.substring(4);
				
				String[] sss = newSubStr.replace(".", "&").split("&");
				if(sss.length>1){
					W5Table newT = t;
					StringBuilder newSub = new StringBuilder();
					StringBuilder newSub2 = new StringBuilder();
					for(int isss=0;isss<sss.length-1;isss++){
						if(isss>0){
							newSub2.setLength(0);
							newSub2.append(newSub);
							newSub.setLength(0);
						}
						for(W5TableField tf :newT.get_tableFieldList())if(tf.getDsc().equals(sss[isss])){
							if(tf.getDefaultControlTip()==7 || tf.getDefaultControlTip()==9 || tf.getDefaultControlTip()==10/* || tf.getDefaultControlTip()==15*/){//sub table
								W5Table st = FrameworkCache.getTable(scd, tf.getDefaultLookupTableId());
								if(st==null)break;//HATA: gerekli bir alt kademe tabloya ulasilamadi
								boolean foundSt = false;
								boolean summaryMust = false;
								int dltId= 0;
								for(W5TableField stf :st.get_tableFieldList())if(stf.getDsc().equals(sss[isss+1])){
									summaryMust = (conversionTip==0) && (stf.getDefaultControlTip()==7 || stf.getDefaultControlTip()==9 || stf.getDefaultControlTip()==10) && stf.getDefaultLookupTableId()!=0;
									dltId = stf.getDefaultLookupTableId();
									foundSt = true;
									break;
								}
								if(!foundSt)break;//HATA: bir sonraki field bulunamadi
								newSub.append("(select ");
								if(isss==sss.length-2 && summaryMust){//burda t'deki summary record sql varsa
									if(smsMailReplaceTip!=0){
										if(smsMailTableIds.contains(dltId))// eger sms/mail isi ve bu bir link ise
											newSub.append("iwb.fnc_sms_mail_adress(?,").append(dltId).append(",").append("y").append(isss).append(".").append(sss[isss+1]).append(",").append(smsMailReplaceTip).append(")");
										else
											break;//HATA: sms mail tipinde olmasi gereken link, degil
									} else
										newSub.append("iwb.fnc_lookup_table_summary(?,").append(dltId).append(",").append("y").append(isss).append(".").append(sss[isss+1]).append(")");
									params.add(scd.get("userRoleId"));
								} else
									newSub.append("y").append(isss).append(".").append(sss[isss+1]);
								newSub.append(" from ").append(st.getDsc()).append(" y").append(isss)
								.append(" where y").append(isss).append(".").append(st.get_tableFieldList().get(0).getDsc())
								.append("=").append(isss==0 ? ("x."+sss[isss]):newSub2);
								if(st.get_tableFieldList().size()>1)for(W5TableField wtf :st.get_tableFieldList())if(wtf.getDsc().equals("customization_id")){
									newSub.append(" AND y").append(isss).append(".customization_id=?");
									params.add(scd.get("customizationId"));
									break;
								}
								
								newSub.append(")");
								newT = st;
							} 
							break;			
						}
						if(newSub.length()==0){//bulamamis uygun sey
							break;
						}
					}
					if(newSub.length()>0){
						sql.append(newSub).append(" ").append(fieldPrefix).append(field_cnt).append(",");
						res.put(subStr, fieldPrefix+field_cnt);
						field_cnt++;
						for(W5TableField tf :newT.get_tableFieldList())if(tf.getDsc().equals(sss[sss.length-1]))resField.put(subStr, tf);
					}
				} else if(sss.length==1){//direk lnk.field_name seklinde olanlar icin
					for(W5TableField tf :t.get_tableFieldList())if(tf.getDsc().equals(sss[0])){
						if((conversionTip==0) && (tf.getDefaultControlTip()==7 || tf.getDefaultControlTip()==9 || tf.getDefaultControlTip()==10) && tf.getDefaultLookupTableId()!=0){//sub table
							W5Table st = FrameworkCache.getTable(scd, tf.getDefaultLookupTableId());
							if(st==null)break;//HATA: gerekli bir alt kademe tabloya ulasilamadi
							
							if(smsMailReplaceTip!=0){
								if(smsMailTableIds.contains(tf.getDefaultLookupTableId()))// eger sms/mail isi ve bu bir link ise
									sql.append("iwb.fnc_sms_mail_adress(?,").append(tf.getDefaultLookupTableId()).append(",").append("x.").append(sss[0]).append(",").append(smsMailReplaceTip).append(")");
								else
									break;//HATA: sms mail tipinde olmasi gereken link, degil
							} else
								sql.append("iwb.fnc_lookup_table_summary(?,").append(tf.getDefaultLookupTableId()).append(",").append("x.").append(sss[0]).append(")");
							params.add(scd.get("userRoleId"));
						} else
							sql.append("x.").append(sss[0]);
						sql.append(" ").append(fieldPrefix).append(field_cnt).append(",");
						res.put(subStr, fieldPrefix+field_cnt);
						field_cnt++;
					}
					
				}
				if(!res.containsKey(subStr))invalidKeys.add(subStr);

			} else invalidKeys.add(subStr);;
			
		}
		Map<String,String> newRes = new HashMap<String,String>();
		Map<String,Object> qres  = null;
		
		if(field_cnt>1){
			sql.append("1 from ").append(t.getDsc()).append(" x where x.").append(t.get_tableFieldList().get(0).getDsc()).append("=?");
			params.add(tablePk);
			if(t.get_tableFieldList().size()>1 && (t.getTableId()!=338))for(W5TableField tf2:t.get_tableFieldList())if(tf2.getDsc().equals("customization_id")){
				sql.append(" AND x.customization_id=?");
				if (requestParams.get("tcustomization_id")!=null)
					params.add(requestParams.get("tcustomization_id"));
				else
					params.add(scd.get("customizationId"));
				break;
			}
			qres = runSQLQuery2Map(sql.toString(), params, null);
		} else
			qres = new HashMap();
		
		for(String keyz: res.keySet()){
			if(res.get(keyz)!=null){
				if(keyz.startsWith("req.") || keyz.startsWith("scd.") || keyz.startsWith("app.") || keyz.equals("company_logo"))
					newRes.put(keyz, res.get(keyz));
				else if(qres!=null && qres.get(res.get(keyz))!=null)
					newRes.put(keyz, qres.get(res.get(keyz)).toString());
			}
		}
		if(replace)for(int bas = tmp.indexOf("${"); bas>=0; bas=tmp.indexOf("${",bas+2)){
			if(bas>0 && tmp.charAt(bas-1)=='$'){
				tmp.replace(bas, bas+1, "");
				continue;
			}
			int bit = tmp.indexOf("}", bas+2);
			String subStr = tmp.substring(bas+2, bit);
			if(subStr.startsWith("lcl.")){ //locale msg key
				tmp.replace(bas, bit+1, LocaleMsgCache.get2(scd, subStr.substring(4)));
				continue;
			}
			String resStr = newRes.get(subStr); 
			if(resStr==null){
				if(invalidKeys.contains(subStr)){
					resStr="[ERR:"+GenericUtil.uStrMax(subStr,50)+"]";
				} else resStr="";
			} else if(conversionTip==0){ //eger sadece serbest donusum var ise
				W5TableField tf = resField.get(subStr);
				if(tf!=null)switch(tf.getDefaultControlTip()){
				case	6://combo static
					if(tf.getDefaultLookupTableId()!=0){
						W5LookUp lu = FrameworkCache.getLookUp(scd, tf.getDefaultLookupTableId());
						if(lu!=null){
							resStr = lu.get_detayMap().get(resStr).getDsc();
							resStr = LocaleMsgCache.get2((Integer)scd.get("customizationId"),(String)scd.get("locale"), resStr);
						}
					}
					break;
				case	8://tree-combo static
					if(tf.getDefaultLookupTableId()!=0){
						W5LookUp lu = FrameworkCache.getLookUp(scd, tf.getDefaultLookupTableId());
						if(lu!=null){
							String[] sss=resStr.split(",");
							if(sss!=null && sss.length>0){
								String tstr="";
								for(String s:sss){
									String qs = lu.get_detayMap().get(s).getDsc();
									qs = LocaleMsgCache.get2((Integer)scd.get("customizationId"),(String)scd.get("locale"), qs);
									tstr+=qs+", ";
								}
								if(tstr.length()>2)resStr=tstr.substring(0,tstr.length()-2);
							}
						}
					}
					break;
				case	-1://demek ki, calculated Field
					break;
				}
			}
			tmp.replace(bas, bit+1, conversionTip==0 ?  resStr : "'"+resStr+"'");
		} else if(conversionTip==0)for(String subStr:resField.keySet()){//replace yok ama herseyin degismesi gerek
			W5TableField tf = resField.get(subStr);
			if(tf!=null)switch(tf.getDefaultControlTip()){
			case	6://combo static
				if(tf.getDefaultLookupTableId()!=0){
					W5LookUp lu = FrameworkCache.getLookUp(scd, tf.getDefaultLookupTableId());
					if(lu!=null){
						String resStr = lu.get_detayMap().get(newRes.get(subStr)).getDsc();
						resStr = LocaleMsgCache.get2((Integer)scd.get("customizationId"),(String)scd.get("locale"), resStr);
						newRes.put(subStr, resStr);
					}
				}
				break;
			case	8://tree-combo static
				if(tf.getDefaultLookupTableId()!=0){
					W5LookUp lu = FrameworkCache.getLookUp(scd, tf.getDefaultLookupTableId());
					if(lu!=null){
						String[] sss=newRes.get(subStr).split(",");
						if(sss!=null && sss.length>0){
							String tstr="";
							for(String s:sss){
								String qs = lu.get_detayMap().get(s).getDsc();
								qs = LocaleMsgCache.get2((Integer)scd.get("customizationId"),(String)scd.get("locale"), qs);
								tstr+=qs+", ";
							}
							if(tstr.length()>2)newRes.put(subStr,tstr.substring(0,tstr.length()-2));
						}
					}
				}
				break;
				
			}
		}
		switch(conversionTip){
		case	3: //JavaScript
			Context cx = Context.enter();
			try {
				// Initialize the standard objects (Object, Function, etc.)
				// This must be done before scripts can be executed. Returns
				// a scope object that we use in later calls.
				Scriptable scope = cx.initStandardObjects();
				// Now evaluate the string we've colected.
				cx.evaluateString(scope, tmp.toString(), null, 1, null);

				Object resq = scope.get("result", scope);
				if(resq!=null && resq instanceof org.mozilla.javascript.Undefined)resq=null;
				tmp.setLength(0);
				if(resq!=null)tmp.append(resq);
				if(tmp.length()>2){
					int tl = tmp.length();
					if(tmp.charAt(tl-2)=='.' && tmp.charAt(tl-1)=='0'){
						Integer nv = GenericUtil.uInteger(tmp.substring(0,tl-2));
						if(nv!=null)tmp.setLength(tl-2);
					}
				}
			} finally {
	             // Exit from the context.
 	             Context.exit();
	        }
			break;
		case	4://expression
/*			Context cx2 = Context.enter();
			try {
				// Initialize the standard objects (Object, Function, etc.)
				// This must be done before scripts can be executed. Returns
				// a scope object that we use in later calls.
				Scriptable scope = cx2.initStandardObjects();
				// Now evaluate the string we've colected.
				cx2.evaluateString(scope, "var result=" + tmp.toString(), null, 1, null);

				Object resq = scope.get("result", scope);
				if(resq!=null && resq instanceof org.mozilla.javascript.Undefined)resq=null;
				tmp.setLength(0);
				if(resq!=null)tmp.append(resq);
 
			} finally {
	             // Exit from the context.
 	             Context.exit();
	        } */
			break;
		}
		return newRes;
	}
	
	
	@Override
	public boolean accessUserFieldControl(W5Table t,
			String accessUserFields, Map<String, Object> scd,			
			Map<String, String> requestParams, String paramSuffix) {
		if(paramSuffix==null)paramSuffix="";
		if (accessUserFields==null){
			return true;
		}
		StringBuilder	sql = new StringBuilder();
		sql.append("select 1 a from ").append(t.getDsc()).append(" t where (");
		String[] fieldIdz = accessUserFields.split(",");
		List<Object> params= new ArrayList();
		boolean bq=false;
		for(String s:fieldIdz){
			int tableFieldId = GenericUtil.uInt(s);
			if(tableFieldId<0){ //TODO: for conditionSQL 
				continue;
			}
			W5TableField tf = t.get_tableFieldMap().get(tableFieldId);
			if(tf!=null){
				if(bq)sql.append(" OR ");else bq=true;
				sql.append("t.").append(tf.getDsc()).append("=?");
				params.add(scd.get("userId"));
			}else {
				Integer tbId = FrameworkCache.wTableFieldMap.get(tableFieldId);
				if(tbId!=null){
					W5Table t2 = FrameworkCache.getTable(t.getCustomizationId(), tbId);
					if(t2!=null && !GenericUtil.isEmpty(t2.get_tableChildList())){
						W5TableField tf2 = t2.get_tableFieldMap().get(tableFieldId);
						for(W5TableChild tc:t2.get_tableChildList())if(tc.getRelatedTableId()==t.getTableId()){
							if(bq)sql.append(" OR ");else bq=true;
							if(tc.getRelatedStaticTableFieldId()>0){
								sql.append("(t.").append(t.get_tableFieldMap().get(tc.getRelatedStaticTableFieldId()).getDsc()).append("=").append(tc.getRelatedStaticTableFieldVal()).append(" AND ");
							}
							sql.append("exists(select 1 from ").append(t2.getDsc()).append(" hq where hq.customization_id=? AND hq.").append(tf2.getDsc()).append("=?").append(" AND hq.")
							.append(t2.get_tableFieldMap().get(tc.getTableFieldId()).getDsc()).append("=t.").append(t.get_tableFieldMap().get(tc.getRelatedTableFieldId()).getDsc());
							if(tc.getRelatedStaticTableFieldId()>0){
								sql.append(")");
							}
							sql.append(")");
							params.add(scd.get("customizationId"));
							params.add(scd.get("userId"));
							break;
						}
					}
				}
			}
		}
		sql.append(")");
		
		Map errorMap = new HashMap();
    	for(W5TableParam x: t.get_tableParamList()){
    		sql.append("AND t.").append(x.getExpressionDsc()).append(" = ? ");
    		Object psonuc = GenericUtil.prepareParam((W5Param)x, scd, requestParams, (short)-1, null, (short)1, x.getDsc()+paramSuffix, null, errorMap);
    		params.add(psonuc);
    	}
    	if(!errorMap.isEmpty())return true;//baska yerde yapsin error
    	Map<String, Object> m = runSQLQuery2Map(sql.toString(), params, null);
		return m==null || m.isEmpty();
	}

	@Override
	public List getRecordPictures(Map<String, Object> scd,int tableId,String tablePk){
		List<Object[]> l=executeSQLQuery("select x.file_attachment_id from iwb.w5_file_attachment x where x.customization_Id=? and x.table_Id=? and x.table_pk=? and exists(select 1 from gen_file_type tt where tt.customization_id=x.customization_id and tt.image_flag=1 and tt.file_type_id=x.file_type_id)" , scd.get("customizationId"),tableId,tablePk);
		List<W5FileAttachment> fa=new ArrayList<W5FileAttachment>();
		if (l!=null)for(Object o :l) fa.addAll(find("from W5FileAttachment t where t.customizationId=? and t.fileAttachmentId=?", scd.get("customizationId"),GenericUtil.uInt(o)));
		return fa;
	}
	
	@Override
	public List<Object[]> getFileType(Map<String, Object> scd, int image_flag){
		List params=new ArrayList<Object>();
		params.add(scd.get("customizationId"));
		params.add(image_flag);
		params.add(image_flag);
		List l = executeSQLQuery2Map("select x.dsc dsc,x.file_type_id id from gen_file_type x where x.customization_id = ? and (x.image_flag = ? or ? is null)", params);
		return l;
	}
	
	
	@Override
	public Object executeRhinoScript(Map<String, Object> scd, Map<String, String> requestParams, String script, Map obj, String result) {
		if(GenericUtil.isEmpty(script))return null;
		Context cx = Context.enter();
		try {
			cx.setOptimizationLevel(-1);
			// Initialize the standard objects (Object, Function, etc.)
			// This must be done before scripts can be executed. Returns
			// a scope object that we use in later calls.
			Scriptable scope = cx.initStandardObjects();

			// Collect the arguments into a single string.
			ScriptEngine se = new ScriptEngine(scd, requestParams, this, engine);
			Object wrappedOut = Context.javaToJS( se, scope);
			ScriptableObject.putProperty(scope, "$iwb", wrappedOut);

			
			StringBuilder sc = new StringBuilder();
			if(obj!=null)sc.append("var _obj=").append(GenericUtil.fromMapToJsonString2Recursive(obj)).append(";\n");
			if(scd!=null)sc.append("var _scd=").append(GenericUtil.fromMapToJsonString2(scd)).append(";\n");
			if(requestParams!=null)sc.append("var _request=").append(GenericUtil.fromMapToJsonString2(requestParams)).append(";\n");
			if(script.charAt(0)=='!')script = script.substring(1);
			sc.append(script);
	
			script = sc.toString();

			cx.evaluateString(scope, script, null, 1, null);
			if(scope.has("result", scope)){
				return scope.get("result", scope);
			}
			return null;
			
		} catch(Exception e){
			throw new IWBException("rhino", "BackendJS", 0, script, e.getMessage(), e);
		} finally {
             // Exit from the context.
	             cx.exit();
        }
	}

	
	@Override
	public Map interprateConversionTemplate(W5Conversion c,
			W5FormResult	dstFormResult,
			int conversionTablePk, boolean checkCondition, boolean onlyForSynch) {
		Map<String, Object> scd = dstFormResult.getScd();
		int customizationId = (Integer)scd.get("customizationId");
		Map<String, String> requestParams = new HashMap(); requestParams.putAll(dstFormResult.getRequestParams());requestParams.put("_conversion_table_pk", ""+conversionTablePk);
		W5FormResult	srcFormResult = getFormResult(scd, c.getSrcFormId(), 2, requestParams);
		W5Form sf = srcFormResult.getForm();
		W5Form df = dstFormResult.getForm();
		int conversionTableId = sf.getObjectId();
		if(GenericUtil.isEmpty(sf.get_conversionList()))return null;
		if(c.getDstFormId()!=df.getFormId())return null;//boyle bir forma donus yok
		if(c.get_conversionColList()==null)for(W5Conversion co:sf.get_conversionList())if(co.getConversionId()==c.getConversionId()){
			c.set_conversionColList(co.get_conversionColList());
			break;
		}
		if(c.get_conversionColList()==null)return null;
		Map<String, Object> m = new HashMap<String, Object>();
		if(checkCondition && !GenericUtil.isEmpty(c.getConditionSqlCode())){ //TODO
			boolean b = conditionRecordExistsCheck(scd, dstFormResult.getRequestParams(), sf.getObjectId(), conversionTablePk, c.getConditionSqlCode());
			if(!b)return null;
		}
		//TODO: source kayda yetki kontrolu olacak
		
		for(W5ConversionCol cCol:c.get_conversionColList())if(!onlyForSynch || cCol.getSynchFlag()!=0){
			if(!GenericUtil.isEmpty(cCol.getConversionCode())){
				String cc = cCol.getConversionCode();
				switch(cCol.getFieldConversionTip()){
				case	2://SQL
					Map<String, Object> sqlm = runSQLQuery2Map(cc, scd, requestParams, null);
					cc = GenericUtil.isEmpty(sqlm) ? null : sqlm.get("result").toString();
					break;
				case	6://Rhino
					Object result = GenericUtil.rhinoValue(executeRhinoScript(dstFormResult.getScd(), dstFormResult.getRequestParams(), cc, null, "result"));
					if(result!=null) cc = result.toString();
					break;
				case	1://not implemented
				default:
					if(cc.contains("${")){
						StringBuilder tmp1 = new StringBuilder(cc);
						interprateTemplate(scd, dstFormResult.getRequestParams(), conversionTableId, conversionTablePk, tmp1, true, 0, cCol.getFieldConversionTip());
						cc = tmp1.toString();
					} 
				}
				for(W5FormCell fc:df.get_formCells())if(cCol.getFormCellId()==fc.getFormCellId()){//buna donusecek
					if(fc.get_sourceObjectDetail()==null)break;
					short fieldTip = ((W5TableField)fc.get_sourceObjectDetail()).getFieldTip();
					Object ob = GenericUtil.getObjectByTip(cc, fieldTip);
					if(ob!=null)m.put(fc.getDsc(), fieldTip==2/*date?*/ ? cc : ob.toString());
					break;
				}
					
			}
		}
		m.put("_cnv", c);
		m.put("_cnv_name", c.getDsc());
		m.put("_cnv_src_tbl_id", ""+c.getSrcTableId());
		m.put("_cnv_src_tbl_pk", ""+conversionTablePk);
		m.put("_cnv_src_frm_id", ""+sf.getFormId());
		m.put("_cnv_record", getSummaryText4Record(scd, c.getSrcTableId(),conversionTablePk));
		return m;
	}
	
	public boolean conditionRecordExistsCheck(Map<String, Object> scd, Map<String, String> requestParams, int tableId, int tablePk, String conditionSqlCode) {
		W5Table t = FrameworkCache.getTable(scd, tableId);
		StringBuilder sql = new StringBuilder();
		sql.append("select 1 from ").append(t.getDsc()).append(" x where x.").append(t.get_tableParamList().get(0).getExpressionDsc()).append("=? AND ");
		List params = new ArrayList();
		params.add(tablePk);
		if(t.get_tableParamList().size()>1 && t.get_tableParamList().get(1).getExpressionDsc().equals("customization_id")){
			sql.append(" x.customization_id=? AND ");
			params.add(scd.get("customizationId"));
		}
		Object[] oz = DBUtil.filterExt4SQL(conditionSqlCode, scd, new HashMap(), null);
		sql.append(oz[0]);
		if(oz.length>1 && oz[1]!=null)params.addAll((List)oz[1]);
		List l = executeSQLQuery2(sql.toString(), params);
		return !GenericUtil.isEmpty(l);
	}

	@Override
	public Map interprateConversionTemplate4WsMethod(Map<String, Object> scd, Map<String, String> requestParams,W5Conversion c,
			int conversionTablePk, W5WsMethod	wsm) {
		int customizationId = (Integer)scd.get("customizationId");
		requestParams.put("_conversion_table_pk", ""+conversionTablePk);
		int conversionTableId = c.getSrcTableId();
		Map<String, Object> m = new HashMap<String, Object>();
	
		for(W5ConversionCol cCol:c.get_conversionColList()){
			if(!GenericUtil.isEmpty(cCol.getConversionCode())){
				String cc = cCol.getConversionCode();
				switch(cCol.getFieldConversionTip()){
				case	2://SQL
					Map<String, Object> sqlm = runSQLQuery2Map(cc, scd, requestParams, null);
					cc = GenericUtil.isEmpty(sqlm) ? null : sqlm.get("result").toString();
				case	6://Rhino
					Object result = GenericUtil.rhinoValue(executeRhinoScript(scd, requestParams, cc, null, "result"));
					if(result!=null) cc = result.toString();
					break;
				case	1://not implemented
				default:
					if(cc.contains("${")){
						StringBuilder tmp1 = new StringBuilder(cc);
						interprateTemplate(scd, requestParams, conversionTableId, conversionTablePk, tmp1, true, 0, cCol.getFieldConversionTip());
						cc = tmp1.toString();
					} 
				}
				for(W5WsMethodParam wsmp:wsm.get_params())if(cCol.getFormCellId()==wsmp.getWsMethodParamId()){//buna donusecek
					Object ob = GenericUtil.getObjectByTip(cc, wsmp.getParamTip());
					if(ob!=null)m.put(wsmp.getDsc(), wsmp.getParamTip()==2/*date?*/ ? cc : ob.toString());
					break;
				}
					
			}
		}
		return m;
	}
	private void addPostQueryFields(W5QueryResult queryResult, StringBuilder sql2, AtomicInteger paramIndex) {
		W5Query query = queryResult.getQuery();
		W5Table mainTable = queryResult.getMainTable();
		int customizationId = (Integer)queryResult.getScd().get("customizationId");
		String pkFieldName=query.getQueryTip()==9 ? query.get_queryFields().get(0).getDsc():"pkpkpk_id";
		if(FrameworkSetting.vcs && mainTable.getVcsFlag()!=0/* && query.getSqlSelect().startsWith("x.*")*/){//VCS
//			sql2.append(",(select cx.vcs_object_status_tip from iwb.w5_vcs_object cx where cx.table_id=").append(query.getMainTableId()).append(" AND cx.customization_id=").append(customizationId).append(" AND cx.table_pk=z.pkpkpk_id) ").append(FieldDefinitions.queryFieldName_Vcs).append(" ");
//			sql2.append(",fnc_vcs_status(").append(customizationId).append(",'").append(queryResult.getScd().get("projectId")).append("',").append(query.getMainTableId()).append(",z.pkpkpk_id,").append(FrameworkUtil.getTableFields4VCS(mainTable,"z")).append(") ").append(FieldDefinitions.queryFieldName_Vcs).append(" ");
			sql2.append(",iwb.fnc_vcs_status2(").append(customizationId).append(",'").append(queryResult.getScd().get("projectId")).append("',").append(query.getMainTableId()).append(",z.").append(pkFieldName).append(") ").append(FieldDefinitions.queryFieldName_Vcs).append(" ");
			W5QueryField field = new W5QueryField();
			field.setDsc(FieldDefinitions.queryFieldName_Vcs);
			queryResult.getPostProcessQueryFields().add(field);
		}
		if(FrameworkCache.getAppSettingIntValue(queryResult.getScd(), "row_based_security_flag")!=0 && mainTable.getAccessTips()!=null && mainTable.getAccessTips().indexOf("0")>-1 && (queryResult.getQueryColMap()==null || queryResult.getQueryColMap().containsKey(FieldDefinitions.queryFieldName_RowBasedSecurity))){
			sql2.append(",(select 1 from iwb.w5_access_control cx where cx.table_id=").append(query.getMainTableId()).append(" AND cx.customization_id=").append(customizationId).append(" AND cx.table_pk=z.").append(pkFieldName).append(" limit 1) ").append(FieldDefinitions.queryFieldName_RowBasedSecurity).append(" ");
			W5QueryField field = new W5QueryField();
			field.setDsc(FieldDefinitions.queryFieldName_RowBasedSecurity);
			queryResult.getPostProcessQueryFields().add(field);
		}
		if(FrameworkCache.getAppSettingIntValue(queryResult.getScd(), "file_attachment_flag")!=0 && mainTable.getFileAttachmentFlag()!=0 && FrameworkCache.roleAccessControl(queryResult.getScd(),  101) && (queryResult.getQueryColMap()==null || queryResult.getQueryColMap().containsKey(FieldDefinitions.queryFieldName_FileAttachment))){//fileAttachment
//			sql2.append(",(select 1 from iwb.w5_file_attachment cx where rownum=1 AND cx.table_id=").append(query.getMainTableId()).append(" AND cx.customization_id=").append(customizationId).append(" AND cx.table_pk=to_char(z.pkpkpk_id)) pkpkpk_faf ");
			sql2.append(",(select count(1) from iwb.w5_file_attachment cx where cx.table_id=").append(query.getMainTableId()).append(" AND cx.customization_id=").append(customizationId).append(" AND cx.table_pk=(z.").append(pkFieldName).append(")::text limit 10) ").append(FieldDefinitions.queryFieldName_FileAttachment).append(" ");
			W5QueryField field = new W5QueryField();
			field.setDsc(FieldDefinitions.queryFieldName_FileAttachment);
			queryResult.getPostProcessQueryFields().add(field);
		}
		if(FrameworkCache.getAppSettingIntValue(queryResult.getScd(), "make_comment_flag")!=0 && mainTable.getMakeCommentFlag()!=0 && (queryResult.getQueryColMap()==null || queryResult.getQueryColMap().containsKey(FieldDefinitions.queryFieldName_Comment))){//comment
//			sql2.append(",(select 1 from iwb.w5_comment cx where rownum=1 AND cx.table_id=").append(query.getMainTableId()).append(" AND cx.customization_id=").append(customizationId).append("  AND cx.table_pk=z.").append(pkFieldName).append(") pkpkpk_cf ");
			W5QueryField field = new W5QueryField();
			field.setDsc(FieldDefinitions.queryFieldName_Comment);
			if(FrameworkCache.getAppSettingIntValue(queryResult.getScd(), "make_comment_summary_flag")!=0){
				sql2.append(",(select cx.comment_count||';'||cxx.comment_user_id||';'||to_char(cxx.comment_dttm,'dd/mm/yyyy hh24:mi:ss')||';'||cx.view_user_ids||'-'||cxx.dsc from iwb.w5_comment_summary cx, iwb.w5_comment cxx where cx.table_id=").append(query.getMainTableId()).append(" AND cx.customization_id=").append(customizationId)
				.append("  AND cx.table_pk::int=z.").append(pkFieldName).append(" AND cxx.customization_id=cx.customization_id AND cxx.comment_id=cx.last_comment_id) pkpkpk_cf ");
				field.setPostProcessTip((short)48);//extra code : commentCount-commentUserId-lastCommentDttm-viewUserIds-msg
			} else {
				sql2.append(",(select count(1) from iwb.w5_comment cx where cx.table_id=").append(query.getMainTableId()).append(" AND cx.customization_id=").append(customizationId).append("  AND cx.table_pk=z.").append(pkFieldName).append(" limit 10) ").append(FieldDefinitions.queryFieldName_Comment).append(" ");
			}
			queryResult.getPostProcessQueryFields().add(field);
		}
		if(FrameworkCache.getAppSettingIntValue(queryResult.getScd(), "approval_flag")!=0 && mainTable.get_approvalMap()!=null && !mainTable.get_approvalMap().isEmpty() && (queryResult.getQueryColMap()==null || queryResult.getQueryColMap().containsKey(FieldDefinitions.queryFieldName_Approval))){//approval Record 
			sql2.append(",(select cx.approval_record_id||';'||cx.approval_id||';'||cx.approval_step_id||';'||coalesce(cx.approval_roles,'')||';'||coalesce(cx.approval_users,'') from iwb.w5_approval_record cx where cx.table_id=").append(query.getMainTableId()).append(" AND cx.customization_id=").append(customizationId).append(" AND cx.table_pk=z.").append(pkFieldName).append(" limit 1) ").append(FieldDefinitions.queryFieldName_Approval).append(" ");
			W5QueryField field = new W5QueryField();
			field.setDsc(FieldDefinitions.queryFieldName_Approval);
			field.setPostProcessTip((short)49);//approvalPostProcessTip2
			queryResult.getPostProcessQueryFields().add(field);
			if(FrameworkCache.getAppSettingIntValue(queryResult.getScd(), "toplu_onay")!=0){
				sql2.append(",(select cx.version_no from iwb.w5_approval_record cx where cx.table_id=").append(query.getMainTableId()).append(" AND cx.customization_id=").append(customizationId).append(" AND cx.table_pk=z.").append(pkFieldName).append(" limit 1) ").append(FieldDefinitions.queryFieldName_ArVersionNo).append(" ");
				field = new W5QueryField();
				field.setDsc(FieldDefinitions.queryFieldName_ArVersionNo);
				queryResult.getPostProcessQueryFields().add(field);
			}
		}

		
	}
	
	
	
	 @Override
	public void saveObject2(Object o, Map<String, Object> scd) {
		saveObject(o);
		if(o instanceof Log5Notification){
			Log5Notification n = (Log5Notification)o;
			if(n.getTableId()!=0 && n.getTablePk()!=0){
				if(scd==null){
					scd = new HashMap(); //TODO: boyle olmaz, scd'yi al
					scd.put("userId", n.getActionUserId());
					scd.put("customizationId", n.getCustomizationId());
					scd.put("locale", "tr");
				}
				n.set_tableRecordList(findRecordParentRecords(scd,n.getTableId(),n.getTablePk(),0, true));
			}
			UserUtil.publishNotification(n, false);
		}		 
	 }
	@Override
	public M5ListResult getMListResult(Map<String, Object> scd, int listId, Map<String,String> requestParams, boolean noSearchForm) {
		int customizationId = (Integer)scd.get("customizationId");
		M5ListResult mlistResult = new M5ListResult(listId);
		mlistResult.setRequestParams(requestParams);
		mlistResult.setScd(scd);
		
		M5List d = null;
		if(FrameworkSetting.preloadWEngine!=0 && (d=FrameworkCache.getMListView(customizationId,listId))!=null){
			mlistResult.setList(d);
		} else {
			d = (M5List)getCustomizedObject("from M5List t where t.listId=? and t.customizationId=?", listId, customizationId, "MobileList"); // ozel bir client icin varsa
			W5Query q = (W5Query)getCustomizedObject("from W5Query t where t.queryId=? and t.customizationId=?", d.getQueryId(), customizationId, "QueryID"); // ozel bir client icin varsa
			d.set_query(q);
			if(q.getMainTableId()!=0)d.set_mainTable(FrameworkCache.getTable(customizationId, q.getMainTableId()));
			/*if(d.getDefaultCrudFormId()!=0){
				W5Form f = (W5Form)getCustomizedObject("from W5Form t where t.formId=? and t.customizationId=?", d.getDefaultCrudFormId(), customizationId); // ozel bir client icin varsa
				f.set_formCells(find("from W5FormCell t where t.formId=? and t.customizationId=? order by t.tabOrder", d.getDefaultCrudFormId(), customizationId));
				d.set_crudForm(f);
			}*/
			Integer searchFormId = (Integer)getCustomizedObject("select t.formId from W5Form t where t.customizationId=? AND t.objectTip=10 and t.objectId=?", customizationId, listId, null); 
			if(searchFormId!=null)d.set_searchFormId(searchFormId);
			d.set_detailMLists(find("from M5List l where l.customizationId=? AND l.parentListId=? order by l.listId", customizationId, listId));
			mlistResult.setList(d);
			if(!GenericUtil.isEmpty(d.getOrderQueryFieldIds()))d.set_orderQueryFieldNames(find("select qf.dsc from W5QueryField qf where qf.queryId=? and qf.customizationId=? AND qf.queryFieldId in ("+d.getOrderQueryFieldIds()+") order by qf.tabOrder", d.getQueryId(), customizationId));
			d.set_toolbarItemList(find("from W5ObjectToolbarItem t where t.objectTip=? AND t.objectId=? AND t.customizationId=? order by t.tabOrder",(short)1345, listId,customizationId));

			d.set_menuItemList(find("from W5ObjectMenuItem t where t.objectTip=? AND t.objectId=? AND t.customizationId=? order by t.tabOrder",(short)1345, listId,customizationId));


		//	d = mlistResult.getList();
//			if(FrameworkSetting.preloadWEngine!=0)FrameworkCache.wListViews.get(d.getCustomizationId()).put(listId, d);
		}
		//search Form
		if(!noSearchForm && d.get_searchFormId()!=0){
			W5FormResult	searchForm = getFormResult(scd, d.get_searchFormId(), 10, requestParams);
			initializeForm(searchForm, false);
			loadFormCellLookups(mlistResult.getScd(), searchForm.getFormCellResults(), mlistResult.getRequestParams(), null);
			mlistResult.setSearchFormResult(searchForm);
		}
		return mlistResult;
	}
	@Override
	public W5ListViewResult getListViewResult(Map<String, Object> scd, int listViewId, Map<String,String> requestParams, boolean noSearchForm) {
		
		W5ListViewResult listViewResult = new W5ListViewResult(listViewId);
		int customizationId = listViewResult.isDev() ? 0:(Integer)scd.get("customizationId");
		listViewResult.setRequestParams(requestParams);
		listViewResult.setScd(scd);
		
		W5List d = null;
		if(FrameworkSetting.preloadWEngine!=0 && (d=FrameworkCache.getListView(customizationId,listViewId))!=null){
			listViewResult.setListView(d);
		} else {
			loadListView(listViewResult);
			d = listViewResult.getListView();
			if(FrameworkSetting.preloadWEngine!=0)FrameworkCache.wListViews.get(customizationId).put(listViewId, d);
		}
		//search Form
		if(!noSearchForm && d.get_searchFormId()!=0){
			W5FormResult	searchForm = getFormResult(scd, d.get_searchFormId(), 72, requestParams);
			initializeForm(searchForm, false);
			loadFormCellLookups(listViewResult.getScd(), searchForm.getFormCellResults(), listViewResult.getRequestParams(), null);
			listViewResult.setSearchFormResult(searchForm);
		}
		return listViewResult;
	}
	private void loadListView(W5ListViewResult listViewResult) {
		int customizationId = listViewResult.isDev() ? 0 : FrameworkCache.getCustomizationId(listViewResult.getScd());
		if(customizationId!=0){
			W5Customization cus = FrameworkCache.wCustomizationMap.get(customizationId);
		}	
		W5List d = (W5List)getCustomizedObject("from W5List t where t.listId=? and t.customizationId=?", listViewResult.getListId(), customizationId, "List"); // ozel bir client icin varsa
		listViewResult.setListView(d);

		W5Query query = null;
		if(FrameworkSetting.preloadWEngine!=0){
			query = getQueryResult(listViewResult.getScd(), d.getQueryId()).getQuery(); 
		}
		
		if(query==null){
			query = new W5Query();
			List<W5QueryField> queryFields = find("from W5QueryField t where t.queryId=? order by t.tabOrder", d.getQueryId());
			d.set_query(query);
			query.set_queryFields(queryFields); // dataReader icin gerekli
			query.setMainTableId((Integer)find("select t.mainTableId from W5Query t where t.queryId=?", d.getQueryId()).get(0));
		} else
			d.set_query(query);
		
		d.set_mainTable(FrameworkCache.getTable(customizationId, query.getMainTableId()));
		d.set_listColumnList(find("from W5ListColumn t where t.customizationId=? AND t.listId=? order by t.tabOrder",customizationId, listViewResult.getListId()));
		
		Map<Integer, W5QueryField> fieldMap = new HashMap<Integer, W5QueryField>();
		Map<String, W5QueryField> fieldMapDsc = new HashMap<String, W5QueryField>();
		for(W5QueryField field:query.get_queryFields()){
			fieldMap.put(field.getQueryFieldId(), field);
			fieldMapDsc.put(field.getDsc(), field);
		}

		d.set_queryFieldMap(fieldMap);
		
		d.set_queryFieldMapDsc(fieldMapDsc);
		d.set_pkQueryField(fieldMap.get(d.getPkQueryFieldId()));
		int totalWidth = 0;
		for(W5ListColumn lc:d.get_listColumnList()){
			lc.set_queryField(fieldMap.get(lc.getQueryFieldId()));
			totalWidth+=lc.getWidth();
		}
		d.set_totalWidth(totalWidth);

		List<Integer> formIdz = find("select t.formId from W5Form t where t.customizationId=? AND t.objectTip=7 and t.objectId=?", customizationId, listViewResult.getListId()); 
		if(formIdz.size()>0 && formIdz.get(0)!=null)d.set_searchFormId(formIdz.get(0));
		
		d.set_toolbarItemList(find("from W5ObjectToolbarItem t where t.objectTip=? AND t.objectId=? AND t.customizationId=? order by t.tabOrder",(short)8,listViewResult.getListId(),customizationId));
		for(W5ObjectToolbarItem c:d.get_toolbarItemList())switch(c.getItemTip()){//TODO:toolbar icine bisey konulacaksa
		case	10:case	7:case	15:case	9:
			break;
		case	14:case	8:case	6:
			break;
		}
	}
	@Override
	public List<W5TableChildHelper> findRecordChildRecords(Map<String, Object> scd, int tableId,
			int tablePk) {
		W5Table t = FrameworkCache.getTable(scd, tableId);
		if(t==null || GenericUtil.isEmpty(t.get_tableChildList()))return null;
		List<W5TableChildHelper> r = new ArrayList<W5TableChildHelper>(t.get_tableChildList().size());
		for(W5TableChild tc:t.get_tableChildList()){
			W5Table ct = FrameworkCache.getTable(scd, tc.getRelatedTableId());
			if(ct==null){
				logger.error("ERROR(findRecordChildRecords) for relatedTableId="+tc.getRelatedTableId());
				continue;
			}
			switch(ct.getAccessViewTip()){
			case	0:
				if(!FrameworkCache.roleAccessControl(scd,  0)){
					continue;
				}
				break;
			case	1:
				if(ct.getAccessViewUserFields()==null && !GenericUtil.accessControl(scd, ct.getAccessViewTip(), ct.getAccessViewRoles(), ct.getAccessViewUsers())){
					continue;
				}
			}
			StringBuilder sql = new StringBuilder();
			sql.append("select count(1) xcount");
			if(ct.getMakeCommentFlag()!=0){
				sql.append(",sum((select count(1) from iwb.w5_comment c where c.customization_id=").append(scd.get("customizationId")).append(" AND c.table_id=").append(ct.getTableId()).append(" AND c.table_pk=x.").append(ct.get_tableParamList().get(0).getExpressionDsc()).append("::text)) xcomment_count");
			}
			if(ct.getFileAttachmentFlag()!=0){
				sql.append(",sum((select count(1) from iwb.w5_file_attachment c where c.customization_id=").append(scd.get("customizationId")).append(" AND c.table_id=").append(ct.getTableId()).append(" AND c.table_pk=x.").append(ct.get_tableParamList().get(0).getExpressionDsc()).append("::text)) xfile_count");
			}
			sql.append(" from ").append(ct.getDsc()).append(" x where x.").append(ct.get_tableFieldMap().get(tc.getRelatedTableFieldId()).getDsc()).append("=").append(tablePk);
			if(tc.getRelatedStaticTableFieldId()!=0){
				sql.append(" AND x.").append(ct.get_tableFieldMap().get(tc.getRelatedStaticTableFieldId()).getDsc()).append("=").append(tc.getRelatedStaticTableFieldVal());
			}
			if(ct.get_tableParamList().size()>1 && ct.get_tableParamList().get(1).getExpressionDsc().equals("customization_id")){
				sql.append(" AND x.customization_id=").append(scd.get("customizationId"));
			}
			if(FrameworkSetting.tableChildrenMaxRecordNumber>0)
				sql.append(" limit ").append(FrameworkSetting.tableChildrenMaxRecordNumber);
			/* //TODO. burda bir de iwb.w5_access_control ve approval yapilacak
			if(ct.getAccessTips()!=null && PromisUtil.hasPartInside2(ct.getAccessTips(), "0")){
				sql.append(" left outer join iwb.w5_access_control ac on ac.ACCESS_TIP=0 AND ac.table_id=").append(t.getTableId()).append(" AND ac.customization_id=${scd.customizationId} AND ac.table_pk=x.").append(t.get_tableParamList().get(0).getExpressionDsc());
			}
*/
			List<Map> l = executeSQLQuery2Map(sql.toString(), null);
			if(!GenericUtil.isEmpty(l)){
				Map m = l.get(0);
				r.add(new W5TableChildHelper(tc, GenericUtil.uInt(m,"xcount"),GenericUtil.uInt(m,"xcomment_count"),GenericUtil.uInt(m,"xfile_count")));
			}
			
		}
		return r;
	}
	
	@Override
	public boolean accessControlTable(Map<String, Object> scd, W5Table t, Integer tablePk){
		if(!GenericUtil.accessControlTable(scd, t))return false;
		if(tablePk!=null && (!GenericUtil.isEmpty(t.get_approvalMap()) 
								|| (t.getAccessViewTip()!=0 && ((t.getAccessTips()!=null && GenericUtil.hasPartInside2(t.getAccessTips(), "0") 
									  							|| !GenericUtil.isEmpty(t.getAccessViewUserFields())))))){ //TODO : ekstra record bazli kontrol
			
		}
		return true;
	}
		
	@Override
	public W5TableRecordInfoResult getTableRecordInfo(Map<String, Object> scd,
			int tableId, int tablePk) {
		W5TableRecordInfoResult result = new W5TableRecordInfoResult(scd, tableId, tablePk);;
		W5Table t = FrameworkCache.getTable(scd, tableId);
		if(t==null || !accessControlTable(scd, t, tablePk))return null;
		Map<String,W5TableField> fieldMap1 = new HashMap();
		for(W5TableField tf:(List<W5TableField>)t.get_tableFieldList()){
		   	fieldMap1.put(tf.getDsc(),tf);
		}
		
		if((fieldMap1.get("INSERT_USER_ID")!=null || fieldMap1.get("insert_user_id")!=null )&& (fieldMap1.get("VERSION_USER_ID")!=null || fieldMap1.get("version_user_id")!=null)){
			StringBuilder sql = new StringBuilder();
			List params = new ArrayList();
			sql.append("select x.version_no, x.insert_user_id, x.insert_dttm, x.version_user_id, x.version_dttm from ").append(t.getDsc()).append(" x where x.").append(t.get_tableParamList().get(0).getExpressionDsc()).append("=?");
			params.add(tablePk);
			if(t.get_tableParamList().size()>1 && t.get_tableParamList().get(1).getExpressionDsc().equals("customization_id")){
				sql.append(" AND x.customization_id=?");
				params.add(scd.get("customizationId"));
			}
			
			List<Map> l = executeSQLQuery2Map(sql.toString(), params);
			if(!GenericUtil.isEmpty(l)){
				Map m = l.get(0);
				result.setVersionNo(GenericUtil.uInt(m,"version_no"));
				result.setInsertUserId(GenericUtil.uInt(m,"insert_user_id"));
				result.setInsertDttm((String)m.get("insert_dttm"));
				result.setVersionUserId(GenericUtil.uInt(m,"version_user_id"));
				result.setVersionDttm((String)m.get("version_dttm"));
			}
		}
		int extraSqlCount=0;
		StringBuilder extraSql=new StringBuilder();
		
		if(FrameworkCache.getAppSettingIntValue(scd, "file_attachment_flag")!=0 && t.getFileAttachmentFlag()!=0){
			extraSql.append("(select count(1) cnt from iwb.w5_file_attachment x where x.customization_id=? AND x.table_id=? AND x.table_pk=?::text) file_attach_count");
			extraSqlCount++;
		} else result.setFileAttachmentCount(-1);
		if(FrameworkCache.getAppSettingIntValue(scd, "make_comment_flag")!=0 && t.getMakeCommentFlag()!=0){
			if(extraSql.length()>0)extraSql.append(",");
			extraSql.append("(select count(1) cnt from iwb.w5_comment x where x.customization_id=? AND x.table_id=? AND x.table_pk=?::text) comment_count");
			extraSqlCount++;
		} else result.setCommentCount(-1);

		if(FrameworkCache.getAppSettingIntValue(scd, "row_based_security_flag")!=0 && (Integer)scd.get("userTip")!=3 && t.getAccessTips()!=null && t.getAccessTips().length()>0){
			if(extraSql.length()>0)extraSql.append(",");
			extraSql.append("(select count(1) cnt from iwb.w5_access_control x where x.customization_id=? AND x.table_id=? AND x.table_pk=?) access_count");
			extraSqlCount++;
		} else result.setAccessControlCount(-1);

		List<Object> params= new ArrayList(extraSqlCount*3+5);
		for(int qi=0;qi<extraSqlCount;qi++){
			params.add(scd.get("customizationId"));
			params.add(tableId);
			params.add(tablePk);
		}

		if(FrameworkCache.getAppSettingIntValue(scd, "form_conversion_flag")!=0){
			if(extraSql.length()>0)extraSql.append(",");
			extraSql.append("(select count(1) cnt from iwb.w5_converted_object y, iwb.w5_conversion x where x.active_flag=1 AND x.customization_id=? AND x.customization_id=y.customization_id AND x.conversion_id=y.conversion_id AND x.src_table_id=? AND y.src_table_pk=?) conversion_count");
			params.add(scd.get("customizationId"));
			params.add(tableId);
			params.add(tablePk);
			extraSqlCount++;
		} else result.setConversionCount(-1);

		if(extraSql.length()>0){

			List<Map> l = executeSQLQuery2Map("select "+extraSql.append(" ").toString(), params);//from dual
			if(!GenericUtil.isEmpty(l)){
				Map m = l.get(0);
	    		if(result.getFileAttachmentCount()!=-1)result.setFileAttachmentCount(GenericUtil.uInt(m.get("file_attach_count")));
	    		if(result.getCommentCount()!=-1)result.setCommentCount(GenericUtil.uInt(m.get("comment_count")));
	    		if(result.getAccessControlCount()!=-1)result.setAccessControlCount(GenericUtil.uInt(m.get("access_count")));
	    		if(result.getFormMailSmsCount()!=-1)result.setFormMailSmsCount(GenericUtil.uInt(m.get("mail_sms_count")));
	    		if(result.getConversionCount()!=-1)result.setConversionCount(GenericUtil.uInt(m.get("conversion_count")));
	    	}
		}
		return result;
		
	}
	



	
	@Override
	public String getSummaryText4Record(Map<String, Object> scd, int tableId,
			int tablePk) {
		W5Table t = FrameworkCache.getTable(scd, tableId);
		String summarySql = t.getSummaryRecordSql();
		String summaryText = null;
		if(!GenericUtil.isEmpty(summarySql)){
			List params = new ArrayList();
			if(summarySql.indexOf("${")>-1){
				Object[] oz = DBUtil.filterExt4SQL(summarySql, scd, new HashMap(), null);
				summarySql = oz[0].toString();
				if(oz.length>1 && oz[1]!=null)params = (List)oz[1]; 
			} 
			String sql = "select "+summarySql+" dsc from "+t.getDsc()+" x where x."+t.get_tableParamList().get(0).getExpressionDsc()+ "=?";
			params.add(tablePk);
			if(t.get_tableParamList().size()==2 && t.get_tableParamList().get(1).getDsc().equals("customizationId")){
				sql+=" AND x.customization_id=?";
				params.add(scd.get("customizationId"));
			}
			Map<String, Object> m = runSQLQuery2Map(sql,params,null);
			if(m!=null)
				summaryText = (String)m.get("dsc");
			if(summaryText==null){
				summaryText="ERROR: "+sql;
			}
		}
		else 
			summaryText ="TODO: make Summary SQL on Table: "+ /*formResult.getForm().get_sourceTable()*/t.getDsc();
		return summaryText;		
	}
	
	@Override
	public boolean checkIfRecordsExists(Map scd, 
			Map<String, String> requestParams, W5Table t) {
		StringBuilder sql = new StringBuilder();
		sql.append("select 1 from ").append(t.getDsc()).append(" t where ");
		
		List<Object> params  = new ArrayList();
		boolean b = false;
		Map m = new HashMap();
		for(W5TableParam x: t.get_tableParamList()){
    		if(b){
    			sql.append(" AND ");
    		}else 
    			b=true;
    		sql.append("t.").append(x.getExpressionDsc()).append(" = ? ");
    		Object psonuc = GenericUtil.prepareParam((W5Param)x, scd, requestParams, (short)-1, null, (short)1, null, null, m);
    		params.add(psonuc);
    	}
		
		
		return runSQLQuery2Map(sql.toString(),params,null)!=null;
	}
	
	@Override
	public void removeTableChildRecords(Map<String, Object> scd, int tableId,
			int tablePk, String dstDetailTableIds) {
		if(tableId==0 || GenericUtil.isEmpty(dstDetailTableIds))return;
		W5Table t = FrameworkCache.getTable(scd, tableId);
		String[] dtsl=dstDetailTableIds.split(",");
		for(String dts:dtsl){
			int detailTableId = GenericUtil.uInt(dts);
			for(W5TableChild tc:t.get_tableChildList())if(tc.getRelatedTableId()==detailTableId){
				W5Table dt = FrameworkCache.getTable(scd, detailTableId);
				List<Object> params=new ArrayList();
				StringBuilder sql = new StringBuilder();
				sql.append("delete from ").append(dt.getDsc()).append(" x where x.customization_id=?");
				params.add(scd.get("customizationId"));
				sql.append(" AND x.").append(dt.get_tableFieldMap().get(tc.getRelatedTableFieldId()).getDsc()).append("=?");
				params.add(tablePk);
				if(tc.getRelatedStaticTableFieldId()!=0){
					sql.append(" AND x.").append(dt.get_tableFieldMap().get(tc.getRelatedStaticTableFieldId()).getDsc()).append("=?");
					params.add(tc.getRelatedStaticTableFieldVal());
				}
				executeUpdateSQLQuery(sql.toString(), params);
			}
			
		}
	}
	
	@Override
	public W5FileAttachment getFileAttachment(int fileAttachmentId){
		return (W5FileAttachment)find("from W5FileAttachment t where t.fileAttachmentId=?", fileAttachmentId).get(0);
	}
	
	@Override
	public String getObjectVcsHash(Map<String, Object> scd, int tableId, int tablePk) {
		W5Table t = FrameworkCache.getTable(scd, tableId);
		StringBuilder s = new StringBuilder();
		s.append("select iwb.md5hash(").append(getTableFields4VCS(t,"x")).append(") xhash from ").append(t.getDsc()).append(" x where x.").append(t.get_tableParamList().get(0).getExpressionDsc()).append("=?");
		List p = new ArrayList();
		p.add(tablePk);
		if(t.get_tableParamList().size()>1){
			s.append(" AND x.customization_id=?");
			p.add(scd.get("customizationId"));
		}
		List l = executeSQLQuery2Map(s.toString(), p);
		if(GenericUtil.isEmpty(l))
		return "!";
		else return (String)((Map)l.get(0)).get("xhash");
	}
	
	@Override
	public Map getTableRecordJson(Map<String, Object> scd, int tableId, int tablePk, int recursiveLevel) {
		W5Table t = FrameworkCache.getTable(scd, tableId);
		StringBuilder s = new StringBuilder();
		s.append("select x.* from ").append(t.getDsc()).append(" x where x.").append(t.get_tableParamList().get(0).getExpressionDsc()).append("=?");
		if(t.get_tableParamList().size()>1)s.append(" AND x.customization_id=").append(scd.get("customizationId"));
		List p= new ArrayList();p.add(tablePk);
		List l = executeSQLQuery2Map(s.toString(), p);
		return GenericUtil.isEmpty(l) ? null : (Map)l.get(0);
	}
	
	@Override
	public boolean saveVcsObject(Map<String, Object> scd, int tableId, int tablePk, int action, JSONObject o) { //TODO
//		dao.updatePlainTableRecord(t, o, vo.getTablePk(), srvCommitUserId);
		W5Table t = FrameworkCache.getTable(scd, tableId);
		StringBuilder s = new StringBuilder();
		List p = new ArrayList();
		switch(action){
		case	1://update
			s.append("update ").append(t.getDsc()).append(" set ");
			for(W5TableField f:t.get_tableFieldList())if(f.getTabOrder()>1){
				if(f.getDsc().equals("insert_user_id") || f.getDsc().equals("insert_dttm") || f.getDsc().equals("customization_id") || f.getDsc().equals("project_uuid"))
						continue;
				if(f.getDsc().equals("version_dttm")){
					s.append(f.getDsc()).append("=iwb.fnc_sysdate(0),");
					continue;
				}
				s.append(f.getDsc()).append("=?,");
				try {
					if(o.has(f.getDsc())){
						p.add(GenericUtil.getObjectByControl((String)o.get(f.getDsc()), f.getParamTip()));
					} else 
						p.add(null);
				} catch (JSONException e) {
					throw new IWBException("vcs","JSONException : saveVcsObject", t.getTableId(), f.getDsc(), e.getMessage(), e.getCause());
				}
			}
			s.setLength(s.length()-1);
			s.append(" where ").append(t.get_tableParamList().get(0).getExpressionDsc()).append("=?");
			if(t.get_tableParamList().size()>1)s.append(" AND customization_id=").append(scd.get("customizationId"));
			p.add(tablePk);
			break;
		case	2://insert
			s.append("insert into ").append(t.getDsc()).append("(");
			StringBuilder s2= new StringBuilder();
			for(W5TableField f:t.get_tableFieldList())if(f.getTabOrder()>0){
				if(GenericUtil.hasPartInside2("insert_dttm,version_dttm", f.getDsc())){
					s.append(f.getDsc()).append(",");
					s2.append("current_timestamp,");
				} else {
					s.append(f.getDsc()).append(",");
					s2.append("?,");
					try {
						if(o.has(f.getDsc())){
							p.add(GenericUtil.getObjectByControl((String)o.get(f.getDsc()), f.getParamTip()));
						} else 
							p.add(null);
					} catch (JSONException e) {
						throw new IWBException("vcs","JSONException : saveVcsObject", t.getTableId(), f.getDsc(), e.getMessage(), e.getCause());
					}
				}
				
			}
			s.setLength(s.length()-1);s2.setLength(s2.length()-1);
			s.append(") values (").append(s2).append(")");
			
			break;
		case	3://delete
			s.append("delete from ").append(t.getDsc()).append(" where ").append(t.get_tableParamList().get(0).getExpressionDsc()).append("=?");
			if(t.get_tableParamList().size()>1)s.append(" AND customization_id=").append(scd.get("customizationId"));
			p.add(tablePk);
			break;
		}
		executeUpdateSQLQuery(s.toString(), p);

		return true;
	}
	@Override
	public String getTableRecordSummary(Map scd, int tableId, int tablePk, int maxLength) {
		W5Table t = FrameworkCache.getTable(scd, tableId);
		if(t==null)return "Table not Found ;)";
		if(GenericUtil.isEmpty(t.get_tableParamList()))return "TableParam not Found ;)";
		StringBuilder sql = new StringBuilder();
		sql.append("select (").append(t.getSummaryRecordSql()).append(") qqq from ").append(t.getDsc()).append(" x where x.").append(t.get_tableParamList().get(0).getExpressionDsc()).append("=?");
		if(t.get_tableParamList().size()>1)sql.append(" AND x.customization_id=").append(scd.get("customizationId"));
		Object[] res = DBUtil.filterExt4SQL(sql.toString(), scd, new HashMap(), new HashMap());
		List summaryParams = (List)res[1];summaryParams.add(tablePk);
		List l = executeSQLQuery2(((StringBuilder)res[0]).toString(), summaryParams);
		if(GenericUtil.isEmpty(l))return "(record not found)("+tablePk+")";
		String s = (String)l.get(0);
		if(s==null)return "(record not found)("+tablePk+")";
		return maxLength==0 ? s : (s.length()>maxLength ? s.substring(0, maxLength) : s);
	}
	
	@Override
	public boolean organizeTable(Map<String, Object> scd,  String fullTableName) {
		if(FrameworkSetting.vcs && FrameworkSetting.vcsServer)return false;
		int customizationId = (Integer)scd.get("customizationId");
		int userId = (Integer)scd.get("userId");
		String projectUuid = (String)scd.get("projectId");
		W5Project prj = FrameworkCache.wProjects.get(projectUuid);
		String schema="iwb";
		if(prj.getSetSearchPathFlag()!=0) {
			schema = prj.getRdbmsSchema();
			executeUpdateSQLQuery("set search_path="+schema);
		}
		
		fullTableName = fullTableName.toLowerCase(FrameworkSetting.appLocale);
		String tableName= fullTableName;
		if(tableName.contains(".")){
			schema=tableName.substring(0, tableName.indexOf('.'));
			tableName = tableName.substring(schema.length()+1);
		}
		boolean vcs=FrameworkSetting.vcs;
		
		int cnt = GenericUtil.uInt(executeSQLQuery("select count(1) from information_schema.tables qx where qx.table_name = ? and qx.table_schema = ?", tableName, schema).get(0));
		if(cnt==0)
			throw new IWBException("framework","No Such Table to Define", 0, tableName, "No Such Table to Define", null);

		int tableId = 0;
		List l = executeSQLQuery("select qx.table_id from iwb.w5_table qx where qx.dsc = ? and qx.customization_id = ? AND qx.project_uuid=?", fullTableName, customizationId, projectUuid);
		if(!GenericUtil.isEmpty(l)){
			tableId = GenericUtil.uInt(l.get(0));
		}
		if(tableId==0){
			tableId = GenericUtil.getGlobalNextval("iwb.seq_table", projectUuid, userId, customizationId);
			int rq = executeUpdateSQLQuery("insert into iwb.w5_table"
					+ "(table_id, dsc, insert_user_id, version_user_id, customization_id, project_uuid)values"
					+ "(?       , ?  , ?             , ?              , ?               , ?)",
					tableId, prj.getSetSearchPathFlag()!=0 ? tableName : fullTableName, userId, userId, customizationId, projectUuid);
			if(vcs)saveObject(new W5VcsObject(scd, 15, tableId));


			String firstField = (String)executeSQLQuery("SELECT lower(qz.COLUMN_NAME) from information_schema.columns qz where qz.table_name = ? and qz.table_schema = ? and qz.ordinal_position=1", tableName, schema).get(0);

			int tableParamId = GenericUtil.getGlobalNextval("iwb.seq_table_param", projectUuid, userId, customizationId);
			rq = executeUpdateSQLQuery("insert into iwb.w5_table_param "
					+ "(table_param_id, table_id, dsc, expression_dsc, tab_order, param_tip, operator_tip, not_null_flag, source_tip, insert_user_id, version_user_id, project_uuid, customization_id)values"
					+ "(?             , ?       , ?  , ?             , ?        , ?        , ?           , ?            , ?         , ?             , ?              , ?           , ?)",
					tableParamId, tableId, "t"+firstField, firstField, 1, 4, 0, 1, 1, userId, userId, projectUuid, customizationId);
			if(vcs)saveObject(new W5VcsObject(scd, 42, tableParamId));

			cnt = GenericUtil.uInt(executeSQLQuery("SELECT count(1) from information_schema.columns qz where qz.table_name = ? and qz.table_schema = ? and lower(qz.COLUMN_NAME)='customization_id'", tableName, schema).get(0));

			if(cnt>0){
				tableParamId = GenericUtil.getGlobalNextval("iwb.seq_table_param", projectUuid, userId, customizationId);
				rq = executeUpdateSQLQuery("insert into iwb.w5_table_param "
					+ "(table_param_id, table_id, dsc, expression_dsc, tab_order, param_tip, operator_tip, not_null_flag, source_tip, insert_user_id, version_user_id, project_uuid, customization_id)values"
					+ "(?             , ?       , ?  , ?             , ?        , ?        , ?           , ?            , ?         , ?             , ?              , ?           , ?)",
					tableParamId, tableId,"customizationId", "customization_id",2, 4, 0, 1, 2, userId, userId, projectUuid, customizationId);
				if(vcs)saveObject(new W5VcsObject(scd, 42, tableParamId));
			}
			
		}
		

		List p = new ArrayList();p.add(tableId);p.add(customizationId);p.add(projectUuid);p.add(tableName);p.add(schema);
		l = executeSQLQuery2Map("select x.*"
				+ ", coalesce((select tf.table_field_id from iwb.w5_table_field tf where tf.table_id = ? and lower(tf.dsc) = x.column_name and tf.customization_id= ? AND tf.project_uuid=?),0) as table_field_id"
				+ ", coalesce((SELECT w.system_type_id FROM iwb.sys_postgre_types w where w.dsc = x.DATA_TYPE),0) xlen"
				+ ", coalesce((SELECT w.framework_type from iwb.sys_postgre_types w where w.dsc = x.DATA_TYPE),0) xtyp"
				+ " from information_schema.columns x where x.table_name = ? and x.table_schema = ?", p);
		for(Map m:(List<Map>)l){
			int tfId = GenericUtil.uInt(m.get("table_field_id"));
			int xlen = GenericUtil.uInt(m.get("xlen"));
			int xtyp = GenericUtil.uInt(m.get("xtyp"));
			int tabOrder = GenericUtil.uInt(m.get("ordinal_position"));
			if(tfId==0){
				String fieldName = ((String)m.get("column_name")).toLowerCase(FrameworkSetting.appLocale);
				int tableFieldId = GenericUtil.getGlobalNextval("iwb.seq_table_field", projectUuid, userId, customizationId);
				int rq = executeUpdateSQLQuery("insert into iwb.w5_table_field "
						+ "(table_field_id, table_id, dsc, field_tip, not_null_flag, max_length, tab_order, insert_user_id, version_user_id, customization_id, project_uuid, source_tip, default_value, can_update_flag, can_insert_flag, copy_source_tip, default_control_tip, default_lookup_table_id) values"
						+ "(?             , ?       , ?  , ?        , ?            , ?         , ?        , ?             , ?              , ?               , ?           , ?         , ?            , ?              , ?              , ?              , ?                  , ? )"
						,tableFieldId, tableId, fieldName,
						fieldName.endsWith("_flag") ? 5 : (xtyp == 3 && GenericUtil.uInt(m.get("numeric_scale"))==0 ? 4: xtyp),
						((String)m.get("is_nullable")).equals("YES") ? 0 : 1,
						xlen==0 ? 0 : (xlen==-1 ? GenericUtil.uInt(m.get("character_maximum_length")): xlen),
						tabOrder, userId, userId, customizationId, projectUuid,
						fieldName.equals("customization_id") ? 2 : (tabOrder==1 ? 4:1),
						fieldName.endsWith("_flag") ? "0" : (fieldName.equals("customization_id") ? "customizationId": (tabOrder == 1 ? "nextval('"+(prj.getSetSearchPathFlag()==0 ? schema+".":"")+"seq_"+tableName+"')":null)),
						GenericUtil.hasPartInside2("customization_id,version_no,insert_user_id,insert_dttm,version_user_id,version_dttm", fieldName) || tabOrder==1 ? 0:1,
						GenericUtil.hasPartInside2("version_no,insert_user_id,insert_dttm,version_user_id,version_dttm", fieldName) ? 0:1,
						fieldName.equals("customization_id") ? 2 : (tabOrder==1 ? 4:6),
						GenericUtil.hasPartInside2("insert_user_id,version_user_id", fieldName) ? 10: 0,GenericUtil.hasPartInside2("insert_user_id,version_user_id", fieldName) ? 336: 0);
				if(vcs)saveObject(new W5VcsObject(scd, 16, tableFieldId));
								
			} else {
				int rq = executeUpdateSQLQuery("update iwb.w5_table_field "
						+ " set tab_order       = ?, "
						+ " version_user_id = ?, "
						+ "version_dttm    = LOCALTIMESTAMP, "
						+ " version_no      = version_no+1, "
						+ " not_null_flag =  ?, "
						+ " max_length =  ? "
						+ " where table_field_id =  ? AND customization_id=?  AND project_uuid=?",
						tabOrder, userId, ((String)m.get("is_nullable")).equals("YES") ? 0 : 1, xlen==0 ? 0 : (xlen==-1 ? GenericUtil.uInt(m.get("character_maximum_length")): xlen),
								tfId, customizationId, projectUuid);
				if(vcs)makeDirtyVcsObject(scd, 16, tfId);
			}
		}

		int rq = executeUpdateSQLQuery("update iwb.w5_table_field "
				+ "set tab_order       = -abs(tab_order), "
				+ "version_user_id = ?, "
				+ "version_dttm    = LOCALTIMESTAMP, "
				+ "version_no      = version_no+1 "
				+ "where table_id = ?  AND tab_order > 0 AND customization_id=? AND project_uuid=? "
				+ " AND (lower(dsc) not in (SELECT lower(q.COLUMN_NAME) from information_schema.columns q where q.table_name = ? and q.table_schema = ?))",
				userId, tableId, customizationId, projectUuid, tableName, schema);
		
		return true;
		
	}
	
	@Override
	public void makeDirtyVcsObject(Map<String, Object> scd, int tableId, int tablePk) {
		if(FrameworkSetting.vcsServer)
			throw new IWBException("vcs","makeDirtyVcsObject",tableId,null, "VCS Server not allowed to make Dirt VCS Object", null);
		List l = find("from W5VcsObject t where t.tableId=? AND t.tablePk=? AND t.customizationId=? AND t.projectUuid=?", tableId, tablePk, scd.get("customizationId"), scd.get("projectId"));
		if(!l.isEmpty()){
			W5VcsObject o = (W5VcsObject)l.get(0);
			if(o.getVcsObjectStatusTip()==9){//1, 2, 3, 8 durumunda hicbirsey degismiyor
				o.setVcsObjectStatusTip((short)1);
				updateObject(o);
			}
		} else
			saveObject(new W5VcsObject(scd, tableId, tablePk));
		
	}
	@Override
	public boolean organizeDbFunc(Map<String, Object> scd,  String fullDbFuncName) {
		if(FrameworkSetting.vcsServer)
			throw new IWBException("vcs","organizeDbFunc",0,fullDbFuncName, "VCS Server not allowed to organizeDbFunc", null);

		int customizationId = (Integer)scd.get("customizationId");
		int userId = (Integer)scd.get("userId");
		String projectUuid = (String)scd.get("projectId");
		
		W5Project po = FrameworkCache.wProjects.get(projectUuid);
		String schema=po.getRdbmsSchema();
		fullDbFuncName = fullDbFuncName.toLowerCase(FrameworkSetting.appLocale);
		String dbFuncName= fullDbFuncName;
		if(dbFuncName.contains(".")){
			schema=dbFuncName.substring(0, dbFuncName.indexOf('.'));
			dbFuncName = dbFuncName.substring(schema.length()+1);
		}
		boolean vcs=FrameworkSetting.vcs;
		
		List l = executeSQLQuery("select qx.proname from pg_proc qx where qx.proname= ? and qx.pronamespace=(select q.oid from pg_namespace q where q.nspname=?)", dbFuncName, schema);
		if(GenericUtil.isEmpty(l))
			throw new IWBException("framework","No Such DbFunc to Define", 0, dbFuncName, "No Such DbFunc to Define", null);
		String params = l.get(0).toString();
		

		int dbFuncId = 0;
		l = executeSQLQuery("select qx.db_func_id from iwb.w5_db_func qx where qx.dsc = ? and qx.customization_id = ? AND qx.project_uuid=?", fullDbFuncName, customizationId, projectUuid);
		Map<String, Object> dbFuncParamMap= new HashMap();
		if(!GenericUtil.isEmpty(l)){
			dbFuncId = GenericUtil.uInt(l.get(0));
			List<Object[]> oldParams =executeSQLQuery("select qx.expression_dsc, qx.db_func_param_id from iwb.w5_db_func_param qx where qx.db_func_id = ? and qx.customization_id = ? order by qx.tab_order", dbFuncId, customizationId);
			int tabOrder=1;
			if(!GenericUtil.isEmpty(oldParams))for(Object[] o:oldParams){
				dbFuncParamMap.put((String)o[0], GenericUtil.uInt(o[1]));
			}
		}
		
		if(dbFuncId==0){
			dbFuncId = GenericUtil.getGlobalNextval("iwb.seq_db_func", projectUuid, userId, customizationId);
			int rq = executeUpdateSQLQuery("insert into iwb.w5_db_func"
					+ "(db_func_id, dsc, insert_user_id, version_user_id, project_uuid, customization_id)values"
					+ "(?         , ?  , ?        , ?             , ?              , ?                , ?           , ?)",
					dbFuncId, schema+"."+dbFuncName, userId, userId, projectUuid, customizationId);
			if(vcs)saveObject(new W5VcsObject(scd, 20, dbFuncId));
		}
		params = params.toLowerCase(FrameworkSetting.appLocale).substring(1, params.length()-1);
		String[] arp=params.split(",");
		for(int qi=0;qi<arp.length;qi++){
			int dbFuncParamId = GenericUtil.uInt(dbFuncParamMap.get(arp[qi]));
			if(dbFuncParamId==0){ // boyle bir kayit yok
				dbFuncParamId = GenericUtil.getGlobalNextval("iwb.seq_db_func_param", projectUuid, userId, customizationId);
				executeUpdateSQLQuery("insert into iwb.w5_db_func_param "
						+ "(db_func_param_id, db_func_id, dsc, expression_dsc, param_tip, tab_order, insert_user_id, version_user_id, source_tip, default_value, not_null_flag, out_flag, project_uuid, customization_id )  values "
						+ "( ?              , ?         , ?  , ?             , 1        , ?        , ?             , ?              , ?         , ?            , ?            , 0       , ?           , ? )",
						dbFuncParamId, dbFuncId, 
							arp[qi].equals("puser_role_id") ? "userRoleId" :
								(arp[qi].equals("plocale") ? "locale" : 
									(arp[qi].equals("ptrigger_action") ? "triggerAction" : (
											dbFuncName.startsWith("pcrud_") && qi<3 ? "t"+arp[qi].substring(1) : arp[qi] )))
						, arp[qi], qi+1 , userId, userId,
						arp[qi].equals("puser_role_id")  || arp[qi].equals("locale")|| arp[qi].equals("plocale") ? 2 : 1,
						arp[qi].equals("puser_role_id") ? "userRoleId" :
							(arp[qi].equals("plocale") ? "locale" : 
								(arp[qi].equals("ptrigger_action") ? "triggerAction" : null)),
						arp[qi].equals("puser_role_id") || arp[qi].equals("plocale") || arp[qi].equals("ptrigger_action") || (dbFuncName.startsWith("pcrud_") && qi<3 ) ? 1 : 0, projectUuid, customizationId);
				if(vcs)saveObject(new W5VcsObject(scd, 21, dbFuncParamId));
			} else{ //var boyle bir kayit
				executeUpdateSQLQuery("update iwb.w5_db_func_param set expression_dsc=?, tab_order = ?, version_user_id = ?, version_dttm    = current_timestamp, version_no      = version_no  + 1 "
						+ "where db_func_param_id =  ? AND customization_id = ? ", arp[qi], qi+1, userId, dbFuncParamId, customizationId);
				dbFuncParamMap.remove(arp[qi]);
				makeDirtyVcsObject(scd, 21, dbFuncParamId);
			}
		}
		if(!dbFuncParamMap.isEmpty())for(String k:dbFuncParamMap.keySet()){
			int dbFuncParamId = GenericUtil.uInt(dbFuncParamMap.get(k));
			executeUpdateSQLQuery("update iwb.w5_db_func_param set expression_dsc=?, tab_order = -abs(tab_order), version_user_id = ?, version_dttm    = current_timestamp, version_no      = version_no  + 1 "
					+ "where db_func_param_id =  ? AND customization_id = ? ", k, userId, dbFuncParamId, customizationId);
			makeDirtyVcsObject(scd, 21, dbFuncParamId);
		}
		return true;
	}
	

	
	@Override
	public String getCurrentDate(int customizationId) {
		return (String)executeSQLQuery("select to_char(iwb.fnc_sysdate(?),'"+GenericUtil.dateFormat+"')",customizationId).get(0); 
	}
	
	@Override
	public String getMd5Hash(String s) {
		return (String)executeSQLQuery("select iwb.md5hash(?)",s).get(0); 
	}
	

	@Override
	public Object getSqlFunc(String s) {
		return (Object)executeSQLQuery("select "+s).get(0); 
	}
	
	@Override
	public Map executeSQLQuery2Map4Debug(final Map<String, Object> scd, final W5Table t, final String sql,final List params, final int limit, final int startOffset) {
		try {
    			return (Map)getCurrentSession().doReturningWork(new ReturningWork<Map>() {
    				@Override
    				public Map execute(Connection conn) throws SQLException {
	                	Map m = new HashMap();
	        			PreparedStatement s = null;
	        			ResultSet rs = null;
	        			try {
		        			if(limit>0){
			        			s = conn.prepareStatement("select count(1) v from ("+sql+") q");	        			
			        			if(params!=null && params.size()>0)for(int i=0;i<params.size();i++){
			        				if(params.get(i)==null) s.setObject(i+1, null);
			    					else if(params.get(i) instanceof Date)s.setDate(i+1, new java.sql.Date(((Date)params.get(i)).getTime()));
			    					else s.setObject(i+1, params.get(i));
			        			}
			        			rs = s.executeQuery();
			        			rs.next();
			        			int cnt = GenericUtil.uInt(rs.getObject(1));
			        			rs.close();
			        			s.close();
		        				Map m2 = new HashMap();m2.put("startRow", startOffset);m2.put("fetchCount", limit);m2.put("totalCount", cnt);
		        				m.put("browseInfo", m2);
			        			s = startOffset>1 ? conn.prepareStatement("select q.* from  ("+sql+") q limit "+(limit+1)+") offset "+startOffset) : conn.prepareStatement("select * from ("+sql+") q limit "+(limit+1)) ;	        			
		        			} else 
		        				s = conn.prepareStatement(sql);
		        			
		        			if(params!=null && params.size()>0)for(int i=0;i<params.size();i++){
		        				if(params.get(i)==null) s.setObject(i+1, null);
		    					else if(params.get(i) instanceof Date)s.setDate(i+1, new java.sql.Date(((Date)params.get(i)).getTime()));
		    					else s.setObject(i+1, params.get(i));
		        			}
		        			
		        			
		        			long startTm = System.currentTimeMillis();
		        			rs = s.executeQuery();
		        			m.put("execTime", System.currentTimeMillis()-startTm);
		        			startTm = System.currentTimeMillis();
	//	        			int columnCount = rs.getMetaData().getColumnCount();
		        			List l = new ArrayList();
		        			Map<String, Object>	result = null;
	    					ResultSetMetaData rsm = rs.getMetaData();
	    					int columnCount = rsm.getColumnCount();
	    					String[] columnNames= new String[columnCount];
	    					List fields = new ArrayList();
	    					
	    					Map<String, Map> map4records = new HashMap<String, Map>();
	    					for(int columnIndex=1;columnIndex<=columnCount;columnIndex++){
	    						String columnName = rsm.getColumnName(columnIndex).toLowerCase(FrameworkSetting.appLocale);
	    						columnNames[columnIndex-1] = columnName;
	    						Map m2 = new HashMap(); m2.put("name", columnName);
	    						switch(rsm.getColumnType(columnIndex)){
	    						case	java.sql.Types.BIGINT:case	java.sql.Types.INTEGER:case	java.sql.Types.SMALLINT:
	    							m2.put("type", "int");break;
	    						case	java.sql.Types.NUMERIC:
	    							m2.put("type", rsm.getScale(columnIndex)==0 ? "int":"float");break;
	    						case	java.sql.Types.DATE:case	java.sql.Types.TIMESTAMP:
	    							m2.put("type", "date");break;
	    						}
	    						if(t!=null)for(W5TableField f:t.get_tableFieldList())if(f.getDsc().equals(columnName)){
	    							if(f.getDefaultLookupTableId()!=0)switch(f.getDefaultControlTip()){
	    							case	6: case 8:
	    								W5LookUp lu = FrameworkCache.getLookUp(scd, f.getDefaultLookupTableId());
	    								if(lu!=null && !GenericUtil.isEmpty(lu.get_detayList())){
	    									Map m3 = new HashMap();
	    									for(W5LookUpDetay d:lu.get_detayList()){
	    										m3.put(d.getVal(), LocaleMsgCache.get2(scd, d.getDsc()));
	    									}
	    									m2.put(f.getDefaultControlTip() == 6 ? "map":"map2", m3);
	    								}
	    								break;
	    							case	7:case	10:case 15:
	    								W5Table tt = FrameworkCache.getTable(scd, f.getDefaultLookupTableId());
	    								if(tt!=null && !GenericUtil.isEmpty(tt.getSummaryRecordSql())){
		    								Map mz = new HashMap();
		    								mz.put("set", new HashSet());mz.put("t", tt);mz.put("field", m2);
		    								map4records.put(columnName, mz);
	    								}
	    							}
	    							
	    							break;
	    						}
	    						fields.add(m2);
	    					}
	    					m.put("fields", fields);
		    					
		        			while(rs.next()){
		        				result = new HashMap<String, Object>();
		    					for(int columnIndex=1;columnIndex<=columnCount;columnIndex++){
		    						String columnName = columnNames[columnIndex-1];
		    						Object obj =rs.getObject(columnIndex);
		    						if(obj==null)continue;
		    						if (obj instanceof java.sql.Timestamp) {
		    	        				try{ result.put(columnName, GenericUtil.uFormatDateTimeSade((java.sql.Timestamp) obj));
		    	        				}catch (Exception e) {}
		    						} else if (obj instanceof java.sql.Date) {
		    	        				try{ result.put(columnName, GenericUtil.uFormatDateSade((java.sql.Date) obj));
		    	        				}catch (Exception e) {}
		    						} else {
		    							String ss = obj.toString();
		    							if(map4records.containsKey(columnName)){
		    								((Set)(map4records.get(columnName).get("set"))).add(ss);
		    							}
		    	        				 result.put(columnName, ss);
		    	        			 }
		    					}
	        					l.add(result);
		        			}
		        			m.put("data", l);
		        			m.put("sql", GenericUtil.replaceSql(sql,params));
		        			m.put("fetchTime", System.currentTimeMillis()-startTm);
		        			
		        			for(String kk:map4records.keySet())try{
		        				Map mm = map4records.get(kk);
		        				Set set = (Set)mm.get("set"); 
		        				if(!set.isEmpty()){
		        					String ss = "";
		        					for(Object sk:set.toArray()){
		        						ss+=","+sk;	        						
		        					}
		        					ss = ss.substring(1);
		        					W5Table t = (W5Table)mm.get("t");
		        					String sql = "select x."+t.get_tableFieldList().get(0).getDsc()+" id, "+t.getSummaryRecordSql()+" dsc from " +t.getDsc()+" x where "+t.get_tableParamList().get(0).getExpressionDsc()+" in ("+ss+")";
		        					if(t.get_tableParamList().size()>1)sql+=" AND x.customization_id="+scd.get("customizationId");
		        					Object[] oz = DBUtil.filterExt4SQL(sql, scd, new HashMap(), null);
		        					List<Object[]> lm = executeSQLQuery2(oz[0].toString(), (List)oz[1]);
		        					if(lm!=null){
		        						Map m3 = new HashMap();
		        						for(Object[] oo:lm){
		        							m3.put(oo[0], oo[1]);
		        						}
			        					((Map)(mm.get("field"))).put("map", m3);
		        					}
		        				}
		        				
		        			}catch(Exception ee){}
	        			} catch(SQLException se){
	        				if(FrameworkSetting.debug)se.printStackTrace();
	        				throw new IWBException("sql","Manual Query executeSQLQuery2Map4Debug",0,GenericUtil.replaceSql(sql,params), se.getMessage(), se.getCause());
	        			} finally {
		        			if(rs!=null)rs.close();
		        			if(s!=null)s.close();
		        			if(FrameworkSetting.hibernateCloseAfterWork)if(conn!=null)conn.close();
	        			}
	        			return m;
	                }
	            }
	    	);
		} catch(IWBException e){
			throw e;
		} catch(Exception e){
			if(FrameworkSetting.debug)e.printStackTrace();
			throw new IWBException("sql","Manual Query executeSQLQuery2Map4Debug",0,GenericUtil.replaceSql(sql,params), e.getMessage(), e.getCause());
		}
	}


	@Override
	public W5DbFuncResult executeDbFunc4Debug(Map<String, Object> scd,
			int dbFuncId, Map<String, String> parameterMap) {
		W5DbFuncResult r = dbFuncId==-1 ? new W5DbFuncResult(-1) : getDbFuncResult(scd, dbFuncId);
		r.setScd(scd);r.setErrorMap(new HashMap());r.setRequestParams(parameterMap);
		Context cx = Context.enter();
		String script = null;
		try {
			cx.setOptimizationLevel(-1);
			// Initialize the standard objects (Object, Function, etc.)
			// This must be done before scripts can be executed. Returns
			// a scope object that we use in later calls.
			Scriptable scope = cx.initStandardObjects();

			script = parameterMap.get("_rhino_script_code");
			if(script.charAt(0)=='!')script = script.substring(1);
			// Collect the arguments into a single string.
			ScriptEngine se = new ScriptEngine(r.getScd(), r.getRequestParams(), this, engine);
			Object wrappedOut = Context.javaToJS( se, scope);
			ScriptableObject.putProperty(scope, "$iwb", wrappedOut);

			
			StringBuilder sc = new StringBuilder();
			boolean hasOutParam = false;
			if(dbFuncId!=-1 && r.getDbFunc().get_dbFuncParamList().size()>0){
				sc.append("var ");
	    		for(W5DbFuncParam p1 : r.getDbFunc().get_dbFuncParamList())if (p1.getOutFlag()==0){
	    			if(sc.length()>4)sc.append(", ");
	            	sc.append(p1.getDsc()).append("=");
	            	String s = parameterMap.get(p1.getDsc());
	            	Object o = GenericUtil.isEmpty(s) ? null : GenericUtil.getObjectByTip(s, p1.getParamTip());
	            	if(o==null){
	            		if(p1.getNotNullFlag()!=0)r.getErrorMap().put(p1.getDsc(), LocaleMsgCache.get2(scd, "validation_error_not_null"));
	            		sc.append("null");
	            	}else if ((o instanceof Integer) || (o instanceof Double) || (o instanceof BigDecimal)|| (o instanceof Boolean))
		            	sc.append(o);
	            	else if ((o instanceof Date))
	            		sc.append("'").append(GenericUtil.uFormatDate((Date)o)).append("'");
	            	else
	            		sc.append("'").append(o).append("'");
	    		} else hasOutParam = true;
    			if(sc.length()>4)sc.append(";\n");else sc.setLength(0);
	    	}
			if(!r.getErrorMap().isEmpty()){
	    		r.setSuccess(false);
	    		return r;
			}
			sc.append("\nvar _scd=").append(GenericUtil.fromMapToJsonString2(r.getScd()))
				.append(";\nvar _request=").append(GenericUtil.fromMapToJsonString2(r.getRequestParams())).append(";\n").append(script);
			
			script = sc.toString();

			if(FrameworkSetting.debug)se.console("start: " + (r.getDbFunc()!=null ? r.getDbFunc().getDsc(): "new"),"DEBUG","info");
			long startTm = System.currentTimeMillis(); 
			cx.evaluateString(scope, script, null, 1, null);
			r.setProcessTime((int)(System.currentTimeMillis() - startTm));
//			if(FrameworkSetting.debug)se.console("end: " + (r.getDbFunc()!=null ? r.getDbFunc().getDsc(): "new"),"DEBUG","success");
			/*
			if(scope.has("errorMsg", scope)){
				Object em = GenericUtil.rhinoValue(scope.get("errorMsg", scope));
				if(em!=null)throw new PromisException("rhino","DbFuncId", r.getDbFuncId(), script, LocaleMsgCache.get2(0,(String)r.getScd().get("locale"),em.toString()), null);					
			} */
			if(hasOutParam){
				//JSONObject jo=new JSONObject();
				Map<String,String> res=new HashMap<String, String>();
		    	r.setResultMap(res);
				for(W5DbFuncParam p1 : r.getDbFunc().get_dbFuncParamList())if (p1.getOutFlag()!=0 && scope.has(p1.getDsc(), scope)){
					Object em = GenericUtil.rhinoValue(scope.get(p1.getDsc(), scope));
					if(em!=null)res.put(p1.getDsc(), em.toString());
	    		}
			}
			if(scope.has("outMsgs", scope)){//TODO
				Object em = scope.get("outMsgs", scope);
			}
		} catch(IWBException e){
			throw e;
		} catch(Exception e){
			if(FrameworkSetting.debug)e.printStackTrace();
			if(e.getCause()!=null && e.getCause() instanceof IWBException){
				IWBException pe = (IWBException) e.getCause();
				throw new IWBException("rhino","DbFuncId", r.getDbFuncId(), pe.getSql(), "Inside of Rhino("+pe.getErrorType() + ", " +  pe.getObjectType() + ", " +  pe.getObjectId()+"): "+pe.getMessage(), pe.getCause());
			}
			throw new IWBException("rhino","DbFuncId", r.getDbFuncId(), script, LocaleMsgCache.get2(0,(String)r.getScd().get("locale"),e.getMessage()), e.getCause());
		} finally {
             // Exit from the context.
	             cx.exit();
        }
		r.setSuccess(true);
		return r;
	}

	@Override
	public void executeQueryAsRhino(W5QueryResult qr, String code) {
		Context cx = Context.enter();
		W5Query q = qr.getQuery();
		String script = GenericUtil.uStrNvl(code, q.getSqlFrom());
		try {
			cx.setOptimizationLevel(-1);
			// Initialize the standard objects (Object, Function, etc.)
			// This must be done before scripts can be executed. Returns
			// a scope object that we use in later calls.
			Scriptable scope = cx.initStandardObjects();

			if(script.charAt(0)=='!')script = script.substring(1);
			// Collect the arguments into a single string.
			ScriptEngine se = new ScriptEngine(qr.getScd(), qr.getRequestParams(), this, engine);
			Object wrappedOut = Context.javaToJS( se, scope);
			ScriptableObject.putProperty(scope, "$iwb", wrappedOut);

			
			StringBuilder sc = new StringBuilder();
			boolean hasOutParam = false;
			if(q.get_queryParams().size()>0){
				sc.append("var ");
	    		for(W5QueryParam p1 : q.get_queryParams()){
	    			if(sc.length()>4)sc.append(", ");
	            	sc.append(p1.getDsc()).append("=");
	            	String s = qr.getRequestParams().get(p1.getDsc());
	            	Object o = GenericUtil.isEmpty(s) ? null : GenericUtil.getObjectByTip(s, p1.getParamTip());
	            	if(o==null){
	            		if(p1.getNotNullFlag()!=0)qr.getErrorMap().put(p1.getDsc(), LocaleMsgCache.get2(qr.getScd(), "validation_error_not_null"));
	            		sc.append("null");
	            	}else if ((o instanceof Integer) || (o instanceof Double) || (o instanceof BigDecimal)|| (o instanceof Boolean))
		            	sc.append(o);
	            	else if ((o instanceof Date))
	            		sc.append("'").append(GenericUtil.uFormatDateTime((Date)o)).append("'");
	            	else
	            		sc.append("'").append(o).append("'");
	    		}
    			if(sc.length()>4)sc.append(";\n");else sc.setLength(0);
	    	}
			if(!qr.getErrorMap().isEmpty()){
	    		return;
			}
			sc.append("\nvar _scd=").append(GenericUtil.fromMapToJsonString2(qr.getScd()))
				.append(";\nvar _request=").append(GenericUtil.fromMapToJsonString2(qr.getRequestParams())).append(";\n").append(script);
			
			script = sc.toString();

			cx.evaluateString(scope, script, null, 1, null);
			if(scope.has("result", scope)){
				Object r = scope.get("result", scope);
				if(r!=null){
					if(r instanceof NativeArray){
						NativeArray ar = (NativeArray)r;
						int	maxTabOrder = 0;
						qr.setNewQueryFields(new ArrayList());
						for(W5QueryField qf:q.get_queryFields()){
							if(qf.getTabOrder()>maxTabOrder)maxTabOrder = qf.getTabOrder();
							qr.getNewQueryFields().add(qf);
						}
						for(W5QueryField qf:q.get_queryFields())if((qf.getPostProcessTip()==16 || qf.getPostProcessTip()==17) && qf.getLookupQueryId()!=0){
							W5QueryResult queryFieldLookupQueryResult = getQueryResult(qr.getScd(), qf.getLookupQueryId());
							if(queryFieldLookupQueryResult!=null && queryFieldLookupQueryResult.getQuery()!=null){
				   				W5QueryField field = new W5QueryField();
				   				field.setDsc(qf.getDsc()+"_qw_");
				   				field.setMainTableFieldId(qf.getMainTableFieldId());
				   				maxTabOrder++;
				   				field.setTabOrder((short)maxTabOrder);
				   				qr.getNewQueryFields().add(field);
				   				if(qf.getPostProcessTip()==16 && queryFieldLookupQueryResult.getQuery().get_queryFields().size()>2)for(int qi=2;qi<queryFieldLookupQueryResult.getQuery().get_queryFields().size();qi++){
				   					W5QueryField qf2 = queryFieldLookupQueryResult.getQuery().get_queryFields().get(qi);
					   				field = new W5QueryField();
					   				field.setDsc(qf.getDsc()+"__"+qf2.getDsc());
					   				field.setMainTableFieldId(qf2.getMainTableFieldId());
					   				maxTabOrder++;
					   				field.setTabOrder((short)maxTabOrder);
					   				qr.getNewQueryFields().add(field);
				   				}
							}
						}
						qr.setData(new ArrayList((int)ar.getLength()));
						for(int qi=0;qi<ar.getLength();qi++){
							NativeObject no = (NativeObject)(ar.get(qi, scope));
							Object[] o = new Object[maxTabOrder];
							qr.getData().add(o);
							for(W5QueryField qf:q.get_queryFields())if(no.has(qf.getDsc(),  scope)){
								Object o2 = no.get(qf.getDsc(),  scope);
								if(o2!=null){
									if(o2 instanceof NativeJavaObject){
						    			o2 = ((NativeJavaObject)o2).unwrap();			
						    		}
									switch(qf.getFieldTip()){
									case	4://integer
										o[qf.getTabOrder()-1] = GenericUtil.uInt(GenericUtil.rhinoValue(o2));								
										break;
									case	8://json
										if(o2 instanceof NativeArray){
											NativeArray no2=(NativeArray)o2;
											o[qf.getTabOrder()-1] = GenericUtil.fromNativeArrayToJsonString2Recursive(no2);								
										} else if(o2 instanceof NativeObject){
											NativeObject no2=(NativeObject)o2;
											o[qf.getTabOrder()-1] = GenericUtil.fromNativeObjectToJsonString2Recursive(no2);
										} else if(o2 instanceof Map)
											o[qf.getTabOrder()-1] = GenericUtil.fromMapToJsonString2Recursive((Map)o2);
										else if(o2 instanceof List)
											o[qf.getTabOrder()-1] = GenericUtil.fromListToJsonString2Recursive((List)o2);
										else 
											o[qf.getTabOrder()-1] = "'"+GenericUtil.stringToJS(o2.toString())+"'";								
										break;
									default:
										o[qf.getTabOrder()-1] = o2;								
									}
								}
							}
						}
						qr.setFetchRowCount((int)ar.getLength());
						if(scope.has("extraOutMap", scope)){
							Map extraOutMap = new HashMap();
							extraOutMap.put("rhino", scope.get("extraOutMap", scope));
							qr.setExtraOutMap(extraOutMap);
						}
						
					}
				}
			} else return;
			
		} catch(Exception e){
			throw new IWBException("rhino", "Query", q.getQueryId(), script, "[8,"+q.getQueryId()+"]", e);
		} finally {
             // Exit from the context.
	             cx.exit();
        }
	}
	
	@Override
	public Map executeQueryAsRhino4Debug(W5QueryResult qr, String script) {
		Context cx = Context.enter();
		W5Query q = qr.getQuery();
		Map m = new HashMap();
		m.put("success", true);
//		String script = q.getSqlFrom();
		try {
			cx.setOptimizationLevel(-1);
			// Initialize the standard objects (Object, Function, etc.)
			// This must be done before scripts can be executed. Returns
			// a scope object that we use in later calls.
			Scriptable scope = cx.initStandardObjects();

			if(script.charAt(0)=='!')script = script.substring(1);
			// Collect the arguments into a single string.
			ScriptEngine se = new ScriptEngine(qr.getScd(), qr.getRequestParams(), this, engine);
			Object wrappedOut = Context.javaToJS( se, scope);
			ScriptableObject.putProperty(scope, "$iwb", wrappedOut);

			
			StringBuilder sc = new StringBuilder();
			boolean hasOutParam = false;
			if(q.get_queryParams().size()>0){
				sc.append("var ");
	    		for(W5QueryParam p1 : q.get_queryParams()){
	    			if(sc.length()>4)sc.append(", ");
	            	sc.append(p1.getDsc()).append("=");
	            	String s = qr.getRequestParams().get(p1.getDsc());
	            	Object o = GenericUtil.isEmpty(s) ? null : GenericUtil.getObjectByTip(s, p1.getParamTip());
	            	if(o==null){
	            		if(p1.getNotNullFlag()!=0)qr.getErrorMap().put(p1.getDsc(), LocaleMsgCache.get2(qr.getScd(), "validation_error_not_null"));
	            		sc.append("null");
	            	}else if ((o instanceof Integer) || (o instanceof Double) || (o instanceof BigDecimal)|| (o instanceof Boolean))
		            	sc.append(o);
	            	else if ((o instanceof Date))
	            		sc.append("'").append(GenericUtil.uFormatDate((Date)o)).append("'");
	            	else
	            		sc.append("'").append(o).append("'");
	    		}
    			if(sc.length()>4)sc.append(";\n");else sc.setLength(0);
	    	}
			if(!qr.getErrorMap().isEmpty()){
				throw new IWBException("rhino","QueryId", q.getQueryId(), script, "Validation ERROR: " + GenericUtil.fromMapToJsonString2(qr.getErrorMap()), null);
			}
			sc.append("\nvar _scd=").append(GenericUtil.fromMapToJsonString2(qr.getScd()))
				.append(";\nvar _request=").append(GenericUtil.fromMapToJsonString2(qr.getRequestParams())).append(";\n").append(script);
			
			script = sc.toString();

			se.console("Start: " + (q.getDsc()!=null ? q.getDsc(): "new"),"iWB-QUERY-DEBUG","warn");
			long startTm = System.currentTimeMillis();
			cx.evaluateString(scope, script, null, 1, null);
			m.put("execTime", System.currentTimeMillis()-startTm);
			se.console("End: " + (q.getDsc()!=null ? q.getDsc(): "new"),"iWB-QUERY-DEBUG","warn");
			startTm = System.currentTimeMillis();
			List data = null;
			if(scope.has("result", scope)){
				Object r = scope.get("result", scope);
				if(r!=null){
					if(r instanceof NativeArray){
						NativeArray ar = (NativeArray)r;
						int	maxTabOrder = 0;
						for(W5QueryField qf:q.get_queryFields()){
							if(qf.getTabOrder()>maxTabOrder)maxTabOrder = qf.getTabOrder();
						}
						qr.setNewQueryFields(q.get_queryFields());
						data = new ArrayList((int)ar.getLength());
						for(int qi=0;qi<ar.getLength();qi++){
							NativeObject no = (NativeObject)(ar.get(qi, scope));
							Object[] o = new Object[maxTabOrder];
							Map d = new HashMap();
							data.add(d);
							for(W5QueryField qf:q.get_queryFields())if(no.has(qf.getDsc(),  scope)){
								//o[qf.getTabOrder()-1] = no.get(qf.getDsc(),  scope);
								d.put(qf.getDsc(), GenericUtil.rhinoValue(no.get(qf.getDsc(),  scope)));
							}
						}
						m.put("fetchTime", System.currentTimeMillis()-startTm);
						Map m2 = new HashMap();m2.put("startRow", 0);m2.put("fetchCount", (int)ar.getLength());m2.put("totalCount", (int)ar.getLength());
        				m.put("browseInfo", m2);
					}
				}
			}
			if(data==null)
				throw new IWBException("rhino","QueryId", q.getQueryId(), script, "[result] object not found", null);
			m.put("data", data);
			List fields = new ArrayList();
			for(W5QueryField qf:q.get_queryFields()){
				Map d= new HashMap();
				d.put("name", qf.getDsc());
				switch(qf.getFieldTip()){
				case	3:d.put("type", "int");break;
				case	4:d.put("type", "float");break;
				case	2:d.put("type", "date");break;
				}
				fields.add(d);
			}
			m.put("fields", fields);
		} catch(IWBException e){
			throw e;
		} catch(Exception e){
			if(e.getCause()!=null && e.getCause() instanceof IWBException){
				IWBException pe = (IWBException) e.getCause();
				throw new IWBException("rhino","QueryId", q.getQueryId(), pe.getSql(), "Inside of Rhino("+pe.getErrorType() + ", " +  pe.getObjectType() + ", " +  pe.getObjectId()+"): "+pe.getMessage(), pe.getCause());
			}


			if(FrameworkSetting.debug)e.printStackTrace();
			throw new IWBException("rhino","QueryId", q.getQueryId(), script, LocaleMsgCache.get2(0,(String)qr.getScd().get("locale"),e.getMessage()), e.getCause());
		} finally {
             // Exit from the context.
	             cx.exit();
        }
		return m;
	}
	public Map executeQuery4StatWS(W5QueryResult queryResult) {
	
		
		W5WsMethod wsm = FrameworkCache.wWsMethods.get(queryResult.getQuery().getMainTableId());
		Map<String, Object> scd = queryResult.getScd();
		if(wsm.get_params()==null){
			wsm.set_params(find("from W5WsMethodParam t where t.wsMethodId=? AND t.customizationId=? order by t.tabOrder", wsm.getWsMethodId(), (Integer)scd.get("customizationId")));
			wsm.set_paramMap(new HashMap());
			for(W5WsMethodParam wsmp:wsm.get_params())
				wsm.get_paramMap().put(wsmp.getWsMethodParamId(), wsmp);
		}
		W5WsMethodParam parentParam = null;
		for(W5WsMethodParam px:wsm.get_params())if(px.getOutFlag()!=0 && px.getParamTip()==10){parentParam=px;break;}
		Map<String, String> m2 = new HashMap();
		Map<String, String> requestParams = queryResult.getRequestParams();
		
		int statType = GenericUtil.uInt(requestParams, "_stat");//0:count, 1:sum, 2.avg
		String funcFields = requestParams.get("_ffids");//statFunctionFields
		if(statType>0 && GenericUtil.isEmpty(funcFields))
			throw new IWBException("framework","Query", queryResult.getQueryId(), null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_grid_stat_func_fields"), null);
			
		int queryFieldId = GenericUtil.uInt(requestParams, "_qfid");
		int stackFieldId = GenericUtil.uInt(requestParams, "_sfid");
		if(stackFieldId>0 && stackFieldId==queryFieldId)stackFieldId=0;

		
		for(W5QueryParam qp:queryResult.getQuery().get_queryParams())if(!GenericUtil.isEmpty(requestParams.get(qp.getDsc()))){
			m2.put(qp.getExpressionDsc(), requestParams.get(qp.getDsc()));
		}
		StringBuilder rc = new StringBuilder();
		rc.append("function _x_(x){\nreturn {").append(queryResult.getQuery().getSqlSelect()).append("\n}}\nvar result=[], q=$iwb.callWs('")
		  .append(wsm.get_ws().getDsc()+"."+wsm.getDsc()).append("',")
		  .append(GenericUtil.fromMapToJsonString2(m2)).append(");\nif(q && q.get('success')){q=q.get('").append(parentParam.getDsc()).append("');for(var i=0;i<q.size();i++)result.push(_x_(q.get(i)));}");
		executeQueryAsRhino(queryResult, rc.toString());
		Map result = new HashMap();result.put("success", true);
		if(queryResult.getErrorMap().isEmpty()){
			List<Map> data = new ArrayList();
			Map<String, Map> mdata = new HashMap();
			W5QueryField statQF = null, funcQF = null;
			W5LookUp statLU = null;
			for(W5QueryField qf:queryResult.getQuery().get_queryFields())if(qf.getQueryFieldId()==queryFieldId){
				statQF = qf;
				if(qf.getPostProcessTip()==10)statLU= FrameworkCache.getLookUp(scd, qf.getLookupQueryId());
				break;
			}
			if(statType>0){
				int funcFieldId = GenericUtil.uInt(funcFields.split(",")[0]);
				for(W5QueryField qf:queryResult.getQuery().get_queryFields())if(qf.getQueryFieldId()==funcFieldId){
					funcQF = qf;
					break;
				}
			}
			for(Object[] o:queryResult.getData()){
				Object okey = o[statQF.getTabOrder()-1];
				String key = "_";
				if(okey!=null)key=okey.toString();
				Map mr = mdata.get(key);
				if(mr==null){
					mr = new HashMap();
					mr.put("id", key);
					if(statLU!=null){
						W5LookUpDetay ld = statLU.get_detayMap().get(key.toString());
						mr.put("dsc", LocaleMsgCache.get2(scd, ld.getDsc()));
					} else  mr.put("dsc", key);
					mr.put("xres", BigDecimal.ZERO);
					mr.put("xcnt", 0);
					mdata.put(key,mr);
					data.add(mr);
				}
				switch(statType){
				case	0://count
				case	2://avg
					int cnt = (Integer)mr.get("xcnt");
					cnt++;
					mr.put("xcnt", cnt);
					if(statType==0)break;
				case	1://sum
					BigDecimal res = (BigDecimal)mr.get("xres");
					res = res.add(GenericUtil.uBigDecimal2(o[funcQF.getTabOrder()-1]));
					mr.put("xres", res);
					break;
				}
			}
			for(Map mr:data)switch(statType){
			case	0://count
				mr.put("xres", mr.get("xcnt"));
				break;
			case	2://avg
				int cnt = (Integer) mr.get("xcnt");
				if(cnt>0)mr.put("xres", ((BigDecimal)mr.get("xres")).divide(new BigDecimal(cnt)));
				break;
			}
			
			result.put("data", data);
			
		}
		
		return result;
	}
	
	
	public Map executeQuery4Stat(Map<String, Object> scd, int gridId, Map<String, String> requestParams) {
		requestParams.remove("firstLimit");requestParams.remove("limit");requestParams.remove("start"); requestParams.remove("sort");
		int customizationId = (Integer)scd.get("customizationId");
		int queryId = gridId>0 ? GenericUtil.uInt(executeSQLQuery("select query_id from iwb.w5_grid g where g.grid_id=? AND g.customization_id=?", gridId, customizationId).get(0)) : -gridId;
		W5QueryResult queryResult = getQueryResult(scd,queryId);
		queryResult.setErrorMap(new HashMap());
		queryResult.setRequestParams(requestParams);
		if(queryResult.getQuery().getQuerySourceTip()==1376)return executeQuery4StatWS(queryResult);
		
		if(queryId!=1 && queryId!=824 && queryResult.getMainTable()!=null && (!FrameworkSetting.debug || (scd.get("roleId")!=null && GenericUtil.uInt(scd.get("roleId"))!=0))){
			W5Table t = queryResult.getMainTable();

			if(t.getAccessViewTip()==0 && !FrameworkCache.roleAccessControl(scd,  0)){
				throw new IWBException("security","Module", 0, null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_guvenlik_modul_kontrol"), null);
			}
			if(t.getAccessViewUserFields()==null && !GenericUtil.accessControl(scd, t.getAccessViewTip(), t.getAccessViewRoles(), t.getAccessViewUsers())){
				throw new IWBException("security","Query", queryId, null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_guvenlik_tablo_kontrol_goruntuleme"), null);
			}
			


		}
/*		StringBuilder tmpx = new StringBuilder("ali baba ${obj.dsc} ve 40 haramiler ${lnk.pk_query_field_id.dsc} olmus");
		interprateTemplate(scd, 5,1294, tmpx, true); */

		
		int statType = GenericUtil.uInt(requestParams, "_stat");//0:count, 1:sum, 2.avg
		String funcFields = requestParams.get("_ffids");//statFunctionFields
		if(statType>0 && GenericUtil.isEmpty(funcFields))
			throw new IWBException("framework","Query", queryId, null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_grid_stat_func_fields"), null);
			
		int queryFieldId = GenericUtil.uInt(requestParams, "_qfid");
		int stackFieldId = GenericUtil.uInt(requestParams, "_sfid");
		if(stackFieldId>0 && stackFieldId==queryFieldId)stackFieldId=0;
		
		W5Query query = queryResult.getQuery();
		W5QueryField qf=null, sf=null;
		for(W5QueryField o:query.get_queryFields())if(o.getQueryFieldId()==queryFieldId){
    		qf=o;
    		break;
    	}
		if(qf==null)
			throw new IWBException("framework","Query", queryId, null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_grid_stat_error"), null);
		if(stackFieldId>0){
			for(W5QueryField o:query.get_queryFields())if(o.getQueryFieldId()==stackFieldId){
	    		sf=o;
	    		break;
	    	}
			if(sf==null)
				throw new IWBException("framework","Query", queryId, null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_grid_stat_error2"), null);
		}
		
		int sortTip = GenericUtil.uInt(requestParams, "_sort");
		String orderBy=null;
		if(sortTip>0 && sortTip<3){
			orderBy=new String[]{"xres","id"}[sortTip-1];
			if(GenericUtil.uInt(requestParams, "_dir")>0)orderBy+=" desc";
		}

//		queryResult.setOrderBy(PromisUtil.uStrNvl(requestParams.get(PromisUtil.uStrNvl(PromisSetting.appSettings.get("sql_sort"),"sort")), queryResult.getQuery().getSqlOrderby()));
//		queryResult.setOrderBy(queryResult.getQuery().getSqlOrderby());
		String queryFieldSQL = qf.getDsc(); 
		if(qf.getFieldTip()==2){//date ise
			queryFieldSQL = "to_char("+queryFieldSQL+", '"+(new String[]{"yyyy","yyyy/Q","yyyy/mm","yyyy/WW","yyyy/mm/dd"}[GenericUtil.uInt(requestParams, "_dtt")])+"')";
		}

		switch(queryResult.getQuery().getQueryTip()){
		case	9:case	10:case	15: //TODO: aslinda hata. olmamasi lazim
			throw new IWBException("framework","Query", queryId, null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_grid_stat_query_error"), null);
		default:
	    	queryResult.prepareQuery(null);
	    	if(!queryResult.getErrorMap().isEmpty())
				throw new IWBException("framework","Query", queryId, null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_grid_stat_query_prepare_error"), null);

	    	String sql = "select " + queryFieldSQL + " id, ";
	    	if(sf!=null)sql+=sf.getDsc() + " stack_id, ";
	    	if(statType!=0){
	    		String[] fq=funcFields.split(",");
	    		Set<Integer> fqs=new HashSet();
	    		for(String s:fq){
	    			fqs.add(GenericUtil.uInt(s));
	    		}

	    		int count=0;
				String[] stats=new String[]{"","sum","avg","max","min"};
	        	for(W5QueryField o:queryResult.getQuery().get_queryFields())if(fqs.contains(o.getQueryFieldId())){
	        		count++;
	        		if(count>1)
	        			sql += ","+stats[statType]+"("+o.getDsc()+") xres"+count;
	        		else
	        			sql += stats[statType]+"("+o.getDsc()+") xres";
	        	}
	        	if(count==0)throw new IWBException("framework","Query", queryId, null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_grid_stat_match_func_fields_error"), null);
	    	} else {
	    		sql += "count(1) xres";
	    	}
	    	
	    	sql+=" from (" + queryResult.getExecutedSql() + ") mq group by id";
	    	if(sf!=null)sql+=", stack_id";
	    	if(queryFieldSQL.startsWith("to_char("))sql+=" order by id";
	    	else if(!GenericUtil.isEmpty(orderBy))sql+=" order by " + orderBy;
	    	queryResult.setExecutedSql(sql);
		}
		Map result = new HashMap();result.put("success", true);
		List<Map> l = executeSQLQuery2Map(queryResult.getExecutedSql(), queryResult.getSqlParams());
		if(l!=null){
			if(sf!=null){
				Set<String> stackSet = new HashSet();
				List<Map> nl = new ArrayList();
				Map<Object, Map> nom = new HashMap();
				for(Map m : l){
					Object oid = m.get("id");
					Map nm = nom.get(oid);
					if(nm==null){
						nm = new HashMap();
						nm.put("id", oid);
						nom.put(oid, nm);
						nl.add(nm);
					}
					for(Object k:m.keySet())if(!k.equals("id") && !k.equals("stack_id")){
						if(!GenericUtil.isEmpty(m.get("stack_id"))){
							nm.put(k+"_"+m.get("stack_id"), m.get(k));
							stackSet.add(m.get("stack_id").toString());
						}
					}
				}
				l = nl;
				Map lm = new HashMap();
				switch(sf.getPostProcessTip()){
				case	10://static;
					W5LookUp ld3 = FrameworkCache.getLookUp(customizationId, sf.getLookupQueryId());
					if(ld3!=null)for(Object k:stackSet)if(ld3.get_detayMap().get(k)!=null)lm.put(k, GenericUtil.uStrMax(LocaleMsgCache.get2(scd, ld3.get_detayMap().get(k).getDsc()),20));
					else throw new IWBException("framework","QueryField", sf.getQueryFieldId(), null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_grid_stat_stacked_error"), null);
					break;
				case	12://table
					for(Object k:stackSet){
						String s = getTableRecordSummary(scd, sf.getLookupQueryId(), GenericUtil.uInt(k), 20);
						lm.put(k,  GenericUtil.isEmpty(s) ? "Not found for: " + k : s);
					}
					break;
				case	20: case 53://user
					for(Object k:stackSet)lm.put(k, GenericUtil.uStrMax( UserUtil.getUserDsc(customizationId, GenericUtil.uInt(k)),20));
				}
				result.put("lookUp", lm);
			} else {
				int maxLegend = GenericUtil.uInt(requestParams, "_max", 10);
				if(l.size()>maxLegend){ //TODO: temizlik
					BigDecimal total = new BigDecimal(0);
					String ids="";
					for(int qi=l.size()-1;qi>=maxLegend-1;qi--){						
						Map m=l.get(qi);
						ids+=","+m.get("id");
						total = total.add(new BigDecimal(m.get("xres").toString()));
						l.remove(qi);
					}
					Map nm = new HashMap();
					nm.put("id", -999999);nm.put("dsc", LocaleMsgCache.get2(scd, "others"));nm.put("xres", total);
					nm.put("ids", ids.substring(1));
					l.add(nm);
					
				}
			}
			for(W5QueryField o:queryResult.getQuery().get_queryFields())if(o.getQueryFieldId()==queryFieldId){
				switch(o.getPostProcessTip()){
				case	10://lookup static
					W5LookUp ld2 = FrameworkCache.getLookUp(customizationId, o.getLookupQueryId());
					if(ld2!=null)for(Map m:l){
						Object o2 = m.get("id");
						if(o2!=null && GenericUtil.uInt(o2)!=-999999)try{
							m.put("dsc", GenericUtil.uStrMax(LocaleMsgCache.get2(scd, ld2.get_detayMap().get(o2).getDsc()),20));
						} catch(Exception e){
							m.put("dsc", "Not found for: " + o2);
						}
						
					}
					break;
				case	12://lookup table
					for(Map m:l){
						Object o2 = m.get("id");
						if(o2!=null && GenericUtil.uInt(o2)!=-999999){
							String s = getTableRecordSummary(scd, o.getLookupQueryId(), GenericUtil.uInt(o2), 20);
							m.put("dsc", GenericUtil.isEmpty(s) ? "Not found for: " + o2 : s);
						}
						
					}
					break;
				case	20:case	53://user
					for(Map m:l){
						Object o2 = m.get("id");
						if(o2!=null && GenericUtil.uInt(o2)!=-999999){
							m.put("dsc", GenericUtil.uStrNvl(UserUtil.getUserDsc(customizationId, GenericUtil.uInt(o2)),"user-"+o2) );
						}
						
					}
				}
				
	    		break;
			}
			result.put("data", l);
    	}
		return result;
	}
	public Map executeQuery4StatTree(Map<String, Object> scd, int gridId, Map<String, String> requestParams) {
		requestParams.remove("firstLimit");requestParams.remove("limit");requestParams.remove("start"); requestParams.remove("sort");
		int customizationId = (Integer)scd.get("customizationId");
		int queryId = GenericUtil.uInt(executeSQLQuery("select query_id from iwb.w5_grid g where g.grid_id=? AND g.customization_id=?", gridId, customizationId).get(0));
		W5QueryResult queryResult = getQueryResult(scd,queryId);
		W5Table t = queryResult.getMainTable();
		if(queryId!=1 && queryId!=824 && queryResult.getMainTable()!=null && (!FrameworkSetting.debug || (scd.get("roleId")!=null && GenericUtil.uInt(scd.get("roleId"))!=0))){
			if(t.getAccessViewTip()==0 && !FrameworkCache.roleAccessControl(scd,  0)){
				throw new IWBException("security","Module", 0, null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_guvenlik_modul_kontrol"), null);
			}
			if(t.getAccessViewUserFields()==null && !GenericUtil.accessControl(scd, t.getAccessViewTip(), t.getAccessViewRoles(), t.getAccessViewUsers())){
				throw new IWBException("security","Query", queryId, null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_guvenlik_tablo_kontrol_goruntuleme"), null);
			}


		}
/*		StringBuilder tmpx = new StringBuilder("ali baba ${obj.dsc} ve 40 haramiler ${lnk.pk_query_field_id.dsc} olmus");
		interprateTemplate(scd, 5,1294, tmpx, true); */
		queryResult.setErrorMap(new HashMap());
		queryResult.setRequestParams(requestParams);
		
		int statType = GenericUtil.uInt(requestParams, "_stat");//0:count, n: sum(queryField) 
		String tableFieldChain = requestParams.get("_qfid");
		String funcFields = statType==0 ? null : requestParams.get("_ffids");
		if(tableFieldChain.indexOf('-')>0)tableFieldChain = tableFieldChain.split("-")[1];
		String tableFieldSQL = "";
		List params = new ArrayList();
		W5TableField tableField = null;
		
		if(tableFieldChain.startsWith("tbl.") || (tableFieldChain.startsWith("lnk.") && tableFieldChain.substring(4).replace(".", "&").split("&").length==1)){//direk tablodan veya link'in ilk kaydi ise
			String newSubStr = tableFieldChain.substring(4);
			boolean fieldFound = false;
			for(W5TableField tf :t.get_tableFieldList())if(tf.getDsc().equals(newSubStr)){
				tableFieldSQL =  "x."+newSubStr;//.put(tableFieldChain, o.toString());
				tableField = tf;
				fieldFound = true;
				break;
			}
			if(!fieldFound)
				throw new IWBException("framework","Query", queryId, null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_grid_stat_field_error"), null);
		} else if(tableFieldChain.startsWith("clc.")){//direk tablodan veya link'in ilk kaydi ise
			String newSubStr = tableFieldChain.substring(4);
			boolean fieldFound = false;
			List<W5TableFieldCalculated> ltcf = find("from W5TableFieldCalculated t where t.customizationId=? AND t.tableId=? AND t.dsc=?", customizationId, t.getTableId(), newSubStr);
			if(ltcf.isEmpty())
				throw new IWBException("framework","Query", queryId, null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_grid_stat_field_error"), null);
			W5TableFieldCalculated tcf = ltcf.get(0);
			Object[] oo = DBUtil.filterExt4SQL(tcf.getSqlCode(), scd, requestParams, null);
			tableFieldSQL =  "("+oo[0].toString()+")";//.put(tableFieldChain, o.toString());
			if(oo.length>1)params.addAll((List)oo[1]);
			tableField = t.get_tableFieldList().get(0);
			fieldFound = true;
		} else if(tableFieldChain.startsWith("lnk.") && tableFieldChain.substring(4).replace(".", "&").split("&").length>1){// burda bu field ile olan baglantiyi cozmek lazim TODO
			String newSubStr = tableFieldChain.substring(4);
			
			String[] sss = newSubStr.replace(".", "&").split("&");
			if(sss.length>1){ //TODO kaldirilmasi lazim
				W5Table newT = t;
				StringBuilder newSub = new StringBuilder();
				StringBuilder newSub2 = new StringBuilder();
				boolean foundSt = false;
				for(int isss=0;isss<sss.length-1;isss++){
					if(isss>0){
						newSub2.setLength(0);
						newSub2.append(newSub);
						newSub.setLength(0);
					}
					for(W5TableField tf :newT.get_tableFieldList())if(tf.getDsc().equals(sss[isss])){
						foundSt = false;
						if(tf.getDefaultControlTip()==7 || tf.getDefaultControlTip()==9 || tf.getDefaultControlTip()==10/* || tf.getDefaultControlTip()==15*/){//sub table
							W5Table st = FrameworkCache.getTable(scd, tf.getDefaultLookupTableId());
							if(st==null)break;//HATA: gerekli bir alt kademe tabloya ulasilamadi
							int dltId = 0;
							for(W5TableField stf :st.get_tableFieldList())if(stf.getDsc().equals(sss[isss+1])){
								dltId = stf.getDefaultLookupTableId();
								tableField = stf;
								foundSt = true;
								break;
							}
							if(!foundSt)break;//HATA: bir sonraki field bulunamadi
							newSub.append("(select ");
							newSub.append("y").append(isss).append(".").append(sss[isss+1]);
							newSub.append(" from ").append(st.getDsc()).append(" y").append(isss)
							.append(" where y").append(isss).append(".").append(st.get_tableFieldList().get(0).getDsc())
							.append("=").append(isss==0 ? ("x."+sss[isss]):newSub2);
							if(st.get_tableFieldList().size()>1)for(W5TableField wtf :st.get_tableFieldList())if(wtf.getDsc().equals("customization_id")){
								newSub.append(" AND y").append(isss).append(".customization_id=?");
								params.add(scd.get("customizationId"));
								break;
							}
							
							newSub.append(")");
							newT = st;
						} 
						break;			
					}
					if(!foundSt){//bulamamis uygun sey
						break;
					}
				}
				if(foundSt && newSub.length()>0){
					tableFieldSQL=  "("+newSub.toString()+")";//.put(tableFieldChain, o.toString());
				} else
					throw new IWBException("framework","Query", queryId, null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_grid_stat_field_error"), null);
			}

		}
		int stackFieldId = GenericUtil.uInt(requestParams, "_sfid");
		String stackField = null;
		W5QueryField stackedQueryField = null;
		
		if(stackFieldId!=0)for(W5QueryField qf:queryResult.getQuery().get_queryFields())if(stackFieldId==qf.getQueryFieldId()){
			stackField = qf.getDsc();
			if(stackField.equals(tableFieldSQL))stackField=null;
			else stackedQueryField = qf;
			break;
		}
//		queryResult.setOrderBy(PromisUtil.uStrNvl(requestParams.get(PromisUtil.uStrNvl(PromisSetting.appSettings.get("sql_sort"),"sort")), queryResult.getQuery().getSqlOrderby()));
//		queryResult.setOrderBy(queryResult.getQuery().getSqlOrderby());
		
		if(tableField.getParamTip()==2 || tableField.getDefaultControlTip()==2 || tableField.getDefaultControlTip()==18){
			tableFieldSQL = "to_char("+tableFieldSQL+", '"+(new String[]{"yyyy","yyyy/Q","yyyy/mm","yyyy/WW","yyyy/mm/dd"}[GenericUtil.uInt(requestParams, "_dtt")])+"')";
		}
		switch(queryResult.getQuery().getQueryTip()){
		case	9:case	10:case	15: //TODO: aslinda hata. olmamasi lazim
			throw new IWBException("framework","Query", queryId, null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_grid_stat_query_error"), null);
		default:
			
			String[] stats=new String[]{"","sum","avg","max","min"};
			queryResult.prepareQuery(null);
	    	if(!queryResult.getErrorMap().isEmpty())throw new IWBException("framework","Query", queryId, null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_grid_stat_error"), null);
	    	String sql = "select " + tableFieldSQL + " id, ";
	    	if(!GenericUtil.isEmpty(stackField))sql+=stackField + " stack_id, ";
	    	if(statType!=0){
	    		String[] fq=funcFields.split(",");
	    		int count=0;
	    		Set<Integer> fqs=new HashSet();
	    		for(String s:fq){
	    			int isx = GenericUtil.uInt(s);
	    			if(isx<0){
	    				List<W5TableFieldCalculated> ltcf = find("from W5TableFieldCalculated t where t.customizationId=? AND t.tableId=? AND t.tableFieldCalculatedId=?", customizationId, t.getTableId(), -isx);
	    				if(ltcf.isEmpty())
	    					throw new IWBException("framework","Query", queryId, null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_grid_stat_field_error"), null);
	    				count++;
	    				W5TableFieldCalculated tcf = ltcf.get(0);
	    				Object[] oo = DBUtil.filterExt4SQL(tcf.getSqlCode(), scd, requestParams, null);
	    				//tableFieldSQL =  oo[0].toString();//.put(tableFieldChain, o.toString());
	    				if(oo.length>1)params.addAll((List)oo[1]);

		        		if(count>1)
		        			sql += ","+stats[statType]+"(("+oo[0]+")) xres"+count;
		        		else
		        			sql += stats[statType]+"(("+oo[0]+")) xres";
	    				
	    			} else
	    			fqs.add(isx);
	    		}
	    		
	        	for(W5QueryField o:queryResult.getQuery().get_queryFields())if(fqs.contains(o.getQueryFieldId())){
	        		count++;
	        		if(count>1)
	        			sql += ","+stats[statType]+"("+o.getDsc()+") xres"+count;
	        		else
	        			sql += stats[statType]+"("+o.getDsc()+") xres";
	        	}
	        	
	        	if(count==0)throw new IWBException("framework","Query", queryId, null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_grid_stat_error"), null);
	    	} else {
	    		sql += "count(1) xres";
	    	}
	    	
	    	sql+=" from (" + queryResult.getExecutedSql() + ") x group by id";
	    	if(!GenericUtil.isEmpty(stackField))sql+=", stack_id";
	    	sql+=tableFieldSQL.startsWith("to_char(") ? " order by id":" order by xres desc";
	    	queryResult.setExecutedSql(sql);
		}
		Map result = new HashMap();result.put("success", true);
		if(queryResult.getErrorMap().isEmpty()){
			if(!params.isEmpty())queryResult.getSqlParams().addAll(0, params);
			List<Map> l = executeSQLQuery2Map(queryResult.getExecutedSql(), queryResult.getSqlParams());
			if(l!=null){
				if(!GenericUtil.isEmpty(stackField)){
					Set<String> stackSet = new HashSet();
					List<Map> nl = new ArrayList();
					Map<Object, Map> nom = new HashMap();
					for(Map m : l){
						Object oid = m.get("id");
						Map nm = nom.get(oid);
						if(nm==null){
							nm = new HashMap();
							nm.put("id", oid);
							nom.put(oid, nm);
							nl.add(nm);
						}
						for(Object k:m.keySet())if(!k.equals("id") && !k.equals("stack_id")){
							if(!GenericUtil.isEmpty(m.get("stack_id"))){
								nm.put(k+"_"+m.get("stack_id"), m.get(k));
								stackSet.add(m.get("stack_id").toString());
							}
						}
					}
					l = nl;
					Map lm = new HashMap();
					switch(stackedQueryField.getPostProcessTip()){
					case	10://static;
						W5LookUp ld3 = FrameworkCache.getLookUp(customizationId, stackedQueryField.getLookupQueryId());
						if(ld3!=null)for(Object k:stackSet)if(ld3.get_detayMap().get(k)!=null)lm.put(k, GenericUtil.uStrMax(LocaleMsgCache.get2(scd, ld3.get_detayMap().get(k).getDsc()),20));
						else throw new IWBException("framework","QueryField", stackedQueryField.getQueryFieldId(), null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_grid_stat_stacked_error"), null);
						break;
					case	12://table
						for(Object k:stackSet){
							String s = getTableRecordSummary(scd, stackedQueryField.getLookupQueryId(), GenericUtil.uInt(k), 20);
							lm.put(k,  GenericUtil.isEmpty(s) ? "Not found for: " + k : s);
						}
						break;
					case	20: case 53://user
						for(Object k:stackSet)lm.put(k, GenericUtil.uStrMax( UserUtil.getUserDsc(customizationId, GenericUtil.uInt(k)),20));
					}
					result.put("lookUp", lm);
				} else {
					int maxLegend = (tableField.getParamTip()==2 || tableField.getDefaultControlTip()==2 || tableField.getDefaultControlTip()==18) ? 360 : 10;
					if(l.size()>maxLegend){ //TODO: temizlik
						BigDecimal total = new BigDecimal(0);
						String ids="";
						for(int qi=l.size()-1;qi>=maxLegend-1;qi--){						
							Map m=l.get(qi);
							ids+=","+m.get("id");
							if(m.get("xres")!=null)total = total.add(new BigDecimal(m.get("xres").toString()));
							l.remove(qi);
						}
						Map nm = new HashMap();
						nm.put("id", -999999);nm.put("dsc", LocaleMsgCache.get2(scd, "others"));nm.put("xres", total);
						nm.put("ids", ids.substring(1));
						l.add(nm);
						
					}
				}
				switch(tableField.getDefaultControlTip()){
				case	6://lookup static
					W5LookUp ld2 = FrameworkCache.getLookUp(customizationId, tableField.getDefaultLookupTableId());
					if(ld2!=null)for(Map m:l){
						Object o2 = m.get("id");
						if(o2!=null && GenericUtil.uInt(o2)!=-999999){
							if(ld2.get_detayMap().get(o2)!=null)m.put("dsc", GenericUtil.uStrMax(LocaleMsgCache.get2(scd, ld2.get_detayMap().get(o2).getDsc()),20));
						}
						
					}
					break;
				case	7:case	10://lookup table
					for(Map m:l){
						Object o2 = m.get("id");
						if(o2!=null && GenericUtil.uInt(o2)!=-999999){
							String s = getTableRecordSummary(scd, tableField.getDefaultLookupTableId(), GenericUtil.uInt(o2), 20);
							m.put("dsc", GenericUtil.isEmpty(s) ? "Not found for: " + o2 : s);
						}
						
					}
					break;
				default:
					for(Map m:l){
						Object o2 = m.get("id");
						m.put("dsc", o2.toString());
						
					}
				}				
	    	}
			result.put("data", l);
		}
		return result;
	}

	public List executeQuery4DataList(Map<String, Object> scd, int tableId, Map<String, String> requestParams) {
		W5Table t = FrameworkCache.getTable(scd, tableId);
		if(t.getAccessViewTip()==0 && !FrameworkCache.roleAccessControl(scd,  0)){
			throw new IWBException("security","Module", 0, null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_guvenlik_modul_kontrol"), null);
		}
		if(t.getAccessViewUserFields()==null && !GenericUtil.accessControl(scd, t.getAccessViewTip(), t.getAccessViewRoles(), t.getAccessViewUsers())){
			throw new IWBException("security","Table", tableId, null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_guvenlik_tablo_kontrol_goruntuleme"), null);
		}

		/*W5Query q = new W5Query();
		q.setMainTableId(tableId); q.setSqlFrom(t.getDsc() + " x");
		
		W5QueryResult queryResult = new W5QueryResult(-1);

		queryResult.setErrorMap(new HashMap());queryResult.setScd(scd);
		queryResult.setRequestParams(requestParams);  */
		StringBuilder sql = new StringBuilder();
		String dateFormat = GenericUtil.uStrNvl(requestParams.get("dtFmt"), "dd/mm/yyyy");
		sql.append("SELECT ");
		String[] cols = requestParams.get("cols").split(",");
		boolean cldFlag=false, cntFlag=false;

		Map<String, W5LookUp> staticLookups = new HashMap();
		String groupBy = "", clcFieldPrefix = "iwb_x_qw_";
		int iwfField = 0;
		Map<String, String> iwbFieldMap = new HashMap();
		Map<String, String> errorMap = new HashMap();
		for(String c:cols){
			String fieldName = "";
				
			if(c.startsWith("clc.")){
				String c2=c.substring(4);
				List<W5TableFieldCalculated> l = find("from W5TableFieldCalculated t where t.customizationId=? AND t.tableId=? AND t.dsc=?" , scd.get("customizationId"), tableId, c2);
				if(!l.isEmpty()){
					sql.append("(").append(l.get(0).getSqlCode()).append(")");
					iwfField++;
					fieldName = clcFieldPrefix+iwfField;
					iwbFieldMap.put(c, fieldName);
				} else {//TODO ERROR
					errorMap.put(c, "Calculated Field not found");
					continue;
				}
	//				sql.append("").append(l.get(0).getSqlCode().replaceAll("x.", "x.")).append(" as ").append(l.get(0).getDsc()).append(",");
			} else if(c.startsWith("cld.")){//childs: only count and sum

				String[] sss = c.substring(4).replace(".", "&").split("&");
				if(sss.length==1){
					errorMap.put(c, "Table Child Field not defined");
					continue;
				}
				StringBuilder newSub = new StringBuilder();
				W5Table detT = null;
				W5TableChild detTc = null;
				for(W5TableChild tc:t.get_tableChildList()){
					detT = FrameworkCache.getTable(scd, tc.getRelatedTableId());
					if(detT!=null && detT.getDsc().equals(sss[0])){
						detTc = tc;
						break;
					}
				}
				if(detT==null){
					errorMap.put(c, "Table Child not found");
					continue;
				}
				for(int isss=1;isss<sss.length && isss<2;isss++){//TODO simdilik 1 seviye
					if(sss[isss].equals("cld")){
						isss++;
						if(isss>=sss.length){
							errorMap.put(c, "Child Tables wrong definition");
							continue;
						}
						if(GenericUtil.isEmpty(detT.get_tableChildList())){
							errorMap.put(c, "Child Tables does not exist for ["+detT.getDsc()+"]");
							continue;
						}
						for(W5TableChild tc:detT.get_tableChildList()){
							detT = FrameworkCache.getTable(scd, tc.getRelatedTableId());
							if(detT!=null && detT.getDsc().equals(sss[isss])){
								detTc = tc;
								break;
							}
						}
						errorMap.put(c, "Child Tables not implemented"); //TODO
						continue;
						
					} else if(sss[isss].equals("clc")){
						isss++;
						if(isss>=sss.length){
							errorMap.put(c, "Calculated Field wrong definition");
							continue;
						}
						List<W5TableFieldCalculated> l = find("from W5TableFieldCalculated t where t.customizationId=? AND t.tableId=? AND t.dsc=?" , scd.get("customizationId"), detT.getTableId(), sss[isss]);
						if(!l.isEmpty()){
							newSub.append("SELECT ").append("sum"/*valMap.get(c)*/).append("((").append(l.get(0).getSqlCode().replaceAll("x.", "z"+isss+".")).append(")) from ").append(detT.getDsc()).append(" z").append(isss).append(" WHERE ");
							newSub.append(" x.").append(t.get_tableFieldMap().get(detTc.getTableFieldId()).getDsc()).append("=z").append(isss).append(".").append(detT.get_tableFieldMap().get(detTc.getRelatedTableFieldId()).getDsc());
							if(detTc.getRelatedStaticTableFieldId()>0 && !GenericUtil.isEmpty(detTc.getRelatedStaticTableFieldVal()))
								newSub.append(" AND z").append(isss).append(".").append(detT.get_tableFieldMap().get(detTc.getRelatedStaticTableFieldId()).getDsc()).append("=").append(detTc.getRelatedStaticTableFieldVal());
							if(detT.get_tableParamList().size()>1)
								newSub.append(" AND z").append(isss).append(".").append("customization_id=${scd.customizationId}");
							sql.append("(").append(newSub).append(")");
							iwfField++;
							fieldName = clcFieldPrefix+iwfField;
							iwbFieldMap.put(c, fieldName);
//							if(valMap.get(c).equals("count"))valMap.put(c,"sum");
						} else {//TODO ERROR
							errorMap.put(c, "Calculated Field not found");
							continue;
						}
					
					} else if(sss[isss].equals("0")){
						newSub.append("SELECT count(1) from ").append(detT.getDsc()).append(" z").append(isss).append(" WHERE ");
						newSub.append(" x.").append(t.get_tableFieldMap().get(detTc.getTableFieldId()).getDsc()).append("=z").append(isss).append(".").append(detT.get_tableFieldMap().get(detTc.getRelatedTableFieldId()).getDsc());
						if(detTc.getRelatedStaticTableFieldId()>0 && !GenericUtil.isEmpty(detTc.getRelatedStaticTableFieldVal()))
							newSub.append(" AND z").append(isss).append(".").append(detT.get_tableFieldMap().get(detTc.getRelatedStaticTableFieldId()).getDsc()).append("=").append(detTc.getRelatedStaticTableFieldVal());
						if(detT.get_tableParamList().size()>1)
							newSub.append(" AND z").append(isss).append(".").append("customization_id=${scd.customizationId}");
						sql.append("(").append(newSub).append(")");
						iwfField++;
						fieldName = clcFieldPrefix+iwfField;
						iwbFieldMap.put(c, fieldName);
//						if(valMap.get(c).equals("count"))valMap.put(c,"sum");
						break;
					} else for(W5TableField tf :detT.get_tableFieldList())if(tf.getDsc().equals(sss[isss])){
						newSub.append("SELECT ").append("sum"/*valMap.get(c)*/).append("(z").append(isss).append(".").append(tf.getDsc()).append(") from ").append(detT.getDsc()).append(" z").append(isss).append(" WHERE ");
						newSub.append(" x.").append(t.get_tableFieldMap().get(detTc.getTableFieldId()).getDsc()).append("=z").append(isss).append(".").append(detT.get_tableFieldMap().get(detTc.getRelatedTableFieldId()).getDsc());
						if(detTc.getRelatedStaticTableFieldId()>0 && !GenericUtil.isEmpty(detTc.getRelatedStaticTableFieldVal()))
							newSub.append(" AND z").append(isss).append(".").append(detT.get_tableFieldMap().get(detTc.getRelatedStaticTableFieldId()).getDsc()).append("=").append(detTc.getRelatedStaticTableFieldVal());
						if(detT.get_tableParamList().size()>1)
							newSub.append(" AND z").append(isss).append(".").append("customization_id=${scd.customizationId}");
						sql.append("(").append(newSub).append(")");
						iwfField++;
						fieldName = clcFieldPrefix+iwfField;
						iwbFieldMap.put(c, fieldName);
//						if(valMap.get(c).equals("count"))valMap.put(c,"sum");
						break;
					}
					
				}

				
			} else if(c.startsWith("lnk.")&& c.lastIndexOf('.')>4){//parents
				W5TableField tableField = null ,mtableField = null;

				String[] sss = c.substring(4).replace(".", "&").split("&");
				W5Table newT = t;
				StringBuilder newSub = new StringBuilder();
				StringBuilder newSub2 = new StringBuilder();
				boolean foundSt = false;
				for(int isss=0;isss<sss.length-1;isss++){
					if(isss>0){
						newSub2.setLength(0);
						newSub2.append(newSub);
						newSub.setLength(0);
					}
					for(W5TableField tf :newT.get_tableFieldList())if(tf.getDsc().equals(sss[isss])){
						foundSt = false;
						if(mtableField==null)mtableField=tf;
						if(tf.getDefaultControlTip()==7 || tf.getDefaultControlTip()==9 || tf.getDefaultControlTip()==10/* || tf.getDefaultControlTip()==15*/){//sub table
							W5Table st = FrameworkCache.getTable(scd, tf.getDefaultLookupTableId());
							if(st==null)break;//HATA: gerekli bir alt kademe tabloya ulasilamadi
							for(W5TableField stf :st.get_tableFieldList())if(stf.getDsc().equals(sss[isss+1])){
								tableField = stf;
								foundSt = true;
								break;
							}
							if(!foundSt)break;//HATA: bir sonraki field bulunamadi
							newSub.append("(select ");
							if(isss==sss.length-2)switch(tableField.getDefaultControlTip()){
							case	2://date
								newSub.append("to_char(y.").append(isss).append(".").append(sss[isss+1]).append(",'").append(dateFormat).append("')");
								break;
							case	7:case 10:case 9://lookup
								if(tableField.getDefaultLookupTableId()>0){
									W5Table dt = FrameworkCache.getTable(scd, tableField.getDefaultLookupTableId());
									if(dt!=null && !GenericUtil.isEmpty(dt.getSummaryRecordSql())){
										newSub.append("(SELECT ").append(dt.getSummaryRecordSql().replaceAll("x.", "qxq.")).append(" FROM ").append(dt.getDsc()).append(" qxq WHERE qxq.").append(dt.get_tableFieldList().get(0).getDsc()).append("=y").append(isss).append(".").append(sss[isss+1]);
										if(dt.get_tableParamList().size()>1)newSub.append(" AND qxq.customization_id=${scd.customizationId}");
										newSub.append(")");
									} else
										newSub.append("y").append(isss).append(".").append(sss[isss+1]);
								} else
									newSub.append("y").append(isss).append(".").append(sss[isss+1]);
								break;
							default:
								newSub.append("y").append(isss).append(".").append(sss[isss+1]);
							} else newSub.append("y").append(isss).append(".").append(sss[isss+1]);
							newSub.append(" from ").append(st.getDsc()).append(" y").append(isss)
							.append(" where y").append(isss).append(".").append(st.get_tableFieldList().get(0).getDsc())
							.append("=").append(isss==0 ? ("x."+sss[isss]):newSub2);
							if(st.get_tableFieldList().size()>1)for(W5TableField wtf :st.get_tableFieldList())if(wtf.getDsc().equals("customization_id")){
								newSub.append(" AND y").append(isss).append(".customization_id=${scd.customizationId}");
								break;
							}
							
							newSub.append(")");
							newT = st;
						} 
						break;			
					}
					if(!foundSt){//bulamamis uygun sey
						break;
					}
				}
				if(foundSt && newSub.length()>0){
					sql.append("(").append(newSub).append(")");//.put(tableFieldChain, o.toString());
				} else
					throw new IWBException("framework","Query", 0, null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_pivot_field_error"), null);

				iwfField++;
				fieldName = clcFieldPrefix+iwfField;
				iwbFieldMap.put(c, fieldName);
				if(tableField!=null && tableField.getDefaultControlTip()==6){
					W5LookUp lu = FrameworkCache.getLookUp(scd, tableField.getDefaultLookupTableId());
					if(lu!=null)staticLookups.put(fieldName, lu);
				}
				
			} else {
				if(c.startsWith("lnk.")){
					iwbFieldMap.put(c, c.substring(4));
					c=c.substring(4);
				}
				for(W5TableField f:t.get_tableFieldList())if(f.getDsc().equals(c)){
					if(f.getDefaultControlTip()==6 && f.getDefaultLookupTableId()>0){
						W5LookUp lu = FrameworkCache.getLookUp(scd, f.getDefaultLookupTableId());
						if(lu!=null)staticLookups.put(f.getDsc(), lu);
					}
					if((f.getDefaultControlTip()==7 || f.getDefaultControlTip()==10) && f.getDefaultLookupTableId()>0){
						W5Table dt = FrameworkCache.getTable(scd, f.getDefaultLookupTableId());
						if(dt!=null && !GenericUtil.isEmpty(dt.getSummaryRecordSql())){
							sql.append("(SELECT ").append(dt.getSummaryRecordSql().replaceAll("x.", "qxq.")).append(" FROM ").append(dt.getDsc()).append(" qxq WHERE qxq.").append(dt.get_tableFieldList().get(0).getDsc()).append("=x.").append(f.getDsc());
							if(dt.get_tableParamList().size()>1)sql.append(" AND qxq.customization_id=${scd.customizationId}");
							sql.append(")");
						} else { 
							sql.append("x.").append(f.getDsc());
						}
					} else {
						if(f.getFieldTip()==2){ //date ise
							sql.append("to_char(x.").append(f.getDsc()).append(",'").append(dateFormat).append("')");
						} else
							sql.append("x.").append(f.getDsc());
					}
					fieldName = f.getDsc();
					break;
				}
			}
			
			sql.append(" ").append(fieldName).append(",");
		}
		sql.setLength(sql.length()-1);
		sql.append(" FROM ").append(t.getDsc()).append(" x");
		if(t.get_tableParamList().size()>1)sql.append(" WHERE x.customization_id=${scd.customizationId}");
		// GROUP BY ").append(groupBy.substring(1)
		StringBuilder sql2=sql;
		sql2.append(" limit 100000");//simdilik sinir koyalim
		
		
		
		Object[] oz = DBUtil.filterExt4SQL(sql2.toString(), scd, requestParams, null);
		List<Map> lm = executeSQLQuery2Map(oz[0].toString(),(List)oz[1]);
		if(!staticLookups.isEmpty() || !iwbFieldMap.isEmpty())for(Map<String, Object> mo:lm){
			for(String k:staticLookups.keySet()){
				W5LookUp lu = staticLookups.get(k);
				W5LookUpDetay ld = lu.get_detayMap().get(mo.get(k)); 
				if(ld!=null)mo.put(k, LocaleMsgCache.get2(scd, ld.getDsc()));				
			}
			for(String k:iwbFieldMap.keySet()){
				mo.put(k, mo.get(iwbFieldMap.get(k)));				
				mo.remove(iwbFieldMap.get(k));				
			}
		}
		return lm;
	}

	public List executeQuery4Pivot(Map<String, Object> scd, int tableId, Map<String, String> requestParams) {
		W5Table t = FrameworkCache.getTable(scd, tableId);

		if(t.getAccessViewTip()==0 && !FrameworkCache.roleAccessControl(scd,  0)){
			throw new IWBException("security","Module", 0, null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_guvenlik_modul_kontrol"), null);
		}
		if(t.getAccessViewUserFields()==null && !GenericUtil.accessControl(scd, t.getAccessViewTip(), t.getAccessViewRoles(), t.getAccessViewUsers())){
			throw new IWBException("security","Table", tableId, null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_guvenlik_tablo_kontrol_goruntuleme"), null);
		}
		

		/*W5Query q = new W5Query();
		q.setMainTableId(tableId); q.setSqlFrom(t.getDsc() + " x");
		
		W5QueryResult queryResult = new W5QueryResult(-1);

		queryResult.setErrorMap(new HashMap());queryResult.setScd(scd);
		queryResult.setRequestParams(requestParams);  */
		StringBuilder sql = new StringBuilder();
		String dateFormat = GenericUtil.uStrNvl(requestParams.get("dtFmt"), "yyyy");
		sql.append("SELECT ");
		String[] cols = requestParams.get("cols").split(",");
		String vals = requestParams.get("vals");
		Map<String, String> valMap = new HashMap();
		boolean cldFlag=false, cntFlag=false;
		if(!GenericUtil.isEmpty(vals)){
			if(vals.equals("count")|| vals.equals(":count")){
				valMap.put("1", "count");
			} else for(String s:vals.split(",")){
				String[] o2 = s.replace(':', ',').split(",");
				String fnc = (o2.length>1 && GenericUtil.hasPartInside("sum,count,min,max", o2[1].toLowerCase())) ? o2[1].toLowerCase() : "count";
				if(fnc.equals("count")){
	//				valMap.clear();break;
					cntFlag = true;
	//				return executeQuery4PivotBasic(scd, tableId, requestParams);
				} 
				if(o2[0].startsWith("cld.")){
					cldFlag = true;
				}
				valMap.put(o2[0], fnc);
			}
		}
		if(!cldFlag && cntFlag){
			valMap.clear();
		}
		
		Map<String, W5LookUp> staticLookups = new HashMap();
		String groupBy = "", clcFieldPrefix = "iwb_x_qw_";
		int iwfField = 0;
		Map<String, String> iwbFieldMap = new HashMap();
		Map<String, String> errorMap = new HashMap();
		for(String c:cols){
			String fieldName = "";
				
			if(c.startsWith("clc.")){
				String c2=c.substring(4);
				List<W5TableFieldCalculated> l = find("from W5TableFieldCalculated t where t.customizationId=? AND t.tableId=? AND t.dsc=?" , scd.get("customizationId"), tableId, c2);
				if(!l.isEmpty()){
					sql.append("(").append(l.get(0).getSqlCode()).append(")");
					iwfField++;
					fieldName = clcFieldPrefix+iwfField;
					iwbFieldMap.put(c, fieldName);
				} else {//TODO ERROR
					errorMap.put(c, "Calculated Field not found");
					continue;
				}
	//				sql.append("").append(l.get(0).getSqlCode().replaceAll("x.", "x.")).append(" as ").append(l.get(0).getDsc()).append(",");
			} else if(c.startsWith("cld.")){//childs: only count and sum
				if(!valMap.containsKey(c) || GenericUtil.isEmpty(t.get_tableChildList())){
					errorMap.put(c, "Child Tables does not exist for ["+t.getDsc()+"]");
					continue;
				}
				String[] sss = c.substring(4).replace(".", "&").split("&");
				if(sss.length==1){
					errorMap.put(c, "Table Child Field not defined");
					continue;
				}
				StringBuilder newSub = new StringBuilder();
				W5Table detT = null;
				W5TableChild detTc = null;
				for(W5TableChild tc:t.get_tableChildList()){
					detT = FrameworkCache.getTable(scd, tc.getRelatedTableId());
					if(detT!=null && detT.getDsc().equals(sss[0])){
						detTc = tc;
						break;
					}
				}
				if(detT==null){
					errorMap.put(c, "Table Child not found");
					continue;
				}
				for(int isss=1;isss<sss.length && isss<2;isss++){//TODO simdilik 1 seviye
					if(sss[isss].equals("cld")){
						isss++;
						if(isss>=sss.length){
							errorMap.put(c, "Child Tables wrong definition");
							continue;
						}
						if(GenericUtil.isEmpty(detT.get_tableChildList())){
							errorMap.put(c, "Child Tables does not exist for ["+detT.getDsc()+"]");
							continue;
						}
						for(W5TableChild tc:detT.get_tableChildList()){
							detT = FrameworkCache.getTable(scd, tc.getRelatedTableId());
							if(detT!=null && detT.getDsc().equals(sss[isss])){
								detTc = tc;
								break;
							}
						}
						errorMap.put(c, "Child Tables not implemented"); //TODO
						continue;
						
					} else if(sss[isss].equals("clc")){
						isss++;
						if(isss>=sss.length){
							errorMap.put(c, "Calculated Field wrong definition");
							continue;
						}
						List<W5TableFieldCalculated> l = find("from W5TableFieldCalculated t where t.customizationId=? AND t.tableId=? AND t.dsc=?" , scd.get("customizationId"), detT.getTableId(), sss[isss]);
						if(!l.isEmpty()){
							newSub.append("SELECT ").append(valMap.get(c)).append("((").append(l.get(0).getSqlCode().replaceAll("x.", "z"+isss+".")).append(")) from ").append(detT.getDsc()).append(" z").append(isss).append(" WHERE ");
							newSub.append(" x.").append(t.get_tableFieldMap().get(detTc.getTableFieldId()).getDsc()).append("=z").append(isss).append(".").append(detT.get_tableFieldMap().get(detTc.getRelatedTableFieldId()).getDsc());
							if(detTc.getRelatedStaticTableFieldId()>0 && !GenericUtil.isEmpty(detTc.getRelatedStaticTableFieldVal()))
								newSub.append(" AND z").append(isss).append(".").append(detT.get_tableFieldMap().get(detTc.getRelatedStaticTableFieldId()).getDsc()).append("=").append(detTc.getRelatedStaticTableFieldVal());
							if(detT.get_tableParamList().size()>1)
								newSub.append(" AND z").append(isss).append(".").append("customization_id=${scd.customizationId}");
							sql.append("(").append(newSub).append(")");
							iwfField++;
							fieldName = clcFieldPrefix+iwfField;
							iwbFieldMap.put(c, fieldName);
							if(valMap.get(c).equals("count"))valMap.put(c,"sum");
						} else {//TODO ERROR
							errorMap.put(c, "Calculated Field not found");
							continue;
						}
					
					} else for(W5TableField tf :detT.get_tableFieldList())if(tf.getDsc().equals(sss[isss])){
						newSub.append("SELECT ").append(valMap.get(c)).append("(z").append(isss).append(".").append(tf.getDsc()).append(") from ").append(detT.getDsc()).append(" z").append(isss).append(" WHERE ");
						newSub.append(" x.").append(t.get_tableFieldMap().get(detTc.getTableFieldId()).getDsc()).append("=z").append(isss).append(".").append(detT.get_tableFieldMap().get(detTc.getRelatedTableFieldId()).getDsc());
						if(detTc.getRelatedStaticTableFieldId()>0 && !GenericUtil.isEmpty(detTc.getRelatedStaticTableFieldVal()))
							newSub.append(" AND z").append(isss).append(".").append(detT.get_tableFieldMap().get(detTc.getRelatedStaticTableFieldId()).getDsc()).append("=").append(detTc.getRelatedStaticTableFieldVal());
						if(detT.get_tableParamList().size()>1)
							newSub.append(" AND z").append(isss).append(".").append("customization_id=${scd.customizationId}");
						sql.append("(").append(newSub).append(")");
						iwfField++;
						fieldName = clcFieldPrefix+iwfField;
						iwbFieldMap.put(c, fieldName);
						if(valMap.get(c).equals("count"))valMap.put(c,"sum");
						break;
					}
					
				}

				
			} else if(c.startsWith("lnk.")&& c.lastIndexOf('.')>4){//parents
				W5TableField tableField = null ,mtableField = null;

				String[] sss = c.substring(4).replace(".", "&").split("&");
				W5Table newT = t;
				StringBuilder newSub = new StringBuilder();
				StringBuilder newSub2 = new StringBuilder();
				boolean foundSt = false;
				for(int isss=0;isss<sss.length-1;isss++){
					if(isss>0){
						newSub2.setLength(0);
						newSub2.append(newSub);
						newSub.setLength(0);
					}
					for(W5TableField tf :newT.get_tableFieldList())if(tf.getDsc().equals(sss[isss])){
						foundSt = false;
						if(mtableField==null)mtableField=tf;
						if(tf.getDefaultControlTip()==7 || tf.getDefaultControlTip()==9 || tf.getDefaultControlTip()==10/* || tf.getDefaultControlTip()==15*/){//sub table
							W5Table st = FrameworkCache.getTable(scd, tf.getDefaultLookupTableId());
							if(st==null)break;//HATA: gerekli bir alt kademe tabloya ulasilamadi
							for(W5TableField stf :st.get_tableFieldList())if(stf.getDsc().equals(sss[isss+1])){
								tableField = stf;
								foundSt = true;
								break;
							}
							if(!foundSt)break;//HATA: bir sonraki field bulunamadi
							newSub.append("(select ");
							if(isss==sss.length-2)switch(tableField.getDefaultControlTip()){
							case	2://date
								newSub.append("to_char(y.").append(isss).append(".").append(sss[isss+1]).append(",'").append(dateFormat).append("')");
								break;
							case	7:case 10:case 9://lookup
								if(tableField.getDefaultLookupTableId()>0){
									W5Table dt = FrameworkCache.getTable(scd, tableField.getDefaultLookupTableId());
									if(dt!=null && !GenericUtil.isEmpty(dt.getSummaryRecordSql())){
										newSub.append("(SELECT ").append(dt.getSummaryRecordSql().replaceAll("x.", "qxq.")).append(" FROM ").append(dt.getDsc()).append(" qxq WHERE qxq.").append(dt.get_tableFieldList().get(0).getDsc()).append("=y").append(isss).append(".").append(sss[isss+1]);
										if(dt.get_tableParamList().size()>1)newSub.append(" AND qxq.customization_id=${scd.customizationId}");
										newSub.append(")");
									} else
										newSub.append("y").append(isss).append(".").append(sss[isss+1]);
								} else
									newSub.append("y").append(isss).append(".").append(sss[isss+1]);
								break;
							default:
								newSub.append("y").append(isss).append(".").append(sss[isss+1]);
							} else newSub.append("y").append(isss).append(".").append(sss[isss+1]);
							newSub.append(" from ").append(st.getDsc()).append(" y").append(isss)
							.append(" where y").append(isss).append(".").append(st.get_tableFieldList().get(0).getDsc())
							.append("=").append(isss==0 ? ("x."+sss[isss]):newSub2);
							if(st.get_tableFieldList().size()>1)for(W5TableField wtf :st.get_tableFieldList())if(wtf.getDsc().equals("customization_id")){
								newSub.append(" AND y").append(isss).append(".customization_id=${scd.customizationId}");
								break;
							}
							
							newSub.append(")");
							newT = st;
						} 
						break;			
					}
					if(!foundSt){//bulamamis uygun sey
						break;
					}
				}
				if(foundSt && newSub.length()>0){
					sql.append("(").append(newSub).append(")");//.put(tableFieldChain, o.toString());
				} else
					throw new IWBException("framework","Query", 0, null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_pivot_field_error"), null);

				iwfField++;
				fieldName = clcFieldPrefix+iwfField;
				iwbFieldMap.put(c, fieldName);
				if(tableField!=null && tableField.getDefaultControlTip()==6){
					W5LookUp lu = FrameworkCache.getLookUp(scd, tableField.getDefaultLookupTableId());
					if(lu!=null)staticLookups.put(fieldName, lu);
				}
				
			} else {
				if(c.startsWith("lnk.")){
					iwbFieldMap.put(c, c.substring(4));
					c=c.substring(4);
				}
				for(W5TableField f:t.get_tableFieldList())if(f.getDsc().equals(c)){
					if(f.getDefaultControlTip()==6 && f.getDefaultLookupTableId()>0){
						W5LookUp lu = FrameworkCache.getLookUp(scd, f.getDefaultLookupTableId());
						if(lu!=null)staticLookups.put(f.getDsc(), lu);
					}
					if((f.getDefaultControlTip()==7 || f.getDefaultControlTip()==10) && f.getDefaultLookupTableId()>0){
						W5Table dt = FrameworkCache.getTable(scd, f.getDefaultLookupTableId());
						if(dt!=null && !GenericUtil.isEmpty(dt.getSummaryRecordSql())){
							sql.append("(SELECT ").append(dt.getSummaryRecordSql().replaceAll("x.", "qxq.")).append(" FROM ").append(dt.getDsc()).append(" qxq WHERE qxq.").append(dt.get_tableFieldList().get(0).getDsc()).append("=x.").append(f.getDsc());
							if(dt.get_tableParamList().size()>1)sql.append(" AND qxq.customization_id=${scd.customizationId}");
							sql.append(")");
						} else { 
							sql.append("x.").append(f.getDsc());
						}
					} else {
						if(f.getFieldTip()==2){ //date ise
							sql.append("to_char(x.").append(f.getDsc()).append(",'").append(dateFormat).append("')");
						} else
							sql.append("x.").append(f.getDsc());
					}
					fieldName = f.getDsc();
					break;
				}
			}
			if(!valMap.containsKey(c))groupBy+=","+fieldName;
			
			sql.append(" ").append(fieldName).append(",");
		}
		sql.setLength(sql.length()-1);
		sql.append(" FROM ").append(t.getDsc()).append(" x");
		if(t.get_tableParamList().size()>1)sql.append(" WHERE x.customization_id=${scd.customizationId}");
		// GROUP BY ").append(groupBy.substring(1)
		StringBuilder sql2;
		if(!valMap.isEmpty()){
			sql2=new StringBuilder(sql.length() + 3*groupBy.length() +50);
			sql2.append("SELECT ").append(groupBy.substring(1));				
			for(String c:valMap.keySet()){
				String fieldName = iwbFieldMap.containsKey(c) ? iwbFieldMap.get(c): c;
				sql2.append(",").append(valMap.get(c)).append("(").append(fieldName).append(") ").append(fieldName.equals("1") ? "xxx":fieldName);
			}
			sql2.append(" FROM (").append(sql).append(") mq GROUP BY ").append(groupBy.substring(1));
		} else sql2=sql;
		
		
		
		Object[] oz = DBUtil.filterExt4SQL(sql2.toString(), scd, requestParams, null);
		List<Map> lm = executeSQLQuery2Map(oz[0].toString(),(List)oz[1]);
		if(!staticLookups.isEmpty() || !iwbFieldMap.isEmpty())for(Map<String, Object> mo:lm){
			for(String k:staticLookups.keySet()){
				W5LookUp lu = staticLookups.get(k);
				W5LookUpDetay ld = lu.get_detayMap().get(mo.get(k)); 
				if(ld!=null)mo.put(k, LocaleMsgCache.get2(scd, ld.getDsc()));				
			}
			for(String k:iwbFieldMap.keySet()){
				mo.put(k, mo.get(iwbFieldMap.get(k)));				
				mo.remove(iwbFieldMap.get(k));				
			}
		}
		return lm;
	}
}

