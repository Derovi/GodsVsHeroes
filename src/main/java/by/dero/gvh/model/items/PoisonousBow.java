package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerShootBowInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.itemsinfo.PoisonPotionInfo;
import by.dero.gvh.model.itemsinfo.PoisonousBowInfo;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PoisonousBow extends Item implements ProjectileHitInterface {
    private final int duration;

    public PoisonousBow(String name, int level, Player owner) {
        super(name, level, owner);
        duration = ((PoisonousBowInfo) getInfo()).getDuration();
    }

    @Override
    public void onProjectileHitEnemy(ProjectileHitEvent event) {
        ((LivingEntity) event.getHitEntity()).addPotionEffect(
                new PotionEffect(PotionEffectType.POISON, duration, 1), true);
    }

    @Override
    public void onProjectileHit(ProjectileHitEvent event) {

    }
}
