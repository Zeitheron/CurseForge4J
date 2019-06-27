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

import com.zeitheron.curseforge.CurseforgeAPI;
import com.zeitheron.curseforge.api.EnumSortRule;
import com.zeitheron.curseforge.api.ICurseForge;
import com.zeitheron.curseforge.api.IGameVersion;
import com.zeitheron.curseforge.api.IMember;
import com.zeitheron.curseforge.api.IProject;
import com.zeitheron.curseforge.api.IProjectList;
import com.zeitheron.curseforge.api.ISearchResult;

public class GenericCurseforge implements ICurseForge
{
	static final SimpleDateFormat SDF1 = new SimpleDateFormat("MM/dd/yyyy");
	final String game;
	final CurseForgePrefs prefs = new CurseForgePrefs();
	final Map<String, Fetchable<IProject>> projectCache = new HashMap<>();
	final Map<String, Fetchable<IMember>> memberCache = new HashMap<>();
	final Map<Long, Fetchable<String>> projectIdToStrMap = new HashMap<>();
	final DefaultableMap<String, HashMap<Integer, GenericProjectList>> listStorage = new DefaultableMap<>(s -> new HashMap<>());
	Fetchable<List<String>> rootCats;
	
	GenericCurseforge(String game)
	{
		this.game = game;
	}
	
