package by.dero.gvh.model;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;

public class AreaManager implements Listener {
    private final ArrayList<Area> buffer = new ArrayList<>();

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

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!isTerritoryDamage(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    public void addArea(final Area area) {
        buffer.add(area);
    }

    public boolean isEntityDamage(final Location loc) {
        for (final Area area : buffer) {
            if (area.inside(loc) && !area.isEntityDamage()) {
                return true;
            }
        }
        return true;
    }

    public boolean isTerritoryDamage(final Location loc) {
        for (final Area area : buffer) {
            if (area.inside(loc) && area.isTerritoryDamage()) {
                return true;
            }
        }
        return false;
    }
}
