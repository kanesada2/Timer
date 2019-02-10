package com.github.kanesada2.Timer;

import org.bukkit.plugin.java.JavaPlugin;

public class Timer extends JavaPlugin {

	private TimerListener listener;
	private TimerCommandExecutor commandExecutor;

	@Override
    public void onEnable() {
        listener = new TimerListener(this);
        getServer().getPluginManager().registerEvents(listener, this);
        commandExecutor = new TimerCommandExecutor(this);
        getCommand("timer").setExecutor(commandExecutor);
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        new TimerBarsManager();
        getLogger().info("Timer Enabled!");
    }

    @Override
    public void onDisable() {
    	getLogger().info("Deleting all timers....");
    	TimerBarsManager.removeAllTimer();
    	getLogger().info("Successfully deleted!");
    }
}
