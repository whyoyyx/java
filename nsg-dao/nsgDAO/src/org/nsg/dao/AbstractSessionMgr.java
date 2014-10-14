package org.nsg.dao;

/**
 * 
 * SessionMgr 抽象基类，实现 {@link SessionMgr} 接口
 *
 * @param <S>	: 数据库连接类型
 */
public abstract class AbstractSessionMgr<S> implements SessionMgr<S>
{
	/** 配置文件 */
	protected String configFile;
	/** 默认事务隔离级别 */
	protected TransIsoLevel defaultTransIsoLevel;
	/** 线程局部 Session 对象 */
	protected final ThreadLocal<S> localSession = new ThreadLocal<S>();
	/** SessionMgr 线程局部执行状态 */
	private ThreadLocal<Boolean> invoking = new ThreadLocal<Boolean>();
	
	/** 加载 {@link SessionMgr} 的默认事务隔离级别 */
	abstract protected void loadDefalutTransIsoLevel();
	
	/** 参考：{@link SessionMgr#unInitialize()} */
	@Override
	public void unInitialize()
	{
		localSession.remove();
		invoking.remove();
	}
	
	/** 参考：{@link SessionMgr#getDefalutTransIsoLevel()} */
	@Override
	public TransIsoLevel getDefalutTransIsoLevel()
	{
		return defaultTransIsoLevel;
	}

	/** 参考：{@link SessionMgr#currentSession()} */
	@Override
	public S currentSession()
	{
		return localSession.get();
	}
	
	/** 参考：{@link SessionMgr#getConfigFile()} */
	@Override
	public String getConfigFile()
	{
		return configFile;
	}
	
	/** 参考：{@link SessionMgr#isInvoking()} */
	@Override
	public boolean isInvoking()
	{
		return Boolean.TRUE == invoking.get();
	}
	
	/** 参考：{@link SessionMgr#setInvoking(boolean)} */
	@Override
	public void setInvoking(boolean value)
	{
		invoking.set(Boolean.valueOf(value));
	}
}
