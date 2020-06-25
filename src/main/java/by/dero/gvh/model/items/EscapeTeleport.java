package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Minigame;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.DoubleSpaceInterface;
import by.dero.gvh.model.itemsinfo.EscapeTeleportInfo;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static by.dero.gvh.model.Drawings.*;
import static by.dero.gvh.nmcapi.PlayerUtils.jumpDown;
import static by.dero.gvh.nmcapi.PlayerUtils.jumpUp;

public class EscapeTeleport extends Item implements DoubleSpaceInterface {
    private final double radius;
    public EscapeTeleport(String name, int level, Player owner) {
        super(name, level, owner);
        radius = ((EscapeTeleportInfo) getInfo()).getRadius();
    }

    private Location getNormal(final Location loc) {
        while (loc.getBlock().getType().equals(Material.AIR) &&
                loc.clone().add(0, 1,0 ).getBlock().getType().equals(Material.AIR) &&
                loc.clone().add(0, 2,0 ).getBlock().getType().equals(Material.AIR)) {
            loc.add(0, -1, 0);
        }
        while (!loc.getBlock().getType().equals(Material.AIR) ||
                !loc.clone().add(0, 1,0 ).getBlock().getType().equals(Material.AIR) ||
                !loc.clone().add(0, 2,0 ).getBlock().getType().equals(Material.AIR)) {
            loc.add(0, 1, 0);
        }
        return loc;
    }

    @Override
    public void onDoubleSpace(Player player) {
        final Location loc = getNormal(randomCylinder(player.getLocation(), radius, -6));

        drawCphere(player.getLocation().clone(), 1.5, Particle.SMOKE_LARGE);
        jumpDown(player, 10);
        player.setInvulnerable(true);
        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                drawLineColor(loc.clone(), player.getLocation().clone(), 255, 0, 0);
                player.teleport(loc.clone().add(0,-2,0));
                jumpUp(player, 10);
                drawCphere(loc.clone(), 1.5, Particle.SMOKE_LARGE);
            }
        };
        runnable.runTaskLater(Plugin.getInstance(), 10);
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setInvulnerable(false);
            }
        }.runTaskLater(Plugin.getInstance(), 20);
        Minigame.getInstance().getGame().getRunnables().add(runnable);
    }
}
