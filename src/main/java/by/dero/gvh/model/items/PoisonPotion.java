package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.itemsinfo.PoisonPotionInfo;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.entity.*;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PoisonPotion extends Item implements InfiniteReplenishInterface,
        ProjectileHitInterface, PlayerInteractInterface {
    private final double radius;
    private final int latency;

    public PoisonPotion(final String name, final int level, final Player owner) {
        super(name, level, owner);
        final PoisonPotionInfo info = (PoisonPotionInfo) getInfo();
        radius = info.getRadius();
        latency = info.getLatency();
    }

    @Override
    public void onProjectileHit(final ProjectileHitEvent event) {
        final Entity at = event.getEntity();
        for (final LivingEntity ent : GameUtils.getNearby(at.getLocation(), radius)) {
            if (GameUtils.isEnemy(ent, getTeam())) {
                new PotionEffect(PotionEffectType.POISON, latency, 1).apply(ent);
            }
        }
    }

    @Override
    public void onProjectileHitEnemy(final ProjectileHitEvent event) {

    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Projectile proj = GameUtils.spawnProjectile(event.getPlayer().getEyeLocation(),
                1, EntityType.SPLASH_POTION, event.getPlayer());
        summonedEntityIds.add(proj.getUniqueId());
    }
}
