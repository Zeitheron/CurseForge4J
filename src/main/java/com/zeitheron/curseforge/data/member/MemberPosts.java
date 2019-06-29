package com.zeitheron.curseforge.data.member;

import com.zeitheron.curseforge.data.utils.ToStringHelper;

public class MemberPosts
{
	protected final long total, comments, forumPosts;
	
	public MemberPosts(long comments, long forumPosts)
	{
		this.total = comments + forumPosts;
		this.comments = comments;
		this.forumPosts = forumPosts;
	}
	
	public long total()
	{
		return total;
	}
	
	public long comments()
	{
		return comments;
	}
	
	public long forumPosts()
	{
		return forumPosts;
	}
	
	@Override
	public String toString()
	{
		return ToStringHelper.toString(this);
	}
}