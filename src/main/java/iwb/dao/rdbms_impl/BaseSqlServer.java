package iwb.dao.rdbms_impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.jdbc.ReturningWork;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

import iwb.exception.PromisException;
import iwb.util.FrameworkSetting;
import iwb.util.GenericUtil;
/**
 * This class serves as the Base class for all other DAOs - namely to hold
 * common methods that they might all use. Can be used for standard CRUD
 * operations.</p>
 *
 * <p><a href="BaseDAOHibernate.java.html"><i>View Source</i></a></p>
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class BaseSqlServer extends HibernateDaoSupport {
	public String sqlPostgre2sqlServer(String s){
		if(s==null || s.length()==0 || (s.indexOf("::")==-1 && s.indexOf("||")==-1 && s.indexOf("fnc_")==-1))return s;
		StringBuilder tmp = new StringBuilder(s);
		for(int i=tmp.indexOf("::",0);i!=-1 && i+5<=tmp.length();i=tmp.indexOf("::",i+1)){
			String qq = tmp.substring(i+2, i+5);
			int j=-1;
			if(qq.equals("int")){
				if(i+5+4<=tmp.length() && tmp.substring(i+2, i+5+4).equals("integer")) j=7;
				else j=3;
			} else if(qq.equals("tex")){
				if(i+5+1<=tmp.length() && tmp.substring(i+2, i+5+1).equals("text")) j=4;
			} else if(qq.equals("dat")){
				if(i+5+1<=tmp.length() && tmp.substring(i+2, i+5+1).equals("date")) j=4;
			} else if(qq.equals("flo")){
				if(i+5+2<=tmp.length() && tmp.substring(i+2, i+5+2).equals("float")) j=5;
			} else if(qq.equals("numeric")){
				if(i+5+4<=tmp.length() && tmp.substring(i+2, i+5+4).equals("numeric")) j=7;
			}
			if(j!=-1)
				tmp.replace(i, i+2+j, "");			
		}
		
		String[] cs= new String[] {"fnc_locale_msg","fnc_lookup_locale","fnc_look_up_detay","fnc_sysdate"};
		for(int ji=0;ji<cs.length;ji++)for(int i=tmp.indexOf(cs[ji],0);i!=-1 && i+5<=tmp.length();i=tmp.indexOf(cs[ji],i+1)){
			if(i==0 || tmp.charAt(i-1)!='.')tmp.replace(i, i, "dbo.");			
		}

		for(int i=tmp.indexOf("||",0);i!=-1;i=tmp.indexOf("||",i)){
			tmp.replace(i, i+2, "+");			
		}

		return tmp.toString();
	}
    /**
     * @see org.appfuse.dao.DAO#saveObject(java.lang.Object)
     */
    public void saveObject(Object o) {
        getHibernateTemplate().persist(o);
    }

    public void updateObject(Object o) {
        getHibernateTemplate().update(o);
    }

    /**
     * @see org.appfuse.dao.DAO#getObject(java.lang.Class, java.io.Serializable)
     */
    @SuppressWarnings("unchecked")
	public<T> T loadObject(Class<T> clazz, Serializable id) {
        return (T)getHibernateTemplate().get(clazz, id);
    }
    
    /**
     * @see org.appfuse.dao.DAO#getObjects(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
	public<T> List<T> listObjects(Class<T> clazz) {
        return getHibernateTemplate().loadAll(clazz);
    }

    /**
     * @see org.appfuse.dao.DAO#removeObject(java.lang.Class, java.io.Serializable)
     */
    @SuppressWarnings({ "unchecked"})
	public void removeObject(Class clazz, Serializable id) {
        getHibernateTemplate().delete(loadObject(clazz, id));
    }
    
    public void removeObject(Object obj) {
        getHibernateTemplate().delete(obj);
    }
    
    public void removeAllObjects(List<?> l){
        getHibernateTemplate().deleteAll(l);
    }
    
    public void saveOrUpdateAllObjects(List<?> l) {
		HibernateTemplate t = getHibernateTemplate();
		for (Object o : l) {
			t.saveOrUpdate(o);
		}
	}
    
	protected void applyParameters(PreparedStatement s, Object... sqlParams) throws SQLException {
		if ((sqlParams==null)||(sqlParams.length==0))
			return;
		int i = 1;
    	for(Object o : sqlParams){
    		if(o==null)s.setObject(i, null);
    		else if(o instanceof Integer)s.setInt(i, ((Integer)o).intValue());
			else if(o instanceof Long)s.setLong(i, ((Long)o).longValue());
			else if(o instanceof Short)s.setShort(i, ((Short)o).shortValue());
			else if(o instanceof java.sql.Timestamp)s.setTimestamp(i, (java.sql.Timestamp)o);
			else if(o instanceof java.sql.Date)s.setDate(i, (java.sql.Date)o);
			else if(o instanceof java.util.Date)s.setTimestamp(i, new java.sql.Timestamp(((java.util.Date)o).getTime()));
			else if(o instanceof BigDecimal)s.setBigDecimal(i, (BigDecimal)o);
			else if(o instanceof String)s.setString(i, (String)o);
			else if(o instanceof Float)s.setFloat(i, (Float)o);
			else if(o instanceof Double)s.setDouble(i, ((Double)o).doubleValue());
    		i++;
    	}	
	}    
	protected void applyParameters(PreparedStatement s, List<Object> sqlParams) throws SQLException {
		if ((sqlParams==null)||(sqlParams.size()==0))
			return;
		int i = 1;
    	for(Object o : sqlParams){
    		if(o==null)s.setObject(i, null);
    		else if(o instanceof Integer)s.setInt(i, ((Integer)o).intValue());
			else if(o instanceof Long)s.setLong(i, ((Long)o).longValue());
			else if(o instanceof Short)s.setShort(i, ((Short)o).shortValue());
			else if(o instanceof java.sql.Timestamp)s.setTimestamp(i, (java.sql.Timestamp)o);
			else if(o instanceof java.sql.Date)
				{
				s.setDate(i, (java.sql.Date)o);
				}
			else if(o instanceof java.util.Date)s.setTimestamp(i, new java.sql.Timestamp(((java.util.Date)o).getTime()));
			else if(o instanceof BigDecimal)s.setBigDecimal(i, (BigDecimal)o);
			else if(o instanceof String)s.setString(i, (String)o);
			else if(o instanceof Float)s.setFloat(i, (Float)o);
			else if(o instanceof Double)s.setDouble(i, ((Double)o).doubleValue());
    		i++;
    	}	
	}
	
	protected Query applyParameters(Query query, List<Object> sqlParams) {
    	if (sqlParams==null)
    		return query;
		int i=0;
		for(Object o : sqlParams){
    		if(o instanceof Integer)
    			query=query.setInteger(i, ((Integer)o).intValue());
			else if(o instanceof Long)
				query=query.setLong(i, ((Long)o).longValue());
			else if(o instanceof Short)
				query=query.setShort(i, ((Short)o).shortValue());
			else if(o instanceof Date)
				query=query.setDate(i, new java.sql.Date(((Date)o).getTime()));
			else if(o instanceof BigDecimal)
				query=query.setBigDecimal(i, (BigDecimal)o);
			else if(o instanceof String)
				query=query.setString(i, (String)o);
			else if(o instanceof Float)
				query=query.setFloat(i, (Float)o);
			else if(o instanceof Double)
				query=query.setDouble(i, ((Double)o).doubleValue());
    		i++;
    	}
		return query;		
	}
