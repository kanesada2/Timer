package com.github.kanesada2.Timer;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class TimerBar{
	private OfflinePlayer owner;
	private int id,time,taskID;
	private boolean isRunning = false;
	private boolean isOPs = false;
	private BossBar bar;
	public TimerBar(int id, OfflinePlayer owner, int time){
		this.id = id;
		this.owner = owner;
		this.time = time;
		String title = String.valueOf(time) + " SEC";
		this.bar = Bukkit.getServer().createBossBar(title, BarColor.GREEN, BarStyle.SOLID);
		bar.setProgress(1);
	}
	public int getID(){
		return id;
	}
	public int getTaskID(){
		return taskID;
	}
	public OfflinePlayer getOwner(){
		return owner;
	}
	public int getTime(){
		return time;
	}
	public boolean isRunning(){
		return isRunning;
	}
	public boolean isOPs(){
		return isOPs;
	}
	public List<Player> getPlayers(){
		return bar.getPlayers();
	}
	public BossBar getBar(){
		return bar;
	}
	public void setTaskID(int taskID){
		this.taskID = taskID;
	}
	public void setOwner(OfflinePlayer owner){
		this.owner = owner;
	}
	public void setRunning(boolean running){
		this.isRunning = running;
	}
	public void setOPs(boolean OPs){
		this.isOPs = OPs;
	}
	public void clock(double delta){
		bar.setProgress(bar.getProgress() - delta);
		this.time--;
	}
	public void addPlayer(Player player){
		bar.addPlayer(player);
	}
	public void removePlayer(Player player){
		bar.removePlayer(player);
	}
	public void remove(){
		bar.setVisible(false);
		bar.removeAll();
	}
}
