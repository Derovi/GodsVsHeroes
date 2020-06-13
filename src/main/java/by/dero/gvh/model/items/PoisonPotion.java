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

public class PoisonPotion extends Item implements PlayerInteractInterface, InfiniteReplenishInterface, ProjectileHitInterface {
    private final double radius;
    private final int latency;

    public PoisonPotion(String name, int level, Player owner) {
        super(name, level, owner);
        PoisonPotionInfo info = (PoisonPotionInfo) getInfo();
        radius = info.getRadius();
        latency = info.getLatency();
    }

    @Override
    public void onProjectileHit(ProjectileHitEvent event) {
        Entity at = event.getEntity();
        for (Entity ent : at.getNearbyEntities(radius, radius, radius)) {
            if (ent instanceof LivingEntity && ent.getLocation().distance(at.getLocation()) <= radius) {
                new PotionEffect(PotionEffectType.POISON, latency, 1).apply((LivingEntity) ent);
            }
        }
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {

    }

    @Override
    public void onProjectileHitEnemy(ProjectileHitEvent event) {

    }
}
