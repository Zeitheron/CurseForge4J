package com.zeitheron.curseforge.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.zeitheron.curseforge.ICurseForge;
import com.zeitheron.curseforge.IProject;
import com.zeitheron.curseforge.fetcher.Fetchable;

public class ProjectFilePage
{
	protected final IProject project;
	protected final int page;
	protected final Fetchable<List<FetchableFile>> files;
	
	ProjectFilePage(IProject project, int page)
	{
		this.project = project;
		this.page = page;
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
		}, 5, TimeUnit.MINUTES);
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
}