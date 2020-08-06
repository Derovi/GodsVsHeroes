package by.dero.gvh.utils;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import ru.cristalix.core.math.V3;

public class Position implements Cloneable {
    @Getter @Setter
    private double x, y, z;

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

    public void add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    @Override
    public Position clone() {
        return new Position(x, y, z);
    }

    public double distance(Position other) {
        return Math.sqrt((x - other.x) * (x - other.x) +
                        (y - other.y) * (y - other.y) +
                        (z - other.z) * (z - other.z));
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }

    public V3 toV3() {
        return new V3(x, y, z);
    }
    
    public Location toLocation(String worldName) {
        return new Location(Bukkit.getWorld(worldName), x, y, z);
    }

    public Location toLocation(World world) {
        return new Location(world, x, y, z);
    }
}
