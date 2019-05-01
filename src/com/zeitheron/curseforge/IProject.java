package com.zeitheron.curseforge;

import java.util.Date;
import java.util.List;

import com.zeitheron.curseforge.data.ProjectMember;

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
	
	List<ProjectMember> membersList();
	
	ProjectMember[] membersArray();
}