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
	protected final String query;
	protected final int page;
	
	protected Map<Integer, ProjectSearchResult> inheritance;
	
	public ProjectSearchResult(ICurseForge cf, int page, String query)
	{
		this.cf = cf;
		this.query = query;
		this.page = page;
		this.projects = new Fetchable<>(generator(cf, page, query), cf.preferences().getCacheLifespan().getVal(), cf.preferences().getCacheLifespan().getUnit());
		setInheritance(new HashMap<>());
	}
	
	private ProjectSearchResult(ICurseForge cf, int page, String query, Map<Integer, ProjectSearchResult> inheritance)
	{
		this.cf = cf;
		this.query = query;
		this.page = page;
		this.projects = new Fetchable<>(generator(cf, page, query), cf.preferences().getCacheLifespan().getVal(), cf.preferences().getCacheLifespan().getUnit());
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
		return new ProjectSearchResult(cf, page + 1, query, inheritance);
	}
	
	@Override
	public ISearchResult<FetchableProject> prevPage()
	{
		if(inheritance.containsKey(page - 1))
			return inheritance.get(page - 1);
		return new ProjectSearchResult(cf, Math.max(1, page - 1), query, inheritance);
	}
	
	@Override
	public ICurseForge curseForge()
	{
		return cf;
	}
	
	static Supplier<List<FetchableProject>> generator(ICurseForge cf, int page, String query)
	{
		return () ->
		{
			List<FetchableProject> projects = new ArrayList<>();
			
			try
			{
				String base = CurseforgeAPI.$cfidg(cf.game()) + "search?search=" + URLEncoder.encode(query, "UTF-8") + "&projects-page=" + page;
				String pg = ICurseForge.getPage(base, true);
				
				List<String> trs = CurseforgeAPI.$cptrs(pg, "<tr class=\"results\">", "</tr>");
				for(String tr : trs)
				{
					String id = CurseforgeAPI.$cptr(tr, "projectID=", "\"");
					String name = CurseforgeAPI.$cptr(tr.substring(tr.lastIndexOf(id) - 20), "projectID=" + id + "\">", "</a>");
					projects.add(new FetchableProject(name, id, cf));
				}
			} catch(UnsupportedEncodingException e)
			{
			}
			
			return projects;
		};
	}
}