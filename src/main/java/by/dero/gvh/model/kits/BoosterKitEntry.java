package by.dero.gvh.model.kits;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Heads;
import by.dero.gvh.model.PlayerInfo;
import by.dero.gvh.utils.GameUtils;
import org.bukkit.inventory.ItemStack;

public class BoosterKitEntry extends KitEntry {
    private String boosterName;

    public BoosterKitEntry(String playerName) {
        super(playerName);
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
