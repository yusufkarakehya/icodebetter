package iwb.dao.rdbms_impl;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.jdbc.ReturningWork;
import org.hibernate.jdbc.Work;
// import org.influxdb.InfluxDBFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import iwb.cache.FrameworkCache;
import iwb.cache.FrameworkSetting;
import iwb.cache.LocaleMsgCache;
import iwb.custom.trigger.QueryTrigger;
// import iwb.dao.tsdb_impl.InfluxDao;
import iwb.domain.db.Log5GlobalFuncAction;
import iwb.domain.db.Log5Notification;
import iwb.domain.db.Log5QueryAction;
import iwb.domain.db.W5Email;
import iwb.domain.db.W5FileAttachment;
import iwb.domain.db.W5Form;
import iwb.domain.db.W5FormCell;
import iwb.domain.db.W5FormModule;
import iwb.domain.db.W5FormSmsMail;
import iwb.domain.db.W5FormValue;
import iwb.domain.db.W5FormValueCell;
import iwb.domain.db.W5GlobalFuncParam;
import iwb.domain.db.W5Grid;
import iwb.domain.db.W5ListBase;
import iwb.domain.db.W5LookUp;
import iwb.domain.db.W5LookUpDetay;
import iwb.domain.db.W5Param;
import iwb.domain.db.W5Project;
import iwb.domain.db.W5Query;
import iwb.domain.db.W5QueryField;
import iwb.domain.db.W5QueryFieldCreation;
import iwb.domain.db.W5QueryParam;
import iwb.domain.db.W5Table;
import iwb.domain.db.W5TableChild;
import iwb.domain.db.W5TableField;
import iwb.domain.db.W5TableFieldCalculated;
import iwb.domain.db.W5TableParam;
import iwb.domain.db.W5VcsCommit;
import iwb.domain.db.W5VcsObject;
import iwb.domain.db.W5Workflow;
import iwb.domain.db.W5WorkflowStep;
import iwb.domain.db.W5WsMethod;
import iwb.domain.db.W5WsMethodParam;
import iwb.domain.helper.W5AccessControlHelper;
import iwb.domain.helper.W5FormCellHelper;
import iwb.domain.helper.W5TableChildHelper;
import iwb.domain.helper.W5TableRecordHelper;
import iwb.domain.result.W5FormResult;
import iwb.domain.result.W5GlobalFuncResult;
import iwb.domain.result.W5QueryResult;
import iwb.domain.result.W5TableRecordInfoResult;
import iwb.engine.GlobalScriptEngine;
import iwb.enums.FieldDefinitions;
import iwb.exception.IWBException;
import iwb.service.FrameworkService;
import iwb.util.DBUtil;
import iwb.util.GenericUtil;
import iwb.util.LogUtil;
import iwb.util.MailUtil;
import iwb.util.UserUtil;

@SuppressWarnings({ "unchecked", "unused" })
@Repository
public class PostgreSQL extends BaseDAO {


	@Lazy
	@Autowired
	private MetadataLoaderDAO metaDataDao;
	
	
	private static Logger logger = Logger.getLogger(PostgreSQL.class);
	@Autowired
	private FrameworkService service;


	@Lazy
	@Autowired
	private GlobalScriptEngine scriptEngine;
	
	/*
	 * public void setEngine(FrameworkEngine engine) { this.engine = engine; }
	 */

	public W5QueryResult executeQuery(Map<String, Object> scd, int queryId, Map<String, String> requestParams) {
		W5QueryResult queryResult = metaDataDao.getQueryResult(scd, queryId);
		if (queryId != 1 && queryId != 824 && queryResult.getMainTable() != null && (!FrameworkSetting.debug
				|| (scd.get("roleId") != null && GenericUtil.uInt(scd.get("roleId")) != 0))) {
			W5Table t = queryResult.getMainTable();
			if (t.getAccessViewUserFields() == null && !GenericUtil.accessControl(scd, t.getAccessViewTip(),
					t.getAccessViewRoles(), t.getAccessViewUsers())) {
				throw new IWBException("security", "Query", queryId, null,
						LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_tablo_kontrol_goruntuleme"),
						null);
			}
		}
		queryResult.setErrorMap(new HashMap());
		queryResult.setRequestParams(requestParams);

		//
		// queryResult.setOrderBy(PromisUtil.uStrNvl(requestParams.get(PromisUtil.uStrNvl(PromisSetting.appSettings.get("sql_sort"),"sort")),
		// queryResult.getQuery().getSqlOrderby()));
		if (requestParams.get("sort") != null) {
			queryResult.setOrderBy(requestParams.get("sort"));
			if (requestParams.get("dir") != null)
				queryResult.setOrderBy(queryResult.getOrderBy() + " " + requestParams.get("dir"));
		} else
			queryResult.setOrderBy(queryResult.getQuery().getSqlOrderby());
		switch (queryResult.getQuery().getQueryTip()) {
		case 9:
		case 10:
			queryResult.prepareTreeQuery(null);
			break;
		default:
			queryResult.prepareQuery(null);
		}
		if (queryResult.getErrorMap().isEmpty()) {
			QueryTrigger.beforeExecuteQuery(queryResult, this);
			queryResult.setFetchRowCount(
					GenericUtil.uIntNvl(requestParams, "limit", GenericUtil.uInt(requestParams, "firstLimit")));
			queryResult.setStartRowNumber(GenericUtil.uInt(requestParams, "start"));
			runQuery(queryResult);
			if (queryResult.getQuery().getShowParentRecordFlag() != 0 && queryResult.getData() != null) {
				for (Object[] oz : queryResult.getData()) {
					int tableId = GenericUtil.uInt(oz[queryResult.getQuery().get_tableIdTabOrder()]);
					int tablePk = GenericUtil.uInt(oz[queryResult.getQuery().get_tablePkTabOrder()]);
					oz[oz.length - 1] = findRecordParentRecords(scd, tableId, tablePk, 0, true);
				}
			}
			QueryTrigger.afterExecuteQuery(queryResult, this);
		}
		return queryResult;
	}

	private void logTableRecord(W5FormResult fr, String paramSuffix) {
		W5Table t = FrameworkCache.getTable(fr.getScd(), fr.getForm().getObjectId());
		StringBuilder sql = new StringBuilder();
		int userId = (Integer) fr.getScd().get("userId");
		int action = fr.getAction();
		String table = t.getDsc();

		if (FrameworkSetting.log2tsdb) {
			String schema = FrameworkCache.getProject(fr.getScd()).getRdbmsSchema();
			sql.append("select * from ").append(table).append(" t ");
			List<Object> whereParams = new ArrayList<Object>(fr.getPkFields().size());

			if (fr.getPkFields().size() > 0) {
				sql.append(" where ");
				boolean b = false;
				StringBuilder startQL = new StringBuilder();
				startQL.append(schema).append("_").append(t.getDsc().replace('.', '_'));
				for (W5TableParam px : t.get_tableParamList()) {
					if (b)
						sql.append(" AND ");
					else
						b = true;
					sql.append("t.").append(px.getExpressionDsc()).append("=?");
					whereParams.add(fr.getPkFields().get(px.getDsc()));
					startQL.append(",").append(px.getExpressionDsc()).append("=")
							.append(fr.getPkFields().get(px.getDsc()));
				}
				List<Map> l = executeSQLQuery2Map(sql.toString(), whereParams);
				if (!GenericUtil.isEmpty(l)) {
					Map m = l.get(0);
					for (W5TableParam px : t.get_tableParamList())
						m.remove(px.getExpressionDsc());
					m.put("_action", fr.getAction());
					startQL.append(" ").append(GenericUtil.fromMapToInfluxFields(m));
					LogUtil.logCrud(startQL.toString());
					// influxDao.insert(pr, "log_table.."+table, p, l.get(0),
					// null); TODO
				}
			}
			return;
		}
		/*
		 * String logTable = table; if(logTable.contains(".")){ logTable =
		 * logTable.substring(logTable.lastIndexOf('.')+1); } Session session =
		 * getCurrentSession();
		 * 
		 * sql.append(
		 * "select count(*)  from information_schema.tables qx where lower(qx.table_name) = '"
		 * ).append(logTable.toLowerCase()).append(
		 * "' and lower(qx.table_schema) = '"
		 * ).append(FrameworkSetting.crudLogSchema).append("'"); int count =
		 * GenericUtil.uInt(session.createSQLQuery(sql.toString()).uniqueResult(
		 * ));
		 * 
		 * sql.setLength(0); sql.append(" select nextval('seq_log') "
		 * ).append(FieldDefinitions.tableFieldName_LogId).append(",")
		 * .append(userId).append(" "
		 * ).append(FieldDefinitions.tableFieldName_LogUserId).append(",")
		 * .append(action).append(" "
		 * ).append(FieldDefinitions.tableFieldName_LogAction)
		 * .append(",iwb.fnc_sysdate(").append(fr.getScd().get("customizationId"
		 * )).append(") "
		 * ).append(FieldDefinitions.tableFieldName_LogDateTime).append(",t.*");
		 * 
		 * sql.append(" from ").append(table).append(" t");
		 * 
		 * List<Object> whereParams = new
		 * ArrayList<Object>(fr.getPkFields().size());
		 * 
		 * if(fr.getPkFields().size() > 0){ sql.append(" where "); boolean b =
		 * false;
		 * 
		 * for(W5TableParam p: t.get_tableParamList()){ if(b)sql.append(" AND "
		 * ); else b=true;
		 * sql.append("t.").append(p.getExpressionDsc()).append("=?");
		 * whereParams.add(fr.getPkFields().get(p.getDsc())); } }
		 * 
		 * 
		 * final String flogTable = logTable; getCurrentSession().doWork(new
		 * Work() {
		 * 
		 * public void execute(Connection conn) throws SQLException { try {
		 * String createSql = GenericUtil.replaceSql("create table "
		 * +FrameworkSetting.crudLogSchema+"."+FrameworkSetting.
		 * crudLogTablePrefix+flogTable + " as " + sql.toString(),whereParams);
		 * if(count==0){ PreparedStatement s = conn.prepareStatement(createSql);
		 * s.execute(); s.close(); } else { Savepoint savepoint =
		 * conn.setSavepoint("spx-1"); PreparedStatement s =
		 * conn.prepareStatement("insert into "
		 * +FrameworkSetting.crudLogSchema+"."+FrameworkSetting.
		 * crudLogTablePrefix+flogTable+
		 * GenericUtil.replaceSql(sql.toString(),whereParams)); try{
		 * s.execute(); s.close(); } catch(SQLException e){ if(conn != null &&
		 * savepoint != null) { conn.rollback(savepoint); }
		 * 
		 * s = conn.prepareStatement("alter table "
		 * +FrameworkSetting.crudLogSchema+"."
		 * +FrameworkSetting.crudLogTablePrefix+ flogTable + " rename to lt5_" +
		 * fr.getForm().getObjectId() + "_"+ new
		 * SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())); s.execute();
		 * s.close();
		 * 
		 * s = conn.prepareStatement(createSql); s.execute(); s.close(); }
		 * 
		 * } } catch(Exception e){
		 * if(FrameworkSetting.debug)e.printStackTrace(); throw new
		 * IWBException("sql","Form Log"
		 * ,fr.getFormId(),GenericUtil.replaceSql(sql.toString(), whereParams),
		 * e.getMessage(), e.getCause()); } } });
		 */
	}
	private void prepareLookupTableQuery(W5QueryResult queryResult, StringBuilder sql2, AtomicInteger paramIndex) { // TODO
		List<Object> preParams = new ArrayList<Object>();
		for (W5QueryField qf : queryResult.getQuery().get_queryFields())
			if (qf.getPostProcessTip() == 12 && qf.getLookupQueryId() != 0 && (queryResult.getQueryColMap() == null
					|| queryResult.getQueryColMap().containsKey(qf.getDsc()))) { // queryField'da
																					// postProcessTip=lookupTable
																					// olanlar
				// saptaniyor
				W5Table tqf = FrameworkCache.getTable(queryResult.getScd(), qf.getLookupQueryId());
				if (tqf != null && tqf.getSummaryRecordSql() != null) {
					sql2.append(",(select ");

					if (tqf.getSummaryRecordSql().contains("${")) {
						Object[] oz = DBUtil.filterExt4SQL(tqf.getSummaryRecordSql(), queryResult.getScd(),
								queryResult.getRequestParams(), null);
						sql2.append(oz[0]);
						if (oz[1] != null)
							preParams.addAll((List) oz[1]);
					} else
						sql2.append(tqf.getSummaryRecordSql());

					sql2.append(" from ").append(tqf.getDsc()).append(" x where x.")
							.append(tqf.get_tableParamList().get(0).getExpressionDsc()).append("=z.")
							.append(qf.getDsc());

					sql2.append(DBUtil.includeTenantProjectPostSQL(queryResult.getScd(), tqf));

					sql2.append(") ").append(qf.getDsc()).append("_qw_ ");
					W5QueryField field = new W5QueryField();
					field.setDsc(qf.getDsc() + "_qw_");
					field.setMainTableFieldId(qf.getMainTableFieldId());
					if (queryResult.getPostProcessQueryFields() == null)
						queryResult.setPostProcessQueryFields(new ArrayList());
					queryResult.getPostProcessQueryFields().add(field);
				}
			} else if (qf.getPostProcessTip() == 13 && qf.getLookupQueryId() != 0
					&& (queryResult.getQueryColMap() == null
							|| queryResult.getQueryColMap().containsKey(qf.getDsc()))) { // queryField'da
																							// postProcessTip=lookupTable
																							// olanlar
				// saptaniyor
				W5Table tqf = FrameworkCache.getTable(queryResult.getScd(), qf.getLookupQueryId());
				if (tqf != null && tqf.getSummaryRecordSql() != null) {
					sql2.append(",(select ");

					if (tqf.getSummaryRecordSql().contains("${")) {
						Object[] oz = DBUtil.filterExt4SQL(tqf.getSummaryRecordSql(), queryResult.getScd(),
								queryResult.getRequestParams(), null);
						sql2.append("string_agg(").append(oz[0]).append(",',')");
						if (oz[1] != null)
							preParams.addAll((List) oz[1]);
					} else
						sql2.append("string_agg(").append(tqf.getSummaryRecordSql()).append(",',')");

					sql2.append(" from ").append(tqf.getDsc()).append(" x where x.")
							.append(tqf.get_tableParamList().get(0).getExpressionDsc())
							.append(" in (select q.satir::int from iwb.tool_parse_numbers(z.").append(qf.getDsc())
							.append(",',') q)");

					sql2.append(DBUtil.includeTenantProjectPostSQL(queryResult.getScd(), tqf));

					sql2.append(") ").append(qf.getDsc()).append("_qw_ ");
					W5QueryField field = new W5QueryField();
					field.setDsc(qf.getDsc() + "_qw_");
					field.setMainTableFieldId(qf.getMainTableFieldId());
					if (queryResult.getPostProcessQueryFields() == null)
						queryResult.setPostProcessQueryFields(new ArrayList());
					queryResult.getPostProcessQueryFields().add(field);
				}
			}
		if (preParams.size() > 0)
			queryResult.getSqlParams().addAll(paramIndex.intValue(), preParams);
	}