/*	
	protected Query applyParameters(Query query, Object[] sqlParams) {
		if (sqlParams==null)
    		return query;
		int i=0;
		for(Object o : sqlParams){
    		if(o instanceof Integer)
    			query=query.setInteger(i, ((Integer)o).intValue());
			else if(o instanceof Long)
				query=query.setLong(i, ((Long)o).longValue());
			else if(o instanceof Short)
				query=query.setShort(i, ((Short)o).shortValue());
			else if(o instanceof Date)
				query=query.setDate(i, new java.sql.Date(((Date)o).getTime()));
			else if(o instanceof BigDecimal)
				query=query.setBigDecimal(i, (BigDecimal)o);
			else if(o instanceof String)
				query=query.setString(i, (String)o);
			else if(o instanceof Float)
				query=query.setFloat(i, (Float)o);
			else if(o instanceof Double)
				query=query.setDouble(i, ((Double)o).doubleValue());
    		i++;
    	}
		return query;		
	}
*/
	
	protected SQLQuery applyParameters2(SQLQuery query, List<Object> sqlParams) {
		if (sqlParams==null)
    		return query;
		int i=0;
		for(Object o : sqlParams){
    		if(o instanceof Integer)
    			query.setInteger(i, ((Integer)o).intValue());
			else if(o instanceof Long)
				query.setLong(i, ((Long)o).longValue());
			else if(o instanceof Short)
				query.setShort(i, ((Short)o).shortValue());
			else if(o instanceof Date)
				query.setDate(i, new java.sql.Date(((Date)o).getTime()));
			else if(o instanceof BigDecimal)
				query.setBigDecimal(i, (BigDecimal)o);
			else if(o instanceof String)
				query.setString(i, (String)o);
			else if(o instanceof Float)
				query.setFloat(i, (Float)o);
			else if(o instanceof Double)
				query.setDouble(i, ((Double)o).doubleValue());
    		i++;
    	}
		return query;		
	}	
	
	protected SQLQuery applyParameters2(SQLQuery query, Object... sqlParams) {
		if (sqlParams==null)
    		return query;
		int i=0;
		for(Object o : sqlParams){
    		if(o instanceof Integer)
    			query.setInteger(i, ((Integer)o).intValue());
			else if(o instanceof Long)
				query.setLong(i, ((Long)o).longValue());
			else if(o instanceof Short)
				query.setShort(i, ((Short)o).shortValue());
			else if(o instanceof Date)
				query.setDate(i, new java.sql.Date(((Date)o).getTime()));
			else if(o instanceof BigDecimal)
				query.setBigDecimal(i, (BigDecimal)o);
			else if(o instanceof String)
				query.setString(i, (String)o);
			else if(o instanceof Float)
				query.setFloat(i, (Float)o);
			else if(o instanceof Double)
				query.setDouble(i, ((Double)o).doubleValue());
    		i++;
    	}
		return query;		
	}

	
	@SuppressWarnings("unchecked")
	public List find(String query,Object... params){
    	return getHibernateTemplate().find(query,params);
    }

