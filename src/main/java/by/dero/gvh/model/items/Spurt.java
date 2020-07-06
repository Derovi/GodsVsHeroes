package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.DoubleSpaceInterface;
import by.dero.gvh.model.itemsinfo.SpurtInfo;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Spurt extends Item implements DoubleSpaceInterface {
    private final int duration;
    private final int power;

    public Spurt(final String name, final int level, final Player owner) {
        super(name, level, owner);
        SpurtInfo info = (SpurtInfo) getInfo();
        duration = info.getSpeedTime();
        power = info.getPower();
    }

    @Override
    public void onDoubleSpace() {
        owner.getLocation().getWorld().playSound(owner.getEyeLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.7f, 1);
        Drawings.drawCircleInFront(owner.getEyeLocation(), 3, 0.5, 5, Particle.EXPLOSION_LARGE);
        owner.setVelocity(owner.getLocation().getDirection().normalize().multiply(power).setY (0.4));
        float playerSpeed = owner.getWalkSpeed();
        owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 1), true);
        new BukkitRunnable() {
            @Override
            public void run() {
                owner.setWalkSpeed(playerSpeed);
            }
        }.runTaskLater(Plugin.getInstance(), duration);
    }
}
