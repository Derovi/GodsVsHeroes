package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;

public class DragonFlyInfo extends ItemInfo {
    private int fireballCoolDown;
    private int duration;
    private double damage;
    private double speed;

    public DragonFlyInfo(ItemDescription description) {
        super(description);
    }

    public int getFireballCoolDown() {
        return fireballCoolDown;
    }

    public void setFireballCoolDown(int fireballCoolDown) {
        this.fireballCoolDown = fireballCoolDown;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
