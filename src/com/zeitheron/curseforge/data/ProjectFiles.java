package com.zeitheron.curseforge.data;

import java.util.HashMap;
import java.util.Map;

import com.zeitheron.curseforge.api.ICurseForge;
import com.zeitheron.curseforge.api.IProject;
import com.zeitheron.curseforge.data.ToStringHelper.Ignore;

public class ProjectFiles
{
	protected final IProject project;
	@Ignore
	protected final Fetchable<String> firstPage;
	@Ignore
	protected final Fetchable<Integer> pageCount;
	private final Map<Integer, ProjectFilePage> pages = new HashMap<>();
	
	public ProjectFiles(IProject project)
	{
		this.project = project;
		ICurseForge cf = project.curseForge();
		this.firstPage = new Fetchable<>(() -> ICurseForge.getPage(project.url() + "/files", true), cf.preferences().getCacheLifespan().getVal(), cf.preferences().getCacheLifespan().getUnit());
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
		}, cf.preferences().getCacheLifespan().getVal(), cf.preferences().getCacheLifespan().getUnit());
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
	
	@Override
	public String toString()
	{
		return ToStringHelper.toString(this);
	}
}