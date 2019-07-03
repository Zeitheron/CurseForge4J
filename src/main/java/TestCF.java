import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.zeitheron.curseforge.api.CurseForgeAPI;
import com.zeitheron.curseforge.api.CurseSearchDetails;
import com.zeitheron.curseforge.api.ICurseForge;
import com.zeitheron.curseforge.api.IGame;
import com.zeitheron.curseforge.api.IGameCategory;
import com.zeitheron.curseforge.api.IMember;
import com.zeitheron.curseforge.api.IProject;
import com.zeitheron.curseforge.api.IProjectFile;
import com.zeitheron.curseforge.data.CurseForgePrefs;
import com.zeitheron.curseforge.data.InternalCFA;
import com.zeitheron.curseforge.data.project.FetchableFile;
import com.zeitheron.curseforge.data.project.FetchableProject;
import com.zeitheron.curseforge.data.utils.TimeHolder;

public class TestCF
{
	public static void main(String[] args)
	{
		try
		{
			CurseForgePrefs prefs = new CurseForgePrefs();
			prefs.setCacheLifespan(new TimeHolder(10L, TimeUnit.MINUTES));
			ICurseForge mc = CurseForgeAPI.create(prefs);
			
			System.out.println(mc.member("Trikzon").get());
			System.out.println(mc.member("Zeitheron").get());
			
			// Test Search
			testSearch(mc);
			
			// Run first iteration - it is going to be slow, since we cache
			// everything
			test(mc);
			
			for(int i = 0; i < 8; ++i)
				System.out.println();
			
			for(int i = 0; i < 8; ++i)
				System.out.println();
			
			for(int i = 0; i < 8; ++i)
				System.out.println();
			
			// Print newest version of JEI
			testFileList(mc);
			
			for(IGame game : mc.allGames().get())
			{
				System.out.println(game.name() + ":");
				for(IGameCategory cat : game.categories().get())
					System.out.println(" - " + cat);
			}
		} catch(Throwable err)
		{
			err.printStackTrace();
		}
	}
	
	public static void testSearch(ICurseForge mc)
	{
		String query = "Solar Flux";
		System.out.println("Searching for \"" + query + "\" in " + mc.gameById("minecraft").categoryById("mc-mods"));
		List<FetchableProject> fps = mc.searchProjects(new CurseSearchDetails(mc.gameById("minecraft"), mc.gameById("minecraft").categoryById("mc-mods")), query).getElements();
		System.out.println("Found " + fps.size() + " elements (page 1):");
		
		long start = System.currentTimeMillis();
		List<IProject> projects = mc.executor().fetchAndWaitForAll(fps.stream().map(f -> f.fetch()).collect(Collectors.toList()));
		System.out.println("NEW FETCH ALGORITHM >> " + (System.currentTimeMillis() - start) + " ms.");
		for(IProject fp : projects)
			System.out.println(" - " + fp.description());
	}
	
	public static void testFileList(ICurseForge mc)
	{
		IProject project = mc.project(320552L).get();
		
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
		System.out.println("Posts: " + member.posts().total() + " Total");
		System.out.println("Thanks: " + member.thanks().total() + " Total - " + member.thanks().received() + " received, " + member.thanks().given() + " given.");
		System.out.println("Projects: " + member.projects().get().size());
		
		int i = 0;
		for(FetchableProject proj : member.projects().get())
		{
			System.out.println("- " + proj);
			++i;
			if(i >= 4)
			{
				System.out.println("-- " + (member.projects().get().size() - i) + " more");
				break;
			}
		}
		
		System.out.println();
		System.out.println("------------------------------------------");
		System.out.println();
		
		IProject project = member.projects().get().get(0).fetch().get();
		
		System.out.println("Newest updated project: " + project.description());
		System.out.println("Latest: ");
		printFileInfo(project.files().latest().fetch().get(), "  ");
	}
}