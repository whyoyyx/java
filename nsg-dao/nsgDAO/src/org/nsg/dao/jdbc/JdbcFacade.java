package org.nsg.dao.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.nsg.dao.AbstractFacade;
import org.nsg.util.KV;


/**
 * 
 * JDBC Facade ʵ����
 *
 */
public abstract class JdbcFacade extends AbstractFacade<AbstractJdbcSessionMgr, Connection>
{
	protected JdbcFacade(AbstractJdbcSessionMgr mgr)
	{
		super(mgr);
	}

	/* ************************************************************************************************** */
	/* ******************************************* ҵ�񷽷� ******************************************* */

	/**
	 * 
	 * ִ�� SQL ��ѯ��
	 * 
	 * @param sql			: ��ѯ���
	 * @param params		: ��ѯ����
	 * @return				  �����
	 * 
	 */
	protected List<Object[]> query(String sql, Object ... params)
	{
		return query(false, sql, params);
	}
	
	/**
	 * 
	 * ִ�� SQL ��ѯ��
	 * 
	 * @param isCallable	: �Ƿ�ʹ�� CallableStatment ִ�в�ѯ
	 * @param sql			: ��ѯ���
	 * @param params		: ��ѯ����
	 * @return				  �����
	 * 
	 */
	protected List<Object[]> query(boolean isCallable, String sql, Object ... params)
	{
		List<Object[]> result = new ArrayList<Object[]>();
		
		ResultSet rs = null;
		PreparedStatement pst = null;

		try
		{
			Connection conn = getSession();
			pst = JdbcUtil.prepareStatement(conn, sql, isCallable);
			JdbcUtil.setParameters(pst, params);
			rs = pst.executeQuery();
			int cols = rs.getMetaData().getColumnCount();
			
			while(rs.next())
			{
				Object[] objs = new Object[cols];
    			for(int i = 0; i < cols; i++)
    				objs[i] = rs.getObject(i + 1);
    				
    			result.add(objs);
			}
		}
		catch (SQLException e)
		{
			throw new JdbcException(e);
		}
		finally
		{
			JdbcUtil.closeSqlObject(pst, rs);
		}
		
		return result;
	}
	
	/**
	 * 
	 * ִ�д洢���̡�
	 * 
	 * @param sql			: ��ѯ���
	 * @param inParams		: �������
	 * @return				  ִ�н�����ɹ���ʧ�ܣ�
	 * 
	 */
	protected boolean call(String sql, Object ... inParams)
	{
		return call(1, new int[0], sql, inParams).getKey();
	}
	
	/**
	 * 
	 * ִ�д洢���̡�
	 * 
	 * @param outParamPos	: ���������λ�ã��� 1 ��ʼ���㣩
	 * @param outParaType	: ����������ͣ��ο���java.sql.Types��
	 * @param sql			: ��ѯ���
	 * @param inParams		: �������
	 * @return				  ִ�н����Key: �ɹ���ʧ�ܣ�Value: ����ɹ���������������
	 * 
	 */
	protected KV<Boolean, Object> call(int outParamPos, int outParaType, String sql, Object ... inParams)
	{
		KV<Boolean, Object[]> keys = call(outParamPos, new int[] {outParaType}, sql, inParams);
		Object[] values = keys.getValue();
		Object value = values != null && values.length > 0 ? values[0] : null;
		
		return new KV<Boolean, Object>(keys.getKey(), value);
	}
	
