package org.nsg.dao;

/**
 * 
 * Facade �������
 *
 * @param <M>	: {@link SessionMgr}
 * @param <S>	: ���ݿ���������
 */
public abstract class AbstractFacade<M extends SessionMgr<S>, S>
{
	private M manager;
	
	protected AbstractFacade(M mgr)
	{
		manager = mgr;
	}
	
	/** ��ȡ������ {@link SessionMgr} */
	public M getManager()
	{
		return manager;
	}
	
	/** ��ȡ���ݿ����� */
	protected S getSession()
	{
		return manager.getSession();
	}
}
