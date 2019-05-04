package com.zeitheron.curseforge.base;

import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

import com.zeitheron.curseforge.ICurseForge;
import com.zeitheron.curseforge.IMember;
import com.zeitheron.curseforge.data.MemberPosts;
import com.zeitheron.curseforge.data.MemberThanks;
import com.zeitheron.curseforge.data.MembersProject;
import com.zeitheron.curseforge.fetcher.Fetchable;

public class BaseMember implements IMember
{
	protected final Date register, lastActive;
	protected final String avatar, name;
	protected final MemberPosts posts;
	protected final MemberThanks thanks;
	protected final long followers;
	protected final Fetchable<List<MembersProject>> projects;
	protected final Fetchable<List<String>> followerList;
	protected final ICurseForge cf;
	protected final String url;
	
	public BaseMember(Date register, Date lastActive, String avatar, String name, MemberPosts posts, MemberThanks thanks, long followers, Supplier<List<MembersProject>> projects, ICurseForge cf, String url, Supplier<List<String>> followerList)
	{
		this.register = register;
		this.lastActive = lastActive;
		this.avatar = avatar;
		this.name = name;
		this.posts = posts;
		this.thanks = thanks;
		this.followers = followers;
		this.projects = new Fetchable<>(projects, cf.preferences().getCacheLifespan().getVal(), cf.preferences().getCacheLifespan().getUnit());
		this.followerList = new Fetchable<List<String>>(followerList, cf.preferences().getCacheLifespan().getVal(), cf.preferences().getCacheLifespan().getUnit());
		this.cf = cf;
		this.url = url;
	}
	
	@Override
	public Date registerDate()
	{
		return register;
	}
	
	@Override
	public String avatarURL()
	{
		return avatar;
	}
	
	@Override
	public Date lastActive()
	{
		return lastActive;
	}
	
	@Override
	public String name()
	{
		return name;
	}
	
	@Override
	public MemberThanks thanks()
	{
		return thanks;
	}
	
	@Override
	public MemberPosts posts()
	{
		return posts;
	}
	
	@Override
	public long followers()
	{
		return followers;
	}
	
	@Override
	public List<MembersProject> projects()
	{
		return projects.get();
	}

	@Override
	public ICurseForge curseForge()
	{
		return cf;
	}
	
	@Override
	public String url()
	{
		return url;
	}

	@Override
	public List<String> followerList()
	{
		return followerList.get();
	}

	@Override
	public String avatarURL(int size)
	{
		String url = avatarURL();
		if(url.contains("?"))
			url += "&size=" + size;
		else
			url += "?size=" + size;
		return url;
	}
}