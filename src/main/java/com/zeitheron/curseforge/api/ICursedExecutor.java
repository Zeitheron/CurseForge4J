package com.zeitheron.curseforge.api;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.zeitheron.curseforge.data.utils.Fetchable;

public interface ICursedExecutor
{
	<T> Future<T> fetch(Fetchable<T> fetchable);
	
	<T> List<Future<T>> fetchAll(Iterable<Fetchable<T>> fetchables);
	
	<T> List<Future<T>> fetchAllCall(Iterable<Callable<T>> fetchables);
	
	default <T> List<T> fetchAndWaitForAll(Iterable<Fetchable<T>> fetchables)
	{
		return waitForAll(fetchAll(fetchables));
	}
	
	default <T> List<T> fetchAndWaitForAllCall(Iterable<Callable<T>> fetchables)
	{
		return waitForAll(fetchAllCall(fetchables));
	}
	
	default <T> List<T> qfetch(List<IQFetchable<T>> projects)
	{
		return fetchAndWaitForAll(projects.stream().map(f -> f.fetch()).collect(Collectors.toList()));
	}
	
	default List<IProject> qfetchProjects(List<Long> projectIds)
	{
		return fetchAndWaitForAll(projectIds.stream().map(curseForge()::project).collect(Collectors.toList()));
	}
	
	default List<IMember> qfetchMembers(List<String> projectIds)
	{
		return fetchAndWaitForAll(projectIds.stream().map(curseForge()::member).collect(Collectors.toList()));
	}
	
	ICurseForge curseForge();
	
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