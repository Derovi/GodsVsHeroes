package by.dero.gvh.utils;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Minigame;
import by.dero.gvh.model.Lang;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import static by.dero.gvh.model.Drawings.drawCircle;

public class Stun {
    public static void stunEntity(LivingEntity p, int latency) {
        new PotionEffect(PotionEffectType.BLINDNESS, latency, 0).apply(p);
        p.sendMessage(Lang.get("game.stunMessage"));
        final BukkitRunnable runnable = new BukkitRunnable() {
            final Location loc = p.getLocation().clone();
            int ticks = 0;
            @Override
            public void run() {
                p.teleport(loc);
                if (ticks % 5 == 0) {
                    for (int i = 0; i <= 2; i++) {
                        drawCircle(p.getLocation().clone().add(0, i,0), 1, Particle.VILLAGER_ANGRY);
                    }
                }
                p.getWorld().spawnParticle(Particle.VILLAGER_HAPPY,
                        p.getEyeLocation().clone().add(p.getLocation().getDirection()),
                        1);
                if (++ticks >= latency ||
                        (p instanceof Player && ((Player) p).getGameMode().equals(GameMode.SPECTATOR))) {
                    this.cancel();
                }
            }
        };
        runnable.runTaskTimer(Plugin.getInstance(), 0, 1);
        Minigame.getInstance().getGame().getRunnables().add(runnable);
    }
}
