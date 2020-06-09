package by.dero.gvh;

import by.dero.gvh.commands.SelectCommand;
import by.dero.gvh.events.PlayerEvents;
import by.dero.gvh.model.Data;
import by.dero.gvh.model.LocalStorage;
import by.dero.gvh.model.StorageInterface;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
        registerCommands();
        storage = new LocalStorage();
        data = new Data(storage);
        data.load();
        game = new Game();
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new PlayerEvents(), this);
    }

    private void registerCommands() {
        commandManager = new CommandManager();
        commandManager.getCommands().put("select", new SelectCommand());
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
