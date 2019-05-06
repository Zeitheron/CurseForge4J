package com.zeitheron.curseforge.data;

public class MemberThanks
{
	protected final long total, given, received;
	
	public MemberThanks(long given, long received)
	{
		this.total = given + received;
		this.given = given;
		this.received = received;
	}
	
	public long total()
	{
		return total;
	}
	
	public long given()
	{
		return given;
	}
	
	public long received()
	{
		return received;
	}
	
	@Override
	public String toString()
	{
		return ToStringHelper.toString(this);
	}
}