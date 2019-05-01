package com.zeitheron.curseforge.data;

import java.util.concurrent.TimeUnit;

import com.zeitheron.curseforge.IProject;
import com.zeitheron.curseforge.IProjectFile;
import com.zeitheron.curseforge.base.BaseFile;
import com.zeitheron.curseforge.fetcher.Fetchable;

public class FetchableFile
{
	protected final String id;
	protected final IProject project;
	protected final Fetchable<IProjectFile> file;
	
	public FetchableFile(IProject project, String id)
	{
		this.project = project;
		this.id = id;
		this.file = new Fetchable<>(() -> BaseFile.create(project, id), 5, TimeUnit.MINUTES);
	}
	
	public Fetchable<IProjectFile> asProjectFile()
	{
		return file;
	}
	
	public String id()
	{
		return id;
	}
	
	public IProject project()
	{
		return project;
	}
}