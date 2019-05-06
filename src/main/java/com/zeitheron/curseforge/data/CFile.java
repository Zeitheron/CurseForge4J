package com.zeitheron.curseforge.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.zeitheron.curseforge.CurseforgeAPI;
import com.zeitheron.curseforge.api.ICurseForge;
import com.zeitheron.curseforge.api.IProject;
import com.zeitheron.curseforge.api.IProjectFile;

public class CFile implements IProjectFile
{
	private static final Map<String, Long> bytes = new HashMap<>();
	static
	{
		bytes.put("kb", 1024L);
		bytes.put("mb", 1024L * 1024L);
		bytes.put("gb", 1024L * 1024L * 1024L);
		bytes.put("tb", 1024L * 1024L * 1024L * 1024L);
	}
	
	protected final IProject project;
	protected final String id, displayName, fileName, md5;
	protected final Date uploaded;
	protected final long downloads, sizel;
	protected final List<FetchableFile> additionalFiles;
	protected final FetchableMember uploader;
	protected final String changelog;
	protected final String size;
	
	public CFile(IProject project, String id, String displayName, String fileName, String md5, Date uploaded, long downloads, List<String> additionalFiles, FetchableMember uploader, String changelog, String size)
	{
		this.project = project;
		this.id = id;
		this.displayName = displayName;
		this.fileName = fileName;
		this.md5 = md5;
		this.uploaded = uploaded;
		this.downloads = downloads;
		this.additionalFiles = additionalFiles.stream().map(fi -> new FetchableFile(project, fi)).collect(Collectors.toList());
		this.uploader = uploader;
		this.changelog = changelog;
		this.size = size;
		String[] val = size.split(" ");
		this.sizel = Math.round(Double.parseDouble(val[0]) * bytes.getOrDefault(val[1].toLowerCase(), 1L));
	}
	
	@Override
	public String url()
	{
		return project.url() + "/files/" + id;
	}
	
	@Override
	public String downloadURL()
	{
		return url() + "/download";
	}
	
	@Override
	public String displayName()
	{
		return displayName;
	}
	
	@Override
	public String fileName()
	{
		return fileName;
	}
	
	@Override
	public String md5()
	{
		return md5;
	}
	
	@Override
	public Date uploaded()
	{
		return uploaded;
	}
	
	@Override
	public long downloads()
	{
		return downloads;
	}
	
	@Override
	public IProject project()
	{
		return project;
	}
	
	@Override
	public List<FetchableFile> additionalFiles()
	{
		return additionalFiles;
	}
	
	@Override
	public FetchableMember uploader()
	{
		return uploader;
	}
	
	@Override
	public String changelog()
	{
		return changelog;
	}
	
	public static IProjectFile create(IProject proj, String id)
	{
		String url = proj.url() + "/files/" + id;
		String page = ICurseForge.getPage(url, true);
		
		String displayName = CurseforgeAPI.$cptr(page, "<h3 class=\"overflow-tip\">", "</h3>");
		String fileName = CurseforgeAPI.$cptr(page, "<div class=\"info-label\">Filename</div><div class=\"info-data overflow-tip\">", "</div>");
		
		FetchableMember uploader = null;
		{
			String userTag = CurseforgeAPI.$cptr(page, "<div class=\"user-tag\">", "</a></div>");
			for(FetchableMember pm : proj.membersList())
				if(userTag.contains(pm.name()))
				{
					uploader = pm;
					break;
				}
		}
		
		String downloadsStr = CurseforgeAPI.$cptr(page, "<div class=\"info-label\">Downloads</div><div class=\"info-data\">", "</div>");
		long downloads = Long.parseLong(downloadsStr.replaceAll(",", "").replaceAll(" ", ""));
		
		String md5 = CurseforgeAPI.$cptr(page, "<span class=\"md5\">", "</span>");
		
		Date uploaded = CurseforgeAPI.$abbr(CurseforgeAPI.$cptr(page, "<div class=\"info-label\">Uploaded</div>", "</abbr></div>"));
		
		List<String> fis = new ArrayList<>();
		
		{
			String add = CurseforgeAPI.$cptr(page, "<tbody>", "</tbody>");
			if(add != null)
			{
				fis.addAll(CurseforgeAPI.$cptrs(add, "/files/", "\""));
				fis.removeIf(s -> s.contains("download"));
			}
		}
		
		String changelog = CurseforgeAPI.$rlnk(CurseforgeAPI.$cptr(page, "<div class=\"logbox\">", "</div></section>"));
		String size = CurseforgeAPI.$cptr(page, "<div class=\"info-label\">Size</div><div class=\"info-data\">", "</div>");
		
		return new CFile(proj, id, displayName, fileName, md5, uploaded, downloads, fis, uploader, changelog, size);
	}

	@Override
	public String size()
	{
		return size;
	}

	@Override
	public long sizeBytes()
	{
		return sizel;
	}
}