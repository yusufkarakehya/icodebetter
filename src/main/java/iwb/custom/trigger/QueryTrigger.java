package iwb.custom.trigger;

import iwb.dao.rdbms_impl.PostgreSQL;
import iwb.domain.result.W5QueryResult;

public class QueryTrigger {
	public static void beforeExecuteQuery(W5QueryResult queryResult, PostgreSQL dao){
		switch(queryResult.getQueryId()){
		
		}
	}
	public static void afterExecuteQuery(W5QueryResult queryResult, PostgreSQL dao){
		/* TODO
		switch(queryResult.getQueryId()){
		case	1727://comment listing
			dao.executeUpdateSQLQuery("{call PSYS_UPDATE_COMMENT_SUMMARY_VW(?,?,?,?) }", 
					queryResult.getScd().get("customizationId"), PromisUtil.uInt(queryResult.getRequestParams().get("xtable_id")), PromisUtil.uInt(queryResult.getRequestParams().get("xtable_pk")),queryResult.getScd().get("userId"));
			break;
		}*/
	}
}
