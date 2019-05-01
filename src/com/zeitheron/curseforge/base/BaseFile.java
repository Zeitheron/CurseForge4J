package com.zeitheron.curseforge.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.zeitheron.curseforge.CurseforgeAPI;
import com.zeitheron.curseforge.ICurseForge;
import com.zeitheron.curseforge.IProject;
import com.zeitheron.curseforge.IProjectFile;
import com.zeitheron.curseforge.data.FetchableFile;
import com.zeitheron.curseforge.data.ProjectMember;

public class BaseFile implements IProjectFile
{
	protected final IProject project;
	protected final String id, displayName, fileName, md5;
	protected final Date uploaded;
	protected final long downloads;
	protected final List<FetchableFile> additionalFiles;
	protected final ProjectMember uploader;
	
	public BaseFile(IProject project, String id, String displayName, String fileName, String md5, Date uploaded, long downloads, List<String> additionalFiles, ProjectMember uploader)
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
	public ProjectMember uploader()
	{
		return uploader;
	}
	
	public static IProjectFile create(IProject proj, String id)
	{
		String url = proj.url() + "/files/" + id;
		String page = ICurseForge.getPage(url, true);
		
		String displayName = CurseforgeAPI.$cptr(page, "<h3 class=\"overflow-tip\">", "</h3>");
		String fileName = CurseforgeAPI.$cptr(page, "<div class=\"info-label\">Filename</div><div class=\"info-data overflow-tip\">", "</div>");
		
		ProjectMember uploader = null;
		{
			String userTag = CurseforgeAPI.$cptr(page, "<div class=\"user-tag\">", "</a></div>");
			for(ProjectMember pm : proj.membersList())
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
		
		return new BaseFile(proj, id, displayName, fileName, md5, uploaded, downloads, fis, uploader);
	}
}