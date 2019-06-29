package com.zeitheron.curseforge.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.zeitheron.curseforge.CurseforgeAPI;
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
		this.firstPage = cf.createFetchable(() -> ICurseForge.getPage(project.url() + "/files/all", true));
		this.pageCount = cf.createFetchable(() ->
		{
			String str = this.firstPage.get();
			boolean hasFile = str.contains("/download");
			List<Integer> ints = CurseforgeAPI.$cptrs(str, "/files/all?page=", "\"").stream().map(Integer::parseInt).collect(Collectors.toList());
			ints.sort((a, b) -> b - a);
			if(ints.isEmpty() && hasFile)
				return 1;
			return ints.isEmpty() ? 0 : ints.get(0);
		});
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
	
	public FetchableFile latestNow()
	{
		if(pageCount() > 0)
			return page(1).files().fetchNow().get(0);
		return null;
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