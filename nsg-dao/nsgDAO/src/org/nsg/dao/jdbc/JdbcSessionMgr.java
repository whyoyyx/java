package org.nsg.dao.jdbc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.nsg.util.GeneralHelper;

/**
 * 
 * JDBC Session ������
 *
 */
public class JdbcSessionMgr extends AbstractJdbcSessionMgr
{
	/** JDBC Ĭ�������ļ� */
	public static final String DEFAULT_CONFIG_FILE	= "jdbc.cfg.xml";
	
	private static final String PROP_DRIVER			= "driver";
	private static final String PROP_URL			= "url";
	
	private Properties info;
	private String url;
	
	@Override
	protected String getDefaultConfigFile()
	{
		return DEFAULT_CONFIG_FILE;
	}
	
	/** 
	 * ��ʼ�� 
	 * 
	 * @param args <br>
	 * 			[0]	: configFile ��Ĭ�ϣ�{@link JdbcSessionMgr#DEFAULT_CONFIG_FILE}�� <br>
	 * @throws InvalidParameterException
	 * @throws JdbcException
	 * 
	*/
	@Override
	public void initialize(String ... args)
	{
		if(args.length == 0)
			initialize();
		else if(args.length == 1)
			initialize(args[0]);
		else
			throw new InvalidParameterException("JdbcSessionMgr initialize fail (invalid paramers)");
	}
	
	/** ��ʼ�� */
	public void initialize()
	{
		initialize(DEFAULT_CONFIG_FILE);
	}
	
	/** ��ʼ�� */
	public void initialize(String configFile)
	{
		try
		{
			parseConfigFile(configFile);
			
			Properties props = new Properties();
			
			if(isXmlConfigFile())
    			loadXmlCfg(props);
    		else
    			loadPropCfg(props);
			
			info = filtrateCfg(props);

			loadDefalutTransIsoLevel();
		}
		catch(Exception e)
		{
			try {unInitialize();} catch(Exception e2) {}
			throw new JdbcException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadXmlCfg(Properties props) throws Exception
	{
		SAXReader sr	= new SAXReader();
		Document doc	= sr.read(new File(configFile));
		Element root	= doc.getRootElement();
		
		List<Element> list = root.elements("property");
		for(Element e : list)
		{
			String key		= e.attributeValue("name");
			String value	= e.getTextTrim();
			
			props.put(key, value);
		}
	}

	private void loadPropCfg(Properties props) throws IOException
	{
		props.load(new FileInputStream(configFile));
	}

	private Properties filtrateCfg(Properties props)
	{
		Properties result				= new Properties();
		Set<Entry<Object, Object>> set	= props.entrySet();
		
		for(Entry<Object, Object> e : set)
		{
			String key	 = GeneralHelper.safeTrimString((String)e.getKey());
			String value = GeneralHelper.safeTrimString((String)e.getValue());
			if(GeneralHelper.isStrNotEmpty(value))
			{
				if(key.equals(PROP_DRIVER))
					loadDriverClass(value);
				else if(key.equals(PROP_URL))
					url = value;
				else
					result.put(e.getKey(), value);
			}
		}

		return result;
	}

	private void loadDriverClass(String driverClass)
	{
		try
		{
			Class.forName(driverClass);
		}
		catch(ClassNotFoundException e)
		{
			try
			{
				Class.forName(driverClass, true, Thread.currentThread().getContextClassLoader());
			}
			catch(ClassNotFoundException e1)
			{
				throw new JdbcException(e);
			}
		}
	}

	/** ע�� */
	@Override
	public void unInitialize()
	{
		super.unInitialize();
	}
	
	/** ��ȡ���ݿ����Ӷ��� 
	 * @throws SQLException */
	@Override
	protected Connection getInternalConnection() throws SQLException
	{
		return DriverManager.getConnection(url, info);
	}
}
