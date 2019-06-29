package com.zeitheron.curseforge.api;

import com.zeitheron.curseforge.data.utils.Fetchable;

public interface IQFetchable<FETCHED>
{
	Fetchable<FETCHED> fetch();
}