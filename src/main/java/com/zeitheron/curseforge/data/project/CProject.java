package com.zeitheron.curseforge.data.project;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.zeitheron.curseforge.api.ICurseForge;
import com.zeitheron.curseforge.api.IProject;
import com.zeitheron.curseforge.data.member.FetchableMember;
import com.zeitheron.curseforge.data.utils.ToStringHelper;
import com.zeitheron.curseforge.data.utils.ToStringHelper.Ignore;

public class CProject implements IProject
{
	protected final String name, overview, avatar, thumb;
	protected final Date create, update;
	protected final long id, downloads;
	@Ignore
	protected final ICurseForge cf;
	protected final List<FetchableMember> membersList;
	@Ignore
	protected final FetchableMember[] membersArray;
	@Ignore
	protected final ProjectFiles files;
	protected final String url;
	protected final String game, gameCategory;
	@Ignore
	protected final String desc;
	
	public CProject(String name, String overview, String desc, String avatar, String thumb, Date create, Date update, long id, long downloads, List<FetchableMember> members, ICurseForge cf, String url, String gameCategory, String game)
	{
		this.name = name;
		this.overview = overview;
		this.desc = desc;
		this.avatar = avatar;
		this.thumb = thumb;
		this.create = create;
		this.game = game;
		this.update = update;
		this.id = id;
		this.downloads = downloads;
		this.membersList = Collections.unmodifiableList(members);
		this.membersArray = members.toArray(new FetchableMember[members.size()]);
		this.cf = cf;
		this.url = url;
		this.files = new ProjectFiles(this);
		this.gameCategory = gameCategory;
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
	public List<FetchableMember> membersList()
	{
		return membersList;
	}
	
	@Override
	public FetchableMember[] membersArray()
	{
		return membersArray.clone();
	}
	
	@Override
	public ICurseForge curseForge()
	{
		return cf;
	}
	
	@Override
	public ProjectFiles files()
	{
		return files;
	}
	
	@Override
	public String url()
	{
		return url;
	}
	
	@Override
	public String game()
	{
		return game;
	}
	
	@Override
	public String category()
	{
		return gameCategory;
	}
	
	@Override
	public String toString()
	{
		return ToStringHelper.toString(this);
	}
}