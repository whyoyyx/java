package org.nsg.dao.jdbc;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;

import org.nsg.dao.AbstractSessionMgr;
import org.nsg.dao.SessionMgr;
import org.nsg.dao.TransIsoLevel;
import org.nsg.util.GeneralHelper;


/**
 * 
 * JDBC {@link SessionMgr} 抽象基类
 *
 */
public abstract class AbstractJdbcSessionMgr extends AbstractSessionMgr<Connection>
{
	/** 获取默认配置文件 */
	protected abstract String getDefaultConfigFile();
	
	/** 获取内部的 {@link Connection} 对象（实际的获取工作由子类实现） 
	 * @throws SQLException */
	protected abstract Connection getInternalConnection() throws SQLException;

	/** 参考：{@link AbstractSessionMgr#loadDefalutTransIsoLevel()} */
	@Override
	protected void loadDefalutTransIsoLevel()
	{
		try
		{
			Connection session		= getSession();
			int level				= session.getTransactionIsolation();
			defaultTransIsoLevel	= TransIsoLevel.fromInt(level);
		}
		catch(SQLException e)
		{
			throw new JdbcException(e);
		}
		finally
		{
			closeSession();
		}
	}
	
	/** 参考：{@link SessionMgr#setSessionTransIsoLevel(TransIsoLevel)} */
	@Override
	public void setSessionTransIsoLevel(TransIsoLevel level)
	{
		try
		{
			Connection session = getSession();
			session.setTransactionIsolation(level.toInt());
		}
		catch(SQLException e)
		{
			throw new JdbcException(e);
		}
	}

	@Override
	public void beginTransaction()
	{
		Connection session = getSession();
		
		if(session != null)
		{
			try
			{
				session.setAutoCommit(false);
			}
			catch(SQLException e)
			{
				throw new JdbcException(e);
			}
		}
	}

	@Override
	public void commit()
	{
		Connection session = getSession();
		
		if(session != null)
		{
			try
			{
				session.commit();
			}
			catch(SQLException e)
			{
				throw new JdbcException(e);
			}
			finally
			{
				try {session.setAutoCommit(true);} catch (SQLException ex) {}
			}
		}
	}

	@Override
	public void rollback()
	{
		Connection session = getSession();
		
		if(session != null)
		{
			try
			{
				session.rollback();
			}
			catch(SQLException e)
			{
				throw new JdbcException(e);
			}
			finally
			{
				try {session.setAutoCommit(true);} catch (SQLException ex) {}
			}
		}
	}

	/** 获取数据库连接对象 */
	@Override
	public final Connection getSession()
	{
		Connection session = localSession.get();
		
		try
		{
        		if(session == null || session.isClosed())
        		{
         			session = getInternalConnection();
         			localSession.set(session);
        		}
		}
		catch(SQLException e)
		{
			localSession.set(null);
			throw new JdbcException(e);
		}

		return session;
	}

	@Override
	public void closeSession()
	{
		Connection session = localSession.get();
		localSession.set(null);

		if(session != null)
		{
			try
			{
				session.close();
			}
			catch(SQLException e)
			{
				throw new JdbcException(e);
			}
		}
	}
	
	protected void parseConfigFile(String configFile) throws FileNotFoundException
	{
		this.configFile = GeneralHelper.getClassResourcePath(	AbstractJdbcSessionMgr.class, 
																GeneralHelper.isStrNotEmpty(configFile) ? 
																configFile : getDefaultConfigFile());
		if(this.configFile == null)
			throw new FileNotFoundException(String.format("config file '%s' not found", configFile));
	}
	
	protected boolean isXmlConfigFile()
	{
		final String PROPS_FILE_SUFFIX = ".properties";
		
		int index = configFile.lastIndexOf('.');
		if(index != -1)
		{
			String suffix = configFile.substring(index);
			if(suffix.equalsIgnoreCase(PROPS_FILE_SUFFIX))
				return false;
		}

		return true;
	}
}
