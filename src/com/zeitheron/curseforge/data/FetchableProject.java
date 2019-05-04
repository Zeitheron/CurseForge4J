package com.zeitheron.curseforge.data;

import com.zeitheron.curseforge.api.ICurseForge;
import com.zeitheron.curseforge.api.IProject;
import com.zeitheron.curseforge.data.ToStringHelper.Ignore;

public class FetchableProject
{
	protected final String name, slug;
	@Ignore
	protected final ICurseForge cf;
	@Ignore
	protected final Fetchable<IProject> project;
	
	public FetchableProject(String name, String slug, ICurseForge cf)
	{
		this.name = name;
		this.slug = slug;
		this.cf = cf;
		this.project = cf.project(slug);
	}
	
	public Fetchable<IProject> fetch()
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
		return ToStringHelper.toString(this);
	}
}