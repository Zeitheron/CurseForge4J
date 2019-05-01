package com.zeitheron.curseforge.base;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.zeitheron.curseforge.IProject;
import com.zeitheron.curseforge.data.ProjectMember;

public class BaseProject implements IProject
{
	protected final String name, overview, desc, avatar, thumb;
	protected final Date create, update;
	protected final long id, downloads;
	
	protected final List<ProjectMember> membersList;
	protected final ProjectMember[] membersArray;
	
	public BaseProject(String name, String overview, String desc, String avatar, String thumb, Date create, Date update, long id, long downloads, List<ProjectMember> members)
	{
		this.name = name;
		this.overview = overview;
		this.desc = desc;
		this.avatar = avatar;
		this.thumb = thumb;
		this.create = create;
		this.update = update;
		this.id = id;
		this.downloads = downloads;
		this.membersList = Collections.unmodifiableList(members);
		this.membersArray = members.toArray(new ProjectMember[members.size()]);
	}
	
	@Override
	public String name()
	{
		return name;
	}
	
	@Override
	public String overview()
	{
		return overview;
	}
	
	@Override
	public String description()
	{
		return desc;
	}
	
	@Override
	public String avatar()
	{
		return avatar;
	}
	
	@Override
	public String thumbnail()
	{
		return thumb;
	}
	
	@Override
	public long totalDownloads()
	{
		return downloads;
	}
	
	@Override
	public Date lastUpdate()
	{
		return update;
	}
	
	@Override
	public Date createTime()
	{
		return create;
	}
	
	@Override
	public long projectId()
	{
		return id;
	}
	
	@Override
	public List<ProjectMember> membersList()
	{
		return membersList;
	}
	
	@Override
	public ProjectMember[] membersArray()
	{
		return membersArray.clone();
	}
}