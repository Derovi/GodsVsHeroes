package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.itemsinfo.HealPotionInfo;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class HealPotion extends Item implements ProjectileHitInterface, InfiniteReplenishInterface, PlayerInteractInterface {
    private final double radius;
    private final int heal;

    public HealPotion(String name, int level, Player owner) {
        super(name, level, owner);
        HealPotionInfo info = (HealPotionInfo)getInfo();
        radius = info.getRadius();
        heal = info.getHeal();
    }

    @Override
    public void onProjectileHit(ProjectileHitEvent event) {
        Entity at = event.getEntity();
        for (Entity ent : at.getNearbyEntities(radius, radius, radius)) {
            if (ent instanceof Player && ent.getLocation().distance(at.getLocation()) <= radius) {
                double hp = Math.min(((Player) ent).getHealth() + heal,
                        ((Player) ent).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                ((Player) ent).setHealth(hp);
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
