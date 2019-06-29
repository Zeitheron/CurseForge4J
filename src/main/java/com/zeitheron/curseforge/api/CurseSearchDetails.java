package com.zeitheron.curseforge.api;

public class CurseSearchDetails
{
	public final IGame game;
	public final IGameCategory category;
	
	public CurseSearchDetails(IGame game, IGameCategory cat)
	{
		this.game = game;
		this.category = cat;
	}
	
	public CurseSearchDetails(IGame game)
	{
		this(game, IGameCategory.WILDCARD);
	}
}