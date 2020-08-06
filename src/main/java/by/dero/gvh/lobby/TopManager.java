package by.dero.gvh.lobby;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.storages.LocalStorage;
import by.dero.gvh.stats.IntTopEntry;
import by.dero.gvh.stats.PlayerStats;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.Document;
import org.bukkit.scheduler.BukkitRunnable;
import ru.cristalix.core.render.IRenderService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TopManager {
    @Getter
    private int lastUpdate = -1;
    @Getter @Setter
    private int topSize = 500;
    @Getter
    private final List<IntTopEntry> top = new ArrayList<>();
    @Getter @Setter
    private Runnable onUpdate = null;

    public TopManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                update();
            }
        }.runTaskTimer(Plugin.getInstance(), 20, 20);
    }

    public void update() {
        int nextGameId = Plugin.getInstance().getStatsData().getNextGameId();
        if (lastUpdate == nextGameId) {
            return;
        }
        top.clear();
        lastUpdate = nextGameId;
        int idx = 1;
        for (Document document : Plugin.getInstance().getGameStatsData().getPlayersCollection().aggregate(
                Collections.singletonList(new BsonDocument("$sort", new BsonDocument("exp", new BsonInt32(-1)))))) {
            if (document.getInteger("exp") == null) {
                continue;
            }
            top.add(new IntTopEntry(document.getString("_id"), document.getInteger("exp"), idx));
            ++idx;
            if (idx > 500) {
                break;
            }
        }
        if (onUpdate != null) {
            onUpdate.run();
        }
    }

    public long getPlayerOrder(String playerName) {
        PlayerStats stats = Plugin.getInstance().getGameStatsData().getPlayerStats(playerName);
        return Plugin.getInstance().getGameStatsData().getPlayersCollection().countDocuments(
                new BsonDocument("exp", new BsonDocument("$lt", new BsonInt32(stats.getExp())))) + 1;
    }
}
