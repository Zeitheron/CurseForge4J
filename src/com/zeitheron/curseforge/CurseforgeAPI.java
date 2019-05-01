package com.zeitheron.curseforge;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.zeitheron.curseforge.base.GenericCurseforge;

public class CurseforgeAPI
{
	public static final Map<String, ICurseForge> wrappers = new HashMap<>();
	
	static
	{
		wrappers.put("minecraft", $.create(GenericCurseforge.class, "minecraft"));
		wrappers.put("terraria", $.create(GenericCurseforge.class, "terraria"));
	}
	
	public static ICurseForge getCurseForge(String game)
	{
		return wrappers.get(game);
	}
	
	public static ICurseForge minecraft()
	{
		return getCurseForge("minecraft");
	}
	
	public static ICurseForge terraria()
	{
		return getCurseForge("terraria");
	}
	
	public static String $cptr(String t, String s, String e)
	{
		int is = t.indexOf(s);
		if(is < 0)
			return null;
		is += s.length();
		int ie = t.indexOf(e, is);
		if(ie < 0)
			return null;
		return t.substring(is, ie);
	}
	
	public static String $cfidg(String sub)
	{
		return "https://" + (sub == null || sub.isEmpty() ? "" : sub + ".") + "curseforge.com/";
	}
	
	public static String $ets(Throwable e)
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(out);
		e.printStackTrace(pw);
		pw.close();
		return out.toString();
	}
	
	public static Date $abbr(String v)
	{
		if(v != null)
		{
			int i = v.indexOf("data-epoch=\"") + 12;
			int e = v.indexOf("\"", i);
			if(e > 0)
				return Date.from(Instant.ofEpochSecond(Long.parseLong(v.substring(i, e))));
			return Date.from(Instant.ofEpochSecond(Long.parseLong(v.substring(i))));
		}
		return null;
	}
	
	private static class $
	{
		@SuppressWarnings({ "rawtypes", "unchecked" })
		static boolean match(Class[] cls, Object[] vls)
		{
			if(cls.length != vls.length)
				return false;
			
			for(int i = 0; i < cls.length; ++i)
			{
				Class c = cls[i];
				Object v = vls[i];
				
				if((v == null && !c.isPrimitive()) || (v != null && c.isAssignableFrom(v.getClass())))
					continue;
				else
					return false;
			}
			
			return true;
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		static <T> T create(Class<T> type, Object... pars)
		{
			try
			{
				Constructor<T> ctr = null;
				for(Constructor c : type.getDeclaredConstructors())
				{
					Class<?>[] ps = c.getParameterTypes();
					if(match(ps, pars))
					{
						ctr = c;
						break;
					}
				}
				if(ctr == null)
					return null;
				ctr.setAccessible(true);
				return ctr.newInstance(pars);
			} catch(ReflectiveOperationException roe)
			{
				return null;
			}
		}
	}
}