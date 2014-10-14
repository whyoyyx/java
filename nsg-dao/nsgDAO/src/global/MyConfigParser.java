package global;

import org.apache.logging.log4j.Logger;
import org.dom4j.Element;
import org.nsg.app.AppConfig;
import org.nsg.app.UserConfigParser;
import org.nsg.dao.jdbc.AbstractJdbcSessionMgr;
import org.nsg.util.LogUtil;

public class MyConfigParser implements UserConfigParser
{
	Logger logger = LogUtil.getDefaultLogger();
	
	private static final String MY_HOME = "my-home";
	
	// 获取JdbcSessionMgr 对象
	private static AbstractJdbcSessionMgr jdbcSessionMgr;
	
	@Override
	public void parse(Element user)
	{
		jdbcSessionMgr		= (AbstractJdbcSessionMgr)AppConfig.getSessionManager("session-mgr-1");
		Element mh = user.element(MY_HOME);
		if(mh != null)
		{
			String myHome = mh.getTextTrim();
			logger.info("My Home is: " + myHome);
		}
	}

	public static final AbstractJdbcSessionMgr getJdbcSessionMgr()
	{
		return jdbcSessionMgr;
	}

}