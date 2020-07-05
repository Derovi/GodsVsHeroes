package by.dero.gvh.minigame;

import by.dero.gvh.CommandManager;
import by.dero.gvh.Plugin;
import by.dero.gvh.PluginMode;
import by.dero.gvh.commands.AddAreaCommand;
import by.dero.gvh.commands.UnlockClassCommand;
import by.dero.gvh.minigame.commands.AddSpawnPointCommand;
import by.dero.gvh.minigame.commands.FinishCommand;
import by.dero.gvh.minigame.commands.SelectCommand;
import by.dero.gvh.minigame.commands.StartCommand;
import by.dero.gvh.minigame.deathmatch.DeathMatch;
import by.dero.gvh.minigame.ethercapture.EtherCapture;
import by.dero.gvh.model.AreaManager;
import by.dero.gvh.model.ServerType;
import by.dero.gvh.model.storages.LocalStorage;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;

public class Minigame implements PluginMode {
    private static Minigame instance;
    private AreaManager areaManager;
    private GameData gameData;
    private Game game;

    public GameEvents getGameEvents() {
        return gameEvents;
    }

    private GameEvents gameEvents;
    private CommandManager commandManager;
    private World world;

    private LootsManager lootsManager;

    @Override
    public void onEnable() {
        instance = this;

        Plugin.getInstance().getServerData().register(Plugin.getInstance().getSettings().getServerName(),
                ServerType.GAME);
        gameData = new GameData(new LocalStorage());
        gameData.load();
        if (gameData.getGameInfo().getMode().equals("deathMatch")) {
            game = new DeathMatch(gameData.getGameInfo(), gameData.getDeathMatchInfo());
        } else {
            game = new EtherCapture(gameData.getGameInfo(), gameData.getEtherCaptureInfo());
        }

        world = Bukkit.getWorld(game.getInfo().getWorld());
        world.setTime(1000);
        world.setDifficulty(Difficulty.NORMAL);
        world.setGameRuleValue("keepInventory", "true");
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("doMobLoot", "false");
        world.setGameRuleValue("announceAdvancements", "false");
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "weather clear 100000");
        game.prepare();
        registerEvents();
        registerCommands();
    }

    @Override
    public void onDisable() {
        if (game != null) {
            game.unload();
        }
    }

    private void registerEvents() {
        gameEvents = new GameEvents();
        lootsManager = new LootsManager();
        areaManager = new AreaManager();
        Plugin instance = Plugin.getInstance();
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(gameEvents, instance);
        manager.registerEvents(areaManager, instance);
        manager.registerEvents(lootsManager, instance);
        manager.registerEvents(new DoubleSpaceListener(), instance);
        manager.registerEvents(new LiftManager(), instance);
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

    public World getWorld() {
        return world;
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

    public AreaManager getAreaManager() {
        return areaManager;
    }

    public LootsManager getLootsManager() {
        return lootsManager;
    }
}
