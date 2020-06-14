package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Item;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DoubleJump extends Item implements Listener {
    public static final Set<UUID> owners = new HashSet<>();
    public DoubleJump(String name, int level, Player owner) {
        super(name, level, owner);
        Bukkit.getPluginManager().registerEvents(this, Plugin.getInstance());
        owners.add(owner.getUniqueId());
        groundUpdate(owner);
    }

    private void groundUpdate (Player player) {
        if (!owners.contains(player.getUniqueId())) {
            return;
        }
        Location location = player.getLocation();
        location = location.subtract (0, 1, 0);

        Block block = location.getBlock ();
        if (block.getType ().isSolid ()) {
            player.setAllowFlight (true);
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerDamage (EntityDamageEvent event) {
        if (event.getEntityType () == EntityType.PLAYER &&
                owners.contains(event.getEntity().getUniqueId()) &&
                event.getCause () == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled (true);
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerMove (PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if (owners.contains(p.getUniqueId()) &&
                !p.getAllowFlight()) {
            groundUpdate(p);
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerToggleFlight (PlayerToggleFlightEvent event) {
        Player p = event.getPlayer();
        if (owners.contains(p.getUniqueId()) &&
                p.getGameMode () == GameMode.SURVIVAL) {
            p.setAllowFlight (false);
            p.setVelocity (p.getLocation ().getDirection ().multiply (1.3d).setY (1.0d));
            event.setCancelled (true);
        }
    }
}
