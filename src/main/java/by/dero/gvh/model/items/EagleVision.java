package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.UltimateInterface;
import by.dero.gvh.model.itemsinfo.EagleVisionInfo;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class EagleVision extends Item implements UltimateInterface {
    final double particleDense = 32;

    double radius;
    Long glowTime;
    Particle searchParticle;

    public EagleVision(String name, int level, Player owner) {
        super(name, level, owner);

        EagleVisionInfo info = (EagleVisionInfo)getInfo();
        radius = info.getRadius();
        glowTime = info.getGlowTime();
        searchParticle = info.getSearchParticle();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation().clone();
        for (Entity obj : Objects.requireNonNull(loc.getWorld()).getNearbyEntities(loc, radius, 200, radius)) {
            Location cur = obj.getLocation().clone();
            double dst = loc.distance(new Location(cur.getWorld(), cur.getX(), loc.getY(), cur.getZ()));
            if (!(obj instanceof LivingEntity) || dst > radius) {
                continue;
            }
            new PotionEffect(PotionEffectType.GLOWING, Math.toIntExact(glowTime), 1).apply(event.getPlayer());
        }
        drawSign(player.getLocation().clone());
    }

    @Override
    public void drawSign(Location loc) {
        new BukkitRunnable(){
            double r = 1, addAngle = 0;
            @Override
            public void run() {
                for (double angle = addAngle; angle < Math.PI * 2 + addAngle; angle += Math.PI * 2 / particleDense) {
                    Objects.requireNonNull(loc.getWorld()).spawnParticle(searchParticle, new Location(
                            loc.getWorld(),
                            loc.getX() + r * Math.cos(angle),
                            loc.getY(),
                            loc.getZ() + r * Math.sin(angle)
                    ), 1,0,0,0,0);
                }

                r += 0.5;
                addAngle += Math.PI / particleDense / 4;
                if (r > radius) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Plugin.getInstance(), 0, 1);
    }
}
