package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Drawings;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerInteractInterface;
import by.dero.gvh.model.itemsinfo.ChainLightningInfo;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

import java.util.HashSet;
import java.util.Objects;
import java.util.function.Predicate;

public class ChainLightning extends Item implements PlayerInteractInterface {
    private final double radius;
    private final double damage;

    public ChainLightning(String name, int level, Player owner) {
        super(name, level, owner);
        ChainLightningInfo info = (ChainLightningInfo)getInfo();
        damage = info.getDamage();
        radius = info.getRadius();
    }
    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Predicate<Entity> pred = (p) -> {
            if (!(p instanceof LivingEntity)) return false;
            if (!(p instanceof Player)) return true;
            return p != player;
        };
        RayTraceResult ray = player.getWorld().rayTraceEntities(
                player.getEyeLocation(),
                player.getLocation().getDirection(),
                100, pred
        );
        if (ray == null || ray.getHitEntity() == null || !(ray.getHitEntity() instanceof LivingEntity)) {
            return;
        }
        new BukkitRunnable() {
            final HashSet<LivingEntity> hit = new HashSet<>();
            LivingEntity cur = player;
            LivingEntity next = (LivingEntity) ray.getHitEntity();
            @Override
            public void run() {
                Drawings.drawLine(cur.getEyeLocation(), next.getEyeLocation(), Particle.FIREWORKS_SPARK);
                Objects.requireNonNull(next.getEyeLocation().getWorld()).spawnParticle(Particle.EXPLOSION_LARGE, next.getEyeLocation(), 1);
                hit.add(next);
                next.damage(damage);
                cur = next;
                next = null;
                for (Entity obj : cur.getNearbyEntities(radius, radius, radius)) {
                    if (pred.test(obj) && obj.getLocation().distance(cur.getLocation()) <= radius &&
                            !hit.contains(obj)) {
                        next = (LivingEntity) obj;
                        break;
                    }
                }
                if (next == null) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Plugin.getInstance(),0, 5);
    }
}
