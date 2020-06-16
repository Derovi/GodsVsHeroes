package by.dero.gvh.model;

import org.bukkit.Location;

public class Area {
    private final Location pt1, pt2;
    private final boolean territoryDamage;
    private final boolean entityDamage;

    public Area(final Location pt1, final Location pt2, final boolean terDamage, final boolean entDamage) {
        this.pt1 = new Location(
                pt1.getWorld(),
                Math.min(pt1.getX(), pt2.getX()),
                Math.min(pt1.getY(), pt2.getY()),
                Math.min(pt1.getZ(), pt2.getZ())
        );
        this.pt2 = new Location(
                pt1.getWorld(),
                Math.max(pt1.getX(), pt2.getX()),
                Math.max(pt1.getY(), pt2.getY()),
                Math.max(pt1.getZ(), pt2.getZ())
        );
        territoryDamage = terDamage;
        entityDamage = entDamage;
    }

    public boolean inside(final Location loc) {
        return pt1.getX() <= loc.getX() && loc.getX() <= pt2.getX() &&
                pt1.getY() <= loc.getY() && loc.getY() <= pt2.getY() &&
                pt1.getZ() <= loc.getZ() && loc.getZ() <= pt2.getZ();
    }

    public boolean isTerritoryDamage() {
        return territoryDamage;
    }

    public boolean isEntityDamage() {
        return entityDamage;
    }
}
