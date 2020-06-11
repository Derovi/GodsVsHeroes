package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.itemsinfo.HealPotionInfo;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;

public class HealPotion extends Item implements ProjectileHitInterface, InfiniteReplenishInterface {
    public HealPotion(String name, int level, Player owner) {
        super(name, level, owner);
    }

    @Override
    public void onProjectileHit(ProjectileHitEvent event) {
        Entity at = event.getEntity();
        double radius = ((HealPotionInfo)getInfo()).getRadius();
        int heal = ((HealPotionInfo)getInfo()).getHeal();
        for (Entity ent : at.getNearbyEntities(radius, radius, radius)) {
            if (ent instanceof LivingEntity && ent.getLocation().distance(at.getLocation()) <= radius) {
                ((LivingEntity) ent).setHealth(((LivingEntity) ent).getHealth() + heal);
            }
        }
    }

    @Override
    public void onProjectileHitEnemy(ProjectileHitEvent event) {

    }
}
