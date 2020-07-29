package by.dero.gvh.donate;

import by.dero.gvh.model.storages.MongoDBStorage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class DonateData {
    private final MongoCollection<Document> collection;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public DonateData(MongoDBStorage storage) {
        collection = storage.getDatabase().getCollection("donate");
    }

    public void save(DonateInfo info) {
        BasicDBObject dbObject = BasicDBObject.parse(gson.toJson(info));
        collection.insertOne(new Document(dbObject));
    }
}
