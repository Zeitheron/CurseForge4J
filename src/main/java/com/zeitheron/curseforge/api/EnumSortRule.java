package com.zeitheron.curseforge.api;

public enum EnumSortRule
{
	DATE_CREATED(1), //
	LAST_UPDATED(2), //
	NAME(3), //
	POPULARITY(4), //
	TOTAL_DOWNLOADS(5);
	
	final int id;
	
	private EnumSortRule(int id)
	{
		this.id = id;
	}
	
	public int getId()
	{
		return id;
	}
}