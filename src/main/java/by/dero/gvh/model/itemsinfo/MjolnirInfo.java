package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.GamePlayer;
import by.dero.gvh.model.annotations.CustomDamage;
import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;
import by.dero.gvh.model.annotations.DynamicCustomization;
import by.dero.gvh.model.annotations.StaticCustomization;
import by.dero.gvh.nmcapi.NMCUtils;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class MjolnirInfo extends ItemInfo {
    private double damage;

    @CustomDamage
    private int meleeDamage;

    public MjolnirInfo(ItemDescription description) {
        super(description);
    }


    @StaticCustomization
    public static void staticCustomize(ItemStack itemStack, ItemInfo info) {
        NBTTagCompound nbt = NMCUtils.getNBT(itemStack);
        nbt.set("aether", new NBTTagString("thor"));
        NMCUtils.setNBT(itemStack, nbt);
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
