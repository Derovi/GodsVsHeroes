package by.dero.gvh.model.storages;

import by.dero.gvh.model.StorageInterface;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;

public class MongoDBStorage implements StorageInterface {
    private MongoClient client;
    private MongoDatabase database;

    public MongoDBStorage(String connectionString, String databaseName) {
        try {
            client = MongoClients.create(connectionString);
            database = client.getDatabase(databaseName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void save(String collection, String name, String object) throws IOException {
        BasicDBObject dbObject = BasicDBObject.parse(object);
        dbObject.put("_id", name);
        if (!exists(collection, name)) {
            database.getCollection(collection).insertOne(new Document(dbObject));
        } else {
            database.getCollection(collection).replaceOne(Filters.eq("_id", name), new Document(dbObject));
        }
    }

    @Override
    public String load(String collection, String name) {
        Document document = database.getCollection(collection).find(
                Filters.eq("_id", name)).first();
        if (document == null) {
            return null;
        }
        document.remove("_id");
        return document.toJson();
    }

    @Override
    public boolean exists(String collection, String name) {
        Document document = database.getCollection(collection).find(
                Filters.eq("_id", name)).first();
        return document != null;
    }

    public MongoClient getClient() {
        return client;
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
