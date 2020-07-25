package by.dero.gvh.stats;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.storages.MongoDBStorage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class GameStatsData {
    private final MongoCollection<Document> collection;
    private final MongoDBStorage storage;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public GameStatsData(MongoDBStorage storage) {
        this.storage = storage;
        collection = storage.getDatabase().getCollection("games");
    }

    public void saveGameStats(GameStats game) {
        int id = Plugin.getInstance().getStatsData().generateGameId();
        try {
            storage.save("games", Integer.toString(id), gson.toJson(game));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
