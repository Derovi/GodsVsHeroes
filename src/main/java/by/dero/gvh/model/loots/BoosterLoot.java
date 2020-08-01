package by.dero.gvh.model.loots;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.PlayerInfo;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.inventory.ItemStack;

public class BoosterLoot extends LootBoxItem {
    private String boosterName;

    public BoosterLoot(String playerName, int chance) {
        super(playerName, chance);
    }

    @Override
    public void give() {
        PlayerInfo info = Plugin.getInstance().getPlayerData().getPlayerInfo(getPlayerName());
        info.activateBooster(boosterName);
        Plugin.getInstance().getPlayerData().savePlayerInfo(info);
    }

    @Override
    public ItemStack getItemStack() {
        return GameUtils.getBoosterHead(boosterName);
    }
}
