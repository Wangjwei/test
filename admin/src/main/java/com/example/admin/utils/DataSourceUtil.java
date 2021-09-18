package com.example.admin.utils;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;


@Slf4j
public class DataSourceUtil {
	
	private static final Map<String, DruidDataSource> dataSources = new HashMap<String, DruidDataSource>();

	public static DruidDataSource getDataSource(String dsid) {
		return dataSources.get(dsid);
	}
	
	public static void removeDataSource(String dsid) {
		DruidDataSource dataSource = getDataSource(dsid);
		if(dataSource != null)
		{
			dataSource.close();
			dataSources.remove(dsid);
			log.info("移除数据源"+dsid);
		}
	}
	
	private static DruidDataSource buildDataSource(String driverclassname,String url,String username,String password,Integer initialsize,Integer minIdle,Integer maxActive,Integer maxWait,String validationQuery)
	{
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(driverclassname);
		//设置数据库连接地址
		dataSource.setUrl(url);
		//设置用户名
		dataSource.setUsername(username);
		//设置密码
		dataSource.setPassword(password);
		
		//设置初始化大小,默认1
		dataSource.setInitialSize(initialsize == null?1:initialsize);
		//设置在数据库连接词中的最小连接数,默认1
		dataSource.setMinIdle(minIdle == null?1:minIdle);
		//设置最大连接数,默认20
		dataSource.setMaxActive(maxActive == null?20:maxActive);
		//设置获取连接的最大等待时间,默认6000
		dataSource.setMaxWait(maxWait == null?6000:maxWait);
		//测试连接语句,默认 SELECT 'X'
		dataSource.setValidationQuery(validationQuery == null || "".equals(validationQuery)?"SELECT 'X'":validationQuery);
		
		return dataSource;
	}

	public static void initDataSource(String dsid,String driverclassname,String url,String username,String password) throws Exception
	{
		//先移除
		removeDataSource(dsid);
		
		//添加
		DataSourceUtil.dataSources.put(dsid, buildDataSource(driverclassname,url,username,password,null,null,null,null,null));
		log.info("添加数据源"+dsid);
	}
	
	public static void initDataSource(String dsid,String driverclassname,String url,String username,String password,Integer initialsize,Integer minIdle,Integer maxActive,Integer maxWait,String validationQuery) throws Exception
	{
		//先移除
		removeDataSource(dsid);
		
		//添加
		DataSourceUtil.dataSources.put(dsid, buildDataSource(driverclassname, url, username, password, initialsize, minIdle, maxActive, maxWait, validationQuery));
		log.info("添加数据源"+dsid);
	}
	
	public static void testDataSource(String driverclassname,String url,String username,String password,Integer initialsize,Integer maxActive,Integer maxWait,String validationQuery) throws Exception
	{
		try {
//			Connection connection = buildDataSource(dataSource.getDriverclassname(), dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword(), dataSource.getInitialsize(), null, dataSource.getMaxactive(), dataSource.getMaxwait(), dataSource.getValidationquery()).getConnection();
			Connection connection = buildDataSource(driverclassname, url, username, password, initialsize, null, maxActive, maxWait, validationQuery).getConnection();
			getResultFirst(connection, validationQuery, null);
			close(connection);
		} catch (Exception e) {
			throw e;
		}
		
	}
	
	/**
	 * 获取tomcat中jndi配置数据源
	 * @param dataSourceName 数据源名称，例如：jdbc/MyDb
	 * @return
	 * @throws Exception
	 */
	public static Connection getTomcatJndiConnection(String dataSourceName) throws Exception
	{
		Connection conn=null;
		Context context=new InitialContext();
		//使用jndi技术，Tomcat往往会在name值前面加上"java:comp/env/"+"jdbc/MyDb"
		DataSource dataSource = (DataSource) context.lookup("java:comp/env/"+dataSourceName);
		conn=dataSource.getConnection();
		return conn;
	}
	
	
	/**
	 * 获取数据源链接
	 * @param ds DruidDataSource|druidDataSourceID 如果为druidDataSourceID需要初始化DruidDataSource
	 * @return
	 * @throws Exception
	 */
	
	public static Connection getConnection(Object ds) throws Exception
	{
		DruidDataSource dataSource = null;
		if(ds instanceof String){
			dataSource = getDataSource((String)ds);
		}else if(ds instanceof DruidDataSource){
			dataSource = (DruidDataSource)ds;
		}

		assert dataSource != null;
		return dataSource.getConnection();
	}
	
	/**
	 * 执行单条sql，且直接提交并关闭Connection，报错后回滚
	 * @param ds Connection|DruidDataSource|druidDataSourceID 如果为druidDataSourceID需要初始化DruidDataSource
	 * @param sql 执行的sql
	 * @param params 参数
	 * @return
	 * @throws Exception
	 */
	public static boolean execute(Connection conn,String sql,Object[] params) throws Exception
	{
		boolean isOk = false;
		PreparedStatement sta = null;
		try {
			conn.setAutoCommit(false);
			sta = conn.prepareStatement(sql);
			if(params != null && params.length > 0)
			{
				for(int i = 0;i<params.length;i++)
				{
					sta.setObject(i+1, params[i]);
				}
			}
			isOk = sta.execute();
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		}finally{
			close(sta,conn);
		}

		return isOk;
	}
	
