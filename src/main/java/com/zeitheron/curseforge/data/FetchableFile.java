package com.zeitheron.curseforge.data;

import com.zeitheron.curseforge.api.IProject;
import com.zeitheron.curseforge.api.IProjectFile;
import com.zeitheron.curseforge.data.ToStringHelper.Ignore;

public class FetchableFile
{
	protected final String id;
	protected final IProject project;
	@Ignore
	protected final Fetchable<IProjectFile> file;
	
	public FetchableFile(IProject project, String id)
	{
		this.project = project;
		this.id = id;
		this.file = project.curseForge().createFetchable(() -> CFile.create(project, id));
	}
	
	public Fetchable<IProjectFile> fetch()
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
	
	@Override
	public String toString()
	{
		return ToStringHelper.toString(this);
	}
}