	/**
	 * 
	 * ִ�д洢���̡�
	 * 
	 * @param outParamStartPos	: �����������ʼλ�ã��� 1 ��ʼ���㣩
	 * @param outParamTypes		: ����������ͼ��ϣ��ο���java.sql.Types��
	 * @param sql				: ��ѯ���
	 * @param inParams			: �������
	 * @return				  	  ִ�н����Key: �ɹ���ʧ�ܣ�Value: ����ɹ����������������ϣ�
	 * 
	 */
	protected KV<Boolean, Object[]> call(int outParamStartPos, int[] outParamTypes, String sql, Object ... inParams)
	{
		KV<Boolean, Object[]> result = new KV<Boolean, Object[]>();
		
		ResultSet rs = null;
		CallableStatement cst = null;

		try
		{
			Connection conn = getSession();
			cst = (CallableStatement)JdbcUtil.prepareStatement(conn, sql, true);
			int inputParameterStartPosition = outParamStartPos == 1 ? outParamStartPos + outParamTypes.length : 1;
			JdbcUtil.setInputParameters(cst, inputParameterStartPosition, inParams);
			JdbcUtil.registerOutputParameters(cst, outParamStartPos, outParamTypes);
			
			result.setKey(cst.execute());
			
			if(result.getKey() && outParamTypes.length > 0)
			{
				rs = cst.getResultSet();
				
				if(rs.next())
				{
					Object[] objs = new Object[outParamTypes.length];
	    			for(int i = 0; i < objs.length; i++)
	    				objs[i] = rs.getObject(i + outParamStartPos);
	    			
	    			result.setValue(objs);
				}
			}
		}
		catch (SQLException e)
		{
			throw new JdbcException(e);
		}
		finally
		{
			JdbcUtil.closeSqlObject(cst, rs);
		}
		
		return result;
	}
	
	/**
	 * 
	 * ִ�� SQL ���¡�
	 * 
	 * @param sql		: ��ѯ���
	 * @param params	: ��ѯ����
	 * @return			  ���½��
	 * 
	 */
	protected int update(String sql, Object ... params)
	{
		return update(false, sql, params);
	}
	
	/**
	 * 
	 * ִ�� SQL ���¡�
	 * 
	 * @param isCallable	: �Ƿ�ʹ�� CallableStatment ִ�и���
	 * @param sql			: ��ѯ���
	 * @param params		: ��ѯ����
	 * @return				  ���½��
	 * 
	 */
	protected int update(boolean isCallable, String sql, Object ... params)
	{
		int effect = 0;
		PreparedStatement pst = null;

		try
		{
			Connection conn = getSession();
			pst = JdbcUtil.prepareStatement(conn, sql, isCallable);
			JdbcUtil.setParameters(pst, params);
			effect = pst.executeUpdate();
		}
		catch (SQLException e)
		{
			throw new JdbcException(e);
		}
		finally
		{
			JdbcUtil.closeSqlObject(pst);
		}
	
		return effect;
	}

	/**
	 * 
	 * ִ�� SQL ���²����������ֶ�ֵ��
	 * 
	 * @param sql		: ��ѯ���
	 * @param params	: ��ѯ����
	 * @return			  ���½����Key: Ӱ���������Value: ���ص������ֶ�ֵ���ϣ�
	 * 
	 */
	protected KV<Integer, List<Object>> updateAndGenerateKeys(String sql, Object ... params)
	{
		KV<Integer, List<Object[]>> keys = updateAndGenerateKeys(null, sql, params);
		
		List<Object[]> values = keys.getValue();
		KV<Integer, List<Object>> result = new KV<Integer, List<Object>>(keys.getKey(), null);
		
		if(values != null && values.size() > 0)
		{
			List<Object> v = new ArrayList<Object>();
			result.setValue(v);
			
			for(Object[] objs : values)
				v.add(objs[0]);
		}
		
		return result;
	}

	/**
	 * 
	 * ִ�� SQL ���²����������ֶ�ֵ��
	 * 
	 * @param keyColIndexes	: �����ֶ���������
	 * @param sql			: ��ѯ���
	 * @param params		: ��ѯ����
	 * @return				  ���½����Key: Ӱ���������Value: ���ص������ֶ�ֵ���ϣ�
	 * 
	 */
	protected KV<Integer, List<Object[]>> updateAndGenerateKeys(int[] keyColIndexes, String sql, Object ... params)
	{
		KV<Integer, List<Object[]>> result = new KV<Integer, List<Object[]>>();
		
		ResultSet rs = null;
		PreparedStatement pst = null;

		try
		{
			Connection conn = getSession();
			
			if(keyColIndexes == null)
				pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			else
				pst = conn.prepareStatement(sql, keyColIndexes);
			
			JdbcUtil.setParameters(pst, params);
			result.setKey(pst.executeUpdate());
			
			if(result.getKey() > 0)
			{
				List<Object[]> value = new ArrayList<Object[]>();
				result.setValue(value);

				rs = pst.getGeneratedKeys();
				while(rs.next())
				{					
					if(keyColIndexes == null)
						value.add(new Object[] {rs.getObject(1)});
					else
					{
						Object[] keys = new Object[keyColIndexes.length];
						
						for(int i = 0; i < keys.length; i++)
							keys[i] = rs.getObject(i + 1);
						
						value.add(keys);
					}
				}
			}
		}
		catch (SQLException e)
		{
			throw new JdbcException(e);
		}
		finally
		{
			JdbcUtil.closeSqlObject(pst, rs);
		}
		
		return result;
	}

