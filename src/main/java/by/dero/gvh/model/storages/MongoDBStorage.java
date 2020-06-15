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
        ObjectId id = new ObjectId(Integer.toHexString(name.hashCode()));
        BasicDBObject dbObject = BasicDBObject.parse(object);
        dbObject.put("_id", id);
        System.out.println("save: " + dbObject.toJson());
        database.getCollection(collection).insertOne(new Document(dbObject));
    }

    @Override
    public String load(String collection, String name) {
        ObjectId id = new ObjectId(Integer.toHexString(name.hashCode()));
        Document document = database.getCollection(collection).find(
                Filters.eq("_id", id)).first();
        if (document == null) {
            System.out.println("load: null");
            return null;
        }
        System.out.println("load: " + document.toJson());
        return document.toJson();
    }

    @Override
    public boolean exists(String collection, String name) {
        ObjectId id = new ObjectId(Integer.toHexString(name.hashCode()));
        Document document = database.getCollection(collection).find(
                Filters.eq("_id", id)).first();
        return document != null;
    }

    public MongoClient getClient() {
        return client;
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
