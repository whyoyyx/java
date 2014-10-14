package org.nsg.app;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.Level;
import org.nsg.util.GeneralHelper;
import org.nsg.util.LogUtil;

/**
 * 
 * 应用程序启动监听器，实现 {@link ServletContextListener} 接口。
 *
 */
public class AppListener implements ServletContextListener
{
	private static final String APP_LOGGER_NAME				= "nsg";
	private static final String DEFAULT_APP_CONFIG_FILE		= "app-config.xml";
	private static final String APP_CONFIG_FILE_KEY			= "app-config-file";
	private static final String LOGGER_CONFIG_FILE_KEY		= "logger-config-file";
	
	private ServletContext context;
		
	@Override
	public void contextInitialized(ServletContextEvent sce)
	{
		context = sce.getServletContext();
		
		String appConfigFile = context.getInitParameter(APP_CONFIG_FILE_KEY);
		if(GeneralHelper.isStrEmpty(appConfigFile))
			appConfigFile = DEFAULT_APP_CONFIG_FILE;

		String loggerConfigFile = context.getInitParameter(LOGGER_CONFIG_FILE_KEY);
		if(GeneralHelper.isStrEmpty(loggerConfigFile))
			loggerConfigFile = LogUtil.DEFAULT_CONFIG_FILE_NAME;
		
		appConfigFile	 = GeneralHelper.getClassResourcePath(AppListener.class, appConfigFile);
		loggerConfigFile = GeneralHelper.getClassResourcePath(AppListener.class, loggerConfigFile);
		
		try
		{
			/* 启动日志组件 */
			LogUtil.initialize(loggerConfigFile);
			LogUtil.setDefaultLoggerName(APP_LOGGER_NAME);

			LogUtil.logServerStartup(this);
			
			/* 加载程序配置 */
			LogUtil.getDefaultLogger().info("load application configuration");
			AppConfig.initialize(appConfigFile);
			
			/* 发送程序启动通知 */
			AppConfig.sendStartupNotice(context, sce);
		}
		catch(Exception e)
		{
			String msg = "application startup fail";
			
			if(LogUtil.isDefaultLoggerValid())
				LogUtil.exception(e, msg, Level.FATAL, true);
			
			throw new RuntimeException(msg, e);
		}
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce)
	{
		try
		{
			/* 发送程序停止通知 */
			AppConfig.sendShutdownNotice(context, sce);

			/* 卸载程序资源 */
			AppConfig.unInitialize();

			if(LogUtil.isDefaultLoggerValid())
				LogUtil.logServerShutdown(this);

			/* 关闭日志组件 */
			LogUtil.shutdown();
		}
		catch(Exception e)
		{
			String msg = "application shutdown exception";

			if(LogUtil.isDefaultLoggerValid())
				LogUtil.exception(e, msg, Level.FATAL, true);

			throw new RuntimeException(msg, e);

		}
	}
}
