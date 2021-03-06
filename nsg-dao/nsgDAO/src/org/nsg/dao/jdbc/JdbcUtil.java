package org.nsg.dao.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

/**
 * 
 * JDBC 帮助类
 *
 */
public class JdbcUtil
{
	/** 关闭 {@link Connection}、{@link Statement}、{@link ResultSet} 等 JDBC SQL 对象 */
	public static final void closeSqlObject(Object ... sqlObjs)
	{
		try
		{
        		for(Object obj : sqlObjs)
        		{
        			if(obj == null)
        				continue;
        			
               			if(obj instanceof Connection)
        				((Connection)obj).close();
               			else if(obj instanceof Statement)				// PreparedStatement and CallableStatement are included !!
        				((Statement)obj).close();
               			else if(obj instanceof ResultSet)				// RowSet is included !!
        				((ResultSet)obj).close();
               			// else if(obj instanceof PreparedStatement)	// no need
               			// 	((PreparedStatement)obj).close();
              			// else if(obj instanceof CallableStatement)	// no need
               			// 	((CallableStatement)obj).close();
               			// else if(obj instanceof RowSet)				// no need
               			// 	((RowSet)obj).close();
         		}
		}
		catch(SQLException e)
		{
			throw new JdbcException(e);
		}
	}
	
	/**
	 * 
	 * 生成 {@link PreparedStatement} 或 {@link CallableStatement} 
	 * 
	 * @param conn			: {@link Connection} 对象
	 * @param sql			: SQL 语句
	 * @param isCallable	: 是否生成 {@link CallableStatement} 
	 * @return				  {@link PreparedStatement} 或 {@link CallableStatement}
	 * @throws SQLException
	 */
	public static final PreparedStatement prepareStatement(Connection conn, String sql, boolean isCallable) throws SQLException
	{
		if(!isCallable)
			return conn.prepareStatement(sql);
		else
			return conn.prepareCall(sql);
	}

	/**
	 * 
	 * 设置查询参数
	 * 
	 * @param pst		: {@link PreparedStatement}
	 * @param params	: 查询参数
	 * @throws SQLException
	 */
	public static final void setParameters(PreparedStatement pst, Object ... params) throws SQLException
	{
		setInputParameters(pst, 1, params);
	}
	
	/**
	 * 
	 * 设置输入参数
	 * 
	 * @param pst		: {@link PreparedStatement}
	 * @param startPos	: 起始索引
	 * @param params	: 查询参数
	 * @throws SQLException
	 */
	public static final void setInputParameters(PreparedStatement pst, int startPos, Object ... params) throws SQLException
	{
		for(int i = 0; i < params.length; i++)
		{
			Object o = params[i];
			
			if(o != null)
				pst.setObject(i + startPos, o);
			else
				pst.setNull(i + startPos, java.sql.Types.NULL);
		}
	}
	
	/**
	 * 
	 * 注册输出参数
	 * 
	 * @param cst		: {@link CallableStatement}
	 * @param startPos	: 起始索引
	 * @param types		: 参数类型数组（参考：{@link Types}）
	 * @throws SQLException
	 */
	public static final void registerOutputParameters(CallableStatement cst, int startPos, int[] types) throws SQLException
	{
		for(int i = 0; i < types.length; i++)
		{
			int t = types[i];
			cst.registerOutParameter(i + startPos, t);
		}
	}
}
