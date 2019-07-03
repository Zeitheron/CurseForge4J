package com.zeitheron.curseforge.data;

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

import com.zeitheron.curseforge.api.CurseSearchDetails;
import com.zeitheron.curseforge.api.ICurseForge;
import com.zeitheron.curseforge.api.ICursedExecutor;
import com.zeitheron.curseforge.api.IGame;
import com.zeitheron.curseforge.api.IMember;
import com.zeitheron.curseforge.api.IProject;
import com.zeitheron.curseforge.api.ISearchResult;
import com.zeitheron.curseforge.data.game.CGame;
import com.zeitheron.curseforge.data.member.CMember;
import com.zeitheron.curseforge.data.member.FetchableMember;
import com.zeitheron.curseforge.data.member.MemberPosts;
import com.zeitheron.curseforge.data.member.MemberThanks;
import com.zeitheron.curseforge.data.project.CProject;
import com.zeitheron.curseforge.data.project.FetchableProject;
import com.zeitheron.curseforge.data.project.ProjectSearchResult;
import com.zeitheron.curseforge.data.threading.GenericCursedExecutor;
import com.zeitheron.curseforge.data.utils.Fetchable;

public class GenericCurseforge implements ICurseForge
{
	static final SimpleDateFormat SDF1 = new SimpleDateFormat("MM/dd/yyyy");
	final CurseForgePrefs prefs = new CurseForgePrefs();
	final Map<String, Fetchable<IProject>> projectCache = new HashMap<>();
	final Map<String, Fetchable<IMember>> memberCache = new HashMap<>();
	final Map<Long, Fetchable<String>> projectIdToStrMap = new HashMap<>();
	Fetchable<List<IGame>> games;
	
	GenericCursedExecutor scheduler;
	
	@Override
	public ICursedExecutor executor()
	{
		if(scheduler == null)
		{
			scheduler = new GenericCursedExecutor(this);
			scheduler.init();
		}
		return scheduler;
	}
	
	@Override
	public Fetchable<String> projectIdByLID(long lid)
	{
		if(!projectIdToStrMap.containsKey(lid))
			projectIdToStrMap.put(lid, createFetchable(() ->
			{
				String url = url() + "projects/" + lid;
				String data = ICurseForge.getPage(url, true);
				return InternalCFA.$cptr(data, "<div class=\"project-avatar project-avatar-64\"><a href=\"", "\"").substring(1);
			}));
		return projectIdToStrMap.get(lid);
	}
	
