package by.dero.gvh.model.interfaces;

import org.bukkit.event.player.PlayerInteractEvent;

public interface DoubleHandInteractInterface {
	void interactMainHand(PlayerInteractEvent event);
	void interactOffHand(PlayerInteractEvent event);
}
