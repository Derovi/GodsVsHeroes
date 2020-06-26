package by.dero.gvh.model;

import by.dero.gvh.Plugin;
import net.minecraft.server.v1_12_R1.EnumParticle;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import static java.lang.Math.random;

public class Drawings {
    private static final double dense = 4;
    private static final Vector randomVector = new Vector(random(), random(), random()).normalize();

    public static Vector rotateAroundAxis(final Vector point, final Vector axis, double angle) throws IllegalArgumentException {
        return rotateAroundNonUnitAxis(point, axis.length() == 1 ? axis : axis.clone().normalize(), angle);
    }

    public static Vector rotateAroundNonUnitAxis(final Vector point, final Vector axis, final double angle) {
        double x = point.getX(), y = point.getY(), z = point.getZ();
        double x2 = axis.getX(), y2 = axis.getY(), z2 = axis.getZ();

        double cosTheta = Math.cos(angle);
        double sinTheta = Math.sin(angle);
        double dotProduct = point.dot(axis);

        double xPrime = x2 * dotProduct * (1d - cosTheta)
                + x * cosTheta
                + (-z2 * y + y2 * z) * sinTheta;
        double yPrime = y2 * dotProduct * (1d - cosTheta)
                + y * cosTheta
                + (z2 * x - x2 * z) * sinTheta;
        double zPrime = z2 * dotProduct * (1d - cosTheta)
                + z * cosTheta
                + (-y2 * x + x2 * y) * sinTheta;

        return point.setX(xPrime).setY(yPrime).setZ(zPrime);
    }


