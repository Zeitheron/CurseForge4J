package com.zeitheron.curseforge;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.zeitheron.curseforge.data.CurseForgePrefs;
import com.zeitheron.curseforge.fetcher.Fetchable;

public interface ICurseForge
{
	Fetchable<IProject> project(String slug);
	
	default Fetchable<IProject> project(long project)
	{
		return project(project + "");
	}
	
	Fetchable<IMember> member(String member);
	
	String game();
	
	static String getPage(String url, boolean format)
	{
		try
		{
			HttpURLConnection urlc = (HttpURLConnection) new URL(url).openConnection();
			urlc.setRequestProperty("User-Agent", "Zeitheron JCF");
			urlc.connect();
			InputStream in = urlc.getInputStream();
			StringBuilder reader = new StringBuilder();
			byte[] buf = new byte[128];
			int read = 0;
			while((read = in.read(buf)) > 0)
				reader.append(new String(buf, 0, read));
			in.close();
			if(format)
			{
				String str = reader.toString();
				str = str.replaceAll("\t", " ");
				while(str.contains("  "))
					str = str.replaceAll("  ", "");
				str = str.replaceAll("\r", "").replaceAll("\n", "");
				return str.trim();
			}
			return reader.toString();
		} catch(IOException e)
		{
			return CurseforgeAPI.$ets(e);
		}
	}
	
	CurseForgePrefs preferences();
}