package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;
import by.dero.gvh.model.annotations.PotionItem;
import org.bukkit.potion.PotionType;

@PotionItem(potionType = PotionType.POISON)
public class PoisonPotionInfo extends ItemInfo {
    private double radius;
    private int duration;

    public PoisonPotionInfo(ItemDescription description) {
        super(description);
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public int getDuration () {
        return duration;
    }

    public void setDuration (int duration) {
        this.duration = duration;
    }
}
