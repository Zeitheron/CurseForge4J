package com.zeitheron.curseforge;

public interface ICurseFetchable<T>
{
	T fetch(ICurseForge forge);
}