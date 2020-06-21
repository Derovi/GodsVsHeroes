package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.interfaces.ProjectileLaunchInterface;
import by.dero.gvh.model.itemsinfo.MagnetizeOrbInfo;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.util.Vector;

import static by.dero.gvh.utils.DataUtils.isEnemy;

public class MagnetizeOrb extends Item implements ProjectileHitInterface,
        InfiniteReplenishInterface, ProjectileLaunchInterface {
    private final double radius;
    public MagnetizeOrb(final String name, final int level, final Player owner) {
        super(name, level, owner);
        radius = ((MagnetizeOrbInfo) getInfo()).getRadius();
    }

    @Override
    public void onProjectileHit(final ProjectileHitEvent event) {
        final Entity proj = event.getEntity();
        final Location loc = proj.getLocation();
        for (final Entity obj : proj.getNearbyEntities(radius, radius, radius)) {
            if (isEnemy(obj, getTeam()) && loc.distance(obj.getLocation()) <= radius) {
                final Vector add = loc.toVector().subtract(obj.getLocation().toVector());
                final double force = Math.log(add.length()) / Math.log(2);
                obj.setVelocity(obj.getVelocity().add(add.normalize().multiply(force)));
            }
        }
    }

    @Override
    public void onProjectileLaunch(final ProjectileLaunchEvent event) {

    }

    @Override
    public void onProjectileHitEnemy(final ProjectileHitEvent event) {

    }
}