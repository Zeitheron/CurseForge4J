package com.zeitheron.curseforge.api;

import java.util.Date;
import java.util.List;

import com.zeitheron.curseforge.data.MemberPosts;
import com.zeitheron.curseforge.data.MemberThanks;
import com.zeitheron.curseforge.data.FetchableProject;

public interface IMember
{
	Date registerDate();
	
	String avatarURL();
	
	String avatarURL(int size);
	
	Date lastActive();
	
	String name();
	
	MemberThanks thanks();
	
	MemberPosts posts();
	
	List<FetchableProject> projects();
	
	List<String> followerList();
	
	long followers();
	
	ICurseForge curseForge();
	
	String url();
	
	boolean online();
}