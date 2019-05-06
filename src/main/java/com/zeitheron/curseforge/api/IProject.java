package com.zeitheron.curseforge.api;

import java.util.Date;
import java.util.List;

import com.zeitheron.curseforge.data.ProjectFiles;
import com.zeitheron.curseforge.data.FetchableMember;

public interface IProject
{
	String name();
	
	String overview();
	
	String description();
	
	String avatar();
	
	String thumbnail();
	
	long totalDownloads();
	
	Date lastUpdate();
	
	Date createTime();
	
	long projectId();
	
	List<FetchableMember> membersList();
	
	FetchableMember[] membersArray();
	
	ProjectFiles files();
	
	String url();
	
	String category();
	
	ICurseForge curseForge();
}