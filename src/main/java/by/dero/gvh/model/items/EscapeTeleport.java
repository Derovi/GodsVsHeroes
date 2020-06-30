package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.minigame.Minigame;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.DoubleSpaceInterface;
import by.dero.gvh.model.itemsinfo.EscapeTeleportInfo;
import by.dero.gvh.utils.DirectedPosition;
import by.dero.gvh.utils.MathUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_12_R1.CraftSound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static by.dero.gvh.model.Drawings.*;
import static by.dero.gvh.nmcapi.PlayerUtils.jumpDown;
import static by.dero.gvh.nmcapi.PlayerUtils.jumpUp;

public class EscapeTeleport extends Item implements DoubleSpaceInterface {
    private final double radius;
    private final double minradius;
    public EscapeTeleport(String name, int level, Player owner) {
        super(name, level, owner);
        final EscapeTeleportInfo info = (EscapeTeleportInfo) getInfo();
        radius = info.getRadius();
        minradius = info.getMinRadius();
    }

    private Location getNormal(Location loc) {
        final Location startLoc = loc.clone();
        final DirectedPosition[] poses = Game.getInstance().getInfo().getMapBorders();
        do {
            loc = MathUtils.randomCylinder(loc.clone(), radius, 0);
        } while (poses[0].getX() > loc.getX() || poses[0].getZ() > loc.getZ() ||
                poses[1].getX() < loc.getX() || poses[1].getZ() < loc.getZ() ||
                startLoc.distance(loc) < minradius);

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
    public void onDoubleSpace() {
        final Location loc = getNormal(owner.getLocation());
        if (!cooldown.isReady()) {
            return;
        }
        cooldown.reload();
        drawCphere(owner.getLocation().clone(), 1.5, Particle.SMOKE_LARGE);
        jumpDown(owner, 10);
        owner.setInvulnerable(true);
        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                drawLineColor(loc.clone(), owner.getLocation().clone(), 255, 0, 0);
                owner.teleport(loc.clone().add(0,-2,0));
                jumpUp(owner, 10);
                drawCphere(loc.clone(), 1.5, Particle.SMOKE_LARGE);
            }
        };
        runnable.runTaskLater(Plugin.getInstance(), 10);
        new BukkitRunnable() {
            @Override
            public void run() {
                owner.setInvulnerable(false);
            }
        }.runTaskLater(Plugin.getInstance(), 20);
        Minigame.getInstance().getGame().getRunnables().add(runnable);
    }
}
