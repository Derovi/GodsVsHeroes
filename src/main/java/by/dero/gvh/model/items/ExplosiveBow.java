package by.dero.gvh.model.items;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.Item;
import by.dero.gvh.model.interfaces.PlayerShootBowInterface;
import by.dero.gvh.model.interfaces.ProjectileHitInterface;
import by.dero.gvh.model.itemsinfo.ExplosiveBowInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.random;

public class ExplosiveBow extends Item implements PlayerShootBowInterface, ProjectileHitInterface {
    private final Set<Entity> arrows = new HashSet<>();

    public ExplosiveBow(String name, int level, Player owner) {
        super(name, level, owner);
    }

    @Override
    public void onPlayerShootBow(EntityShootBowEvent event) {
        Player player = (Player) event.getEntity();
        Entity obj = event.getProjectile();
        arrows.add(obj);
        new BukkitRunnable() {
            double power;
            int ticks = 0;
            final Vector st = new Vector(random(), random(), random()).normalize();
            @Override
            public void run() {
                Vector dir = obj.getLocation().getDirection();
                Vector at = st.clone().crossProduct(dir).rotateAroundAxis(dir, ticks * Math.PI / 8).multiply(2);
                at.add(obj.getLocation().toVector());
                player.spawnParticle(Particle.FIREWORKS_SPARK,
                        new Location(obj.getWorld(), at.getX(), at.getY(), at.getZ()),
                        1,0,0,0,0);
                at.subtract(obj.getLocation().toVector());
                at.rotateAroundAxis(dir, Math.PI);
                at.add(obj.getLocation().toVector());
                player.spawnParticle(Particle.FIREWORKS_SPARK,
                        new Location(obj.getWorld(), at.getX(), at.getY(), at.getZ()),
                        1,0,0,0,0);
                player.spawnParticle(Particle.LAVA, obj.getLocation(), 10);
                if (!arrows.contains(obj)) {
                    obj.getWorld().createExplosion(obj.getLocation(), (float) (power*power*((ExplosiveBowInfo)getInfo()).getMultiplier()));
                    this.cancel();
                }
                power = obj.getVelocity().length();
                ticks++;
            }
        }.runTaskTimer(Plugin.getInstance(), 0, 1);
        player.setVelocity(new Vector(0, 0, 0).
                subtract(obj.getVelocity()).multiply(((ExplosiveBowInfo)getInfo()).getMultiplier()));
    }

    @Override
    public void onProjectileHit(ProjectileHitEvent event) {
        arrows.remove(event.getEntity());
    }

    @Override
    public void onProjectileHitEnemy(ProjectileHitEvent event) {
    }
}
