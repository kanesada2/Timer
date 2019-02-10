package com.github.kanesada2.Timer;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class Util {
	private static Timer plugin = Timer.getPlugin(Timer.class);
	public static boolean isNumber(String number){
		try {
	        Integer.parseInt(number);
	        return true;
	        } catch (NumberFormatException e) {
	        return false;
	    }
	}
	public static void startTimer(TimerBar timer){
		BukkitTask task = new TimerTask(timer).runTaskTimer(plugin, 0, 20);
		timer.setRunning(true);
		timer.setTaskID(task.getTaskId());
		Sound sound =  Sound.valueOf(plugin.getConfig().getString("Sounds.Timer_Start", "ENTITY_WITHER_SPAWN"));
		for(Player player : timer.getPlayers()){
			player.sendTitle(plugin.getConfig().getString("Msg.Timer_Start"), "", 10, 20, 10);
			player.playSound(player.getLocation(), sound , 1, 1);
		}
	}
	public static void finishTimer(TimerBar timer){
		Sound sound =  Sound.valueOf(plugin.getConfig().getString("Sounds.Timer_Finish", "BLOCK_ANVIL_USE"));
		for(Player player : timer.getPlayers()){
			player.sendTitle(plugin.getConfig().getString("Msg.Timer_Finish"), "", 10, 20, 10);
			player.playSound(player.getLocation(), sound , 1, 1);
		}
		timer.setRunning(false);
		TimerBarsManager.removeTimer(timer.getID());
	}

}
