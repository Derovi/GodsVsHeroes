package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;

public class SkeletonArmyInfo extends ItemInfo {
    private int melee;
    private int meleeDamage;
    private int meleeHealth;
    private int range;
    private int duration;

    public SkeletonArmyInfo(ItemDescription description) {
        super(description);
    }

    public int getMelee() {
        return melee;
    }

    public void setMelee(int melee) {
        this.melee = melee;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getDuration () {
        return duration;
    }

    public void setDuration (int duration) {
        this.duration = duration;
    }

    public int getMeleeDamage () {
        return meleeDamage;
    }

    public void setMeleeDamage (int meleeDamage) {
        this.meleeDamage = meleeDamage;
    }

    public int getMeleeHealth () {
        return meleeHealth;
    }

    public void setMeleeHealth (int meleeHealth) {
        this.meleeHealth = meleeHealth;
    }
}
