package by.dero.gvh.utils;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class DirectedPosition extends Position {
    @Getter private double dx = 0;
    @Getter private double dy = 0;
    @Getter private double dz = 0;

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
    
    public double getPitch() {
        if (dx == 0 && dz == 0) {
            return 0;
        }
        return new Vector(dx, 0, dz).angle(MathUtils.ZEROVECTOR);
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
    
    public void setDirection(double dx, double dy, double dz) {
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }
}
