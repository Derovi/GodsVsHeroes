package by.dero.gvh;

import by.dero.gvh.commands.*;
import by.dero.gvh.events.PlayerEvents;
import by.dero.gvh.game.DeathMatch;
import by.dero.gvh.game.Game;
import by.dero.gvh.game.GameData;
import by.dero.gvh.game.StandManager;
import by.dero.gvh.model.Data;
import by.dero.gvh.model.storages.LocalStorage;
import by.dero.gvh.model.PlayerData;
import by.dero.gvh.model.StorageInterface;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {
    private static Plugin instance;
    private StorageInterface storage;
    private Data data;
    private Game game;
    private GameData gameData;
    private PlayerData playerData;

    private CommandManager commandManager;

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        registerEvents();
        registerCommands();
        //data = new Data(new MongoDBStorage("mongodb://minigame:ORg3.47gZ51@79.174.13.142:27017/?authSource=admin",
        //        "gvh"));
        data = new Data(new LocalStorage());
        data.load();
        gameData = new GameData(new LocalStorage());
        gameData.load();
        playerData = new PlayerData(new LocalStorage());
        System.out.println("n3 " + (gameData.getDeathMatchInfo() == null));
        game = new DeathMatch(gameData.getGameInfo(), gameData.getDeathMatchInfo());
        game.prepare();
        Bukkit.getPluginManager().registerEvents(game, this);
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new PlayerEvents(), this);
        Bukkit.getPluginManager().registerEvents(new StandManager(), this);
    }

    private void registerCommands() {
        commandManager = new CommandManager();
        commandManager.getCommands().put("select", new SelectCommand());
        commandManager.getCommands().put("start", new StartCommand());
        commandManager.getCommands().put("finish", new FinishCommand());
        commandManager.getCommands().put("addspawnpoint", new AddSpawnPointCommand());
        commandManager.getCommands().put("makestand", new MakeStandCommand());
    }

    public GameData getGameData() {
        return gameData;
    }

    public PlayerData getPlayerData() {
        return playerData;
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
