package com.zeitheron.curseforge.data.project;

import com.zeitheron.curseforge.api.IProject;
import com.zeitheron.curseforge.api.IProjectFile;
import com.zeitheron.curseforge.api.IQFetchable;
import com.zeitheron.curseforge.data.utils.Fetchable;
import com.zeitheron.curseforge.data.utils.ToStringHelper;
import com.zeitheron.curseforge.data.utils.ToStringHelper.Ignore;

public class FetchableFile implements IQFetchable<IProjectFile>
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
	
	@Override
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