	/**
	 * 
	 * ִ�� SQL ���¡�
	 * 
	 * @param sql		: ��ѯ���
	 * @param params	: ��ѯ������List ��ÿһ��Ԫ�����һ�� batch��
	 * @return			  Ӱ���������
	 * 
	 */
	protected int[] updateBatch(String sql, List<Object[]> params)
	{
		return updateBatch(false, sql, params);
	}
	
	/**
	 * 
	 * ִ�� SQL ���¡�
	 * 
	 * @param sql			: ��ѯ���
	 * @param isCallable	: �Ƿ�ʹ�� CallableStatment ִ�в�ѯ
	 * @param params		: ��ѯ������List ��ÿһ��Ԫ�����һ�� batch��
	 * @return				  Ӱ���������
	 * 
	 */
	protected int[] updateBatch(boolean isCallable, String sql, List<Object[]> params)
	{
		return updateBatch(isCallable, sql, params, 0);
	}
	
	/**
	 * 
	 * ִ�� SQL ���¡�
	 * 
	 * @param sql			: ��ѯ���
	 * @param params		: ��ѯ������List ��ÿһ��Ԫ�����һ�� batch��
	 * @param batchSize	: ����ִ�� batchUpdate ʱ��ÿ�� batch ����Ŀ��Ĭ�ϣ�params ��Ԫ�ظ�����
	 * @return				  Ӱ���������
	 * 
	 */
	protected int[] updateBatch(String sql, List<Object[]> params, int batchSize)
	{
		return updateBatch(false, sql, params, batchSize);
	}
	
	/**
	 * 
	 * ִ�� SQL ���¡�
	 * 
	 * @param isCallable	: �Ƿ�ʹ�� CallableStatment ִ�в�ѯ
	 * @param sql			: ��ѯ���
	 * @param params		: ��ѯ������List ��ÿһ��Ԫ�����һ�� batch��
	 * @param batchSize	: ����ִ�� batchUpdate ʱ��ÿ�� batch ����Ŀ��Ĭ�ϣ�params ��Ԫ�ظ�����
	 * @return				  Ӱ���������
	 * 
	 */
	protected int[] updateBatch(boolean isCallable, String sql, List<Object[]> params, int batchSize)
	{
		PreparedStatement pst = null;
		
		int i			= 0;
		int start		= 0;
		int size		= params.size();
		int[] effect	= new int[size];
		
		if(size == 0) return effect;
		if(batchSize <= 0) batchSize = size;

		try
		{
			Connection conn = getSession();
			pst = JdbcUtil.prepareStatement(conn, sql, isCallable);
			
			while(i < size)
			{
				Object[] objects = params.get(i);
				JdbcUtil.setParameters(pst, objects);
				pst.addBatch();
				
				if(++i % batchSize == 0)
				{
					System.arraycopy(pst.executeBatch(), 0, effect, start, i - start);
					pst.clearBatch();
					start = i; 
				}
			}
			
			if(i % batchSize != 0)
				System.arraycopy(pst.executeBatch(), 0, effect, start, i - start);
		}
		catch (SQLException e)
		{
			throw new JdbcException(e);
		}
		finally
		{
			JdbcUtil.closeSqlObject(pst);
		}
	
		return effect;
	}
}
