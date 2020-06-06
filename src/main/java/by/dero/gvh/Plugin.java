package by.dero.gvh;

import by.dero.gvh.events.PlayerEvents;
import by.dero.gvh.model.Data;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {
    private static Plugin instance;
    private Data data;

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

    public Data getData() {
        return data;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}
