package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;
import by.dero.gvh.model.annotations.PotionItem;
import org.bukkit.potion.PotionType;

@PotionItem(potionType = PotionType.INSTANT_DAMAGE)
public class DamagePotionInfo extends ItemInfo {
    private double damage;
    private double radius;

    public DamagePotionInfo(ItemDescription description) {
        super(description);
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