	@Override
	public Fetchable<String> projectIdByLID(long lid)
	{
		if(!projectIdToStrMap.containsKey(lid))
			projectIdToStrMap.put(lid, createFetchable(() ->
			{
				String url = url() + "projects/" + lid;
				String data = ICurseForge.getPage(url, true);
				return CurseforgeAPI.$cptr(data, "<div class=\"project-avatar project-avatar-64\"><a href=\"", "\"").substring(1);
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
				
				String name = CurseforgeAPI.$cptr(page, "<meta property=\"og:title\" content=\"", "\"");
				if(name == null)
					return null;
				
				String overview = CurseforgeAPI.$cptr(page, "<meta property=\"og:description\" content=\"", "\"");
				if(overview == null)
					return null;
				
				String avatar = null, thumbnail = null;
				{
					String avs = CurseforgeAPI.$cptr(page, "<div class=\"project-avatar project-avatar-64\">", "</a></div>");
					avatar = CurseforgeAPI.$cptr(avs, "data-featherlight=\"", "\">");
					thumbnail = CurseforgeAPI.$cptr(avs, "<img src=\"", "\"");
				}
				
				String desc = CurseforgeAPI.$cptr(page, "<section class=\"flex flex-col project-detail\"><div class=\"box p-4 pb-2 project-detail__content\" data-user-content>", "</div><div class=\"mt-6\"></div></section>");
				
				long projectId = Long.parseLong(CurseforgeAPI.$cptr(page, "<span>Project ID</span><span>", "</span>"));
				
				Date created = CurseforgeAPI.$abbr(CurseforgeAPI.$cptr(page, "<div class=\"info-label\">Created </div><div class=\"info-data\"><abbr", "</abbr>"));
				Date lastUpdate = CurseforgeAPI.$abbr(CurseforgeAPI.$cptr(page, "<div class=\"info-label\">Last Released File</div><div class=\"info-data\"><abbr", "</abbr>"));
				long totalDownloads = Long.parseLong(CurseforgeAPI.$cptr(page, "<span>Total Downloads</span><span>", "</span>").replaceAll(",", ""));
				
				List<FetchableMember> members = new ArrayList<>();
				{
					String rawMembers = CurseforgeAPI.$cptr(page, "<h3 class=\"font-bold mb-3 text-lg\">Members</h3>", "</div></div></div></div>") + "</div></div></div></div>";
					
					for(String member : CurseforgeAPI.$cptrs(rawMembers, "<div class=\"flex mb-2\">", "</div></div>"))
					{
						String msl = CurseforgeAPI.$cptr(member, "<a href=\"/members/", "\">");
						String mname = CurseforgeAPI.$cptr(member, "<a href=\"/members/" + msl + "\"><span>", "</span></a>");
						String mrole = CurseforgeAPI.$cptr(member, "<p class=\"text-xs\">", "</p>");
						members.add(new FetchableMember(mname, mrole, this));
					}
				}
				
				String[] data = project.split("/");
				
				String rootGame = data[0];
				String rootGameCategory = data[1];
				
				return new CProject(name, overview, CurseforgeAPI.$rlnk(desc), avatar, thumbnail, created, lastUpdate, projectId, totalDownloads, members, this, url, rootGameCategory, rootGame);
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
				
				String name = CurseforgeAPI.$cptr(page, "<div class=\"username text-xl\">", "</div>");
				
				if(name == null)
					return null;
				
				String registeredUserIcon = CurseforgeAPI.$cptr(page, "<div class=\"user-avatar pr-5\">", "</div></div>");
				String avatar = CurseforgeAPI.$cptr(registeredUserIcon.toLowerCase(), "<a href=\"/members/" + name.toLowerCase() + "\"><img ", "</a>");
				if(avatar != null)
				{
					int start = avatar.indexOf("src=\"") + 5;
					int end = avatar.indexOf("\"", start);
					avatar = avatar.substring(start, end);
					int io = registeredUserIcon.toLowerCase().indexOf(avatar);
					avatar = registeredUserIcon.substring(io, io + avatar.length());
					online = registeredUserIcon.contains("<i class=\"u-icon u-icon-online\"></i>");
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
				
				String followersStr = CurseforgeAPI.$cptr(page, "<div class=\"followers w-1/3 border-r text-center p-3 border-gray--100\"><span>", "</span>");
				long followers = Long.parseLong(followersStr.split(" ")[0]);
				
				String postsStr = CurseforgeAPI.$cptr(page, "<div class=\"posts w-1/3 border-r text-center p-3 border-gray--100\"><span class=\"tip\" title=\"(", "\">");
				String[] postMeta$ = postsStr.split(", ");
				long comments = Long.parseLong(postMeta$[0].split(" ")[0].substring(1));
				long forumPosts = Long.parseLong(postMeta$[1].split(" ")[0]);
				
				String likesStr = CurseforgeAPI.$cptr(page, "<div class=\"likes w-1/3 text-center p-3 border-gray--100\"><span class=\"tip\" title=\"(", ")\">");
				String[] likeMeta$ = likesStr.split(", ");
				long th_rcv = Long.parseLong(likeMeta$[0].split(" ")[0]);
				long th_gvn = Long.parseLong(likeMeta$[1].split(" ")[0]);
				
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
							for(String v : CurseforgeAPI.$cptrs(pg, "<li class=\"latest-post-item project-list-bubble-item\">", "</figure></div></div></li>"))
							{
								String pr = CurseforgeAPI.$cptr(v, "<a href=\"/", "\"");
								String avt = CurseforgeAPI.$cptr(v, "<img src=\"", "\" alt");
								if(!ids.contains(pr))
								{
									ids.add(pr);
									prs.add(new FetchableProject(CurseforgeAPI.$cptr(CurseforgeAPI.$cptr(v, "<h4>", "</h4>"), "\">", "</a>"), pr, avt, this));
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
					Set<String> located = new HashSet<>(CurseforgeAPI.$cptrs(txt, "<a href=\"/members/", "\""));
					located.removeIf(s -> s.contains("/"));
					located.remove(member.toLowerCase());
					return Collections.unmodifiableList(new ArrayList<>(located));
				};
				
				return new CMember(registerDate, lastActive, avatar, name, new MemberPosts(comments, forumPosts), new MemberThanks(th_gvn, th_rcv), followers, projects, this, base, followerList, online);
			}));
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
	
	@Override
	public ISearchResult<FetchableProject> searchProjects(String query)
	{
		return new ProjectSearchResult(this, 1, query);
	}
	
	@Override
	public IProjectList listCategory(String cat, EnumSortRule sort, IGameVersion version)
	{
		if(version == null)
			version = IGameVersion.NULL;
		HashMap<Integer, GenericProjectList> thisList = listStorage.get(version.toString());
		if(!thisList.containsKey(1))
			thisList.put(1, new GenericProjectList(cat, 1, sort, listStorage, this, version));
		return thisList.get(1);
	}
	
	@Override
	public <T> Fetchable<T> createFetchable(Supplier<T> get)
	{
		return new Fetchable<>(get, prefs.getCacheLifespan().getVal(), prefs.getCacheLifespan().getUnit());
	}
	
	@Override
	public String url()
	{
		return CurseforgeAPI.$cfidg(game());
	}
	
	@Override
	public Fetchable<List<String>> rootCategories()
	{
		if(rootCats == null)
			rootCats = createFetchable(() ->
			{
				String page = ICurseForge.getPage(url() + "projects", true);
				return Collections.unmodifiableList(CurseforgeAPI.$cptrs(page, "<div class=\"project-category\"><a class=\"project-icon\" href=\"/", "\""));
			});
		return rootCats;
	}
}