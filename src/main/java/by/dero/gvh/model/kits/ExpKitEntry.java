package by.dero.gvh.model.kits;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.PlayerInfo;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ExpKitEntry extends KitEntry {
    private int count;

    public ExpKitEntry(String playerName, int count) {
        super(playerName);
        this.count = count;
    }

    @Override
    public void give() {
        PlayerInfo info = Plugin.getInstance().getPlayerData().getPlayerInfo(getPlayerName());
        info.setBalance(info.getBalance() + count);
        Plugin.getInstance().getPlayerData().savePlayerInfo(info);
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemStack(Material.EXP_BOTTLE);
    }
}
