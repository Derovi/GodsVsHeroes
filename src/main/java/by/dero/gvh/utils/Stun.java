package by.dero.gvh.utils;

import by.dero.gvh.Plugin;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Stun {
    public static void stunEntity(LivingEntity p, int latency) {
        new PotionEffect(PotionEffectType.BLINDNESS, latency, 0).apply(p);
        new BukkitRunnable() {
            final Location loc = p.getLocation().clone();
            int ticks = 0;
            @Override
            public void run() {
                p.teleport(loc);
                if (++ticks >= latency) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Plugin.getInstance(), 0, 1);
    }
}
