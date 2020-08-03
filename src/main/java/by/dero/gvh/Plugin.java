package by.dero.gvh;

import by.dero.gvh.bookapi.BookManager;
import by.dero.gvh.commands.*;
import by.dero.gvh.donate.DonateData;
import by.dero.gvh.lobby.Lobby;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.minigame.Minigame;
import by.dero.gvh.model.*;
import by.dero.gvh.model.loots.LootBoxManager;
import by.dero.gvh.model.storages.LocalStorage;
import by.dero.gvh.model.storages.MongoDBStorage;
import by.dero.gvh.nmcapi.CustomEntities;
import by.dero.gvh.stats.GameStatsData;
import by.dero.gvh.stats.StatsData;
import by.dero.gvh.utils.*;
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
    @Getter private static Plugin instance;
    @Getter private Data data;
    @Getter private PlayerData playerData;
    @Getter private ServerData serverData;
    @Getter private ReportData reportData;
    @Getter private GameStatsData gameStatsData;
    @Getter private StatsData statsData;
    @Getter private DonateData donateData;
    @Getter private PluginMode pluginMode;
    @Getter private BookManager bookManager;
    @Getter private BoosterManager boosterManager;
    @Getter private LootBoxManager donateKitManager;
    @Getter private CosmeticManager cosmeticManager;
    @Getter private Settings settings;
    @Getter private Lang lang;

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
            IScoreboardService.get().getServerStatusBoard().setDisplayName("§5EtherWar");
            IRealmService.get().getCurrentRealmInfo().setMaxPlayers(1000);
            String typeName = IRealmService.get().getCurrentRealmInfo().getRealmId().getTypeName();
            if (typeName.equals("EW") || typeName.equals("EWP")) {
                IRealmService.get().getCurrentRealmInfo().setGroupName("EtherWar");
            }
            settings.setServerName(IRealmService.get().getCurrentRealmInfo().getRealmId().getRealmName());;
        }
        boosterManager = new BoosterManager();
        Bukkit.getPluginCommand("test").setExecutor(new TestCommand());
        Bukkit.getPluginCommand("thx").setExecutor(new ThxCommand());
        Bukkit.getPluginCommand("ether").setExecutor(new EtherCommand());
        Bukkit.getPluginCommand("vote").setExecutor(new VoteCommand());
        Bukkit.getPluginCommand("bug").setExecutor(new BugCommand());
        //Bukkit.getPluginCommand("tochc").setExecutor(new ToCHCCommand());
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
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        Bukkit.getPluginManager().registerEvents(this, this);
        new MathUtils();
        AdvancementDataWorld.REGISTRY.advancements.clear();
        AdvancementDataWorld.REGISTRY.c.clear();
        bookManager = new BookManager();
        cosmeticManager = new CosmeticManager();
        donateKitManager = new LootBoxManager();
//
//        LoadedMap<World> map = IMapService.get().
//                loadMap(IMapService.get().
//                        getMapByGameTypeAndMapName("EtherWar","Lobby1").get().getLatest(), BukkitWorldLoader.INSTANCE).join();
    }

    @Override
    public void onDisable() {
        pluginMode.onDisable();
        CustomEntities.unregisterEntities();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        serverData.updateOnline(settings.getServerName(),
                Bukkit.getServer().getOnlinePlayers().size());
        if (!event.getPlayer().isOp() && IRealmService.get().getCurrentRealmInfo().getRealmId().getTypeName().equals("TEST")) {
            event.getPlayer().sendMessage("§6Вы были перенаправлены на основной сервер! " +
                    "В следующий раз заходите через §5Голову дракона§6 в компасе!" +
                    "Если вы хотели попасть на CHC - /tochc");
            BridgeUtils.redirectPlayer(event.getPlayer(), "EW-1");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        serverData.updateOnline(settings.getServerName(),
                Bukkit.getServer().getOnlinePlayers().size() - 1);
    }
}
