package by.dero.gvh.model.kits;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.PlayerInfo;
import by.dero.gvh.utils.InterfaceUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ExpLoot extends LootBoxItem {
    public enum Type {
        K1, K2, K3, K4, K5, K6;

        public int getValue() {
            switch (this) {
                case K1:
                    return 100;
                case K2:
                    return 200;
                case K3:
                    return 300;
                case K4:
                    return 400;
                case K5:
                    return 500;
                case K6:
                    return 600;
            }
            return 0;
        }

        public Material getMaterial() {
            switch (this) {
                case K1:
                    return Material.CLAY_BALL;
                case K2:
                    return Material.EMERALD;
                case K3:
                    return Material.MELON;
                case K4:
                    return Material.SLIME_BLOCK;
                case K5:
                    return Material.EMERALD_BLOCK;
                case K6:
                    return Material.EXP_BOTTLE;
            }
            return Material.BEDROCK;
        }
    }

    Type type;

    public ExpLoot(String playerName, int chance, Type type) {
        super(playerName, chance);
        this.type = type;
    }

    @Override
    public void give() {
        PlayerInfo info = Plugin.getInstance().getPlayerData().getPlayerInfo(getPlayerName());
        info.setBalance(info.getBalance() + type.getValue());
        Plugin.getInstance().getPlayerData().savePlayerInfo(info);
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(type.getMaterial());
        InterfaceUtils.changeName(itemStack, "§a" + type.getValue());
        return itemStack;
    }
}