	@Override
	public Fetchable<IProject> project(String project)
	{
		if(!projectCache.containsKey(project.toLowerCase()))
			projectCache.put(project.toLowerCase(), createFetchable(() ->
			{
				String url = url() + project;
				
				String page = ICurseForge.getPage(url, true);
				
				String name = InternalCFA.$cptr(page, "<meta property=\"og:title\" content=\"", "\"");
				if(name == null)
					return null;
				
				String overview = InternalCFA.$cptr(page, "<meta property=\"og:description\" content=\"", "\"");
				if(overview == null)
					return null;
				
				String avatar = null, thumbnail = null;
				{
					String avs = InternalCFA.$cptr(page, "<div class=\"project-avatar project-avatar-64\">", "</a></div>");
					avatar = InternalCFA.$cptr(avs, "data-featherlight=\"", "\">");
					thumbnail = InternalCFA.$cptr(avs, "<img src=\"", "\"");
				}
				
				String desc = InternalCFA.$cptr(page, "<div class=\"box p-4 pb-2 project-detail__content\" data-user-content>", "</div><div class=\"mt-6\">");
				
				long projectId = Long.parseLong(InternalCFA.$cptr(page, "<span>Project ID</span><span>", "</span>"));
				
				Date created = InternalCFA.$abbr(InternalCFA.$cptr(page, "<div class=\"info-label\">Created </div><div class=\"info-data\"><abbr", "</abbr>"));
				Date lastUpdate = InternalCFA.$abbr(InternalCFA.$cptr(page, "<div class=\"info-label\">Last Released File</div><div class=\"info-data\"><abbr", "</abbr>"));
				long totalDownloads = Long.parseLong(InternalCFA.$cptr(page, "<span>Total Downloads</span><span>", "</span>").replaceAll(",", ""));
				
				List<FetchableMember> members = new ArrayList<>();
				{
					String rawMembers = InternalCFA.$cptr(page, "<h3 class=\"font-bold mb-3 text-lg\">Members</h3>", "</div></div></div></div>") + "</div></div></div></div>";
					
					for(String member : InternalCFA.$cptrs(rawMembers, "<div class=\"flex mb-2\">", "</div></div>"))
					{
						String msl = InternalCFA.$cptr(member, "<a href=\"/members/", "\">");
						String mname = InternalCFA.$cptr(member, "<a href=\"/members/" + msl + "\"><span>", "</span></a>");
						String mrole = InternalCFA.$cptr(member, "<p class=\"text-xs\">", "</p>");
						members.add(new FetchableMember(mname, mrole, this));
					}
				}
				
				String[] data = project.split("/");
				
				String rootGame = data[0];
				String rootGameCategory = data[1];
				
				return new CProject(name, overview, InternalCFA.$rlnk(desc), avatar, thumbnail, created, lastUpdate, projectId, totalDownloads, members, this, url, rootGameCategory, rootGame);
			}));
		return projectCache.get(project.toLowerCase());
	}
	
