package by.dero.gvh.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class DirectedPosition extends Position {
    private double dx = 0;
    private double dy = 0;
    private double dz = 0;

    public DirectedPosition() {
    }

    public DirectedPosition(Location location) {
        super(location);
        setDirection(location.getDirection());
    }

    public DirectedPosition(double x, double y, double z, Vector direction) {
        super(x, y, z);
        setDirection(direction);
    }

    @Override
    public Location toLocation(String worldName) {
        return toLocation(Bukkit.getWorld(worldName));
    }

    @Override
    public Location toLocation(World world) {
        Location result = new Location(world, getX(), getY(), getZ());
        result.setDirection(getDirection());
        return result;
    }

    public Vector getDirection() {
        return new Vector(dx, dy, dz);
    }

    public void setDirection(Vector direction) {
        this.dx = direction.getX();
        this.dy = direction.getY();
        this.dz = direction.getZ();
    }
}
