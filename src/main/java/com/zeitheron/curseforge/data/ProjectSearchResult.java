package com.zeitheron.curseforge.data;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.zeitheron.curseforge.CurseforgeAPI;
import com.zeitheron.curseforge.api.ICurseForge;
import com.zeitheron.curseforge.api.ISearchResult;

public class ProjectSearchResult implements ISearchResult<FetchableProject>
{
	protected final Fetchable<List<FetchableProject>> projects;
	protected final ICurseForge cf;
	protected final String query, category;
	protected final int page;
	
	protected Map<Integer, ProjectSearchResult> inheritance;
	
	public ProjectSearchResult(ICurseForge cf, int page, String category, String query)
	{
		this.cf = cf;
		this.query = query;
		this.category = category;
		this.page = page;
		this.projects = cf.createFetchable(generator(cf, page, category, query));
		setInheritance(new HashMap<>());
	}
	
	private ProjectSearchResult(ICurseForge cf, int page, String category, String query, Map<Integer, ProjectSearchResult> inheritance)
	{
		this.cf = cf;
		this.query = query;
		this.category = category;
		this.page = page;
		this.projects = cf.createFetchable(generator(cf, page, category, query));
		setInheritance(inheritance);
	}
	
	private ProjectSearchResult setInheritance(Map<Integer, ProjectSearchResult> inheritance)
	{
		this.inheritance = inheritance;
		inheritance.put(page, this);
		return this;
	}
	
	@Override
	public int page()
	{
		return page;
	}
	
	@Override
	public String query()
	{
		return query;
	}
	
	@Override
	public List<FetchableProject> getElements()
	{
		return projects.get();
	}
	
	@Override
	public ISearchResult<FetchableProject> nextPage()
	{
		if(inheritance.containsKey(page + 1))
			return inheritance.get(page + 1);
		return new ProjectSearchResult(cf, page + 1, category, query, inheritance);
	}
	
	@Override
	public ISearchResult<FetchableProject> prevPage()
	{
		if(inheritance.containsKey(page - 1))
			return inheritance.get(page - 1);
		return new ProjectSearchResult(cf, Math.max(1, page - 1), category, query, inheritance);
	}
	
	@Override
	public ICurseForge curseForge()
	{
		return cf;
	}
	
	static Supplier<List<FetchableProject>> generator(ICurseForge cf, int page, String category, String query)
	{
		return () ->
		{
			List<FetchableProject> projects = new ArrayList<>();
			
			try
			{
				String base = cf.url() + category + "/search?search=" + URLEncoder.encode(query, "UTF-8") + "&projects-page=" + page;
				String pg = ICurseForge.getPage(base, true);
				
				for(String item : CurseforgeAPI.$cptrs(pg, "<div class=\"project-avatar project-avatar-64\">", "</a></div></div></div></div></div>"))
				{
					String slug = CurseforgeAPI.$cptr(item, "<a href=\"/", "\"");
					String avatar = CurseforgeAPI.$cptr(item, "<img src=\"", "\"");
					String name = CurseforgeAPI.$cptr(item, "<h3 class=\"text-primary-500 font-bold text-lg hover:no-underline\">", "</h3>");
					String author = CurseforgeAPI.$cptr(item, "<span>By</span>&nbsp;<a href=\"/members/", "\"");
					
					projects.add(new FetchableProject(name, slug, avatar, cf, author));
				}
			} catch(UnsupportedEncodingException e)
			{
			}
			
			return projects;
		};
	}
}