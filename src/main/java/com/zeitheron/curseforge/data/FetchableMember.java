package com.zeitheron.curseforge.data;

import com.zeitheron.curseforge.api.ICurseForge;
import com.zeitheron.curseforge.api.IMember;
import com.zeitheron.curseforge.data.ToStringHelper.Ignore;

public class FetchableMember
{
	protected final String name, role;
	@Ignore
	protected final ICurseForge cf;
	@Ignore
	protected final Fetchable<IMember> member;
	
	public FetchableMember(String name, String role, ICurseForge cf)
	{
		this.name = name;
		this.role = role;
		this.cf = cf;
		this.member = cf.member(this.name);
	}
	
	public Fetchable<IMember> fetch()
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
		return ToStringHelper.toString(this);
	}
}