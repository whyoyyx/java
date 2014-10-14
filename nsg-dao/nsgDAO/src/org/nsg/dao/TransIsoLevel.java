package org.nsg.dao;

import java.security.InvalidParameterException;
import java.sql.Connection;

/** ������뼶�� */
public enum TransIsoLevel
{
	/** ���ݿ�����Ĭ��������뼶�� */
	DEFAULT
	{
		/** ����: 0 */
		@Override
		public int toInt()
		{
			return Connection.TRANSACTION_NONE;
		}
	},
	/** ��������������ظ����ͻö� */
	READ_UNCOMMITTED
	{
		/** ����: 1 */
		@Override
		public int toInt()
		{
			return Connection.TRANSACTION_READ_UNCOMMITTED;
		}
	},
	/** ��ֹ��������������ظ����ͻö� */
	READ_COMMITTED
	{
		/** ����: 2 */
		@Override
		public int toInt()
		{
			return Connection.TRANSACTION_READ_COMMITTED;
		}
	},
	/** ��ֹ����Ͳ����ظ�����������ö� */
	REPEATABLE_READ
	{
		/** ����: 4 */
		@Override
		public int toInt()
		{
			return Connection.TRANSACTION_REPEATABLE_READ;
		}
	},
	/** ��ֹ����������ظ����ͻö� */
	SERIALIZABLE
	{
		/** ����: 8 */
		@Override
		public int toInt()
		{
			return Connection.TRANSACTION_SERIALIZABLE;
		}
	};

	/** ������뼶��ö�ٶ���ת��Ϊ����ֵ */
	public abstract int toInt();

	/** ����ֵת��Ϊ������뼶��ö�ٶ���
	 * 
	 * @param level			<br>			: 0 - {@link TransIsoLevel#DEFAULT}<br>
	 * 										: 1 - {@link TransIsoLevel#READ_UNCOMMITTED}<br>
	 * 										: 2 - {@link TransIsoLevel#READ_COMMITTED}<br>
	 * 										: 4 - {@link TransIsoLevel#REPEATABLE_READ}<br>
	 * 										: 8 - {@link TransIsoLevel#SERIALIZABLE}<br>
	 * @throws InvalidParameterException	: �Ҳ�����Ӧ��������뼶��ö�ٶ���ʱ�׳��쳣
	 *  
	 */
	public static final TransIsoLevel fromInt(int level)
	{
		for(TransIsoLevel lv : TransIsoLevel.values())
		{
			if(lv.toInt() == level)
				return lv;
		}
		
		throw new InvalidParameterException("invalid paramer value");
	}
}
