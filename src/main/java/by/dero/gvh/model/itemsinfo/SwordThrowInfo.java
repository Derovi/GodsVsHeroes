package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.CosmeticManager;
import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.minigame.Minigame;
import by.dero.gvh.model.CosmeticInfo;
import by.dero.gvh.model.CustomizationContext;
import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;
import by.dero.gvh.model.annotations.CustomDamage;
import by.dero.gvh.nmcapi.NMCUtils;
import by.dero.gvh.utils.GameUtils;
import com.sk89q.jnbt.NBTUtils;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagInt;
import net.minecraft.server.v1_12_R1.NBTTagList;
import net.minecraft.server.v1_12_R1.NBTTagString;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;

public class SwordThrowInfo extends ItemInfo {
    private double damage;

    @CustomDamage
    private int meleeDamage;


    @Override
    public ItemStack dynamicCustomization(ItemStack itemStack, CustomizationContext context) {
        CosmeticInfo cosmeticInfo = Plugin.getInstance().getCosmeticManager().getByGroup(
                context.getPlayer(), CosmeticManager.getWeaponGroup(context.getClassName()));

        if (cosmeticInfo != null) {
            cosmeticInfo.addNBT(itemStack);
        }

        if (context.getClassName().equals("paladin")) {
            if (Plugin.getInstance().getPluginMode() instanceof Minigame &&
                    Game.getInstance().getState().equals(Game.State.GAME) &&
                    GameUtils.getPlayer(context.getPlayer().getName()).isUltimateBuf()) {
                itemStack.setType(Material.DIAMOND_SWORD);
                NBTTagCompound compound = NMCUtils.getNBT(itemStack);
                NBTTagList modifiers = new NBTTagList();
                NBTTagCompound damage = new NBTTagCompound();
                damage.set("AttributeName", new NBTTagString("generic.attackDamage"));
                damage.set("Name", new NBTTagString("generic.attackDamage"));
                damage.set("Amount", new NBTTagInt(getMeleeDamage()));
                damage.set("Operation", new NBTTagInt(0));
                damage.set("UUIDLeast", new NBTTagInt(894654));
                damage.set("UUIDMost", new NBTTagInt(2872));
                damage.set("Slot", new NBTTagString("mainhand"));
                modifiers.add(damage);
                compound.set("AttributeModifiers", modifiers);
            } else {
                itemStack.setType(getMaterial());
            }
        }
        return itemStack;
    }

    public SwordThrowInfo(ItemDescription description) {
        super(description);
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
