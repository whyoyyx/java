package org.nsg.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;

/**
 * 
 * 日志记录器（用 log4j 实现）
 *
 */
public class LogUtil
{
	/** 默认日志上下文名称 */
	public static final	String DEFAULT_CONTEXT_NAME		= "nsg Context";
	/** 默认配置文件 */
	public static final	String DEFAULT_CONFIG_FILE_NAME	= "log4j2.xml";
	
	private static Logger defaultLogger;
	private static LoggerContext context;
	
	/** 设置应用程序默认日志记录器名称 */
	public static final void setDefaultLoggerName(Class<?> clazz)
	{
		defaultLogger = LogManager.getLogger(clazz);
	}
	
	/** 设置应用程序默认日志记录器名称 */
	public static final void setDefaultLoggerName(String name)
	{
		defaultLogger = LogManager.getLogger(name);
	}
	
	/** 获取应用程序默认日志记录器对象 */
	public static final Logger getDefaultLogger()
	{
		return defaultLogger;
	}
	
	/** 初始化日志上下文 */
	public static final void initialize()
	{
		String file = GeneralHelper.getClassResourcePath(LogUtil.class, DEFAULT_CONFIG_FILE_NAME);
		initialize(DEFAULT_CONTEXT_NAME, file);
	}
	
	/** 初始化日志上下文 */
	public static final void initialize(String file)
	{
		initialize(DEFAULT_CONTEXT_NAME, file);
	}
	
	/** 初始化日志上下文 */
	public static final void initialize(String contextName, String file)
	{
		context = Configurator.initialize(contextName, file);
	}
	
	/** 关闭日志上下文 */
	public static final void shutdown()
	{
		if(context != null)
		{
			Configurator.shutdown(context);
			
			context			= null;
			defaultLogger	= null;
		}
	}
	
	/** 检查日志系统是否可用 */
	public static final boolean isValid()
	{
		return context != null;
	}
	
	/** 检查默认日志记录器是否可用 */
	public static final boolean isDefaultLoggerValid()
	{
		return defaultLogger != null;
	}
	
	/** 记录操作异常日志 */
	public static final void exception(Throwable e, Object action, Level level,boolean printStack)
	{
		StringBuilder msg = new StringBuilder("Execption occur on ");
		msg.append(action);
		msg.append(" --> ");
		msg.append(e.toString());
		String error = msg.toString();

		if(printStack)
			defaultLogger.log(level, error, e);
		else
			defaultLogger.log(level, error);
	}
	
	/** 记录操作失败日志 */
	public static final void fail(Object action, Object module, boolean printStack)
	{
		StringBuilder msg = new StringBuilder("Operation fail on ");
		msg.append(action);
		msg.append(" --> ");
		msg.append(module);

		defaultLogger.error(msg.toString());
	}
	
	/** 记录服务器启动日志 */
	public static final void logServerStartup(Object o)
	{
		defaultLogger.info("->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->");
		defaultLogger.info("starting: {}", o);
	}
	
	/** 记录服务器关闭日志 */
	public static final void logServerShutdown(Object o)
	{
		defaultLogger.info("stoping: {}", o);
		defaultLogger.info("<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-");
	}
}
