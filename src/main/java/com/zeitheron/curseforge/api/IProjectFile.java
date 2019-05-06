package com.zeitheron.curseforge.api;

import java.util.Date;
import java.util.List;

import com.zeitheron.curseforge.data.FetchableFile;
import com.zeitheron.curseforge.data.FetchableMember;

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
	
	FetchableMember uploader();
	
	String url();
	
	String changelog();
	
	String size();
	
	long sizeBytes();
}