package com.zeitheron.curseforge.api;

import com.zeitheron.curseforge.data.StringUtils;

public interface IGameVersion
{
	static IGameVersion NULL = new IGameVersion()
	{
		@Override
		public long entityId()
		{
			return 0;
		}
		
		@Override
		public long typeId()
		{
			return 0;
		}
		
		@Override
		public String displayName()
		{
			return "";
		}
		
		@Override
		public String toString()
		{
			return "";
		}
	};
	
	static IGameVersion create(String dname, long entityId, long typeId)
	{
		String name = StringUtils.unescapeHtml3(dname);
		return new IGameVersion()
		{
			@Override
			public long typeId()
			{
				return typeId;
			}
			
			@Override
			public long entityId()
			{
				return entityId;
			}
			
			@Override
			public String displayName()
			{
				return name;
			}
			
			@Override
			public String toString()
			{
				return typeId + ":" + entityId;
			}
		};
	}
	
	long typeId();
	
	long entityId();
	
	String displayName();
}