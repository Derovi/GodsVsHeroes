package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.interfaces.ProjectileLaunchInterface;
import by.dero.gvh.model.itemsinfo.StunRocksInfo;
import by.dero.gvh.utils.Stun;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import static by.dero.gvh.utils.DataUtils.*;

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
        final Player player = event.getPlayer();
        final Vector dir = player.getLocation().getDirection().clone();

        final Location loc = player.getEyeLocation().clone().add(dir.clone().multiply(2));
        Snowball snowball = (Snowball) loc.getWorld().spawnEntity(loc,
                EntityType.SNOWBALL);
        snowball.setVelocity(dir.multiply(1.2));
        summonedEntityIds.add(snowball.getUniqueId());
    }
}

