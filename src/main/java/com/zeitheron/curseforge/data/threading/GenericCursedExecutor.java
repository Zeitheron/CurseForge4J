package com.zeitheron.curseforge.data.threading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.zeitheron.curseforge.api.threading.ICursedExecutor;
import com.zeitheron.curseforge.data.GenericCurseforge;
import com.zeitheron.curseforge.data.utils.Fetchable;

public class GenericCursedExecutor implements ICursedExecutor
{
	ExecutorService exec;
	final GenericCurseforge cf;
	private boolean hasInit;
	
	public GenericCursedExecutor(GenericCurseforge cf)
	{
		this.cf = cf;
	}
	
	public boolean init()
	{
		if(!hasInit)
		{
			hasInit = true;
			exec = Executors.newFixedThreadPool(cf.preferences().getNumberOfFetchThreads());
			return true;
		}
		return false;
	}
	
	@Override
	public <T> Future<T> fetch(Fetchable<T> fetchable)
	{
		return exec.submit(fetchable::fetchNow);
	}
	
	@Override
	public <T> List<Future<T>> fetchAll(Iterable<Fetchable<T>> fetchables)
	{
		List<Future<T>> futures = new ArrayList<>();
		for(Fetchable<T> f : fetchables)
			futures.add(fetch(f));
		return futures;
	}

	@Override
	public <T> List<Future<T>> fetchAllCall(Iterable<Callable<T>> fetchables)
	{
		List<Future<T>> futures = new ArrayList<>();
		for(Callable<T> f : fetchables)
			futures.add(exec.submit(f));
		return futures;
	}
}