	@Override
	public Fetchable<IMember> member(String member)
	{
		if(!memberCache.containsKey(member.toLowerCase()))
			memberCache.put(member.toLowerCase(), createFetchable(() ->
			{
				boolean online = false;
				
				String base = url() + "members/" + member.toLowerCase();
				String page = ICurseForge.getPage(base + "/projects", true);
				
				String name = InternalCFA.$cptr(page, "<div class=\"username text-xl\">", "</div>");
				
				if(name == null)
					return null;
				
				String registeredUserIcon = InternalCFA.$cptr(page, "<div class=\"user-avatar pr-5\">", "</div></div>");
				String avatar = InternalCFA.$cptr(registeredUserIcon.toLowerCase(), "<a href=\"/members/" + name.toLowerCase() + "\"><img ", "</a>");
				if(avatar != null)
				{
					int start = avatar.indexOf("src=\"") + 5;
					int end = avatar.indexOf("\"", start);
					avatar = avatar.substring(start, end);
					int io = registeredUserIcon.toLowerCase().indexOf(avatar);
					avatar = registeredUserIcon.substring(io, io + avatar.length());
					online = registeredUserIcon.contains("<i class=\"u-icon u-icon-online\"></i>");
				}
				String memberSince = InternalCFA.$cptr(page, "Member Since: ", "\">");
				Date registerDate = null;
				
				try
				{
					registerDate = SDF1.parse(memberSince);
				} catch(ParseException e)
				{
					e.printStackTrace();
				}
				
				Date lastActive = InternalCFA.$abbr(InternalCFA.$cptr(page, "Last active <abbr class=\"tip standard-datetime-precise\" title=\"", "\">"));
				
				String followersStr = InternalCFA.$cptr(page, "<div class=\"followers w-1/3 border-r text-center p-3 border-gray--100\"><span>", "</span>");
				long followers = getLong(followersStr.split(" ")[0]);
				
				String postsStr = InternalCFA.$cptr(page, "<div class=\"posts w-1/3 border-r text-center p-3 border-gray--100\"><span class=\"tip\" title=\"(", "\">");
				String[] postMeta$ = postsStr.split(", ");
				long comments = getLong(postMeta$[0].split(" ")[0].substring(1));
				long forumPosts = getLong(postMeta$[1].split(" ")[0]);
				
				String likesStr = InternalCFA.$cptr(page, "<div class=\"likes w-1/3 text-center p-3 border-gray--100\"><span class=\"tip\" title=\"(", ")\">");
				String[] likeMeta$ = likesStr.split(", ");
				long th_rcv = getLong(likeMeta$[0].split(" ")[0]);
				long th_gvn = getLong(likeMeta$[1].split(" ")[0]);
				
				Supplier<List<FetchableProject>> projects = () ->
				{
					List<FetchableProject> prs = new ArrayList<>();
					List<String> ids = new ArrayList<>();
					int i = 1;
					while(true)
					{
						String pg = ICurseForge.getPage(base + "/projects?page=" + i, true);
						int added = 0;
						{
							for(String v : InternalCFA.$cptrs(pg, "<li class=\"latest-post-item project-list-bubble-item\">", "</figure></div></div></li>"))
							{
								String pr = InternalCFA.$cptr(v, "<a href=\"/", "\"");
								String avt = InternalCFA.$cptr(v, "<img src=\"", "\" alt");
								String author = InternalCFA.$cptr(v, "<div class=\"username text-xl\"><a href=\"/members/", "\"");
								if(!ids.contains(pr))
								{
									ids.add(pr);
									prs.add(new FetchableProject(InternalCFA.$cptr(InternalCFA.$cptr(v, "<h4>", "</h4>"), "\">", "</a>"), pr, avt, this, author));
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
					String txt = ICurseForge.getPage(base + "/followers", true).toLowerCase();
					Set<String> located = new HashSet<>(InternalCFA.$cptrs(txt, "<a href=\"/members/", "\""));
					located.removeIf(s -> s.contains("/"));
					located.remove(member.toLowerCase());
					return Collections.unmodifiableList(new ArrayList<>(located));
				};
				
				return new CMember(registerDate, lastActive, avatar, name, new MemberPosts(comments, forumPosts), new MemberThanks(th_gvn, th_rcv), followers, projects, this, base, followerList, online);
			}));
		return memberCache.get(member.toLowerCase());
	}
	
	@Override
	public CurseForgePrefs preferences()
	{
		return prefs;
	}
	
	@Override
	public ISearchResult<FetchableProject> searchProjects(CurseSearchDetails details, String query)
	{
		return new ProjectSearchResult(this, 1, details, query);
	}
	
	@Override
	public <T> Fetchable<T> createFetchable(Supplier<T> get)
	{
		return new Fetchable<>(get, prefs.getCacheLifespan().getVal(), prefs.getCacheLifespan().getUnit());
	}
	
	@Override
	public String url()
	{
		return InternalCFA.$cfidg("www");
	}
	
	@Override
	public IGame gameById(String id)
	{
		for(IGame game : allGames().get())
			if(game.id().equalsIgnoreCase(id))
				return game;
		return null;
	}
	
	@Override
	public Fetchable<List<IGame>> allGames()
	{
		if(games == null)
			games = createFetchable(() ->
			{
				List<IGame> games = new ArrayList<>();
				
				String page = ICurseForge.getPage(url() + "all-games", true);
				
				for(String str : InternalCFA.$cptrs(page, "<div class=\"flex-steady py-2 max-w-half w-1/3 flex-wrap md:w-1/6 px-2\"><a href=\"", "</article>"))
				{
					String gameId = InternalCFA.$cptr(str, "/", "\"");
					String coverImage = InternalCFA.$cptr(str, "<img class=\"absolute inset-0 h-full w-full block\" src=\"", "\"");
					String gameName = InternalCFA.$cptr(str, "<div class=\"flex flex-col\"><p>", "</p>");
					games.add(new CGame(gameId, gameName, coverImage, this));
				}
				
				return Collections.unmodifiableList(games);
			});
		return games;
	}
	
	private long getLong(String str)
	{
		try
		{
			return Long.parseLong(str);
		} catch(NumberFormatException nfe)
		{
			return 0L;
		}
	}
}