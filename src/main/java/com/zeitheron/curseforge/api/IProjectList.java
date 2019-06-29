package com.zeitheron.curseforge.api;

import java.util.List;

import com.zeitheron.curseforge.data.project.FetchableProject;
import com.zeitheron.curseforge.data.utils.Fetchable;

public interface IProjectList
{
	String category();
	
	int page();
	
	EnumSortRule sorting();
	
	IProjectList sorting(EnumSortRule rule);
	
	Fetchable<List<FetchableProject>> projects();
	
	IProjectList page(int page);
	
	default IProjectList nextPage()
	{
		return page(page() + 1);
	}
	
	default IProjectList prevPage()
	{
		return page(Math.max(1, page() - 1));
	}
	
	ICurseForge curseForge();
}