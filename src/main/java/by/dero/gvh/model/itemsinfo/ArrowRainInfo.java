package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemInfo;

public class ArrowRainInfo extends ItemInfo {
    private double radius = 20;
    private int arrowCycles = 100;
    private int cycleDelay = 5;
    private int cooldown = 5;

    public int getCooldown() {
        return cooldown;
    }

    public int getArrowCycles() {
        return arrowCycles;
    }

    public void setArrowCycles(int arrowCycles) {
        this.arrowCycles = arrowCycles;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public int getCycleDelay() {
        return cycleDelay;
    }

    public void setCycleDelay(int cycleDelay) {
        this.cycleDelay = cycleDelay;
    }
}
