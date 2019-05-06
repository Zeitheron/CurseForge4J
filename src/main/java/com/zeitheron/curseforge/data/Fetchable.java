package com.zeitheron.curseforge.data;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class Fetchable<T> implements Supplier<T>
{
	private static final ExecutorService runtime = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	private final Supplier<T> fetcher;
	private long fetchTime;
	private T fetchValue;
	private boolean fetching, fetched;
	private final long refreshRateMillis;
	
	public Fetchable(Supplier<T> fetcher, long rate, TimeUnit rateUnit)
	{
		this.fetcher = fetcher;
		this.refreshRateMillis = TimeUnit.MILLISECONDS.convert(rate, rateUnit);
	}
	
	public T fetchNow()
	{
		fetched = false;
		fetching = true;
		fetchValue = fetcher.get();
		fetchTime = System.currentTimeMillis();
		fetched = true;
		fetching = false;
		return fetchValue;
	}
	
	public Future<T> fetchASAP()
	{
		return runtime.submit(this::fetchNow);
	}
	
	@Override
	public T get()
	{
		if(!fetched && !fetching)
			return fetchNow();
		if(fetched && !fetching)
		{
			long ct = System.currentTimeMillis();
			long elapse = ct - fetchTime;
			if(elapse < 0 || elapse >= refreshRateMillis)
				fetchASAP();
		}
		return fetchValue;
	}
}