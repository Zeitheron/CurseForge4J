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
	final DefaultableMap<String, HashMap<Integer, GenericProjectList>> listStorage = new DefaultableMap<>(s -> new HashMap<>());
	Fetchable<List<String>> rootCats;
	Fetchable<List<IGameVersion>> gameVersions;
	
	GenericCurseforge(String game)
	{
		this.game = game;
	}
	
	@Override
	public Fetchable<IProject> project(String project)
	{
		if(!projectCache.containsKey(project.toLowerCase()))
			projectCache.put(project.toLowerCase(), createFetchable(() ->
			{
				String url = url() + "projects/" + project;
				
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
				
				url = url() + "projects/" + projectId;
				
				Date created = CurseforgeAPI.$abbr(CurseforgeAPI.$cptr(page, "<div class=\"info-label\">Created </div><div class=\"info-data\"><abbr", "</abbr>"));
				Date lastUpdate = CurseforgeAPI.$abbr(CurseforgeAPI.$cptr(page, "<div class=\"info-label\">Last Released File</div><div class=\"info-data\"><abbr", "</abbr>"));
				long totalDownloads = Long.parseLong(CurseforgeAPI.$cptr(page, "<div class=\"info-label\">Total Downloads</div><div class=\"info-data\">", "</div>").replaceAll(",", ""));
				
				List<FetchableMember> members = new ArrayList<>();
				{
					String rawMembers = CurseforgeAPI.$cptr(page, "<div class=\"cf-sidebar-inner\"><ul class=\"cf-details project-members\">", "</ul></div>");
					String[] mba = rawMembers.split("<li class=");
					for(int i = 1; i < mba.length; ++i)
					{
						String mbr = mba[i];
						String mbn = CurseforgeAPI.$cptr(mbr, "<span>", "</span>");
						String mbk = CurseforgeAPI.$cptr(mbr, "<span class=\"title\">", "</span>");
						if(mbn != null && mbk != null)
							members.add(new FetchableMember(mbn, mbk, this));
					}
				}
				;
				
				String rootGameCategory = CurseforgeAPI.$cptr(CurseforgeAPI.$cptr(page, "\"RootGameCategory\">", "</h2>"), "href=\"/", "\">");
				
				return new CProject(name, overview, CurseforgeAPI.$rlnk(desc), avatar, thumbnail, created, lastUpdate, projectId, totalDownloads, members, this, url, rootGameCategory);
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
				
				String base = url() + "members/" + member;
				String page = ICurseForge.getPage(base, true);
				
				String name = CurseforgeAPI.$cptr(page, "<li class=\"username\">", "</li>");
				
				if(name == null)
					return null;
				
				String registeredUserIcon = CurseforgeAPI.$cptr(page, "<div class=\"avatar avatar-100 user user-role-", "</div>");
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
							String[] values = pg.split("<a href=\"/projects");
							for(String v : values)
								if(v.charAt(0) == '/')
								{
									int e;
									String pr = v.substring(1, e = v.indexOf("\">"));
									if(!ids.contains(pr))
									{
										ids.add(pr);
										prs.add(new FetchableProject(v.substring(e + 2, v.indexOf("</a>")), pr, this));
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
	
	@Override
	public Fetchable<List<IGameVersion>> gameVersions()
	{
		if(gameVersions == null)
			gameVersions = createFetchable(() ->
			{
				List<IGameVersion> vs = new ArrayList<>();
				
				String cat = rootCategories().get().get(0);
				String page = ICurseForge.getPage(url() + cat, true);
				
				String select = CurseforgeAPI.$cptr(page, "<select id=\"filter-game-version\" name=\"filter-game-version\"", "</select>");
				for(String item : CurseforgeAPI.$cptrs(select, "<option", "</option>"))
				{
					if(item.startsWith("value=\""))
					{
						String id = item.substring(7);
						String[] ids = id.substring(0, id.indexOf("\"")).split(":");
						String name = item.substring(item.lastIndexOf(">") + 5);
						vs.add(IGameVersion.create(name, Long.parseLong(ids[1]), Long.parseLong(ids[0])));
					}
				}
				
				return Collections.unmodifiableList(vs);
			});
		return gameVersions;
	}
}