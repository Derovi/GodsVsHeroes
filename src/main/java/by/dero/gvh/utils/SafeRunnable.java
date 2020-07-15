package by.dero.gvh.utils;

import org.bukkit.scheduler.BukkitRunnable;

public abstract class SafeRunnable extends BukkitRunnable {
	@Override
	public synchronized void cancel () throws IllegalStateException {
		if (this.task != null) {
			super.cancel();
		}
	}
}
