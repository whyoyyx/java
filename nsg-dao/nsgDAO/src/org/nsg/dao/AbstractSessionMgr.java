package org.nsg.dao;

/**
 * 
 * SessionMgr ������࣬ʵ�� {@link SessionMgr} �ӿ�
 *
 * @param <S>	: ���ݿ���������
 */
public abstract class AbstractSessionMgr<S> implements SessionMgr<S>
{
	/** �����ļ� */
	protected String configFile;
	/** Ĭ��������뼶�� */
	protected TransIsoLevel defaultTransIsoLevel;
	/** �ֲ߳̾� Session ���� */
	protected final ThreadLocal<S> localSession = new ThreadLocal<S>();
	/** SessionMgr �ֲ߳̾�ִ��״̬ */
	private ThreadLocal<Boolean> invoking = new ThreadLocal<Boolean>();
	
	/** ���� {@link SessionMgr} ��Ĭ��������뼶�� */
	abstract protected void loadDefalutTransIsoLevel();
	
	/** �ο���{@link SessionMgr#unInitialize()} */
	@Override
	public void unInitialize()
	{
		localSession.remove();
		invoking.remove();
	}
	
	/** �ο���{@link SessionMgr#getDefalutTransIsoLevel()} */
	@Override
	public TransIsoLevel getDefalutTransIsoLevel()
	{
		return defaultTransIsoLevel;
	}

	/** �ο���{@link SessionMgr#currentSession()} */
	@Override
	public S currentSession()
	{
		return localSession.get();
	}
	
	/** �ο���{@link SessionMgr#getConfigFile()} */
	@Override
	public String getConfigFile()
	{
		return configFile;
	}
	
	/** �ο���{@link SessionMgr#isInvoking()} */
	@Override
	public boolean isInvoking()
	{
		return Boolean.TRUE == invoking.get();
	}
	
	/** �ο���{@link SessionMgr#setInvoking(boolean)} */
	@Override
	public void setInvoking(boolean value)
	{
		invoking.set(Boolean.valueOf(value));
	}
}
