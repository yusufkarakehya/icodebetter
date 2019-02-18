package iwb.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import iwb.cache.FrameworkCache;
import iwb.cache.FrameworkSetting;
import iwb.dao.rdbms_impl.MetadataLoaderDAO;
import iwb.dao.rdbms_impl.PostgreSQL;
import iwb.domain.db.W5Table;
import iwb.domain.result.W5GlobalFuncResult;
import iwb.domain.result.W5QueryResult;
import iwb.exception.IWBException;
import iwb.util.DBUtil;
import iwb.util.GenericUtil;

@Component
public class DebugEngine {
	@Lazy
	@Autowired
	private PostgreSQL dao;
	
	@Lazy
	@Autowired
	private MetadataLoaderDAO metaDataDao;


	@Lazy
	@Autowired
	private GlobalScriptEngine scriptEngine;
	
	public Object executeQuery4Debug(Map<String, Object> scd, int queryId, Map<String, String> requestParams) {
		W5QueryResult queryResult = queryId == -1 ? new W5QueryResult(-1) : metaDataDao.getQueryResult(scd, queryId);

		queryResult.setErrorMap(new HashMap());
		queryResult.setScd(scd);
		queryResult.setRequestParams(requestParams);
		if (queryId == -1 || queryResult.getQuery().getQuerySourceTip() != 0) {
			String orderBy = requestParams.get("sort");
			if (GenericUtil.isEmpty(orderBy))
				orderBy = requestParams.get("_sql_orderby");
			else if (requestParams.containsKey("dir"))
				orderBy += " " + requestParams.get("dir");
			queryResult.prepareQuery4Debug(requestParams.get("_sql_select"), requestParams.get("_sql_from"),
					requestParams.get("_sql_where"), requestParams.get("_sql_groupby"), orderBy);

			if (queryResult.getErrorMap().isEmpty()) {
				queryResult.setFetchRowCount(20);
				queryResult.setStartRowNumber(0);
				String sqlFrom2 = requestParams.get("_sql_from").toLowerCase();
				W5Table t = null;
				if (!sqlFrom2.contains("select") && !sqlFrom2.contains(",") && !sqlFrom2.contains("left")
						&& !sqlFrom2.contains("(")) {
					String[] ss = sqlFrom2.split(" ");
					if (ss.length < 3) {
						List l = dao.find("select t.tableId from W5Table t where t.projectUuid=? AND t.dsc=?",
								scd.get("projectId"), ss[0]);
						if (!l.isEmpty())
							t = FrameworkCache.getTable(scd, (Integer) l.get(0));
					}
				}
				if (FrameworkSetting.cloud && (Integer) scd.get("customizationId") > 0
						&& DBUtil.checkTenantSQLSecurity(queryResult.getExecutedSql())) {
					throw new IWBException("security", "SQL", 0, null,
							"Forbidden Command. Please contact iCodeBetter team ;)", null);
				}

				dao.checkTenant(scd);
				return dao.executeSQLQuery2Map4Debug(scd, t, queryResult.getExecutedSql(), queryResult.getSqlParams(),
						GenericUtil.uIntNvl(requestParams, "limit", 50),
						GenericUtil.uIntNvl(requestParams, "start", 0));
				// if(queryResult.getData()==null)queryResult.setData(new
				// ArrayList());
			}
		} else { // rhino
			return scriptEngine.executeQueryAsScript4Debug(queryResult, requestParams.get("_sql_from"));
		}

		return queryResult;
	}

	public W5GlobalFuncResult executeGlobalFunc4Debug(Map<String, Object> scd, int dbFuncId,
			Map<String, String> parameterMap) {
		return scriptEngine.executeGlobalFunc4Debug(scd, dbFuncId, parameterMap);
	}
}
