package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;
import by.dero.gvh.model.annotations.CustomDamage;
import by.dero.gvh.model.annotations.DynamicCustomization;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SwordThrowInfo extends ItemInfo {
    private double damage;

    @CustomDamage
    private int meleeDamage;

    @DynamicCustomization
    public void customize(ItemStack itemStack, ItemInfo info, Player player) {
        if (Game.getInstance().getState().equals(Game.State.GAME) &&
                GameUtils.getPlayer(player.getName()).isUltimateBuf()) {
            itemStack.setType(Material.DIAMOND_SWORD);
        } else {
            itemStack.setType(info.getMaterial());
        }
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
