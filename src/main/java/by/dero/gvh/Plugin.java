package by.dero.gvh;

import by.dero.gvh.events.PlayerEvents;
import by.dero.gvh.model.Data;
import by.dero.gvh.model.LocalStorage;
import by.dero.gvh.model.StorageInterface;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {
    private static Plugin instance;
    private StorageInterface storage;
    private Data data;
    private Game game;

    private CommandManager commandManager;

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        registerEvents();
        commandManager = new CommandManager();
        storage = new LocalStorage();
        storage.prepare();
        Data data = new Data(storage);
        data.load();
        Game game = new Game();
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

    public Game getGame() {
        return game;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}
