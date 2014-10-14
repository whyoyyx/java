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
 * JDBC ������
 *
 */
public class JdbcUtil
{
	/** �ر� {@link Connection}��{@link Statement}��{@link ResultSet} �� JDBC SQL ���� */
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
	 * ���� {@link PreparedStatement} �� {@link CallableStatement} 
	 * 
	 * @param conn			: {@link Connection} ����
	 * @param sql			: SQL ���
	 * @param isCallable	: �Ƿ����� {@link CallableStatement} 
	 * @return				  {@link PreparedStatement} �� {@link CallableStatement}
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
	 * ���ò�ѯ����
	 * 
	 * @param pst		: {@link PreparedStatement}
	 * @param params	: ��ѯ����
	 * @throws SQLException
	 */
	public static final void setParameters(PreparedStatement pst, Object ... params) throws SQLException
	{
		setInputParameters(pst, 1, params);
	}
	
	/**
	 * 
	 * �����������
	 * 
	 * @param pst		: {@link PreparedStatement}
	 * @param startPos	: ��ʼ����
	 * @param params	: ��ѯ����
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
	 * ע���������
	 * 
	 * @param cst		: {@link CallableStatement}
	 * @param startPos	: ��ʼ����
	 * @param types		: �����������飨�ο���{@link Types}��
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
