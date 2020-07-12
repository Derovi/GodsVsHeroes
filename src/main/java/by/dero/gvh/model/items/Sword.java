package by.dero.gvh.model.items;

import by.dero.gvh.model.Item;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Sword extends Item {
    public Sword(String name, int level, Player owner) {
        super(name, level, owner);
        owner.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(1024.0D);
        owner.saveData();
    }

    @Override
    public ItemStack getItemStack () {
        ItemStack sword = new ItemStack(getInfo().getMaterial(), 1);

        sword = setItemMeta(sword, name, getInfo());
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(sword);
        NBTTagCompound compound = nmsStack.hasTag() ? nmsStack.getTag() : new NBTTagCompound();
        NBTTagList modifiers = new NBTTagList();
        NBTTagCompound damage = new NBTTagCompound();
        damage.set("AttributeName", new NBTTagString("generic.attackDamage"));
        damage.set("Name", new NBTTagString("generic.attackDamage"));
        damage.set("Amount", new NBTTagInt(20));
        damage.set("Operation", new NBTTagInt(0));
        damage.set("UUIDLeast", new NBTTagInt(894654));
        damage.set("UUIDMost", new NBTTagInt(2872));
        damage.set("Slot", new NBTTagString("mainhand"));
        modifiers.add(damage);
        compound.set("AttributeModifiers", modifiers);
        nmsStack.setTag(compound);
        sword = CraftItemStack.asBukkitCopy(nmsStack);

        return sword;
    }
}
