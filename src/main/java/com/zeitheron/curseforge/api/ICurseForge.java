package com.zeitheron.curseforge.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.zeitheron.curseforge.api.threading.ICursedExecutor;
import com.zeitheron.curseforge.data.CurseForgePrefs;
import com.zeitheron.curseforge.data.InternalCFA;
import com.zeitheron.curseforge.data.project.FetchableProject;
import com.zeitheron.curseforge.data.utils.Fetchable;

public interface ICurseForge
{
	Fetchable<String> projectIdByLID(long lid);
	
	Fetchable<IProject> project(String slug);
	
	default Fetchable<IProject> project(long lid)
	{
		return project(projectIdByLID(lid).get());
	}
	
	Fetchable<IMember> member(String member);
	
	CurseForgePrefs preferences();
	
	ISearchResult<FetchableProject> searchProjects(CurseSearchDetails details, String query);
	
	<T> Fetchable<T> createFetchable(Supplier<T> get);
	
	String url();
	
	ICursedExecutor executor();
	
	IGame gameById(String id);
	
	Fetchable<List<IGame>> allGames();
	
	// Fetchable<List<IGameVersion>> gameVersions();
	
	static void setDebug(PrintStream ps)
	{
		data.debug = ps;
	}
	
	static String getPage(String url, boolean format)
	{
		return getPage(url, format, null);
	}
	
	static String getPage(String url, boolean format, Consumer<String> inp)
	{
		if(data.debug != null)
			data.debug.println("GET " + url);
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
			if(inp != null)
				inp.accept(urlc.getURL().toString());
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
			return InternalCFA.$ets(e);
		}
	}
	
	static String getPath(String url)
	{
		if(data.debug != null)
			data.debug.println("GET PATH " + url);
		try
		{
			HttpURLConnection urlc = (HttpURLConnection) new URL(url).openConnection();
			urlc.setRequestProperty("User-Agent", "Zeitheron JCF");
			urlc.setInstanceFollowRedirects(true);
			urlc.connect();
			return urlc.getURL().toString();
		} catch(IOException e)
		{
			return InternalCFA.$ets(e);
		}
	}
	
	static class data
	{
		static PrintStream debug;
	}
}