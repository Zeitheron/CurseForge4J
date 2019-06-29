package com.zeitheron.curseforge.data;

import java.util.concurrent.TimeUnit;

import com.zeitheron.curseforge.data.utils.TimeHolder;

public final class CurseForgePrefs
{
	private TimeHolder cacheLifespan;
	private int numFetchThreads;
	
	{
		reset();
	}
	
	public void reset()
	{
		setCacheLifespan(new TimeHolder(5L, TimeUnit.MINUTES));
		numFetchThreads = Runtime.getRuntime().availableProcessors();
	}
	
	public CurseForgePrefs setCacheLifespan(TimeHolder cacheLifespan)
	{
		this.cacheLifespan = cacheLifespan;
		return this;
	}
	
	public CurseForgePrefs multiplyNumberOfFetchThreads(int by)
	{
		if(by > 0)
			this.numFetchThreads *= by;
		return this;
	}
	
	public CurseForgePrefs setNumberOfFetchThreads(int numFetchThreads)
	{
		this.numFetchThreads = numFetchThreads;
		return this;
	}
	
	public TimeHolder getCacheLifespan()
	{
		return cacheLifespan;
	}
	
	public int getNumberOfFetchThreads()
	{
		return numFetchThreads;
	}
	
	public void inheritFrom(CurseForgePrefs other)
	{
		setCacheLifespan(other.getCacheLifespan());
		setNumberOfFetchThreads(other.getNumberOfFetchThreads());
	}
}