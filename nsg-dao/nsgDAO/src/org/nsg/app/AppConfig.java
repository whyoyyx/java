package org.nsg.app;

import java.io.File;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.nsg.dao.SessionMgr;
import org.nsg.util.GeneralHelper;
import org.nsg.util.LogUtil;

/**
 * 
 * ϵͳ�����࣬�������ϵͳ���á�
 *
 */
@SuppressWarnings("rawtypes")
public class AppConfig
{
	private Map<String, SessionMgr> sessionMgrs = new HashMap<String, SessionMgr>();
	private UserConfigParser userCfgParser;
	private AppLifeCycleListener appListener;
	
	private String confFile;
	
	private static final Logger appLogger	= LogUtil.getDefaultLogger();
	private static final AppConfig instance	= new AppConfig();
	
	private AppConfig()
	{
		
	}
	
	/**
	 * 
	 * ��ȡ���� {@link SessionMgr} 
	 * 
	 * @return	  ���� -> ʵ��
	 * 
	 */
	public static final Map<String, SessionMgr> getSessionManagers()
	{
		return instance.sessionMgrs;
	}
	
	/**
	 * 
	 * ��ȡָ�����Ƶ� {@link SessionMgr} 
	 * 
	 * @param name	: {@link SessionMgr} ����
	 * @return	  	: {@link SessionMgr} ʵ��
	 * 
	 */
	public static final SessionMgr getSessionManager(String name)
	{
		return instance.sessionMgrs.get(name);
	}

	static final void sendStartupNotice(ServletContext context, ServletContextEvent sce)
	{
		if(instance.appListener != null)
			instance.appListener.onStartup(context, sce);
	}
	
	static final void sendShutdownNotice(ServletContext context, ServletContextEvent sce)
	{
		if(instance.appListener != null)
			instance.appListener.onShutdown(context, sce);
	}

	static void initialize(String file)
	{
		instance.confFile = file;
		instance.loadConfig();
	}

	private void loadConfig()
	{
		try
		{
			Element root = getRootElement();
			Element sys	 = getSystemElement(root);
			
			loadSessionMgrs(sys);		
			loadUserConfig(root, sys);
			loadAppListener(sys);
		}
		catch(Exception e)
		{
			throw new RuntimeException("load application configuration fail", e);
		}
	}

	private Element getRootElement() throws DocumentException
	{
		SAXReader sr = new SAXReader();
		Document doc = sr.read(new File(confFile));
		Element root = doc.getRootElement();
		
		return root;
	}

	private Element getSystemElement(Element root)
	{
		Element sys = root.element("system");
		
		if(sys == null)
			throw new RuntimeException("'system' element not found");
		
		return sys;
	}

