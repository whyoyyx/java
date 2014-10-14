package org.nsg.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;

/**
 * 
 * ��־��¼������ log4j ʵ�֣�
 *
 */
public class LogUtil
{
	/** Ĭ����־���������� */
	public static final	String DEFAULT_CONTEXT_NAME		= "nsg Context";
	/** Ĭ�������ļ� */
	public static final	String DEFAULT_CONFIG_FILE_NAME	= "log4j2.xml";
	
	private static Logger defaultLogger;
	private static LoggerContext context;
	
	/** ����Ӧ�ó���Ĭ����־��¼������ */
	public static final void setDefaultLoggerName(Class<?> clazz)
	{
		defaultLogger = LogManager.getLogger(clazz);
	}
	
	/** ����Ӧ�ó���Ĭ����־��¼������ */
	public static final void setDefaultLoggerName(String name)
	{
		defaultLogger = LogManager.getLogger(name);
	}
	
	/** ��ȡӦ�ó���Ĭ����־��¼������ */
	public static final Logger getDefaultLogger()
	{
		return defaultLogger;
	}
	
	/** ��ʼ����־������ */
	public static final void initialize()
	{
		String file = GeneralHelper.getClassResourcePath(LogUtil.class, DEFAULT_CONFIG_FILE_NAME);
		initialize(DEFAULT_CONTEXT_NAME, file);
	}
	
	/** ��ʼ����־������ */
	public static final void initialize(String file)
	{
		initialize(DEFAULT_CONTEXT_NAME, file);
	}
	
	/** ��ʼ����־������ */
	public static final void initialize(String contextName, String file)
	{
		context = Configurator.initialize(contextName, file);
	}
	
	/** �ر���־������ */
	public static final void shutdown()
	{
		if(context != null)
		{
			Configurator.shutdown(context);
			
			context			= null;
			defaultLogger	= null;
		}
	}
	
	/** �����־ϵͳ�Ƿ���� */
	public static final boolean isValid()
	{
		return context != null;
	}
	
	/** ���Ĭ����־��¼���Ƿ���� */
	public static final boolean isDefaultLoggerValid()
	{
		return defaultLogger != null;
	}
	
	/** ��¼�����쳣��־ */
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
	
	/** ��¼����ʧ����־ */
	public static final void fail(Object action, Object module, boolean printStack)
	{
		StringBuilder msg = new StringBuilder("Operation fail on ");
		msg.append(action);
		msg.append(" --> ");
		msg.append(module);

		defaultLogger.error(msg.toString());
	}
	
	/** ��¼������������־ */
	public static final void logServerStartup(Object o)
	{
		defaultLogger.info("->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->");
		defaultLogger.info("starting: {}", o);
	}
	
	/** ��¼�������ر���־ */
	public static final void logServerShutdown(Object o)
	{
		defaultLogger.info("stoping: {}", o);
		defaultLogger.info("<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-");
	}
}
