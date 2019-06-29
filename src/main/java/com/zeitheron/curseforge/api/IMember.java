package com.zeitheron.curseforge.api;

import java.util.Date;
import java.util.List;

import com.zeitheron.curseforge.data.member.MemberPosts;
import com.zeitheron.curseforge.data.member.MemberThanks;
import com.zeitheron.curseforge.data.project.FetchableProject;
import com.zeitheron.curseforge.data.utils.Fetchable;

public interface IMember
{
	Date registerDate();
	
	String avatarURL();
	
	String avatarURL(int size);
	
	Date lastActive();
	
	String name();
	
	MemberThanks thanks();
	
	MemberPosts posts();
	
	Fetchable<List<FetchableProject>> projects();
	
	List<String> followerList();
	
	long followers();
	
	ICurseForge curseForge();
	
	String url();
	
	boolean online();
}