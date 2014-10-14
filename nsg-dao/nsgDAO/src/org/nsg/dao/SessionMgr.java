package org.nsg.dao;

/**
 * 
 * Session ������
 *
 * @param <S>	: ���ݿ���������
 */
public interface SessionMgr<S>
{
	/**
	 * 
	 * ��ʼ��
	 * 
	 * @param args	: �Զ����ʼ����
	*/
	void initialize(String ... args);
	/** ע�� */
	void unInitialize();
	/** ��ʼ���� */
	void beginTransaction();
	/** �ύ���� */
	void commit();
	/** �ع����� */
	void rollback();
	/** �ر����ݿ����Ӷ��� */
	void closeSession();
	/** ��ȡ���ݿ����Ӷ�������������򴴽��������¶��� */
	S getSession();
	/** ��ȡ���ݿ����Ӷ�������������򷵻� null */
	S currentSession();
	/** ��ȡ��ǰ�����ļ� */
	String getConfigFile();
	/** ��ȡ {@link SessionMgr} ��Ĭ��������뼶�� */
	TransIsoLevel getDefalutTransIsoLevel();
	/** ���õ�ǰ�߳���ص����ݿ����Ӷ�������񼶱� */
	void setSessionTransIsoLevel(final TransIsoLevel level);
	/** ��� {@link SessionMgr} �Ƿ�����ִ�� */
	boolean isInvoking();
	/** ���� {@link SessionMgr} ��ִ��״̬ */
	void setInvoking(boolean value);
}
