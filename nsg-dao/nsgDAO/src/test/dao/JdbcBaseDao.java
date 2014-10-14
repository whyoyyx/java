package test.dao;


import global.MyConfigParser;

import org.nsg.dao.jdbc.AbstractJdbcSessionMgr;
import org.nsg.dao.jdbc.JdbcFacade;

public class JdbcBaseDao extends JdbcFacade
{
	protected JdbcBaseDao()
	{
		this(MyConfigParser.getJdbcSessionMgr());
	}
	
	protected JdbcBaseDao(AbstractJdbcSessionMgr mgr)
	{
		super(mgr);
	}
}
