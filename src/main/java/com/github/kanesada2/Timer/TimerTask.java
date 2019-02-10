package com.github.kanesada2.Timer;

import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitRunnable;

public class TimerTask extends BukkitRunnable {
	TimerBar timer;
	BossBar bar;
	double span;
	public TimerTask(TimerBar timer){
		this.timer = timer;
		this.bar = timer.getBar();
		this.span = 1d / (double)timer.getTime();
	}
	@Override
	public void run() {
		if(bar.getProgress() < span){
			bar.setProgress(0);
			Util.finishTimer(timer);
			this.cancel();
		}else{
			timer.clock(span);
			bar.setTitle(timer.getTime() + " SEC");
		}

	}

}
