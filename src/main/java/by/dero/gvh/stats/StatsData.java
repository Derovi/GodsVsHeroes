package by.dero.gvh.stats;

import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.ServerInfo;
import by.dero.gvh.model.ServerType;
import by.dero.gvh.model.storages.MongoDBStorage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.List;

public class StatsData {
    private final MongoCollection<Document> collection;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public StatsData(MongoDBStorage storage) {
        collection = storage.getDatabase().getCollection("stats");
    }
}
