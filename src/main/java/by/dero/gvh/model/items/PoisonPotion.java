package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.itemsinfo.PoisonPotionInfo;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static by.dero.gvh.utils.DataUtils.isEnemy;

public class PoisonPotion extends Item implements PlayerInteractInterface, InfiniteReplenishInterface, ProjectileHitInterface {
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
        for (final Entity ent : at.getNearbyEntities(radius, radius, radius)) {
            if (isEnemy(ent, team) && ent.getLocation().distance(at.getLocation()) <= radius) {
                new PotionEffect(PotionEffectType.POISON, latency, 1).apply((LivingEntity) ent);
            }
        }
    }

    @Override
    public void onPlayerInteract(final PlayerInteractEvent event) {

    }

    @Override
    public void onProjectileHitEnemy(final ProjectileHitEvent event) {

    }
}
