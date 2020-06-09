package by.dero.gvh.events;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.ShootBowInterface;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerEvents implements Listener {
    @EventHandler
    public void onEntityShoot(org.bukkit.event.entity.EntityShootBowEvent event) {
        if ((event.getEntity() instanceof Player)) {
            String playerName = event.getEntity().getName();
            Item selectedItem =  Plugin.getInstance().getGame().getPlayers().get(playerName).getSelectedItem();
            if (selectedItem instanceof ShootBowInterface) {
                ((ShootBowInterface) selectedItem).onPlayerShootBow(event);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Plugin.getInstance().getGame().addPlayer(event.getPlayer());
    }
}
