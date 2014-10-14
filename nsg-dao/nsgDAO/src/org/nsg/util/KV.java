package org.nsg.util;

public class KV<K extends Object,V extends Object>
{
	private K key;
	private V value;
	
	public KV()
	{
		
	}
	
	public KV(K key, V value)
	{
		this.key = key;
		this.value = value;
	}

	public K getKey()
	{
		return key;
	}

	public void setKey(K key)
	{
		this.key = key;
	}

	public V getValue()
	{
		return value;
	}

	public void setValue(V value)
	{
		this.value = value;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof KV)
		{
			KV<?, ?> other = (KV<?, ?>) obj;
			
			if (key == other.getKey())
				return true;
			if (key != null)
				return key.equals(other.getKey());
		}
		
		return false;
	}

	@Override
	public int hashCode()
	{
		return key != null ? key.hashCode() : 0;
	}

	@Override
	public String toString()
	{
		return String.format("{'%s' = '%s'}",key,value);
	}

	
}

	