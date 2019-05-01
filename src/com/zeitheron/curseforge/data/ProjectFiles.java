package com.zeitheron.curseforge.data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.zeitheron.curseforge.ICurseForge;
import com.zeitheron.curseforge.IProject;
import com.zeitheron.curseforge.fetcher.Fetchable;

public class ProjectFiles
{
	protected final IProject project;
	protected final Fetchable<String> firstPage;
	protected final Fetchable<Integer> pageCount;
	private final Map<Integer, ProjectFilePage> pages = new HashMap<>();
	
	public ProjectFiles(IProject project)
	{
		this.project = project;
		this.firstPage = new Fetchable<>(() -> ICurseForge.getPage(project.url() + "/files", true), 5, TimeUnit.MINUTES);
		this.pageCount = new Fetchable<>(() ->
		{
			int mp = 0;
			String str = this.firstPage.get();
			int i;
			boolean hasFile = str.contains("/files/");
			while((i = str.indexOf("/files?page=")) != -1)
			{
				str = str.substring(i + 12);
				String pi = str.substring(0, str.indexOf('"'));
				mp = Math.max(mp, Integer.parseInt(pi));
			}
			if(mp == 0 && hasFile)
				return 1;
			return mp;
		}, 5, TimeUnit.MINUTES);
	}
	
	public int pageCount()
	{
		return pageCount.get();
	}
	
	public ProjectFilePage page(int id)
	{
		if(pageCount() == 0)
			return null;
		if(id < 1 || id > pageCount())
			throw new IllegalArgumentException("Invalid page index. Must be in range [1;" + pageCount() + "]");
		if(!pages.containsKey(id))
			pages.put(id, new ProjectFilePage(project, id));
		return pages.get(id);
	}
	
	public FetchableFile latest()
	{
		if(pageCount() > 0)
			return page(1).files().get().get(0);
		return null;
	}
	
	public IProject project()
	{
		return project;
	}
}