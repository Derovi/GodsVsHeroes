package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.itemsinfo.StunRocksInfo;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.Stun;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class StunRocks extends Item implements InfiniteReplenishInterface,
        ProjectileHitInterface, PlayerInteractInterface {
    private final int duration;
    public StunRocks(final String name, final int level, final Player owner) {
        super(name, level, owner);
        duration = ((StunRocksInfo) getInfo()).getDuration();
    }

    @Override
    public void onProjectileHitEnemy(ProjectileHitEvent event) {
        if (GameUtils.isEnemy(event.getHitEntity(), getTeam())) {
            Stun.stunEntity((LivingEntity) event.getHitEntity(), duration);
        }
    }

    @Override
    public void onProjectileHit(ProjectileHitEvent event) {
        final Location loc = event.getEntity().getLocation();
        loc.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, loc, 1);

        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.07f, 1);
        for (final LivingEntity entity : GameUtils.getNearby(event.getEntity().getLocation(), 1.5)) {
            if (GameUtils.isEnemy(entity, getTeam())) {
                Stun.stunEntity(entity, duration);
            }
        }
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Projectile proj = GameUtils.spawnProjectile(event.getPlayer().getEyeLocation(),
                1.2, EntityType.SNOWBALL, event.getPlayer());
        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_EGG_THROW, 1.07f, 1);
        summonedEntityIds.add(proj.getUniqueId());
    }
}

