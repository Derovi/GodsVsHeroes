package by.dero.gvh.minigame;

import by.dero.gvh.CommandManager;
import by.dero.gvh.Plugin;
import by.dero.gvh.PluginMode;
import by.dero.gvh.minigame.commands.AddSpawnPointCommand;
import by.dero.gvh.minigame.commands.FinishCommand;
import by.dero.gvh.minigame.commands.SelectCommand;
import by.dero.gvh.minigame.commands.StartCommand;
import by.dero.gvh.model.storages.LocalStorage;
import org.bukkit.Bukkit;

public class Minigame implements PluginMode {
    private static Minigame instance;
    private GameData gameData;
    private Game game;
    private CommandManager commandManager;

    @Override
    public void onEnable() {
        instance = this;
        gameData = new GameData(new LocalStorage());
        gameData.load();
        game = new DeathMatch(gameData.getGameInfo(), gameData.getDeathMatchInfo());
        game.prepare();
        Bukkit.getPluginManager().registerEvents(game, Plugin.getInstance());
        registerEvents();
        registerCommands();
    }

    @Override
    public void onDisable() {}

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new GameEvents(), Plugin.getInstance());
    }

    private void registerCommands() {
        commandManager = new CommandManager();
        commandManager.getCommands().put("select", new SelectCommand());
        commandManager.getCommands().put("start", new StartCommand());
        commandManager.getCommands().put("finish", new FinishCommand());
        commandManager.getCommands().put("addspawnpoint", new AddSpawnPointCommand());
    }

    public static Minigame getInstance() {
        return instance;
    }

    public GameData getGameData() {
        return gameData;
    }

    public Game getGame() {
        return game;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}
