package com.zeitheron.curseforge.api.threading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.zeitheron.curseforge.data.Fetchable;

public interface ICursedExecutor
{
	<T> Future<T> fetch(Fetchable<T> fetchable);
	
	<T> List<Future<T>> fetchAll(Iterable<Fetchable<T>> fetchables);
	
	default <T> List<T> fetchAndWaitForAll(Iterable<Fetchable<T>> fetchables)
	{
		return waitForAll(fetchAll(fetchables));
	}
	
	@SuppressWarnings("unchecked")
	default <T> List<T> waitForAll(Future<T>... futures)
	{
		List<T> values = new ArrayList<>();
		for(Future<T> cur : futures)
			try
			{
				values.add(cur.get());
			} catch(InterruptedException | ExecutionException e)
			{
				values.add(null);
			}
		return values;
	}
	
	default <T> List<T> waitForAll(Iterable<Future<T>> futures)
	{
		List<T> values = new ArrayList<>();
		for(Future<T> cur : futures)
			try
			{
				values.add(cur.get());
			} catch(InterruptedException | ExecutionException e)
			{
				values.add(null);
			}
		return values;
	}
}