package com.zeitheron.curseforge.data.utils;

import java.util.HashMap;
import java.util.function.Function;

@SuppressWarnings("serial")
class DefaultableMap<K, V> extends HashMap<K, V>
{
	final Function<K, V> defVal;
	
	DefaultableMap(Function<K, V> defVal)
	{
		this.defVal = defVal;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key)
	{
		if(!containsKey(key))
			put((K) key, defVal.apply((K) key));
		return super.get(key);
	}
}