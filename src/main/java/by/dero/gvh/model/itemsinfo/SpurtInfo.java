package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;

public class SpurtInfo extends ItemInfo {
    private int power;
    private int speedTime;

    public SpurtInfo(ItemDescription description) {
        super(description);
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getSpeedTime() {
        return speedTime;
    }

    public void setSpeedTime(int speedTime) {
        this.speedTime = speedTime;
    }
}
