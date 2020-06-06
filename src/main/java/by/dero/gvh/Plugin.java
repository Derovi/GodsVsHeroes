package by.dero.gvh;

import by.dero.gvh.events.PlayerEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {
    private static Plugin instance;

    private CommandManager commandManager;

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        registerEvents();
        commandManager = new CommandManager();
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new PlayerEvents(), this);
    }

    public static Plugin getInstance() {
        return instance;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}
