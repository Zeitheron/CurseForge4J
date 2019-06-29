package com.zeitheron.curseforge.data;

import com.zeitheron.curseforge.api.ICurseForge;
import com.zeitheron.curseforge.api.IProject;
import com.zeitheron.curseforge.data.ToStringHelper.Ignore;

public class FetchableProject
{
	protected final String name, slug, avatar, author;
	@Ignore
	protected final ICurseForge cf;
	@Ignore
	protected final Fetchable<IProject> project;
	
	public FetchableProject(String name, String slug, String avatar, ICurseForge cf, String author)
	{
		this.name = name;
		this.slug = slug;
		this.cf = cf;
		this.avatar = avatar;
		this.project = cf.project(slug);
		this.author = author;
	}
	
	public Fetchable<IProject> fetch()
	{
		return project;
	}
	
	public String author()
	{
		return author;
	}
	
	public String name()
	{
		return name;
	}
	
	public String slug()
	{
		return slug;
	}
	
	public String avatar()
	{
		return avatar;
	}
	
	@Override
	public String toString()
	{
		return ToStringHelper.toString(this);
	}
}