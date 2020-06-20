package by.dero.gvh.model;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public class Drawings {
    private static final double dense = 2;

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
}
