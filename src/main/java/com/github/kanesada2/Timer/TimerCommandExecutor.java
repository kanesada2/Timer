package com.github.kanesada2.Timer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class TimerCommandExecutor implements CommandExecutor, TabCompleter {

	private Timer plugin;

    public TimerCommandExecutor(Timer plugin) {
        this.plugin = plugin;
    }
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if (!cmd.getName().equalsIgnoreCase("Timer")) {
            return null;
        }
		ArrayList<String> completions = new ArrayList<String>();
		if(args.length == 1){
			if (args[0].length() == 0) {
				completions.add("?");
				completions.add("set");
				completions.add("start");
	            completions.add("cancel");
	            completions.add("leave");
	            completions.add("reroad");
			}else {
				if ("reload".startsWith(args[0])) {
	                completions.add("reload");
	            }
				if ("set".startsWith(args[0])) {
	                completions.add("set");
	            }
	            if ("start".startsWith(args[0])) {
	                completions.add("start");
	            }
	            if ("cancel".startsWith(args[0])) {
	                completions.add("cancel");
	            }
	            if ("leave".startsWith(args[0])) {
	                completions.add("leave");
	            }
	        }
		}
		return completions;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!cmd.getName().equalsIgnoreCase("Timer")) {
            return false;
        }
		switch(args.length){
			case 0:
				if(sender instanceof Player){
					if(sender.hasPermission("Timer.join")){
						HashSet<TimerBar> timers = TimerBarsManager.getTimers();
						if(timers.size() < 1){
							sender.sendMessage("No timer is set on this server now.");
							return false;
						}
						for(Iterator<TimerBar> itr = timers.iterator(); itr.hasNext();){
							TimerBar timer = itr.next();
							if(timer.getPlayers().contains((Player)sender)){
								sender.sendMessage("You are already a member of Timer#" + timer.getID() +".");
								return false;
							}
						}
						Inventory inventory = Bukkit.getServer().createInventory(null, ((int)(timers.size() + 8) / 9) * 9, "Choose the timer");
						for(TimerBar timer : timers){
							ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte)SkullType.PLAYER.ordinal());
							SkullMeta smeta = (SkullMeta)head.getItemMeta();
							smeta.setOwningPlayer(timer.getOwner());
							head.setItemMeta(smeta);
							ItemMeta meta = head.getItemMeta();
							meta.setDisplayName("Timer#" + timer.getID());
							List<String> lore = new ArrayList<String>();
							if(timer.isOPs()){
								lore.add("Owner: SERVER");
							}else{
								lore.add("Owner: " + timer.getOwner().getName());
							}
							lore.add(timer.getTime() + "SEC");
							lore.add(timer.getPlayers().size() + " players joined");
							if(timer.isRunning()){
								lore.add("NOW RUNNING");
							}
							meta.setLore(lore);
							head.setItemMeta(meta);
							inventory.addItem(head);
							((Player) sender).setMetadata("Timer'sViewer", new FixedMetadataValue(plugin,true));
						}
						((Player) sender).openInventory(inventory);
						return true;
					}else{
						sender.sendMessage("You don't have permission.");
					}
				}else if(sender instanceof ConsoleCommandSender){
					HashSet<TimerBar> timers = TimerBarsManager.getTimers();
					if(timers.size() < 1){
						sender.sendMessage("No timer is set on this server now.");
						return false;
					}
					for(TimerBar timer : timers){
						String ownersName = "SERVER";
						if(!timer.isOPs()){
							ownersName = timer.getOwner().getName();
						}
						String msg = "Timer#" + timer.getID() + "," + "Owner:" + ownersName + "," + timer.getTime() + "SEC," + timer.getPlayers().size() + "players joined";
						if(timer.isRunning()){
							msg += " NOW RUNNING";
						}
						sender.sendMessage(msg);
					}
					return true;
				}
				return false;
			case 1:
				if(args[0].equalsIgnoreCase("?")){
					String [] msgs = new String[7];
					msgs[0] = "/timer " + ChatColor.YELLOW + " show all available timers.";
					msgs[1] = "/timer ?" + ChatColor.YELLOW + " show all Timer's command.";
					msgs[2] = "/timer reload" + ChatColor.YELLOW + " reload Timer's config file.";
					msgs[3] = "/timer set <seconds> " + ChatColor.YELLOW + " prepare a new timer.";
					msgs[4] = "/timer start [ID]" + ChatColor.YELLOW + " start counting down your own timer. Normally ID option is only allowed for OPs.";
					msgs[5] = "/timer cancel [ID]" + ChatColor.YELLOW + " cancel your own timer. Normally ID option is only allowed for OPs.";
					msgs[6] = "/timer leave " + ChatColor.YELLOW + " remove you from the timer you arleady joined. You can't leave from your own timer.";
					sender.sendMessage(msgs);
					return true;
				}else if(args[0].equalsIgnoreCase("reload")){
					if(sender instanceof Player){
						if(!sender.hasPermission("Timer.reload")){
							sender.sendMessage("You don't have permisson.");
							return false;
						}
					}else if(!(sender instanceof ConsoleCommandSender)){
						return false;
					}
					plugin.reloadConfig();
					Bukkit.getLogger().info("Timer Reloaded!");
					return true;
				}else if(args[0].equalsIgnoreCase("start")){
					if(!(sender instanceof Player)){
						Bukkit.getLogger().info("Please send this command from game.");
						return false;
					}
					Player player = (Player)sender;
					HashSet<TimerBar> timers = TimerBarsManager.getTimers();
					if(timers.size() < 1){
						sender.sendMessage("No timer is set on this server now.");
						return false;
					}
					for(Iterator<TimerBar> itr = timers.iterator(); itr.hasNext();){
						TimerBar timer = itr.next();
						if(player.getUniqueId().equals(timer.getOwner().getUniqueId()) && !timer.isOPs()){
							if(timer.isRunning()){
								sender.sendMessage("Your timer is already running.");
								return false;
							}
							Util.startTimer(timer);
							return true;
						}else if(!itr.hasNext()){
							sender.sendMessage("You don't own any timer.");
						}
					}
				}else if(args[0].equalsIgnoreCase("cancel")){
					if(!(sender instanceof Player)){
						Bukkit.getLogger().info("Please send this command from game.");
						return false;
					}
					Player player = (Player)sender;
					HashSet<TimerBar> timers = TimerBarsManager.getTimers();
					if(timers.size() < 1){
						sender.sendMessage("No timer is set on this server now.");
						return false;
					}
					for(Iterator<TimerBar> itr = timers.iterator(); itr.hasNext();){
						TimerBar timer = itr.next();
						if(player.getUniqueId().equals(timer.getOwner().getUniqueId()) && !timer.isOPs()){
							TimerBarsManager.removeTimer(timer);
							sender.sendMessage("Your timer successfully removed.");
							return true;
						}else if(!itr.hasNext()){
							sender.sendMessage("You don't own any timer.");
						}
					}
				}else if(args[0].equalsIgnoreCase("leave")){
					if(!(sender instanceof Player)){
						Bukkit.getLogger().info("Please send this command from game.");
						return false;
					}
					Player player = (Player)sender;
					HashSet<TimerBar> timers = TimerBarsManager.getTimers();
					if(timers.size() < 1){
						sender.sendMessage("No timer is set on this server now.");
						return false;
					}
					for(Iterator<TimerBar> itr = timers.iterator(); itr.hasNext();){
						TimerBar timer = itr.next();
						if(player.getUniqueId().equals(timer.getOwner().getUniqueId()) && !timer.isOPs()){
							sender.sendMessage("You can't leave from your own timer.");
							return false;
						}
						if(timer.getPlayers().contains(player)){
							timer.removePlayer(player);
							sender.sendMessage("You leave from Timer#" + timer.getID() +".");
							return true;
						}else if(!itr.hasNext()){
							sender.sendMessage("You are not a member of any timer.");
						}
					}
				}else{
					sender.sendMessage("Unknown command. Please check /timer ?");
				}
				return false;
			case 2:
				if(args[0].equalsIgnoreCase("set")){
					if(sender instanceof Player){
						if(!sender.hasPermission("Timer.set")){
							sender.sendMessage("You don't have permission.");
							return false;
						}else{
							Player player = (Player)sender;
							HashSet<TimerBar> timers = TimerBarsManager.getTimers();
							for(Iterator<TimerBar> itr = timers.iterator(); itr.hasNext();){
								TimerBar timer = itr.next();
								if(player.getUniqueId().equals(timer.getOwner().getUniqueId()) && !timer.isOPs()){
									sender.sendMessage("You are already a owner of Timer#" + timer.getID() +".");
									return false;
								}
								if(timer.getPlayers().contains(player)){
									sender.sendMessage("You are already a member of Timer#" + timer.getID() +".");
									return false;
								}
							}
						}
					}
					if(Util.isNumber(args[1])){
						int time = Integer.parseInt(args[1]);
						if(time > plugin.getConfig().getInt("Max_Time",3600)){
							sender.sendMessage("Too long time is set. Max value is " + plugin.getConfig().getString("Max_Time","3600"));
							return false;
						}else if(time <= 0){
							sender.sendMessage("Time number must be bigger than 0. Going back is not suppoted yet. ");
							return false;
						}
						OfflinePlayer owner = Bukkit.getServer().getPlayer(UUID.fromString(plugin.getConfig().getString("Default_Owner_UUID", "150299c6-ad64-4ecf-bb5c-ede6702fd5c8")));
						if(sender instanceof Player){
							owner = (Player)sender;
							TimerBarsManager.addTimer(owner, time).addPlayer((Player)sender);
						}else{
							TimerBarsManager.addTimer(owner, time).setOPs(true);;
						}
						sender.sendMessage("Your timer is successfully set!!");
						return true;
					}else{
						sender.sendMessage("Please set a timer with a natural number.");
					}
				}else if(args[0].equalsIgnoreCase("cancel")){
					if(sender instanceof Player && !sender.hasPermission("Timer.cancelOthers")){
							sender.sendMessage("You don't have permission.");
							return false;
					}
					if(Util.isNumber(args[1])){
						if(TimerBarsManager.removeTimer(Integer.parseInt(args[1]))){
							sender.sendMessage("Timer#" + args[1] +" successfully removed.");
							return true;
						}else{
							sender.sendMessage("Timer#" + args[1] +" is not found.");
						}
					}else{
						sender.sendMessage("ID must be a natural number.");
					}
				}else if(args[0].equalsIgnoreCase("start")){
					if(sender instanceof Player && !sender.hasPermission("Timer.startOthers")){
						sender.sendMessage("You don't have permission.");
						return false;
				}
					if(Util.isNumber(args[1])){
						HashSet<TimerBar> timers = TimerBarsManager.getTimers();
						if(timers.size() < 1){
							sender.sendMessage("No timer is set on this server now.");
							return false;
						}
						for(Iterator<TimerBar> itr = timers.iterator(); itr.hasNext();){
							TimerBar timer = itr.next();
							if(timer.getID() == Integer.parseInt(args[1])){
								if(timer.isRunning()){
									sender.sendMessage("Timer#" + timer.getID() + "is already running.");
									return false;
								}
								Util.startTimer(TimerBarsManager.getTimer(Integer.parseInt(args[1])));
								sender.sendMessage("Timer#" + timer.getID() + "started!");
								return true;
							}else if(!itr.hasNext()){
								sender.sendMessage("Timer#" + args[1] +" is not found.");
							}
						}
					}else{
						sender.sendMessage("ID must be a natural number.");
					}
				}
				return false;
			default:
				sender.sendMessage("Unknown command. Please check /timer ?");
				return false;

		}
	}

}
