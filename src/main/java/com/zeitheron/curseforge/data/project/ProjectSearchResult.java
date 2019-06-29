package com.zeitheron.curseforge.data.project;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.zeitheron.curseforge.api.CurseSearchDetails;
import com.zeitheron.curseforge.api.ICurseForge;
import com.zeitheron.curseforge.api.IGameCategory;
import com.zeitheron.curseforge.api.ISearchResult;
import com.zeitheron.curseforge.data.InternalCFA;
import com.zeitheron.curseforge.data.utils.Fetchable;

public class ProjectSearchResult implements ISearchResult<FetchableProject>
{
	protected final Fetchable<List<FetchableProject>> projects;
	protected final ICurseForge cf;
	protected final String query;
	protected final CurseSearchDetails details;
	protected final int page;
	
	protected Map<Integer, ProjectSearchResult> inheritance;
	
	public ProjectSearchResult(ICurseForge cf, int page, CurseSearchDetails details, String query)
	{
		this.cf = cf;
		this.query = query;
		this.details = details;
		this.page = page;
		this.projects = cf.createFetchable(generator(cf, page, details, query));
		setInheritance(new HashMap<>());
	}
	
	private ProjectSearchResult(ICurseForge cf, int page, CurseSearchDetails details, String query, Map<Integer, ProjectSearchResult> inheritance)
	{
		this.cf = cf;
		this.query = query;
		this.details = details;
		this.page = page;
		this.projects = cf.createFetchable(generator(cf, page, details, query));
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
		return new ProjectSearchResult(cf, page + 1, details, query, inheritance);
	}
	
	@Override
	public ISearchResult<FetchableProject> prevPage()
	{
		if(inheritance.containsKey(page - 1))
			return inheritance.get(page - 1);
		return new ProjectSearchResult(cf, Math.max(1, page - 1), details, query, inheritance);
	}
	
	@Override
	public ICurseForge curseForge()
	{
		return cf;
	}
	
	static Supplier<List<FetchableProject>> generator(ICurseForge cf, int page, CurseSearchDetails details, String query)
	{
		return () ->
		{
			List<FetchableProject> projects = new ArrayList<>();
			List<String> categories = new ArrayList<>();
			if(details.category.wildcard())
				for(IGameCategory cat : details.game.categories().get())
					categories.add(cat.id());
			else
				categories.add(details.category.id());
			cf.executor().fetchAndWaitForAllCall(categories.stream().map(cat -> (Callable<List<FetchableProject>>) () ->
			{
				List<FetchableProject> pi = new ArrayList<>();
				String base = cf.url() + details.game.id() + "/" + cat + "/search?search=" + URLEncoder.encode(query, "UTF-8") + "&projects-page=" + page;
				String pg = ICurseForge.getPage(base, true);
				for(String item : InternalCFA.$cptrs(pg, "<div class=\"project-avatar project-avatar-64\">", "</a></div></div></div></div></div>"))
				{
					String slug = InternalCFA.$cptr(item, "<a href=\"/", "\"");
					String avatar = InternalCFA.$cptr(item, "<img src=\"", "\"");
					String name = InternalCFA.$cptr(item, "<h3 class=\"text-primary-500 font-bold text-lg hover:no-underline\">", "</h3>");
					String author = InternalCFA.$cptr(item, "<span>By</span>&nbsp;<a href=\"/members/", "\"");
					pi.add(new FetchableProject(name, slug, avatar, cf, author));
				}
				return pi;
			}).collect(Collectors.toList())).forEach(l -> projects.addAll(l));
			return projects;
		};
	}
}