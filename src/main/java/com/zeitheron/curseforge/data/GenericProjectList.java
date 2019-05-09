package com.zeitheron.curseforge.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zeitheron.curseforge.CurseforgeAPI;
import com.zeitheron.curseforge.api.EnumSortRule;
import com.zeitheron.curseforge.api.ICurseForge;
import com.zeitheron.curseforge.api.IGameVersion;
import com.zeitheron.curseforge.api.IProjectList;

class GenericProjectList implements IProjectList
{
	final String category;
	final int page;
	final EnumSortRule sort;
	final Map<String, HashMap<Integer, GenericProjectList>> pages;
	final ICurseForge cf;
	final Fetchable<List<FetchableProject>> fetcher;
	final IGameVersion version;
	
	GenericProjectList(String cat, int page, EnumSortRule sort, Map<String, HashMap<Integer, GenericProjectList>> pages, ICurseForge cf, IGameVersion version)
	{
		this.category = cat;
		this.page = page;
		this.sort = sort;
		this.pages = pages;
		this.cf = cf;
		this.version = version;
		this.fetcher = cf.createFetchable(() ->
		{
			String versionQuery = "";
			if(version != IGameVersion.NULL)
				versionQuery = "&filter-game-version=" + version;
			List<FetchableProject> ps = new ArrayList<>();
			String url = cf.url() + cat + "?filter-sort=" + sort.getId() + "&page=" + page + versionQuery;
			String html = ICurseForge.getPage(url, true);
			for(String item : CurseforgeAPI.$cptrs(html, "<li class=\"project-list-item\">", "</div></div></li>"))
			{
				String name = CurseforgeAPI.$cptr(item, "<div class=\"name-wrapper overflow-tip\"><a href=\"/projects/", "</a></div>");
				String[] idName = name.split("\">");
				ps.add(new FetchableProject(StringUtils.unescapeHtml3(idName[1]), idName[0], cf));
			}
			return Collections.unmodifiableList(ps);
		});
	}
	
	@Override
	public String category()
	{
		return category;
	}
	
	@Override
	public int page()
	{
		return page;
	}
	
	@Override
	public Fetchable<List<FetchableProject>> projects()
	{
		return fetcher;
	}
	
	@Override
	public IProjectList page(int page)
	{
		Map<Integer, GenericProjectList> ps = pages.get(version().toString());
		if(!ps.containsKey(page))
			ps.put(page, new GenericProjectList(category, page, sort, pages, cf, version));
		return ps.get(page);
	}
	
	@Override
	public EnumSortRule sorting()
	{
		return sort;
	}
	
	@Override
	public IProjectList sorting(EnumSortRule rule)
	{
		return cf.listCategory(category, rule).page(page);
	}
	
	@Override
	public ICurseForge curseForge()
	{
		return cf;
	}
	
	@Override
	public IGameVersion version()
	{
		return version;
	}
}