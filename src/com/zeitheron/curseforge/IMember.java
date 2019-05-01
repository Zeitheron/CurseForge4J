package com.zeitheron.curseforge;

import java.util.Date;
import java.util.List;

import com.zeitheron.curseforge.data.MemberPosts;
import com.zeitheron.curseforge.data.MemberThanks;
import com.zeitheron.curseforge.data.MembersProject;

public interface IMember
{
	Date registerDate();
	
	String avatarURL();
	
	Date lastActive();
	
	String name();
	
	MemberThanks thanks();
	
	MemberPosts posts();
	
	List<MembersProject> projects();
	
	long followers();
	
	ICurseForge curseForge();
	
	String url();
}