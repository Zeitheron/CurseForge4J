package com.zeitheron.curseforge.data;

import com.zeitheron.curseforge.ICurseForge;
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
		ICurseForge cf = project.curseForge();
		this.file = new Fetchable<>(() -> BaseFile.create(project, id), cf.preferences().getCacheLifespan().getVal(), cf.preferences().getCacheLifespan().getUnit());
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