package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemInfo;
import org.bukkit.Particle;

public class LightningStormInfo extends ItemInfo {
    private double radius;
    private int damage;
    private int strikes;
    private double[] signRadius;
    private long delayStrikes;
    private Particle drawParticle;

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public int getStrikes() {
        return strikes;
    }

    public void setStrikes(int strikes) {
        this.strikes = strikes;
    }

    public double[] getSignRadius() {
        return signRadius;
    }

    public void setSignRadius(double[] signRadius) {
        this.signRadius = signRadius;
    }

    public long getDelayStrikes() {
        return delayStrikes;
    }

    public void setDelayStrikes(long delayStrikes) {
        this.delayStrikes = delayStrikes;
    }

    public Particle getDrawParticle() {
        return drawParticle;
    }

    public void setDrawParticle(Particle drawParticle) {
        this.drawParticle = drawParticle;
    }

    public int getDamage () {
        return damage;
    }

    public void setDamage (int damage) {
        this.damage = damage;
    }
}
