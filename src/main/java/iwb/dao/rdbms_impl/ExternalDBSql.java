package iwb.dao.rdbms_impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import iwb.cache.FrameworkCache;
import iwb.cache.FrameworkSetting;
import iwb.cache.LocaleMsgCache;
import iwb.custom.trigger.QueryTrigger;
import iwb.domain.db.W5ExternalDb;
import iwb.domain.db.W5Query;
import iwb.domain.db.W5QueryField;
import iwb.domain.db.W5QueryFieldCreation;
import iwb.domain.db.W5Table;
import iwb.domain.db.W5TableField;
import iwb.domain.result.W5QueryResult;
import iwb.exception.IWBException;
import iwb.util.DBUtil;
import iwb.util.GenericUtil;

@SuppressWarnings({ "unchecked", "unused" })
@Component
public class ExternalDBSql {

	protected void applyParameters(PreparedStatement s, List<Object> sqlParams) throws SQLException {
		if ((sqlParams == null) || (sqlParams.isEmpty())) {
			return;
		}
		applyParameters(s, sqlParams.toArray());
	}

	protected void applyParameters(PreparedStatement s, Object... sqlParams) throws SQLException {
		if ((sqlParams == null) || (sqlParams.length == 0)) {
			return;
		}
		int i = 1;
		for (Object o : sqlParams) {
			if (o == null) {
				s.setObject(i, null);
			} else if (o instanceof Integer) {
				s.setInt(i, ((Integer) o));
			} else if (o instanceof Long) {
				s.setLong(i, ((Long) o));
			} else if (o instanceof Short) {
				s.setShort(i, ((Short) o));
			} else if (o instanceof java.sql.Timestamp) {
				s.setTimestamp(i, (java.sql.Timestamp) o);
			} else if (o instanceof java.sql.Date) {
				s.setDate(i, (java.sql.Date) o);
			} else if (o instanceof java.util.Date) {
				s.setTimestamp(i, new java.sql.Timestamp(((java.util.Date) o).getTime()));
			} else if (o instanceof BigDecimal) {
				s.setBigDecimal(i, (BigDecimal) o);
			} else if (o instanceof String) {
				s.setString(i, (String) o);
			} else if (o instanceof Float) {
				s.setFloat(i, (Float) o);
			} else if (o instanceof Double) {
				s.setDouble(i, ((Double) o));
			} else if (o instanceof Boolean) {
				s.setBoolean(i, ((Boolean) o));
			}
			i++;
		}
	}

