package org.nsg.dao.jdbc;

/**
 * 
 * JDBC “Ï≥£¿‡£¨ºÃ≥– {@link RuntimeException}
 * 
 */
@SuppressWarnings("serial")
public class JdbcException extends RuntimeException
{
	public JdbcException()
	{
		
	}
	
	public JdbcException(String desc)
	{
		super(desc);
	}
	
	public JdbcException(Throwable e)
	{
		super(e);
	}
	
	public JdbcException(String desc, Throwable e)
	{
		super(desc, e);
	}
}
