package by.dero.gvh.model;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Minigame;
import by.dero.gvh.utils.MathUtils;
import net.minecraft.server.v1_12_R1.EntityFireworks;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftFirework;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.UUID;

public class Drawings {
    public static final double dense = 3;
    public static final Vector randomVector = new Vector(Math.random(), Math.random(), Math.random()).normalize();

    public static void drawLine(Location a, Location b, Particle obj) {
        Vector cur = a.toVector();
        Vector to = b.toVector();
        while (true) {
            a.getWorld().spawnParticle(obj,
                    new Location(a.getWorld(), cur.getX(), cur.getY(), cur.getZ()),
                    1, 0,0,0,0);

            if (cur.equals(to)) {
                break;
            }
            if (cur.distance(to) < 1 / dense) {
                cur = to;
                continue;
            }
            cur.add(to.clone().subtract(cur).normalize().multiply(1/dense));
        }
    }


    public static void drawLineColor(final Location a, final Location b,
                                     final int red, final int green, final int blue) {
        Vector cur = a.toVector();
        final Vector to = b.toVector();
        while (true) {
            a.getWorld().spawnParticle(Particle.REDSTONE,
                    new Location(a.getWorld(), cur.getX(), cur.getY(), cur.getZ()),
                    0, red, green, blue);

            if (cur.equals(to)) {
                break;
            }
            if (cur.distance(to) < 1 / dense) {
                cur = to;
                continue;
            }
            cur.add(to.clone().subtract(cur).normalize().multiply(1/dense));
        }
    }

    public static void drawCircle(Location loc, double radius, Particle obj) {
        long steps = Math.round(MathUtils.PI2 * radius * dense);
        for (double angle = 0; angle < MathUtils.PI2; angle += MathUtils.PI2 / steps) {
            loc.getWorld().spawnParticle(
                    obj,
                    new Location(
                            loc.getWorld(),
                            loc.getX() + MathUtils.cos(angle) * radius,
                            loc.getY(),
                            loc.getZ() + MathUtils.sin(angle) * radius
                    ), 1, 0, 0, 0, 0);
        }
    }

    private static final Color[] colors = new Color[] {
        Color.AQUA, Color.BLUE, Color.FUCHSIA, Color.LIME, Color.MAROON, Color.NAVY,
        Color.OLIVE, Color.ORANGE, Color.WHITE, Color.YELLOW, Color.SILVER, Color.RED, Color.PURPLE
    };

    public static void spawnFirework(final Location loc, final int amount) {
        for (int i = 0; i < amount; i++) {
            CraftWorld world = (CraftWorld) loc.getWorld();
            EntityFireworks fw = new EntityFireworks(world.world);
            fw.setPosition(loc.getX(), loc.getY(), loc.getZ());
            fw.expectedLifespan = 2;
            fw.noclip = true;

            CraftItemStack item = ((CraftFirework) fw.getBukkitEntity()).item;
            FireworkMeta meta = (FireworkMeta) item.getItemMeta();
            meta.setPower(2);
            meta.addEffect(FireworkEffect.builder().withColor(
                    colors[(int)(Math.random()*colors.length)]).flicker(true).build());
            item.setItemMeta(meta);

            world.addEntity(fw, CreatureSpawnEvent.SpawnReason.CUSTOM);
        }
    }

