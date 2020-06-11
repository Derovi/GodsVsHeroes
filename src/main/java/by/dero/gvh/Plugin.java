package by.dero.gvh;

import by.dero.gvh.commands.SelectCommand;
import by.dero.gvh.events.PlayerEvents;
import by.dero.gvh.game.Game;
import by.dero.gvh.game.GameInfo;
import by.dero.gvh.model.Data;
import by.dero.gvh.model.LocalStorage;
import by.dero.gvh.model.StorageInterface;
import com.google.gson.Gson;
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
        registerCommands();
        storage = new LocalStorage();
        data = new Data(storage);
        data.load();
        GameInfo gameInfo = new GameInfo();
        try {
            gameInfo = new Gson().fromJson(Data.loadOrDefault(new LocalStorage(), "game", "game",
                    Utils.readResourceFile("/game.json")), GameInfo.class);
        } catch (Exception exception) {
            System.err.println("Can't load game info!");
            exception.printStackTrace();
        }
        game = new Game(gameInfo);
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
