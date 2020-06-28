package by.dero.gvh.utils;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class MathUtils {
    private static final short CALCSIZE = 30000;
    public static final double PI2 = Math.PI * 2;
    public static final double ANGTOIDX = 1.0 * CALCSIZE / PI2;
    public static final double IDXTOANG = 1 / ANGTOIDX;
    public static final Vector UPVECTOR  = new Vector(0, 1, 0);
    public static final Vector ZEROVECTOR  = new Vector(0, 0, 0);
    private static final double[] COS = new double[CALCSIZE+1];
    private static final double[] SIN = new double[CALCSIZE+1];
    private static final double[] TG = new double[CALCSIZE+1];
    private static final double[] CTG = new double[CALCSIZE+1];

    public MathUtils() {
        double ang = 0;
        final int halfedsize = CALCSIZE / 2;
        for (short i = 0; i < CALCSIZE; i++, ang += IDXTOANG) {
            COS[i] = Math.cos(ang);
            SIN[i] = Math.sqrt(1.0 - COS[i] * COS[i]);
            if (i > halfedsize) {
                SIN[i] = -SIN[i];
            }
            if (COS[i] != 0) {
                TG[i] = SIN[i] / COS[i];
            }
            if (SIN[i] != 0) {
                CTG[i] = COS[i] / SIN[i];
            }
        }
    }

    public static double getRightAngle(double angle) {
        if (angle < 0) {
            angle += Math.floor(-angle / PI2 + 1) * PI2;
        }
        if (angle > PI2) {
            angle -= Math.floor(angle / PI2) * PI2;
        }
        return angle;
    }

    public static double cos(final double angle) {
        return COS[(int) Math.floor(getRightAngle(angle)*ANGTOIDX)];
    }

    public static double sin(double angle) {
        return SIN[(int) Math.floor(getRightAngle(angle)*ANGTOIDX)];
    }

    public static double tg(double angle) {
        return TG[(int) Math.floor(getRightAngle(angle)*ANGTOIDX)];
    }

    public static double ctg(double angle) {
        return CTG[(int) Math.floor(getRightAngle(angle)*ANGTOIDX)];
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
}
