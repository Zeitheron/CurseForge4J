package com.zeitheron.curseforge.data.member;

import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

import com.zeitheron.curseforge.api.ICurseForge;
import com.zeitheron.curseforge.api.IMember;
import com.zeitheron.curseforge.data.project.FetchableProject;
import com.zeitheron.curseforge.data.utils.Fetchable;
import com.zeitheron.curseforge.data.utils.ToStringHelper;
import com.zeitheron.curseforge.data.utils.ToStringHelper.Ignore;

public class CMember implements IMember
{
	protected final Date register, lastActive;
	protected final String avatar, name;
	protected final MemberPosts posts;
	protected final MemberThanks thanks;
	protected final long followers;
	@Ignore
	protected final Fetchable<List<FetchableProject>> projects;
	@Ignore
	protected final Fetchable<List<String>> followerList;
	@Ignore
	protected final ICurseForge cf;
	protected final String url;
	protected final boolean online;
	
	public CMember(Date register, Date lastActive, String avatar, String name, MemberPosts posts, MemberThanks thanks, long followers, Supplier<List<FetchableProject>> projects, ICurseForge cf, String url, Supplier<List<String>> followerList, boolean online)
	{
		this.register = register;
		this.lastActive = lastActive;
		this.avatar = avatar;
		this.name = name;
		this.posts = posts;
		this.thanks = thanks;
		this.followers = followers;
		this.projects = cf.createFetchable(projects);
		this.followerList = cf.createFetchable(followerList);
		this.cf = cf;
		this.url = url;
		this.online = online;
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
	public Fetchable<List<FetchableProject>> projects()
	{
		return projects;
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
		
		int jtvnw = url.lastIndexOf("profile_image-");
		if(jtvnw != -1)
		{
			String resolution = url.substring(jtvnw + 14, url.lastIndexOf('.'));
			if(resolution.contains("x"))
				return url;
			if(resolution.length() == -1)
			{
				String[] parts = resolution.split("x");
				int width = Integer.parseInt(parts[0]);
				int height = Integer.parseInt(parts[1]);
				float ratioW = size / (float) width;
				float ratioH = size / (float) height;
				float ratio = Math.max(ratioW, ratioH);
				int nw = Math.round(width * ratio);
				int nh = Math.round(height * ratio);
				return url.replace("profile_image-" + resolution, "profile_image-" + nw + "x" + nh);
			}
		}
		
		if(url.contains("?"))
			url += "&size=" + size;
		else
			url += "?size=" + size;
		return url;
	}
	
	@Override
	public boolean online()
	{
		return online;
	}
	
	@Override
	public String toString()
	{
		return ToStringHelper.toString(this);
	}
}