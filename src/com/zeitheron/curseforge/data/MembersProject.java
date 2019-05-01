package com.zeitheron.curseforge.data;

import com.zeitheron.curseforge.ICurseForge;
import com.zeitheron.curseforge.IProject;
import com.zeitheron.curseforge.fetcher.Fetchable;

public class MembersProject
{
	protected final String name, slug;
	protected final ICurseForge cf;
	protected final Fetchable<IProject> project;
	
	public MembersProject(String name, String slug, ICurseForge cf)
	{
		this.name = name;
		this.slug = slug;
		this.cf = cf;
		this.project = cf.project(slug);
	}
	
	public Fetchable<IProject> asProject()
	{
		return project;
	}
	
	public String name()
	{
		return name;
	}
	
	public String slug()
	{
		return slug;
	}
	
	@Override
	public String toString()
	{
		return name + " (" + slug + ")";
	}
}