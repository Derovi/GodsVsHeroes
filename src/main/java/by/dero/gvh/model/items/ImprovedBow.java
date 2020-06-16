package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.itemsinfo.ImprovedBowInfo;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.Objects;

public class ImprovedBow extends Item implements ProjectileHitInterface {
    private final int damage;
    public ImprovedBow(final String name, final int level, final Player owner) {
        super(name, level, owner);
        damage = ((ImprovedBowInfo) getInfo()).getDamage();
    }

    @Override
    public void onProjectileHit(final ProjectileHitEvent event) { }

    @Override
    public void onProjectileHitEnemy(final ProjectileHitEvent event) {
        ((LivingEntity) Objects.requireNonNull(event.getHitEntity())).damage(damage, getOwner());
    }
}
