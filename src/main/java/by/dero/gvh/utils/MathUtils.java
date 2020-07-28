package by.dero.gvh.utils;

import net.minecraft.server.v1_12_R1.MathHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

public class MathUtils {
    private static final short CALCSIZE = 30000;
    public static final double PI2 = Math.PI * 2;
    public static final double ANGTOIDX = 1.0 * CALCSIZE / PI2;
    public static final double IDXTOANG = 1 / ANGTOIDX;
    public static final Vector UPVECTOR  = new Vector(0, 1, 0);
    public static final Vector DOWNVECTOR  = new Vector(0, -1, 0);
    public static final Vector ZEROVECTOR  = new Vector(0, 0, 0);

    public static double getRightAngle(double angle) {
        if (angle < 0) {
            angle += Math.floor(-angle / PI2 + 1) * PI2;
        }
        if (angle > PI2) {
            angle -= Math.floor(angle / PI2) * PI2;
        }
        return angle;
    }

    public static double cos(double angle) {
        return MathHelper.cos((float) angle);
    }

    public static double sin(double angle) {
        return MathHelper.sin((float) angle);
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

    public static Location getInCircle(Location loc, double radius, double angle) {
        return new Location(
                loc.getWorld(),
                loc.getX() + cos(angle) * radius,
                loc.getY(),
                loc.getZ() + sin(angle) * radius
        );
    }
    
    public static Location randomCylinder(final Location center, final double minRadius, final double maxRadius, final double depth) {
        final double dst = Math.random() * (maxRadius - minRadius) + minRadius;
        return randomCylinderWall(center, dst, depth);
    }
    public static Location randomCylinder(final Location center, final double radius, final double depth) {
        final double dst = Math.random() * radius;
        return randomCylinderWall(center, dst, depth);
    }

    public static Location randomCylinderWall(final Location center, final double radius, final double depth) {
        final double angle = Math.random() * MathUtils.PI2;
        return center.clone().add(
                radius*cos(angle),
                -Math.random() * depth,
                radius*sin(angle)
        );
    }

    public static Vector rotateAroundAxis(final Vector point, final Vector axis, double angle) throws IllegalArgumentException {
        return rotateAroundNonUnitAxis(point, axis.length() == 1 ? axis : axis.clone().normalize(), angle);
    }

    public static Vector rotateAroundNonUnitAxis(final Vector point, final Vector axis, final double angle) {
        double x = point.getX(), y = point.getY(), z = point.getZ();
        double x2 = axis.getX(), y2 = axis.getY(), z2 = axis.getZ();

        double cosTheta = cos(angle);
        double sinTheta = sin(angle);
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

    public static Vector getInCphere(final Vector center,
                                     final double radius,
                                     final double horAngle,
                                     final double vertAngle) {

        Vector locCenter = new Vector(center.getX(), center.getY() + sin(vertAngle) * radius, center.getZ());
        final double locRadius = cos(vertAngle) * radius;
        return locCenter.add(new Vector(
                cos(horAngle) * locRadius,
                0,
                sin(horAngle) * locRadius)
        );
    }

    public static Vector getInCphereByHeight(Vector center, double radius, double horAngle, double height) {
        double vertAngle = Math.asin(height / radius * 2 - 1);
        return getInCphere(center, radius, horAngle, vertAngle);
    }

    public static Location getGoodInCylinder(Location zxc, double minradius, double radius) {
        Location loc;
        do {
            loc = MathUtils.randomCylinder(zxc.clone(), minradius, radius, 0);
//        loc.y = loc.getWorld().getHighestBlockYAt((int)loc.x, (int)loc.z) + 1;
            while (loc.getBlock().getType().equals(Material.AIR) &&
                    loc.clone().add(0, -1, 0).getBlock().getType().equals(Material.AIR) &&
                    loc.clone().add(0, 1, 0).getBlock().getType().equals(Material.AIR)) {
                loc = loc.subtract(0, 1, 0);
                if (loc.getBlock().getType().equals(Material.BARRIER)) {
                    break;
                }
            }
            while ((!loc.getBlock().getType().equals(Material.AIR) ||
                    !loc.clone().add(0, 1, 0).getBlock().getType().equals(Material.AIR) ||
                    !loc.clone().add(0, 2, 0).getBlock().getType().equals(Material.AIR)) &&
                    loc.getY() < 180) {
                if (loc.getBlock().getType().equals(Material.BARRIER)) {
                    break;
                }
                loc = loc.add(0, 1, 0);
            }
        } while (loc.getY() > 180 && !loc.getBlock().getType().equals(Material.BARRIER));
        return loc;
    }

    public static int randInt(int rvalue) {
        return (int) (Math.random() * rvalue);
    }
}
