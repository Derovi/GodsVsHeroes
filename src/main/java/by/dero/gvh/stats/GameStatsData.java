package by.dero.gvh.stats;

import by.dero.gvh.model.storages.MongoDBStorage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class GameStatsData {
    private final MongoCollection<Document> collection;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public GameStatsData(MongoDBStorage storage) {
        collection = storage.getDatabase().getCollection("games");
    }

    public void saveGameStats(GameStats game) {

    }
}
