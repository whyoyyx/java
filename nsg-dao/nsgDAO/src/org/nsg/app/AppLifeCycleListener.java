package org.nsg.app;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

/** Ӧ�ó����������ڼ������ӿ� */
public interface AppLifeCycleListener
{
	/**
	 *  ��������֪ͨ
	 *  
	 *  @param context	: Ӧ�ó���� {@link ServletContext}
	 *  @param sce	: �¼����� {@link ServletContextEvent}
	 *  
	*/
	void onStartup(ServletContext context, ServletContextEvent sce);

	/**
	 *  ����ر�֪ͨ
	 *  
	 *  @param context	: Ӧ�ó���� {@link ServletContext}
	 *  @param sce	: �¼����� {@link ServletContextEvent}
	 *  
	*/
	void onShutdown(ServletContext context, ServletContextEvent sce);

}
