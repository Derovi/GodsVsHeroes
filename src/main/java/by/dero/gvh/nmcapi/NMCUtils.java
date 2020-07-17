package by.dero.gvh.nmcapi;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NMCUtils {
    public static void setNBT(ItemStack itemStack, NBTTagCompound tagCompound) {
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        nmsStack.setTag(tagCompound);
        itemStack.setItemMeta(nmsStack.asBukkitCopy().getItemMeta());
    }

    public static NBTTagCompound getNBT(ItemStack itemStack) {
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        return (nmsStack.hasTag() ? nmsStack.getTag() : new NBTTagCompound());
    }
}
