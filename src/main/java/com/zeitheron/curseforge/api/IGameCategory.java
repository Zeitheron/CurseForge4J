package com.zeitheron.curseforge.api;

public interface IGameCategory
{
	public static IGameCategory WILDCARD = new IGameCategory()
	{
		@Override
		public String url()
		{
			return null;
		}
		
		@Override
		public String id()
		{
			return null;
		}
		
		@Override
		public String name()
		{
			return null;
		}
		
		@Override
		public IGame game()
		{
			return null;
		}
		
		@Override
		public ICurseForge curseForge()
		{
			return null;
		}
	};
	
	String id();
	
	String name();
	
	String url();
	
	IGame game();
	
	ICurseForge curseForge();
	
	default boolean wildcard()
	{
		return this == WILDCARD;
	}
}