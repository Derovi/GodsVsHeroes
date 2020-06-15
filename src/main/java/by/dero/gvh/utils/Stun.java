package by.dero.gvh.utils;

import by.dero.gvh.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class Stun {
    private static final HashMap<UUID, Long> players = new HashMap<>();

    public static void stunPlayer(LivingEntity p, int latency) {
        players.put(p.getUniqueId(), Calendar.getInstance().getTimeInMillis() + latency * 50);
        new PotionEffect(PotionEffectType.BLINDNESS, latency, 1).apply(p);
        new BukkitRunnable() {
            Location loc = p.getLocation().clone();
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
