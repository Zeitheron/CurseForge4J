package com.zeitheron.curseforge;

import java.util.Date;
import java.util.List;

import com.zeitheron.curseforge.data.FetchableFile;
import com.zeitheron.curseforge.data.ProjectMember;

public interface IProjectFile
{
	String downloadURL();
	
	String displayName();
	
	String fileName();
	
	String md5();
	
	Date uploaded();
	
	long downloads();
	
	IProject project();
	
	List<FetchableFile> additionalFiles();
	
	ProjectMember uploader();
	
	String url();
}