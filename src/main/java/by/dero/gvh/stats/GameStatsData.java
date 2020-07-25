package by.dero.gvh.stats;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.storages.MongoDBStorage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

public class GameStatsData {
    private final MongoCollection<Document> gamesCollection;
    private final MongoCollection<Document> playersCollection;
    private final MongoDBStorage storage;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public GameStatsData(MongoDBStorage storage) {
        this.storage = storage;
        gamesCollection = storage.getDatabase().getCollection("games");
        playersCollection = storage.getDatabase().getCollection("playerStats");
    }

    public void saveGameStats(GameStats game) {
        int id = Plugin.getInstance().getStatsData().generateGameId();
        try {
            BasicDBObject dbObject = BasicDBObject.parse(gson.toJson(game));
            dbObject.put("_id", id);
            gamesCollection.insertOne(new Document(dbObject));
            for (GamePlayerStats gamePlayerStats : game.getPlayers().values()) {
                PlayerStats playerStats = new PlayerStats(gamePlayerStats.getName());
                Document document = playersCollection.find(
                        Filters.eq("_id", gamePlayerStats.getName())).first();
                if (document != null) {
                    playerStats = gson.fromJson(document.toJson(), PlayerStats.class);
                }
                playerStats.addGame(game);
                UpdateOptions options = new UpdateOptions();
                options.upsert(true);
                playersCollection.updateOne(new Document("_id", playerStats.getName()),
                        new Document(BasicDBObject.parse(gson.toJson(playerStats))), options);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
