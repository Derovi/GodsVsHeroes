package by.dero.gvh.utils;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class MathUtils {
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
                loc.getX() + Math.cos(angle) * radius,
                loc.getY(),
                loc.getZ() + Math.sin(angle) * radius
        );
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
}
