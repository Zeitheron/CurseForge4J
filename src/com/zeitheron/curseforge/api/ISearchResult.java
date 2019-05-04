package com.zeitheron.curseforge.api;

import java.util.List;

public interface ISearchResult<STH>
{
	int page();
	
	String query();
	
	List<STH> getElements();
	
	ISearchResult<STH> nextPage();
	
	ISearchResult<STH> prevPage();
	
	ICurseForge curseForge();
}