    public static void spawnMovingSphere(final Location center,
                                          final int duration,
                                          final double radius,
                                          final double horAngleSpeed,
                                          final double vertStartAngle,
                                          final double vertEndAngle,
                                          final int dT,
                                          final Particle particle,
                                          final Player player) {
        final int parts = 16;
        final double vertAngleSpeed = (vertEndAngle - vertStartAngle) / duration;
        new BukkitRunnable() {
            double horAngle = 0;
            double vertAngle = vertStartAngle;
            int timePassed = 0;
            @Override
            public void run() {
                for (double partAngle = 0; partAngle < MathUtils.PI2; partAngle += MathUtils.PI2 / parts) {
                    double resHor = horAngle + partAngle;
                    final Location at = MathUtils.getInCphere(center.toVector(), radius, resHor, vertAngle).toLocation(center.getWorld());
                    player.getWorld().spawnParticle(particle, at, 0,0,0, 0);
                }

                horAngle += horAngleSpeed * dT;
                vertAngle += vertAngleSpeed * dT;
                timePassed += dT;
                if (timePassed >= duration) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Plugin.getInstance(), 0, dT);
    }

    public static void spawnMovingCircle(final Location loc,
                                         final int duration,
                                         final double radius,
                                         final double dense,
                                         final double speed,
                                         final Particle particle,
                                         final World world) {
        final int dT = 2;
        final int parts = (int) Math.round(MathUtils.PI2 * radius * dense);
        new BukkitRunnable() {
            double horAngle = 0;
            int timePassed = 0;
            @Override
            public void run() {
                for (double partAngle = 0; partAngle < MathUtils.PI2; partAngle += MathUtils.PI2 / parts) {
                    final double angle = horAngle + partAngle;
                    final Location at = MathUtils.getInCircle(loc, radius, angle);
                    world.spawnParticle(particle, at, 0,0,0,0);
                }

                horAngle += speed * dT;
                timePassed += dT;
                if (timePassed >= duration) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Plugin.getInstance(), 0, dT);
    }

    public static void spawnMovingCircle(final Location loc,
                                         final int duration,
                                         final double radius,
                                         final double dense,
                                         final double speed,
                                         final Particle particle,
                                         final Player player) {
        final int dT = 2;
        final int parts = (int) Math.round(MathUtils.PI2 * radius * dense);
        new BukkitRunnable() {
            double horAngle = 0;
            int timePassed = 0;
            @Override
            public void run() {
                for (double partAngle = 0; partAngle < MathUtils.PI2; partAngle += MathUtils.PI2 / parts) {
                    final double angle = horAngle + partAngle;
                    final Location at = MathUtils.getInCircle(loc, radius, angle);
                    player.spawnParticle(particle, at, 0,0,0,0);
                }

                horAngle += speed * dT;
                timePassed += dT;
                if (timePassed >= duration) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Plugin.getInstance(), 0, dT);
    }

    public static void spawnUnlockParticles(final Location loc,
                                     final Player player,
                                     final int duration,
                                     final double radius,
                                     final double startAngle,
                                     final double endAngle) {

        player.playSound(loc, Sound.BLOCK_END_PORTAL_SPAWN, 2f, 1);
        for (int i = 0; i <= duration; i += 15) {
            Bukkit.getServer().getScheduler().runTaskLater(Plugin.getInstance(), () ->
                    player.playSound(loc, Sound.BLOCK_FIRE_AMBIENT, 2, 1), i);
        }

        spawnMovingSphere(loc.clone().add(0,1,0),
                duration / 2, radius, Math.PI / 80,
                startAngle, endAngle, 1, Particle.FLAME, player);

        spawnMovingCircle(loc.clone().add(0, 0.15,0),
                duration, MathUtils.cos(endAngle) * radius, 3,Math.PI / 160, Particle.FLAME, player);

        spawnMovingCircle(loc.clone().add(0, 1,0),
                duration, MathUtils.cos(endAngle) * radius, 3, Math.PI / 160, Particle.FLAME, player);

        spawnMovingCircle(loc.clone().add(0, 1.85,0),
                duration, MathUtils.cos(endAngle) * radius, 3, Math.PI / 160, Particle.FLAME, player);

        Bukkit.getServer().getScheduler().runTaskLater(Plugin.getInstance(), () ->
                spawnMovingSphere(loc.clone().add(0,1,0),
                        duration / 2, radius, Math.PI / 80,
                        endAngle, startAngle, 1, Particle.FLAME, player), duration / 2);

        Bukkit.getServer().getScheduler().runTaskLater(Plugin.getInstance(), () -> {
                    player.playSound(loc, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 2, 1);
                    spawnFirework(loc.clone().add(0,1,0), 2);
                }, duration);
    }

    public static void drawCircleInFront(final Location loc, final double radius,
                                         final double dst, final int parts, final Particle par) {
        final Vector dir = loc.getDirection();
        final Location center = loc.clone().add(dir.clone().multiply(dst));

        for (int i = 0; i < parts; i++) {
            final double angle = MathUtils.PI2 * i / parts;
            final Vector at = MathUtils.rotateAroundAxis(randomVector.clone().crossProduct(dir).normalize(), dir, angle).multiply(radius);
            at.add(center.toVector());
            loc.getWorld().spawnParticle(par,
                    new Location(center.getWorld(), at.getX(), at.getY(), at.getZ()),
                    0,0,0,0);
        }
    }

    public static void drawCircleInFrontColor(final Location loc, final double radius,
                                         final double dst, final int parts,
                                         final int red, final int green, final int blue) {
        final Vector dir = loc.getDirection();
        final Location center = loc.clone().add(dir.clone().multiply(dst));

        for (int i = 0; i < parts; i++) {
            final double angle = MathUtils.PI2 * i / parts;
            final Vector at = MathUtils.rotateAroundAxis(randomVector.clone().crossProduct(dir).normalize(), dir, angle).multiply(radius);
            at.add(center.toVector());
            loc.getWorld().spawnParticle(Particle.REDSTONE,
                    new Location(center.getWorld(), at.getX(), at.getY(), at.getZ()),
                    0, red, green, blue, 0);
        }
    }

    public static void drawCphere(final Location loc, final double radius,
                                  final Particle particle) {
        final int parts = 8;
        final double vertAngleSpeed = Math.PI / parts;
        double vertAngle = -Math.PI / 2;
        for (int i = 0; i < parts; i++) {
            for (double partAngle = 0; partAngle < MathUtils.PI2; partAngle += MathUtils.PI2 / parts) {
                final Location at = MathUtils.getInCphere(loc.toVector(), radius, partAngle, vertAngle).toLocation(loc.getWorld());
                loc.getWorld().spawnParticle(particle, at, 0,0,0, 0);
            }

            vertAngle += vertAngleSpeed;
        }
    }

    public static Location[] drawSector(final Location loc,
                                  final double innerRadius, final double outerRadius,
                                  final double sumAngle,
                                  final Particle particle) {

        final int parts = 8;
        final double angleSpeed = sumAngle / (parts - 1);
        final double radSpeed = (outerRadius - innerRadius) / (parts - 1);
        final Vector upVector = MathUtils.getUpVector(loc.getDirection());
        final Location[] ret = new Location[parts*parts];
        int idx = 0;
        for (double angle = -sumAngle/2; angle <= sumAngle/2; angle += angleSpeed) {
            final Vector atVector = MathUtils.rotateAroundAxis(loc.getDirection().clone(), upVector, angle);
            for (double radius = innerRadius; radius <= outerRadius; radius += radSpeed) {
                final Location pos = loc.clone().add(atVector.clone().multiply(radius));
                loc.getWorld().spawnParticle(particle, pos, 0, 0,0,0);
                ret[idx] = pos;
                idx++;
            }
        }
        return ret;
    }

    public static void addTrail(final Projectile proj) {
        final HashSet<UUID> projectiles = Minigame.getInstance().getGameEvents().getProjectiles();
        projectiles.add(proj.getUniqueId());
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!projectiles.contains(proj.getUniqueId())) {
                    this.cancel();
                }
                final Location loc = proj.getLocation();
                loc.getWorld().spawnParticle(Particle.FLAME, loc.getX(), loc.getY(), loc.getZ(),
                        0, 0, 0, 0);
            }
        }.runTaskTimer(Plugin.getInstance(), 0, 1);
    }
}
