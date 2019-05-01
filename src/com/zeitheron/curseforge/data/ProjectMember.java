package com.zeitheron.curseforge.data;

import com.zeitheron.curseforge.ICurseForge;
import com.zeitheron.curseforge.IMember;
import com.zeitheron.curseforge.fetcher.Fetchable;

public class ProjectMember
{
	protected final String name, role;
	protected final ICurseForge cf;
	protected final Fetchable<IMember> member;
	
	public ProjectMember(String name, String role, ICurseForge cf)
	{
		this.name = name;
		this.role = role;
		this.cf = cf;
		this.member = cf.member(this.name);
	}
	
	public Fetchable<IMember> asMember()
	{
		return member;
	}
	
	public String name()
	{
		return name;
	}
	
	public String role()
	{
		return role;
	}
	
	@Override
	public String toString()
	{
		return name + " - " + role;
	}
}