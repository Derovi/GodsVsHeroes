package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;

public class HealPotionInfo extends ItemInfo {
    private double radius;
    private int heal;
    private int allyHeal;

    public HealPotionInfo(ItemDescription description) {
        super(description);
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public int getHeal() {
        return heal;
    }

    public void setHeal(int heal) {
        this.heal = heal;
    }

    public int getAllyHeal () {
        return allyHeal;
    }

    public void setAllyHeal (int allyHeal) {
        this.allyHeal = allyHeal;
    }
}
