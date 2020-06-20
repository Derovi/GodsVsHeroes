package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.InfiniteReplenishInterface;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.interfaces.ProjectileLaunchInterface;
import by.dero.gvh.model.itemsinfo.DamagePotionInfo;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import static by.dero.gvh.utils.DataUtils.getNearby;
import static by.dero.gvh.utils.DataUtils.isEnemy;

public class DamagePotion extends Item implements ProjectileHitInterface, InfiniteReplenishInterface, PlayerInteractInterface, ProjectileLaunchInterface {
    private final double radius;
    private final double damage;
    public DamagePotion(final String name, final int level, final Player owner) {
        super(name, level, owner);
        final DamagePotionInfo info = (DamagePotionInfo) getInfo();
        radius = info.getRadius();
        damage = info.getDamage();
    }

    @Override
    public void onProjectileHit(final ProjectileHitEvent event) {
        final Entity at = event.getEntity();
        for (final LivingEntity ent : getNearby(at.getLocation(), radius)) {
            if (isEnemy(ent, getTeam())) {
                ent.damage(damage, getOwner());
            }
        }
    }

    @Override
    public void onProjectileHitEnemy(ProjectileHitEvent event) {

    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {

    }

    @Override
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
    }
}