    public static Location randomCylinder(final Location center, final double radius, final double depth) {
        final double dst = Math.random() * radius;
        final double angle = Math.random() * Math.PI * 2;
        return center.clone().add(
                dst*Math.cos(angle),
                -Math.random() * depth,
                dst*Math.sin(angle)
        );
    }

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
                    0, red, green, blue, 2);

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
        long steps = Math.round(Math.PI * 2 * radius * dense);
        for (double angle = 0; angle < Math.PI * 2; angle += Math.PI * 2 / steps) {
            loc.getWorld().spawnParticle(
                    obj,
                    new Location(
                            loc.getWorld(),
                            loc.getX() + Math.cos(angle) * radius,
                            loc.getY(),
                            loc.getZ() + Math.sin(angle) * radius
                    ), 1, 0, 0, 0, 0);
        }
    }

    public static Location getInCircle(Location loc, double radius, double angle) {
        return new Location(
                loc.getWorld(),
                loc.getX() + Math.cos(angle) * radius,
                loc.getY(),
                loc.getZ() + Math.sin(angle) * radius
        );
    }

    public static void spawnFirework(final Location loc, final int amount) {
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.LIME).flicker(true).build());
        fw.setFireworkMeta(fwm);
        fw.detonate();

        for(int i = 0;i < amount; i++){
            Firework fw2 = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
            fw2.setFireworkMeta(fwm);
            fw2.detonate();
        }
    }

    public static Vector getInCphere(final Vector center,
                                        final double radius,
                                        final double horAngle,
                                        final double vertAngle) {

        Vector locCenter = new Vector(center.getX(), center.getY() + Math.sin(vertAngle) * radius, center.getZ());
        final double locRadius = Math.cos(vertAngle) * radius;
        return locCenter.add(new Vector(
                Math.cos(horAngle) * locRadius,
                0,
                Math.sin(horAngle) * locRadius)
        );
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
                for (double partAngle = 0; partAngle < Math.PI * 2; partAngle += Math.PI * 2 / parts) {
                    double resHor = horAngle + partAngle;
                    final Location at = getInCphere(center.toVector(), radius, resHor, vertAngle).toLocation(center.getWorld());
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
        final int parts = (int) Math.round(Math.PI * 2 * radius * dense);
        new BukkitRunnable() {
            double horAngle = 0;
            int timePassed = 0;
            @Override
            public void run() {
                for (double partAngle = 0; partAngle < Math.PI * 2; partAngle += Math.PI * 2 / parts) {
                    final double angle = horAngle + partAngle;
                    final Location at = getInCircle(loc, radius, angle);
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
        final int parts = (int) Math.round(Math.PI * 2 * radius * dense);
        new BukkitRunnable() {
            double horAngle = 0;
            int timePassed = 0;
            @Override
            public void run() {
                for (double partAngle = 0; partAngle < Math.PI * 2; partAngle += Math.PI * 2 / parts) {
                    final double angle = horAngle + partAngle;
                    final Location at = getInCircle(loc, radius, angle);
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

        spawnMovingSphere(loc.clone().add(0,1,0),
                duration / 2, radius, Math.PI / 80,
                startAngle, endAngle, 1, Particle.FLAME, player);

        spawnMovingCircle(loc.clone().add(0, 0.15,0),
                duration, Math.cos(endAngle) * radius, 3,Math.PI / 160, Particle.FLAME, player);

        spawnMovingCircle(loc.clone().add(0, 1,0),
                duration, Math.cos(endAngle) * radius, 3, Math.PI / 160, Particle.FLAME, player);

        spawnMovingCircle(loc.clone().add(0, 1.85,0),
                duration, Math.cos(endAngle) * radius, 3, Math.PI / 160, Particle.FLAME, player);

        Bukkit.getServer().getScheduler().runTaskLater(Plugin.getInstance(), () ->
                spawnMovingSphere(loc.clone().add(0,1,0),
                        duration / 2, radius, Math.PI / 80,
                        endAngle, startAngle, 1, Particle.FLAME, player), duration / 2);

        Bukkit.getServer().getScheduler().runTaskLater(Plugin.getInstance(), ()->
                spawnFirework(loc.clone().add(0,1,0), 2), duration);
    }

    public static void drawCircleInFront(final LivingEntity entity, final double radius,
                                         final double dst, final int parts, final Particle par) {
        final Vector dir = entity.getLocation().getDirection();
        final Location center = entity.getEyeLocation().clone().add(dir.clone().multiply(dst));

        for (int i = 0; i < parts; i++) {
            final double angle = Math.PI * 2 * i / parts;
            final Vector at = rotateAroundAxis(randomVector.clone().crossProduct(dir).normalize(), dir, angle).multiply(radius);
            at.add(center.toVector());
            entity.getWorld().spawnParticle(par,
                    new Location(center.getWorld(), at.getX(), at.getY(), at.getZ()),
                    0,0,0,0);
        }
    }

    public static void drawCircleInFrontColor(final LivingEntity entity, final double radius,
                                         final double dst, final int parts,
                                         final int red, final int green, final int blue) {
        final Vector dir = entity.getLocation().getDirection();
        final Location center = entity.getEyeLocation().clone().add(dir.clone().multiply(dst));

        for (int i = 0; i < parts; i++) {
            final double angle = Math.PI * 2 * i / parts;
            final Vector at = rotateAroundAxis(randomVector.clone().crossProduct(dir).normalize(), dir, angle).multiply(radius);
            at.add(center.toVector());
            entity.getWorld().spawnParticle(Particle.REDSTONE,
                    new Location(center.getWorld(), at.getX(), at.getY(), at.getZ()),
                    0, red, green, blue, 2);
        }
    }

    public static void drawCphere(final Location loc, final double radius,
                                  final Particle particle) {
        final int parts = 8;
        final double vertAngleSpeed = Math.PI / parts;
        double vertAngle = -Math.PI / 2;
        for (int i = 0; i < parts; i++) {
            for (double partAngle = 0; partAngle < Math.PI * 2; partAngle += Math.PI * 2 / parts) {
                final Location at = getInCphere(loc.toVector(), radius, partAngle, vertAngle).toLocation(loc.getWorld());
                loc.getWorld().spawnParticle(particle, at, 0,0,0, 0);
            }

            vertAngle += vertAngleSpeed;
        }
    }

    public static Vector getRightVector(final Vector vector) {
        return new Vector(
            vector.getZ(),
            0,
            -vector.getX()
        ).normalize();
    }

    public static Vector getUpVector(final Vector vector) {
        return vector.clone().getCrossProduct(getRightVector(vector)).normalize();
    }

    public static Location[] drawSector(final Location loc,
                                  final double innerRadius, final double outerRadius,
                                  final double sumAngle,
                                  final Particle particle) {

        final int parts = 8;
        final double angleSpeed = sumAngle / (parts - 1);
        final double radSpeed = (outerRadius - innerRadius) / (parts - 1);
        final Vector upVector = getUpVector(loc.getDirection());
        final Location[] ret = new Location[parts*parts];
        int idx = 0;
        for (double angle = -sumAngle/2; angle <= sumAngle/2; angle += angleSpeed) {
            final Vector atVector = rotateAroundAxis(loc.getDirection().clone(), upVector, angle);
            for (double radius = innerRadius; radius <= outerRadius; radius += radSpeed) {
                final Location pos = loc.clone().add(atVector.clone().multiply(radius));
                loc.getWorld().spawnParticle(particle, pos, 0, 0,0,0);
                ret[idx] = pos;
                idx++;
            }
        }
        return ret;
    }
}
