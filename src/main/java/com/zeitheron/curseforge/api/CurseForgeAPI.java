package com.zeitheron.curseforge.api;

import com.zeitheron.curseforge.data.CurseForgePrefs;
import com.zeitheron.curseforge.data.InternalCFA;

public class CurseForgeAPI
{
	public static final String API_VERSION = version();
	
	public static String version()
	{
		return InternalCFA.version();
	}
	
	public static ICurseForge create(CurseForgePrefs prefs)
	{
		return InternalCFA.www(prefs);
	}
}