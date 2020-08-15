package by.dero.gvh.stats;

import by.dero.gvh.model.storages.MongoDBStorage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import javax.print.Doc;

public class StatsData {
    private final MongoCollection<Document> collection;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public StatsData(MongoDBStorage storage) {
        collection = storage.getDatabase().getCollection("stats");
        UpdateOptions options = new UpdateOptions();
        options.upsert(true);
        //collection.insertOne(new Document("_id", "games"), new Document("lastID", "0"), options);
    }

    public int getNextGameId() {
        BasicDBObject find = new BasicDBObject();
        find.put("_id", "games");
        BasicDBObject update = new BasicDBObject();
        Document obj =  collection.find(new Document(find)).first();
        return obj.getInteger("lastID");
    }

    public int generateGameId() {
        BasicDBObject find = new BasicDBObject();
        find.put("_id", "games");
        BasicDBObject update = new BasicDBObject();
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        options.upsert(true);
        update.put("$inc", new BasicDBObject("lastID", 1));
        Document obj =  collection.findOneAndUpdate(new Document(find), new Document(update), options);
        return obj.getInteger("lastID");
    }
}
