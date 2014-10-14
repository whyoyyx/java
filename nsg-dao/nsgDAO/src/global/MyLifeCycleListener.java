package global;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.logging.log4j.Logger;
import org.nsg.app.AppLifeCycleListener;
import org.nsg.util.LogUtil;

public class MyLifeCycleListener implements AppLifeCycleListener
{
	Logger logger = LogUtil.getDefaultLogger();
	
	@Override
	public void onStartup(ServletContext context, ServletContextEvent sce)
	{
		logger.info(this.getClass().getName() + " -> onStartup()");
	}

	@Override
	public void onShutdown(ServletContext context, ServletContextEvent sce)
	{
		logger.info(this.getClass().getName() + " -> onShutdown()");
	}
}
