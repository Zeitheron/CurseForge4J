package com.zeitheron.curseforge.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.function.Supplier;

import com.zeitheron.curseforge.CurseforgeAPI;
import com.zeitheron.curseforge.data.CurseForgePrefs;
import com.zeitheron.curseforge.data.Fetchable;
import com.zeitheron.curseforge.data.FetchableProject;

public interface ICurseForge
{
	Fetchable<IProject> project(String slug);
	
	default Fetchable<IProject> project(long project)
	{
		return project(project + "");
	}
	
	Fetchable<IMember> member(String member);
	
	String game();
	
	CurseForgePrefs preferences();
	
	ISearchResult<FetchableProject> searchProjects(String query);
	
	default IProjectList listCategory(String cat)
	{
		return listCategory(cat, EnumSortRule.POPULARITY);
	}
	
	default IProjectList listCategory(String cat, EnumSortRule sort)
	{
		return listCategory(cat, sort, null);
	}
	
	IProjectList listCategory(String cat, EnumSortRule sort, IGameVersion version);
	
	<T> Fetchable<T> createFetchable(Supplier<T> get);
	
	String url();
	
	Fetchable<List<String>> rootCategories();
	
	Fetchable<List<IGameVersion>> gameVersions();
	
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
}