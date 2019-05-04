package com.zeitheron.curseforge.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.zeitheron.curseforge.api.ICurseForge;
import com.zeitheron.curseforge.api.IProject;
import com.zeitheron.curseforge.data.ToStringHelper.Ignore;

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
		this.files = new Fetchable<>(() ->
		{
			String pg;
			if(page == 1)
				pg = project.files().firstPage.get();
			else
				pg = ICurseForge.getPage(project.url() + "/files?page=" + page, true);
			
			List<String> fis = new ArrayList<>();
			
			int i;
			while((i = pg.indexOf("/files/")) != -1)
			{
				pg = pg.substring(i + 7);
				String fi = pg.substring(0, pg.indexOf('"'));
				if(!fi.equals("latest") && !fi.contains("download") && !fis.contains(fi))
					fis.add(fi);
			}
			
			return fis.stream().map(fi -> new FetchableFile(project, fi)).collect(Collectors.toList());
		}, cf.preferences().getCacheLifespan().getVal(), cf.preferences().getCacheLifespan().getUnit());
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