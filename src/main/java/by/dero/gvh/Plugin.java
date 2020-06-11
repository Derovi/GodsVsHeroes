package by.dero.gvh;

import by.dero.gvh.commands.AddSpawnPointCommand;
import by.dero.gvh.commands.FinishCommand;
import by.dero.gvh.commands.SelectCommand;
import by.dero.gvh.commands.StartCommand;
import by.dero.gvh.events.PlayerEvents;
import by.dero.gvh.game.DeathMatch;
import by.dero.gvh.game.Game;
import by.dero.gvh.game.GameData;
import by.dero.gvh.game.GameInfo;
import by.dero.gvh.model.Data;
import by.dero.gvh.model.LocalStorage;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {
    private static Plugin instance;
    private Game game;
    private Data data;
    private GameData gameData;

    private CommandManager commandManager;

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        registerEvents();
        registerCommands();
        data = new Data(new LocalStorage());
        data.load();
        gameData = new GameData(new LocalStorage());
        game = new DeathMatch(gameData.getGameInfo(), gameData.getDeathMatchInfo());
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new PlayerEvents(), this);
    }

    private void registerCommands() {
        commandManager = new CommandManager();
        commandManager.getCommands().put("select", new SelectCommand());
        commandManager.getCommands().put("start", new StartCommand());
        commandManager.getCommands().put("finish", new FinishCommand());
        commandManager.getCommands().put("addspawnpoint", new AddSpawnPointCommand());
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
