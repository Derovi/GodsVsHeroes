package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemInfo;

public class EscapeTeleportInfo extends ItemInfo {
    private double radius;
    private double minRadius;

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getMinRadius() {
        return minRadius;
    }

    public void setMinRadius(double minRadius) {
        this.minRadius = minRadius;
    }
}
