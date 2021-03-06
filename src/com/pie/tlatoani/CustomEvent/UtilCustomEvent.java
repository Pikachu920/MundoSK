package com.pie.tlatoani.CustomEvent;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;

public class UtilCustomEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	private String id;
	private Object[] args;
	@SuppressWarnings("rawtypes")
	private Map<ClassInfo, Object> details = new HashMap<ClassInfo, Object>();
	
	public UtilCustomEvent(String id, Object[] details, Object[] args) {
		this.id = id;
		for (int i = 0; i < details.length; i++) 
			this.details.put(Classes.getSuperClassInfo(details[i].getClass()), details[i]);
		this.args = args;
	}
	
	public Object getDetail(ClassInfo<?> type) {
		return details.containsKey(type) ? details.get(type) : null ;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public String getID() {
		return id;
	}
	
	public Object[] getArgs() {
		return args;
	}

}
