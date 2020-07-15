package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;

public class PoisonousBowInfo extends ItemInfo {
    private int duration;

    public PoisonousBowInfo(ItemDescription description) {
        super(description);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
