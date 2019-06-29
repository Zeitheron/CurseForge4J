package com.zeitheron.curseforge.data.project;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.zeitheron.curseforge.api.ICurseForge;
import com.zeitheron.curseforge.api.IProject;
import com.zeitheron.curseforge.data.InternalCFA;
import com.zeitheron.curseforge.data.utils.Fetchable;
import com.zeitheron.curseforge.data.utils.ToStringHelper;
import com.zeitheron.curseforge.data.utils.ToStringHelper.Ignore;

public class ProjectFilePage
{
	protected final IProject project;
	protected final int page;
	@Ignore
	protected final Fetchable<List<FetchableFile>> files;
	
	ProjectFilePage(IProject project, int page)
	{
		this.project = project;
		this.page = page;
		ICurseForge cf = project.curseForge();
		this.files = cf.createFetchable(() ->
		{
			String pg;
			if(page == 1)
				pg = project.files().firstPage.get();
			else
				pg = ICurseForge.getPage(project.url() + "/files/all?page=" + page, true);
			List<String> fis = new ArrayList<>();
			for(String sub : InternalCFA.$cptrs(pg, "<a data-action=\"file-link\" href=\"", "</a>"))
				fis.add(InternalCFA.$cptr(sub, "/files/", "\">"));
			return fis.stream().map(fi -> new FetchableFile(project, fi)).collect(Collectors.toList());
		});
	}
	
	public Fetchable<List<FetchableFile>> files()
	{
		return files;
	}
	
	public int page()
	{
		return page;
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