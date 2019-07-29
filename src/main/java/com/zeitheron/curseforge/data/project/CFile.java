package com.zeitheron.curseforge.data.project;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.zeitheron.curseforge.api.ICurseForge;
import com.zeitheron.curseforge.api.IProject;
import com.zeitheron.curseforge.api.IProjectFile;
import com.zeitheron.curseforge.data.InternalCFA;
import com.zeitheron.curseforge.data.member.FetchableMember;
import com.zeitheron.curseforge.data.utils.ToStringHelper;
import com.zeitheron.curseforge.data.utils.ToStringHelper.Ignore;

public class CFile implements IProjectFile
{
	@Ignore
	private static final Map<String, Long> bytes = new HashMap<>();
	static
	{
		bytes.put("kb", 1024L);
		bytes.put("mb", 1024L * 1024L);
		bytes.put("gb", 1024L * 1024L * 1024L);
		bytes.put("tb", 1024L * 1024L * 1024L * 1024L);
	}
	
	@Ignore
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
	
	@Override
	public String toString()
	{
		return ToStringHelper.toString(this);
	}
	
	public static IProjectFile create(IProject proj, String id)
	{
		String url = proj.url() + "/files/" + id;
		String page = ICurseForge.getPage(url, true);
		
		String displayName = InternalCFA.$cptr(page, "<h3 class=\"text-primary-500 text-lg\">", "</h3>");
		String fileName = InternalCFA.$cptr(page, "<div class=\"flex flex-col mr-2\"><span class=\"font-bold text-sm leading-loose mb-1\">Filename</span><span class=\"text-sm\">", "</span></div>");
		
		FetchableMember uploader = null;
		{
			String userTag = InternalCFA.$cptr(page, "<span class=\"font-bold text-sm leading-loose\">Uploaded by</span>", "</span></a></div>");
			if(userTag != null)
			for(FetchableMember pm : proj.membersList())
				if(userTag.contains(pm.name()))
					{
						uploader = pm;
						break;
					}
		}
		
		String downloadsStr = InternalCFA.$cptr(page, "<span class=\"font-bold text-sm leading-loose\">Downloads</span><span class=\"text-sm\">", "</span>");
		long downloads = Long.parseLong(downloadsStr.replaceAll(",", "").replaceAll(" ", ""));
		
		String md5 = InternalCFA.$cptr(page, "<span class=\"font-bold text-sm leading-loose\">MD5</span><span class=\"text-sm\">", "</span>");
		
		Date uploaded = InternalCFA.$abbr(InternalCFA.$cptr(page, "<span class=\"font-bold text-sm leading-loose\">Uploaded</span>", "</abbr></div>"));
		
		List<String> fis = new ArrayList<>();
		
		String add = InternalCFA.$cptr(page, "<table class=\"listing listing-project-file", "</tbody></table>");
		if(add != null)
			fis.addAll(InternalCFA.$cptrs(add, "/download/", "\""));
		fis.removeIf(s -> s.contains("?"));
		
		String changelog = InternalCFA.$rlnk(InternalCFA.$cptr(page, "<h4 class=\"font-bold text-sm mb-2\">Changelog</h4><div class=\"bg-accent rounded py-1 pl-1 border-primary-100 border text-gray-500\"><div class=\"user-content min max-h-60 overflow-auto block\">", "</div></div></div>"));
		String size = InternalCFA.$cptr(page, "<span class=\"font-bold text-sm leading-loose\">Size</span><span class=\"text-sm\">", "</span>");
		
		return new CFile(proj, id, displayName == null || displayName.isEmpty() ? fileName : displayName, fileName, md5, uploaded, downloads, fis, uploader, changelog, size);
	}
}