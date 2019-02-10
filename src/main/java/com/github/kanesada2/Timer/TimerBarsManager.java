package com.github.kanesada2.Timer;

import java.util.HashSet;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public  class TimerBarsManager {
	private static HashSet<TimerBar> timers;
	private static int id;
	public TimerBarsManager(){
		TimerBarsManager.timers = new HashSet<TimerBar>();
		id = 0;
	}
	public static HashSet<TimerBar> getTimers(){
		return timers;
	}
	public static TimerBar getTimer(int id){
		for(Iterator<TimerBar> iterator = timers.iterator(); iterator.hasNext();){
			TimerBar timer = iterator.next();
			if(id == timer.getID()){
				return timer;
			}
		}
		return null;
	}
	public static TimerBar addTimer(OfflinePlayer owner, int time){
		id++;
		TimerBar timer = new TimerBar(id, owner,time);
		timers.add(timer);
		return timer;
	}
	public static void removeTimer(TimerBar timer){
		timer.remove();
		timers.remove(timer);
		if(timer.isRunning()){
			Bukkit.getServer().getScheduler().cancelTask(timer.getTaskID());
		}
	}
	public static boolean removeTimer(int id){
		for(Iterator<TimerBar> iterator = timers.iterator(); iterator.hasNext();){
			TimerBar timer = iterator.next();
			if(id == timer.getID()){
				removeTimer(timer);
				return true;
			}
		}
		return false;
	}
	public static void removeAllTimer(){
		for(Iterator<TimerBar> iterator = timers.iterator(); iterator.hasNext();){
			TimerBar timer = iterator.next();
			iterator.remove();
			timer.remove();
			if(timer.isRunning()){
				Bukkit.getServer().getScheduler().cancelTask(timer.getTaskID());
			}
		}
	}

}