	public void runQuery(W5QueryResult queryResult) {
		String projectId = (String) queryResult.getScd().get("projectId");
		W5Query q = queryResult.getQuery();
		W5ExternalDb edb = FrameworkCache.wExternalDbs.get(projectId).get(q.getExternalDbId());
		Connection con = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		StringBuilder sql2 = new StringBuilder();
		try {
			con = edb.getConnection();
			if (queryResult.getFetchRowCount() != 0) {
				String countSQL = "select count(1) from (" + queryResult.getExecutedSql() + " ) x";
				s = con.prepareStatement(countSQL);
				applyParameters(s, queryResult.getSqlParams());

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


				switch (edb.getLkpDbType()) {
				case 2:// postgre

					sql2.append("select z.* from (").append(queryResult.getExecutedSql()).append(" limit ?");
					if (queryResult.getStartRowNumber() > 0) {
						sql2.append(" offset ?");
					}
					sql2.append(") z");
					break;
				case	1://oracle
					sql2.append("select r.* from (select z.*, rownum rx_rx_rx_qwq from (").append(queryResult.getExecutedSql()).append(") z) r where rownum<=?");
					if (queryResult.getStartRowNumber() > 0) {
						sql2.append(" AND rx_rx_rx_qwq>?");
					}
					break;
				case	3://ms sql server ROW_NUMBER() OVER ( ORDER BY OrderDate ) AS RowNum
					sql2.append("select r.* from (select z.*, ROW_NUMBER() OVER ( ORDER BY null ) AS rx_rx_rx_qwq from (").append(queryResult.getExecutedSql()).append(") z) r where rx_rx_rx_qwq<=?");
					if (queryResult.getStartRowNumber() > 0) {
						sql2.append(" AND rx_rx_rx_qwq>?");
					}
				}

			} else {
				sql2.append(queryResult.getExecutedSql());
			}

			List<Object[]> resultData = queryResult.getFetchRowCount() == 0 ? new ArrayList<Object[]>()
					: new ArrayList<Object[]>(queryResult.getFetchRowCount());
			// sql = sql2.toString();
			s = con.prepareStatement(sql2.toString());
			applyParameters(s, queryResult.getSqlParams());
			queryResult.setExecutedSql(sql2.toString());
			//
			// if(PromisSetting.debug)logger.info(PromisUtil.replaceSql(sql2.toString(),queryResult.getSqlParams()));
			rs = s.executeQuery();
			int maxTabOrder = 0;

			List<W5QueryField> newQueryFields = null;
			while (rs.next() /*
								 * && (maxFetchedCount==0 || totalFetchedCount<maxFetchedCount )
								 */) {
				if (newQueryFields == null) {
					newQueryFields = new ArrayList(queryResult.getQuery().get_queryFields().size()
							+ (queryResult.getPostProcessQueryFields() != null
									? queryResult.getPostProcessQueryFields().size()
									: 0));
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
								W5TableField tf = queryResult.getMainTable() != null && qf.getMainTableFieldId() > 0
										? queryResult.getMainTable().get_tableFieldMap().get(qf.getMainTableFieldId())
										: null;
								if (tf == null || ((GenericUtil.isEmpty(tf.getRelatedSessionField())
										|| GenericUtil.uInt(queryResult.getScd().get(tf.getRelatedSessionField())) != 0)
										&& (tf.getAccessViewUserFields() != null || GenericUtil.accessControl(
												queryResult.getScd(), tf.getAccessViewTip(), tf.getAccessViewRoles(),
												tf.getAccessViewUsers())))) { // access
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
				for (W5QueryField field : newQueryFields) {
					Object obj = rs.getObject(field.getDsc());
					if (obj != null) {
						if (obj instanceof java.sql.Timestamp) {
							try {
								obj = (queryResult.getQuery().getQueryTip() == 2 && field.getFieldTip() == 2)
										? (java.sql.Timestamp) obj
										: GenericUtil.uFormatDateTime((java.sql.Timestamp) obj);
							} catch (Exception e) {
								obj = "java.sql.Timestamp";
							}
						} else if (obj instanceof java.sql.Date) {
							try {
								obj = (queryResult.getQuery().getQueryTip() == 2 && field.getFieldTip() == 2)
										? rs.getTimestamp(field.getDsc())
										: GenericUtil.uFormatDateTime(rs.getTimestamp(field.getDsc()));
							} catch (Exception e) {
								obj = "java.sql.Date";
							}
						} else if (obj instanceof Boolean) {
							obj = (Boolean) obj ? 1 : 0;
						}
					}
					o[field.getTabOrder() - 1] = obj;
				}
				resultData.add(o);
			}
			if (queryResult.getFetchRowCount() == 0 && resultData != null) {
				queryResult.setResultRowCount(resultData.size());
			}
			queryResult.setData(resultData);

			if (rs != null)
				rs.close();
			if (s != null)
				s.close();
			if (con != null)
				con.close();

		} catch (Exception e) {
//			error = e.getMessage();
			throw new IWBException("sql", "External.Query", queryResult.getQueryId(),
					GenericUtil.replaceSql(sql2.length() == 0 ? queryResult.getExecutedSql() : sql2.toString(),
							queryResult.getSqlParams()),
					"[8," + queryResult.getQueryId() + "] " + queryResult.getQuery().getDsc(), e);
		}
	}

	public ExternalDBSql() {
		if(!FrameworkSetting.externalDb)return;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.out.println("Oracle Driver not found");
		}
		if(false)try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e) {
			System.out.println("SQL Server Driver not found");
		}
	}

	public void organizeQueryFields(Map<String, Object> scd, W5Query query, String sqlStr, List<Object> sqlParams,
			Map<String, W5QueryFieldCreation> existField, List<W5QueryFieldCreation> updateList,
			List<W5QueryFieldCreation> insertList) {
		String projectId = (String) scd.get("projectId");
		int userId = (Integer) scd.get("userId");
		W5ExternalDb edb = FrameworkCache.wExternalDbs.get(projectId).get(query.getExternalDbId());
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		StringBuilder sql2 = new StringBuilder();
		try {
			con = edb.getConnection();
			stmt = con.prepareStatement(sqlStr);
			if (sqlParams.size() > 0)
				applyParameters(stmt, sqlParams);
			rs = stmt.executeQuery();
			ResultSetMetaData meta = rs.getMetaData();

			int columnNumber = meta.getColumnCount();
			for (int i = 1, j = 0; i <= columnNumber; i++) {
				String columnName = meta.getColumnName(i).toLowerCase(FrameworkSetting.appLocale);
				if (existField.get(columnName) == null) { // eger
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

					field.setQueryFieldId(
							GenericUtil.getGlobalNextval("iwb.seq_query_field", (String) scd.get("projectId"),
									(Integer) scd.get("userId"), (Integer) scd.get("customizationId")));
					insertList.add(field);
					j++;
				} else if (existField.get(columnName) != null && (existField.get(columnName).getTabOrder() != i)) {
					W5QueryFieldCreation field = existField.get(columnName);
					field.setTabOrder((short) (i));
					field.setVersionUserId(userId);
					field.setVersionDttm(new java.sql.Timestamp(new java.util.Date().getTime()));
					updateList.add(field);
				}
				existField.remove(columnName);
			}
			rs.close();
			stmt.close();
			if (FrameworkSetting.hibernateCloseAfterWork)
				con.close();
		} catch (Exception e) {
//			error = e.getMessage();
			throw new IWBException("sql", "External.Query", query.getQueryId(),
					GenericUtil.replaceSql(sqlStr, sqlParams), "[8," + query.getQueryId() + "] " + query.getDsc(), e);
		}

	}

}
