package com.zeitheron.curseforge.data;

import java.util.concurrent.TimeUnit;

public final class TimeHolder
{
	private final long val;
	private final TimeUnit unit;
	
	public TimeHolder(long val, TimeUnit unit)
	{
		this.val = val;
		this.unit = unit;
	}
	
	public long to(TimeUnit dst)
	{
		return dst.convert(val, unit);
	}
	
	public long getVal()
	{
		return val;
	}
	
	public TimeUnit getUnit()
	{
		return unit;
	}
}