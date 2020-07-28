package by.dero.gvh;

import by.dero.gvh.bookapi.BookManager;
import by.dero.gvh.commands.AdviceCommand;
import by.dero.gvh.commands.BugCommand;
import by.dero.gvh.commands.EtherCommand;
import by.dero.gvh.commands.TestCommand;
import by.dero.gvh.donate.DonateData;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.minigame.Minigame;
import by.dero.gvh.model.*;
import by.dero.gvh.model.kits.DonateKitManager;
import by.dero.gvh.model.storages.LocalStorage;
import by.dero.gvh.model.storages.MongoDBStorage;
import by.dero.gvh.nmcapi.CustomEntities;
import by.dero.gvh.stats.GameStatsData;
import by.dero.gvh.stats.StatsData;
import by.dero.gvh.utils.DataUtils;
import by.dero.gvh.utils.GameUtils;
import by.dero.gvh.utils.MathUtils;
import by.dero.gvh.utils.ResourceUtils;
import com.google.gson.Gson;
import lombok.Getter;
import net.minecraft.server.v1_12_R1.AdvancementDataWorld;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import ru.cristalix.core.CoreApi;
import ru.cristalix.core.invoice.IInvoiceService;
import ru.cristalix.core.invoice.InvoiceService;
import ru.cristalix.core.karma.IKarmaService;
import ru.cristalix.core.karma.KarmaService;
import ru.cristalix.core.map.IMapService;
import ru.cristalix.core.map.MapService;
import ru.cristalix.core.network.ISocketClient;
import ru.cristalix.core.permissions.IPermissionService;
import ru.cristalix.core.pvp.CPSLimiter;
import ru.cristalix.core.realm.IRealmService;
import ru.cristalix.core.scoreboard.IScoreboardService;
import ru.cristalix.core.scoreboard.ScoreboardService;
import ru.cristalix.core.transfer.ITransferService;
import ru.cristalix.core.transfer.TransferService;

import java.io.IOException;

public class Plugin extends JavaPlugin implements Listener {
    private static Plugin instance;
    private Data data;
    private PlayerData playerData;
    private ServerData serverData;
    private ReportData reportData;
    @Getter
    private GameStatsData gameStatsData;
    @Getter
    private StatsData statsData;
    @Getter
    private DonateData donateData;
    private PluginMode pluginMode;
    private BookManager bookManager;
    @Getter
    private BoosterManager boosterManager;
    @Getter
    private DonateKitManager donateKitManager;
    @Getter
    private CosmeticManager cosmeticManager;
    private Settings settings;
    private Lang lang;

    @Override
    public void onEnable() {
        super.onEnable();
        CustomEntities.registerEntities();
        instance = this;
        new GameUtils();
        try {
            String text = DataUtils.loadOrDefault(new LocalStorage(),
                    "settings", "settings", ResourceUtils.readResourceFile("/settings.json"));
            settings = new Gson().fromJson(text,
                    Settings.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (settings.isCristalix()) {
            CoreApi.get().registerService(ITransferService.class, new TransferService(ISocketClient.get()));
            CoreApi.get().registerService(IScoreboardService.class, new ScoreboardService());
            CoreApi.get().registerService(IInvoiceService.class, new InvoiceService(ISocketClient.get()));
            CoreApi.get().registerService(IMapService.class, new MapService());
            IPermissionService.get().enableTablePermissions();
            new CPSLimiter(this, 10);
            IScoreboardService.get().getServerStatusBoard().setDisplayName("§5EtherWar §f - beta");
            IRealmService.get().getCurrentRealmInfo().setMaxPlayers(1000);
            settings.setServerName(IRealmService.get().getCurrentRealmInfo().getRealmId().getRealmName());;
        }
        boosterManager = new BoosterManager();
        Bukkit.getPluginCommand("test").setExecutor(new TestCommand());
        Bukkit.getPluginCommand("ether").setExecutor(new EtherCommand());
        Bukkit.getPluginCommand("bug").setExecutor(new BugCommand());
        Bukkit.getPluginCommand("advice").setExecutor(new AdviceCommand());
        lang = new Lang(new LocalStorage());
        lang.load(settings.getLocale());

        StorageInterface dataStorage = null;
        if (settings.getDataStorageType().equals("local")) {
            dataStorage = new LocalStorage();
        } else
        if (settings.getDataStorageType().equals("mongodb")) {
            dataStorage = new MongoDBStorage(settings.getDataMongodbConnection(), settings.getDataMongodbDatabase());
        }
        data = new Data(dataStorage);
        data.load();
        playerData = new PlayerData(new MongoDBStorage(
                settings.getPlayerDataMongodbConnection(), settings.getPlayerDataMongodbDatabase()));
        serverData = new ServerData(new MongoDBStorage(
                settings.getServerDataMongodbConnection(), settings.getServerDataMongodbDatabase()));

        statsData = new StatsData(new MongoDBStorage(
                settings.getServerDataMongodbConnection(), settings.getServerDataMongodbDatabase()));

        donateData = new DonateData(new MongoDBStorage(
                settings.getServerDataMongodbConnection(), settings.getServerDataMongodbDatabase()));

        gameStatsData = new GameStatsData(new MongoDBStorage(
                settings.getServerDataMongodbConnection(), settings.getServerDataMongodbDatabase()));

        StorageInterface reportDataStorage = new LocalStorage();
        if (settings.getReportDataStorageType().equals("mongodb")) {
            reportDataStorage = new MongoDBStorage(
                    settings.getReportDataMongodbConnection(), settings.getReportDataMongodbDatabase());
        }
        reportData = new ReportData(reportDataStorage);
        World world;
        if (settings.getMode().equals("minigame")) {
            pluginMode = new Minigame();
            pluginMode.onEnable();

            world = Bukkit.getWorld(Minigame.getInstance().getGame().getInfo().getLobbyWorld());

            for (final LivingEntity ent : world.getLivingEntities()) {
                ent.remove();
            }
            if (settings.isCristalix()) {
                CoreApi.get().registerService(IKarmaService.class, new KarmaService(ISocketClient.get()));
                IKarmaService.get().enableGG(uuid -> Minigame.getInstance().getGame().getState().equals(Game.State.FINISHING));
            }
        } else {
            pluginMode = new Lobby();
            pluginMode.onEnable();

            Bukkit.getPluginManager().registerEvents((Listener) pluginMode, this);
        }
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(Plugin.getInstance(), "BungeeCord");

        Bukkit.getPluginManager().registerEvents(this, this);
        new MathUtils();
        AdvancementDataWorld.REGISTRY.advancements.clear();
        AdvancementDataWorld.REGISTRY.c.clear();
        bookManager = new BookManager();
        cosmeticManager = new CosmeticManager();
        donateKitManager = new DonateKitManager();
    }

    @Override
    public void onDisable() {
        pluginMode.onDisable();
        CustomEntities.unregisterEntities();
    }

    public ServerData getServerData() {
        return serverData;
    }

    public Lang getLang() {
        return lang;
    }

    public Settings getSettings() {
        return settings;
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

    public ReportData getReportData() {
        return reportData;
    }

    public PluginMode getPluginMode() {
        return pluginMode;
    }

    public BookManager getBookManager() {
        return bookManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        serverData.updateOnline(settings.getServerName(),
                Bukkit.getServer().getOnlinePlayers().size());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        serverData.updateOnline(settings.getServerName(),
                Bukkit.getServer().getOnlinePlayers().size() - 1);
    }
}
