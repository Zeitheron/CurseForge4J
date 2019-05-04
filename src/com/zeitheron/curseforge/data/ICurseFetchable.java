package com.zeitheron.curseforge.data;

import com.zeitheron.curseforge.api.ICurseForge;

public interface ICurseFetchable<T>
{
	T fetch(ICurseForge forge);
}