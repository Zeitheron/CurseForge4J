import java.util.List;
import java.util.concurrent.TimeUnit;

import com.zeitheron.curseforge.CurseforgeAPI;
import com.zeitheron.curseforge.api.EnumSortRule;
import com.zeitheron.curseforge.api.ICurseForge;
import com.zeitheron.curseforge.api.IGameVersion;
import com.zeitheron.curseforge.api.IMember;
import com.zeitheron.curseforge.api.IProject;
import com.zeitheron.curseforge.api.IProjectFile;
import com.zeitheron.curseforge.api.IProjectList;
import com.zeitheron.curseforge.data.CurseForgePrefs;
import com.zeitheron.curseforge.data.FetchableFile;
import com.zeitheron.curseforge.data.FetchableProject;
import com.zeitheron.curseforge.data.TimeHolder;

public class TestCF
{
	public static void main(String[] args)
	{
		CurseForgePrefs prefs = new CurseForgePrefs();
		prefs.setCacheLifespan(new TimeHolder(10L, TimeUnit.MINUTES));
		ICurseForge mc = CurseforgeAPI.minecraft(prefs);
		
		System.out.println(mc.member("Ircmaan").get().avatarURL());
		System.out.println(mc.member("Zeitheron").get().avatarURL());
		
		// Test Project List
		testProjectList(mc);
		
		// Test Search
		testSearch(mc);
		
		// Print newest version of Hammer Lib
		testFileList(mc);
		
		for(int i = 0; i < 8; ++i)
			System.out.println();
			
		// Run first iteration - it is going to be slow, since we cache
		// everything
		test(mc);
	}
	
	public static void testProjectList(ICurseForge mc)
	{
		IGameVersion ver = mc.gameVersions().get().stream().filter(v -> v.displayName().equals("1.13.2")).findFirst().orElse(null);
		
		IProjectList list = mc.listCategory(CurseforgeAPI.CATEGORY_MC_MODS, EnumSortRule.LAST_UPDATED, ver);
		
		System.out.println("First page of last updates mods:");
		for(FetchableProject fp : list.projects().get())
			System.out.println(" - " + fp.name());
		System.out.println("-------------------------------");
	}
	
	public static void testSearch(ICurseForge mc)
	{
		String query = "Solar Flux";
		System.out.println("Searching for \"" + query + "\"");
		List<FetchableProject> fps = mc.searchProjects(query).getElements();
		System.out.println("Found " + fps.size() + " elements (page 1):");
		for(FetchableProject fp : fps)
			System.out.println(" - " + fp);
	}
	
	public static void testFileList(ICurseForge mc)
	{
		IProject project = mc.project("hammer-lib").get();
		
		System.out.println("Listing all files...");
		for(int page = 1; page <= project.files().pageCount(); ++page)
		{
			System.out.println("Page " + page);
			List<FetchableFile> ffs = project.files().page(page).files().get();
			if(ffs.isEmpty())
				break;
			for(FetchableFile ff : ffs)
				System.out.println(" -" + ff.fetch().get().displayName());
		}
		
		System.out.println("Name: " + project.name() + "; Overview: " + project.overview());
		System.out.println("Description (HTML): " + project.description());
		System.out.println("Avatar | Thumbnail: " + project.avatar() + " | " + project.thumbnail());
		System.out.println("Total downloads: " + String.format("%,d", project.totalDownloads()));
		System.out.println("Last updated: " + project.lastUpdate());
		System.out.println("Created @: " + project.createTime());
		System.out.println("Type: " + project.category());
		System.out.println("Members: " + project.membersList());
		
		System.out.println();
		System.out.println("------------------------------------------");
		System.out.println("Querying latest file...");
		System.out.println();
		
		IProjectFile latest = project //
		        .files() //
		        .latest() //
		        .fetch() //
		        .get();
		
		System.out.println("Latest:");
		printFileInfo(latest, "  ");
		
		System.out.println("Searching for the file... ");
		System.out.println(mc.searchProjects(latest.md5()).getElements());
	}
	
	private static void printFileInfo(IProjectFile file, String prefix)
	{
		System.out.println(prefix + "Display name: " + file.displayName());
		System.out.println(prefix + "File name: " + file.fileName());
		System.out.println(prefix + "MD5: " + file.md5());
		System.out.println(prefix + "Downloads: " + file.downloads());
		System.out.println(prefix + "Uploader: " + file.uploader());
		System.out.println(prefix + "Uploaded: " + file.uploaded());
		System.out.println(prefix + "Changelog: " + file.changelog());
		System.out.println(prefix + "Size: " + file.size() + " (~" + file.sizeBytes() + " bytes)");
		if(!file.additionalFiles().isEmpty())
		{
			System.out.println(prefix + "Additional files:");
			for(FetchableFile add : file.additionalFiles())
			{
				System.out.println(prefix + "  " + add.id() + ":");
				printFileInfo(add.fetch().fetchNow(), prefix + "    ");
			}
		}
	}
	
	public static void test(ICurseForge mc)
	{
		IMember member = mc.member("Zeitheron").get();
		
		System.out.println("Name: " + member.name());
		System.out.println("Registered @: " + member.registerDate());
		System.out.println("Last active: " + member.lastActive());
		System.out.println("Followers: " + member.followers() + " " + member.followerList());
		System.out.println("Posts: " + member.posts().total() + " Total - " + member.posts().comments() + " comments, " + member.posts().forumPosts() + " forum posts.");
		System.out.println("Thanks: " + member.thanks().total() + " Total - " + member.thanks().received() + " received, " + member.thanks().given() + " given.");
		System.out.println("Projects: " + member.projects().size());
		
		int i = 0;
		for(FetchableProject proj : member.projects())
		{
			System.out.println("- " + proj);
			++i;
			if(i >= 4)
			{
				System.out.println("-- " + (member.projects().size() - i) + " more");
				break;
			}
		}
		
		System.out.println();
		System.out.println("------------------------------------------");
		System.out.println();
		
		IProject project = member.projects().get(0).fetch().get();
		
		System.out.println("Newest updated project: " + project);
		System.out.println("Latest:");
		printFileInfo(project.files().latest().fetch().get(), "  ");
	}
}