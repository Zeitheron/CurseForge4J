package com.zeitheron.curseforge.api;

import java.util.List;

import com.zeitheron.curseforge.data.utils.Fetchable;

public interface IGame
{
	String name();
	
	String id();
	
	String coverImageUrl();
	
	Fetchable<List<IGameCategory>> categories();
	
	default IGameCategory categoryById(String id)
	{
		return categories().get().stream().filter(c -> c.id().equalsIgnoreCase(id)).findFirst().orElse(null);
	}
	
	ICurseForge curseForge();
}