/*	public List executeSQLQuery2(String sql,Object... params) {
		Session session = getSession();
		List l = null;
		try {
			SQLQuery query= session.createSQLQuery(sql);
			if(params!=null && params.length>0)applyParameters2(query, params);
			l = query.list();
		} catch(Exception e){
			if(PromisSetting.debug)e.printStackTrace();
			throw new PromisException("sql","Manuel Query Execute2List",0,sql, e.getMessage(), e.getCause());
		} finally {
//			session.close();
		}
		return l;
	} *//*
	public List executeSQLQuery(final String sql,final Object... params) {
		try {
	    	return (List)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws SQLException {
	        			Connection conn = session.connection();
	        			PreparedStatement s = conn.prepareStatement(sql);
	        			
	        			if(params!=null && params.length>0)for(int i=0;i<params.length;i++){
	        				if(params[i]==null) s.setObject(i+1, null);
	    					else if(params[i] instanceof Date)s.setDate(i+1, new java.sql.Date(((Date)params[i]).getTime()));
	    					else s.setObject(i+1, params[i]);
	        			}
	        			
	        			
	        			ResultSet rs = s.executeQuery();
	        			int columnCount = rs.getMetaData().getColumnCount();
	        			List l = new ArrayList();
	        			while(rs.next()){
	        				if(columnCount==1)
	        					l.add(rs.getObject(1));
	        				else {
	        					Object[] oz = new Object[columnCount];
	        					for(int i=0;i<columnCount;i++)
	        						oz[i] = rs.getObject(i+1);
	        					l.add(oz);
	        				}
	        			}
	        			rs.close();
	        			s.close();
	        			conn.close();
	        			return l.size()==0 ? null : l;
	                }
	            }
	    	);
		} catch(Exception e){
			if(PromisSetting.debug)e.printStackTrace();
			throw new PromisException("sql","Manuel Query Execute2List",0,sql, e.getMessage(), e.getCause());
		}
	}
	
*/
	public List executeSQLQuery(final String sql,final Object... params) {
		try{
			return getSessionFactory().getCurrentSession().doReturningWork(new ReturningWork<List>() {
				@Override
				public List execute(Connection conn) throws SQLException {
					PreparedStatement s = conn.prepareStatement(sqlPostgre2sqlServer(sql));
	    			
	    			if(params!=null && params.length>0)for(int i=0;i<params.length;i++){
	    				if(params[i]==null) s.setObject(i+1, null);
						else if(params[i] instanceof Date)s.setDate(i+1, new java.sql.Date(((Date)params[i]).getTime()));
						else s.setObject(i+1, params[i]);
	    			}
	    			
	    			
	    			ResultSet rs = s.executeQuery();
	    			int columnCount = rs.getMetaData().getColumnCount();
	    			List l = new ArrayList();
	    			while(rs.next()){
	    				if(columnCount==1)
	    					l.add(rs.getObject(1));
	    				else {
	    					Object[] oz = new Object[columnCount];
	    					for(int i=0;i<columnCount;i++)
	    						oz[i] = rs.getObject(i+1);
	    					l.add(oz);
	    				}
	    			}
	    			rs.close();
	    			s.close();
	    			if(FrameworkSetting.hibernateCloseAfterWork)conn.close();
	    			return  l.size()==0 ? null : l;
	            }
	        
			});	
		} catch(Exception e){
			if(FrameworkSetting.debug)e.printStackTrace();
			throw new PromisException("sql","Manuel Query Execute2List",0,sql, e.getMessage(), e.getCause());
		}
	}
	public List executeSQLQuery2(final String sql,final List params) {
		try {
			return getSessionFactory().getCurrentSession().doReturningWork(new ReturningWork<List>() {
				@Override
				public List execute(Connection conn) throws SQLException {
	        			PreparedStatement s = conn.prepareStatement(sqlPostgre2sqlServer(sql));
	        			
	        			if(params!=null && params.size()>0)for(int i=0;i<params.size();i++){
	        				if(params.get(i)==null) s.setObject(i+1, null);
	    					else if(params.get(i) instanceof Date)s.setDate(i+1, new java.sql.Date(((Date)params.get(i)).getTime()));
	    					else s.setObject(i+1, params.get(i));
	        			}
	        			
	        			
	        			ResultSet rs = s.executeQuery();
	        			int columnCount = rs.getMetaData().getColumnCount();
	        			List l = new ArrayList();
	        			while(rs.next()){
	        				if(columnCount==1)
	        					l.add(rs.getObject(1));
	        				else {
	        					Object[] oz = new Object[columnCount];
	        					for(int i=0;i<columnCount;i++)
	        						oz[i] = rs.getObject(i+1);
	        					l.add(oz);
	        				}
	        			}
	        			rs.close();
	        			s.close();
	        			if(FrameworkSetting.hibernateCloseAfterWork)conn.close();
	        			return l.size()==0 ? null : l;
	                }
	            }
	    	);
		} catch(Exception e){
			if(FrameworkSetting.debug)e.printStackTrace();
			throw new PromisException("sql","Manuel Query Execute2List",0,sql, e.getMessage(), e.getCause());
		}
	}
	public List executeSQLQuery2Map(final String sql,final List params) {
		try {
			return getSessionFactory().getCurrentSession().doReturningWork(new ReturningWork<List>() {
				@Override
				public List execute(Connection conn) throws SQLException {
	        			PreparedStatement s = conn.prepareStatement(sqlPostgre2sqlServer(sql));
	        			if(params!=null && params.size()>0)for(int i=0;i<params.size();i++){
	        				if(params.get(i)==null) s.setObject(i+1, null);
	    					else if(params.get(i) instanceof Date)s.setDate(i+1, new java.sql.Date(((Date)params.get(i)).getTime()));
	    					else s.setObject(i+1, params.get(i));
	        			}
	        			
	        			
	        			ResultSet rs = s.executeQuery();
	        			int columnCount = rs.getMetaData().getColumnCount();
	        			List l = new ArrayList();
	        			Map<String, Object>	result = null;
    					ResultSetMetaData rsm = null;
	    					
	        			while(rs.next()){
	        				if(rsm==null)rsm=rs.getMetaData();
	        				result = new HashMap<String, Object>();
	    					for(int columnIndex=1;columnIndex<=rsm.getColumnCount();columnIndex++){
	    						String columnName = rsm.getColumnName(columnIndex).toLowerCase(FrameworkSetting.appLocale);
	    						Object obj =rs.getObject(columnIndex);
	    						if(obj==null)continue;
							//	if (obj instanceof oracle.sql.TIMESTAMP) {
							//		try{ result.put(columnName, PromisUtil.uFormatDateTime(((oracle.sql.TIMESTAMP) obj).timestampValue()));
							//		}catch (Exception e) {result.put(columnName,"oracle.sql.TIMESTAMP");}
							//	} else 
	    						if (obj instanceof java.sql.Timestamp) {
	    	        				try{ result.put(columnName, GenericUtil.uFormatDateTimeSade((java.sql.Timestamp) obj));
	    	        				}catch (Exception e) {}
	    						} else if (obj instanceof java.sql.Date) {
	    	        				try{ result.put(columnName, GenericUtil.uFormatDateSade((java.sql.Date) obj));
	    	        				}catch (Exception e) {}
	    						} //else if (obj instanceof oracle.sql.CLOB) {
	    							//oracle.sql.CLOB new_obj = (oracle.sql.CLOB) obj;
	    	        				//try{ result.put(columnName, new_obj.getSubString((int)1,(int)new_obj.length()));
	    	        				//}catch (Exception e) {}
	    					//	}
	    	        			 else result.put(columnName, obj.toString());
	    					}
        					l.add(result);
	        			}
	        			rs.close();
	        			s.close();
	        			if(FrameworkSetting.hibernateCloseAfterWork)conn.close();
	        			return l.size()==0 ? null : l;
	                }
	            }
	    	);
		} catch(Exception e){
			if(FrameworkSetting.debug)e.printStackTrace();
			throw new PromisException("sql","Manuel Query Execute2ListOfMap",0,GenericUtil.replaceSql(sql,params), e.getMessage(), e.getCause());
		}
	}
	/*
	public int executeUpdateSQLQuery2(String sql,Object... params) {
		Session session = getSession();
		try {
			SQLQuery query= session.createSQLQuery(sql);
			if(params!=null && params.length>0)applyParameters2(query, params);
			int result = query.executeUpdate();
			return result;
			
		} catch(Exception e){
			if(PromisSetting.debug)e.printStackTrace();
			throw new PromisException("sql","Manuel Query Execute Update",0,sql, e.getMessage(), e.getCause());
		} 
	}
*/
	public int executeUpdateSQLQuery(final String sql,final Object... params) {
		try{
			return getSessionFactory().getCurrentSession().doReturningWork(new ReturningWork<Integer>() {
				@Override
				public Integer execute(Connection conn) throws SQLException {
	        			PreparedStatement s = conn.prepareStatement(sqlPostgre2sqlServer(sql));
	        			if(params!=null && params.length>0){
	        				if(params.length==1 && params[0] instanceof List){
	        					List l = (List)params[0];
	        					int i=0;
	        					for(Object p:l){
			        				if(p==null) s.setObject(i+1, null);
			    					else if(p instanceof Date)s.setDate(i+1, new java.sql.Date(((Date)p).getTime()));
			    					else if(p instanceof Double)s.setDouble(i+1, Double.valueOf(p.toString()));
			    					else if(p instanceof Integer)s.setInt(i+1, Integer.valueOf(p.toString()));
			    					else s.setObject(i+1, p);
			        				i++;
			        			}
	        				} else {
	        					for(int i=0;i<params.length;i++){
			        				if(params[i]==null) s.setObject(i+1, null);
			    					else if(params[i] instanceof Date)s.setDate(i+1, new java.sql.Date(((Date)params[i]).getTime()));
			    					else if(params[i] instanceof Double)s.setDouble(i+1, Double.valueOf(params[i].toString()));
			    					else if(params[i] instanceof Integer)s.setInt(i+1, Integer.valueOf(params[i].toString()));
			    					else s.setObject(i+1, params[i]);
			        			}
	        				}
	        			}
	        			try{
	        				s.execute();
		        			s.close();
		        			if(FrameworkSetting.hibernateCloseAfterWork)conn.close();
	        			}
	        			catch (Exception e) {
	        				if(FrameworkSetting.debug)e.printStackTrace();
	        				throw new PromisException("sql","Manuel Query Execute Update",0,sql, e.getMessage(), e.getCause());
						}
	        			
	        			return 1;
	                }
	            }
	    	);
		} catch(Exception e){
				if(FrameworkSetting.debug)e.printStackTrace();
				throw new PromisException("sql","Manuel Query Execute Update",0,sql, e.getMessage(), e.getCause());
		}
	}
	/*
	public int executeSQLWithoutTransaction(final String sql,final Object... params){	// Transaction disable hale getiriliyor..
		try{
	    	getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws SQLException {
	        			Connection conn = session.connection();
	        			conn.rollback(); //**** daha oncekiler roolback yapliyor.
	        			PreparedStatement s = conn.prepareStatement(sql);
	        			if(params!=null && params.length>0)for(int i=0;i<params.length;i++){
	        				if(params[i]==null) s.setObject(i+1, null);
	    					else if(params[i] instanceof Date)s.setDate(i+1, new java.sql.Date(((Date)params[i]).getTime()));
	    					else if(params[i] instanceof Double)s.setDouble(i+1, Double.valueOf(params[i].toString()));
	    					else if(params[i] instanceof Integer)s.setInt(i+1, Integer.valueOf(params[i].toString()));
	    					else s.setObject(i+1, params[i]);
	        			}
	        			try{
	        				s.execute();
	        			    conn.commit(); //** sadece bu sql calistiriliyor.
		        			s.close();
		        			conn.close();
	        			}
	        			catch (Exception e) {
	        				if(PromisSetting.debug)e.printStackTrace();
	        				throw new PromisException("sql","Manuel Query Execute Update",0,sql, e.getMessage(), e.getCause());
						}
	        			
	        			return 1;
	                }
	            }
	    	);
		} catch(Exception e){
				if(PromisSetting.debug)e.printStackTrace();
				throw new PromisException("sql","Manuel Query Execute Update",0,sql, e.getMessage(), e.getCause());
		}
		return 1;
	} */
}
