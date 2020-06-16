package by.dero.gvh.lobby;

import by.dero.gvh.Plugin;
import by.dero.gvh.PluginMode;
import by.dero.gvh.minigame.GameEvents;
import org.bukkit.Bukkit;

public class Lobby implements PluginMode {
    @Override
    public void onEnable() {
        registerEvents();
    }

    @Override
    public void onDisable() {}

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new LobbyEvents(), Plugin.getInstance());
    }
}