	// ע�����ݿ� Session ������
	@SuppressWarnings("unchecked")
	private void loadSessionMgrs(Element sys) throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		Element sms = sys.element("database-session-managers");
		if(sms == null)
			appLogger.warn("'database-session-managers' element not found");
		else
		{
			List<Element> mgrs = sms.elements("manager");
			if(mgrs.size() == 0)
				appLogger.warn("none of DATABASE SESSION MANAGER found");
			else
			{				
				for(Element e : mgrs)
				{
					String name = e.attributeValue("name");
					String clazz = e.attributeValue("class");
					
					if(GeneralHelper.isStrEmpty(name) || GeneralHelper.isStrEmpty(clazz))
						throw new RuntimeException("manager's 'name' or 'class' attribute not valid");
					
					String[] params;
					Element iniargs = e.element("initialize-args");
					if(iniargs == null)
						params = new String[0];
					else
					{
						List<Element> args = iniargs.elements("arg");
						params = new String[args.size()];
						
						for(int i = 0; i < args.size(); i++)
							params[i] = args.get(i).getTextTrim();
					}
					
					appLogger.info(String.format("register DATABASE SESSION MANAGER '%s (%s)'", name, clazz));
					
					SessionMgr mgr = (SessionMgr)(Class.forName(clazz).newInstance());
					mgr.initialize(params);
					sessionMgrs.put(name, mgr);	
				}
			}
		}
	}

	// ע�� UserConfigParser
	private void loadUserConfig(Element root, Element sys) throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		Element parser = sys.element("user-config-parser");
		if(parser != null)
		{
			String clazz = parser.attributeValue("class");
			if(!GeneralHelper.isStrEmpty(clazz))
			{
				appLogger.info(String.format("register USER CONFIG PARSER '%s'", clazz));
				userCfgParser = (UserConfigParser)(Class.forName(clazz).newInstance());
				
				parseUserConfig(root);
			}
		}
	}

	// �����û�������Ϣ
	private void parseUserConfig(Element root)
	{
		appLogger.info("parse user configuration");
		Element user = root.element("user");
		userCfgParser.parse(user);			
	}

	// ע�� AppLifeCycleListener
	private void loadAppListener(Element sys) throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		Element listener = sys.element("app-life-cycle-listener");
		if(listener != null)
		{
			String clazz = listener.attributeValue("class");
			if(!GeneralHelper.isStrEmpty(clazz))
			{
				appLogger.info(String.format("register APP LIFE CYCLE LISTENER '%s'", clazz));
				appListener = (AppLifeCycleListener)(Class.forName(clazz).newInstance());
			}
		}
	}
	
	static void unInitialize()
	{
		instance.cleanup();
		deregisterDrivers();	
	}

	// ע�����ݿ� Session ������
	private void cleanup()
	{
		Set<Map.Entry<String, SessionMgr>> mgrs = sessionMgrs.entrySet();
		for(Map.Entry<String, SessionMgr> mgr : mgrs)
		{
			appLogger.info(String.format("unregister DATABASE SESSION MANAGER '%s'", mgr.getKey()));
			mgr.getValue().unInitialize();
		}
		
		sessionMgrs		= null;
		userCfgParser	= null;
		appListener		= null;
	}

	// ж�����ݿ���������
	private static void deregisterDrivers()
	{
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while(drivers.hasMoreElements())
		{
			Driver driver = drivers.nextElement();
			try
			{
				appLogger.info(String.format("unregister JDBC DRIVER '%s'", driver));
				DriverManager.deregisterDriver(driver);
			}
			catch(SQLException e)
			{
				LogUtil.exception(e, String.format("unregister JDBC DRIVER '%s' fail", driver), Level.FATAL, true);
			}
		}
	}
	
	/* **************************************************************************************************** */
	//										Reload User Config												//
	/* **************************************************************************************************** */

	/** ��ȡӦ�ó������ö��� {@linkplain AppConfig} ʵ�� */
	public static final AppConfig instance()
	{
		return instance;
	}
	
	/** ���¼���Ӧ�ó��������ļ��� &lt;user&gt; �ڵ�<br>
	 * <ul>
	 * <li>������ֻ���¼��� &lt;user&gt; �ڵ㣬�����¼��� &lt;system&gt; �ڵ㣬
	 * ������������ &lt;system&gt; �ڵ��������Ϣ����������Ӧ�ó������ʹ������Ч</li>
	 * <li>���������ٴε����� &lt;system&gt;/&lt;user-config-parser&gt; �ڵ㶨��� {@linkplain UserConfigParser}
	 * �� {@linkplain UserConfigParser#parse(Element) parse(Element)} �������¼��� &lt;user&gt; �ڵ�</li>
	 * </ul>
	 * 
	 * @param delay			: ִ�����¼��ز�������ʱʱ�䣨���룩��ָ��һ����ʱʱ����Ϊ��ȷ�����е�ǰ���ڴ��������ִ����Ϻ��ִ�����¼��ز���
	 * @throws Exception	: ����ʧ���׳����쳣
	 * 
	 */
	synchronized public void reloadUserConfig(long delay) throws Exception
	{
		if(userCfgParser != null)
		{
			try
			{
				GeneralHelper.waitFor(delay);
				
				Element root = getRootElement();
				parseUserConfig(root);
			}
			catch(Exception e)
			{
				throw e;
			}
		}
	}
}
