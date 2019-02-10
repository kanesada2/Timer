package com.github.kanesada2.Timer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class TimerListener implements Listener {
	private Timer plugin;

	public TimerListener(Timer plugin) {
        this.plugin = plugin;
    }
	@EventHandler(priority = EventPriority.LOW)
	public void onClicked(InventoryClickEvent e){
		if(!e.getWhoClicked().hasMetadata("Timer'sViewer")){
			return;
		}
		e.setCancelled(true);
		if(Math.abs(e.getRawSlot()) >= e.getInventory().getSize()){
			return;
		}
		if(!(e.isLeftClick() && e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName()) || e.isShiftClick()){
			return;
		}
		Player player = (Player)e.getWhoClicked();
		int ID = Integer.parseInt(e.getCurrentItem().getItemMeta().getDisplayName().split("#")[1]);
		TimerBar timer = TimerBarsManager.getTimer(ID);
		if(timer == null){
			player.sendMessage("This timer is already expired.");
			return;
		}
		if(timer.getPlayers().contains(player)){
			player.sendMessage("You are already a member of this timer.");
			return;
		}
		for(Player member : timer.getPlayers()){
			if(member.getUniqueId().equals(player.getUniqueId())){
				timer.removePlayer(member);
			}
		}
		timer.addPlayer(player);
		if(timer.isOPs()){
			Bukkit.getLogger().info(player.getDisplayName() + " joined Timer#" + timer.getID());
		}else if(timer.getOwner().isOnline()){
			((Player)timer.getOwner()).sendMessage(ChatColor.GREEN + player.getDisplayName() + " joined your timer!");
		}
		plugin.getServer().getScheduler().runTask(plugin, new Runnable()
	      {
	        @Override
	        public void run()
	        {
	        	player.closeInventory();
	        }
	      });
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onCloseInventory(InventoryCloseEvent e){
		if(!e.getPlayer().hasMetadata("Timer'sViewer")){
			return;
		}
		e.getPlayer().removeMetadata("Timer'sViewer", plugin);
	}
}
