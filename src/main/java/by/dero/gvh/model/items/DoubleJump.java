package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Item;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import static by.dero.gvh.utils.DataUtils.getPlayer;

import static by.dero.gvh.utils.DataUtils.getPlayer;

public class DoubleJump extends Item implements Listener {
    public DoubleJump(final String name, final int level, final Player owner) {
        super(name, level, owner);
        Bukkit.getPluginManager().registerEvents(this, Plugin.getInstance());
        groundUpdate(owner);
    }

    private void groundUpdate (final Player player) {
        if (!getPlayer(player.getName()).getItems().containsKey(getName())) {
            return;
        }

        final Block block = player.getLocation().clone().subtract(0,1,0).getBlock();;
        if (block.getType ().isSolid ()) {
            player.setAllowFlight (true);
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerDamage (final EntityDamageEvent event) {
        if (event.getEntityType () == EntityType.PLAYER &&
                getPlayer(event.getEntity().getName()).getItems().containsKey(getName()) &&
                event.getCause () == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled (true);
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerMove (final PlayerMoveEvent event) {
        final Player p = event.getPlayer();
        if (getPlayer(p.getName()).getItems().containsKey(getName()) &&
                !p.getAllowFlight()) {
            groundUpdate(p);
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerToggleFlight (final PlayerToggleFlightEvent event) {
        final Player p = event.getPlayer();
        if (getPlayer(p.getName()).getItems().containsKey(getName()) &&
                p.getGameMode () == GameMode.SURVIVAL) {
            p.setAllowFlight (false);
            p.setVelocity (p.getLocation().getDirection().multiply (1.1d).setY (1.0d));
            event.setCancelled (true);
        }
    }
}
