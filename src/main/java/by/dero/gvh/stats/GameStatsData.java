package by.dero.gvh.stats;

import by.dero.gvh.Plugin;
import by.dero.gvh.model.PlayerInfo;
import by.dero.gvh.model.storages.MongoDBStorage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.Document;
import org.bson.types.ObjectId;

public class GameStatsData {
    @Getter
    private final MongoCollection<Document> gamesCollection;
    @Getter
    private final MongoCollection<Document> playersCollection;
    private final MongoDBStorage storage;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public GameStatsData(MongoDBStorage storage) {
        this.storage = storage;
        gamesCollection = storage.getDatabase().getCollection("games");
        playersCollection = storage.getDatabase().getCollection("playerStats");
        playersCollection.createIndex(new BsonDocument("exp", new BsonInt32(-1)));
    }

    public GameStats getGameStats(int id) {
        Document document = gamesCollection.find(
                Filters.eq("_id", id)).first();
        if (document == null) {
            return null;
        }
        return gson.fromJson(document.toJson(), GameStats.class);
    }

    public PlayerStats getPlayerStats(String playerName) {
        Document document = playersCollection.find(
                Filters.eq("_id", playerName)).first();
        if (document == null) {
            return new PlayerStats(playerName);
        }
        return gson.fromJson(document.toJson(), PlayerStats.class);
    }

    public void savePlayerStats(PlayerStats stats) {
        ReplaceOptions options = new ReplaceOptions();
        options.upsert(true);
        Document updater = new Document(BasicDBObject.parse(gson.toJson(stats)));
        playersCollection.replaceOne(Filters.eq("_id", stats.getName()), updater, options);
    }

    public void saveGameStats(GameStats game) {
        int id = Plugin.getInstance().getStatsData().generateGameId();
        game.setId(id);
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
                ReplaceOptions options = new ReplaceOptions();
                options.upsert(true);
                Document updater = new Document(BasicDBObject.parse(gson.toJson(playerStats)));
                playersCollection.replaceOne(Filters.eq("_id", playerStats.getName()), updater, options);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void analyzeAllAndSave(int startID) {
        GamesAnalyzer gamesAnalyzer = new GamesAnalyzer();
        for (Document document : gamesCollection.find()) {
            GameStats stats = gson.fromJson(document.toJson(), GameStats.class);
            if (stats.getId() < startID) {
                continue;
            }
            gamesAnalyzer.getGames().add(stats);
        }
        BasicDBObject object = BasicDBObject.parse(gson.toJson(gamesAnalyzer.getBundle()));
        storage.getDatabase().getCollection("analyzeReports").insertOne(
                new Document(object));
    }
}
