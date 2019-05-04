package com.zeitheron.curseforge.data;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;

class ToStringHelper
{
	public static String toString(Object obj)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(obj.getClass().getSimpleName());
		sb.append('{');
		for(Field f : obj.getClass().getDeclaredFields())
			if(f.getAnnotation(Ignore.class) == null)
				try
				{
					String name = f.getName();
					ActualName an = f.getAnnotation(ActualName.class);
					if(an != null)
						name = an.value();
					f.setAccessible(true);
					Object v = f.get(obj);
					sb.append(name + '=');
					sb.append(v + ",");
				} catch(Throwable err)
				{
					sb.append(f.getName() + "=<GET_ERR>,");
				}
		if(sb.charAt(sb.length() - 1) == ',')
			sb.deleteCharAt(sb.length() - 1);
		sb.append('}');
		return sb.toString();
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Ignore
	{
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface ActualName
	{
		String value();
	}
}