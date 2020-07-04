package by.dero.gvh.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class IntPosition {
    int x;
    int y;
    int z;

    public IntPosition() {
    }

    public IntPosition(Location location) {
        x = location.getBlockX();
        y = location.getBlockY();
        z = location.getBlockZ();
    }

    public IntPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double distance(IntPosition other) {
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

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }
}