	/**
	 * 执行单条sql，不关闭Connection，报错后回滚
	 * @param ds Connection|DruidDataSource|druidDataSourceID 如果为druidDataSourceID需要初始化DruidDataSource
	 * @param sql 执行的sql
	 * @param params 参数
	 * @param isCommit 是否直接提交
	 * @return
	 * @throws Exception
	 */
	public static boolean execute(Connection conn,String sql,Object[] params,boolean isCommit) throws Exception
	{
		boolean isOk = false;
		PreparedStatement sta = null;
		try {
			conn.setAutoCommit(false);
			sta = conn.prepareStatement(sql);
			if(params != null && params.length > 0)
			{
				for(int i = 0;i<params.length;i++)
				{
					sta.setObject(i+1, params[i]);
				}
			}
			isOk = sta.execute();
			if(isCommit)
			{
				conn.commit();
			}
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		}finally{
			close(sta);
		}

		return isOk;
	}
	
	/**
	 * 批量提交，不关闭，不提交Connection
	 * @param conn 数据源链接
	 * @param batchCount 一次性提交数据量
	 * @param sql 执行sql
	 * @param params 参数
	 * @return 提交数据量
	 * @throws Exception
	 */
	public static int executeBatch(Connection conn,int batchCount, String sql,List<List<Object>> params) throws Exception
	{
		int executeCount = 0;
		PreparedStatement sta = null;
		try {
			conn.setAutoCommit(false);
			sta = conn.prepareStatement(sql);
			if(params != null && params.size() > 0)
			{
				int bc = 0;
				for(List<Object> param:params)
				{
					if(param.size() > 0)
					{
						for(int i = 0;i<param.size();i++)
						{
							sta.setObject(i+1, param.get(i));
						}
						sta.addBatch();
						bc ++;
						executeCount ++;
					}
					if(bc >= batchCount)
					{
						sta.executeBatch();
						sta.clearBatch();
						bc = 0;
					}
				}
				if(bc > 0)
				{
					sta.executeBatch();
					sta.clearBatch();
				}
			}
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		}finally{
			close(sta);
		}
		return executeCount;
	}
	
	/**
	 * 获取结果集，不关闭Connection
	 * @param Connection
	 * @param sql 执行sql
	 * @param params 执行sql参数
	 * @return 结果集
	 * @throws Exception
	 */
	public static List<Map<String, Object>> getResultList(Connection conn,String sql,Object[] params) throws Exception
	{
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Map<String, Object> row = null;
		PreparedStatement sta = null;
		ResultSet set = null;
		ResultSetMetaData metaData = null;
		try {
			sta = conn.prepareStatement(sql);
			if(params != null && params.length > 0)
			{
				for(int i = 0;i<params.length;i++)
				{
					sta.setObject(i+1, params[i]);
				}
			}
			set = sta.executeQuery();
			metaData = set.getMetaData();
			int count = metaData.getColumnCount();
			String[] columnNames = new String[count];
			for(int i=0;i<count;i++)
			{
				columnNames[i] = metaData.getColumnLabel(i+1);
			}
			
			while (set.next()) {
				row = new HashMap<String, Object>();
				for(String columnName:columnNames)
				{
					row.put(columnName.toLowerCase(), set.getObject(columnName));
				}
				list.add(row);
			}
			
		} finally{
			close(set,sta);
		}

		return list;
	}
	
	/**
	 * 获取结果集第一条数据，不关闭Connection
	 * @param Connection
	 * @param sql 执行sql
	 * @param params 执行sql参数
	 * @return 结果集
	 * @throws Exception
	 */
	public static Map<String, Object> getResultFirst(Connection conn,String sql,Object[] params) throws Exception
	{
		Map<String, Object> row = null;
		PreparedStatement sta = null;
		ResultSet set = null;
		ResultSetMetaData metaData = null;
		try {
			sta = conn.prepareStatement(sql);
			if(params != null && params.length > 0)
			{
				for(int i = 0;i<params.length;i++)
				{
					sta.setObject(i+1, params[i]);
				}
			}
			set = sta.executeQuery();
			metaData = set.getMetaData();
			int count = metaData.getColumnCount();
			String[] columnNames = new String[count];
			for(int i=0;i<count;i++)
			{
				columnNames[i] = metaData.getColumnLabel(i+1);
			}
			
			if (set.next()) {
				row = new HashMap<String, Object>();
				for(String columnName:columnNames)
				{
					row.put(columnName.toLowerCase(), set.getObject(columnName));
				}
			}
			
		} finally{
			close(set,sta);
		}

		return row;
	}
	
	/**
	  * 
	  * 释放资源
	  * @param objects
	  */
	public static void close(Object ...objects){
		if(objects != null && objects.length > 0){
			try {
				for(Object o:objects){
					if(o instanceof ResultSet){
						((ResultSet)o).close();
					}else if(o instanceof Statement){
						((Statement)o).close();
					}else if(o instanceof Connection){
						((Connection)o).close();
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String pageSql(String sql)
	{
		return "select * from (select * from (" + sql + ") where rn_ <= ?) where rn_ > ?";
	}
	
	public static String pageSqlCount(String sql)
	{
		return "select count(1) cc from (" + sql + ")";
	}
	
	public static void main(String[] args) {
		try {
			DataSourceUtil.initDataSource("sys", "oracle.jdbc.OracleDriver", "jdbc:oracle:thin:@//10.193.1.70:1521/orcl", "ZXDS", "b12a3a6d70c7b");
			Map<String, Object> count = DataSourceUtil.getResultFirst(DataSourceUtil.getConnection("sys"), "select count(1) cc from T_JK_QWY_USER t where t.USERID in ?", new Object[]{Arrays.asList("de931a4267bdd65fd6a62f40f5b3bdbc","6df62d5f44930bfb3f913597743d1a1e")});
			System.out.println(count.get("cc"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
