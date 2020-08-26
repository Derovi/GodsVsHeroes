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
import by.dero.gvh.minigame.flagCapture.FlagCapture;
import by.dero.gvh.model.AreaManager;
import by.dero.gvh.model.ServerType;
import by.dero.gvh.model.storages.LocalStorage;
import by.dero.gvh.utils.WorldUtils;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import ru.cristalix.core.realm.IRealmService;

import java.util.ArrayList;

public class Minigame implements PluginMode {
    @Getter private static Minigame instance;
    @Getter private AreaManager areaManager;
    @Getter private GameData gameData;
    @Getter private Game game;

    @Getter private GameEvents gameEvents;
    @Getter private CommandManager commandManager;
    @Getter private World lobbyWorld;

    @Getter private LootsManager lootsManager;
    @Getter private LiftManager liftManager;
    public static long startTime;
    
    @Getter private static final ArrayList<String> modes = Lists.newArrayList(
            "etherCapture",
            "deathMatch",
            "flagCapture"
    );
    

    @Override
    public void onEnable() {
        instance = this;
        startTime = System.currentTimeMillis();

        gameData = new GameData(new LocalStorage());
        gameData.load();
        switch (gameData.getGameInfo().getMode()) {
            case "deathMatch" : game = new DeathMatch(gameData.getGameInfo(), gameData.getDeathMatchInfo()); break;
            case "etherCapture" : game = new EtherCapture(gameData.getGameInfo(), gameData.getEtherCaptureInfo()); break;
            default : game = new FlagCapture(gameData.getGameInfo(), gameData.getFlagCaptureInfo()); break;
        }
        
        Plugin.getInstance().getServerData().register(Plugin.getInstance().getSettings().getServerName(),
                ServerType.GAME, gameData.getGameInfo().getMode(), game.getInfo().getMaxPlayerCount());

        lobbyWorld = Bukkit.getWorld(game.getInfo().getLobbyWorld());
        WorldUtils.prepareWorld(lobbyWorld);
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "weather clear 100000");
        game.prepare();
        registerEvents();
        registerCommands();
        if (Plugin.getInstance().getSettings().isCristalix()) {
            IRealmService.get().getCurrentRealmInfo().setMaxPlayers(game.getInfo().getMaxPlayerCount());
        }
        GameTabWrapper gameTabWrapper = new GameTabWrapper(Plugin.getInstance());
    }

    public static Long getLastTicks() {
        return (System.currentTimeMillis() - startTime) / 50;
    }

    @Override
    public void onDisable() {
        if (game != null) {
            game.unload();
        }
        Plugin.getInstance().getServerData().unregister(Plugin.getInstance().getSettings().getServerName());
    }

    private void registerEvents() {
        gameEvents = new GameEvents();
        lootsManager = new LootsManager();
        areaManager = new AreaManager();
        liftManager = new LiftManager();
        Plugin instance = Plugin.getInstance();
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(gameEvents, instance);
        manager.registerEvents(areaManager, instance);
        manager.registerEvents(lootsManager, instance);
        manager.registerEvents(new DoubleSpaceListener(), instance);
        manager.registerEvents(liftManager, instance);
        manager.registerEvents(game, instance);
        manager.registerEvents(new CommandChat(), instance);
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
}
