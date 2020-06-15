package by.dero.gvh.model.storages;

import by.dero.gvh.model.StorageInterface;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

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
        database.getCollection(collection).insertOne(new Document(dbObject));
    }

    @Override
    public String load(String collection, String name) {
        return null;
    }

    @Override
    public boolean exists(String collection, String name) {
        return false;
    }

    public MongoClient getClient() {
        return client;
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
