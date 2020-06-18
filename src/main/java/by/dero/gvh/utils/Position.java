package by.dero.gvh.utils;

import javafx.geometry.Pos;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Position {
    double x, y, z;

    public Position() {
    }

    public Position(Location location) {
        x = location.getX();
        y = location.getY();
        z = location.getZ();
    }

    public Position(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double distance(Position other) {
        return Math.sqrt((x - other.x) * (x - other.x) +
                        (y - other.y) * (y - other.y) +
                        (z - other.z) * (z - other.z));
    }

    public Location toLocation(String worldName) {
        return new Location(Bukkit.getWorld(worldName), x, y, z);
    }

    public Location toLocation(World world) {
        return new Location(world, x, y, z);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }
}
