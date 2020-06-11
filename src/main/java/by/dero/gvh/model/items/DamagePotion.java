package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.itemsinfo.DamagePotionInfo;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class DamagePotion extends Item implements ProjectileHitInterface, InfiniteReplenishInterface, PlayerInteractInterface {
    private final double radius;
    private final double damage;
    public DamagePotion(String name, int level, Player owner) {
        super(name, level, owner);
        DamagePotionInfo info = (DamagePotionInfo)getInfo();
        radius = info.getRadius();
        damage = info.getDamage();
    }

    @Override
    public void onProjectileHit(ProjectileHitEvent event) {
        Entity at = event.getEntity();
        for (Entity ent : at.getNearbyEntities(radius, radius, radius)) {
            if (ent instanceof LivingEntity && ent.getLocation().distance(at.getLocation()) <= radius) {
                ((LivingEntity) ent).damage(damage);
            }
        }
    }

    @Override
    public void onProjectileHitEnemy(ProjectileHitEvent event) {

    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {

    }
}

