package org.nsg.dao;

/**
 * 
 * Facade 抽象基类
 *
 * @param <M>	: {@link SessionMgr}
 * @param <S>	: 数据库连接类型
 */
public abstract class AbstractFacade<M extends SessionMgr<S>, S>
{
	private M manager;
	
	protected AbstractFacade(M mgr)
	{
		manager = mgr;
	}
	
	/** 获取关联的 {@link SessionMgr} */
	public M getManager()
	{
		return manager;
	}
	
	/** 获取数据库连接 */
	protected S getSession()
	{
		return manager.getSession();
	}
}
