package com.zeitheron.curseforge.api;

import java.util.List;

import com.zeitheron.curseforge.data.Fetchable;
import com.zeitheron.curseforge.data.FetchableProject;

public interface IProjectList
{
	String category();
	
	int page();
	
	IGameVersion version();
	
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