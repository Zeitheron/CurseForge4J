package com.zeitheron.curseforge.api;

import java.util.Date;
import java.util.List;

import com.zeitheron.curseforge.data.member.FetchableMember;
import com.zeitheron.curseforge.data.project.ProjectFiles;

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
	
	String game();
	
	String category();
	
	ICurseForge curseForge();
}