package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemInfo;

public class DragonFlyInfo extends ItemInfo {
    private int fireballCoolDown;
    private int duration;
    private int damage;

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

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
}
