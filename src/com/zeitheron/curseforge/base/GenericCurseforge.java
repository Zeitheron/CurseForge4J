package com.zeitheron.curseforge.base;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.zeitheron.curseforge.CurseforgeAPI;
import com.zeitheron.curseforge.ICurseForge;
import com.zeitheron.curseforge.IMember;
import com.zeitheron.curseforge.IProject;
import com.zeitheron.curseforge.data.CurseForgePrefs;
import com.zeitheron.curseforge.data.MemberPosts;
import com.zeitheron.curseforge.data.MemberThanks;
import com.zeitheron.curseforge.data.MembersProject;
import com.zeitheron.curseforge.data.ProjectMember;
import com.zeitheron.curseforge.fetcher.Fetchable;

public class GenericCurseforge implements ICurseForge
{
	private static final SimpleDateFormat SDF1 = new SimpleDateFormat("MM/dd/yyyy");
	
	protected final String game;
	
	private final CurseForgePrefs prefs = new CurseForgePrefs();
	
	private final Map<String, Fetchable<IProject>> projectCache = new HashMap<>();
	private final Map<String, Fetchable<IMember>> memberCache = new HashMap<>();
	
	protected GenericCurseforge(String game)
	{
		this.game = game;
	}
	
	@Override
	public Fetchable<IProject> project(String project)
	{
		if(!projectCache.containsKey(project.toLowerCase()))
			projectCache.put(project.toLowerCase(), new Fetchable<>(() ->
			{
				String url = CurseforgeAPI.$cfidg(game()) + "projects/" + project;
				
				String page = ICurseForge.getPage(url, true);
				
				String name = CurseforgeAPI.$cptr(page, "<meta property=\"og:title\" content=\"", "\"");
				if(name == null)
					return null;
				
				String overview = CurseforgeAPI.$cptr(page, "<meta property=\"og:description\" content=\"", "\"");
				if(overview == null)
					return null;
				
				String avatar = null, thumbnail = null;
				{
					String avs = CurseforgeAPI.$cptr(page, "<div class=\"avatar-wrapper\">", "</div>");
					int fs = avs.indexOf("href=\"");
					if(fs >= 0)
					{
						fs += 6;
						int fe = avs.indexOf('\"', fs);
						avatar = avs.substring(fs, fe);
					}
					int ps = avs.indexOf("src=\"");
					if(ps >= 0)
					{
						ps += 5;
						int pe = avs.indexOf('\"', ps);
						thumbnail = avs.substring(ps, pe);
					}
				}
				
				String desc = CurseforgeAPI.$cptr(page, "<div class=\"project-description\" data-user-content>", "</div></div>");
				
				long projectId = Long.parseLong(CurseforgeAPI.$cptr(page, "<div class=\"info-label\">Project ID</div><div class=\"info-data\">", "</div>"));
				
				Date created = CurseforgeAPI.$abbr(CurseforgeAPI.$cptr(page, "<div class=\"info-label\">Created </div><div class=\"info-data\"><abbr", "</abbr>"));
				Date lastUpdate = CurseforgeAPI.$abbr(CurseforgeAPI.$cptr(page, "<div class=\"info-label\">Last Released File</div><div class=\"info-data\"><abbr", "</abbr>"));
				long totalDownloads = Long.parseLong(CurseforgeAPI.$cptr(page, "<div class=\"info-label\">Total Downloads</div><div class=\"info-data\">", "</div>").replaceAll(",", ""));
				
				List<ProjectMember> members = new ArrayList<>();
				{
					String rawMembers = CurseforgeAPI.$cptr(page, "<div class=\"cf-sidebar-inner\"><ul class=\"cf-details project-members\">", "</ul></div>");
					String[] mba = rawMembers.split("<li class=");
					for(int i = 1; i < mba.length; ++i)
					{
						String mbr = mba[i];
						String mbn = CurseforgeAPI.$cptr(mbr, "<span>", "</span>");
						String mbk = CurseforgeAPI.$cptr(mbr, "<span class=\"title\">", "</span>");
						if(mbn != null && mbk != null)
							members.add(new ProjectMember(mbn, mbk, this));
					}
				}
				
				return new BaseProject(name, overview, CurseforgeAPI.$rlnk(desc), avatar, thumbnail, created, lastUpdate, projectId, totalDownloads, members, this, url);
			}, preferences().getCacheLifespan().getVal(), preferences().getCacheLifespan().getUnit()));
		return projectCache.get(project.toLowerCase());
	}
	
