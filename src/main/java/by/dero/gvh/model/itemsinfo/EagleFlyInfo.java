package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;
import lombok.Getter;
import lombok.Setter;

public class EagleFlyInfo extends ItemInfo {
    private double speed;
    private int duration;
    @Getter
    @Setter
    private int heal;

    public EagleFlyInfo(ItemDescription description) {
        super(description);
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
