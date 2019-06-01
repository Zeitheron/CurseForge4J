package com.zeitheron.curseforge;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zeitheron.curseforge.api.ICurseForge;
import com.zeitheron.curseforge.data.CurseForgePrefs;
import com.zeitheron.curseforge.data.GenericCurseforge;

public class CurseforgeAPI
{
	public static final String API_VERSION = version();
	public static final String CATEGORY_MC_MODPACKS = "modpacks";
	public static final String CATEGORY_MC_CUSTOMIZATION = "customization";
	public static final String CATEGORY_MC_ADDONS = "mc-addons";
	public static final String CATEGORY_MC_MODS = "mc-mods";
	public static final String CATEGORY_MC_TEXTURE_PACKS = "texture-packs";
	public static final String CATEGORY_MC_WORLDS = "worlds";
	
	public static String version()
	{
		return "v1.3.2";
	}
	
	public static ICurseForge minecraft()
	{
		return $.create(GenericCurseforge.class, "minecraft");
	}
	
	public static ICurseForge minecraft(CurseForgePrefs prefs)
	{
		ICurseForge cf = $.create(GenericCurseforge.class, "minecraft");
		cf.preferences().inheritFrom(prefs);
		return cf;
	}
	
	public static List<String> $cptrs(String t, String s, String e)
	{
		List<String> v = new ArrayList<>();
		
		int i;
		while((i = t.indexOf(s)) != -1)
		{
			t = t.substring(i + s.length());
			int ei = t.indexOf(e);
			if(ei != -1)
				v.add(t.substring(0, ei));
		}
		
		return v;
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
	
	public static String $rlnk(String txt)
	{
		if(txt == null || txt.isEmpty() || !txt.contains("<a href=\"/linkout?remoteUrl="))
			return txt;
		
		String cp = txt;
		int i;
		while((i = cp.indexOf("<a href=\"/linkout?remoteUrl=")) != -1)
		{
			String href = cp.substring(i + 28, cp.indexOf("\"", i + 28));
			cp = cp.substring(i + 28 + href.length());
			try
			{
				String valid = href.replaceAll("%253a", ":").replaceAll("%252f", "/");
				txt = txt.replace("/linkout?remoteUrl=" + href, valid);
			} catch(Throwable e)
			{
				e.printStackTrace();
			}
		}
		return txt;
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