	public void runQuery(final W5QueryResult queryResult) {

		final W5Query query = queryResult.getQuery();
		Log5QueryAction queryAction = new Log5QueryAction(queryResult);

		// String sql = null;
		final W5Table mainTable = queryResult.getMainTable();
		final StringBuilder sql2 = new StringBuilder();
		final AtomicInteger paramIndex = new AtomicInteger(0);
		String error = null;

		try {

			getCurrentSession().doWork(new Work() {

				public void execute(Connection conn) throws SQLException {
					PreparedStatement s = null;
					ResultSet rs = null;
					Set<String> errorFieldSet = new HashSet();
					if (queryResult.getFetchRowCount() != 0) {
						if (false && !GenericUtil.isEmpty(queryResult.getExecutedSqlFrom())) {
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
						if (resultRowCount < queryResult.getStartRowNumber()) {
							queryResult.setStartRowNumber(0);
						}
						queryResult.setResultRowCount(resultRowCount);
						if (resultRowCount < queryResult.getStartRowNumber() + queryResult.getFetchRowCount()) {
							queryResult.setFetchRowCount((int) (resultRowCount - queryResult.getStartRowNumber()));
						}

						queryResult.getSqlParams().add(queryResult.getFetchRowCount()); // queryResult.getStartRowNumber()+
						if (queryResult.getStartRowNumber() > 0)
							queryResult.getSqlParams().add(queryResult.getStartRowNumber());

						sql2.append("select z.*");
						if (query.getSqlPostSelect() != null && query.getSqlPostSelect().trim().length() > 2) {
							if (query.getSqlPostSelect().contains("${")) {
								Object[] oz = DBUtil.filterExt4SQL(query.getSqlPostSelect(), queryResult.getScd(),
										queryResult.getRequestParams(), null);
								sql2.append(", ").append(oz[0]);
								if (oz[1] != null) {
									queryResult.getSqlParams().addAll(0, (List) oz[1]);
									paramIndex.getAndAdd(((List) oz[1]).size());
								}

							} else
								sql2.append(", ").append(query.getSqlPostSelect());
							sql2.append(" ");
						}
						if (queryResult.getPostProcessQueryFields() != null && mainTable != null
								&& queryResult.getViewLogModeTip() == 0) {
							addPostQueryFields(queryResult, sql2, paramIndex);
						}

						for (W5QueryField qf : queryResult.getQuery().get_queryFields())
							if ((qf.getPostProcessTip() == 12 || qf.getPostProcessTip() == 13)
									&& qf.getLookupQueryId() != 0) { // queryField'da
																		// postProcessTip=lookupTable
																		// olanlar
																		// saptaniyor
								prepareLookupTableQuery(queryResult, sql2, paramIndex);
								break;
							}
						for (W5QueryField qf : queryResult.getQuery().get_queryFields())
							if ((qf.getPostProcessTip() == 16 || qf.getPostProcessTip() == 17)
									&& qf.getLookupQueryId() != 0) { // queryField'da
																		// postProcessTip=lookupQuery
																		// olanlar
																		// saptaniyor
								W5QueryResult queryFieldLookupQueryResult = metaDataDao.getQueryResult(queryResult.getScd(),
										qf.getLookupQueryId());
								if (queryFieldLookupQueryResult != null
										&& queryFieldLookupQueryResult.getQuery() != null) {
									W5QueryField field = new W5QueryField();
									field.setDsc(qf.getDsc() + "_qw_");
									errorFieldSet.add(field.getDsc());
									field.setMainTableFieldId(qf.getMainTableFieldId());
									if (queryResult.getPostProcessQueryFields() == null)
										queryResult.setPostProcessQueryFields(new ArrayList());
									queryResult.getPostProcessQueryFields().add(field);
									if (qf.getPostProcessTip() == 16
											&& queryFieldLookupQueryResult.getQuery().get_queryFields().size() > 2)
										for (int qi = 2; qi < queryFieldLookupQueryResult.getQuery().get_queryFields()
												.size(); qi++) {
											W5QueryField qf2 = queryFieldLookupQueryResult.getQuery().get_queryFields()
													.get(qi);
											field = new W5QueryField();
											field.setDsc(qf.getDsc() + "__" + qf2.getDsc());
											errorFieldSet.add(field.getDsc());
											field.setMainTableFieldId(qf2.getMainTableFieldId());
											queryResult.getPostProcessQueryFields().add(field);
										}
								}
							}

						sql2.append("from (").append(queryResult.getExecutedSql()).append(" limit ?");
						if (queryResult.getStartRowNumber() > 0) {
							sql2.append(" offset ?");
						}
						sql2.append(") z");

					} else {
						if (queryResult.getPostProcessQueryFields() != null && mainTable != null
								&& queryResult.getViewLogModeTip() == 0) {
							sql2.append("select z.*"); //
							if (query.getSqlPostSelect() != null && query.getSqlPostSelect().trim().length() > 2) {
								if (query.getSqlPostSelect().contains("${")) {
									Object[] oz = DBUtil.filterExt4SQL(query.getSqlPostSelect(), queryResult.getScd(),
											queryResult.getRequestParams(), null);
									sql2.append(", ").append(oz[0]);
									if (oz[1] != null) {
										queryResult.getSqlParams().addAll(0, (List) oz[1]);
										paramIndex.getAndAdd(((List) oz[1]).size());
									}
								} else
									sql2.append(", ").append(query.getSqlPostSelect());
								if (queryResult.getQueryColMap() != null)
									for (W5QueryField qf : queryResult.getQuery().get_queryFields())
										if (qf.getPostProcessTip() == 99) {
											queryResult.getQueryColMap().put(qf.getDsc(), qf);
										}
							}
							addPostQueryFields(queryResult, sql2, paramIndex);

							for (W5QueryField qf : queryResult.getQuery().get_queryFields())
								if ((qf.getPostProcessTip() == 12 || qf.getPostProcessTip() == 13)
										&& qf.getLookupQueryId() != 0) { // queryField'da
																			// postProcessTip=lookupTable
																			// olanlar
									// saptaniyor
									prepareLookupTableQuery(queryResult, sql2, paramIndex);
									break;
								}

							for (W5QueryField qf : queryResult.getQuery().get_queryFields())
								if ((qf.getPostProcessTip() == 16 || qf.getPostProcessTip() == 17)
										&& qf.getLookupQueryId() != 0) { // queryField'da
																			// postProcessTip=lookupQuery
																			// olanlar
									// saptaniyor
									W5QueryResult queryFieldLookupQueryResult = metaDataDao.getQueryResult(queryResult.getScd(),
											qf.getLookupQueryId());
									if (queryFieldLookupQueryResult != null
											&& queryFieldLookupQueryResult.getQuery() != null) {
										W5QueryField field = new W5QueryField();
										field.setDsc(qf.getDsc() + "_qw_");
										errorFieldSet.add(field.getDsc());
										field.setMainTableFieldId(qf.getMainTableFieldId());
										if (queryResult.getPostProcessQueryFields() == null)
											queryResult.setPostProcessQueryFields(new ArrayList());
										queryResult.getPostProcessQueryFields().add(field);
										if (qf.getPostProcessTip() == 16
												&& queryFieldLookupQueryResult.getQuery().get_queryFields().size() > 2)
											for (int qi = 2; qi < queryFieldLookupQueryResult.getQuery()
													.get_queryFields().size(); qi++) {
												W5QueryField qf2 = queryFieldLookupQueryResult.getQuery()
														.get_queryFields().get(qi);
												field = new W5QueryField();
												field.setDsc(qf.getDsc() + "__" + qf2.getDsc());
												errorFieldSet.add(field.getDsc());
												field.setMainTableFieldId(qf2.getMainTableFieldId());
												queryResult.getPostProcessQueryFields().add(field);
											}
									}
								}

							sql2.append(" from (").append(queryResult.getExecutedSql()).append(" ) z");

						} else {
							sql2.append("select z.*");
							if (query.getSqlPostSelect() != null && query.getSqlPostSelect().trim().length() > 2) {
								if (query.getSqlPostSelect().contains("${")) {
									Object[] oz = DBUtil.filterExt4SQL(query.getSqlPostSelect(), queryResult.getScd(),
											queryResult.getRequestParams(), null);
									sql2.append(", ").append(oz[0]);
									if (oz[1] != null)
										queryResult.getSqlParams().addAll(0, (List) oz[1]);
								} else
									sql2.append(", ").append(query.getSqlPostSelect());
							}
							for (W5QueryField qf : queryResult.getQuery().get_queryFields())
								if (qf.getPostProcessTip() == 12 && qf.getLookupQueryId() != 0) { // queryField'da
																									// postProcessTip=lookupTable
																									// olanlar
									// saptaniyor
									prepareLookupTableQuery(queryResult, sql2, paramIndex);
									break;
								}
							for (W5QueryField qf : queryResult.getQuery().get_queryFields())
								if ((qf.getPostProcessTip() == 16) && qf.getLookupQueryId() != 0) { // queryField'da
																									// postProcessTip=lookupQuery
																									// olanlar
									// saptaniyor
									W5QueryResult queryFieldLookupQueryResult = metaDataDao.getQueryResult(queryResult.getScd(),
											qf.getLookupQueryId());
									if (queryFieldLookupQueryResult != null
											&& queryFieldLookupQueryResult.getQuery() != null) {
										W5QueryField field = new W5QueryField();
										field.setDsc(qf.getDsc() + "_qw_");
										errorFieldSet.add(field.getDsc());
										field.setMainTableFieldId(qf.getMainTableFieldId());
										if (queryResult.getPostProcessQueryFields() == null)
											queryResult.setPostProcessQueryFields(new ArrayList());
										queryResult.getPostProcessQueryFields().add(field);
										if (qf.getPostProcessTip() == 16
												&& queryFieldLookupQueryResult.getQuery().get_queryFields().size() > 2)
											for (int qi = 2; qi < queryFieldLookupQueryResult.getQuery()
													.get_queryFields().size(); qi++) {
												W5QueryField qf2 = queryFieldLookupQueryResult.getQuery()
														.get_queryFields().get(qi);
												field = new W5QueryField();
												field.setDsc(qf.getDsc() + "__" + qf2.getDsc());
												errorFieldSet.add(field.getDsc());
												field.setMainTableFieldId(qf2.getMainTableFieldId());
												queryResult.getPostProcessQueryFields().add(field);
											}
									}
								}

							sql2.append(" from (").append(queryResult.getExecutedSql()).append(" ) z");
						}
					}

					List<Object[]> resultData = queryResult.getFetchRowCount() == 0 ? new ArrayList<Object[]>()
							: new ArrayList<Object[]>(queryResult.getFetchRowCount());
					// sql = sql2.toString();
					s = conn.prepareStatement(sql2.toString());
					applyParameters(s, queryResult.getSqlParams());
					queryResult.setExecutedSql(sql2.toString());
					//
					// if(PromisSetting.debug)logger.info(PromisUtil.replaceSql(sql2.toString(),queryResult.getSqlParams()));
					rs = s.executeQuery();
					int maxTabOrder = 0;
					Set liveSyncKeys = null;
					if (FrameworkSetting.liveSyncRecord && mainTable != null && mainTable.getLiveSyncFlag() != 0
							&& FrameworkCache.isDevEntity("15." + mainTable.getTableId())
							&& queryResult.getRequestParams() != null && queryResult.getScd() != null
							&& queryResult.getRequestParams().containsKey(".t")
							&& queryResult.getRequestParams().containsKey(".w")) {
						int grdOrFcId = GenericUtil.uInt(queryResult.getRequestParams().get("_gid"));
						if (grdOrFcId == 0)
							grdOrFcId = -GenericUtil.uInt(queryResult.getRequestParams().get("_fdid"));
						if (grdOrFcId != 0) {
							boolean mobile = GenericUtil.uInt(queryResult.getScd().get("mobile")) != 0;
							String sessionId = mobile ? (String) queryResult.getScd().get("mobileDeviceId")
									: (String) queryResult.getScd().get("sessionId");
							liveSyncKeys = UserUtil.getTableGridFormCellCachedKeys(
									(String) queryResult.getScd().get("projectId"), mainTable.getTableId(),
									(Integer) queryResult.getScd().get("userId"), sessionId,
									mobile ? sessionId : queryResult.getRequestParams().get(".w"),
									queryResult.getRequestParams().get(".t"), grdOrFcId, queryResult.getRequestParams(),
									true);
							// if(liveSyncKeys!=null)liveSyncKeys.clear();
							// !queryResult.getScd().containsKey("mobile") ||
						}
					}
					List<W5QueryField> newQueryFields = null;
					while (rs.next() /*
										 * && (maxFetchedCount==0 ||
										 * totalFetchedCount<maxFetchedCount )
										 */) {
						if (newQueryFields == null) {
							newQueryFields = new ArrayList(queryResult.getQuery().get_queryFields().size()
									+ (queryResult.getPostProcessQueryFields() != null
											? queryResult.getPostProcessQueryFields().size() : 0));
							if (queryResult.getQueryColMap() != null) {
								for (W5QueryField qf : queryResult.getQuery().get_queryFields())
									if (queryResult.getQueryColMap().containsKey(qf.getDsc())) {
										newQueryFields.add(qf);
										if (maxTabOrder < qf.getTabOrder())
											maxTabOrder = qf.getTabOrder();
									}
							} else {
								for (W5QueryField qf : queryResult.getQuery().get_queryFields())
									if (qf.getTabOrder() > 0) {
										W5TableField tf = queryResult.getMainTable() != null
												&& qf.getMainTableFieldId() > 0
														? queryResult.getMainTable().get_tableFieldMap()
																.get(qf.getMainTableFieldId())
														: null;
										if (tf == null || ((GenericUtil.isEmpty(tf.getRelatedSessionField())
												|| GenericUtil.uInt(
														queryResult.getScd().get(tf.getRelatedSessionField())) != 0)
												&& (tf.getAccessViewUserFields() != null || GenericUtil.accessControl(
														queryResult.getScd(), tf.getAccessViewTip(),
														tf.getAccessViewRoles(), tf.getAccessViewUsers())))) { // access
																												// control
											newQueryFields.add(qf);
											if (maxTabOrder < qf.getTabOrder())
												maxTabOrder = qf.getTabOrder();
										}
									}
							}
							// if(!GenericUtil.isEmpty(tf.getRelatedSessionField())
							// &&
							// GenericUtil.uInt(formResult.getScd().get(tf.getRelatedSessionField()))==0)continue;

							// post process fields:comment, file attachment,
							// access_control, approval

							if (queryResult.getPostProcessQueryFields() != null) {
								for (W5QueryField qf : queryResult.getPostProcessQueryFields()) {
									qf.setTabOrder((short) (++maxTabOrder));
									newQueryFields.add(qf);
								}
							}
							if (queryResult.getQuery().getShowParentRecordFlag() != 0)
								maxTabOrder++;

							queryResult.setNewQueryFields(newQueryFields);
						}

						Object[] o = new Object[maxTabOrder];
						for (W5QueryField field : newQueryFields)
							if (!errorFieldSet.contains(field.getDsc()))
								try {
									Object obj = rs.getObject(field.getDsc());
									if (obj != null) {
										if (obj instanceof java.sql.Timestamp) {
											try {
												obj = (queryResult.getQuery().getQueryTip() == 2
														&& field.getFieldTip() == 2) ? (java.sql.Timestamp) obj
																: GenericUtil.uFormatDateTime((java.sql.Timestamp) obj);
											} catch (Exception e) {
												obj = "java.sql.Timestamp";
											}
										} else if (obj instanceof java.sql.Date) {
											try {
												obj = (queryResult.getQuery().getQueryTip() == 2
														&& field.getFieldTip() == 2) ? rs.getTimestamp(field.getDsc())
																: GenericUtil.uFormatDateTime(
																		rs.getTimestamp(field.getDsc()));
											} catch (Exception e) {
												obj = "java.sql.Date";
											}
										} else if (obj instanceof Boolean) {
											obj = (Boolean) obj ? 1 : 0;
										}
									}
									o[field.getTabOrder() - 1] = obj;
								} catch (Exception ez) {
									if (FrameworkSetting.debug)
										throw ez;
									errorFieldSet.add(field.getDsc());
								}
						if (query.getDataFillDirectionTip() != 0)
							resultData.add(0, o);
						else
							resultData.add(o);
						if (liveSyncKeys != null)
							liveSyncKeys.add(GenericUtil.uInt(o[query.getQueryTip() == 3 ? 1 : 0]));
					}
					if (queryResult.getFetchRowCount() == 0 && resultData != null) {
						queryResult.setResultRowCount(resultData.size());
					}
					queryResult.setData(resultData);

					if (rs != null)
						rs.close();
					if (s != null)
						s.close();
					if (FrameworkSetting.hibernateCloseAfterWork)
						if (conn != null)
							conn.close();
				}
			});
			// } catch(IWBException pe){error = pe.getMessage();throw pe;
		} catch (Exception e) {
			error = e.getMessage();
			throw new IWBException("sql", "Query", queryResult.getQueryId(),
					GenericUtil.replaceSql(sql2.length() == 0 ? queryResult.getExecutedSql() : sql2.toString(),
							queryResult.getSqlParams()),
					"[8," + queryResult.getQueryId() + "] " + queryResult.getQuery().getDsc(), e);
		} finally {
			logQueryAction(queryAction, queryResult, error);
		}
	}

	public Map<String, Object> runSQLQuery2Map(String code, Map<String, Object> scd, Map<String, String> requestParams,
			Map<String, Object> obj) {
		Object[] oz = DBUtil.filterExt4SQL(code, scd, requestParams, obj);
		return runSQLQuery2Map(oz[0].toString(), (List) oz[1], null);
	}

	public Map<String, Object> runSQLQuery2Map(String sql, List params, List<W5QueryField> queryFields) {
		return runSQLQuery2Map(sql, params, queryFields, true);
	}

	public List runQuery2Map(Map<String, Object> scd, int queryId, Map<String, String> requestParams) {
		W5QueryResult queryResult = metaDataDao.getQueryResult(scd, queryId);
		queryResult.setErrorMap(new HashMap());
		queryResult.setRequestParams(requestParams);
		
		if(queryResult.getQuery().getQuerySourceTip()==0) { //if JavaScript
			scriptEngine.executeQueryAsScript(queryResult, null);
			return queryResult.getData();
		}
		
		if (queryId != 1 && queryId != 824 && queryResult.getMainTable() != null && (!FrameworkSetting.debug
				|| (scd.get("roleId") != null && GenericUtil.uInt(scd.get("roleId")) != 0))) {
			W5Table t = queryResult.getMainTable();
			if (t.getAccessViewUserFields() == null && !GenericUtil.accessControl(scd, t.getAccessViewTip(),
					t.getAccessViewRoles(), t.getAccessViewUsers())) {
				throw new IWBException("security", "Query", queryId, null,
						LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_tablo_kontrol_goruntuleme"),
						null);
			}
		}
		/*
		 * StringBuilder tmpx = new StringBuilder(
		 * "ali baba ${obj.dsc} ve 40 haramiler ${lnk.pk_query_field_id.dsc} olmus"
		 * ); dao.interprateTemplate(scd, 5,1294, tmpx, true);
		 */
		queryResult.setViewLogModeTip((short) GenericUtil.uInt(requestParams, "_vlm"));

		//
		// queryResult.setOrderBy(PromisUtil.uStrNvl(requestParams.get(PromisUtil.uStrNvl(PromisSetting.appSettings.get("sql_sort"),"sort")),
		// queryResult.getQuery().getSqlOrderby()));
		if (!GenericUtil.isEmpty(requestParams.get("sort"))) {
			if (requestParams.get("sort").equals(FieldDefinitions.queryFieldName_Comment)) {
				// queryResult.setOrderBy("coalesce((select qz.last_comment_id
				// from iwb.w5_comment_summary
				// qz where qz.table_id= AND qz.table_pk= AND
				// qz.customization_id=),0) DESC");
				queryResult.setOrderBy(FieldDefinitions.queryFieldName_Comment); // +
																					// "
																					// "
																					// +
																					// requestParams.get("dir")
			} else if (!requestParams.get("sort").contains("_qw_")) {
				queryResult.setOrderBy(requestParams.get("sort"));
				if (requestParams.get("dir") != null) {
					if (queryResult.getMainTable() != null)
						for (W5QueryField f : queryResult.getQuery().get_queryFields())
							if (queryResult.getOrderBy().equals(f.getDsc())) {
								if (f.getMainTableFieldId() != 0 && queryResult.getMainTable().get_tableFieldMap()
										.containsKey(f.getMainTableFieldId())) {
									queryResult.setOrderBy("x." + queryResult.getOrderBy());
								}
								break;
							}
					// queryResult.setOrderBy(((!queryResult.getQuery().getSqlFrom().contains(",")
					// &&
					// !queryResult.getQuery().getSqlFrom().contains("join") &&
					// queryResult.getQuery().getSqlFrom().contains(" x")) ?
					// "x." : "") +
					// queryResult.getOrderBy() + " " +
					// requestParams.get("dir"));
					queryResult.setOrderBy(queryResult.getOrderBy() + " " + requestParams.get("dir"));
				}
			} else
				queryResult.setOrderBy(queryResult.getQuery().getSqlOrderby());
		} else
			queryResult.setOrderBy(queryResult.getQuery().getSqlOrderby());
		switch (queryResult.getQuery().getQueryTip()) {
		case 9:
		case 10:
			queryResult.prepareTreeQuery(null);
			break;
		case 15:
			queryResult.prepareDataViewQuery(null);
			break;
		default:
			queryResult.prepareQuery(null);
		}
		List l = null;
		if (queryResult.getErrorMap().isEmpty()) {
			QueryTrigger.beforeExecuteQuery(queryResult, this);
			queryResult.setFetchRowCount(
					GenericUtil.uIntNvl(requestParams, "limit", GenericUtil.uInt(requestParams, "firstLimit")));
			queryResult.setStartRowNumber(GenericUtil.uInt(requestParams, "start"));
			l = executeSQLQuery2Map(queryResult.getExecutedSql(), queryResult.getSqlParams());
			QueryTrigger.afterExecuteQuery(queryResult, this);
		}

		return l;
	}

	public Map<String, Object> runSQLQuery2Map(final String sql, final List params,
			final List<W5QueryField> queryFields, final boolean closeConnectionAfterRun) {
		try {
			return getCurrentSession().doReturningWork(new ReturningWork<Map<String, Object>>() {

				public Map<String, Object> execute(Connection conn) throws SQLException {
					PreparedStatement s = conn.prepareStatement("select x.* from (" + sql + ")  x  limit 1");
					if (params != null)
						applyParameters(s, params);
					ResultSet rs = s.executeQuery();
					Map<String, Object> result = null;
					if (rs.next()) {
						result = new HashMap<String, Object>();
						if (queryFields == null || queryFields.size() == 0) {
							ResultSetMetaData rsm = rs.getMetaData();
							for (int columnIndex = 1; columnIndex <= rsm.getColumnCount(); columnIndex++) {
								String columnName = rsm.getColumnName(columnIndex)
										.toLowerCase(FrameworkSetting.appLocale);
								Object obj = rs.getObject(columnIndex);
								if (obj == null)
									continue;
								if (obj instanceof java.sql.Timestamp) {
									try {
										result.put(columnName, GenericUtil.uFormatDateTime((java.sql.Timestamp) obj));
									} catch (Exception e) {
									}
								} else if (obj instanceof java.sql.Date) {
									try {
										obj = rs.getTimestamp(columnIndex);
										result.put(columnName, GenericUtil.uFormatDateTime((java.sql.Timestamp) obj));
									} catch (Exception e) {
										obj = rs.getObject(columnIndex);
										try {
											result.put(columnName, GenericUtil.uFormatDateSade((java.sql.Date) obj));
										} catch (Exception e2) {
										}
									}
								} else
									result.put(columnName, obj);
							}
						} else
							for (W5QueryField f : queryFields) {
								Object obj = rs.getObject(f.getDsc());
								if (obj == null)
									continue;
								String columnName = f.getDsc();
								if (obj instanceof java.sql.Timestamp) {
									try {
										result.put(columnName,
												GenericUtil.uFormatDateTimeSade((java.sql.Timestamp) obj));
									} catch (Exception e) {
									}
								} else if (obj instanceof java.sql.Date) {
									try {
										result.put(columnName, GenericUtil.uFormatDateSade((java.sql.Date) obj));
									} catch (Exception e) {
									}
								} else {
									Object o = GenericUtil.getObjectByTip(obj.toString(), f.getFieldTip());
									switch (f.getPostProcessTip()) {
									case 9: // _fieldName
										result.put("_" + columnName, o);
										break;
									case 2: // locale
										result.put(columnName, LocaleMsgCache.get2(0,
												FrameworkCache.getAppSettingStringValue(0, "locale"), (String) o)); // TODO.
																													// burasi
																													// scd'deki
																													// degerler
																													// olacak
										break;
									default:
										result.put(columnName, o);
									}
								}
							}
					}
					if (rs != null)
						rs.close();
					if (s != null)
						s.close();
					if (FrameworkSetting.hibernateCloseAfterWork)
						if (closeConnectionAfterRun && conn != null)
							conn.close();
					return result;
				}
			});
			// s = conn.prepareStatement("select x.* from ("+sql+") x where
			// rownum=1");

		} catch (Exception e) {
			// if(FrameworkSetting.debug)e.printStackTrace();
			// logException(PromisUtil.replaceSql(sql, params)+"\n"+
			// e.getMessage(),PromisUtil.uInt(PromisCache.appSettings.get(0).get("default_customization_id")),0);
			throw new IWBException("sql", "Custom.Query.Map", 0, GenericUtil.replaceSql(sql, params), "Error Executing",
					e);
		}
	}

	private void logQueryAction(Log5QueryAction action, W5QueryResult queryResult, String error) {
		if (queryResult.getQuery() == null || !FrameworkSetting.logQueryAction)
			return;
		action.calcProcessTime();

		queryResult.setProcessTime(action.getProcessTime());
		if (queryResult.getQuery().getLogLevelTip() == 2 || (queryResult.getQuery().getLogLevelTip() == 1
				&& FrameworkSetting.logQueryActionMinTime <= action.getProcessTime())) {
			if (FrameworkSetting.log2tsdb) {
				action.setDsc(GenericUtil.replaceSql(queryResult.getExecutedSql(), queryResult.getSqlParams()));
			} else {
				action.setDsc(GenericUtil.uStrMax(
						GenericUtil.replaceSql(queryResult.getExecutedSql(), queryResult.getSqlParams()), 3999));
			}
			saveObject(action);
		}
	}

	public void logGlobalFuncAction(Log5GlobalFuncAction action, W5GlobalFuncResult fr, String error) {
		if (fr.getGlobalFunc().getLogLevelTip() == 0
				|| FrameworkCache.getAppSettingIntValue(fr.getScd(), "log_db_func_action") == 0)
			return;
		action.calcProcessTime();
		if ((fr.getGlobalFunc().getLogLevelTip() == 1) || (fr.getGlobalFunc().getLogLevelTip() == 2
				&& FrameworkCache.getAppSettingIntValue(fr.getScd(), "log_db_func_action") != 0 && FrameworkCache
						.getAppSettingIntValue(fr.getScd(), "log_db_func_action_mintime") <= action.getProcessTime())) {
			action.setDsc(GenericUtil.replaceSql(fr.getExecutedSql(), fr.getSqlParams()));
			saveObject(action);
		}
	}

	public void loadFormCellLookups(Map<String, Object> scd, List<W5FormCellHelper> formCellResults,
			Map<String, String> requestParams, String tabId) {
		String includedValues;
		String projectId = (String) scd.get("projectId");
		// W5Customization cus =
		// FrameworkCache.wCustomizationMap.get(customizationId);

		for (W5FormCellHelper rc : formCellResults)
			try {
				W5FormCell c = rc.getFormCell();
				if(c.getActiveFlag()==0)continue;
				includedValues = c.getLookupIncludedValues();
				Map<String, String> paramMap = new HashMap<String, String>();
				Set<Integer> keys = null;
				switch (c.getControlTip()) {
				case 100: // button
					if (c.getInitialSourceTip() == 4) { // sql ise
						rc.setExtraValuesMap(runSQLQuery2Map(
								GenericUtil.filterExt(c.getInitialValue(), scd, requestParams, null).toString(), null,
								null));
					}
					break;
				case 60: // remote superboxselect
				case 16: // remote query
				case 9: // remote query
					rc.setLookupQueryResult(metaDataDao.getQueryResult(scd, c.getLookupQueryId()));
					// c.set_lookupListCount(c.getLookupQueryId()); // Fake:
					// Normalde Query Id tutulur, ama
					// su anda kac adet column tutuyor
					break;

				case 58: // superboxselect
				case 8: // lovcombo static
				case 6: // eger static combobox ise listeyi load et
					W5LookUp lookUp = FrameworkCache.getLookUp(scd, c.getLookupQueryId(), "Form(" + c.getFormId() + ")."
							+ c.getDsc() + "-> LookUp not found: " + c.getLookupQueryId());
					rc.setLocaleMsgFlag((short) 1);
					List<W5LookUpDetay> oldList = !FrameworkCache.hasQueuedReloadCache(projectId,
							"13." + lookUp.getLookUpId())
									? lookUp.get_detayList()
									: (List<W5LookUpDetay>) find(
											"from W5LookUpDetay t where t.projectUuid=? AND t.lookUpId=? order by t.tabOrder",
											projectId, c.getLookupQueryId());

					List<W5LookUpDetay> newList = null;
					if (includedValues != null && includedValues.length() > 0) {
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
					} else if (requestParams.get("_lsc" + c.getFormCellId()) != null) {
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
					if (tabId != null)
						keys = UserUtil.getTableGridFormCellCachedKeys((String) scd.get("projectId"),
								-c.getLookupQueryId(), (Integer) scd.get("userId"), (String) scd.get("sessionId"),
								requestParams.get(".w"), tabId, -c.getFormCellId(), requestParams, false);
					for (W5LookUpDetay ld : newList) {
						newList2.add(ld);
					}
					rc.setLookupListValues(newList2);
					break;
				case 10:
				case 61: // advanced select, advancedselect w/ button
					paramMap.put("xid", rc.getValue());
				case 7:
				case 15:
				case 59: // dynamic query, lovcombo, superbox
				case 23:
				case 24:
				case 26:
				case 55: // tree combo and treepanel
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
					if (lookupQueryResult.getQuery().getQuerySourceTip() != 15)
						switch (lookupQueryResult.getQuery().getQuerySourceTip()) {
						case 1376: // WS Method
							W5WsMethod wsm = FrameworkCache.getWsMethod(projectId,
									lookupQueryResult.getQuery().getMainTableId());
							/*if (wsm.get_params() == null) {
								wsm.set_params(
										find("from W5WsMethodParam t where t.wsMethodId=? AND t.projectUuid=? order by t.tabOrder",
												wsm.getWsMethodId(), projectId));
								wsm.set_paramMap(new HashMap());
								for (W5WsMethodParam wsmp : wsm.get_params())
									wsm.get_paramMap().put(wsmp.getWsMethodParamId(), wsmp);
							}*/
							W5WsMethodParam parentParam = null;
							for (W5WsMethodParam px : wsm.get_params())
								if (px.getOutFlag() != 0 && px.getParamTip() == 10) {
									parentParam = px;
									break;
								}
							Map<String, String> m2 = new HashMap();
							if (requestParams.get("filter[value]") != null) {
								requestParams.put("xdsc", requestParams.get("filter[value]"));
								requestParams.remove("filter[value]");
							}
							for (W5QueryParam qp : lookupQueryResult.getQuery().get_queryParams())
								if (!GenericUtil.isEmpty(requestParams.get(qp.getDsc()))) {
									m2.put(qp.getExpressionDsc(), requestParams.get(qp.getDsc()));
								}
							StringBuilder rc2 = new StringBuilder();
							rc2.append("function _x_(x){\nreturn {").append(lookupQueryResult.getQuery().getSqlSelect())
									.append("\n}}\nvar result=[], q=$.REST('")
									.append(wsm.get_ws().getDsc() + "." + wsm.getDsc()).append("',")
									.append(GenericUtil.fromMapToJsonString2(m2))
									.append(");\nif(q && q.get('success')){q=q.get('").append(parentParam.getDsc())
									.append("');for(var i=0;i<q.size();i++)result.push(_x_(q.get(i)));}");
							scriptEngine.executeQueryAsScript(lookupQueryResult, rc2.toString());
							rc.setLookupQueryResult(lookupQueryResult);
							continue;
						default:
							rc.setLookupQueryResult(lookupQueryResult);
							continue; // burda sadece table icin olur
						}
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
					if (c.getControlTip() == 10 || c.getControlTip() == 23 || c.getControlTip() == 7) {
						if (c.getDialogGridId() != 0) {
							if (rc.getExtraValuesMap() == null)
								rc.setExtraValuesMap(new HashMap());
							rc.getExtraValuesMap().put("dialogGrid",
									metaDataDao.getGridResult(scd, c.getDialogGridId(), requestParams, true));
						}

						if (c.getControlTip() == 10 && GenericUtil.isEmpty(rc.getValue()))
							break; // advanced select ise ve degeri yoksa
									// hicbirsey koyma
					}

					if (lookupQueryResult.getErrorMap().isEmpty()) {
						runQuery(lookupQueryResult);
						if (tabId != null && lookupQueryResult.getQuery().getMainTableId() != 0
								&& requestParams.get(".w") != null) {
							keys = UserUtil.getTableGridFormCellCachedKeys((String) scd.get("projectId"),
									lookupQueryResult.getQuery().getMainTableId(), (Integer) scd.get("userId"),
									(String) scd.get("sessionId"), requestParams.get(".w"), tabId, -c.getFormCellId(),
									requestParams, true);
							if (keys != null)
								for (Object[] o : lookupQueryResult.getData())
									keys.add(GenericUtil.uInt(o[1]));
						}
						if (paramMap.get("xapproval_id") != null && c.getLookupQueryId() == 606) { // onaylanmis
																									// ve
																									// reddedildiyi
																									// koy
							W5Workflow ta = FrameworkCache.getWorkflow(projectId,
									GenericUtil.uInt(paramMap.get("xapproval_id")));
							if (ta.getApprovalRequestTip() != 1) { // gercek
																	// veri
																	// uzerinde?
								lookupQueryResult
										.getData().add(0,
												new Object[] {
														LocaleMsgCache.get2((Integer) scd.get("customizationId"),
																(String) scd.get("locale"), ta.getApprovalRequestMsg()),
												901 });
							}
							if (ta.getOnRejectTip() == 1) { // make status
															// rejected
								lookupQueryResult
										.getData().add(
												new Object[] {
														LocaleMsgCache.get2((Integer) scd.get("customizationId"),
																(String) scd.get("locale"), ta.getRejectedMsg()),
												999 });
							}
						}
					}
					// paramMap.clear();
				}
			} catch (Exception e) {
				throw new IWBException("framework", "Load.FormElement", rc.getFormCell().getFormCellId(), null,
						"[41," + rc.getFormCell().getFormCellId() + "]", e);
			}
	}

	public void loadFormTable(final W5FormResult formResult) {
		W5Form f = formResult.getForm();
		final W5Table t = FrameworkCache.getTable(formResult.getScd(), f.getObjectId());
		final StringBuilder sql = new StringBuilder();
		sql.append("select ");
		String log5LogId = null; // logdan gosterilecekse
		if (formResult.getRequestParams() != null && formResult.getRequestParams().containsKey("_log5_log_id")) {
			log5LogId = formResult.getRequestParams().get("_log5_log_id");
		}
		if (formResult.getFormCellResults() == null)
			formResult.setFormCellResults(new ArrayList<W5FormCellHelper>(formResult.getForm().get_formCells().size()));
		for (W5FormCell cell : formResult.getForm().get_formCells()) {
			if (cell.getObjectDetailId() != 0) {
				W5TableField tf = ((W5TableField) cell.get_sourceObjectDetail());
				if (tf != null && tf.getTabOrder() > 0) {
					if (tf.getAccessViewTip() != 0 && tf.getAccessViewUserFields() != null) {
						if ((!GenericUtil.accessControl(formResult.getScd(), tf.getAccessViewTip(),
								tf.getAccessViewRoles(), tf.getAccessViewUsers())
								&& (!GenericUtil.isEmpty(tf.getAccessViewUserFields())
										&& accessUserFieldControl(t, tf.getAccessViewUserFields(), formResult.getScd(),
												formResult.getRequestParams(), null))))
							continue;
					}
					if (!GenericUtil.isEmpty(tf.getRelatedSessionField())
							&& GenericUtil.uInt(formResult.getScd().get(tf.getRelatedSessionField())) == 0)
						continue;

					W5FormCellHelper result = new W5FormCellHelper(cell);
					formResult.getFormCellResults().add(result);
					if (log5LogId != null) {
						sql.append("t.").append(((W5TableField) cell.get_sourceObjectDetail()).getDsc()).append(",");
					} else {
						sql.append("t.").append(((W5TableField) cell.get_sourceObjectDetail()).getDsc()).append(",");
					}
				}
			} else {
				W5FormCellHelper result = new W5FormCellHelper(cell);
				formResult.getFormCellResults().add(result);
			}
		}
		sql.replace(sql.length() - 1, sql.length(), " from ");
		if (log5LogId != null) {
			sql.append(FrameworkSetting.crudLogSchema).append(".");
		}
		sql.append(t.getDsc()).append(" t");
		boolean b = false;
		sql.append(" where ");
		final List<Object> realParams = new ArrayList<Object>();
		Object pkField = null;
		if (log5LogId != null) {
			sql.append(" t.log5_log_id = ? ");
			realParams.add(GenericUtil.uInt(log5LogId));
			formResult.getPkFields().put("log5_log_id", log5LogId);
		} else {

			for (W5TableParam x : t.get_tableParamList()) {
				if (b) {
					sql.append(" AND ");
				} else
					b = true;
				sql.append("t.").append(x.getExpressionDsc()).append(" = ? ");
				Object psonuc = GenericUtil.prepareParam((W5Param) x, formResult.getScd(),
						formResult.getRequestParams(), (short) -1, null, (short) 1, null, null,
						formResult.getErrorMap());
				if (pkField == null)
					pkField = psonuc;
				realParams.add(psonuc);
				formResult.getPkFields().put(x.getDsc(), psonuc);
			}
		}

		final Object pkField2 = pkField;
		try {
			getCurrentSession().doWork(new Work() {

				public void execute(Connection conn) throws SQLException {
					PreparedStatement s = conn.prepareStatement(sql.toString());
					applyParameters(s, realParams);
					ResultSet rs = s.executeQuery();
					if (!rs.next())
						throw new IWBException("sql", "Form.Load", formResult.getFormId(),
								GenericUtil.replaceSql(sql.toString(), realParams), "No Record Found", null);

					for (W5FormCellHelper cellResult : formResult.getFormCellResults())
						if (cellResult.getFormCell().getObjectDetailId() != 0) {
							W5TableField tf = (W5TableField) cellResult.getFormCell().get_sourceObjectDetail();
							Object obj = rs.getObject((tf).getDsc());
							if (obj != null) {
								if (tf.getFieldTip() == 5 && obj instanceof Boolean) {
									obj = (Boolean) obj ? 1 : 0;
								} else if (tf.getFieldTip() == 2 && obj instanceof java.sql.Timestamp) {
									try {
										obj = GenericUtil.uFormatDateTime((java.sql.Timestamp) obj);
									} catch (Exception e) {
									}
								} else if (tf.getFieldTip() == 2 && obj instanceof java.sql.Date) {
									try {
										if (cellResult.getFormCell().getControlTip() == 18) { // date
																								// time
											obj = rs.getTimestamp(
													((W5TableField) cellResult.getFormCell().get_sourceObjectDetail())
															.getDsc());
											obj = GenericUtil.uFormatDateTime((java.sql.Timestamp) obj);
										} else // date
											obj = GenericUtil.uFormatDate((java.util.Date) obj);
									} catch (Exception e) {
									}
								}
								cellResult.setValue(obj.toString());
							}
						} else if (cellResult.getFormCell().getControlTip() == 101) {
							switch (cellResult.getFormCell().getInitialSourceTip()) {
							case 0: // yok-sabit
								cellResult.setValue(cellResult.getFormCell().getInitialValue());
								break;
							case 1: // request
								cellResult.setValue(
										formResult.getRequestParams().get(cellResult.getFormCell().getInitialValue()));
								break;
							case 2:
								Object o = formResult.getScd().get(cellResult.getFormCell().getInitialValue());
								cellResult.setValue(o == null ? null : o.toString());
								break;
							case 3: // app_setting
								cellResult.setValue(FrameworkCache.getAppSettingStringValue(formResult.getScd(),
										cellResult.getFormCell().getInitialValue()));
								break;
							case 4: // SQL
								Object[] oz = DBUtil.filterExt4SQL(cellResult.getFormCell().getInitialValue(),
										formResult.getScd(), formResult.getRequestParams(), null);
								if (oz[1] != null && ((List) oz[1]).size() > 0) {
									Map<String, Object> m = runSQLQuery2Map(oz[0].toString(), (List) oz[1], null,
											false);
									if (m != null)
										cellResult.setValue(m.values().iterator().next().toString());
								} else {
									List l = executeSQLQuery(oz[0].toString());
									if (l != null && l.size() > 0 && l.get(0) != null)
										cellResult.setValue(l.get(0).toString());
								}
								if (GenericUtil.isEmpty(cellResult.getValue()))
									cellResult.setValue(" ");
								break;
							case 5: // CustomJS(Rhino)
								Object res = scriptEngine.executeScript(formResult.getScd(), formResult.getRequestParams(), cellResult.getFormCell().getDefaultValue(), null, "41d"+cellResult.getFormCell().getFormCellId() );
							
//									if (res != null && res instanceof org.mozilla.javascript.Undefined)res = null;
								if (res != null && ((W5Param) cellResult.getFormCell().get_sourceObjectDetail())
										.getParamTip() == 4)
									res = "" + new BigDecimal(res.toString()).intValue();
								cellResult.setValue(res == null ? null : res.toString());

								break;
							}
						}
					if (rs.next()) // fazladan kayit geldi
						throw new IWBException("sql", "Table", t.getTableId(),
								GenericUtil.replaceSql(sql.toString(), realParams),
								"[15," + t.getTableId() + "] Loaded more then 1 record", null);
					if (rs != null)
						rs.close();
					if (s != null)
						s.close();

					if (pkField2 != null) {
						int extraSqlCount = 0;
						StringBuilder extraSql = new StringBuilder();
						extraSql.append("select ");
						Set<String> extrInfoSet = new HashSet();
						if (FrameworkCache.getAppSettingIntValue(formResult.getScd(), "file_attachment_flag") != 0
								&& t.getFileAttachmentFlag() != 0) {
							extraSql.append(
									"(select count(1) cnt from iwb.w5_file_attachment x where x.customization_id=? AND x.table_id=? AND x.table_pk=?::text) file_attach_count");
							extrInfoSet.add("file_attach_count");
							extraSqlCount++;
						}
						if (FrameworkCache.getAppSettingIntValue(formResult.getScd(), "make_comment_flag") != 0
								&& t.getMakeCommentFlag() != 0) {
							if (extraSql.length() > 10)
								extraSql.append(",");
							if (FrameworkCache.getAppSettingIntValue(formResult.getScd(),
									"make_comment_summary_flag") != 0) {
								extraSql.append(
										"(select cx.comment_count||';'||cxx.comment_user_id||';'||to_char(cxx.comment_dttm,'dd/mm/yyyy hh24:mi:ss')||';'||cx.view_user_ids||'-'||cxx.dsc from iwb.w5_comment_summary cx, iwb.w5_comment cxx where cx.customization_id=? AND cx.table_id=? AND cx.table_pk=?::text AND cxx.customization_id=cx.customization_id AND cxx.comment_id=cx.last_comment_id) comment_extra");
								extrInfoSet.add("comment_extra");
								extraSqlCount++;
							} else {
								extraSql.append(
										"(select count(1) cnt from iwb.w5_comment x where x.project_uuid=? AND x.table_id=? AND x.table_pk=?) comment_count");
								extrInfoSet.add("comment_count");
								extraSqlCount++;
							}
						}
						/*
						 * if(FrameworkCache.getAppSettingIntValue(formResult.
						 * getScd(), "row_based_security_flag")!=0 &&
						 * (Integer)formResult.getScd().get("userTip")!=3 &&
						 * t.getAccessTips()!=null &&
						 * t.getAccessTips().length()>0){
						 * if(extraSql.length()>10)extraSql.append(",");
						 * extraSql.append(
						 * "(select count(1) cnt from iwb.w5_access_control x where x.customization_id=? AND x.table_id=? AND x.table_pk=?) access_count"
						 * ); extrInfoSet.add("access_count"); extraSqlCount++;
						 * }
						 */

						if (extraSql.length() > 10) {
							s = conn.prepareStatement(extraSql.append(" ").toString());
							List<Object> params = new ArrayList(extraSqlCount * 3 + 1);
							for (int qi = 0; qi < extraSqlCount; qi++) {
								params.add(formResult.getScd().get("customizationId"));
								params.add(t.getTableId());
								params.add(pkField2);
							}
							applyParameters(s, params); // romisUtil.replaceSql(extraSql.toString(),params)
							rs = s.executeQuery();
							if (rs.next()) {
								if (extrInfoSet.contains("file_attach_count"))
									formResult.setFileAttachmentCount(
											GenericUtil.uInt(rs.getObject("file_attach_count")));
								if (extrInfoSet.contains("comment_count")) {
									formResult.setCommentCount(GenericUtil.uInt(rs.getObject("comment_count")));
								} else if (extrInfoSet.contains("comment_extra")) {
									formResult.setCommentExtraInfo((String) rs.getObject("comment_extra"));
								}
								// if(extrInfoSet.contains("access_count"))formResult.setAccessControlCount(GenericUtil.uInt(rs.getObject("access_count")));
							}
							if (rs != null)
								rs.close();
							if (s != null)
								s.close();
						}
					}
					if (FrameworkSetting.hibernateCloseAfterWork)
						if (conn != null)
							conn.close();
				}
			});

		} catch (Exception e) {
			throw new IWBException("framework", "FetchRecord.Form", formResult.getFormId(), null,
					"[40," + formResult.getFormId() + "] Form Load Exception", e);
		} finally {
			// session.close();
		}
	}

	public String getInitialFormCellValue(Map<String, Object> scd, W5FormCell cell, Map<String, String> requestParams) {
		String result = null;
		switch (cell.getInitialSourceTip()) {
		case 0: // yok-sabit
			result = (cell.getInitialValue());
			break;
		case 1: // request
			result = (requestParams.get(cell.getInitialValue()));
			break;
		case 2:
			Object o = scd.get(cell.getInitialValue());
			result = (o == null ? null : o.toString());
			break;
		case 3: // app_setting
			result = (FrameworkCache.getAppSettingStringValue(scd, cell.getInitialValue()));
			break;
		case 4: // SQL
			Object[] oz = DBUtil.filterExt4SQL(cell.getInitialValue(), scd, requestParams, null);
			Map<String, Object> m = runSQLQuery2Map(oz[0].toString(), (List) oz[1], null);
			if (m != null)
				result = (m.values().iterator().next().toString());
			break;
		case 5: // CustomJS(Rhino)
			Object res = scriptEngine.executeScript(scd, requestParams, cell.getInitialValue(), null, "41i"+cell.getFormCellId() );

			if (res != null && ((W5Param) cell.get_sourceObjectDetail()).getParamTip() == 4)
				res = "" + new BigDecimal(res.toString()).intValue();
			result = (res == null ? null : res.toString());

			break;
		case 10: // approvalStates
			String selectedItems = "";
			W5Workflow app = FrameworkCache.getWorkflow(scd, GenericUtil.uInt(cell.getInitialValue()));
			for (W5WorkflowStep step : app.get_approvalStepList())
				if (GenericUtil.accessControl(scd, (short) 1, step.getApprovalRoles(), step.getApprovalUsers()))
					selectedItems += "," + step.getApprovalStepId();
			if (selectedItems.length() > 0)
				result = (selectedItems.substring(1));
		}
		return result;
	}

	public Map<String, String> interprateSmsTemplate(W5FormSmsMail fsm, Map<String, Object> scd,
			Map<String, String> requestParams, int fsmTableId, int fsmTablePk) {
		Map<String, String> m = new HashMap<String, String>();
		String phone = fsm.getSmsMailTo();
		if (phone != null && phone.contains("${")) {
			StringBuilder tmp1 = new StringBuilder(phone);
			interprateTemplate(scd, requestParams, fsmTableId, fsmTablePk, tmp1, true, 1, 0);
			phone = tmp1.toString();
		}
		m.put("phone", phone);
		String smsBody = fsm.getSmsMailBody();
		if (smsBody != null && smsBody.contains("${")) {
			StringBuilder tmp1 = new StringBuilder(smsBody);
			interprateTemplate(scd, requestParams, fsmTableId, fsmTablePk, tmp1, true, 0, 0);
			smsBody = tmp1.toString();
		}
		m.put("body", smsBody);
		return m;
	}

	public W5Email interprateMailTemplate(W5FormSmsMail fsm, Map<String, Object> scd, Map<String, String> requestParams,
			int fsmTableId, int fsmTablePk) {

		W5Email email = new W5Email();
		String mailTo = fsm.getSmsMailTo();
		if (mailTo != null && mailTo.contains("${")) {
			StringBuilder tmp1 = new StringBuilder(mailTo);
			interprateTemplate(scd, requestParams, fsmTableId, fsmTablePk, tmp1, true, 2, 0);
			mailTo = MailUtil.organizeMailAdress(tmp1.toString());
		}
		email.setMailTo(mailTo);
		String mailCc = fsm.getSmsMailCc();
		if (mailCc != null && mailCc.contains("${")) {
			StringBuilder tmp1 = new StringBuilder(mailCc);
			interprateTemplate(scd, requestParams, fsmTableId, fsmTablePk, tmp1, true, 2, 0);
			mailCc = MailUtil.organizeMailAdress(tmp1.toString());
		}
		email.setMailCc(mailCc);
		String mailBcc = fsm.getSmsMailBcc();
		if (mailBcc != null && mailBcc.contains("${")) {
			StringBuilder tmp1 = new StringBuilder(mailBcc);
			interprateTemplate(scd, requestParams, fsmTableId, fsmTablePk, tmp1, true, 2, 0);
			mailBcc = MailUtil.organizeMailAdress(tmp1.toString());
		}
		email.setMailBcc(mailBcc);
		String mailSubject = fsm.getSmsMailSubject();
		if (mailSubject != null && mailSubject.contains("${")) {
			StringBuilder tmp1 = new StringBuilder(mailSubject);
			interprateTemplate(scd, requestParams, fsmTableId, fsmTablePk, tmp1, true, 0, 0);
			mailSubject = tmp1.toString();
		}
		email.setMailSubject(mailSubject);
		String mailBody = fsm.getSmsMailBody();
		if (mailBody != null && mailBody.contains("${")) {
			StringBuilder tmp1 = new StringBuilder(mailBody);
			interprateTemplate(scd, requestParams, fsmTableId, fsmTablePk, tmp1, true, 0, 0);
			mailBody = tmp1.toString();
		}
		email.setMailBody(mailBody);
		return email;
	}

	public void initializeForm(W5FormResult formResult, boolean onlyFreeFields) {
		W5Form form = formResult.getForm();
		String projectId = FrameworkCache.getProjectId(formResult.getScd(), "40." + formResult.getFormId());
		W5Table t = null;
		switch (form.getObjectTip()) {
		case 2://table
			t = FrameworkCache.getTable(projectId, form.getObjectId());
			break; // table
		case 1://grid
			W5Grid g = metaDataDao.getGridResult(formResult.getScd(), form.getObjectId(), new HashMap(), true).getGrid();

			if (g != null) {
				W5Query q = metaDataDao.getQueryResult(formResult.getScd(), g.getQueryId()).getQuery();
				if (q != null)
					t = FrameworkCache.getTable(projectId, q.getMainTableId()); // grid
			}
			break;
		// case 3: t= PromisCache.getTable(formResult.getScd(),
		// form.getObjectId()); break;
		}
		if (formResult.getFormCellResults() == null)
			formResult.setFormCellResults(new ArrayList<W5FormCellHelper>(form.get_formCells().size()));
		if (form.get_formCells() != null)
			formResult.getExtraFormCells().addAll(0, form.get_formCells());
		for (W5FormCell cell : formResult.getExtraFormCells())
			if (!onlyFreeFields || cell.getObjectDetailId() == 0)
				try {
					if (t != null) {
						W5TableField tf = null;
						if (form.getObjectTip() == 2 && cell.get_sourceObjectDetail() != null
								&& cell.get_sourceObjectDetail() instanceof W5TableField) {
							tf = (W5TableField) cell.get_sourceObjectDetail();
						} else if (form.getObjectTip() == 1 && cell.get_sourceObjectDetail() != null) {
							if (cell.get_sourceObjectDetail() instanceof W5QueryParam) {
								W5QueryParam qp = (W5QueryParam) cell.get_sourceObjectDetail();
								if (qp.getRelatedTableFieldId() != 0 && t != null) {
									tf = t.get_tableFieldMap().get(qp.getRelatedTableFieldId());
								}
							} else if (cell.get_sourceObjectDetail() instanceof W5TableField) {
								tf = (W5TableField) cell.get_sourceObjectDetail();
							}
						}
						if (tf != null) {
							if (!GenericUtil.accessControl(formResult.getScd(), tf.getAccessInsertTip(),
									tf.getAccessInsertRoles(), tf.getAccessInsertUsers()))
								continue; // access control
							if (!GenericUtil.isEmpty(tf.getRelatedSessionField())
									&& GenericUtil.uInt(formResult.getScd().get(tf.getRelatedSessionField())) == 0)
								continue;
						}
					}
					W5FormCellHelper result = new W5FormCellHelper(cell);
					switch (cell.getInitialSourceTip()) {
					case 0: // yok-sabit
						result.setValue(cell.getInitialValue());
						break;
					case 1: // request
						result.setValue(formResult.getRequestParams().get(cell.getInitialValue()));
						break;
					case 2:
						Object o = formResult.getScd().get(cell.getInitialValue());
						result.setValue(o == null ? null : o.toString());
						break;
					case 3: // app_setting
						result.setValue(
								FrameworkCache.getAppSettingStringValue(formResult.getScd(), cell.getInitialValue()));
						break;
					case 4: // SQL
						Object[] oz = DBUtil.filterExt4SQL(cell.getInitialValue(), formResult.getScd(),
								formResult.getRequestParams(), null);
						if (oz[1] != null && ((List) oz[1]).size() > 0) {
							Map<String, Object> m = runSQLQuery2Map(oz[0].toString(), (List) oz[1], null);
							if (m != null && m.size() > 0)
								result.setValue(m.values().iterator().next().toString());
						} else {
							List l = executeSQLQuery(oz[0].toString());
							if (l != null && l.size() > 0 && l.get(0) != null)
								result.setValue(l.get(0).toString());
						}
						break;
					case 5: // CustomJS(Rhino)
						Object res = scriptEngine.executeScript(formResult.getScd(), formResult.getRequestParams(), cell.getInitialValue(), null, "41i"+cell.getFormCellId() );

						if (res != null && ((W5Param) cell.get_sourceObjectDetail()).getParamTip() == 4)
							res = "" + new BigDecimal(res.toString()).intValue();
						result.setValue(res == null ? null : res.toString());

						break;
					case 10: // approvalStates
						String selectedItems = "";
						W5Workflow app = FrameworkCache.getWorkflow(projectId,
								GenericUtil.uInt(cell.getInitialValue()));
						if (app != null)
							for (W5WorkflowStep step : app.get_approvalStepList())
								if (GenericUtil.accessControl(formResult.getScd(), (short) 1, step.getApprovalRoles(),
										step.getApprovalUsers()))
									selectedItems += "," + step.getApprovalStepId();
						if (selectedItems.length() > 0)
							result.setValue(selectedItems.substring(1));
					}
					formResult.getFormCellResults().add(result);
				} catch (Exception e) {
					throw new IWBException("framework", "Initialize FormElement", cell.getFormCellId(), null,
							"[41," + cell.getFormCellId() + "] " + cell.getDsc(), e);
				}
	}


	public boolean updateFormTable(final W5FormResult formResult, final String paramSuffix) {
		Map<String, Object> scd = formResult.getScd();
		W5Form f = formResult.getForm();
		final W5Table t = FrameworkCache.getTable(scd, f.getObjectId());
		if (FrameworkSetting.vcs && FrameworkSetting.vcsServer && t.getVcsFlag() != 0)
			throw new IWBException("vcs", "Form Update", formResult.getFormId(), null,
					"VCS Server not allowed to update VCS Table", null);
		final StringBuilder sql = new StringBuilder();
		sql.append("update ");
		sql.append(t.getDsc()).append(" set ");
		final List<Object> updateParams = new ArrayList<Object>();
		List<Object> whereParams = new ArrayList<Object>();
		Set<String> usedFields = new HashSet<String>();

		Map<Integer, W5FormModule> moduleMap = null;
		if (formResult.getForm().getRenderTip() != 0) {
			moduleMap = new HashMap<Integer, W5FormModule>();
			if (formResult.getForm().get_moduleList() != null)
				for (W5FormModule m : formResult.getForm().get_moduleList())
					moduleMap.put(m.getFormModuleId(), m);
		}
		W5WorkflowStep approvalStep = null;
		if (formResult.getApprovalRecord() != null) {
			approvalStep = FrameworkCache.getWorkflow(scd, formResult.getApprovalRecord().getApprovalId())
					.get_approvalStepMap().get(formResult.getApprovalRecord().getApprovalStepId());
		}
		boolean b = false;
		boolean extendedFlag = false;
		for (W5FormCell x : f.get_formCells())
			if (x.getNrdTip() != 1 && x.getObjectDetailId() != 0 && x.get_sourceObjectDetail() != null
					&& x.getControlTip() < 100) { // normal ve readonly ise
				W5TableField tf = (W5TableField) x.get_sourceObjectDetail();
				if (tf.getCanUpdateFlag() == 0 || tf.getTabOrder() < 1)
					continue; // x.getCanUpdate()!=0
				if (approvalStep != null && approvalStep.getUpdatableFields() != null
						&& !GenericUtil.hasPartInside(approvalStep.getUpdatableFields(), "" + tf.getTableFieldId()))
					continue;
				if (tf.getAccessViewTip() != 0
						&& !GenericUtil.accessControl(scd, tf.getAccessViewTip(), tf.getAccessViewRoles(),
								tf.getAccessViewUsers())
						&& (GenericUtil.isEmpty(tf.getAccessViewUserFields()) || accessUserFieldControl(t,
								tf.getAccessViewUserFields(), scd, formResult.getRequestParams(), paramSuffix)))
					continue;
				if (tf.getAccessUpdateTip() != 0
						&& !GenericUtil.accessControl(scd, tf.getAccessUpdateTip(), tf.getAccessUpdateRoles(),
								tf.getAccessUpdateUsers())
						&& (GenericUtil.isEmpty(tf.getAccessUpdateUserFields()) || accessUserFieldControl(t,
								tf.getAccessUpdateUserFields(), scd, formResult.getRequestParams(), paramSuffix)))
					continue;

				if (!GenericUtil.isEmpty(tf.getRelatedSessionField())
						&& GenericUtil.uInt(formResult.getScd().get(tf.getRelatedSessionField())) == 0)
					continue;

				if (moduleMap != null && moduleMap.get(x.getFormModuleId()) != null) {
					W5FormModule module = moduleMap.get(x.getFormModuleId());
					if (!GenericUtil.accessControl(scd, module.getAccessViewTip(), module.getAccessViewRoles(),
							module.getAccessViewUsers()))
						continue;
				}

				if (paramSuffix.length() > 0 && formResult.getRequestParams().get(x.getDsc() + paramSuffix) == null)
					continue;
				if (x.getControlTip() == 31 && GenericUtil.uInt(x.getLookupIncludedValues()) == 1
						&& !GenericUtil.hasPartInside(x.getLookupIncludedParams(), "" + scd.get("userId")))
					continue;
				Object psonuc = GenericUtil.prepareParam(tf, scd, formResult.getRequestParams(), x.getSourceTip(), null,
						x.getNotNullFlag(), x.getDsc() + paramSuffix, x.getDefaultValue(), formResult.getErrorMap());

				if (formResult.getErrorMap().isEmpty()) {
					if (x.getFormCellId() == 6060 || x.getFormCellId() == 16327 || x.getFormCellId() == 16866) { // mail
																													// sifre
																													// icin
						if (psonuc != null && psonuc.toString().startsWith("****"))
							continue;
						if (FrameworkSetting.mailPassEncrypt)
							psonuc = GenericUtil.PRMEncrypt(psonuc.toString());
					}

					if (b)
						sql.append(" , ");
					else
						b = true;
					sql.append(tf.getDsc()).append(" = ? ");
					updateParams.add(psonuc);
					usedFields.add(tf.getDsc());
				}
			}

		if (formResult.getErrorMap().size() > 0)
			return false;

		for (W5TableField p1 : t.get_tableFieldList())
			if (p1.getCanUpdateFlag() != 0 && !usedFields.contains(p1.getDsc()))
				switch (p1.getSourceTip()) { // geri kalan fieldlar icin
				case 4: // calculated Fieldlar icin
					if (b) {
						sql.append(" , ");
					} else
						b = true;
					usedFields.add(p1.getDsc());
					sql.append(p1.getDsc()).append(" = ? ");
					usedFields.add(p1.getDsc());

					Object[] oo = DBUtil.filterExt4SQL("select (" + p1.getDefaultValue() + ")", scd, new HashMap(),
							null);
					List res = executeSQLQuery2(oo[0].toString(), (List) (oo.length > 1 ? oo[1] : null));
					updateParams.add(GenericUtil.isEmpty(res) ? null : res.get(0));
					break;
				case 2: // session
					Object psonuc = GenericUtil.prepareParam(p1, scd, formResult.getRequestParams(), (short) -1, null,
							(short) 0, null, null, formResult.getErrorMap());
					if (psonuc != null) {
						if (b) {
							sql.append(" , ");
						} else
							b = true;
						usedFields.add(p1.getDsc());
						sql.append(p1.getDsc()).append(" = ? ");
						updateParams.add(psonuc);
						usedFields.add(p1.getDsc());
					}

					break;
				}
		if (usedFields.isEmpty()) { // sorun var
			throw new IWBException("validation", "Form Update", formResult.getFormId(), null, "No Used Fields", null);
		}
		if (f.get_versioningFlag()) { // eger versionin varsa, version'lari
										// degistir ve version_no'yu
			// arttir
			sql.append(", version_no=version_no+1, version_user_id=?, version_dttm=iwb.fnc_sysdate(?)");
			updateParams.add(scd.get("userId"));
			updateParams.add(scd.get("customizationId"));
		}

		b = false;
		sql.append(" where ");
		for (W5TableParam x : t.get_tableParamList())
			if (x.getNotNullFlag() != 0) {
				if (b)
					sql.append(" AND ");
				else
					b = true;
				sql.append(x.getExpressionDsc()).append(" = ? ");
				Object psonuc = GenericUtil.prepareParam(x, scd, formResult.getRequestParams(), (short) -1, null,
						(short) 0, x.getDsc() + paramSuffix, null, formResult.getErrorMap());
				whereParams.add(psonuc);
				formResult.getPkFields().put(x.getDsc(), psonuc);
			}

		updateParams.addAll(whereParams);

		try {
			final boolean extendedFlag2 = extendedFlag;
			final Map<Integer, W5FormModule> moduleMap2 = moduleMap;
			return getCurrentSession().doReturningWork(new ReturningWork<Boolean>() {

				public Boolean execute(Connection conn) throws SQLException {
					PreparedStatement s = conn.prepareStatement(sql.toString());
					applyParameters(s, updateParams);
					int updateCount = s.executeUpdate();
					s.close();
					if (FrameworkSetting.hibernateCloseAfterWork)
						conn.close();
					if (t.getDoUpdateLogFlag() != 0)
						logTableRecord(formResult, paramSuffix);
					return updateCount == 1;
				}
			});

		} catch (Exception e) {
			throw new IWBException("sql", "Form.Update", formResult.getFormId(),
					GenericUtil.replaceSql(sql.toString(), updateParams), "Error Updating", e);
		} finally {
			// session.close();
		}
	}

	public int copyFormTable(final W5FormResult formResult, final String schema, final String paramSuffix,
			boolean copyFlag) {
		final W5Form f = formResult.getForm();
		final W5Table t = FrameworkCache.getTable(formResult.getScd(), f.getObjectId()); // formResult.getForm().get_sourceTable();
		if (FrameworkSetting.vcs && FrameworkSetting.vcsServer && t.getVcsFlag() != 0)
			throw new IWBException("vcs", "Form Insert", formResult.getFormId(), null,
					"VCS Server not allowed to insert VCS Table", null);
		final StringBuilder sql = new StringBuilder(), postSql = new StringBuilder();
		sql.append("insert into ");
		if (schema != null)
			sql.append(schema).append(".");
		sql.append(t.getDsc()).append(" ( ");
		postSql.append(" select ");
		final List<Object> copyParams = new ArrayList<Object>();
		boolean b = false;
		boolean extendedFlag = false;
		int paramCount = 0;
		final Map<Integer, String> calculatedParams = new HashMap<Integer, String>();
		final Map<Integer, String> calculatedParamNames = new HashMap<Integer, String>();
		Set<String> usedFields = new HashSet<String>();

		Map<Integer, W5FormModule> moduleMap = null;
		if (formResult.getForm().getRenderTip() != 0) {
			moduleMap = new HashMap<Integer, W5FormModule>();
			if (formResult.getForm().get_moduleList() != null)
				for (W5FormModule m : formResult.getForm().get_moduleList())
					moduleMap.put(m.getFormModuleId(), m);
		}

		for (W5FormCell x : f.get_formCells())
			if (x.getNrdTip() != 1 && x.getObjectDetailId() != 0 && x.getControlTip() < 100) { // disabled(1)
																								// degil
																								// VE
																								// freeField(getObjectDetailId()!=0)
																								// degilse
				W5TableField p1 = (W5TableField) x.get_sourceObjectDetail();
				if (p1 == null || p1.getCanInsertFlag() == 0)
					continue; // x.getCanInsert()!=0

				// view AND update control
				if (
				/*
				 * !PromisUtil.accessControl(formResult.getScd(),
				 * p1.getAccessViewTip(), p1.getAccessViewRoles(),
				 * p1.getAccessViewUsers()) ||
				 */
				!GenericUtil.accessControl(formResult.getScd(), p1.getAccessInsertTip(), p1.getAccessInsertRoles(),
						p1.getAccessInsertUsers()))
					continue; // access control

				// module view control
				if (moduleMap != null && moduleMap.get(x.getFormModuleId()) != null) {
					W5FormModule module = moduleMap.get(x.getFormModuleId());
					if (!GenericUtil.accessControl(formResult.getScd(), module.getAccessViewTip(),
							module.getAccessViewRoles(), module.getAccessViewUsers()))
						continue;
				}

				Object psonuc = null;
				switch (p1.getCopySourceTip()) {
				case 7: // object_source (readonly)
				case 6: // object_source
					if (copyFlag || p1.getCopySourceTip() == 7) {
						if (b) {
							sql.append(" , ");
							postSql.append(" , ");
						} else
							b = true;
						usedFields.add(p1.getDsc());
						sql.append(p1.getDsc());
						postSql.append(p1.getDsc());
						continue;
					}
				case 1: // request
					psonuc = GenericUtil.prepareParam(p1, formResult.getScd(), formResult.getRequestParams(),
							x.getSourceTip(), null, x.getNotNullFlag(), x.getDsc() + paramSuffix, x.getDefaultValue(),
							formResult.getErrorMap());
					break;
				default:
					continue;
				}

				if (formResult.getErrorMap().isEmpty()) {
					if (b) {
						sql.append(" , ");
						postSql.append(" , ");
					} else
						b = true;
					usedFields.add(p1.getDsc());
					sql.append(p1.getDsc());
					if (x.getOutFlag() != 0) { // bu field outputParam'a
												// yazilacak
						if (x.getSourceTip() == 4) { // calculated, sql
														// calisacak sonra deger
														// verilecek
							calculatedParams.put(paramCount, (String) psonuc);
							calculatedParamNames.put(paramCount, x.getDsc());
						} else {
							formResult.getOutputFields().put(x.getDsc(), psonuc);
						}
						postSql.append(" ? ");
						copyParams.add(null);
						paramCount++;
					} else { // calculated, outputa yazilmadan direk
						if (x.getSourceTip() == 4) { // calculated, sql
														// calisacak sonra deger
														// verilecek
							postSql.append(" ( ").append(psonuc).append(" ) ");
						} else {
							postSql.append(" ? ");
							copyParams.add(psonuc);
							paramCount++;
						}
					}
				}
			}

		for (W5TableField p1 : t.get_tableFieldList())
			if (p1.getCanInsertFlag() != 0 && !usedFields.contains(p1.getDsc()))
				switch (p1.getCopySourceTip()) {
				case 7: //
					if (p1.getSourceTip() != 4)
						break;
				case 4: // calculated Fieldlar icin
					if (b) {
						sql.append(" , ");
						postSql.append(" , ");
					} else
						b = true;
					usedFields.add(p1.getDsc());
					sql.append(p1.getDsc());
					calculatedParams.put(paramCount, GenericUtil
							.filterExt(p1.getDefaultValue(), formResult.getScd(), formResult.getRequestParams(), null)
							.toString());
					calculatedParamNames.put(paramCount, p1.getDsc());
					postSql.append(" ? ");
					copyParams.add(null);
					paramCount++;
					break;
				case 2: // session
					Object psonuc = GenericUtil.prepareParam(p1, formResult.getScd(), formResult.getRequestParams(),
							(short) -1, null, (short) 0, null, null, formResult.getErrorMap());
					if (psonuc != null) {
						if (b) {
							sql.append(" , ");
							postSql.append(" , ");
						} else
							b = true;
						usedFields.add(p1.getDsc());
						sql.append(p1.getDsc());
						postSql.append(" ? ");
						copyParams.add(psonuc);
						paramCount++;
					}

					break;
				case 9: // UUID
					Object psonuc2 = GenericUtil.prepareParam(p1, formResult.getScd(), formResult.getRequestParams(),
							(short) -1, null, (short) 0, null, null, formResult.getErrorMap());
					if (psonuc2 != null) {
						if (b) {
							sql.append(" , ");
							postSql.append(" , ");
						} else
							b = true;
						usedFields.add(p1.getDsc());
						sql.append(p1.getDsc());
						postSql.append(" ? ");
						copyParams.add(psonuc2);
						paramCount++;

						formResult.getOutputFields().put(p1.getDsc(), psonuc2);
					}

					break;
				case 8: // Global Nextval
					Object psonuc3 = GenericUtil.prepareParam(p1, formResult.getScd(), formResult.getRequestParams(),
							(short) -1, null, (short) 0, null, null, formResult.getErrorMap());
					if (psonuc3 != null) {
						if (b) {
							sql.append(" , ");
							postSql.append(" , ");
						} else
							b = true;
						usedFields.add(p1.getDsc());
						sql.append(p1.getDsc());
						postSql.append(" ? ");
						copyParams.add(psonuc3);
						paramCount++;

						formResult.getOutputFields().put(p1.getDsc(), psonuc3);
					}

					break;
				}

		if (!formResult.getErrorMap().isEmpty())
			return 0;

		if (usedFields.isEmpty()) { // sorun var
			throw new IWBException("validation", "Form Copy", formResult.getFormId(), null, "No Used Fields", null);
		}

		if (f.get_versioningFlag()) { // eger versioning varsa, version'lari
										// degistir ve version_no'yu
			// arttir
			sql.append(", version_no, insert_user_id, version_user_id, insert_dttm, version_dttm ");
			postSql.append(" , 1, ?, ?, iwb.fnc_sysdate(?), iwb.fnc_sysdate(?)");
			copyParams.add(formResult.getScd().get("userId"));
			copyParams.add(formResult.getScd().get("userId"));
			copyParams.add(formResult.getScd().get("customizationId"));
			copyParams.add(formResult.getScd().get("customizationId"));
		}

		sql.append(" ) ").append(postSql).append(" from ");
		if (schema != null)
			sql.append(schema).append(".");
		sql.append(t.getDsc());

		b = false;
		sql.append(" where ");
		for (W5TableParam x : t.get_tableParamList())
			if (x.getNotNullFlag() != 0) {
				if (b)
					sql.append(" AND ");
				else
					b = true;
				sql.append(x.getExpressionDsc()).append(" = ? ");
				Object psonuc = GenericUtil.prepareParam(x, formResult.getScd(), formResult.getRequestParams(),
						(short) -1, null, (short) 0, x.getDsc() + paramSuffix, null, formResult.getErrorMap());
				copyParams.add(psonuc);
				formResult.getPkFields().put(x.getDsc(), psonuc);
			}

		b = false;
		try {
			return getCurrentSession().doReturningWork(new ReturningWork<Integer>() {

				public Integer execute(Connection conn) throws SQLException {
					PreparedStatement s = null;
					for (Integer o : calculatedParams.keySet()) { // calculated
																	// ve output
																	// edilecek
																	// parametreler
																	// hesaplaniyor
						// once
						String seq = calculatedParams.get(o);
						if (seq.endsWith(".nextval"))
							seq = "nextval('" + seq.substring(0, seq.length() - 8) + "')";
						s = conn.prepareStatement("select " + seq + " "); // from
																			// dual
						ResultSet rs = s.executeQuery();
						rs.next();
						Object paramOut = rs.getObject(1);
						if (paramOut != null) {
							if (paramOut instanceof java.sql.Timestamp) {
								try {
									paramOut = (java.sql.Timestamp) paramOut;
								} catch (Exception e) {
									paramOut = "java.sql.Timestamp";
								}
							} else if (paramOut instanceof java.sql.Date) {
								try {
									paramOut = rs.getTimestamp(1);
								} catch (Exception e) {
									paramOut = "java.sql.Date";
								}
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
					if (schema == null) {
						count = s.executeUpdate();
					} else
						try { // farkli bir yere koymaya calisiyor
							count = s.executeUpdate();
						} catch (Exception e) {
							PreparedStatement s2 = conn.prepareStatement("create table " + schema + "." + t.getDsc()
									+ " as select * from " + t.getDsc() + " where 1=2");
							s2.executeUpdate();
							s2.close();
							String pk = "";
							for (W5TableParam p : t.get_tableParamList()) {
								if (pk.length() > 0)
									pk += ",";
								pk += p.getExpressionDsc();
							}
							s2 = conn.prepareStatement("alter table " + schema + "." + t.getDsc()
									+ " add constraint PK_APPROVAL_" + t.getTableId() + " primary key (" + pk + ")");
							s2.executeUpdate();
							s2.close();
							count = s.executeUpdate();
						}
					s.close();
					if (FrameworkSetting.hibernateCloseAfterWork)
						conn.close();

					return count;
				}
			});

		} catch (Exception e) {
			throw new IWBException("sql", "Form.Copy", formResult.getFormId(),
					GenericUtil.replaceSql(sql.toString(), copyParams), "Error Copying", e);
		} finally {
			// session.close();
		}
	}

	public Object[] listObjectCommentAndAttachUsers(Map<String, Object> scd, Map<String, String> requestParams) {
		int tableId = GenericUtil.uInt(requestParams.get("table_id"));
		int tablePk = GenericUtil.uInt(requestParams.get("table_pk"));
		int customizationId = (Integer) scd.get("customizationId");
		int sessionUserId = (Integer) scd.get("userId");
		int recordInsertUserId = 0, recordVersionUserId = 0, assignedUserId = 0;
		boolean recordInsertUserFlag = false, recordVersionUserFlag = false, assignedUserFlag = false,
				customizationFlag = false;
		Set<Integer> extraUserIds = new HashSet<Integer>();
		List<Object[]> l = executeSQLQuery(
				"select t.dsc, tp.expression_Dsc"
						+ ",(select count(1) from iwb.w5_Table_Field tf where tf.table_Id=t.table_Id AND tf.customization_Id=t.customization_Id AND lower(tf.dsc)='insert_user_id') insert_user_id_count"
						+ ",(select count(1) from iwb.w5_Table_Field tf where tf.table_Id=t.table_Id AND tf.customization_Id=t.customization_Id AND lower(tf.dsc)='version_user_id') version_user_id_count"
						+ ",(select count(1) from iwb.w5_Table_Field tf where tf.table_Id=t.table_Id AND tf.customization_Id=t.customization_Id AND lower(tf.dsc)='assigned_user_id') assigned_user_id_count "
						+ ",(select count(1) from iwb.w5_Table_Field tf where tf.table_Id=t.table_Id AND tf.customization_Id=t.customization_Id AND lower(tf.dsc)='customization_id') customization_id_count "
						+ "from iwb.w5_Table t, iwb.W5_Table_Param tp "
						+ "where t.customization_Id=? AND t.table_Id=? AND t.table_Id=tp.table_Id AND tp.tab_Order=1",
				customizationId, tableId);
		if (!l.isEmpty()) {
			String tableDsc = (l.get(0)[0]).toString();
			String tablePkDsc = (l.get(0)[1]).toString();
			recordInsertUserFlag = GenericUtil.uInt((l.get(0)[2]).toString()) != 0;
			recordVersionUserFlag = GenericUtil.uInt((l.get(0)[3]).toString()) != 0;
			assignedUserFlag = GenericUtil.uInt((l.get(0)[4]).toString()) != 0;
			customizationFlag = GenericUtil.uInt((l.get(0)[5]).toString()) != 0;
			if (recordInsertUserFlag || recordVersionUserFlag || assignedUserFlag) {
				StringBuilder sql = new StringBuilder();
				sql.append("select ");
				int fieldCount = 0;
				if (recordInsertUserFlag) {
					sql.append("x.insert_user_id,");
					fieldCount++;
				}
				if (recordVersionUserFlag) {
					sql.append("x.version_user_id,");
					recordVersionUserId = recordInsertUserFlag ? 1 : 0;
					fieldCount++;
				}
				if (assignedUserFlag) {
					sql.append("x.assigned_user_id,");
					assignedUserId = (recordInsertUserFlag ? 1 : 0) + (recordVersionUserFlag ? 1 : 0);
					fieldCount++;
				}
				sql.setLength(sql.length() - 1);
				sql.append(" from ").append(tableDsc).append(" x where x.").append(tablePkDsc).append("=?");
				if (customizationFlag)
					sql.append(" AND x.customization_id=?");
				List<Object[]> l2 = customizationFlag ? executeSQLQuery(sql.toString(), tablePk, customizationId)
						: executeSQLQuery(sql.toString(), tablePk);
				if (l2 != null && !l2.isEmpty()) {
					if (recordInsertUserFlag)
						extraUserIds.add(GenericUtil.uInt(fieldCount == 1 ? l2.get(0) : l2.get(0)[0]));
					if (recordVersionUserFlag)
						extraUserIds
								.add(GenericUtil.uInt(fieldCount == 1 ? l2.get(0) : l2.get(0)[recordVersionUserId]));
					if (assignedUserFlag)
						extraUserIds.add(GenericUtil.uInt(fieldCount == 1 ? l2.get(0) : l2.get(0)[assignedUserId]));
				}
			}
		}
		/*
		 * TODO List<Object> newCommentUsers = executeSQLQuery(
		 * "select distinct c.comment_user_id from iwb.w5_comment c where c.customization_id=? and c.table_id=? AND c.table_pk=? AND not exists(select 1 from iwb.w5_notification n where n.customization_id=c.customization_id and n.active_flag=1 AND n.notification_tip=1 AND n.table_id=c.table_id AND n.table_pk=c.table_pk AND n.user_id=c.comment_user_id)"
		 * , customizationId,tableId, tablePk);
		 * if(newCommentUsers!=null)for(Object o:newCommentUsers){
		 * extraUserIds.add(PromisUtil.uInt(o)); } List<Object> newAttachUsers =
		 * executeSQLQuery(
		 * "select distinct c.upload_user_id from iwb.w5_file_attachment c where c.customization_id=? and c.table_id=? AND c.table_pk=? AND not exists(select 1 from iwb.w5_notification n where n.customization_id=c.customization_id and n.active_flag=1 AND n.notification_tip=2 AND n.table_id=c.table_id AND n.table_pk=c.table_pk AND n.user_id=c.upload_user_id)"
		 * , customizationId,tableId, tablePk);
		 * if(newAttachUsers!=null)for(Object o:newAttachUsers){
		 * extraUserIds.add(PromisUtil.uInt(o)); }
		 */
		extraUserIds.remove(sessionUserId);
		return extraUserIds.isEmpty() ? null : extraUserIds.toArray();
	}

	public int insertFormTable(final W5FormResult formResult, final String paramSuffix) {
		final W5Form f = formResult.getForm();
		final String projectId = FrameworkCache.getProjectId(formResult.getScd(), "40." + formResult.getFormId());
		final W5Table t = FrameworkCache.getTable(projectId, f.getObjectId()); // formResult.getForm().get_sourceTable();
		if (FrameworkSetting.vcs && FrameworkSetting.vcsServer && t.getVcsFlag() != 0)
			throw new IWBException("vcs", "Form Insert", formResult.getFormId(), null,
					"VCS Server not allowed to insert VCS Table", null);

		final StringBuilder sql = new StringBuilder(), postSql = new StringBuilder();
		sql.append("insert into ");
		sql.append(t.getDsc()).append(" ( ");
		postSql.append(" values (");
		final List<Object> insertParams = new ArrayList<Object>();
		boolean b = false;
		int paramCount = 0;
		final Map<Integer, String> calculatedParams = new HashMap<Integer, String>();
		final Map<Integer, String> calculatedParamNames = new HashMap<Integer, String>();
		Set<String> usedFields = new HashSet<String>();

		Map<Integer, W5FormModule> moduleMap = null;
		if (formResult.getForm().getRenderTip() != 0) {
			moduleMap = new HashMap<Integer, W5FormModule>();
			if (formResult.getForm().get_moduleList() != null)
				for (W5FormModule m : formResult.getForm().get_moduleList())
					moduleMap.put(m.getFormModuleId(), m);
		}

		for (W5FormCell x : f.get_formCells())
			if (x.getNrdTip() != 1 && x.getObjectDetailId() != 0 && x.getControlTip() < 100) { // disabled(1)
																								// degil
																								// VE
																								// freeField(getObjectDetailId()!=0)
																								// degilse
				W5TableField tf = (W5TableField) x.get_sourceObjectDetail();
				if (tf == null)
					continue; // error. aslinda olmamasi lazim
				if (tf.getCanInsertFlag() == 0)
					continue; // x.getCanInsert()!=0

				// view AND update control
				if (
				/*
				 * !PromisUtil.accessControl(formResult.getScd(),
				 * p1.getAccessViewTip(), p1.getAccessViewRoles(),
				 * p1.getAccessViewUsers()) ||
				 */
				!GenericUtil.accessControl(formResult.getScd(), tf.getAccessInsertTip(), tf.getAccessInsertRoles(),
						tf.getAccessInsertUsers()))
					continue; // access control

				// related session field control
				if (!GenericUtil.isEmpty(tf.getRelatedSessionField())
						&& GenericUtil.uInt(formResult.getScd().get(tf.getRelatedSessionField())) == 0)
					continue;

				// module view control
				if (moduleMap != null && moduleMap.get(x.getFormModuleId()) != null) {
					W5FormModule module = moduleMap.get(x.getFormModuleId());
					if (!GenericUtil.accessControl(formResult.getScd(), module.getAccessViewTip(),
							module.getAccessViewRoles(), module.getAccessViewUsers()))
						continue;
				}

				Object psonuc = GenericUtil.prepareParam(tf, formResult.getScd(), formResult.getRequestParams(),
						x.getSourceTip(), null, x.getNotNullFlag(), x.getDsc() + paramSuffix, x.getDefaultValue(),
						formResult.getErrorMap());

				if (formResult.getErrorMap().isEmpty()) {
					if (b) {
						sql.append(" , ");
						postSql.append(" , ");
					} else
						b = true;
					usedFields.add(tf.getDsc());
					sql.append(tf.getDsc());
					if (x.getOutFlag() != 0) { // bu field outputParam'a
												// yazilacak
						if (x.getSourceTip() == 4) { // calculated, sql
														// calisacak sonra deger
														// verilecek
							calculatedParams.put(paramCount, (String) psonuc);
							calculatedParamNames.put(paramCount, x.getDsc());
						} else {
							formResult.getOutputFields().put(x.getDsc(), psonuc);
						}
						postSql.append(" ? ");
						insertParams.add(null);
						paramCount++;
					} else { // calculated, outputa yazilmadan direk
						if (x.getSourceTip() == 4) { // calculated, sql
														// calisacak sonra deger
														// verilecek
							postSql.append(" ( ").append(psonuc).append(" ) ");
						} else {
							postSql.append(" ? ");
							insertParams.add(psonuc);
							paramCount++;
						}
					}
				}
			}

		for (W5TableField p1 : t.get_tableFieldList())
			if (p1.getCanInsertFlag() != 0 && !usedFields.contains(p1.getDsc()))
				switch (p1.getSourceTip()) {

				case 4: // SQL calculated Fieldlar icin
					if (b) {
						sql.append(" , ");
						postSql.append(" , ");
					} else
						b = true;
					usedFields.add(p1.getDsc());
					sql.append(p1.getDsc());
					calculatedParams.put(paramCount, GenericUtil
							.filterExt(p1.getDefaultValue(), formResult.getScd(), formResult.getRequestParams(), null)
							.toString());
					calculatedParamNames.put(paramCount, p1.getDsc());
					postSql.append(" ? ");
					insertParams.add(null);
					paramCount++;
					break;
				case 5:// javascript
				case 2: // session
					Object psonuc = GenericUtil.prepareParam(p1, formResult.getScd(), formResult.getRequestParams(),
							(short) -1, null, (short) 0, null, null, formResult.getErrorMap());
					if (psonuc != null) {
						if (b) {
							sql.append(" , ");
							postSql.append(" , ");
						} else
							b = true;
						usedFields.add(p1.getDsc());
						sql.append(p1.getDsc());
						postSql.append(" ? ");
						insertParams.add(psonuc);
						paramCount++;
					}

					break;
				case 9: // UUID
					Object psonuc2 = GenericUtil.prepareParam(p1, formResult.getScd(), formResult.getRequestParams(),
							(short) -1, null, (short) 0, null, null, formResult.getErrorMap());
					if (psonuc2 != null) {
						if (b) {
							sql.append(" , ");
							postSql.append(" , ");
						} else
							b = true;
						usedFields.add(p1.getDsc());
						sql.append(p1.getDsc());
						postSql.append(" ? ");
						insertParams.add(psonuc2);
						paramCount++;

						formResult.getOutputFields().put(p1.getDsc(), psonuc2);
					}

					break;
				case 8: // Global Nextval
					Object psonuc3 = GenericUtil.prepareParam(p1, formResult.getScd(), formResult.getRequestParams(),
							(short) -1, null, (short) 0, null, null, formResult.getErrorMap());
					if (psonuc3 != null) {
						if (b) {
							sql.append(" , ");
							postSql.append(" , ");
						} else
							b = true;
						usedFields.add(p1.getDsc());
						sql.append(p1.getDsc());
						postSql.append(" ? ");
						insertParams.add(psonuc3);
						paramCount++;

						formResult.getOutputFields().put(p1.getDsc(), psonuc3);
					}

					break;
				}

		if (!formResult.getErrorMap().isEmpty())
			return 0;

		if (usedFields.isEmpty()) { // sorun var
			throw new IWBException("validation", "Form Insert", formResult.getFormId(), null, "No Used Fields", null);
		}

		if (f.get_versioningFlag()) { // eger versioning varsa, version'lari
										// degistir ve version_no'yu
			// arttir
			sql.append(", version_no, insert_user_id, version_user_id, insert_dttm, version_dttm ");
			postSql.append(" , 1, ?, ?, iwb.fnc_sysdate(?), iwb.fnc_sysdate(?) ");
			insertParams.add(formResult.getScd().get("userId"));
			insertParams.add(formResult.getScd().get("userId"));
			insertParams.add(formResult.getScd().get("customizationId"));
			insertParams.add(formResult.getScd().get("customizationId"));
		}

		sql.append(" ) ").append(postSql).append(")");

		try {
			final Map<Integer, W5FormModule> moduleMap2 = moduleMap;

			return getCurrentSession().doReturningWork(new ReturningWork<Integer>() {

				public Integer execute(Connection conn) throws SQLException {
					PreparedStatement s = null;
					int count = 0;
					for (Integer o : calculatedParams.keySet()) { // calculated
																	// ve output
																	// edilecek
																	// parametreler
																	// hesaplaniyor
						// once
						String seq = calculatedParams.get(o);
						if (seq.endsWith(".nextval"))
							seq = "nextval('" + seq.substring(0, seq.length() - 8) + "')";
						s = conn.prepareStatement("select " + seq + " "); // from
																			// dual
						ResultSet rs = s.executeQuery();
						rs.next();
						Object paramOut = rs.getObject(1);
						if (paramOut != null) {
							if (paramOut instanceof java.sql.Timestamp) {
								try {
									paramOut = (java.sql.Timestamp) paramOut;
								} catch (Exception e) {
									paramOut = "java.sql.Timestamp";
								}
							} else if (paramOut instanceof java.sql.Date) {
								try {
									paramOut = rs.getTimestamp(1);
								} catch (Exception e) {
									paramOut = "java.sql.Date";
								}
							}
						}

						rs.close();
						s.close();
						insertParams.set(o, paramOut);
						formResult.getOutputFields().put(calculatedParamNames.get(o), paramOut);
					}
					s = conn.prepareStatement(sql.toString());
					applyParameters(s, insertParams);
					count = s.executeUpdate();
					s.close();
					int customizationId = (Integer) formResult.getScd().get("customizationId");

					if (t.getTableId() != 329
							&& FrameworkCache.getAppSettingIntValue(customizationId, "make_comment_flag") != 0
							&& t.getMakeCommentFlag() != 0) {
						PreparedStatement s2 = conn.prepareStatement(
								"update iwb.w5_comment set table_pk=? where project_uuid=? AND table_id=? AND table_pk=?");
						applyParameters(s2,
								formResult.getOutputFields().get(t.get_tableParamList().get(0).getExpressionDsc()),
								projectId, t.getTableId(),
								GenericUtil.uInt(formResult.getRequestParams().get("_tmpId")));
						s2.executeUpdate();
						s2.close();
					}

					/*
					 * if(t.getTableId()!=370 &&
					 * FrameworkCache.getAppSettingIntValue(customizationId,
					 * "row_based_security_flag")!=0 &&
					 * !GenericUtil.isEmpty(t.getAccessTips())){
					 * PreparedStatement s2 = conn.prepareStatement(
					 * "update iwb.w5_access_control set table_pk=?::int where customization_id=? AND table_id=? AND table_pk=?::int"
					 * ); applyParameters(s2,
					 * formResult.getOutputFields().get(t.get_tableParamList().
					 * get(0).getExpressionDsc()),customizationId,t.getTableId()
					 * ,formResult.getRequestParams().get("_tmpId"));
					 * s2.executeUpdate(); s2.close(); }
					 */

					if (FrameworkSetting.hibernateCloseAfterWork)
						conn.close();

					return count;
				}
			});

		} catch (Exception e) {
			throw new IWBException("sql", "Form.Insert", formResult.getFormId(),
					GenericUtil.replaceSql(sql.toString(), insertParams), "Error Inserting", e);
		} finally {
			// session.close();
		}

		/*
		 * if(formResult.getForm().get_sourceTable() != null &&
		 * formResult.getForm().get_sourceTable().getDoInsertLogFlag()!=0){
		 * Bunun yapılabilmesi için önce logTableRecord'un değişmesi lazım.
		 * for(W5TableParam x: t.get_tableParamList()){
		 * if(x.getNotNullFlag()!=0){ Object psonuc = PromisUtil.prepareParam(x,
		 * formResult.getScd(), formResult.getRequestParams(), (short)-1, null,
		 * (short)0, x.getDsc() + paramSuffix, null, formResult.getErrorMap());
		 * formResult.getPkFields().put(x.getDsc(), psonuc); } }
		 * logTableRecord(formResult); }
		 */
	}

	public boolean deleteTableRecord(W5FormResult formResult, String paramSuffix) {
		W5Form f = formResult.getForm();
		W5Table t = FrameworkCache.getTable(formResult.getScd(), f.getObjectId());
		if (FrameworkSetting.vcs && FrameworkSetting.vcsServer && t.getVcsFlag() != 0)
			throw new IWBException("vcs", "Form Record Update", formResult.getFormId(), null,
					"VCS Server not allowed to delete VCS Table", null);
		StringBuilder sql = new StringBuilder();
		sql.append("delete from ").append(t.getDsc()).append(" where ");
		List<Object> realParams = new ArrayList<Object>();
		boolean b = false;
		for (W5TableParam x : t.get_tableParamList())
			if (x.getNotNullFlag() != 0) {
				if (b)
					sql.append(" AND ");
				else
					b = true;
				sql.append(x.getExpressionDsc()).append(" = ? ");
				Object psonuc = GenericUtil.prepareParam(x, formResult.getScd(), formResult.getRequestParams(),
						(short) -1, null, (short) 0, x.getDsc() + paramSuffix, null, formResult.getErrorMap());
				realParams.add(psonuc);
				formResult.getPkFields().put(x.getDsc(), psonuc);
			}

		if (t.getDoDeleteLogFlag() != 0)
			logTableRecord(formResult, paramSuffix);
		Session session = getCurrentSession();
		try {
			b = applyParameters(session.createSQLQuery(sql.toString()), realParams).executeUpdate() > 0;

		} catch (Exception e) {
			throw new IWBException("sql", "Form.Delete", formResult.getFormId(),
					GenericUtil.replaceSql(sql.toString(), realParams), "Error Deleting", e);
		} finally {
			// session.close();
		}
		return b;
	}

	private void organizeQueryFields4WSMethod(Map<String, Object> scd, final W5Query q, final short insertFlag) {
		final List<W5QueryFieldCreation> updateList = new ArrayList<W5QueryFieldCreation>();
		final List<W5QueryFieldCreation> insertList = new ArrayList<W5QueryFieldCreation>();
		final Map<String, W5QueryFieldCreation> existField = new HashMap<String, W5QueryFieldCreation>();
		final List<Object> sqlParams = new ArrayList();
		String projectId = (String) scd.get("projectId");
		List<W5QueryFieldCreation> existingQueryFields = find(
				"from W5QueryFieldCreation t where t.queryId=? AND t.projectUuid=?", q.getQueryId(), projectId);
		for (W5QueryFieldCreation field : existingQueryFields) {
			existField.put(field.getDsc().toLowerCase(FrameworkSetting.appLocale), field);
		}
		if (q.getSqlSelect().equals("*")) {
			W5WsMethodParam parentParam = (W5WsMethodParam) getCustomizedObject(
					"from W5WsMethodParam p where p.outFlag=1 AND p.wsMethodId=? AND p.paramTip=10 AND p.projectUuid=?",
					q.getMainTableId(), projectId, "Parent WSMethodParam");
			List<W5WsMethodParam> outParams = find(
					"from W5WsMethodParam p where p.outFlag=1 AND p.wsMethodId=? AND p.parentWsMethodParamId=? AND p.projectUuid=? order by p.tabOrder",
					q.getMainTableId(), parentParam.getWsMethodParamId(), projectId);
			int j = 0;
			for (W5WsMethodParam wsmp : outParams) {
				String columnName = wsmp.getDsc().toLowerCase(FrameworkSetting.appLocale);
				if (insertFlag != 0 && existField.get(columnName) == null) {
					W5QueryFieldCreation field = new W5QueryFieldCreation();
					field.setDsc(columnName);
					field.setTabOrder((short) (j + 1));
					field.setQueryId(q.getQueryId());
					field.setFieldTip(wsmp.getParamTip());
					field.setInsertUserId((Integer) scd.get("userId"));
					field.setVersionUserId((Integer) scd.get("userId"));
					field.setVersionDttm(new java.sql.Timestamp(new java.util.Date().getTime()));
					field.setProjectUuid((String) scd.get("projectId"));
					field.setOprojectUuid((String) scd.get("projectId"));
					field.setMainTableFieldId(wsmp.getWsMethodParamId());
					field.setQueryFieldId(GenericUtil.getGlobalNextval("iwb.seq_query_field", projectId,
							(Integer) scd.get("userId"), (Integer) scd.get("customizationId")));
					insertList.add(field);
					j++;
				}
				existField.remove(columnName);
			}
		} else {
			String[] lines = q.getSqlSelect().split("\n");
			int j = 0;
			for (String p : lines)
				if (!GenericUtil.isEmpty(p)) {
					String columnName = p.substring(0, p.indexOf(':'));
					if (insertFlag != 0 && existField.get(columnName) == null) {
						W5QueryFieldCreation field = new W5QueryFieldCreation();
						field.setDsc(columnName);
						field.setTabOrder((short) (j + 1));
						field.setQueryId(q.getQueryId());
						field.setFieldTip((short) 1);
						field.setInsertUserId((Integer) scd.get("userId"));
						field.setVersionUserId((Integer) scd.get("userId"));
						field.setVersionDttm(new java.sql.Timestamp(new java.util.Date().getTime()));
						field.setProjectUuid(projectId);
						field.setOprojectUuid(projectId);
						field.setMainTableFieldId(0);
						field.setQueryFieldId(GenericUtil.getGlobalNextval("iwb.seq_query_field", projectId,
								(Integer) scd.get("userId"), (Integer) scd.get("customizationId")));
						insertList.add(field);
						j++;
					}
					existField.remove(columnName);
				}
		}
		boolean vcs = FrameworkSetting.vcs;
		if (insertFlag != 0 && insertList != null)
			for (W5QueryFieldCreation field : insertList) {
				saveObject(field);
				if (vcs)
					saveObject(new W5VcsObject(scd, 9, field.getQueryFieldId()));
			}
		for (W5QueryFieldCreation field : updateList) {
			updateObject(field);
			if (vcs)
				makeDirtyVcsObject(scd, 9, field.getQueryFieldId());
		}
	}

	private void organizeQueryFields4TSMeasurement(Map<String, Object> scd, final W5Query q, final short insertFlag) {
		final Map<String, W5QueryFieldCreation> existField = new HashMap<String, W5QueryFieldCreation>();
		final List<Object> sqlParams = new ArrayList();
		String projectId = (String) scd.get("projectId");
		List<W5QueryFieldCreation> existingQueryFields = find(
				"from W5QueryFieldCreation t where t.queryId=? AND t.projectUuid=?", q.getQueryId());
		for (W5QueryFieldCreation field : existingQueryFields) {
			existField.put(field.getDsc().toLowerCase(FrameworkSetting.appLocale), field);
		}
	}

	public void organizeQueryFields(final Map<String, Object> scd, final int queryId, final short insertFlag) {
		W5Project po = FrameworkCache.getProject(scd);

		executeUpdateSQLQuery("set search_path=" + po.getRdbmsSchema());
		final int userId = (Integer) scd.get("userId");
		final List<W5QueryFieldCreation> updateList = new ArrayList<W5QueryFieldCreation>();
		final List<W5QueryFieldCreation> insertList = new ArrayList<W5QueryFieldCreation>();

		for (final W5Query query : (List<W5Query>) find("from W5Query t where t.queryId=? AND t.projectUuid=?", queryId,
				po.getProjectUuid())) {
			if (query.getQuerySourceTip() == 1376) {
				organizeQueryFields4WSMethod(scd, query, insertFlag);
				continue;
			} else if (query.getQuerySourceTip() == 2709) {
				organizeQueryFields4TSMeasurement(scd, query, insertFlag);
				continue;
			}
			final Map<String, W5QueryFieldCreation> existField = new HashMap<String, W5QueryFieldCreation>();
			final List<Object> sqlParams = new ArrayList();
			List<W5QueryFieldCreation> existingQueryFields = find(
					"from W5QueryFieldCreation t where t.queryId=? AND t.projectUuid=?", queryId, po.getProjectUuid());
			for (W5QueryFieldCreation field : existingQueryFields) {
				existField.put(field.getDsc().toLowerCase(FrameworkSetting.appLocale), field);
			}

			StringBuilder sql = new StringBuilder();
			sql.append("select ").append(query.getSqlSelect());
			sql.append(" from ").append(query.getSqlFrom());
			if (query.getSqlWhere() != null && query.getSqlWhere().trim().length() > 0)
				sql.append(" where ").append(query.getSqlWhere().trim());
			if (query.getSqlGroupby() != null && query.getSqlGroupby().trim().length() > 0 && query.getQueryTip() != 9) // group
																														// by
																														// connect
																														// olmayacak
				sql.append(" group by ").append(query.getSqlGroupby().trim());
			if (query.getSqlPostSelect() != null && query.getSqlPostSelect().trim().length() > 2) {
				sql = new StringBuilder(sql.length() + 100).append("select z.*,").append(query.getSqlPostSelect())
						.append(" from (").append(sql).append(") z");
			}
			Object[] oz = DBUtil.filterExt4SQL(sql.toString(), scd, null, null);
			final String sqlStr = ((StringBuilder) oz[0]).toString();
			if (oz[1] != null)
				sqlParams.addAll((List) oz[1]);
			else
				for (int qi = 0; qi < sqlStr.length(); qi++)
					if (sqlStr.charAt(qi) == '?')
						sqlParams.add(null);

			try {
				getCurrentSession().doWork(new Work() {

					public void execute(Connection conn) throws SQLException {
						PreparedStatement stmt = null;
						ResultSet rs = null;
						stmt = conn.prepareStatement(sqlStr);
						if (sqlParams.size() > 0)
							applyParameters(stmt, sqlParams);
						rs = stmt.executeQuery();
						ResultSetMetaData meta = rs.getMetaData();
						Map<String, W5TableField> fieldMap = new HashMap<String, W5TableField>();
						W5Table t = FrameworkCache.getTable(scd, query.getMainTableId());
						if (t != null)
							for (W5TableField f : t.get_tableFieldList()) {
								fieldMap.put(f.getDsc().toLowerCase(), f);
							}

						int columnNumber = meta.getColumnCount();
						for (int i = 1, j = 0; i <= columnNumber; i++) {
							String columnName = meta.getColumnName(i).toLowerCase(FrameworkSetting.appLocale);
							if (insertFlag != 0 && existField.get(columnName) == null) { // eger
																							// daha
																							// onceden
																							// boyle
																							// tanimlanmis
																							// bir
																							// field
																							// yoksa
								W5QueryFieldCreation field = new W5QueryFieldCreation();
								field.setDsc(columnName);
								field.setCustomizationId((Integer) scd.get("customizationId"));
								if (columnName.equals("insert_user_id") || columnName.equals("version_user_id"))
									field.setPostProcessTip((short) 53);
								field.setTabOrder((short) (i));
								field.setQueryId(query.getQueryId());
								field.setFieldTip((short) DBUtil.java2iwbType(meta.getColumnType(i)));
								if (field.getFieldTip() == 4) {
									// numeric değerde ondalık varsa tipi 3 yap
									int sc = meta.getScale(i);
									if (sc > 0)
										field.setFieldTip((short) 3);
								}
								field.setInsertUserId(userId);
								field.setVersionUserId(userId);
								field.setVersionDttm(new java.sql.Timestamp(new java.util.Date().getTime()));
								field.setProjectUuid((String) scd.get("projectId"));
								field.setOprojectUuid((String) scd.get("projectId"));
								if (fieldMap.containsKey(columnName.toLowerCase())) {
									W5TableField tf = fieldMap.get(columnName.toLowerCase());
									field.setMainTableFieldId(tf.getTableFieldId());
									if (tf.getDefaultLookupTableId() > 0) {
										switch (tf.getDefaultControlTip()) {
										case 6:
											field.setPostProcessTip((short) 10);
											break; // combo static
										case 8:
										case 58:
											field.setPostProcessTip((short) 11);
											break; // lov-combo static
										case 7:
										case 10:
											field.setPostProcessTip((short) 12);
											break; // combo query
										case 15:
										case 59:
											field.setPostProcessTip((short) 13);
											break; // lov-combo query
										case 51:
										case 52:
											field.setPostProcessTip(tf.getDefaultControlTip());
											break; // combo static
										}
										if (tf.getDefaultControlTip() != 0)
											field.setLookupQueryId(tf.getDefaultLookupTableId());
									}
								}
								field.setQueryFieldId(GenericUtil.getGlobalNextval("iwb.seq_query_field",
										(String) scd.get("projectId"), (Integer) scd.get("userId"),
										(Integer) scd.get("customizationId")));
								insertList.add(field);
								j++;
							} else if (existField.get(columnName) != null
									&& (existField.get(columnName).getTabOrder() != i
											|| (existField.get(columnName).getMainTableFieldId() == 0
													&& fieldMap.containsKey(columnName.toLowerCase())))) {
								W5QueryFieldCreation field = existField.get(columnName);
								field.setTabOrder((short) (i));
								field.setVersionUserId(userId);
								field.setVersionDttm(new java.sql.Timestamp(new java.util.Date().getTime()));
								if (field.getMainTableFieldId() == 0
										&& fieldMap.containsKey(columnName.toLowerCase())) {
									field.setMainTableFieldId(fieldMap.get(columnName.toLowerCase()).getTableFieldId());
								}
								updateList.add(field);
							}
							existField.remove(columnName);
						}
						rs.close();
						stmt.close();
						if (FrameworkSetting.hibernateCloseAfterWork)
							conn.close();
					}
				});

				for (W5QueryFieldCreation field : existField.values()) { // icinde
																			// bulunmayanlari
																			// negatif
																			// olarak
																			// koy
					field.setTabOrder((short) -Math.abs(field.getTabOrder()));
					field.setPostProcessTip((short) 99);
					field.setVersionUserId(userId);
					field.setVersionDttm(new java.sql.Timestamp(new java.util.Date().getTime()));
					updateList.add(field);
				}
			} catch (Exception e) {
				if (FrameworkSetting.debug)
					e.printStackTrace();
				if (queryId != -1)
					throw new IWBException("sql", "QueryField.Creation", queryId, sql.toString(), "Error Creating", e);
			}
		}
		boolean vcs = FrameworkSetting.vcs;
		if (insertFlag != 0 && insertList != null)
			for (W5QueryFieldCreation field : insertList) {
				saveObject(field);
				if (vcs)
					saveObject(new W5VcsObject(scd, 9, field.getQueryFieldId()));
			}
		for (W5QueryFieldCreation field : updateList) {
			updateObject(field);
			if (vcs)
				makeDirtyVcsObject(scd, 9, field.getQueryFieldId());
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

	

	public void executeDbFunc(final W5GlobalFuncResult r, final String paramSuffix) {
		Log5GlobalFuncAction action = new Log5GlobalFuncAction(r);
		String error = null;
		
		final List<Object> sqlParams = new ArrayList<Object>();
		final List<String> sqlNames = new ArrayList<String>();
		final StringBuilder sql = new StringBuilder();
		sql.append("{call ");

		sql.append(r.getGlobalFunc().getDsc());
		boolean hasOutParam = false;
		if (r.getGlobalFunc().get_dbFuncParamList().size() > 0) {
			boolean b = false;
			sql.append("( ");
			for (W5GlobalFuncParam p1 : r.getGlobalFunc().get_dbFuncParamList()) {
				if (b)
					sql.append(",");
				else
					b = true;
				sql.append(" ? ");
				String pvalue = null;
				if (p1.getOutFlag() != 0) {
					sqlNames.add(p1.getDsc());
					sqlParams.add(null);
					hasOutParam = true;
				} else {
					Object psonuc = GenericUtil.prepareParam(p1, r.getScd(), r.getRequestParams(), (short) -1, null,
							(short) 0, p1.getSourceTip() == 1 ? p1.getDsc() + paramSuffix : null, null,
							r.getErrorMap());
					sqlParams.add(psonuc);
					sqlNames.add(null);
				}
			}
			sql.append(" )");
		}
		sql.append("}");

		r.setExecutedSql(sql.toString());
		r.setSqlParams(sqlParams);

		if (!r.getErrorMap().isEmpty()) {
			r.setSuccess(false);
			return;
		}
		final boolean hasOutParam2 = hasOutParam;
		try {
			getCurrentSession().doWork(new Work() {

				public void execute(Connection conn) throws SQLException {

					CallableStatement s = conn.prepareCall(sql.toString());
					int i = 1;
					for (int ix = 0; ix < sqlParams.size(); ix++) {
						Object o = sqlParams.get(ix);
						if (sqlNames.get(ix) != null) {
							W5GlobalFuncParam p1 = r.getGlobalFunc().get_dbFuncParamList().get(i - 1);
							short t = p1.getParamTip();
							int t1 = java.sql.Types.VARCHAR;
							if (t == 1)
								t1 = java.sql.Types.VARCHAR;
							else if (t == 2)
								t1 = java.sql.Types.DATE;
							else if (t == 3)
								t1 = java.sql.Types.DECIMAL;
							else if (t == 4)
								t1 = java.sql.Types.INTEGER;
							else if (t == 5)
								t1 = java.sql.Types.SMALLINT;

							if (p1.getOutFlag() == 1)
								s.registerOutParameter(i, t1);
						} else {
							if (o == null)
								s.setObject(i, null);
							else if (o instanceof Date)
								s.setTimestamp(i, new java.sql.Timestamp(((Date) o).getTime()));
							else
								s.setObject(i, o);
						}
						i++;
					}
					s.execute();

					if (hasOutParam2) {
						// JSONObject jo=new JSONObject();
						Map<String, String> res = new HashMap<String, String>();
						for (int ixx = 0; ixx < sqlNames.size(); ixx++)
							if (sqlNames.get(ixx) != null) {
								Object o = s.getObject(ixx + 1);
								if (o != null) {
									if (o instanceof java.sql.Date) {
										o = GenericUtil.uFormatDate((java.sql.Date) o);
									}
									res.put(sqlNames.get(ixx), o.toString());
								}
							}
						r.setResultMap(res);
					}
					if (s != null)
						s.close();
					r.setSuccess(true);

					if (FrameworkSetting.hibernateCloseAfterWork)
						if (conn != null)
							conn.close();

				}
			});
		} catch (Exception e) {
			throw new IWBException("sql", "DbFunc.Execute", r.getGlobalFuncId(),
					GenericUtil.replaceSql(r.getExecutedSql(), sqlParams), "[20," + r.getGlobalFuncId() + "] Error Executing", e);
		} finally {
			logGlobalFuncAction(action, r, error);
		}
		/*
		 * if(PromisCache.getAppSettingIntValue(r.getScd(), "bpm_flag")!=0){ int
		 * nextBpmActionId = bpmControl(r.getScd(), r.getRequestParams(),
		 * r.getGlobalFunc().get_listBpmStartAction(),
		 * r.getGlobalFunc().get_listBpmEndAction(), 11, 0, 0); if
		 * (nextBpmActionId>-1)r.setNextBpmActions(find(
		 * "select x from BpmAction x,BpmProcessStep s where x.customizationId=s.customizationId and x.customizationId=? and x.activeFlag=1 AND x.prerequisitActionId=? AND x.wizardStepFlag!=0 AND s.actionId=x.actionId"
		 * , r.getScd().get("customizationId"),nextBpmActionId)); }
		 */
	}

	public void bookmarkForm(String dsc, int formId, int userId, int customizationId, W5FormResult formResult) {
		W5FormValue formValue = new W5FormValue();
		formValue.setFormId(formId);
		formValue.setDsc(dsc);
		formValue.setInsertUserId(userId);
		formValue.setCustomizationId(customizationId);
		saveObject(formValue);

		for (W5FormCell c : formResult.getForm().get_formCells())
			if (c.getSourceTip() == 1) {
				String val = formResult.getRequestParams().get(c.getDsc());
				if (val != null && val.length() > 0 && (formId > 0 || val.length() <= 2048)) {
					W5FormValueCell fvc = new W5FormValueCell();
					if (val.length() > 2048)
						val = val.substring(0, 2048);
					fvc.setVal(val);
					fvc.setFormCellId(c.getFormCellId());
					fvc.setFormValueId(formValue.getFormValueId());
					fvc.setCustomizationId((Integer) formResult.getScd().get("customizationId"));
					saveObject(fvc);
				}
			}
		if (formResult.getPkFields() == null)
			formResult.setPkFields(new HashMap());
		formResult.getPkFields().put("id", formValue.getFormValueId());
	}


	public void copyTableRecord(int tableId, int tablePk, String srcSchema, String dstSchema) {
		W5Table t = FrameworkCache.getTable(0, tableId);
		W5TableParam tp = (W5TableParam) find("from W5TableParam t where t.tableId=?", tableId).get(0);
		StringBuilder b = new StringBuilder();
		b.append("insert into ").append(dstSchema).append(".").append(t.getDsc()).append(" select * from ")
				.append(srcSchema).append(".").append(t.getDsc()).append(" where ").append(tp.getExpressionDsc())
				.append("=?");

		Session session = getCurrentSession();

		try {
			SQLQuery query = session.createSQLQuery(b.toString());
			query.setInteger(0, tablePk);
			query.executeUpdate();
		} catch (Exception e) {
			if (FrameworkSetting.debug)
				e.printStackTrace();
			if (dstSchema != null && !dstSchema.equals(FrameworkCache.getAppSettingStringValue(0, "default_schema"))) {
				b.setLength(0);
				b.append("create table ").append(dstSchema).append(".").append(t.getDsc()).append(" as select * from ")
						.append(srcSchema).append(".").append(t.getDsc()).append(" where ")
						.append(tp.getExpressionDsc()).append("=?");
				try {
					SQLQuery query = session.createSQLQuery(b.toString());
					query.setInteger(0, tablePk);
					query.executeUpdate();
					session.createSQLQuery("alter table " + dstSchema + "." + t.getDsc() + " add constraint PK_T"
							+ t.getTableId() + " primary key (" + tp.getExpressionDsc() + ")").executeUpdate();
				} catch (Exception e2) {
					throw new IWBException("sql", "Copy(Insert) Table Record", tableId,
							b.toString() + " --> " + tablePk, e.getMessage(), e);
				}
			} else
				throw new IWBException("sql", "Copy Table Record", tableId, b.toString() + " --> " + tablePk,
						e.getMessage(), e);
		} finally {
			// session.close();
		}
	}

	public String getTableFields4VCS(W5Table t, String prefix) {
		StringBuilder s = new StringBuilder();
		if (t == null || GenericUtil.isEmpty(t.get_tableFieldList()))
			return s.toString();
		for (W5TableField f : t.get_tableFieldList()) {
			if (f.getTabOrder() < 0 || f.getDsc().equals(t.get_tableFieldList().get(0).getDsc()))
				continue;
			if (f.getDsc().equals("version_no") || f.getDsc().equals("insert_user_id")
					|| f.getDsc().equals("insert_dttm") || f.getDsc().equals("version_user_id")
					|| f.getDsc().equals("version_dttm") || f.getDsc().equals("customization_id")
					|| f.getDsc().equals("project_uuid"))
				continue;

			if (s.length() > 0)
				s.append(" || '-iwb-' || ");
			switch (f.getParamTip()) {
			case 1:
				s.append("coalesce(").append(prefix).append(".").append(f.getDsc()).append(",'')");
				break;
			case 4: // integer
			case 5: // boolean
			case 3: // double
				if (f.getNotNullFlag() != 0)
					s.append(prefix).append(".").append(f.getDsc()).append("::text");
				else
					s.append("case when ").append(prefix).append(".").append(f.getDsc())
							.append(" is null then '' else ").append(prefix).append(".").append(f.getDsc())
							.append("::text end");
				break;
			case 2:
				s.append("case when ").append(prefix).append(".").append(f.getDsc())
						.append(" is null then '' else to_char(").append(prefix).append(".").append(f.getDsc())
						.append(",'ddmmyyy-hh24miss') end");
				break;
			default:
				s.append("coalesce(").append(prefix).append(".").append(f.getDsc()).append(",'')");
				break;
			}
		}

		return s.toString();
	}

	public boolean copyFormTableDetail(W5FormResult masterFormResult, W5TableChild tc, String newMasterTablePk,
			String schema, String prefix) {
		Map<String, Object> scd = masterFormResult.getScd();
		W5Table t = FrameworkCache.getTable(scd, tc.getRelatedTableId());
		if (t == null)
			return false;
		Map<String, String> requestParams = new HashMap();
		requestParams.putAll(masterFormResult.getRequestParams());
		W5Table mt = FrameworkCache.getTable(scd, masterFormResult.getForm().getObjectId()); // .get_sourceTable();
		W5FormResult formResult = metaDataDao.getFormResult(scd, t.getDefaultUpdateFormId(), 2, requestParams);
		if (t.getAccessViewTip() == 0 && !FrameworkCache.roleAccessControl(scd, 0)) {
			// throw new PromisException("security","Module", 0, null, "Modul
			// Kontrol: Erişim kontrolünüz
			// yok", null);
			return false;
		}
		if (!GenericUtil.accessControl(scd, t.getAccessViewTip(), t.getAccessViewRoles(), t.getAccessViewUsers())) {
			// throw new PromisException("security","Form",
			// t.getDefaultUpdateFormId(), null, "Tablo
			// Kontrol: Veri görünteleyemezsiniz", null);
			return false;
		}
		/*
		 * if(!PromisUtil.accessControl(scd, t.getAccessInsertTip(),
		 * t.getAccessInsertRoles(), t.getAccessInsertUsers())){ //throw new
		 * PromisException("security","Form", t.getDefaultUpdateFormId(), null,
		 * "Tablo Kontrol: Yeni kayit yapamazsiniz", null); return false; }
		 */

		List<Object> sqlParams = new ArrayList();
		String masterFieldDsc = mt.get_tableFieldMap().get(tc.getTableFieldId()).getDsc();
		String detailFieldDsc = t.get_tableFieldMap().get(tc.getRelatedTableFieldId()).getDsc();
		String detailStaticFieldDsc = tc.getRelatedStaticTableFieldId() > 0
				? t.get_tableFieldMap().get(tc.getRelatedStaticTableFieldId()).getDsc() : null;

		String detailSql = "select x." + t.get_tableParamList().get(0).getExpressionDsc();
		boolean multiKey = false;
		boolean cusFlag = false;
		if (t.get_tableParamList().size() > 1) {
			if (!t.get_tableParamList().get(1).getExpressionDsc().equals("project_uuid")) {
				for (W5TableParam tp : t.get_tableParamList())
					if (!t.get_tableParamList().get(1).getExpressionDsc().equals("project_uuid")) {
						if (!multiKey) {
							multiKey = true;
							continue;
						}
						detailSql += ",x." + tp.getExpressionDsc();
					} else
						cusFlag = true;
			} else
				cusFlag = true;
		}
		detailSql += " from " + t.getDsc() + " x where ";
		if (cusFlag) {
			detailSql += "x.project_uuid=? AND ";
			sqlParams.add(scd.get("projectId"));
		}
		detailSql += "exists(select 1 from " + mt.getDsc() + " q where q." + masterFieldDsc + " = x." + detailFieldDsc // detail
																														// ile
																														// iliski
				+ " AND q." + mt.get_tableParamList().get(0).getExpressionDsc() + " = ?"; // master
																							// kayit
																							// ile
																							// iliski
		sqlParams.add(GenericUtil.uInt(masterFormResult.getPkFields().get("t" + masterFieldDsc)));
		// sqlParams.add(PromisUtil.uInt(masterPk));

		detailSql += DBUtil.includeTenantProjectPostSQL(scd, mt, "q");

		if (detailStaticFieldDsc != null)
			detailSql += " AND x." + detailStaticFieldDsc + "=" + tc.getRelatedStaticTableFieldVal();
		detailSql += ")";
		boolean bt = false;
		for (W5TableField tf : t.get_tableFieldList())
			if (tf.getDsc().equals("tab_order")) {
				detailSql += " order by x.tab_order";
				bt = true;
				break;
			}
		if (!bt) {
			detailSql += " order by x." + t.get_tableFieldList().get(0).getDsc();
		}

		List<Object> rl = executeSQLQuery(detailSql, sqlParams.toArray());
		requestParams.put(detailFieldDsc, GenericUtil.isEmpty(newMasterTablePk)
				? requestParams.get(mt.get_tableParamList().get(0).getDsc()) : newMasterTablePk);
		if (rl != null)
			for (Object o : rl) {
				if (multiKey) {
					int qi = 0;
					for (W5TableParam tp : t.get_tableParamList())
						if (!t.get_tableParamList().get(1).getExpressionDsc().equals("project_uuid")) {
							requestParams.put(tp.getDsc(), ((Object[]) o)[qi++].toString());
						}
				} else
					requestParams.put(t.get_tableParamList().get(0).getDsc(), o.toString());
				copyFormTable(formResult, schema, prefix, true);
				if (!GenericUtil.isEmpty(t.get_tableChildList())) {
					if (!GenericUtil.isEmpty(formResult.getOutputFields()))
						for (String k : formResult.getOutputFields().keySet()) {
							if (formResult.getOutputFields().get(k) != null)
								requestParams.put(k, formResult.getOutputFields().get(k).toString());
						}
					// String pcopyTablePk =
					// requestParams.get(t.get_tableParamList().get(0).getDsc());
					for (W5TableChild tc2 : t.get_tableChildList())
						if (tc2.getCopyStrategyTip() == 1) {
							copyFormTableDetail(formResult, tc2,
									requestParams.get(t.get_tableFieldList().get(0).getDsc()), schema, prefix);
						}
				}
			}
		return true;
		// throw new PromisException("security","Form",
		// t.getDefaultUpdateFormId(), null, "Tablo
		// Kontrol: Veri görünteleyemezsiniz", null);
	}

	public List<Map> getMainTableData(Map<String, Object> scd, int tableId, String tablePk) { // TODO:
																								// yetki
																								// eksik

		int count = 0;
		String customizationId = scd.get("customizationId").toString();
		W5Table table = FrameworkCache.getTable(customizationId, tableId);

		StringBuilder sql = new StringBuilder();
		List lp = new ArrayList();
		lp.add(tablePk);
		sql.append("select t.* ,");

		sql.replace(sql.length() - 1, sql.length(), " from " + table.getDsc() + "  t where");
		for (W5TableParam x : table.get_tableParamList()) {
			sql.append(" t." + x.getExpressionDsc() + "= ? and");
			if (x.getExpressionDsc().equals("project_uuid"))
				lp.add(scd.get("projectId"));
		}
		sql.replace(sql.length() - 3, sql.length(), "");

		return executeSQLQuery2Map(sql.toString(), lp);
	}

	public List<W5TableRecordHelper> findRecordParentRecords(Map<String, Object> scd, int tableId, int tablePk,
			int maxLevel, boolean includeSummarySqlFlag) { // TODO:includeSummarySqlFlag
															// ise yaramiyor
		List<W5TableRecordHelper> l = new ArrayList<W5TableRecordHelper>();
		// if(true)return l; bunu kim neden yapti ??
		W5Table t = FrameworkCache.getTable(scd, tableId);
		W5TableChild tc = null;
		long currentMillis = System.currentTimeMillis();
		Map<String, String> requestParams = new HashMap();
		int ptCount = 0; // parentCount
		int level = 0;
		if (maxLevel == 0)
			maxLevel = 10;
		while (t != null && tablePk != 0 && level < maxLevel) {
			level++;
			W5TableRecordHelper trh = null; // FrameworkCache.getTableCacheValue(t.getCustomizationId(),
											// tableId,tablePk);
			if (trh != null && (currentMillis - trh.getLastAccessTime()) < FrameworkSetting.cacheTimeoutRecord) { // caching
																													// den
																													// aliyorum

				trh.setLastAccessTime(currentMillis);
				l.add(trh);
				t = FrameworkCache.getTable(scd, trh.getParentTableId());
				tablePk = trh.getParentTablePk();
			} else if (!GenericUtil.isEmpty(t.get_tableParamList())) {
				requestParams.put(t.get_tableParamList().get(0).getDsc(), "" + tablePk);
				StringBuilder sql = new StringBuilder(512);
				boolean accessControlFlag = false;
				sql.append("select ");
				if (t.getSummaryRecordSql() == null) // bir tane degeriini
														// koyacagiz
					sql.append("'WARNING!!! Not defined SummarySql' dsc");
				else {
					sql.append(t.getSummaryRecordSql());
					if (!t.getSummaryRecordSql().contains(" dsc,") && !t.getSummaryRecordSql().endsWith(" dsc"))
						sql.append(" dsc");
				}
				if (t.get_tableParentList() != null && t.get_tableParentList().size() > 0) {
					sql.append(",x.");
					tc = t.get_tableParentList().get(0);
					sql.append(t.get_tableFieldMap().get(tc.getRelatedTableFieldId()).getDsc()).append(" ptable_pk");
					if (tc.getRelatedStaticTableFieldId() != 0) {
						sql.append(", x.").append(t.get_tableFieldMap().get(tc.getRelatedStaticTableFieldId()).getDsc())
								.append(" pobject_tip");
						ptCount = 2; // multi:parent
					} else
						ptCount = 1; // single:parent

				} else
					ptCount = 0;
				if (t.getMakeCommentFlag() != 0)
					sql.append(", (select count(1) from iwb.w5_comment cx where cx.table_id=").append(t.getTableId())
							.append(" AND cx.project_uuid='${scd.projectId}' AND cx.table_pk=x.")
							.append(t.get_tableParamList().get(0).getExpressionDsc()).append(") pcomment_count ");
				if (t.getAccessTips() != null && GenericUtil.hasPartInside2(t.getAccessTips(), "0"))
					accessControlFlag = true;
				if (accessControlFlag)
					sql.append(", ac.access_roles, ac.access_users");
				sql.append(" from ").append(t.getDsc()).append(" x");
				if (accessControlFlag)
					sql.append(" left outer join iwb.w5_access_control ac on ac.ACCESS_TIP=0 AND ac.table_id=")
							.append(t.getTableId())
							.append(" AND ac.customization_id=${scd.customizationId} AND cast(ac.table_pk as int8)=x.")
							.append(t.get_tableParamList().get(0).getExpressionDsc());
				sql.append(" where x.").append(t.get_tableParamList().get(0).getExpressionDsc()).append("=${req.")
						.append(t.get_tableParamList().get(0).getDsc()).append("}");
				sql.append(DBUtil.includeTenantProjectPostSQL(scd, t));
				Object[] oz = DBUtil.filterExt4SQL(sql.toString(), scd, requestParams, null);
				Map<String, Object> m = runSQLQuery2Map(oz[0].toString(), (List) oz[1], null);
				trh = new W5TableRecordHelper();
				trh.setTableId(tableId);
				trh.setTablePk(tablePk);
				l.add(trh);

				if (m != null) {
					if (accessControlFlag)
						trh.setViewAccessControl(new W5AccessControlHelper((String) m.get("access_roles"),
								(String) m.get("access_users")));
					if (m.size() == 1) {
						Object o = m.values().iterator().next();
						if (o != null)
							trh.setRecordDsc(o.toString());
					} else
						trh.setRecordDsc((String) m.get("dsc"));
					if (t.getMakeCommentFlag() != 0)
						trh.setCommentCount(GenericUtil.uInt(m.get("pcomment_count")));
					trh.setCachedTime(currentMillis);
					trh.setLastAccessTime(trh.getCachedTime());
					// FrameworkCache.putTableCacheValue(t.getCustomizationId(),
					// tableId, tablePk,
					// trh);//cache le objeyi
					switch (ptCount) {
					case 0:
						return l;
					case 1: // single parent
						t = FrameworkCache.getTable(scd, tc.getTableId());
						trh.setParentTableId(tableId = t.getTableId());
						trh.setParentTablePk(tablePk = GenericUtil.uInt(m.get("ptable_pk")));
						break;
					default: // multi parent
						tableId = -1;
						trh.setParentTablePk(tablePk = GenericUtil.uInt(m.get("ptable_pk")));
						int pobjectTip = GenericUtil.uInt(m.get("pobject_tip"));
						for (W5TableChild tc2 : t.get_tableParentList())
							if (tc2.getRelatedStaticTableFieldVal() == pobjectTip) {
								trh.setParentTableId(tableId = tc2.getTableId());
								t = FrameworkCache.getTable(scd, tableId);
								break;
							}
						if (tableId == -1) { // sorun, parent bulamamis
							W5TableRecordHelper trhError = new W5TableRecordHelper();
							trhError.setRecordDsc("ERROR: parent not found");
							l.add(trhError);
							return l;
						}
					}

				} else
					t = null;
			}
		}
		return l;
	}

	public Object interprateTemplateExpression(Map<String, Object> scd, Map<String, String> requestParams, int tableId,
			int tablePk, StringBuilder tmp) {
		Map<String, String> res = new HashMap<String, String>();
		String result = "";
		// if(tmp.indexOf("${")<0)return res;
		Map<String, W5TableField> resField = new HashMap<String, W5TableField>();
		Set<String> invalidKeys = new HashSet<String>();
		StringBuilder tmp1 = new StringBuilder();
		tmp1.append(tmp);
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList();
		sql.append("select ");
		List<W5TableFieldCalculated> ltfc = find(
				"from W5TableFieldCalculated t where t.projectUuid=? AND t.tableId=? order by t.tabOrder",
				scd.get("projectId"), tableId);
		W5Table t = FrameworkCache.getTable(scd, tableId);
		for (int bas = tmp1.indexOf("${"); bas >= 0; bas = tmp1.indexOf("${", bas + 2)) {
			int bit = tmp1.indexOf("}", bas + 2);
			String subStr = tmp1.substring(bas + 2, bit);
			// if(res.containsKey(subStr))continue; // daha once hesaplandiysa
			// bir daha gerek yok TODO
			if (subStr.startsWith("scd.")) { // session
				Object o = scd.get(subStr.substring(4));
				tmp1.replace(bas, bit + 1, "?"); // .put(subStr, o.toString());
				params.add(o);
			} else if (subStr.startsWith("app.")) { // application settingsden
				String appStr = FrameworkCache.getAppSettingStringValue(scd, subStr.substring(4));
				tmp1.replace(bas, bit + 1, "?"); // .put(subStr, o.toString());
				params.add(appStr);
			} else if (subStr.startsWith("req.")) { // application settingsden
				String reqStr = requestParams.get(subStr.substring(4));
				tmp1.replace(bas, bit + 1, "?"); // .put(subStr, o.toString());
				params.add(reqStr);
			} else if (subStr.startsWith("tbl.")
					|| (subStr.startsWith("lnk.") && subStr.substring(4).replace(".", "&").split("&").length == 1)) { // direk
																														// tablodan
																														// veya
																														// link'in
																														// ilk
																														// kaydi
																														// ise
				String newSubStr = subStr.substring(4);
				boolean fieldFound = false;
				for (W5TableField tf : t.get_tableFieldList())
					if (tf.getDsc().equals(newSubStr)) {
						tmp1.replace(bas, bit + 1, "x." + newSubStr); // .put(subStr,
																		// o.toString());
						fieldFound = true;
						break;
					}

				if (!fieldFound)
					invalidKeys.add(subStr);
				;
			} else if (subStr.startsWith("clc.")) {
				boolean fieldFound = false;
				String newSubStr = subStr.substring(4);
				for (W5TableFieldCalculated tfc : ltfc)
					if (tfc.getDsc().equals(newSubStr)) {
						String sqlCode = tfc.getSqlCode();
						W5TableField ntf = new W5TableField(tfc.getTableFieldCalculatedId());
						ntf.setDefaultControlTip((short) -1);
						if (sqlCode.contains("${")) {
							Object[] oz = DBUtil.filterExt4SQL(sqlCode, scd, requestParams, null);
							sqlCode = (String) oz[0];
							if (oz.length > 1)
								params.addAll((List) oz[1]);
						}
						tmp1.replace(bas, bit + 1, "(" + sqlCode + ")"); // .put(subStr,
																			// o.toString());
						fieldFound = true;
						break;
					}
				if (!fieldFound)
					invalidKeys.add(subStr);
				;
			} else if (subStr.startsWith("lnk.") && subStr.substring(4).replace(".", "&").split("&").length > 1) { // burda
																													// bu
																													// field
																													// ile
																													// olan
																													// baglantiyi
																													// cozmek
																													// lazim
																													// TODO
				String newSubStr = subStr.substring(4);

				String[] sss = newSubStr.replace(".", "&").split("&");
				if (sss.length > 1) { // TODO kaldirilmasi lazim
					W5Table newT = t;
					StringBuilder newSub = new StringBuilder();
					StringBuilder newSub2 = new StringBuilder();
					boolean foundSt = false;
					for (int isss = 0; isss < sss.length - 1; isss++) {
						if (isss > 0) {
							newSub2.setLength(0);
							newSub2.append(newSub);
							newSub.setLength(0);
						}
						for (W5TableField tf : newT.get_tableFieldList())
							if (tf.getDsc().equals(sss[isss])) {
								foundSt = false;
								if (tf.getDefaultControlTip() == 7 || tf.getDefaultControlTip() == 9 || tf
										.getDefaultControlTip() == 10 /*
																		 * || tf.
																		 * getDefaultControlTip
																		 * ()==
																		 * 15
																		 */) { // sub
																			// table
									W5Table st = FrameworkCache.getTable(scd, tf.getDefaultLookupTableId());
									if (st == null)
										break; // HATA: gerekli bir alt kademe
												// tabloya ulasilamadi
									int dltId = 0;
									for (W5TableField stf : st.get_tableFieldList())
										if (stf.getDsc().equals(sss[isss + 1])) {
											dltId = stf.getDefaultLookupTableId();
											foundSt = true;
											break;
										}
									if (!foundSt)
										break; // HATA: bir sonraki field
												// bulunamadi
									newSub.append("(select ");
									newSub.append("y").append(isss).append(".").append(sss[isss + 1]);
									newSub.append(" from ").append(st.getDsc()).append(" y").append(isss)
											.append(" where y").append(isss).append(".")
											.append(st.get_tableFieldList().get(0).getDsc()).append("=")
											.append(isss == 0 ? ("x." + sss[isss]) : newSub2);
									if (st.get_tableFieldList().size() > 1)
										for (W5TableField wtf : st.get_tableFieldList())
											if (wtf.getDsc().equals("project_uuid")) {
												newSub.append(" AND y").append(isss).append(".project_uuid=?");
												params.add(scd.get("projectId"));
												break;
											}

									newSub.append(")");
									newT = st;
								}
								break;
							}
						if (!foundSt) { // bulamamis uygun sey
							break;
						}
					}
					if (foundSt && newSub.length() > 0) {
						tmp1.replace(bas, bit + 1, "(" + newSub.toString() + ")"); // .put(subStr,
																					// o.toString());
					}
				}
			}
		}
		sql.append(tmp1).append(" xxx from ").append(t.getDsc()).append(" x where x.")
				.append(t.get_tableParamList().get(0).getExpressionDsc()).append("=?");
		params.add(tablePk);
		sql.append(DBUtil.includeTenantProjectPostSQL(scd, t));
		Map<String, Object> qres = runSQLQuery2Map(sql.toString(), params, null);
		return GenericUtil.isEmpty(qres) ? "" : qres.get("xxx");
	}

	public Map<String, String> interprateTemplate(Map<String, Object> scd, Map<String, String> requestParams,
			int tableId, int tablePk, StringBuilder tmp, boolean replace, int smsMailReplaceTip, int conversionTip) {
		Map<String, String> res = new HashMap<String, String>();
		if (conversionTip == 4) {
			Object o = interprateTemplateExpression(scd, requestParams, tableId, tablePk, tmp);
			res.put("result", o.toString());
			if (replace) {
				tmp.setLength(0);
				tmp.append(o);
			}
			return res;
		}
		if (tmp.indexOf("${") < 0)
			return res;
		Map<String, W5TableField> resField = new HashMap<String, W5TableField>();
		Set<String> invalidKeys = new HashSet<String>();
		// ${req.param} -> request'ten ne geldiyse
		// ${scd.param} -> session'dan ne geldiyse
		// ${app.param} -> app_setting'ten ne geldiyse
		// ${lcl.locale_msg_key} -> locale_msg tablosundan bakilacak
		// ${tbl.field_name} -> direk field ismi
		// ${lnk.field_name1.field_name2} -> link oldugu tablodaki field ismi
		// ${lnk.field_name1.field_name2.field_name3} -> link oldugu tablodaki
		// field in linkindeki diger
		// vs.vs.
		// smsMailReplaceTip : 0->yok, 1:sms, 2:mail
		// conversionTip : 0->Serbest, 1: lookup-mapping, 2: sql, 3: javascript,
		// 4: expression, 5:
		// serbest&''
		Set<Integer> smsMailTableIds = null;
		if (smsMailReplaceTip != 0) {
			smsMailTableIds = new HashSet<Integer>();
			String qs = FrameworkCache.getAppSettingStringValue(scd, "sms_mail_table_ids");
			if (qs != null) {
				String[] oqs = qs.split(",");
				if (oqs != null)
					for (String toqs : oqs)
						smsMailTableIds.add(GenericUtil.uInteger(toqs));
			}
		}
		String fieldPrefix = "fx_q1_";
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList();
		sql.append("select ");
		int field_cnt = 1;
		List<W5TableFieldCalculated> ltfc = find(
				"from W5TableFieldCalculated t where t.projectUuid=? AND t.tableId=? order by t.tabOrder",
				FrameworkCache.getProjectId(scd.get("projectId"), "15." + tableId), tableId);
		W5Table t = FrameworkCache.getTable(scd, tableId);
		for (int bas = tmp.indexOf("${"); bas >= 0; bas = tmp.indexOf("${", bas + 2)) {
			if (bas > 0 && tmp.charAt(bas - 1) == '$')
				continue;
			int bit = tmp.indexOf("}", bas + 2);
			String subStr = tmp.substring(bas + 2, bit);
			if (res.containsKey(subStr))
				continue; // daha once hesaplandiysa bir daha gerek yok
			if (subStr.startsWith("scd.")) { // session
				Object o = scd.get(subStr.substring(4));
				if (o != null)
					res.put(subStr, o.toString());
			} else if (subStr.startsWith("app.")) { // application settingsden
				String appStr = FrameworkCache.getAppSettingStringValue(scd, subStr.substring(4));
				if (appStr != null)
					res.put(subStr, appStr);
			} else if (subStr.startsWith("req.")) { // requestten
				String reqStr = requestParams.get(subStr.substring(4));
				if (reqStr != null)
					res.put(subStr, reqStr);
			} else if (subStr.startsWith("tbl.") || (smsMailReplaceTip == 0 && subStr.startsWith("lnk.")
					&& conversionTip != 0 && subStr.substring(4).replace(".", "&").split("&").length == 1)) { // direk
																												// tablodan
																												// veya
																												// link'in
																												// ilk
																												// kaydi
																												// ise
				String newSubStr = subStr.substring(4);
				for (W5TableField tf : t.get_tableFieldList())
					if (tf.getDsc().equals(newSubStr)) {
						if (tf.getDefaultControlTip() == 15) {
							W5Table st = FrameworkCache.getTable(scd, tf.getDefaultLookupTableId());
							if (st != null && smsMailReplaceTip != 0 && smsMailTableIds.contains(st.getTableId())) { // eger
																														// sms/mail
																														// isi
																														// ve
																														// bu
																														// bir
																														// link
																														// ise
								res.put(subStr, fieldPrefix + field_cnt);
								resField.put(subStr, tf);
								sql.append("iwb.fnc_sms_mail_adress_multi(?,").append(st.getTableId()).append(",")
										.append("x.").append(newSubStr).append(",").append(smsMailReplaceTip)
										.append(") ").append(fieldPrefix).append(field_cnt).append(",");
								;
								params.add(scd.get("userRoleId"));
								field_cnt++;
							}
						} else {
							res.put(subStr, fieldPrefix + field_cnt);
							resField.put(subStr, tf);
							if (tf.getFieldTip() == 2)
								sql.append("to_char(x.").append(newSubStr).append(",'dd/mm/yyyy')");
							else
								sql.append("x.").append(newSubStr);
							sql.append(" ").append(fieldPrefix).append(field_cnt).append(",");
							field_cnt++;
						}
						break;
					}
				if (!res.containsKey(subStr))
					invalidKeys.add(subStr);
				;
			} else if (subStr.startsWith("lcl.")) { // locale msg key
				continue;
			} else if (subStr.startsWith("clc.")) { // calculated field
				String newSubStr = subStr.substring(4);
				for (W5TableFieldCalculated tfc : ltfc)
					if (tfc.getDsc().equals(newSubStr)) {
						String sqlCode = tfc.getSqlCode();
						res.put(subStr, fieldPrefix + field_cnt);
						W5TableField ntf = new W5TableField(tfc.getTableFieldCalculatedId());
						ntf.setDefaultControlTip((short) -1);
						resField.put(subStr, ntf);
						if (sqlCode.contains("${")) {
							Object[] oz = DBUtil.filterExt4SQL(sqlCode, scd, requestParams, null);
							sqlCode = oz[0].toString();
							if (oz.length > 1)
								params.addAll((List) oz[1]);
						}
						if (tfc.getFieldTip() == 2)
							sql.append("to_char((").append(sqlCode).append("),'dd/mm/yyyy')");
						else
							sql.append("(").append(sqlCode).append(")");
						sql.append(" ").append(fieldPrefix).append(field_cnt).append(",");
						field_cnt++;
						break;
					}
				if (!res.containsKey(subStr))
					invalidKeys.add(subStr);
			} else if (subStr.startsWith("lnk.")) { // burda bu field ile olan
													// baglantiyi cozmek lazim
				String newSubStr = subStr.substring(4);

				String[] sss = newSubStr.replace(".", "&").split("&");
				if (sss.length > 1) {
					W5Table newT = t;
					StringBuilder newSub = new StringBuilder();
					StringBuilder newSub2 = new StringBuilder();
					for (int isss = 0; isss < sss.length - 1; isss++) {
						if (isss > 0) {
							newSub2.setLength(0);
							newSub2.append(newSub);
							newSub.setLength(0);
						}
						for (W5TableField tf : newT.get_tableFieldList())
							if (tf.getDsc().equals(sss[isss])) {
								if (tf.getDefaultControlTip() == 7 || tf.getDefaultControlTip() == 9 || tf
										.getDefaultControlTip() == 10 /*
																		 * || tf.
																		 * getDefaultControlTip
																		 * ()==
																		 * 15
																		 */) { // sub
																			// table
									W5Table st = FrameworkCache.getTable(scd, tf.getDefaultLookupTableId());
									if (st == null)
										break; // HATA: gerekli bir alt kademe
												// tabloya ulasilamadi
									boolean foundSt = false;
									boolean summaryMust = false;
									int dltId = 0;
									for (W5TableField stf : st.get_tableFieldList())
										if (stf.getDsc().equals(sss[isss + 1])) {
											summaryMust = (conversionTip == 0)
													&& (stf.getDefaultControlTip() == 7
															|| stf.getDefaultControlTip() == 9
															|| stf.getDefaultControlTip() == 10)
													&& stf.getDefaultLookupTableId() != 0;
											dltId = stf.getDefaultLookupTableId();
											foundSt = true;
											break;
										}
									if (!foundSt)
										break; // HATA: bir sonraki field
												// bulunamadi
									newSub.append("(select ");
									if (isss == sss.length - 2 && summaryMust) { // burda
																					// t'deki
																					// summary
																					// record
																					// sql
																					// varsa
										if (smsMailReplaceTip != 0) {
											if (smsMailTableIds.contains(dltId)) // eger
																					// sms/mail
																					// isi
																					// ve
																					// bu
																					// bir
																					// link
																					// ise
												newSub.append("iwb.fnc_sms_mail_adress(?,").append(dltId).append(",")
														.append("y").append(isss).append(".").append(sss[isss + 1])
														.append(",").append(smsMailReplaceTip).append(")");
											else
												break; // HATA: sms mail tipinde
														// olmasi gereken link,
														// degil
										} else
											newSub.append("iwb.fnc_lookup_table_summary(?,").append(dltId).append(",")
													.append("y").append(isss).append(".").append(sss[isss + 1])
													.append(")");
										params.add(scd.get("userRoleId"));
									} else
										newSub.append("y").append(isss).append(".").append(sss[isss + 1]);
									newSub.append(" from ").append(st.getDsc()).append(" y").append(isss)
											.append(" where y").append(isss).append(".")
											.append(st.get_tableFieldList().get(0).getDsc()).append("=")
											.append(isss == 0 ? ("x." + sss[isss]) : newSub2);
									if (st.get_tableFieldList().size() > 1)
										for (W5TableField wtf : st.get_tableFieldList())
											if (wtf.getDsc().equals("project_uuid")) {
												newSub.append(" AND y").append(isss).append(".project_uuid=?");
												params.add(scd.get("projectId"));
												break;
											}

									newSub.append(")");
									newT = st;
								}
								break;
							}
						if (newSub.length() == 0) { // bulamamis uygun sey
							break;
						}
					}
					if (newSub.length() > 0) {
						sql.append(newSub).append(" ").append(fieldPrefix).append(field_cnt).append(",");
						res.put(subStr, fieldPrefix + field_cnt);
						field_cnt++;
						for (W5TableField tf : newT.get_tableFieldList())
							if (tf.getDsc().equals(sss[sss.length - 1]))
								resField.put(subStr, tf);
					}
				} else if (sss.length == 1) { // direk lnk.field_name seklinde
												// olanlar icin
					for (W5TableField tf : t.get_tableFieldList())
						if (tf.getDsc().equals(sss[0])) {
							if ((conversionTip == 0) && (tf.getDefaultControlTip() == 7
									|| tf.getDefaultControlTip() == 9 || tf.getDefaultControlTip() == 10)
									&& tf.getDefaultLookupTableId() != 0) { // sub
																			// table
								W5Table st = FrameworkCache.getTable(scd, tf.getDefaultLookupTableId());
								if (st == null)
									break; // HATA: gerekli bir alt kademe
											// tabloya ulasilamadi

								if (smsMailReplaceTip != 0) {
									if (smsMailTableIds.contains(tf.getDefaultLookupTableId())) // eger
																								// sms/mail
																								// isi
																								// ve
																								// bu
																								// bir
																								// link
																								// ise
										sql.append("iwb.fnc_sms_mail_adress(?,").append(tf.getDefaultLookupTableId())
												.append(",").append("x.").append(sss[0]).append(",")
												.append(smsMailReplaceTip).append(")");
									else
										break; // HATA: sms mail tipinde olmasi
												// gereken link, degil
								} else
									sql.append("iwb.fnc_lookup_table_summary(?,").append(tf.getDefaultLookupTableId())
											.append(",").append("x.").append(sss[0]).append(")");
								params.add(scd.get("userRoleId"));
							} else
								sql.append("x.").append(sss[0]);
							sql.append(" ").append(fieldPrefix).append(field_cnt).append(",");
							res.put(subStr, fieldPrefix + field_cnt);
							field_cnt++;
						}
				}
				if (!res.containsKey(subStr))
					invalidKeys.add(subStr);

			} else
				invalidKeys.add(subStr);
			;
		}
		Map<String, String> newRes = new HashMap<String, String>();
		Map<String, Object> qres = null;

		if (field_cnt > 1) {
			sql.append("1 from ").append(t.getDsc()).append(" x where x.")
					.append(t.get_tableFieldList().get(0).getDsc()).append("=?");
			params.add(tablePk);
			if (t.get_tableFieldList().size() > 1 && (t.getTableId() != 338))
				for (W5TableField tf2 : t.get_tableFieldList())
					if (tf2.getDsc().equals("project_uuid")) {
						sql.append(" AND x.project_uuid=?");
						params.add(scd.get("projectId"));
						break;
					}
			qres = runSQLQuery2Map(sql.toString(), params, null);
		} else
			qres = new HashMap();

		for (String keyz : res.keySet()) {
			if (res.get(keyz) != null) {
				if (keyz.startsWith("req.") || keyz.startsWith("scd.") || keyz.startsWith("app.")
						|| keyz.equals("company_logo"))
					newRes.put(keyz, res.get(keyz));
				else if (qres != null && qres.get(res.get(keyz)) != null)
					newRes.put(keyz, qres.get(res.get(keyz)).toString());
			}
		}
		if (replace)
			for (int bas = tmp.indexOf("${"); bas >= 0; bas = tmp.indexOf("${", bas + 2)) {
				if (bas > 0 && tmp.charAt(bas - 1) == '$') {
					tmp.replace(bas, bas + 1, "");
					continue;
				}
				int bit = tmp.indexOf("}", bas + 2);
				String subStr = tmp.substring(bas + 2, bit);
				if (subStr.startsWith("lcl.")) { // locale msg key
					tmp.replace(bas, bit + 1, LocaleMsgCache.get2(scd, subStr.substring(4)));
					continue;
				}
				String resStr = newRes.get(subStr);
				if (resStr == null) {
					if (invalidKeys.contains(subStr)) {
						resStr = "[ERR:" + GenericUtil.uStrMax(subStr, 50) + "]";
					} else
						resStr = "";
				} else if (conversionTip == 0) { // eger sadece serbest donusum
													// var ise
					W5TableField tf = resField.get(subStr);
					if (tf != null)
						switch (tf.getDefaultControlTip()) {
						case 6: // combo static
							if (tf.getDefaultLookupTableId() != 0) {
								W5LookUp lu = FrameworkCache.getLookUp(scd, tf.getDefaultLookupTableId());
								if (lu != null) {
									resStr = lu.get_detayMap().get(resStr).getDsc();
									resStr = LocaleMsgCache.get2(scd, resStr);
								}
							}
							break;
						case 8: // tree-combo static
							if (tf.getDefaultLookupTableId() != 0) {
								W5LookUp lu = FrameworkCache.getLookUp(scd, tf.getDefaultLookupTableId());
								if (lu != null) {
									String[] sss = resStr.split(",");
									if (sss != null && sss.length > 0) {
										String tstr = "";
										for (String s : sss) {
											String qs = lu.get_detayMap().get(s).getDsc();
											qs = LocaleMsgCache.get2(scd, qs);
											tstr += qs + ", ";
										}
										if (tstr.length() > 2)
											resStr = tstr.substring(0, tstr.length() - 2);
									}
								}
							}
							break;
						case -1: // demek ki, calculated Field
							break;
						}
				}
				tmp.replace(bas, bit + 1, conversionTip == 0 ? resStr : "'" + resStr + "'");
			}
		else if (conversionTip == 0)
			for (String subStr : resField.keySet()) { // replace yok ama
														// herseyin degismesi
														// gerek
				W5TableField tf = resField.get(subStr);
				if (tf != null)
					switch (tf.getDefaultControlTip()) {
					case 6: // combo static
						if (tf.getDefaultLookupTableId() != 0) {
							W5LookUp lu = FrameworkCache.getLookUp(scd, tf.getDefaultLookupTableId());
							if (lu != null) {
								String resStr = lu.get_detayMap().get(newRes.get(subStr)).getDsc();
								resStr = LocaleMsgCache.get2(scd, resStr);
								newRes.put(subStr, resStr);
							}
						}
						break;
					case 8: // tree-combo static
						if (tf.getDefaultLookupTableId() != 0) {
							W5LookUp lu = FrameworkCache.getLookUp(scd, tf.getDefaultLookupTableId());
							if (lu != null) {
								String[] sss = newRes.get(subStr).split(",");
								if (sss != null && sss.length > 0) {
									String tstr = "";
									for (String s : sss) {
										String qs = lu.get_detayMap().get(s).getDsc();
										qs = LocaleMsgCache.get2(scd, qs);
										tstr += qs + ", ";
									}
									if (tstr.length() > 2)
										newRes.put(subStr, tstr.substring(0, tstr.length() - 2));
								}
							}
						}
						break;
					}
			}
		switch (conversionTip) {
		case 3: // JavaScript
			Object resq = scriptEngine.executeScript(scd, requestParams, tmp.toString(), null, "cnv_it_"+tableId+"_"+tablePk );

			tmp.setLength(0);
			if (resq != null)
				tmp.append(resq);
			if (tmp.length() > 2) {
				int tl = tmp.length();
				if (tmp.charAt(tl - 2) == '.' && tmp.charAt(tl - 1) == '0') {
					Integer nv = GenericUtil.uInteger(tmp.substring(0, tl - 2));
					if (nv != null)
						tmp.setLength(tl - 2);
				}
			}
			break;
		}
		return newRes;
	}

	public boolean accessUserFieldControl(W5Table t, String accessUserFields, Map<String, Object> scd,
			Map<String, String> requestParams, String paramSuffix) {
		if (paramSuffix == null)
			paramSuffix = "";
		if (accessUserFields == null) {
			return true;
		}
		StringBuilder sql = new StringBuilder();
		sql.append("select 1 a from ").append(t.getDsc()).append(" t where (");
		String[] fieldIdz = accessUserFields.split(",");
		List<Object> params = new ArrayList();
		boolean bq = false;
		for (String s : fieldIdz) {
			int tableFieldId = GenericUtil.uInt(s);
			if (tableFieldId < 0) { // TODO: for conditionSQL
				continue;
			}
			W5TableField tf = t.get_tableFieldMap().get(tableFieldId);
			if (tf != null) {
				if (bq)
					sql.append(" OR ");
				else
					bq = true;
				sql.append("t.").append(tf.getDsc()).append("=?");
				params.add(scd.get("userId"));
			} /*
				 * else { Integer tbId =
				 * FrameworkCache.wTableFieldMap.get(tableFieldId);
				 * if(tbId!=null){ W5Table t2 =
				 * FrameworkCache.getTable(t.getCustomizationId(), tbId);
				 * if(t2!=null &&
				 * !GenericUtil.isEmpty(t2.get_tableChildList())){ W5TableField
				 * tf2 = t2.get_tableFieldMap().get(tableFieldId);
				 * for(W5TableChild
				 * tc:t2.get_tableChildList())if(tc.getRelatedTableId()==t.
				 * getTableId()){ if(bq)sql.append(" OR ");else bq=true;
				 * if(tc.getRelatedStaticTableFieldId()>0){
				 * sql.append("(t.").append(t.get_tableFieldMap().get(tc.
				 * getRelatedStaticTableFieldId()).getDsc()).append("=").append(
				 * tc.getRelatedStaticTableFieldVal()).append(" AND "); }
				 * sql.append("exists(select 1 from "
				 * ).append(t2.getDsc()).append(
				 * " hq where hq.customization_id=? AND hq."
				 * ).append(tf2.getDsc()).append("=?").append(" AND hq.")
				 * .append(t2.get_tableFieldMap().get(tc.getTableFieldId()).
				 * getDsc()).append("=t.").append(t.get_tableFieldMap().get(tc.
				 * getRelatedTableFieldId()).getDsc());
				 * if(tc.getRelatedStaticTableFieldId()>0){ sql.append(")"); }
				 * sql.append(")"); params.add(scd.get("customizationId"));
				 * params.add(scd.get("userId")); break; } } } }
				 */
		}
		sql.append(")");

		Map errorMap = new HashMap();
		for (W5TableParam x : t.get_tableParamList()) {
			sql.append("AND t.").append(x.getExpressionDsc()).append(" = ? ");
			Object psonuc = GenericUtil.prepareParam((W5Param) x, scd, requestParams, (short) -1, null, (short) 1,
					x.getDsc() + paramSuffix, null, errorMap);
			params.add(psonuc);
		}
		if (!errorMap.isEmpty())
			return true; // baska yerde yapsin error
		Map<String, Object> m = runSQLQuery2Map(sql.toString(), params, null);
		return m == null || m.isEmpty();
	}

	public List getRecordPictures(Map<String, Object> scd, int tableId, String tablePk) {
		List<Object[]> l = executeSQLQuery(
				"select x.file_attachment_id from iwb.w5_file_attachment x where x.customization_Id=? and x.table_Id=? and x.table_pk=? and exists(select 1 from gen_file_type tt where tt.customization_id=x.customization_id and tt.image_flag=1 and tt.file_type_id=x.file_type_id)",
				scd.get("customizationId"), tableId, tablePk);
		List<W5FileAttachment> fa = new ArrayList<W5FileAttachment>();
		if (l != null)
			for (Object o : l)
				fa.addAll(find("from W5FileAttachment t where t.customizationId=? and t.fileAttachmentId=?",
						scd.get("customizationId"), GenericUtil.uInt(o)));
		return fa;
	}

	public List<Object[]> getFileType(Map<String, Object> scd, int image_flag) {
		List params = new ArrayList<Object>();
		params.add(scd.get("customizationId"));
		params.add(image_flag);
		params.add(image_flag);
		List l = executeSQLQuery2Map(
				"select x.dsc dsc,x.file_type_id id from gen_file_type x where x.customization_id = ? and (x.image_flag = ? or ? is null)",
				params);
		return l;
	}




	public boolean conditionRecordExistsCheck(Map<String, Object> scd, Map<String, String> requestParams, int tableId,
			int tablePk, String conditionSqlCode) {
		W5Table t = FrameworkCache.getTable(scd, tableId);
		StringBuilder sql = new StringBuilder();
		sql.append("select 1 from ").append(t.getDsc()).append(" x where x.")
				.append(t.get_tableParamList().get(0).getExpressionDsc()).append("=?");
		List params = new ArrayList();
		params.add(tablePk);
		sql.append(DBUtil.includeTenantProjectPostSQL(scd, t)).append(" AND ");
		Object[] oz = DBUtil.filterExt4SQL(conditionSqlCode, scd, new HashMap(), null);
		sql.append(oz[0]);
		if (oz.length > 1 && oz[1] != null)
			params.addAll((List) oz[1]);
		List l = executeSQLQuery2(sql.toString(), params);
		return !GenericUtil.isEmpty(l);
	}


	private void addPostQueryFields(W5QueryResult queryResult, StringBuilder sql2, AtomicInteger paramIndex) {
		W5Query query = queryResult.getQuery();
		W5Table mainTable = queryResult.getMainTable();
		int customizationId = (Integer) queryResult.getScd().get("customizationId");
		String pkFieldName = query.getQueryTip() == 9 ? query.get_queryFields().get(0).getDsc() : "pkpkpk_id";
		if (FrameworkSetting.vcs && mainTable
				.getVcsFlag() != 0 /*
									 * && query.getSqlSelect().startsWith("x.*")
									 */) { // VCS
			// sql2.append(",(select cx.vcs_object_status_tip from
			// iwb.w5_vcs_object cx where
			// cx.table_id=").append(query.getMainTableId()).append(" AND
			// cx.customization_id=").append(customizationId).append(" AND
			// cx.table_pk=z.pkpkpk_id)
			// ").append(FieldDefinitions.queryFieldName_Vcs).append(" ");
			//
			// sql2.append(",fnc_vcs_status(").append(customizationId).append(",'").append(queryResult.getScd().get("projectId")).append("',").append(query.getMainTableId()).append(",z.pkpkpk_id,").append(FrameworkUtil.getTableFields4VCS(mainTable,"z")).append(")
			// ").append(FieldDefinitions.queryFieldName_Vcs).append(" ");
			sql2.append(",iwb.fnc_vcs_status2(").append(customizationId).append(",'")
					.append(queryResult.getScd().get("projectId")).append("',").append(query.getMainTableId())
					.append(",z.").append(pkFieldName).append(") ").append(FieldDefinitions.queryFieldName_Vcs)
					.append(" ");
			W5QueryField field = new W5QueryField();
			field.setDsc(FieldDefinitions.queryFieldName_Vcs);
			queryResult.getPostProcessQueryFields().add(field);
		}
		if (FrameworkCache.getAppSettingIntValue(queryResult.getScd(), "row_based_security_flag") != 0
				&& mainTable.getAccessTips() != null && mainTable.getAccessTips().indexOf("0") > -1
				&& (queryResult.getQueryColMap() == null || queryResult.getQueryColMap()
						.containsKey(FieldDefinitions.queryFieldName_RowBasedSecurity))) {
			sql2.append(",(select 1 from iwb.w5_access_control cx where cx.table_id=").append(query.getMainTableId())
					.append(" AND cx.customization_id=").append(customizationId).append(" AND cx.table_pk=z.")
					.append(pkFieldName).append(" limit 1) ").append(FieldDefinitions.queryFieldName_RowBasedSecurity)
					.append(" ");
			W5QueryField field = new W5QueryField();
			field.setDsc(FieldDefinitions.queryFieldName_RowBasedSecurity);
			queryResult.getPostProcessQueryFields().add(field);
		}
		if (FrameworkCache.getAppSettingIntValue(queryResult.getScd(), "file_attachment_flag") != 0
				&& mainTable.getFileAttachmentFlag() != 0 && FrameworkCache.roleAccessControl(queryResult.getScd(), 101)
				&& (queryResult.getQueryColMap() == null
						|| queryResult.getQueryColMap().containsKey(FieldDefinitions.queryFieldName_FileAttachment))) { // fileAttachment
			// sql2.append(",(select 1 from iwb.w5_file_attachment cx where
			// rownum=1 AND
			// cx.table_id=").append(query.getMainTableId()).append(" AND
			// cx.customization_id=").append(customizationId).append(" AND
			// cx.table_pk=to_char(z.pkpkpk_id)) pkpkpk_faf ");
			sql2.append(",(select count(1) from iwb.w5_file_attachment cx where cx.table_id=")
					.append(query.getMainTableId()).append(" AND cx.customization_id=").append(customizationId)
					.append(" AND cx.table_pk=(z.").append(pkFieldName).append(")::text limit 10) ")
					.append(FieldDefinitions.queryFieldName_FileAttachment).append(" ");
			W5QueryField field = new W5QueryField();
			field.setDsc(FieldDefinitions.queryFieldName_FileAttachment);
			queryResult.getPostProcessQueryFields().add(field);
		}
		if (FrameworkCache.getAppSettingIntValue(queryResult.getScd(), "make_comment_flag") != 0
				&& mainTable.getMakeCommentFlag() != 0 && (queryResult.getQueryColMap() == null
						|| queryResult.getQueryColMap().containsKey(FieldDefinitions.queryFieldName_Comment))) { // comment
			// sql2.append(",(select 1 from iwb.w5_comment cx where rownum=1 AND
			// cx.table_id=").append(query.getMainTableId()).append(" AND
			// cx.customization_id=").append(customizationId).append(" AND
			// cx.table_pk=z.").append(pkFieldName).append(") pkpkpk_cf ");
			W5QueryField field = new W5QueryField();
			field.setDsc(FieldDefinitions.queryFieldName_Comment);
			if (FrameworkCache.getAppSettingIntValue(queryResult.getScd(), "make_comment_summary_flag") != 0) {
				sql2.append(
						",(select cx.comment_count||';'||cxx.comment_user_id||';'||to_char(cxx.comment_dttm,'dd/mm/yyyy hh24:mi:ss')||';'||cx.view_user_ids||'-'||cxx.dsc from iwb.w5_comment_summary cx, iwb.w5_comment cxx where cx.table_id=")
						.append(query.getMainTableId()).append(" AND cx.project_uuid='")
						.append(queryResult.getScd().get("projectId")).append("'  AND cx.table_pk::int=z.")
						.append(pkFieldName)
						.append(" AND cxx.customization_id=cx.customization_id AND cxx.comment_id=cx.last_comment_id) pkpkpk_cf ");
				field.setPostProcessTip((short) 48); // extra code :
														// commentCount-commentUserId-lastCommentDttm-viewUserIds-msg
			} else {
				sql2.append(",(select count(1) from iwb.w5_comment cx where cx.table_id=")
						.append(query.getMainTableId()).append(" AND cx.project_uuid='")
						.append(queryResult.getScd().get("projectId")).append("'  AND cx.table_pk::int=z.")
						.append(pkFieldName).append(" limit 10) ").append(FieldDefinitions.queryFieldName_Comment)
						.append(" ");
			}
			queryResult.getPostProcessQueryFields().add(field);
		}
		if (FrameworkCache.getAppSettingIntValue(queryResult.getScd(), "approval_flag") != 0
				&& mainTable.get_approvalMap() != null && !mainTable.get_approvalMap().isEmpty()
				&& (queryResult.getQueryColMap() == null
						|| queryResult.getQueryColMap().containsKey(FieldDefinitions.queryFieldName_Approval))) { // approval
																													// Record
			sql2.append(
					",(select cx.approval_record_id||';'||cx.approval_id||';'||cx.approval_step_id||';'||coalesce(cx.approval_roles,'')||';'||coalesce(cx.approval_users,'') from iwb.w5_approval_record cx where cx.table_id=")
					.append(query.getMainTableId()).append(" AND cx.customization_id=").append(customizationId)
					.append(" AND cx.table_pk=z.").append(pkFieldName).append(" limit 1) ")
					.append(FieldDefinitions.queryFieldName_Approval).append(" ");
			W5QueryField field = new W5QueryField();
			field.setDsc(FieldDefinitions.queryFieldName_Approval);
			field.setPostProcessTip((short) 49); // approvalPostProcessTip2
			queryResult.getPostProcessQueryFields().add(field);
			if (FrameworkCache.getAppSettingIntValue(queryResult.getScd(), "toplu_onay") != 0) {
				sql2.append(",(select cx.version_no from iwb.w5_approval_record cx where cx.table_id=")
						.append(query.getMainTableId()).append(" AND cx.customization_id=").append(customizationId)
						.append(" AND cx.table_pk=z.").append(pkFieldName).append(" limit 1) ")
						.append(FieldDefinitions.queryFieldName_ArVersionNo).append(" ");
				field = new W5QueryField();
				field.setDsc(FieldDefinitions.queryFieldName_ArVersionNo);
				queryResult.getPostProcessQueryFields().add(field);
			}
		}
	}

	public void saveObject2(Object o, Map<String, Object> scd) {
		saveObject(o);
		if (o instanceof Log5Notification) {
			Log5Notification n = (Log5Notification) o;
			if (n.getTableId() != 0 && n.getTablePk() != 0) {
				if (scd == null) {
					scd = new HashMap(); // TODO: boyle olmaz, scd'yi al
					scd.put("userId", n.getActionUserId());
					scd.put("customizationId", n.getCustomizationId());
					scd.put("locale", "tr");
				}
				n.set_tableRecordList(findRecordParentRecords(scd, n.getTableId(), n.getTablePk(), 0, true));
			}
			UserUtil.publishNotification(n, false);
		}
	}

	public List<W5TableChildHelper> findRecordChildRecords(Map<String, Object> scd, int tableId, int tablePk) {
		W5Table t = FrameworkCache.getTable(scd, tableId);
		if (t == null || GenericUtil.isEmpty(t.get_tableChildList()))
			return null;
		List<W5TableChildHelper> r = new ArrayList<W5TableChildHelper>(t.get_tableChildList().size());
		for (W5TableChild tc : t.get_tableChildList()) {
			W5Table ct = FrameworkCache.getTable(scd, tc.getRelatedTableId());
			if (ct == null) {
				logger.error("ERROR(findRecordChildRecords) for relatedTableId=" + tc.getRelatedTableId());
				continue;
			}
			switch (ct.getAccessViewTip()) {
			case 0:
				if (!FrameworkCache.roleAccessControl(scd, 0)) {
					continue;
				}
				break;
			case 1:
				if (ct.getAccessViewUserFields() == null && !GenericUtil.accessControl(scd, ct.getAccessViewTip(),
						ct.getAccessViewRoles(), ct.getAccessViewUsers())) {
					continue;
				}
			}
			StringBuilder sql = new StringBuilder();
			sql.append("select count(1) xcount");
			if (ct.getMakeCommentFlag() != 0) {
				sql.append(",sum((select count(1) from iwb.w5_comment c where c.project_uuid='")
						.append(scd.get("projectId")).append("' AND c.table_id=").append(ct.getTableId())
						.append(" AND c.table_pk=x.").append(ct.get_tableParamList().get(0).getExpressionDsc())
						.append(")) xcomment_count");
			}
			if (ct.getFileAttachmentFlag() != 0) {
				sql.append(",sum((select count(1) from iwb.w5_file_attachment c where c.customization_id=")
						.append(scd.get("customizationId")).append(" AND c.table_id=").append(ct.getTableId())
						.append(" AND c.table_pk=x.").append(ct.get_tableParamList().get(0).getExpressionDsc())
						.append("::text)) xfile_count");
			}
			sql.append(" from ").append(ct.getDsc()).append(" x where x.")
					.append(ct.get_tableFieldMap().get(tc.getRelatedTableFieldId()).getDsc()).append("=")
					.append(tablePk);
			if (tc.getRelatedStaticTableFieldId() != 0) {
				sql.append(" AND x.").append(ct.get_tableFieldMap().get(tc.getRelatedStaticTableFieldId()).getDsc())
						.append("=").append(tc.getRelatedStaticTableFieldVal());
			}
			sql.append(DBUtil.includeTenantProjectPostSQL(scd, ct));
			if (FrameworkSetting.tableChildrenMaxRecordNumber > 0)
				sql.append(" limit ").append(FrameworkSetting.tableChildrenMaxRecordNumber);
			/*
			 * //TODO. burda bir de iwb.w5_access_control ve approval yapilacak
			 * if(ct.getAccessTips()!=null &&
			 * PromisUtil.hasPartInside2(ct.getAccessTips(), "0")){ sql.append(
			 * " left outer join iwb.w5_access_control ac on ac.ACCESS_TIP=0 AND ac.table_id="
			 * ).append(t.getTableId()).append(
			 * " AND ac.customization_id=${scd.customizationId} AND ac.table_pk=x."
			 * ).append(t.get_tableParamList().get(0).getExpressionDsc()); }
			 */
			List<Map> l = executeSQLQuery2Map(sql.toString(), null);
			if (!GenericUtil.isEmpty(l)) {
				Map m = l.get(0);
				r.add(new W5TableChildHelper(tc, GenericUtil.uInt(m, "xcount"), GenericUtil.uInt(m, "xcomment_count"),
						GenericUtil.uInt(m, "xfile_count")));
			}
		}
		return r;
	}

	public boolean accessControlTable(Map<String, Object> scd, W5Table t, Integer tablePk) {
		if (!GenericUtil.accessControlTable(scd, t))
			return false;
		if (tablePk != null && (!GenericUtil.isEmpty(t.get_approvalMap()) || (t.getAccessViewTip() != 0
				&& ((t.getAccessTips() != null && GenericUtil.hasPartInside2(t.getAccessTips(), "0")
						|| !GenericUtil.isEmpty(t.getAccessViewUserFields())))))) { // TODO
																					// :
																					// ekstra
																					// record
																					// bazli
																					// kontrol
		}
		return true;
	}

	public W5TableRecordInfoResult getTableRecordInfo(Map<String, Object> scd, int tableId, int tablePk) {
		W5TableRecordInfoResult result = new W5TableRecordInfoResult(scd, tableId, tablePk);
		;
		W5Table t = FrameworkCache.getTable(scd, tableId);
		if (t == null || !accessControlTable(scd, t, tablePk))
			return null;
		Map<String, W5TableField> fieldMap1 = new HashMap();
		for (W5TableField tf : (List<W5TableField>) t.get_tableFieldList()) {
			fieldMap1.put(tf.getDsc(), tf);
		}

		if ((fieldMap1.get("INSERT_USER_ID") != null || fieldMap1.get("insert_user_id") != null)
				&& (fieldMap1.get("VERSION_USER_ID") != null || fieldMap1.get("version_user_id") != null)) {
			StringBuilder sql = new StringBuilder();
			List params = new ArrayList();
			sql.append("select x.version_no, x.insert_user_id, x.insert_dttm, x.version_user_id, x.version_dttm from ")
					.append(t.getDsc()).append(" x where x.").append(t.get_tableParamList().get(0).getExpressionDsc())
					.append("=?");
			params.add(tablePk);
			sql.append(DBUtil.includeTenantProjectPostSQL(scd, t));

			List<Map> l = executeSQLQuery2Map(sql.toString(), params);
			if (!GenericUtil.isEmpty(l)) {
				Map m = l.get(0);
				result.setVersionNo(GenericUtil.uInt(m, "version_no"));
				result.setInsertUserId(GenericUtil.uInt(m, "insert_user_id"));
				result.setInsertDttm((String) m.get("insert_dttm"));
				result.setVersionUserId(GenericUtil.uInt(m, "version_user_id"));
				result.setVersionDttm((String) m.get("version_dttm"));
			}
		}
		int extraSqlCount = 0;
		StringBuilder extraSql = new StringBuilder();

		/*
		 * if(FrameworkCache.getAppSettingIntValue(scd,
		 * "file_attachment_flag")!=0 && t.getFileAttachmentFlag()!=0){
		 * extraSql.append(
		 * "(select count(1) cnt from iwb.w5_file_attachment x where x.customization_id=? AND x.table_id=? AND x.table_pk=?::text) file_attach_count"
		 * ); extraSqlCount++; } else
		 */ result.setFileAttachmentCount(-1);
		if (FrameworkCache.getAppSettingIntValue(scd, "make_comment_flag") != 0 && t.getMakeCommentFlag() != 0) {
			if (extraSql.length() > 0)
				extraSql.append(",");
			extraSql.append(
					"(select count(1) cnt from iwb.w5_comment x where x.project_uuid=? AND x.table_id=? AND x.table_pk=?::integer) comment_count");
			extraSqlCount++;
		} else
			result.setCommentCount(-1);

		/*
		 * if(FrameworkCache.getAppSettingIntValue(scd,
		 * "row_based_security_flag")!=0 && (Integer)scd.get("userTip")!=3 &&
		 * t.getAccessTips()!=null && t.getAccessTips().length()>0){
		 * if(extraSql.length()>0)extraSql.append(","); extraSql.append(
		 * "(select count(1) cnt from iwb.w5_access_control x where x.customization_id=? AND x.table_id=? AND x.table_pk=?) access_count"
		 * ); extraSqlCount++; } else result.setAccessControlCount(-1);
		 */
		List<Object> params = new ArrayList(extraSqlCount * 3 + 5);
		for (int qi = 0; qi < extraSqlCount; qi++) {
			params.add(scd.get("projectId"));
			params.add(tableId);
			params.add(tablePk);
		}

		if (FrameworkCache.getAppSettingIntValue(scd, "form_conversion_flag") != 0) {
			if (extraSql.length() > 0)
				extraSql.append(",");
			extraSql.append(
					"(select count(1) cnt from iwb.w5_converted_object y, iwb.w5_conversion x where x.active_flag=1 AND x.project_uuid=? AND x.project_uuid=y.project_uuid AND x.conversion_id=y.conversion_id AND x.src_table_id=? AND y.src_table_pk=?) conversion_count");
			params.add(scd.get("projectId"));
			params.add(tableId);
			params.add(tablePk);
			extraSqlCount++;
		} else
			result.setConversionCount(-1);

		if (extraSql.length() > 0) {

			List<Map> l = executeSQLQuery2Map("select " + extraSql.append(" ").toString(), params); // from
																									// dual
			if (!GenericUtil.isEmpty(l)) {
				Map m = l.get(0);
				if (result.getFileAttachmentCount() != -1)
					result.setFileAttachmentCount(GenericUtil.uInt(m.get("file_attach_count")));
				if (result.getCommentCount() != -1)
					result.setCommentCount(GenericUtil.uInt(m.get("comment_count")));
				if (result.getAccessControlCount() != -1)
					result.setAccessControlCount(GenericUtil.uInt(m.get("access_count")));
				if (result.getFormMailSmsCount() != -1)
					result.setFormMailSmsCount(GenericUtil.uInt(m.get("mail_sms_count")));
				if (result.getConversionCount() != -1)
					result.setConversionCount(GenericUtil.uInt(m.get("conversion_count")));
			}
		}
		return result;
	}

	public String getSummaryText4Record(Map<String, Object> scd, int tableId, int tablePk) {
		W5Table t = FrameworkCache.getTable(scd, tableId);
		String summarySql = t.getSummaryRecordSql();
		String summaryText = null;
		if (!GenericUtil.isEmpty(summarySql)) {
			List params = new ArrayList();
			if (summarySql.indexOf("${") > -1) {
				Object[] oz = DBUtil.filterExt4SQL(summarySql, scd, new HashMap(), null);
				summarySql = oz[0].toString();
				if (oz.length > 1 && oz[1] != null)
					params = (List) oz[1];
			}
			String sql = "select " + summarySql + " dsc from " + t.getDsc() + " x where x."
					+ t.get_tableParamList().get(0).getExpressionDsc() + "=?";
			params.add(tablePk);
			sql += DBUtil.includeTenantProjectPostSQL(scd, t);
			Map<String, Object> m = runSQLQuery2Map(sql, params, null);
			if (m != null)
				summaryText = (String) m.get("dsc");
			if (summaryText == null) {
				summaryText = "ERROR: " + sql;
			}
		} else
			summaryText = "TODO: make Summary SQL on Table: "
					+ /* formResult.getForm().get_sourceTable() */ t.getDsc();
		return summaryText;
	}

	public boolean checkIfRecordsExists(Map scd, Map<String, String> requestParams, W5Table t) {
		StringBuilder sql = new StringBuilder();
		sql.append("select 1 from ").append(t.getDsc()).append(" t where ");

		List<Object> params = new ArrayList();
		boolean b = false;
		Map m = new HashMap();
		for (W5TableParam x : t.get_tableParamList()) {
			if (b) {
				sql.append(" AND ");
			} else
				b = true;
			sql.append("t.").append(x.getExpressionDsc()).append(" = ? ");
			Object psonuc = GenericUtil.prepareParam((W5Param) x, scd, requestParams, (short) -1, null, (short) 1, null,
					null, m);
			params.add(psonuc);
		}

		return runSQLQuery2Map(sql.toString(), params, null) != null;
	}

	public void removeTableChildRecords(Map<String, Object> scd, int tableId, int tablePk, String dstDetailTableIds) {
		if (tableId == 0 || GenericUtil.isEmpty(dstDetailTableIds))
			return;
		W5Table t = FrameworkCache.getTable(scd, tableId);
		String[] dtsl = dstDetailTableIds.split(",");
		for (String dts : dtsl) {
			int detailTableId = GenericUtil.uInt(dts);
			for (W5TableChild tc : t.get_tableChildList())
				if (tc.getRelatedTableId() == detailTableId) {
					W5Table dt = FrameworkCache.getTable(scd, detailTableId);
					List<Object> params = new ArrayList();
					StringBuilder sql = new StringBuilder();
					sql.append("delete from ").append(dt.getDsc()).append(" x where x.customization_id=?");
					params.add(scd.get("customizationId"));
					sql.append(" AND x.").append(dt.get_tableFieldMap().get(tc.getRelatedTableFieldId()).getDsc())
							.append("=?");
					params.add(tablePk);
					if (tc.getRelatedStaticTableFieldId() != 0) {
						sql.append(" AND x.")
								.append(dt.get_tableFieldMap().get(tc.getRelatedStaticTableFieldId()).getDsc())
								.append("=?");
						params.add(tc.getRelatedStaticTableFieldVal());
					}
					executeUpdateSQLQuery(sql.toString(), params);
				}
		}
	}

	public W5FileAttachment getFileAttachment(int fileAttachmentId) {
		return (W5FileAttachment) find("from W5FileAttachment t where t.fileAttachmentId=?", fileAttachmentId).get(0);
	}

	public String getObjectVcsHash(Map<String, Object> scd, int tableId, int tablePk) {
		W5Table t = FrameworkCache.getTable(scd, tableId);
		StringBuilder s = new StringBuilder();
		s.append("select iwb.md5hash(").append(getTableFields4VCS(t, "x")).append(") xhash from ").append(t.getDsc())
				.append(" x where x.").append(t.get_tableParamList().get(0).getExpressionDsc()).append("=?");
		List p = new ArrayList();
		p.add(tablePk);
		s.append(DBUtil.includeTenantProjectPostSQL(scd, t));
		List l = executeSQLQuery2Map(s.toString(), p);
		if (GenericUtil.isEmpty(l))
			return "!";
		else
			return (String) ((Map) l.get(0)).get("xhash");
	}

	public Map getTableRecordJson(Map<String, Object> scd, int tableId, int tablePk, int recursiveLevel) {
		W5Table t = FrameworkCache.getTable(scd, tableId);
		StringBuilder s = new StringBuilder();
		s.append("select x.* from ").append(t.getDsc()).append(" x where x.")
				.append(t.get_tableParamList().get(0).getExpressionDsc()).append("=?");
		s.append(DBUtil.includeTenantProjectPostSQL(scd, t));
		List p = new ArrayList();
		p.add(tablePk);
		List l = executeSQLQuery2Map(s.toString(), p);
		return GenericUtil.isEmpty(l) ? null : (Map) l.get(0);
	}

	public boolean saveVcsObject(Map<String, Object> scd, int tableId, int tablePk, int action, JSONObject o) { // TODO
		// dao.updatePlainTableRecord(t, o, vo.getTablePk(), srvCommitUserId);
		try {
			W5Table t = FrameworkCache.getTable(scd, tableId);
			StringBuilder s = new StringBuilder();
			List p = new ArrayList();
			switch (action) {
			case 1: // update
				s.append("update ").append(t.getDsc()).append(" x set ");
				for (W5TableField f : t.get_tableFieldList())
					if (f.getTabOrder() > 1) {
						if (f.getDsc().equals("insert_user_id") || f.getDsc().equals("insert_dttm")
								|| f.getDsc().equals("customization_id") || f.getDsc().equals("project_uuid"))
							continue;
						if (f.getDsc().equals("version_dttm")) {
							s.append(f.getDsc()).append("=iwb.fnc_sysdate(0),");
							continue;
						}
						s.append(f.getDsc()).append("=?,");
						try {
							if (o.has(f.getDsc())) {
								p.add(GenericUtil.getObjectByControl((String) o.get(f.getDsc()), f.getParamTip()));
							} else
								p.add(null);
						} catch (JSONException e) {
							throw new IWBException("vcs", "JSONException : saveVcsObject", t.getTableId(), f.getDsc(),
									e.getMessage(), e);
						}
					}
				s.setLength(s.length() - 1);
				s.append(" where ").append(t.get_tableParamList().get(0).getExpressionDsc()).append("=?");
				p.add(tablePk);
				s.append(DBUtil.includeTenantProjectPostSQL(scd, t));
				break;
			case 2: // insert
				s.append("insert into ").append(t.getDsc()).append("(");
				StringBuilder s2 = new StringBuilder();
				for (W5TableField f : t.get_tableFieldList())
					if (f.getTabOrder() > 0) {
						if (GenericUtil.hasPartInside2("insert_dttm,version_dttm", f.getDsc())) {
							s.append(f.getDsc()).append(",");
							s2.append("current_timestamp,");
						} else {
							s.append(f.getDsc()).append(",");
							s2.append("?,");
							if (f.getDsc().equals("project_uuid"))
								p.add(scd.get("projectId"));
							else
								try {
									if (o.has(f.getDsc())) {
										p.add(GenericUtil.getObjectByControl((String) o.get(f.getDsc()),
												f.getParamTip()));
									} else
										p.add(null);
								} catch (JSONException e) {
									throw new IWBException("vcs", "JSONException : saveVcsObject", t.getTableId(),
											f.getDsc(), e.getMessage(), e);
								}
						}
					}
				s.setLength(s.length() - 1);
				s2.setLength(s2.length() - 1);
				s.append(") values (").append(s2).append(")");

				break;
			case 3: // delete
				s.append("delete from ").append(t.getDsc()).append(" x where ")
						.append(t.get_tableParamList().get(0).getExpressionDsc()).append("=?");
				p.add(tablePk);
				s.append(DBUtil.includeTenantProjectPostSQL(scd, t));
				break;
			}
			executeUpdateSQLQuery(s.toString(), p);
		} catch (Exception e) {
			throw new IWBException("framework", "Save.VCSObject", tablePk, null, "[" + tableId + "," + tablePk + "] ",
					e);
		}

		return true;
	}

	public String getTableRecordSummary(Map scd, int tableId, int tablePk, int maxLength) {
		W5Table t = FrameworkCache.getTable(scd, tableId);
		if (t == null)
			return "Table not Found ;)";
		if (GenericUtil.isEmpty(t.get_tableParamList()))
			return "TableParam not Found ;)";
		StringBuilder sql = new StringBuilder();
		sql.append("select (").append(t.getSummaryRecordSql()).append(") qqq from ").append(t.getDsc())
				.append(" x where x.").append(t.get_tableParamList().get(0).getExpressionDsc()).append("=?");
		sql.append(DBUtil.includeTenantProjectPostSQL(scd, t));
		Object[] res = DBUtil.filterExt4SQL(sql.toString(), scd, new HashMap(), new HashMap());
		List summaryParams = (List) res[1];
		summaryParams.add(tablePk);
		List l = executeSQLQuery2(((StringBuilder) res[0]).toString(), summaryParams);
		if (GenericUtil.isEmpty(l))
			return "(record not found)(" + tablePk + ")";
		String s = (String) l.get(0);
		if (s == null)
			return "(record not found)(" + tablePk + ")";
		return maxLength == 0 ? s : (s.length() > maxLength ? s.substring(0, maxLength) : s);
	}

	public boolean organizeTable(Map<String, Object> scd, String fullTableName) {
		if (FrameworkSetting.vcs && FrameworkSetting.vcsServer)
			return false;
		int customizationId = (Integer) scd.get("customizationId");
		int userId = (Integer) scd.get("userId");
		String projectUuid = (String) scd.get("projectId");
		W5Project prj = FrameworkCache.getProject(projectUuid);
		String schema = prj.getRdbmsSchema();
		executeUpdateSQLQuery("set search_path=" + schema);

		fullTableName = fullTableName.toLowerCase(FrameworkSetting.appLocale);
		String tableName = fullTableName;
		if (tableName.contains(".")) {
			schema = tableName.substring(0, tableName.indexOf('.'));
			tableName = tableName.substring(schema.length() + 1);
		}
		boolean vcs = FrameworkSetting.vcs;

		int cnt = GenericUtil.uInt(executeSQLQuery(
				"select count(1) from information_schema.tables qx where qx.table_name = ? and qx.table_schema = ?",
				tableName, schema).get(0));
		if (cnt == 0)
			throw new IWBException("framework", "No Such Table to Define", 0, tableName, "No Such Table to Define",
					null);

		int tableId = 0;
		List l = executeSQLQuery(
				"select qx.table_id from iwb.w5_table qx where qx.dsc = ? and qx.customization_id = ? AND qx.project_uuid=?",
				fullTableName, customizationId, projectUuid);
		if (!GenericUtil.isEmpty(l)) {
			tableId = GenericUtil.uInt(l.get(0));
		}
		if (tableId == 0) {
			tableId = GenericUtil.getGlobalNextval("iwb.seq_table", projectUuid, userId, customizationId);
			int rq = executeUpdateSQLQuery(
					"insert into iwb.w5_table"
							+ "(table_id, dsc, insert_user_id, version_user_id, customization_id, project_uuid, oproject_uuid)values"
							+ "(?       , ?  , ?             , ?              , ?               , ?           , ?)",
					tableId, tableName, userId, userId, customizationId, projectUuid, projectUuid);
			if (vcs)
				saveObject(new W5VcsObject(scd, 15, tableId));

			String firstField = (String) executeSQLQuery(
					"SELECT lower(qz.COLUMN_NAME) from information_schema.columns qz where qz.table_name = ? and qz.table_schema = ? and qz.ordinal_position=1",
					tableName, schema).get(0);

			int tableParamId = GenericUtil.getGlobalNextval("iwb.seq_table_param", projectUuid, userId,
					customizationId);
			rq = executeUpdateSQLQuery(
					"insert into iwb.w5_table_param "
							+ "(table_param_id, table_id, dsc, expression_dsc, tab_order, param_tip, operator_tip, not_null_flag, source_tip, insert_user_id, version_user_id, project_uuid, customization_id, oproject_uuid)values"
							+ "(?             , ?       , ?  , ?             , ?        , ?        , ?           , ?            , ?         , ?             , ?              , ?           , ?               , ?)",
					tableParamId, tableId, "t" + firstField, firstField, 1, 4, 0, 1, 1, userId, userId, projectUuid,
					customizationId, projectUuid);
			if (vcs)
				saveObject(new W5VcsObject(scd, 42, tableParamId));

			cnt = GenericUtil.uInt(executeSQLQuery(
					"SELECT count(1) from information_schema.columns qz where qz.table_name = ? and qz.table_schema = ? and lower(qz.COLUMN_NAME)='customization_id'",
					tableName, schema).get(0));

			if (cnt > 0) {
				tableParamId = GenericUtil.getGlobalNextval("iwb.seq_table_param", projectUuid, userId,
						customizationId);
				rq = executeUpdateSQLQuery(
						"insert into iwb.w5_table_param "
								+ "(table_param_id, table_id, dsc, expression_dsc, tab_order, param_tip, operator_tip, not_null_flag, source_tip, insert_user_id, version_user_id, project_uuid, customization_id, oproject_uuid)values"
								+ "(?             , ?       , ?  , ?             , ?        , ?        , ?           , ?            , ?         , ?             , ?              , ?           , ?               , ?)",
						tableParamId, tableId, "customizationId", "customization_id", 2, 4, 0, 1, 2, userId, userId,
						projectUuid, customizationId, projectUuid);
				if (vcs)
					saveObject(new W5VcsObject(scd, 42, tableParamId));
			}
		}

		List p = new ArrayList();
		p.add(tableId);
		p.add(customizationId);
		p.add(projectUuid);
		p.add(tableName);
		p.add(schema);
		l = executeSQLQuery2Map("select x.*"
				+ ", coalesce((select tf.table_field_id from iwb.w5_table_field tf where tf.table_id = ? and lower(tf.dsc) = x.column_name and tf.customization_id= ? AND tf.project_uuid=?),0) as table_field_id"
				+ ", coalesce((SELECT w.system_type_id FROM iwb.sys_postgre_types w where w.dsc = x.DATA_TYPE),0) xlen"
				+ ", coalesce((SELECT w.framework_type from iwb.sys_postgre_types w where w.dsc = x.DATA_TYPE),0) xtyp"
				+ " from information_schema.columns x where x.table_name = ? and x.table_schema = ?", p);
		for (Map m : (List<Map>) l) {
			int tfId = GenericUtil.uInt(m.get("table_field_id"));
			int xlen = GenericUtil.uInt(m.get("xlen"));
			int xtyp = GenericUtil.uInt(m.get("xtyp"));
			int tabOrder = GenericUtil.uInt(m.get("ordinal_position"));
			if (tfId == 0) {
				String fieldName = ((String) m.get("column_name")).toLowerCase(FrameworkSetting.appLocale);
				int tableFieldId = GenericUtil.getGlobalNextval("iwb.seq_table_field", projectUuid, userId,
						customizationId);
				boolean sessField = fieldName.equals("customization_id") || fieldName.equals("project_uuid")
						|| fieldName.equals("oproject_uuid");
				int rq = executeUpdateSQLQuery(
						"insert into iwb.w5_table_field "
								+ "(table_field_id, table_id, dsc, field_tip, not_null_flag, max_length, tab_order, insert_user_id, version_user_id, customization_id, project_uuid, source_tip, default_value, can_update_flag, can_insert_flag, copy_source_tip, default_control_tip, default_lookup_table_id, oproject_uuid) values"
								+ "(?             , ?       , ?  , ?        , ?            , ?         , ?        , ?             , ?              , ?               , ?           , ?         , ?            , ?              , ?              , ?              , ?                  , ?                      , ?)",
						tableFieldId, tableId, fieldName,
						fieldName.endsWith("_flag") ? 5
								: (xtyp == 3 && GenericUtil.uInt(m.get("numeric_scale")) == 0 ? 4 : xtyp),
						((String) m.get("is_nullable")).equals("YES") ? 0 : 1,
						xlen == 0 ? 0 : (xlen == -1 ? GenericUtil.uInt(m.get("character_maximum_length")) : xlen),
						tabOrder, userId, userId, customizationId, projectUuid, sessField ? 2 : (tabOrder == 1 ? 4 : 1),
						fieldName.endsWith("_flag") ? "0"
								: (fieldName.equals("customization_id") ? "customizationId"
										: (tabOrder == 1 ? "nextval('seq_" + tableName + "')" : null)),
						GenericUtil.hasPartInside2(
								"customization_id,version_no,insert_user_id,insert_dttm,version_user_id,version_dttm",
								fieldName) || tabOrder == 1 ? 0 : 1,
						GenericUtil.hasPartInside2("version_no,insert_user_id,insert_dttm,version_user_id,version_dttm",
								fieldName) ? 0 : 1,
						sessField ? 2 : (tabOrder == 1 ? 4 : 6),
						GenericUtil.hasPartInside2("insert_user_id,version_user_id", fieldName) ? 10 : 0,
						GenericUtil.hasPartInside2("insert_user_id,version_user_id", fieldName) ? 336 : 0, projectUuid);
				if (vcs)
					saveObject(new W5VcsObject(scd, 16, tableFieldId));

			} else {
				int rq = executeUpdateSQLQuery(
						"update iwb.w5_table_field " + " set tab_order       = ?, " + " version_user_id = ?, "
								+ "version_dttm    = LOCALTIMESTAMP, " + " version_no      = version_no+1, "
								+ " not_null_flag =  ?, " + " max_length =  ? "
								+ " where table_field_id =  ? AND  project_uuid=?",
						tabOrder, userId, ((String) m.get("is_nullable")).equals("YES") ? 0 : 1,
						xlen == 0 ? 0 : (xlen == -1 ? GenericUtil.uInt(m.get("character_maximum_length")) : xlen), tfId,
						projectUuid);
				if (vcs)
					makeDirtyVcsObject(scd, 16, tfId);
			}
		}

		int rq = executeUpdateSQLQuery(
				"update iwb.w5_table_field " + "set tab_order       = -abs(tab_order), " + "version_user_id = ?, "
						+ "version_dttm    = LOCALTIMESTAMP, " + "version_no      = version_no+1 "
						+ "where table_id = ?  AND tab_order > 0  AND project_uuid=? "
						+ " AND (lower(dsc) not in (SELECT lower(q.COLUMN_NAME) from information_schema.columns q where q.table_name = ? and q.table_schema = ?))",
				userId, tableId, projectUuid, tableName, schema);

		return true;
	}

	public void makeDirtyVcsObject(Map<String, Object> scd, int tableId, int tablePk) {
		if (FrameworkSetting.vcsServer)
			throw new IWBException("vcs", "makeDirtyVcsObject", tableId, null,
					"VCS Server not allowed to make Dirt VCS Object", null);
		List l = find("from W5VcsObject t where t.tableId=? AND t.tablePk=? AND t.projectUuid=?", tableId, tablePk,
				scd.get("projectId"));
		if (!l.isEmpty()) {
			W5VcsObject o = (W5VcsObject) l.get(0);
			if (o.getVcsObjectStatusTip() == 9) { // 1, 2, 3, 8 durumunda
													// hicbirsey degismiyor
				o.setVcsObjectStatusTip((short) 1);
				updateObject(o);
			}
		} else
			saveObject(new W5VcsObject(scd, tableId, tablePk));
	}

	public boolean organizeGlobalFunc(Map<String, Object> scd, String fullGlobalFuncName) {
		if (FrameworkSetting.vcsServer)
			throw new IWBException("vcs", "organizeGlobalFunc", 0, fullGlobalFuncName,
					"VCS Server not allowed to organizeGlobalFunc", null);

		int customizationId = (Integer) scd.get("customizationId");
		int userId = (Integer) scd.get("userId");
		String projectUuid = (String) scd.get("projectId");

		W5Project po = FrameworkCache.getProject(projectUuid);
		String schema = po.getRdbmsSchema();
		fullGlobalFuncName = fullGlobalFuncName.toLowerCase(FrameworkSetting.appLocale);
		String dbFuncName = fullGlobalFuncName;
		if (dbFuncName.contains(".")) {
			schema = dbFuncName.substring(0, dbFuncName.indexOf('.'));
			dbFuncName = dbFuncName.substring(schema.length() + 1);
		}
		boolean vcs = FrameworkSetting.vcs;

		List l = executeSQLQuery(
				"select qx.proname from pg_proc qx where qx.proname= ? and qx.pronamespace=(select q.oid from pg_namespace q where q.nspname=?)",
				dbFuncName, schema);
		if (GenericUtil.isEmpty(l))
			throw new IWBException("framework", "No Such GlobalFunc to Define", 0, dbFuncName,
					"No Such GlobalFunc to Define", null);
		String params = l.get(0).toString();

		int dbFuncId = 0;
		l = executeSQLQuery(
				"select qx.db_func_id from iwb.w5_db_func qx where qx.dsc = ? and qx.customization_id = ? AND qx.project_uuid=?",
				fullGlobalFuncName, customizationId, projectUuid);
		Map<String, Object> dbFuncParamMap = new HashMap();
		if (!GenericUtil.isEmpty(l)) {
			dbFuncId = GenericUtil.uInt(l.get(0));
			List<Object[]> oldParams = executeSQLQuery(
					"select qx.expression_dsc, qx.db_func_param_id from iwb.w5_db_func_param qx where qx.db_func_id = ? and qx.customization_id = ? order by qx.tab_order",
					dbFuncId, customizationId);
			int tabOrder = 1;
			if (!GenericUtil.isEmpty(oldParams))
				for (Object[] o : oldParams) {
					dbFuncParamMap.put((String) o[0], GenericUtil.uInt(o[1]));
				}
		}

		if (dbFuncId == 0) {
			dbFuncId = GenericUtil.getGlobalNextval("iwb.seq_db_func", projectUuid, userId, customizationId);
			int rq = executeUpdateSQLQuery(
					"insert into iwb.w5_db_func"
							+ "(db_func_id, dsc, insert_user_id, version_user_id, project_uuid, customization_id)values"
							+ "(?         , ?  , ?        , ?             , ?              , ?                , ?           , ?)",
					dbFuncId, schema + "." + dbFuncName, userId, userId, projectUuid, customizationId);
			if (vcs)
				saveObject(new W5VcsObject(scd, 20, dbFuncId));
		}
		params = params.toLowerCase(FrameworkSetting.appLocale).substring(1, params.length() - 1);
		String[] arp = params.split(",");
		for (int qi = 0; qi < arp.length; qi++) {
			int dbFuncParamId = GenericUtil.uInt(dbFuncParamMap.get(arp[qi]));
			if (dbFuncParamId == 0) { // boyle bir kayit yok
				dbFuncParamId = GenericUtil.getGlobalNextval("iwb.seq_db_func_param", projectUuid, userId,
						customizationId);
				executeUpdateSQLQuery(
						"insert into iwb.w5_db_func_param "
								+ "(db_func_param_id, db_func_id, dsc, expression_dsc, param_tip, tab_order, insert_user_id, version_user_id, source_tip, default_value, not_null_flag, out_flag, project_uuid, customization_id )  values "
								+ "( ?              , ?         , ?  , ?             , 1        , ?        , ?             , ?              , ?         , ?            , ?            , 0       , ?           , ? )",
						dbFuncParamId, dbFuncId,
						arp[qi].equals("puser_role_id") ? "userRoleId"
								: (arp[qi].equals("plocale") ? "locale"
										: (arp[qi].equals("ptrigger_action") ? "triggerAction"
												: (dbFuncName.startsWith("pcrud_") && qi < 3
														? "t" + arp[qi].substring(1) : arp[qi]))),
						arp[qi], qi + 1, userId, userId,
						arp[qi].equals("puser_role_id") || arp[qi].equals("locale") || arp[qi].equals("plocale") ? 2
								: 1,
						arp[qi].equals("puser_role_id") ? "userRoleId"
								: (arp[qi].equals("plocale") ? "locale"
										: (arp[qi].equals("ptrigger_action") ? "triggerAction" : null)),
						arp[qi].equals("puser_role_id") || arp[qi].equals("plocale")
								|| arp[qi].equals("ptrigger_action") || (dbFuncName.startsWith("pcrud_") && qi < 3) ? 1
										: 0,
						projectUuid, customizationId);
				if (vcs)
					saveObject(new W5VcsObject(scd, 21, dbFuncParamId));
			} else { // var boyle bir kayit
				executeUpdateSQLQuery(
						"update iwb.w5_db_func_param set expression_dsc=?, tab_order = ?, version_user_id = ?, version_dttm    = current_timestamp, version_no      = version_no  + 1 "
								+ "where db_func_param_id =  ? AND customization_id = ? ",
						arp[qi], qi + 1, userId, dbFuncParamId, customizationId);
				dbFuncParamMap.remove(arp[qi]);
				makeDirtyVcsObject(scd, 21, dbFuncParamId);
			}
		}
		if (!dbFuncParamMap.isEmpty())
			for (String k : dbFuncParamMap.keySet()) {
				int dbFuncParamId = GenericUtil.uInt(dbFuncParamMap.get(k));
				executeUpdateSQLQuery(
						"update iwb.w5_db_func_param set expression_dsc=?, tab_order = -abs(tab_order), version_user_id = ?, version_dttm    = current_timestamp, version_no      = version_no  + 1 "
								+ "where db_func_param_id =  ? AND customization_id = ? ",
						k, userId, dbFuncParamId, customizationId);
				makeDirtyVcsObject(scd, 21, dbFuncParamId);
			}
		return true;
	}

	public String getCurrentDate(int customizationId) {
		return (String) executeSQLQuery("select to_char(iwb.fnc_sysdate(?),'" + GenericUtil.dateFormat + "')",
				customizationId).get(0);
	}

	public Object getSqlFunc(String s) {
		return (Object) executeSQLQuery("select " + s).get(0);
	}

	public Map executeSQLQuery2Map4Debug(final Map<String, Object> scd, final W5Table t, final String sql,
			final List params, final int limit, final int startOffset) {
		try {
			return (Map) getCurrentSession().doReturningWork(new ReturningWork<Map>() {

				public Map execute(Connection conn) throws SQLException {
					Map m = new HashMap();
					PreparedStatement s = null;
					ResultSet rs = null;
					try {
						if (limit > 0) {
							s = conn.prepareStatement("select count(1) v from (" + sql + ") q");
							if (params != null && params.size() > 0)
								for (int i = 0; i < params.size(); i++) {
									if (params.get(i) == null)
										s.setObject(i + 1, null);
									else if (params.get(i) instanceof Date)
										s.setDate(i + 1, new java.sql.Date(((Date) params.get(i)).getTime()));
									else
										s.setObject(i + 1, params.get(i));
								}
							rs = s.executeQuery();
							rs.next();
							int cnt = GenericUtil.uInt(rs.getObject(1));
							rs.close();
							s.close();
							Map m2 = new HashMap();
							m2.put("startRow", startOffset);
							m2.put("fetchCount", limit);
							m2.put("totalCount", cnt);
							m.put("browseInfo", m2);
							s = startOffset > 1
									? conn.prepareStatement("select q.* from  (" + sql + ") q limit " + (limit + 1)
											+ ") offset " + startOffset)
									: conn.prepareStatement("select * from (" + sql + ") q limit " + (limit + 1));
						} else
							s = conn.prepareStatement(sql);

						if (params != null && params.size() > 0)
							for (int i = 0; i < params.size(); i++) {
								if (params.get(i) == null)
									s.setObject(i + 1, null);
								else if (params.get(i) instanceof Date)
									s.setDate(i + 1, new java.sql.Date(((Date) params.get(i)).getTime()));
								else
									s.setObject(i + 1, params.get(i));
							}

						long startTm = System.currentTimeMillis();
						rs = s.executeQuery();
						m.put("execTime", System.currentTimeMillis() - startTm);
						startTm = System.currentTimeMillis();
						// int columnCount = rs.getMetaData().getColumnCount();
						List l = new ArrayList();
						Map<String, Object> result = null;
						ResultSetMetaData rsm = rs.getMetaData();
						int columnCount = rsm.getColumnCount();
						String[] columnNames = new String[columnCount];
						List fields = new ArrayList();

						Map<String, Map> map4records = new HashMap<String, Map>();
						for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
							String columnName = rsm.getColumnName(columnIndex).toLowerCase(FrameworkSetting.appLocale);
							columnNames[columnIndex - 1] = columnName;
							Map m2 = new HashMap();
							m2.put("name", columnName);
							switch (rsm.getColumnType(columnIndex)) {
							case java.sql.Types.BIGINT:
							case java.sql.Types.INTEGER:
							case java.sql.Types.SMALLINT:
								m2.put("type", "int");
								break;
							case java.sql.Types.NUMERIC:
								m2.put("type", rsm.getScale(columnIndex) == 0 ? "int" : "float");
								break;
							case java.sql.Types.DATE:
							case java.sql.Types.TIMESTAMP:
								m2.put("type", "date");
								break;
							}
							if (t != null)
								for (W5TableField f : t.get_tableFieldList())
									if (f.getDsc().equals(columnName)) {
										if (f.getDefaultLookupTableId() != 0)
											switch (f.getDefaultControlTip()) {
											case 6:
											case 8:
												W5LookUp lu = FrameworkCache.getLookUp(scd,
														f.getDefaultLookupTableId());
												if (lu != null && !GenericUtil.isEmpty(lu.get_detayList())) {
													Map m3 = new HashMap();
													for (W5LookUpDetay d : lu.get_detayList()) {
														m3.put(d.getVal(), LocaleMsgCache.get2(scd, d.getDsc()));
													}
													m2.put(f.getDefaultControlTip() == 6 ? "map" : "map2", m3);
												}
												break;
											case 7:
											case 10:
											case 15:
												W5Table tt = FrameworkCache.getTable(scd, f.getDefaultLookupTableId());
												if (tt != null && !GenericUtil.isEmpty(tt.getSummaryRecordSql())) {
													Map mz = new HashMap();
													mz.put("set", new HashSet());
													mz.put("t", tt);
													mz.put("field", m2);
													map4records.put(columnName, mz);
												}
											}

										break;
									}
							fields.add(m2);
						}
						m.put("fields", fields);

						while (rs.next()) {
							result = new HashMap<String, Object>();
							for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
								String columnName = columnNames[columnIndex - 1];
								Object obj = rs.getObject(columnIndex);
								if (obj == null)
									continue;
								if (obj instanceof java.sql.Timestamp) {
									try {
										result.put(columnName,
												GenericUtil.uFormatDateTimeSade((java.sql.Timestamp) obj));
									} catch (Exception e) {
									}
								} else if (obj instanceof java.sql.Date) {
									try {
										result.put(columnName, GenericUtil.uFormatDateSade((java.sql.Date) obj));
									} catch (Exception e) {
									}
								} else {
									String ss = obj.toString();
									if (map4records.containsKey(columnName)) {
										((Set) (map4records.get(columnName).get("set"))).add(ss);
									}
									result.put(columnName, ss);
								}
							}
							l.add(result);
						}
						m.put("data", l);
						m.put("sql", GenericUtil.replaceSql(sql, params));
						m.put("fetchTime", System.currentTimeMillis() - startTm);

						for (String kk : map4records.keySet())
							try {
								Map mm = map4records.get(kk);
								Set set = (Set) mm.get("set");
								if (!set.isEmpty()) {
									String ss = "";
									for (Object sk : set.toArray()) {
										ss += "," + sk;
									}
									ss = ss.substring(1);
									W5Table t = (W5Table) mm.get("t");
									String sql = "select x." + t.get_tableFieldList().get(0).getDsc() + " id, "
											+ t.getSummaryRecordSql() + " dsc from " + t.getDsc() + " x where "
											+ t.get_tableParamList().get(0).getExpressionDsc() + " in (" + ss + ")";
									sql += DBUtil.includeTenantProjectPostSQL(scd, t);
									Object[] oz = DBUtil.filterExt4SQL(sql, scd, new HashMap(), null);
									List<Object[]> lm = executeSQLQuery2(oz[0].toString(), (List) oz[1]);
									if (lm != null) {
										Map m3 = new HashMap();
										for (Object[] oo : lm) {
											m3.put(oo[0], oo[1]);
										}
										((Map) (mm.get("field"))).put("map", m3);
									}
								}

							} catch (Exception ee) {
							}
					} catch (SQLException se) {
						throw se;
					} finally {
						if (rs != null)
							rs.close();
						if (s != null)
							s.close();
						if (FrameworkSetting.hibernateCloseAfterWork)
							if (conn != null)
								conn.close();
					}
					return m;
				}
			});
		} catch (Exception e) {
			throw new IWBException("sql", "Debug.Query", -1, GenericUtil.replaceSql(sql, params), "Error Executing", e);
		}
	}



	public Map executeQuery4Stat(Map<String, Object> scd, int gridId, Map<String, String> requestParams) {

		int customizationId = (Integer) scd.get("customizationId");
		int queryId = gridId > 0 ? metaDataDao.getGridResult(scd, gridId, requestParams, false ).getGrid().getQueryId(): -gridId;
		W5QueryResult queryResult = metaDataDao.getQueryResult(scd, queryId);
		queryResult.setErrorMap(new HashMap());
		queryResult.setRequestParams(requestParams);
		if (queryResult.getQuery().getQuerySourceTip() == 1376)
			return scriptEngine.executeQuery4StatWS(queryResult);

		if (queryId != 1 && queryId != 824 && queryResult.getMainTable() != null && (!FrameworkSetting.debug
				|| (scd.get("roleId") != null && GenericUtil.uInt(scd.get("roleId")) != 0))) {
			W5Table t = queryResult.getMainTable();

			if (t.getAccessViewTip() == 0 && !FrameworkCache.roleAccessControl(scd, 0)) {
				throw new IWBException("security", "Module", 0, null,
						LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_modul_kontrol"), null);
			}
			if (t.getAccessViewUserFields() == null && !GenericUtil.accessControl(scd, t.getAccessViewTip(),
					t.getAccessViewRoles(), t.getAccessViewUsers())) {
				throw new IWBException("security", "Query", queryId, null,
						LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_tablo_kontrol_goruntuleme"),
						null);
			}
		}
		/*
		 * StringBuilder tmpx = new StringBuilder(
		 * "ali baba ${obj.dsc} ve 40 haramiler ${lnk.pk_query_field_id.dsc} olmus"
		 * ); interprateTemplate(scd, 5,1294, tmpx, true);
		 */

		int statType = GenericUtil.uInt(requestParams, "_stat"); // 0:count,
																	// 1:sum,
																	// 2.avg
		String funcFields = requestParams.get("_ffids"); // statFunctionFields
		if (statType > 0 && GenericUtil.isEmpty(funcFields))
			throw new IWBException("framework", "Query", queryId, null,
					LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_grid_stat_func_fields"), null);

		int queryFieldId = GenericUtil.uInt(requestParams, "_qfid");
		int stackFieldId = GenericUtil.uInt(requestParams, "_sfid");
		if (stackFieldId > 0 && stackFieldId == queryFieldId)
			stackFieldId = 0;

		W5Query query = queryResult.getQuery();
		W5QueryField qf = null, sf = null;
		for (W5QueryField o : query.get_queryFields())
			if (o.getQueryFieldId() == queryFieldId) {
				qf = o;
				break;
			}
		if (qf == null)
			throw new IWBException("framework", "Query", queryId, null,
					LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_grid_stat_error"), null);
		if (stackFieldId > 0) {
			for (W5QueryField o : query.get_queryFields())
				if (o.getQueryFieldId() == stackFieldId) {
					sf = o;
					break;
				}
			if (sf == null)
				throw new IWBException("framework", "Query", queryId, null,
						LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_grid_stat_error2"), null);
		}

		int sortTip = GenericUtil.uInt(requestParams, "_sort");
		String orderBy = null;
		if (sortTip > 0 && sortTip < 3) {
			orderBy = new String[] { "xres", "id" }[sortTip - 1];
			if (GenericUtil.uInt(requestParams, "_dir") > 0)
				orderBy += " desc";
		}

		//
		// queryResult.setOrderBy(PromisUtil.uStrNvl(requestParams.get(PromisUtil.uStrNvl(PromisSetting.appSettings.get("sql_sort"),"sort")),
		// queryResult.getQuery().getSqlOrderby()));
		// queryResult.setOrderBy(queryResult.getQuery().getSqlOrderby());
		String queryFieldSQL = qf.getDsc();
		if (qf.getFieldTip() == 2) { // date ise
			queryFieldSQL = "to_char(" + queryFieldSQL + ", '"
					+ (new String[] { "yyyy", "yyyy/Q", "yyyy/mm", "yyyy/WW", "yyyy/mm/dd" }[GenericUtil
							.uInt(requestParams, "_dtt")])
					+ "')";
		}

		switch (queryResult.getQuery().getQueryTip()) {
		case 9:
		case 10:
		case 15: // TODO: aslinda hata. olmamasi lazim
			throw new IWBException("framework", "Query", queryId, null,
					LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_grid_stat_query_error"), null);
		default:
			queryResult.prepareQuery(null);
			if (!queryResult.getErrorMap().isEmpty())
				throw new IWBException("framework", "Query", queryId, null,
						LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_grid_stat_query_prepare_error"), null);

			String sql = "select " + queryFieldSQL + " id, ";
			if (sf != null)
				sql += sf.getDsc() + " stack_id, ";
			if (statType != 0) {
				String[] fq = funcFields.split(",");
				Set<Integer> fqs = new HashSet();
				for (String s : fq) {
					fqs.add(GenericUtil.uInt(s));
				}

				int count = 0;
				String[] stats = new String[] { "", "sum", "avg", "max", "min" };
				for (W5QueryField o : queryResult.getQuery().get_queryFields())
					if (fqs.contains(o.getQueryFieldId())) {
						count++;
						if (count > 1)
							sql += "," + stats[statType] + "(" + o.getDsc() + ") xres" + count;
						else
							sql += stats[statType] + "(" + o.getDsc() + ") xres";
					}
				if (count == 0)
					throw new IWBException("framework", "Query", queryId, null,
							LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_grid_stat_match_func_fields_error"),
							null);
			} else {
				sql += "count(1) xres";
			}

			sql += " from (" + queryResult.getExecutedSql() + ") mq group by id";
			if (sf != null)
				sql += ", stack_id";
			if (queryFieldSQL.startsWith("to_char("))
				sql += " order by id";
			else if (!GenericUtil.isEmpty(orderBy))
				sql += " order by " + orderBy;
			queryResult.setExecutedSql(sql);
		}
		Map result = new HashMap();
		result.put("success", true);
		List<Map> l = executeSQLQuery2Map(queryResult.getExecutedSql(), queryResult.getSqlParams());
		if (l != null) {
			if (sf != null) {
				Set<String> stackSet = new HashSet();
				List<Map> nl = new ArrayList();
				Map<Object, Map> nom = new HashMap();
				for (Map m : l) {
					Object oid = m.get("id");
					Map nm = nom.get(oid);
					if (nm == null) {
						nm = new HashMap();
						nm.put("id", oid);
						nom.put(oid, nm);
						nl.add(nm);
					}
					for (Object k : m.keySet())
						if (!k.equals("id") && !k.equals("stack_id")) {
							if (!GenericUtil.isEmpty(m.get("stack_id"))) {
								nm.put(k + "_" + m.get("stack_id"), m.get(k));
								stackSet.add(m.get("stack_id").toString());
							}
						}
				}
				l = nl;
				Map lm = new HashMap();
				switch (sf.getPostProcessTip()) {
				case 10: // static;
					W5LookUp ld3 = FrameworkCache.getLookUp(customizationId, sf.getLookupQueryId());
					if (ld3 != null)
						for (Object k : stackSet)
							if (ld3.get_detayMap().get(k) != null)
								lm.put(k, GenericUtil
										.uStrMax(LocaleMsgCache.get2(scd, ld3.get_detayMap().get(k).getDsc()), 20));
							else
								throw new IWBException("framework", "QueryField", sf.getQueryFieldId(), null,
										LocaleMsgCache.get2(0, (String) scd.get("locale"),
												"fw_grid_stat_stacked_error"),
										null);
					break;
				case 12: // table
					for (Object k : stackSet) {
						String s = getTableRecordSummary(scd, sf.getLookupQueryId(), GenericUtil.uInt(k), 20);
						lm.put(k, GenericUtil.isEmpty(s) ? "Not found for: " + k : s);
					}
					break;
				case 20:
				case 53: // user
					for (Object k : stackSet)
						lm.put(k, GenericUtil.uStrMax(UserUtil.getUserDsc(GenericUtil.uInt(k)), 20));
				}
				result.put("lookUp", lm);
			} else {
				int maxLegend = GenericUtil.uInt(requestParams, "_max", 10);
				if (l.size() > maxLegend) { // TODO: temizlik
					BigDecimal total = new BigDecimal(0);
					String ids = "";
					for (int qi = l.size() - 1; qi >= maxLegend - 1; qi--) {
						Map m = l.get(qi);
						ids += "," + m.get("id");
						total = total.add(new BigDecimal(m.get("xres").toString()));
						l.remove(qi);
					}
					Map nm = new HashMap();
					nm.put("id", -999999);
					nm.put("dsc", LocaleMsgCache.get2(scd, "others"));
					nm.put("xres", total);
					nm.put("ids", ids.substring(1));
					l.add(nm);
				}
			}
			for (W5QueryField o : queryResult.getQuery().get_queryFields())
				if (o.getQueryFieldId() == queryFieldId) {
					switch (o.getPostProcessTip()) {
					case 10: // lookup static
						W5LookUp ld2 = FrameworkCache.getLookUp(customizationId, o.getLookupQueryId());
						if (ld2 != null)
							for (Map m : l) {
								Object o2 = m.get("id");
								if (o2 != null && GenericUtil.uInt(o2) != -999999)
									try {
										m.put("dsc", GenericUtil.uStrMax(
												LocaleMsgCache.get2(scd, ld2.get_detayMap().get(o2).getDsc()), 20));
									} catch (Exception e) {
										m.put("dsc", "Not found for: " + o2);
									}
							}
						break;
					case 12: // lookup table
						for (Map m : l) {
							Object o2 = m.get("id");
							if (o2 != null && GenericUtil.uInt(o2) != -999999) {
								String s = getTableRecordSummary(scd, o.getLookupQueryId(), GenericUtil.uInt(o2), 20);
								m.put("dsc", GenericUtil.isEmpty(s) ? "Not found for: " + o2 : s);
							}
						}
						break;
					case 20:
					case 53: // user
						for (Map m : l) {
							Object o2 = m.get("id");
							if (o2 != null && GenericUtil.uInt(o2) != -999999) {
								m.put("dsc",
										GenericUtil.uStrNvl(UserUtil.getUserDsc(GenericUtil.uInt(o2)), "user-" + o2));
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

		String projectId = (String) scd.get("projectId");
		int queryId = metaDataDao.getGridResult(scd, gridId, requestParams, false ).getGrid().getQueryId();
		W5QueryResult queryResult = metaDataDao.getQueryResult(scd, queryId);
		W5Table t = queryResult.getMainTable();
		if (queryId != 1 && queryId != 824 && queryResult.getMainTable() != null && (!FrameworkSetting.debug
				|| (scd.get("roleId") != null && GenericUtil.uInt(scd.get("roleId")) != 0))) {
			if (t.getAccessViewTip() == 0 && !FrameworkCache.roleAccessControl(scd, 0)) {
				throw new IWBException("security", "Module", 0, null,
						LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_modul_kontrol"), null);
			}
			if (t.getAccessViewUserFields() == null && !GenericUtil.accessControl(scd, t.getAccessViewTip(),
					t.getAccessViewRoles(), t.getAccessViewUsers())) {
				throw new IWBException("security", "Query", queryId, null,
						LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_tablo_kontrol_goruntuleme"),
						null);
			}
		}
		/*
		 * StringBuilder tmpx = new StringBuilder(
		 * "ali baba ${obj.dsc} ve 40 haramiler ${lnk.pk_query_field_id.dsc} olmus"
		 * ); interprateTemplate(scd, 5,1294, tmpx, true);
		 */
		queryResult.setErrorMap(new HashMap());
		queryResult.setRequestParams(requestParams);

		int statType = GenericUtil.uInt(requestParams, "_stat"); // 0:count, n:
																	// sum(queryField)
		String tableFieldChain = requestParams.get("_qfid");
		String funcFields = statType == 0 ? null : requestParams.get("_ffids");
		if (tableFieldChain.indexOf('-') > 0)
			tableFieldChain = tableFieldChain.split("-")[1];
		String tableFieldSQL = "";
		List params = new ArrayList();
		W5TableField tableField = null;

		if (tableFieldChain.startsWith("tbl.") || (tableFieldChain.startsWith("lnk.")
				&& tableFieldChain.substring(4).replace(".", "&").split("&").length == 1)) { // direk
																								// tablodan
																								// veya
																								// link'in
																								// ilk
																								// kaydi
																								// ise
			String newSubStr = tableFieldChain.substring(4);
			boolean fieldFound = false;
			for (W5TableField tf : t.get_tableFieldList())
				if (tf.getDsc().equals(newSubStr)) {
					tableFieldSQL = "x." + newSubStr; // .put(tableFieldChain,
														// o.toString());
					tableField = tf;
					fieldFound = true;
					break;
				}
			if (!fieldFound)
				throw new IWBException("framework", "Query", queryId, null,
						LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_grid_stat_field_error"), null);
		} else if (tableFieldChain.startsWith("clc.")) { // direk tablodan veya
															// link'in ilk kaydi
															// ise
			String newSubStr = tableFieldChain.substring(4);
			boolean fieldFound = false;
			List<W5TableFieldCalculated> ltcf = find(
					"from W5TableFieldCalculated t where t.projectUuid=? AND t.tableId=? AND t.dsc=?", projectId,
					t.getTableId(), newSubStr);
			if (ltcf.isEmpty())
				throw new IWBException("framework", "Query", queryId, null,
						LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_grid_stat_field_error"), null);
			W5TableFieldCalculated tcf = ltcf.get(0);
			Object[] oo = DBUtil.filterExt4SQL(tcf.getSqlCode(), scd, requestParams, null);
			tableFieldSQL = "(" + oo[0].toString() + ")"; // .put(tableFieldChain,
															// o.toString());
			if (oo.length > 1)
				params.addAll((List) oo[1]);
			tableField = t.get_tableFieldList().get(0);
			fieldFound = true;
		} else if (tableFieldChain.startsWith("lnk.")
				&& tableFieldChain.substring(4).replace(".", "&").split("&").length > 1) { // burda
																							// bu
																							// field
																							// ile
																							// olan
																							// baglantiyi
																							// cozmek
																							// lazim
																							// TODO
			String newSubStr = tableFieldChain.substring(4);

			String[] sss = newSubStr.replace(".", "&").split("&");
			if (sss.length > 1) { // TODO kaldirilmasi lazim
				W5Table newT = t;
				StringBuilder newSub = new StringBuilder();
				StringBuilder newSub2 = new StringBuilder();
				boolean foundSt = false;
				for (int isss = 0; isss < sss.length - 1; isss++) {
					if (isss > 0) {
						newSub2.setLength(0);
						newSub2.append(newSub);
						newSub.setLength(0);
					}
					for (W5TableField tf : newT.get_tableFieldList())
						if (tf.getDsc().equals(sss[isss])) {
							foundSt = false;
							if (tf.getDefaultControlTip() == 7 || tf.getDefaultControlTip() == 9 || tf
									.getDefaultControlTip() == 10 /*
																	 * || tf.
																	 * getDefaultControlTip
																	 * ()==15
																	 */) { // sub
																		// table
								W5Table st = FrameworkCache.getTable(scd, tf.getDefaultLookupTableId());
								if (st == null)
									break; // HATA: gerekli bir alt kademe
											// tabloya ulasilamadi
								int dltId = 0;
								for (W5TableField stf : st.get_tableFieldList())
									if (stf.getDsc().equals(sss[isss + 1])) {
										dltId = stf.getDefaultLookupTableId();
										tableField = stf;
										foundSt = true;
										break;
									}
								if (!foundSt)
									break; // HATA: bir sonraki field bulunamadi
								newSub.append("(select ");
								newSub.append("y").append(isss).append(".").append(sss[isss + 1]);
								newSub.append(" from ").append(st.getDsc()).append(" y").append(isss).append(" where y")
										.append(isss).append(".").append(st.get_tableFieldList().get(0).getDsc())
										.append("=").append(isss == 0 ? ("x." + sss[isss]) : newSub2);
								if (st.get_tableFieldList().size() > 1)
									for (W5TableField wtf : st.get_tableFieldList())
										if (wtf.getDsc().equals("project_uuid")) {
											newSub.append(" AND y").append(isss).append(".project_uuid=?");
											params.add(scd.get("projectId"));
											break;
										}

								newSub.append(")");
								newT = st;
							}
							break;
						}
					if (!foundSt) { // bulamamis uygun sey
						break;
					}
				}
				if (foundSt && newSub.length() > 0) {
					tableFieldSQL = "(" + newSub.toString() + ")"; // .put(tableFieldChain,
																	// o.toString());
				} else
					throw new IWBException("framework", "Query", queryId, null,
							LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_grid_stat_field_error"), null);
			}
		}
		int stackFieldId = GenericUtil.uInt(requestParams, "_sfid");
		String stackField = null;
		W5QueryField stackedQueryField = null;

		if (stackFieldId != 0)
			for (W5QueryField qf : queryResult.getQuery().get_queryFields())
				if (stackFieldId == qf.getQueryFieldId()) {
					stackField = qf.getDsc();
					if (stackField.equals(tableFieldSQL))
						stackField = null;
					else
						stackedQueryField = qf;
					break;
				}
		//
		// queryResult.setOrderBy(PromisUtil.uStrNvl(requestParams.get(PromisUtil.uStrNvl(PromisSetting.appSettings.get("sql_sort"),"sort")),
		// queryResult.getQuery().getSqlOrderby()));
		// queryResult.setOrderBy(queryResult.getQuery().getSqlOrderby());

		if (tableField.getParamTip() == 2 || tableField.getDefaultControlTip() == 2
				|| tableField.getDefaultControlTip() == 18) {
			tableFieldSQL = "to_char(" + tableFieldSQL + ", '"
					+ (new String[] { "yyyy", "yyyy/Q", "yyyy/mm", "yyyy/WW", "yyyy/mm/dd" }[GenericUtil
							.uInt(requestParams, "_dtt")])
					+ "')";
		}
		switch (queryResult.getQuery().getQueryTip()) {
		case 9:
		case 10:
		case 15: // TODO: aslinda hata. olmamasi lazim
			throw new IWBException("framework", "Query", queryId, null,
					LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_grid_stat_query_error"), null);
		default:
			String[] stats = new String[] { "", "sum", "avg", "max", "min" };
			queryResult.prepareQuery(null);
			if (!queryResult.getErrorMap().isEmpty())
				throw new IWBException("framework", "Query", queryId, null,
						LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_grid_stat_error"), null);
			String sql = "select " + tableFieldSQL + " id, ";
			if (!GenericUtil.isEmpty(stackField))
				sql += stackField + " stack_id, ";
			if (statType != 0) {
				String[] fq = funcFields.split(",");
				int count = 0;
				Set<Integer> fqs = new HashSet();
				for (String s : fq) {
					int isx = GenericUtil.uInt(s);
					if (isx < 0) {
						List<W5TableFieldCalculated> ltcf = find(
								"from W5TableFieldCalculated t where t.projectUuid=? AND t.tableId=? AND t.tableFieldCalculatedId=?",
								projectId, t.getTableId(), -isx);
						if (ltcf.isEmpty())
							throw new IWBException("framework", "Query", queryId, null,
									LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_grid_stat_field_error"),
									null);
						count++;
						W5TableFieldCalculated tcf = ltcf.get(0);
						Object[] oo = DBUtil.filterExt4SQL(tcf.getSqlCode(), scd, requestParams, null);
						// tableFieldSQL =
						// oo[0].toString();//.put(tableFieldChain,
						// o.toString());
						if (oo.length > 1)
							params.addAll((List) oo[1]);

						if (count > 1)
							sql += "," + stats[statType] + "((" + oo[0] + ")) xres" + count;
						else
							sql += stats[statType] + "((" + oo[0] + ")) xres";

					} else
						fqs.add(isx);
				}

				for (W5QueryField o : queryResult.getQuery().get_queryFields())
					if (fqs.contains(o.getQueryFieldId())) {
						count++;
						if (count > 1)
							sql += "," + stats[statType] + "(" + o.getDsc() + ") xres" + count;
						else
							sql += stats[statType] + "(" + o.getDsc() + ") xres";
					}

				if (count == 0)
					throw new IWBException("framework", "Query", queryId, null,
							LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_grid_stat_error"), null);
			} else {
				sql += "count(1) xres";
			}

			sql += " from (" + queryResult.getExecutedSql() + ") x group by id";
			if (!GenericUtil.isEmpty(stackField))
				sql += ", stack_id";
			sql += tableFieldSQL.startsWith("to_char(") ? " order by id" : " order by xres desc";
			queryResult.setExecutedSql(sql);
		}
		Map result = new HashMap();
		result.put("success", true);
		if (queryResult.getErrorMap().isEmpty()) {
			if (!params.isEmpty())
				queryResult.getSqlParams().addAll(0, params);
			List<Map> l = executeSQLQuery2Map(queryResult.getExecutedSql(), queryResult.getSqlParams());
			if (l != null) {
				if (!GenericUtil.isEmpty(stackField)) {
					Set<String> stackSet = new HashSet();
					List<Map> nl = new ArrayList();
					Map<Object, Map> nom = new HashMap();
					for (Map m : l) {
						Object oid = m.get("id");
						Map nm = nom.get(oid);
						if (nm == null) {
							nm = new HashMap();
							nm.put("id", oid);
							nom.put(oid, nm);
							nl.add(nm);
						}
						for (Object k : m.keySet())
							if (!k.equals("id") && !k.equals("stack_id")) {
								if (!GenericUtil.isEmpty(m.get("stack_id"))) {
									nm.put(k + "_" + m.get("stack_id"), m.get(k));
									stackSet.add(m.get("stack_id").toString());
								}
							}
					}
					l = nl;
					Map lm = new HashMap();
					switch (stackedQueryField.getPostProcessTip()) {
					case 10: // static;
						W5LookUp ld3 = FrameworkCache.getLookUp(projectId, stackedQueryField.getLookupQueryId());
						if (ld3 != null)
							for (Object k : stackSet)
								if (ld3.get_detayMap().get(k) != null)
									lm.put(k, GenericUtil
											.uStrMax(LocaleMsgCache.get2(scd, ld3.get_detayMap().get(k).getDsc()), 20));
								else
									throw new IWBException("framework",
											"QueryField", stackedQueryField.getQueryFieldId(), null, LocaleMsgCache
													.get2(0, (String) scd.get("locale"), "fw_grid_stat_stacked_error"),
											null);
						break;
					case 12: // table
						for (Object k : stackSet) {
							String s = getTableRecordSummary(scd, stackedQueryField.getLookupQueryId(),
									GenericUtil.uInt(k), 20);
							lm.put(k, GenericUtil.isEmpty(s) ? "Not found for: " + k : s);
						}
						break;
					case 20:
					case 53: // user
						for (Object k : stackSet)
							lm.put(k, GenericUtil.uStrMax(UserUtil.getUserDsc(GenericUtil.uInt(k)), 20));
					}
					result.put("lookUp", lm);
				} else {
					int maxLegend = (tableField.getParamTip() == 2 || tableField.getDefaultControlTip() == 2
							|| tableField.getDefaultControlTip() == 18) ? 360 : 10;
					if (l.size() > maxLegend) { // TODO: temizlik
						BigDecimal total = new BigDecimal(0);
						String ids = "";
						for (int qi = l.size() - 1; qi >= maxLegend - 1; qi--) {
							Map m = l.get(qi);
							ids += "," + m.get("id");
							if (m.get("xres") != null)
								total = total.add(new BigDecimal(m.get("xres").toString()));
							l.remove(qi);
						}
						Map nm = new HashMap();
						nm.put("id", -999999);
						nm.put("dsc", LocaleMsgCache.get2(scd, "others"));
						nm.put("xres", total);
						nm.put("ids", ids.substring(1));
						l.add(nm);
					}
				}
				switch (tableField.getDefaultControlTip()) {
				case 6: // lookup static
					W5LookUp ld2 = FrameworkCache.getLookUp(projectId, tableField.getDefaultLookupTableId());
					if (ld2 != null)
						for (Map m : l) {
							Object o2 = m.get("id");
							if (o2 != null && GenericUtil.uInt(o2) != -999999) {
								if (ld2.get_detayMap().get(o2) != null)
									m.put("dsc", GenericUtil.uStrMax(
											LocaleMsgCache.get2(scd, ld2.get_detayMap().get(o2).getDsc()), 20));
							}
						}
					break;
				case 7:
				case 10: // lookup table
					for (Map m : l) {
						Object o2 = m.get("id");
						if (o2 != null && GenericUtil.uInt(o2) != -999999) {
							String s = getTableRecordSummary(scd, tableField.getDefaultLookupTableId(),
									GenericUtil.uInt(o2), 20);
							m.put("dsc", GenericUtil.isEmpty(s) ? "Not found for: " + o2 : s);
						}
					}
					break;
				default:
					for (Map m : l) {
						Object o2 = m.get("id");
						m.put("dsc", o2 == null ? "" : o2.toString());
					}
				}
			}
			result.put("data", l);
		}
		return result;
	}

	public List executeQuery4DataList(Map<String, Object> scd, W5Table t, Map<String, String> requestParams) {


		/*
		 * W5Query q = new W5Query(); q.setMainTableId(tableId);
		 * q.setSqlFrom(t.getDsc() + " x");
		 * 
		 * W5QueryResult queryResult = new W5QueryResult(-1);
		 * 
		 * queryResult.setErrorMap(new HashMap());queryResult.setScd(scd);
		 * queryResult.setRequestParams(requestParams);
		 */
		StringBuilder sql = new StringBuilder();
		String dateFormat = GenericUtil.uStrNvl(requestParams.get("dtFmt"), "dd/mm/yyyy");
		sql.append("SELECT ");
		String[] cols = requestParams.get("cols").split(",");
		boolean cldFlag = false, cntFlag = false;

		Map<String, W5LookUp> staticLookups = new HashMap();
		String groupBy = "", clcFieldPrefix = "iwb_x_qw_";
		int iwfField = 0;
		Map<String, String> iwbFieldMap = new HashMap();
		Map<String, String> errorMap = new HashMap();
		for (String c : cols) {
			String fieldName = "";

			if (c.startsWith("clc.")) {
				String c2 = c.substring(4);
				List<W5TableFieldCalculated> l = find(
						"from W5TableFieldCalculated t where t.projectUuid=? AND t.tableId=? AND t.dsc=?",
						t.getProjectUuid(), t.getTableId(), c2);
				if (!l.isEmpty()) {
					sql.append("(").append(l.get(0).getSqlCode()).append(")");
					iwfField++;
					fieldName = clcFieldPrefix + iwfField;
					iwbFieldMap.put(c, fieldName);
				} else { // TODO ERROR
					errorMap.put(c, "Calculated Field not found");
					continue;
				}
				// sql.append("").append(l.get(0).getSqlCode().replaceAll("x.",
				// "x.")).append(" as
				// ").append(l.get(0).getDsc()).append(",");
			} else if (c.startsWith("cld.")) { // childs: only count and sum

				String[] sss = c.substring(4).replace(".", "&").split("&");
				if (sss.length == 1) {
					errorMap.put(c, "Table Child Field not defined");
					continue;
				}
				StringBuilder newSub = new StringBuilder();
				W5Table detT = null;
				W5TableChild detTc = null;
				for (W5TableChild tc : t.get_tableChildList()) {
					detT = FrameworkCache.getTable(scd, tc.getRelatedTableId());
					if (detT != null && detT.getDsc().equals(sss[0])) {
						detTc = tc;
						break;
					}
				}
				if (detT == null) {
					errorMap.put(c, "Table Child not found");
					continue;
				}
				for (int isss = 1; isss < sss.length && isss < 2; isss++) { // TODO
																			// simdilik
																			// 1
																			// seviye
					if (sss[isss].equals("cld")) {
						isss++;
						if (isss >= sss.length) {
							errorMap.put(c, "Child Tables wrong definition");
							continue;
						}
						if (GenericUtil.isEmpty(detT.get_tableChildList())) {
							errorMap.put(c, "Child Tables does not exist for [" + detT.getDsc() + "]");
							continue;
						}
						for (W5TableChild tc : detT.get_tableChildList()) {
							detT = FrameworkCache.getTable(scd, tc.getRelatedTableId());
							if (detT != null && detT.getDsc().equals(sss[isss])) {
								detTc = tc;
								break;
							}
						}
						errorMap.put(c, "Child Tables not implemented"); // TODO
						continue;

					} else if (sss[isss].equals("clc")) {
						isss++;
						if (isss >= sss.length) {
							errorMap.put(c, "Calculated Field wrong definition");
							continue;
						}
						List<W5TableFieldCalculated> l = find(
								"from W5TableFieldCalculated t where t.projectUuid=? AND t.tableId=? AND t.dsc=?",
								t.getProjectUuid(), detT.getTableId(), sss[isss]);
						if (!l.isEmpty()) {
							newSub.append("SELECT ")
									.append("sum" /* valMap.get(c) */).append("((")
									.append(l.get(0).getSqlCode().replaceAll("x.", "z" + isss + ".")).append(")) from ")
									.append(detT.getDsc()).append(" z").append(isss).append(" WHERE ");
							newSub.append(" x.").append(t.get_tableFieldMap().get(detTc.getTableFieldId()).getDsc())
									.append("=z").append(isss).append(".")
									.append(detT.get_tableFieldMap().get(detTc.getRelatedTableFieldId()).getDsc());
							if (detTc.getRelatedStaticTableFieldId() > 0
									&& !GenericUtil.isEmpty(detTc.getRelatedStaticTableFieldVal()))
								newSub.append(" AND z").append(isss)
										.append(".").append(detT.get_tableFieldMap()
												.get(detTc.getRelatedStaticTableFieldId()).getDsc())
										.append("=").append(detTc.getRelatedStaticTableFieldVal());
							// if(detT.get_tableParamList().size()>1)newSub.append("
							// AND
							// z").append(isss).append(".").append("customization_id=${scd.customizationId}");
							newSub.append(DBUtil.includeTenantProjectPostSQL(scd, detT, "z" + isss));
							sql.append("(").append(newSub).append(")");
							iwfField++;
							fieldName = clcFieldPrefix + iwfField;
							iwbFieldMap.put(c, fieldName);
							// if(valMap.get(c).equals("count"))valMap.put(c,"sum");
						} else { // TODO ERROR
							errorMap.put(c, "Calculated Field not found");
							continue;
						}

					} else if (sss[isss].equals("0")) {
						newSub.append("SELECT count(1) from ").append(detT.getDsc()).append(" z").append(isss)
								.append(" WHERE ");
						newSub.append(" x.").append(t.get_tableFieldMap().get(detTc.getTableFieldId()).getDsc())
								.append("=z").append(isss).append(".")
								.append(detT.get_tableFieldMap().get(detTc.getRelatedTableFieldId()).getDsc());
						if (detTc.getRelatedStaticTableFieldId() > 0
								&& !GenericUtil.isEmpty(detTc.getRelatedStaticTableFieldVal()))
							newSub.append(" AND z").append(isss).append(".")
									.append(detT.get_tableFieldMap().get(detTc.getRelatedStaticTableFieldId()).getDsc())
									.append("=").append(detTc.getRelatedStaticTableFieldVal());
						// if(detT.get_tableParamList().size()>1)newSub.append("
						// AND
						// z").append(isss).append(".").append("customization_id=${scd.customizationId}");
						newSub.append(DBUtil.includeTenantProjectPostSQL(scd, detT, "z" + isss));
						sql.append("(").append(newSub).append(")");
						iwfField++;
						fieldName = clcFieldPrefix + iwfField;
						iwbFieldMap.put(c, fieldName);
						// if(valMap.get(c).equals("count"))valMap.put(c,"sum");
						break;
					} else
						for (W5TableField tf : detT.get_tableFieldList())
							if (tf.getDsc().equals(sss[isss])) {
								newSub.append("SELECT ")
										.append("sum" /* valMap.get(c) */).append("(z").append(isss).append(".")
										.append(tf.getDsc()).append(") from ").append(detT.getDsc()).append(" z")
										.append(isss).append(" WHERE ");
								newSub.append(" x.").append(t.get_tableFieldMap().get(detTc.getTableFieldId()).getDsc())
										.append("=z").append(isss).append(".")
										.append(detT.get_tableFieldMap().get(detTc.getRelatedTableFieldId()).getDsc());
								if (detTc.getRelatedStaticTableFieldId() > 0
										&& !GenericUtil.isEmpty(detTc.getRelatedStaticTableFieldVal()))
									newSub.append(" AND z").append(isss).append(".")
											.append(detT.get_tableFieldMap().get(detTc.getRelatedStaticTableFieldId())
													.getDsc())
											.append("=").append(detTc.getRelatedStaticTableFieldVal());
								// if(detT.get_tableParamList().size()>1)newSub.append("
								// AND
								// z").append(isss).append(".").append("customization_id=${scd.customizationId}");
								newSub.append(DBUtil.includeTenantProjectPostSQL(scd, detT, "z" + isss));
								sql.append("(").append(newSub).append(")");
								iwfField++;
								fieldName = clcFieldPrefix + iwfField;
								iwbFieldMap.put(c, fieldName);
								// if(valMap.get(c).equals("count"))valMap.put(c,"sum");
								break;
							}
				}

			} else if (c.startsWith("lnk.") && c.lastIndexOf('.') > 4) { // parents
				W5TableField tableField = null, mtableField = null;

				String[] sss = c.substring(4).replace(".", "&").split("&");
				W5Table newT = t;
				StringBuilder newSub = new StringBuilder();
				StringBuilder newSub2 = new StringBuilder();
				boolean foundSt = false;
				for (int isss = 0; isss < sss.length - 1; isss++) {
					if (isss > 0) {
						newSub2.setLength(0);
						newSub2.append(newSub);
						newSub.setLength(0);
					}
					for (W5TableField tf : newT.get_tableFieldList())
						if (tf.getDsc().equals(sss[isss])) {
							foundSt = false;
							if (mtableField == null)
								mtableField = tf;
							if (tf.getDefaultControlTip() == 7 || tf.getDefaultControlTip() == 9 || tf
									.getDefaultControlTip() == 10 /*
																	 * || tf.
																	 * getDefaultControlTip
																	 * ()==15
																	 */) { // sub
																		// table
								W5Table st = FrameworkCache.getTable(scd, tf.getDefaultLookupTableId());
								if (st == null)
									break; // HATA: gerekli bir alt kademe
											// tabloya ulasilamadi
								for (W5TableField stf : st.get_tableFieldList())
									if (stf.getDsc().equals(sss[isss + 1])) {
										tableField = stf;
										foundSt = true;
										break;
									}
								if (!foundSt)
									break; // HATA: bir sonraki field bulunamadi
								newSub.append("(select ");
								if (isss == sss.length - 2)
									switch (tableField.getDefaultControlTip()) {
									case 2: // date
										newSub.append("to_char(y.").append(isss).append(".").append(sss[isss + 1])
												.append(",'").append(dateFormat).append("')");
										break;
									case 7:
									case 10:
									case 9: // lookup
										if (tableField.getDefaultLookupTableId() > 0) {
											W5Table dt = FrameworkCache.getTable(scd,
													tableField.getDefaultLookupTableId());
											if (dt != null && !GenericUtil.isEmpty(dt.getSummaryRecordSql())) {
												newSub.append("(SELECT ")
														.append(dt.getSummaryRecordSql().replaceAll("x.", "qxq."))
														.append(" FROM ").append(dt.getDsc()).append(" qxq WHERE qxq.")
														.append(dt.get_tableFieldList().get(0).getDsc()).append("=y")
														.append(isss).append(".").append(sss[isss + 1]);
												// if(dt.get_tableParamList().size()>1)newSub.append("
												// AND
												// qxq.customization_id=${scd.customizationId}");
												newSub.append(DBUtil.includeTenantProjectPostSQL(scd, dt, "qxq"));

												newSub.append(")");
											} else
												newSub.append("y").append(isss).append(".").append(sss[isss + 1]);
										} else
											newSub.append("y").append(isss).append(".").append(sss[isss + 1]);
										break;
									default:
										newSub.append("y").append(isss).append(".").append(sss[isss + 1]);
									}
								else
									newSub.append("y").append(isss).append(".").append(sss[isss + 1]);
								newSub.append(" from ").append(st.getDsc()).append(" y").append(isss).append(" where y")
										.append(isss).append(".").append(st.get_tableFieldList().get(0).getDsc())
										.append("=").append(isss == 0 ? ("x." + sss[isss]) : newSub2);
								if (st.get_tableFieldList().size() > 1)
									for (W5TableField wtf : st.get_tableFieldList())
										if (wtf.getDsc().equals("project_uuid")) {
											newSub.append(" AND y").append(isss)
													.append(".project_uuid='${scd.projectId}'");
											break;
										}

								newSub.append(")");
								newT = st;
							}
							break;
						}
					if (!foundSt) { // bulamamis uygun sey
						break;
					}
				}
				if (foundSt && newSub.length() > 0) {
					sql.append("(").append(newSub).append(")"); // .put(tableFieldChain,
																// o.toString());
				} else
					throw new IWBException("framework", "Query", 0, null,
							LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_pivot_field_error"), null);

				iwfField++;
				fieldName = clcFieldPrefix + iwfField;
				iwbFieldMap.put(c, fieldName);
				if (tableField != null && tableField.getDefaultControlTip() == 6) {
					W5LookUp lu = FrameworkCache.getLookUp(scd, tableField.getDefaultLookupTableId());
					if (lu != null)
						staticLookups.put(fieldName, lu);
				}

			} else {
				if (c.startsWith("lnk.")) {
					iwbFieldMap.put(c, c.substring(4));
					c = c.substring(4);
				}
				for (W5TableField f : t.get_tableFieldList())
					if (f.getDsc().equals(c)) {
						if (f.getDefaultControlTip() == 6 && f.getDefaultLookupTableId() > 0) {
							W5LookUp lu = FrameworkCache.getLookUp(scd, f.getDefaultLookupTableId());
							if (lu != null)
								staticLookups.put(f.getDsc(), lu);
						}
						if ((f.getDefaultControlTip() == 7 || f.getDefaultControlTip() == 10)
								&& f.getDefaultLookupTableId() > 0) {
							W5Table dt = FrameworkCache.getTable(scd, f.getDefaultLookupTableId());
							if (dt != null && !GenericUtil.isEmpty(dt.getSummaryRecordSql())) {
								sql.append("(SELECT ").append(dt.getSummaryRecordSql().replaceAll("x.", "qxq."))
										.append(" FROM ").append(dt.getDsc()).append(" qxq WHERE qxq.")
										.append(dt.get_tableFieldList().get(0).getDsc()).append("=x.")
										.append(f.getDsc());
								// if(dt.get_tableParamList().size()>1)sql.append("
								// AND
								// qxq.customization_id=${scd.customizationId}");
								sql.append(DBUtil.includeTenantProjectPostSQL(scd, dt, "qxq"));
								sql.append(")");
							} else {
								sql.append("x.").append(f.getDsc());
							}
						} else {
							if (f.getFieldTip() == 2) { // date ise
								sql.append("to_char(x.").append(f.getDsc()).append(",'").append(dateFormat)
										.append("')");
							} else
								sql.append("x.").append(f.getDsc());
						}
						fieldName = f.getDsc();
						break;
					}
			}

			sql.append(" ").append(fieldName).append(",");
		}
		sql.setLength(sql.length() - 1);
		sql.append(" FROM ").append(t.getDsc()).append(" x");
		if (t.get_tableParamList().size() > 1)
			sql.append(" WHERE 1=1").append(DBUtil.includeTenantProjectPostSQL(scd, t));
		// GROUP BY ").append(groupBy.substring(1)
		StringBuilder sql2 = sql;
		sql2.append(" limit 100000"); // simdilik sinir koyalim

		Object[] oz = DBUtil.filterExt4SQL(sql2.toString(), scd, requestParams, null);
		List<Map> lm = executeSQLQuery2Map(oz[0].toString(), (List) oz[1]);
		if (!staticLookups.isEmpty() || !iwbFieldMap.isEmpty())
			for (Map<String, Object> mo : lm) {
				for (String k : staticLookups.keySet()) {
					W5LookUp lu = staticLookups.get(k);
					W5LookUpDetay ld = lu.get_detayMap().get(mo.get(k));
					if (ld != null)
						mo.put(k, LocaleMsgCache.get2(scd, ld.getDsc()));
				}
				for (String k : iwbFieldMap.keySet()) {
					mo.put(k, mo.get(iwbFieldMap.get(k)));
					mo.remove(iwbFieldMap.get(k));
				}
			}
		return lm;
	}


	public List executeQuery4Pivot(Map<String, Object> scd, W5Table t, Map<String, String> requestParams) {


		/*
		 * W5Query q = new W5Query(); q.setMainTableId(tableId);
		 * q.setSqlFrom(t.getDsc() + " x");
		 * 
		 * W5QueryResult queryResult = new W5QueryResult(-1);
		 * 
		 * queryResult.setErrorMap(new HashMap());queryResult.setScd(scd);
		 * queryResult.setRequestParams(requestParams);
		 */
		StringBuilder sql = new StringBuilder();
		String dateFormat = GenericUtil.uStrNvl(requestParams.get("dtFmt"), "yyyy");
		sql.append("SELECT ");
		String[] cols = requestParams.get("cols").split(",");
		String vals = requestParams.get("vals");
		Map<String, String> valMap = new HashMap();
		boolean cldFlag = false, cntFlag = false;
		if (!GenericUtil.isEmpty(vals)) {
			if (vals.equals("count") || vals.equals(":count")) {
				valMap.put("1", "count");
			} else
				for (String s : vals.split(",")) {
					String[] o2 = s.replace(':', ',').split(",");
					String fnc = (o2.length > 1 && GenericUtil.hasPartInside("sum,count,min,max", o2[1].toLowerCase()))
							? o2[1].toLowerCase() : "count";
					if (fnc.equals("count")) {
						// valMap.clear();break;
						cntFlag = true;
						// return executeQuery4PivotBasic(scd, tableId,
						// requestParams);
					}
					if (o2[0].startsWith("cld.")) {
						cldFlag = true;
					}
					valMap.put(o2[0], fnc);
				}
		}
		if (!cldFlag && cntFlag) {
			valMap.clear();
		}

		Map<String, W5LookUp> staticLookups = new HashMap();
		String groupBy = "", clcFieldPrefix = "iwb_x_qw_";
		int iwfField = 0;
		Map<String, String> iwbFieldMap = new HashMap();
		Map<String, String> errorMap = new HashMap();
		for (String c : cols) {
			String fieldName = "";

			if (c.startsWith("clc.")) {
				String c2 = c.substring(4);
				List<W5TableFieldCalculated> l = metaDataDao.findTableCalcFieldByDsc(t, c2);
				if (!l.isEmpty()) {
					sql.append("(").append(l.get(0).getSqlCode()).append(")");
					iwfField++;
					fieldName = clcFieldPrefix + iwfField;
					iwbFieldMap.put(c, fieldName);
				} else { // TODO ERROR
					errorMap.put(c, "Calculated Field not found");
					continue;
				}
				// sql.append("").append(l.get(0).getSqlCode().replaceAll("x.",
				// "x.")).append(" as
				// ").append(l.get(0).getDsc()).append(",");
			} else if (c.startsWith("cld.")) { // childs: only count and sum
				if (!valMap.containsKey(c) || GenericUtil.isEmpty(t.get_tableChildList())) {
					errorMap.put(c, "Child Tables does not exist for [" + t.getDsc() + "]");
					continue;
				}
				String[] sss = c.substring(4).replace(".", "&").split("&");
				if (sss.length == 1) {
					errorMap.put(c, "Table Child Field not defined");
					continue;
				}
				StringBuilder newSub = new StringBuilder();
				W5Table detT = null;
				W5TableChild detTc = null;
				for (W5TableChild tc : t.get_tableChildList()) {
					detT = FrameworkCache.getTable(scd, tc.getRelatedTableId());
					if (detT != null && detT.getDsc().equals(sss[0])) {
						detTc = tc;
						break;
					}
				}
				if (detT == null) {
					errorMap.put(c, "Table Child not found");
					continue;
				}
				for (int isss = 1; isss < sss.length && isss < 2; isss++) { // TODO
																			// simdilik
																			// 1
																			// seviye
					if (sss[isss].equals("cld")) {
						isss++;
						if (isss >= sss.length) {
							errorMap.put(c, "Child Tables wrong definition");
							continue;
						}
						if (GenericUtil.isEmpty(detT.get_tableChildList())) {
							errorMap.put(c, "Child Tables does not exist for [" + detT.getDsc() + "]");
							continue;
						}
						for (W5TableChild tc : detT.get_tableChildList()) {
							detT = FrameworkCache.getTable(scd, tc.getRelatedTableId());
							if (detT != null && detT.getDsc().equals(sss[isss])) {
								detTc = tc;
								break;
							}
						}
						errorMap.put(c, "Child Tables not implemented"); // TODO
						continue;

					} else if (sss[isss].equals("clc")) {
						isss++;
						if (isss >= sss.length) {
							errorMap.put(c, "Calculated Field wrong definition");
							continue;
						}
						List<W5TableFieldCalculated> l = metaDataDao.findTableCalcFieldByDsc(detT, sss[isss]);
						if (!l.isEmpty()) {
							newSub.append("SELECT ").append(valMap.get(c)).append("((")
									.append(l.get(0).getSqlCode().replaceAll("x.", "z" + isss + ".")).append(")) from ")
									.append(detT.getDsc()).append(" z").append(isss).append(" WHERE ");
							newSub.append(" x.").append(t.get_tableFieldMap().get(detTc.getTableFieldId()).getDsc())
									.append("=z").append(isss).append(".")
									.append(detT.get_tableFieldMap().get(detTc.getRelatedTableFieldId()).getDsc());
							if (detTc.getRelatedStaticTableFieldId() > 0
									&& !GenericUtil.isEmpty(detTc.getRelatedStaticTableFieldVal()))
								newSub.append(" AND z").append(isss)
										.append(".").append(detT.get_tableFieldMap()
												.get(detTc.getRelatedStaticTableFieldId()).getDsc())
										.append("=").append(detTc.getRelatedStaticTableFieldVal());
							// if(detT.get_tableParamList().size()>1)newSub.append("
							// AND
							// z").append(isss).append(".").append("customization_id=${scd.customizationId}");
							newSub.append(DBUtil.includeTenantProjectPostSQL(scd, detT, "z" + isss));

							sql.append("(").append(newSub).append(")");
							iwfField++;
							fieldName = clcFieldPrefix + iwfField;
							iwbFieldMap.put(c, fieldName);
							if (valMap.get(c).equals("count"))
								valMap.put(c, "sum");
						} else { // TODO ERROR
							errorMap.put(c, "Calculated Field not found");
							continue;
						}

					} else
						for (W5TableField tf : detT.get_tableFieldList())
							if (tf.getDsc().equals(sss[isss])) {
								newSub.append("SELECT ").append(valMap.get(c)).append("(z").append(isss).append(".")
										.append(tf.getDsc()).append(") from ").append(detT.getDsc()).append(" z")
										.append(isss).append(" WHERE ");
								newSub.append(" x.").append(t.get_tableFieldMap().get(detTc.getTableFieldId()).getDsc())
										.append("=z").append(isss).append(".")
										.append(detT.get_tableFieldMap().get(detTc.getRelatedTableFieldId()).getDsc());
								if (detTc.getRelatedStaticTableFieldId() > 0
										&& !GenericUtil.isEmpty(detTc.getRelatedStaticTableFieldVal()))
									newSub.append(" AND z").append(isss).append(".")
											.append(detT.get_tableFieldMap().get(detTc.getRelatedStaticTableFieldId())
													.getDsc())
											.append("=").append(detTc.getRelatedStaticTableFieldVal());
								// if(detT.get_tableParamList().size()>1)newSub.append("
								// AND
								// z").append(isss).append(".").append("customization_id=${scd.customizationId}");
								newSub.append(DBUtil.includeTenantProjectPostSQL(scd, detT, "z" + isss));
								sql.append("(").append(newSub).append(")");
								iwfField++;
								fieldName = clcFieldPrefix + iwfField;
								iwbFieldMap.put(c, fieldName);
								if (valMap.get(c).equals("count"))
									valMap.put(c, "sum");
								break;
							}
				}

			} else if (c.startsWith("lnk.") && c.lastIndexOf('.') > 4) { // parents
				W5TableField tableField = null, mtableField = null;

				String[] sss = c.substring(4).replace(".", "&").split("&");
				W5Table newT = t;
				StringBuilder newSub = new StringBuilder();
				StringBuilder newSub2 = new StringBuilder();
				boolean foundSt = false;
				for (int isss = 0; isss < sss.length - 1; isss++) {
					if (isss > 0) {
						newSub2.setLength(0);
						newSub2.append(newSub);
						newSub.setLength(0);
					}
					for (W5TableField tf : newT.get_tableFieldList())
						if (tf.getDsc().equals(sss[isss])) {
							foundSt = false;
							if (mtableField == null)
								mtableField = tf;
							if (tf.getDefaultControlTip() == 7 || tf.getDefaultControlTip() == 9 || tf
									.getDefaultControlTip() == 10 /*
																	 * || tf.
																	 * getDefaultControlTip
																	 * ()==15
																	 */) { // sub
																		// table
								W5Table st = FrameworkCache.getTable(scd, tf.getDefaultLookupTableId());
								if (st == null)
									break; // HATA: gerekli bir alt kademe
											// tabloya ulasilamadi
								for (W5TableField stf : st.get_tableFieldList())
									if (stf.getDsc().equals(sss[isss + 1])) {
										tableField = stf;
										foundSt = true;
										break;
									}
								if (!foundSt)
									break; // HATA: bir sonraki field bulunamadi
								newSub.append("(select ");
								if (isss == sss.length - 2)
									switch (tableField.getDefaultControlTip()) {
									case 2: // date
										newSub.append("to_char(y.").append(isss).append(".").append(sss[isss + 1])
												.append(",'").append(dateFormat).append("')");
										break;
									case 7:
									case 10:
									case 9: // lookup
										if (tableField.getDefaultLookupTableId() > 0) {
											W5Table dt = FrameworkCache.getTable(scd,
													tableField.getDefaultLookupTableId());
											if (dt != null && !GenericUtil.isEmpty(dt.getSummaryRecordSql())) {
												newSub.append("(SELECT ")
														.append(dt.getSummaryRecordSql().replaceAll("x.", "qxq."))
														.append(" FROM ").append(dt.getDsc()).append(" qxq WHERE qxq.")
														.append(dt.get_tableFieldList().get(0).getDsc()).append("=y")
														.append(isss).append(".").append(sss[isss + 1]);
												// if(dt.get_tableParamList().size()>1)newSub.append("
												// AND
												// qxq.customization_id=${scd.customizationId}");
												newSub.append(DBUtil.includeTenantProjectPostSQL(scd, dt, "qxq"));

												newSub.append(")");
											} else
												newSub.append("y").append(isss).append(".").append(sss[isss + 1]);
										} else
											newSub.append("y").append(isss).append(".").append(sss[isss + 1]);
										break;
									default:
										newSub.append("y").append(isss).append(".").append(sss[isss + 1]);
									}
								else
									newSub.append("y").append(isss).append(".").append(sss[isss + 1]);
								newSub.append(" from ").append(st.getDsc()).append(" y").append(isss).append(" where y")
										.append(isss).append(".").append(st.get_tableFieldList().get(0).getDsc())
										.append("=").append(isss == 0 ? ("x." + sss[isss]) : newSub2);
								if (st.get_tableFieldList().size() > 1)
									for (W5TableField wtf : st.get_tableFieldList())
										if (wtf.getDsc().equals("project_uuid")) {
											newSub.append(" AND y").append(isss)
													.append(".project_uuid='${scd.projectId}'");
											break;
										}

								newSub.append(")");
								newT = st;
							}
							break;
						}
					if (!foundSt) { // bulamamis uygun sey
						break;
					}
				}
				if (foundSt && newSub.length() > 0) {
					sql.append("(").append(newSub).append(")"); // .put(tableFieldChain,
																// o.toString());
				} else
					throw new IWBException("framework", "Query", 0, null,
							LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_pivot_field_error"), null);

				iwfField++;
				fieldName = clcFieldPrefix + iwfField;
				iwbFieldMap.put(c, fieldName);
				if (tableField != null && tableField.getDefaultControlTip() == 6) {
					W5LookUp lu = FrameworkCache.getLookUp(scd, tableField.getDefaultLookupTableId());
					if (lu != null)
						staticLookups.put(fieldName, lu);
				}

			} else {
				if (c.startsWith("lnk.")) {
					iwbFieldMap.put(c, c.substring(4));
					c = c.substring(4);
				}
				for (W5TableField f : t.get_tableFieldList())
					if (f.getDsc().equals(c)) {
						if (f.getDefaultControlTip() == 6 && f.getDefaultLookupTableId() > 0) {
							W5LookUp lu = FrameworkCache.getLookUp(scd, f.getDefaultLookupTableId());
							if (lu != null)
								staticLookups.put(f.getDsc(), lu);
						}
						if ((f.getDefaultControlTip() == 7 || f.getDefaultControlTip() == 10)
								&& f.getDefaultLookupTableId() > 0) {
							W5Table dt = FrameworkCache.getTable(scd, f.getDefaultLookupTableId());
							if (dt != null && !GenericUtil.isEmpty(dt.getSummaryRecordSql())) {
								sql.append("(SELECT ").append(dt.getSummaryRecordSql().replaceAll("x.", "qxq."))
										.append(" FROM ").append(dt.getDsc()).append(" qxq WHERE qxq.")
										.append(dt.get_tableFieldList().get(0).getDsc()).append("=x.")
										.append(f.getDsc());
								// if(dt.get_tableParamList().size()>1)sql.append("
								// AND
								// qxq.customization_id=${scd.customizationId}");
								sql.append(DBUtil.includeTenantProjectPostSQL(scd, dt, "qxq"));

								sql.append(")");
							} else {
								sql.append("x.").append(f.getDsc());
							}
						} else {
							if (f.getFieldTip() == 2) { // date ise
								sql.append("to_char(x.").append(f.getDsc()).append(",'").append(dateFormat)
										.append("')");
							} else
								sql.append("x.").append(f.getDsc());
						}
						fieldName = f.getDsc();
						break;
					}
			}
			if (!valMap.containsKey(c))
				groupBy += "," + fieldName;

			sql.append(" ").append(fieldName).append(",");
		}
		sql.setLength(sql.length() - 1);
		sql.append(" FROM ").append(t.getDsc()).append(" x");
		if (t.get_tableParamList().size() > 1)
			sql.append(" WHERE 1=1").append(DBUtil.includeTenantProjectPostSQL(scd, t));
		// GROUP BY ").append(groupBy.substring(1)
		StringBuilder sql2;
		if (!valMap.isEmpty()) {
			sql2 = new StringBuilder(sql.length() + 3 * groupBy.length() + 50);
			sql2.append("SELECT ").append(groupBy.substring(1));
			for (String c : valMap.keySet()) {
				String fieldName = iwbFieldMap.containsKey(c) ? iwbFieldMap.get(c) : c;
				sql2.append(",").append(valMap.get(c)).append("(").append(fieldName).append(") ")
						.append(fieldName.equals("1") ? "xxx" : fieldName);
			}
			sql2.append(" FROM (").append(sql).append(") mq GROUP BY ").append(groupBy.substring(1));
		} else
			sql2 = sql;

		Object[] oz = DBUtil.filterExt4SQL(sql2.toString(), scd, requestParams, null);
		List<Map> lm = executeSQLQuery2Map(oz[0].toString(), (List) oz[1]);
		if (!staticLookups.isEmpty() || !iwbFieldMap.isEmpty())
			for (Map<String, Object> mo : lm) {
				for (String k : staticLookups.keySet()) {
					W5LookUp lu = staticLookups.get(k);
					W5LookUpDetay ld = lu.get_detayMap().get(mo.get(k));
					if (ld != null)
						mo.put(k, LocaleMsgCache.get2(scd, ld.getDsc()));
				}
				for (String k : iwbFieldMap.keySet()) {
					mo.put(k, mo.get(iwbFieldMap.get(k)));
					mo.remove(iwbFieldMap.get(k));
				}
			}
		return lm;
	}

	public String deleteSubProject(Map scd, String subProjectId) {
		W5Project po = FrameworkCache.getProject(scd);
		W5Project dpo = FrameworkCache.getProject(subProjectId);
		if (po == null || dpo == null)
			return "Projects do not exist";
		List ll = executeSQLQuery(
				"select 1 from iwb.w5_project_related_project where project_uuid=? AND related_project_uuid=?",
				po.getProjectUuid(), dpo.getProjectUuid());
		if (GenericUtil.isEmpty(ll))
			return "No related project record";

		executeUpdateSQLQuery(
				"delete from iwb.w5_project_related_project where project_uuid=? AND related_project_uuid=?",
				po.getProjectUuid(), dpo.getProjectUuid());

		executeUpdateSQLQuery(
				"delete from iwb.w5_vcs_commit where project_uuid=? AND oproject_uuid=? and vcs_commit_id<0",
				po.getProjectUuid(), dpo.getProjectUuid());

		List<Object[]> tableNames = executeSQLQuery(
				"select t.dsc,(select tf.default_value from iwb.w5_table_field tf where tf.table_id=t.table_id AND tf.tab_order=1 AND tf.project_uuid=t.project_uuid limit 1) from iwb.w5_table t where t.project_uuid=? AND t.oproject_uuid=? order by table_id desc",
				po.getProjectUuid(), dpo.getProjectUuid());
		executeUpdateSQLQuery("set search_path=" + po.getRdbmsSchema());
		if (!GenericUtil.isEmpty(tableNames)) {
			StringBuilder sql = new StringBuilder();
			for (Object[] t : tableNames)
				if (GenericUtil.uInt(executeSQLQuery(
						"select count(1) from information_schema.tables qx where qx.table_name = ? and qx.table_schema = ?",
						t[0], po.getRdbmsSchema()).get(0)) > 0) {
					sql.append("drop table ").append(t[0]).append(";");
					if (!GenericUtil.isEmpty(t[1]) && t[1].toString().toLowerCase().startsWith("nextval(")) {
						String seq = t[1].toString().substring("nextval(".length() + 1, t[1].toString().length() - 2);
						if (GenericUtil.uInt(executeSQLQuery(
								"select count(1) from information_schema.sequences qx where qx.sequence_name = ? and qx.sequence_schema = ?",
								seq, po.getRdbmsSchema()).get(0)) > 0)
							sql.append("drop sequence ").append(seq).append(";");
					}
					sql.append("\n");
				}
			if (sql.length() != 0) {
				executeUpdateSQLQuery(sql.toString());
				W5VcsCommit commit = new W5VcsCommit();
				commit.setProjectUuid(po.getProjectUuid());
				commit.setCommitTip((short) 2);
				commit.setExtraSql(sql.toString());
				commit.setComment("iWB. Clean tables of sub project: " + dpo.getDsc());
				commit.setCommitUserId((Integer) scd.get("userId"));
				Object oi = executeSQLQuery("select nextval('iwb.seq_vcs_commit')").get(0);
				commit.setVcsCommitId(-GenericUtil.uInt(oi));
				saveObject(commit);
			}
		}

		executeUpdateSQLQuery(
				"update iwb.w5_vcs_object x " + "set vcs_object_status_tip=3 "
						+ "where x.project_uuid=? AND x.vcs_object_status_tip in (1,9) AND exists(select 1 from iwb.w5_vcs_object z where z.project_uuid=? AND z.table_id=x.table_id AND z.table_pk=x.table_pk)",
				po.getProjectUuid(), subProjectId);

		executeUpdateSQLQuery(
				"delete from iwb.w5_vcs_object x where "
						+ "x.project_uuid=? AND x.vcs_object_status_tip=2 AND exists(select 1 from iwb.w5_vcs_object z where z.project_uuid=? AND z.table_id=x.table_id AND z.table_pk=x.table_pk)",
				po.getProjectUuid(), subProjectId);

		List<Object> ll3 = executeSQLQuery(
				"select x.table_id from iwb.w5_vcs_object x where x.project_uuid=? group by x.table_id order by x.table_id",
				subProjectId);
		if (ll3 != null)
			for (Object o : ll3) {
				StringBuilder sql = new StringBuilder();
				W5Table t = FrameworkCache.getTable(0, GenericUtil.uInt(o));
				String pkField = t.get_tableParamList().get(0).getExpressionDsc();
				sql.append("delete from ").append(t.getDsc()).append(" x where x.project_uuid=? AND x.oproject_uuid=?");
				executeUpdateSQLQuery(sql.toString(), po.getProjectUuid(), subProjectId);
			}

		return null;
	}

	public boolean copyProject(Map scd, String dstProjectId, int dstCustomizationId) { // from,
																						// to
		String srcProjectId = (String) scd.get("projectId");
		if (srcProjectId.equals(dstProjectId))
			return false;
		W5Project po = FrameworkCache.getProject(srcProjectId), npo = null;
		// int customizationId = (Integer)scd.get("customizationId");
		int userId = (Integer) scd.get("userId");
		// int dstCustomizationId = customizationId;
		int smaxSqlCommit = 0;
		String schema = "c" + GenericUtil.lPad(dstCustomizationId + "", 5, '0') + "_" + dstProjectId.replace('-', '_');
		executeUpdateSQLQuery("set search_path=" + schema);

		npo = (W5Project) find("from W5Project w where w.projectUuid=?", dstProjectId).get(0);

		List<String> doneCommits = find(
				"select t.extraSql from W5VcsCommit t where t.commitTip=2 AND t.projectUuid=? AND (t.vcsCommitId>0 OR t.runLocalFlag!=0) AND length(t.extraSql)>2",
				dstProjectId);
		Set<String> doneSet = new HashSet();
		for (String co : doneCommits)
			doneSet.add(co);
		List<W5VcsCommit> sqlCommits = find(
				"from W5VcsCommit t where t.commitTip=2 AND t.projectUuid=? order by abs(t.vcsCommitId)", srcProjectId);
		for (W5VcsCommit o : sqlCommits) {
			if (!GenericUtil.isEmpty(o.getExtraSql()) && !doneSet.contains(o.getExtraSql())) {
				W5VcsCommit no = o.newInstance(dstProjectId);
				if (o.getVcsCommitId() > 0)
					no.setVcsCommitId(-no.getVcsCommitId());
				while (no.getExtraSql().contains(po.getRdbmsSchema()))
					no.setExtraSql(no.getExtraSql().replace(po.getRdbmsSchema(), schema));
				if ((o.getVcsCommitId() > 0 || o.getRunLocalFlag() != 0)) {
					if (no.getRunLocalFlag() == 0)
						no.setRunLocalFlag((short) 1);
					executeUpdateSQLQuery(no.getExtraSql());
					smaxSqlCommit = o.getVcsCommitId();
				}
				saveObject(no);
			}
		}

		executeUpdateSQLQuery(
				"update iwb.w5_vcs_object x "
						+ "set vcs_object_status_tip=(select case when z.vcs_object_status_tip in (3,8) then z.vcs_object_status_tip when x.vcs_commit_id!=2 AND z.vcs_commit_id!=x.vcs_commit_id then 1 else x.vcs_object_status_tip end from iwb.w5_vcs_object z where z.project_uuid=? AND z.table_id=x.table_id AND z.table_pk=x.table_pk),"
						+ "vcs_commit_id=(select z.vcs_commit_id from iwb.w5_vcs_object z where z.project_uuid=? AND z.table_id=x.table_id AND z.table_pk=x.table_pk) "
						+ "where x.project_uuid=? AND exists(select 1 from iwb.w5_vcs_object z where z.project_uuid=? AND z.table_id=x.table_id AND z.table_pk=x.table_pk)",
				srcProjectId, srcProjectId, dstProjectId, srcProjectId);

		executeUpdateSQLQuery(
				"INSERT INTO iwb.w5_vcs_object(vcs_object_id, table_id, table_pk, customization_id, project_uuid, vcs_commit_id, vcs_commit_record_hash, vcs_object_status_tip) "
						+ "select nextval('iwb.seq_vcs_object'), x.table_id, x.table_pk, ?, ?, 1, x.vcs_commit_record_hash, 2 from iwb.w5_vcs_object x where x.vcs_object_status_tip not in (3,8) AND x.project_uuid=? AND not exists(select 1 from iwb.w5_vcs_object z where z.project_uuid=? AND z.table_id=x.table_id AND z.table_pk=x.table_pk)",
				dstCustomizationId, dstProjectId, srcProjectId, dstProjectId);

		List<Object> ll3 = executeSQLQuery(
				"select x.table_id from iwb.w5_vcs_object x where x.project_uuid=? group by x.table_id order by x.table_id",
				srcProjectId);
		if (ll3 != null)
			for (Object o : ll3) {
				StringBuilder sql = new StringBuilder();
				W5Table t = FrameworkCache.getTable(0, GenericUtil.uInt(o));
				String pkField = t.get_tableParamList().get(0).getExpressionDsc();
				sql.append("delete from ").append(t.getDsc())
						.append(" x where x.project_uuid=? AND x.oproject_uuid=? AND not exists(select 1 from ")
						.append(t.getDsc()).append(" z where z.project_uuid=? AND z.").append(pkField).append("=x.")
						.append(pkField);
				for (W5TableField tf : t.get_tableFieldList())
					if (tf.getTabOrder() > 1 && !GenericUtil.hasPartInside2(
							"customization_id,project_uuid,oproject_uuid,version_no,insert_user_id,version_user_id,insert_dttm,version_dttm",
							tf.getDsc())) {
						sql.append(" AND x.").append(tf.getDsc()).append("=z.").append(tf.getDsc());
					}
				sql.append(")");
				executeUpdateSQLQuery(sql.toString(), dstProjectId, srcProjectId, srcProjectId);

				sql.setLength(0);
				StringBuilder sql2 = new StringBuilder();
				sql.append("insert into ").append(t.getDsc()).append("(");
				for (W5TableField tf : t.get_tableFieldList())
					if (!GenericUtil.hasPartInside2(
							"customization_id,project_uuid,oproject_uuid,version_no,insert_user_id,version_user_id,insert_dttm,version_dttm",
							tf.getDsc())) {
						sql.append(tf.getDsc()).append(",");
						sql2.append(tf.getDsc()).append(",");
					}
				sql.append("customization_id,project_uuid,oproject_uuid)");
				sql2.append("?,?,?");
				sql.append(" select ").append(sql2).append(" from ").append(t.getDsc())
						.append(" x where x.project_uuid=? AND not exists(select 1 from ").append(t.getDsc())
						.append(" z where z.project_uuid=? AND z.").append(pkField).append("=x.").append(pkField)
						.append(")");
				executeUpdateSQLQuery(sql.toString(), dstCustomizationId, dstProjectId, srcProjectId, srcProjectId,
						dstProjectId);
			}
		if (dstCustomizationId > 1) {
			List ll = executeSQLQuery(
					"select 1 from iwb.w5_project_related_project where project_uuid=? AND related_project_uuid=?",
					dstProjectId, srcProjectId);
			if (GenericUtil.isEmpty(ll))
				executeUpdateSQLQuery(
						"INSERT INTO iwb.w5_project_related_project(project_uuid, related_project_uuid, insert_user_id,version_user_id) "
								+ "VALUES (?, ?, ?, ?)",
						dstProjectId, srcProjectId, userId, userId);
		}

		executeUpdateSQLQuery(
				"INSERT INTO iwb.w5_project_related_project(project_uuid, related_project_uuid, insert_user_id,version_user_id) "
						+ "select ?, related_project_uuid, ?,? from iwb.w5_project_related_project x where x.project_uuid=? and not exists(select 1 from iwb.w5_project_related_project z where z.project_uuid=? AND z.related_project_uuid=x.related_project_uuid)",
				dstProjectId, userId, userId, srcProjectId, dstProjectId);
		return true;
	}

	public void deleteProjectMetadata(String delProjectId) {
		String[] tables = new String[] { "w5_table_access_control", "w5_ts_measurement_tag", "w5_ts_portlet_object",
				"w5_ts_portlet", "w5_ts_measurement_field", "w5_ts_measurement", "w5_ws_look_up_detay", "w5_ws_look_up",
				"m5_list_template", "w5_ws_server_token", "w5_ws_server_method_param", "w5_ws_server_method",
				"w5_ws_server", "w5_ws_method_param", "w5_ws_method", "w5_ws", "m5_list", "w5_tutorial",
				"w5_tutorial_user", "w5_table_access_condition_sql", "w5_table_trigger", "w5_form_hint",
				"w5_form_sms_mail_alarm", "w5_table_field_calculated", "w5_doc", "w5_list_column", "w5_list",
				"w5_data_view", "w5_converted_object", "w5_form_sms_mail", "w5_custom_grid_column_condtion",
				"w5_custom_grid_column_renderer", "w5_table_filter", "w5_conversion_col", "w5_conversion", "w5_feed",
				"w5_uploaded_data", "w5_uploaded_import_col_map", "w5_uploaded_import_col", "w5_uploaded_import",
				"w5_table_child", "w5_form_cell_code_detail", "w5_help", "w5_bi_graph_dashboard", "w5_mobile_device",
				"w5_approval_record", "w5_approval_step", "w5_approval", "w5_jasper_report", "w5_access_control",
				"w5_user_tip", "w5_role"
				// ,"w5_user"
				, "w5_login_rule_detail", "w5_login_rule", "w5_user_role", "w5_jasper_object", "w5_jasper",
				"w5_comment", "w5_form_value_cell", "w5_form_value", "w5_object_menu_item", "w5_grid_module",
				"w5_form_module", "w5_object_toolbar_item", "w5_exception_filter", "w5_xform_builder_detail",
				"w5_xform_builder", "w5_component", "m5_menu", "w5_menu", "w5_template_object", "w5_template", "w5_sms",
				"w5_table_param", "w5_form_cell", "w5_form", "w5_db_func_param", "w5_db_func", "w5_table_field",
				"w5_table", "w5_look_up_detay", "w5_look_up", "w5_locale_msg", "w5_query_param", "w5_query_field",
				"w5_query", "w5_grid", "w5_grid_column", "w5_vcs_object", "w5_vcs_commit", "w5_project_task",
				"w5_project_invitation", "w5_project_related_project" };
		List params = new ArrayList();
		params.add(delProjectId);
		for (int qi = 0; qi < tables.length; qi++)
			executeUpdateSQLQuery("delete from iwb." + tables[qi] + " where project_uuid=?", params);

		executeUpdateSQLQuery("delete from iwb.w5_user_related_project where related_project_uuid=?", params);
	}


	public void checkTenant(Map<String, Object> scd) {
		W5Project po = FrameworkCache.getProject(scd);
		if (po != null)
			executeUpdateSQLQuery("set search_path=" + po.getRdbmsSchema());
	}
	

	public void reloadUsersCache(String projectId) { // customizationID ??
		List<Object[]> l = (List<Object[]>) executeSQLQuery(
				"select x.customization_id, x.user_id, x.user_name, x.dsc, 1 allow_multi_login_flag, x.profile_picture_id from iwb.w5_user x "
						+ (projectId != null ? (" where x.project_uuid='" + projectId + "'") : ""));
		if (l != null)
			for (Object[] m : l) {
				UserUtil.addUserWithProfilePicutre(GenericUtil.uInt(m[1]), (String) m[2], (String) m[3],
						GenericUtil.uInt(m[4]) != 0, GenericUtil.uInt(m[5]));
			}
	}

	public void reloadUsersCache(int customizationId) { // customizationID ??
		List<Object[]> l = (List<Object[]>) executeSQLQuery(
				"select x.customization_id, x.user_id, x.user_name, x.dsc, 1 allow_multi_login_flag, x.profile_picture_id from iwb.w5_user x "
						+ (customizationId >= 0 ? (" where x.customization_id=" + customizationId + "") : ""));
		if (l != null)
			for (Object[] m : l) {
				UserUtil.addUserWithProfilePicutre(GenericUtil.uInt(m[1]), (String) m[2], (String) m[3],
						GenericUtil.uInt(m[4]) != 0, GenericUtil.uInt(m[5]));
			}
	}
}
