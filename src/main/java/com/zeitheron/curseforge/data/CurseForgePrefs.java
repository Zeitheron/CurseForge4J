package com.zeitheron.curseforge.data;

import java.util.concurrent.TimeUnit;

public final class CurseForgePrefs
{
	private TimeHolder cacheLifespan;
	
	{
		reset();
	}
	
	public void reset()
	{
		setCacheLifespan(new TimeHolder(5L, TimeUnit.MINUTES));
	}
	
	public void setCacheLifespan(TimeHolder cacheLifespan)
	{
		this.cacheLifespan = cacheLifespan;
	}
	
	public TimeHolder getCacheLifespan()
	{
		return cacheLifespan;
	}
	
	public void inheritFrom(CurseForgePrefs other)
	{
		setCacheLifespan(other.getCacheLifespan());
	}
}