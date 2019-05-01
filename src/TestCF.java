import com.zeitheron.curseforge.CurseforgeAPI;
import com.zeitheron.curseforge.ICurseForge;
import com.zeitheron.curseforge.IMember;
import com.zeitheron.curseforge.IProject;
import com.zeitheron.curseforge.IProjectFile;
import com.zeitheron.curseforge.data.FetchableFile;
import com.zeitheron.curseforge.data.MembersProject;

public class TestCF
{
	public static void main(String[] args)
	{
		// Run first iteration - it is going to be slow, since we cache
		// everything
		// test();
		
		testFileList();
		
		for(int i = 0; i < 8; ++i)
			System.out.println();
			
		// Run second iteration - it's very fast, since eveything is already
		// cached.
		// test();
	}
	
	public static void testFileList()
	{
		ICurseForge mc = CurseforgeAPI.minecraft();
		IProject project = mc.project("always-online").get();
		
		System.out.println("Name: " + project.name() + "; Overview: " + project.overview());
		System.out.println("Description (HTML): " + project.description());
		System.out.println("Avatar | Thumbnail: " + project.avatar() + " | " + project.thumbnail());
		System.out.println("Total downloads: " + String.format("%,d", project.totalDownloads()));
		System.out.println("Last updated: " + project.lastUpdate());
		System.out.println("Created @: " + project.createTime());
		System.out.println("Members: " + project.membersList());
		
		System.out.println();
		System.out.println("------------------------------------------");
		System.out.println("Querying latest file...");
		System.out.println();
		
		IProjectFile latest = project //
		        .files() //
		        .latest() //
		        .asProjectFile() //
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
				printFileInfo(add.asProjectFile().fetchNow(), prefix + "    ");
			}
		}
	}
	
	public static void test()
	{
		ICurseForge mc = CurseforgeAPI.minecraft();
		
		IMember member = mc.member("Zeitheron").get();
		
		System.out.println("Name: " + member.name());
		System.out.println("Registered @: " + member.registerDate());
		System.out.println("Last active: " + member.lastActive());
		System.out.println("Followers: " + member.followers());
		System.out.println("Posts: " + member.posts().total() + " Total - " + member.posts().comments() + " comments, " + member.posts().forumPosts() + " forum posts.");
		System.out.println("Thanks: " + member.thanks().total() + " Total - " + member.thanks().received() + " received, " + member.thanks().given() + " given.");
		System.out.println("Projects: " + member.projects().size());
		
		int i = 0;
		for(MembersProject proj : member.projects())
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
		
		IProject project = member.projects().get(0).asProject().get();
		
		System.out.println("Name: " + project.name() + "; Overview: " + project.overview());
		System.out.println("Description (HTML): " + project.description());
		System.out.println("Avatar | Thumbnail: " + project.avatar() + " | " + project.thumbnail());
		System.out.println("Total downloads: " + String.format("%,d", project.totalDownloads()));
		System.out.println("Last updated: " + project.lastUpdate());
		System.out.println("Created @: " + project.createTime());
		System.out.println("Members: " + project.membersList());
	}
}