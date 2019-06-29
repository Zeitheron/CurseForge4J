package com.zeitheron.curseforge.data.game;

import com.zeitheron.curseforge.api.ICurseForge;
import com.zeitheron.curseforge.api.IGame;
import com.zeitheron.curseforge.api.IGameCategory;
import com.zeitheron.curseforge.data.utils.ToStringHelper;
import com.zeitheron.curseforge.data.utils.ToStringHelper.Ignore;

public class CGameCat implements IGameCategory
{
	public final String id, name;
	
	@Ignore
	public final IGame game;
	
	@Ignore
	public final ICurseForge cf;
	
	public CGameCat(String id, String name, IGame game)
	{
		this.id = id;
		this.name = name;
		this.game = game;
		this.cf = game.curseForge();
	}
	
	@Override
	public String id()
	{
		return id;
	}
	
	@Override
	public String name()
	{
		return name;
	}

	@Override
	public String url()
	{
		return cf.url() + game.id() + "/" + id();
	}

	@Override
	public IGame game()
	{
		return game;
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