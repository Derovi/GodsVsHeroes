package by.dero.gvh.minigame;

import by.dero.gvh.CommandManager;
import by.dero.gvh.Plugin;
import by.dero.gvh.PluginMode;
import by.dero.gvh.commands.AddAreaCommand;
import by.dero.gvh.commands.TestCommand;
import by.dero.gvh.commands.UnlockClassCommand;
import by.dero.gvh.minigame.commands.AddSpawnPointCommand;
import by.dero.gvh.minigame.commands.FinishCommand;
import by.dero.gvh.minigame.commands.SelectCommand;
import by.dero.gvh.minigame.commands.StartCommand;
import by.dero.gvh.model.AreaManager;
import by.dero.gvh.model.ServerType;
import by.dero.gvh.model.storages.LocalStorage;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.World;

public class Minigame implements PluginMode {
    private static Minigame instance;
    private static AreaManager areaManager;
    private GameData gameData;
    private Game game;

    public GameEvents getGameEvents() {
        return gameEvents;
    }

    private GameEvents gameEvents;
    private CommandManager commandManager;

    @Override
    public void onEnable() {
        instance = this;

        Plugin.getInstance().getServerData().register(Plugin.getInstance().getSettings().getServerName(),
                ServerType.GAME);
        areaManager = new AreaManager();
        gameData = new GameData(new LocalStorage());
        gameData.load();
        game = new DeathMatch(gameData.getGameInfo(), gameData.getDeathMatchInfo());

        final World world = Bukkit.getWorld(game.getInfo().getWorld());
        world.setTime(1000);
        world.setGameRule(GameRule.KEEP_INVENTORY, true);
        world.setDifficulty(Difficulty.NORMAL);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_MOB_LOOT, false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_MOB_LOOT, false);
        game.prepare();
        Bukkit.getPluginManager().registerEvents(game, Plugin.getInstance());
        registerEvents();
        registerCommands();
    }

    @Override
    public void onDisable() {}

    private void registerEvents() {
        gameEvents = new GameEvents();
        Bukkit.getPluginManager().registerEvents(gameEvents, Plugin.getInstance());
    }

    private void registerCommands() {
        commandManager = new CommandManager();
        commandManager.getCommands().put("select", new SelectCommand());
        commandManager.getCommands().put("start", new StartCommand());
        commandManager.getCommands().put("finish", new FinishCommand());
        commandManager.getCommands().put("addspawnpoint", new AddSpawnPointCommand());
        commandManager.getCommands().put("addarea", new AddAreaCommand());
        commandManager.getCommands().put("unlock", new UnlockClassCommand());
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

    public static AreaManager getAreaManager() {
        return areaManager;
    }
}