	@Override
	public Fetchable<IMember> member(String member)
	{
		if(!memberCache.containsKey(member.toLowerCase()))
			memberCache.put(member.toLowerCase(), new Fetchable<>(() ->
			{
				String base = CurseforgeAPI.$cfidg(game()) + "members/" + member;
				String page = ICurseForge.getPage(base, true);
				
				String name = CurseforgeAPI.$cptr(page, "<li class=\"username\">", "</li>");
				
				if(name == null)
					return null;
				
				String avatar = CurseforgeAPI.$cptr(page, "<a href=\"/members/" + name + "\"><img ", "</a>");
				if(avatar != null)
				{
					int start = avatar.indexOf("src=\"") + 5;
					int end = avatar.indexOf("\"", start);
					avatar = avatar.substring(start, end);
				}
				String memberSince = CurseforgeAPI.$cptr(page, "Member Since: ", "\">");
				Date registerDate = null;
				
				try
				{
					registerDate = SDF1.parse(memberSince);
				} catch(ParseException e)
				{
					e.printStackTrace();
				}
				
				Date lastActive = CurseforgeAPI.$abbr(CurseforgeAPI.$cptr(page, "Last active <abbr class=\"tip standard-datetime-precise\" title=\"", "\">"));
				
				String followersStr = CurseforgeAPI.$cptr(page, "<li class=\"followers\"><span>", "</span>");
				long followers = Long.parseLong(followersStr.split(" ")[0]);
				
				String postsStr = CurseforgeAPI.$cptr(page, "<li class=\"posts\"><span class=\"tip\" title=\"", "\">");
				String[] postMeta$ = postsStr.split(", ");
				long comments = Long.parseLong(postMeta$[0].split(" ")[0].substring(1));
				long forumPosts = Long.parseLong(postMeta$[1].split(" ")[0]);
				
				String likesStr = CurseforgeAPI.$cptr(page, "<li class=\"likes\"><span class=\"tip\" title=\"", "\">");
				String[] likeMeta$ = likesStr.split(", ");
				long th_rcv = Long.parseLong(likeMeta$[0].split(" ")[0].substring(1));
				long th_gvn = Long.parseLong(likeMeta$[1].split(" ")[0]);
				
				Supplier<List<MembersProject>> projects = () ->
				{
					List<MembersProject> prs = new ArrayList<>();
					List<String> ids = new ArrayList<>();
					int i = 1;
					while(true)
					{
						String pg = ICurseForge.getPage(base + "/projects?page=" + i, true);
						int added = 0;
						{
							String[] values = pg.split("<a href=\"/projects");
							for(String v : values)
								if(v.charAt(0) == '/')
								{
									int e;
									String pr = v.substring(1, e = v.indexOf("\">"));
									if(!ids.contains(pr))
									{
										ids.add(pr);
										prs.add(new MembersProject(v.substring(e + 2, v.indexOf("</a>")), pr, this));
										++added;
									}
								}
						}
						if(added == 0)
							break;
						++i;
					}
					
					return Collections.unmodifiableList(prs);
				};
				
				Supplier<List<String>> followerList = () ->
				{
					String txt = ICurseForge.getPage(base + "/followers", true);
					Set<String> located = new HashSet<>(CurseforgeAPI.$cptrs(txt, "<a href=\"/members/", "\""));
					located.removeIf(s -> s.contains("/"));
					located.remove(member);
					return Collections.unmodifiableList(new ArrayList<>(located));
				};
				
				return new BaseMember(registerDate, lastActive, avatar, name, new MemberPosts(comments, forumPosts), new MemberThanks(th_gvn, th_rcv), followers, projects, this, base, followerList);
			}, preferences().getCacheLifespan().getVal(), preferences().getCacheLifespan().getUnit()));
		return memberCache.get(member.toLowerCase());
	}
	
	@Override
	public String game()
	{
		return game;
	}
	
	@Override
	public CurseForgePrefs preferences()
	{
		return prefs;
	}
}