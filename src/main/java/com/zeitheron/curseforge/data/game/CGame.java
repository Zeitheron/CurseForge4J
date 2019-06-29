package com.zeitheron.curseforge.data.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.zeitheron.curseforge.api.ICurseForge;
import com.zeitheron.curseforge.api.IGame;
import com.zeitheron.curseforge.api.IGameCategory;
import com.zeitheron.curseforge.data.InternalCFA;
import com.zeitheron.curseforge.data.utils.Fetchable;
import com.zeitheron.curseforge.data.utils.ToStringHelper;
import com.zeitheron.curseforge.data.utils.ToStringHelper.Ignore;

public class CGame implements IGame
{
	public final String name, id, coverImage;
	
	@Ignore
	public final ICurseForge cf;
	
	@Ignore
	protected final Fetchable<List<IGameCategory>> catGet;
	
	public CGame(String id, String name, String cover, ICurseForge cf)
	{
		this.name = name;
		this.id = id;
		this.coverImage = cover;
		this.cf = cf;
		this.catGet = cf.createFetchable(() ->
		{
			List<IGameCategory> cats = new ArrayList<>();
			List<String> path = new ArrayList<>();
			String baseURL = cf.url() + "/" + id;
			String page = ICurseForge.getPage(baseURL, true, path::add);
			
			for(String cat : InternalCFA.$cptrs(page, "<div class=\"mr-2 pb-1", "</div>"))
			{
				String cid = InternalCFA.$cptr(cat, id + "/", "\">");
				String cname = InternalCFA.$cptr(cat, cid + "\">", "</a>");
				cats.add(new CGameCat(cid, cname, this));
			}
			
			if(cats.isEmpty())
			{
				String url = path.get(0);
				if(url.indexOf(baseURL) == 0)
				{
					String cid = url.substring(baseURL.length()) + 1;
					String cname = InternalCFA.$cptr(page, "<h2 class=\"text-xl font-bold\">All ", "</h2>");
					cats.add(new CGameCat(cid, cname, this));
				}
			}
			
			return Collections.unmodifiableList(cats);
		});
	}
	
	@Override
	public String name()
	{
		return name;
	}
	
	@Override
	public String id()
	{
		return id;
	}
	
	@Override
	public String coverImageUrl()
	{
		return coverImage;
	}
	
	@Override
	public Fetchable<List<IGameCategory>> categories()
	{
		return catGet;
	}
	
	@Override
	public ICurseForge curseForge()
	{
		return cf;
	}
	
	@Override
	public String toString()
	{
		return ToStringHelper.toString(this);
	}
}