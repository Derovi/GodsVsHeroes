package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerShootBowInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.itemsinfo.ExplosiveBowInfo;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.MathUtils;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ExplosiveBow extends Item implements PlayerShootBowInterface, ProjectileHitInterface {
    private final double reclining;
    private final double multiplier;

    private final Set<UUID> arrows = new HashSet<>();

    public ExplosiveBow(final String name, final int level, final Player owner) {
        super(name, level, owner);
        final ExplosiveBowInfo info = (ExplosiveBowInfo) getInfo();
        reclining = info.getReclining();
        multiplier = info.getMultiplier();
    }

    @Override
    public void onPlayerShootBow(EntityShootBowEvent event) {
        final Player player = (Player) event.getEntity();
        if (!cooldown.isReady()) {
            event.setCancelled(true);
            return;
        }
        cooldown.reload();
        final Entity obj = event.getProjectile();
        arrows.add(obj.getUniqueId());
        new BukkitRunnable() {
            double power;
            int ticks = 0;
            final Vector st = new Vector(Math.random(), Math.random(), Math.random()).normalize();
            @Override
            public void run() {
                Vector dir = obj.getLocation().getDirection();
                Vector at = MathUtils.rotateAroundAxis(st.clone().crossProduct(dir), dir, ticks * Math.PI / 8).multiply(2);
                at.add(obj.getLocation().toVector());
                player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK,
                        new Location(obj.getWorld(), at.getX(), at.getY(), at.getZ()),
                        1,0,0,0,0);
                at.subtract(obj.getLocation().toVector());
                MathUtils.rotateAroundAxis(at, dir, Math.PI);
                at.add(obj.getLocation().toVector());
                player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK,
                        new Location(obj.getWorld(), at.getX(), at.getY(), at.getZ()),
                        1,0,0,0,0);
                player.getWorld().spawnParticle(Particle.LAVA, obj.getLocation(), 3);
                if (!arrows.contains(obj.getUniqueId()) || ticks > 300) {
                    final float force = (float)(power*power*multiplier);
                    Location loc = obj.getLocation();
                    loc.getWorld().playEffect(loc, Effect.END_GATEWAY_SPAWN, null);
                    for (LivingEntity ent : GameUtils.getNearby(loc, 5)) {
                        GameUtils.damage(force, ent, owner);
                    }
                    this.cancel();
                    return;
                }
                power = obj.getVelocity().length();
                ticks++;
            }
        }.runTaskTimer(Plugin.getInstance(), 0, 1);
        player.setVelocity(new Vector(0, 0, 0).
                subtract(obj.getVelocity()).multiply(reclining));
    }

    @Override
    public void onProjectileHit(ProjectileHitEvent event) {
        arrows.remove(event.getEntity().getUniqueId());
    }

    @Override
    public void onProjectileHitEnemy(ProjectileHitEvent event) {
    }
}
