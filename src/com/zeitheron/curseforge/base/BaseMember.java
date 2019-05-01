package com.zeitheron.curseforge.base;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

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
	protected final Fetchable<List<MembersProject	>> projects;
	
	public BaseMember(Date register, Date lastActive, String avatar, String name, MemberPosts posts, MemberThanks thanks, long followers, Supplier<List<MembersProject>> projects)
	{
		this.register = register;
		this.lastActive = lastActive;
		this.avatar = avatar;
		this.name = name;
		this.posts = posts;
		this.thanks = thanks;
		this.followers = followers;
		this.projects = new Fetchable<>(projects, 5, TimeUnit.MINUTES);
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
}