package by.dero.gvh.model;

import by.dero.gvh.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;

public class AreaManager implements Listener {
    private final ArrayList<Area> buffer = new ArrayList<>();

    public AreaManager() {
        Bukkit.getPluginManager().registerEvents(this, Plugin.getInstance());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!isEntityDamage(event.getEntity().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!isTerritoryDamage(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    public void addArea(final Area area) {
        buffer.add(area);
    }

    public boolean isEntityDamage(final Location loc) {
        for (final Area area : buffer) {
            if (area.inside(loc) && area.isEntityDamage()) {
                return true;
            }
        }
        return false;
    }

    public boolean isTerritoryDamage(final Location loc) {
        for (final Area area : buffer) {
            Bukkit.getServer().broadcastMessage(area.inside(loc) + " " + area.isTerritoryDamage());
            if (area.inside(loc) && area.isTerritoryDamage()) {
                return true;
            }
        }
        return false;
    }
}
