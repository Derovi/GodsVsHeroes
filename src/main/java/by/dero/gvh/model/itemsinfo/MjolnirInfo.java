package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.annotations.CustomDamage;
import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;
import by.dero.gvh.model.annotations.NbtAddition;
import by.dero.gvh.nmcapi.NMCUtils;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.inventory.ItemStack;

public class MjolnirInfo extends ItemInfo {
    private double damage;

    @CustomDamage
    private int meleeDamage;

    public MjolnirInfo(ItemDescription description) {
        super(description);
    }


    @NbtAddition
    public static void addNbt(ItemInfo info, ItemStack itemStack) {
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
