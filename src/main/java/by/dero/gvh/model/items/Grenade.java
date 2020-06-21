package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.interfaces.ProjectileLaunchInterface;
import by.dero.gvh.model.itemsinfo.GrenadeInfo;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import static by.dero.gvh.utils.DataUtils.*;

public class Grenade extends Item implements InfiniteReplenishInterface,
        ProjectileHitInterface, ProjectileLaunchInterface {
    private final double radius;
    private final double damage;

    public Grenade(final String name, final int level, final Player owner) {
        super(name, level, owner);
        GrenadeInfo info = (GrenadeInfo) getInfo();
        radius = info.getRadius();
        damage = info.getDamage();
    }

    @Override
    public void onProjectileHit(final ProjectileHitEvent event) {
        final Location loc = event.getEntity().getLocation();
        loc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc, 1);
        for (final LivingEntity ent : getNearby(loc, radius)) {
            if (isEnemy(ent, getTeam())) {
                damage(damage, ent, getOwner());
            }
        }
    }

    @Override
    public void onProjectileHitEnemy(final ProjectileHitEvent event) {

    }

    @Override
    public void onProjectileLaunch(final ProjectileLaunchEvent event) {
        
    }
}
