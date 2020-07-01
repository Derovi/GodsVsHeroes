package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.itemsinfo.StunRocksInfo;
import by.dero.gvh.utils.Stun;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import static by.dero.gvh.utils.GameUtils.*;

public class StunRocks extends Item implements InfiniteReplenishInterface,
        ProjectileHitInterface, PlayerInteractInterface {
    private final int duration;
    public StunRocks(final String name, final int level, final Player owner) {
        super(name, level, owner);
        duration = ((StunRocksInfo) getInfo()).getDuration();
    }

    @Override
    public void onProjectileHitEnemy(ProjectileHitEvent event) {
        if (isEnemy(event.getHitEntity(), getTeam())) {
            Stun.stunEntity((LivingEntity) event.getHitEntity(), duration);
        }
    }

    @Override
    public void onProjectileHit(ProjectileHitEvent event) {
        final Location loc = event.getEntity().getLocation();
        loc.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, loc, 1);
        for (final LivingEntity entity : getNearby(event.getEntity().getLocation(), 1.5)) {
            if (isEnemy(entity, getTeam())) {
                Stun.stunEntity(entity, duration);
            }
        }
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Projectile proj = spawnProjectile(event.getPlayer().getEyeLocation(),
                1.2, EntityType.SNOWBALL, event.getPlayer());
        summonedEntityIds.add(proj.getUniqueId());
    }
}

