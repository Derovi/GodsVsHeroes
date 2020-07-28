package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.CosmeticManager;
import by.dero.gvh.Plugin;
import by.dero.gvh.model.CosmeticInfo;
import by.dero.gvh.model.CustomizationContext;
import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;
import by.dero.gvh.model.annotations.CustomDamage;
import org.bukkit.inventory.ItemStack;

public class KnifeThrowInfo extends ItemInfo {
    private double damage;

    @CustomDamage
    private int meleeDamage;

    public KnifeThrowInfo(ItemDescription description) {
        super(description);
    }

    @Override
    public ItemStack dynamicCustomization(ItemStack itemStack, CustomizationContext context) {
        CosmeticInfo cosmeticInfo = Plugin.getInstance().getCosmeticManager().getByGroup(
                context.getPlayer(), CosmeticManager.getWeaponGroup(context.getClassName()));
        if (cosmeticInfo != null) {
            cosmeticInfo.addNBT(itemStack);
        }
        return itemStack;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public int getMeleeDamage () {
        return meleeDamage;
    }

    public void setMeleeDamage (int meleeDamage) {
        this.meleeDamage = meleeDamage;
    }
}
