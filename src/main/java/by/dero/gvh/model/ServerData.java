package by.dero.gvh.model;

import by.dero.gvh.Plugin;
import by.dero.gvh.minigame.Game;
import by.dero.gvh.model.storages.MongoDBStorage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitRunnable;

import javax.print.Doc;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Filter;

public class ServerData {
    private final MongoCollection<Document> collection;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private List<ServerInfo> savedGameServers;
    private ServerInfo savedLobbyServer;

    public ServerData(MongoDBStorage storage) {
        collection = storage.getDatabase().getCollection("servers");
        new BukkitRunnable() {  // updater
            @Override
            public void run() {
                savedLobbyServer = getLobbyServer();
                savedGameServers = getGameServers();
            }
        }.runTaskTimer(Plugin.getInstance(), 5, 2);
    }

    private ServerInfo getLobbyServer() {
        Document document = collection.find(Filters.eq("type", "LOBBY")).first();
        if (document == null) {
            return null;
        }
        return gson.fromJson(document.toJson(), ServerInfo.class);
    }

    private List<ServerInfo> getGameServers() {
        List<ServerInfo> servers = new LinkedList<>();
        for (Document document : collection.find(
                Filters.and(Filters.eq("type", "GAME")))) {
            servers.add(gson.fromJson(document.toJson(), ServerInfo.class));
        }
        servers.sort((info1, info2) -> {
            int status1 = 0;
            if (info1.getStatus().equals(Game.State.GAME_FULL.toString())) {
                status1 = 1;
            } else if (info1.getStatus().equals(Game.State.PREPARING.toString())) {
                status1 = 2;
            } else if (info1.getStatus().equals(Game.State.FINISHING.toString())) {
                status1 = 3;
            } else if (info1.getStatus().equals(Game.State.GAME.toString())) {
                status1 = 4;
            }
            int status2 = 0;
            if (info2.getStatus().equals(Game.State.GAME_FULL.toString())) {
                status2 = 1;
            } else if (info2.getStatus().equals(Game.State.PREPARING.toString())) {
                status2 = 2;
            } else if (info2.getStatus().equals(Game.State.FINISHING.toString())) {
                status2 = 3;
            } else if (info1.getStatus().equals(Game.State.GAME.toString())) {
                status2 = 4;
            }
            if (status1 == status2) {
                return info2.getOnline() - info1.getOnline();
            }
            return status1 - status2;
        });
        return servers;
    }

    public ServerInfo getServerInfo(String serverName) {
        Document document = collection.find(Filters.eq("_id", serverName)).first();
        if (document == null) {
            return null;
        }
        return gson.fromJson(document.toJson(), ServerInfo.class);
    }

    public void register(String serverName, ServerType type, int maxOnline) {
        ServerInfo serverInfo = new ServerInfo(serverName, type);
        serverInfo.setMaxOnline(maxOnline);
        BasicDBObject dbObject = BasicDBObject.parse(gson.toJson(serverInfo));
        dbObject.put("_id", serverName);
        ReplaceOptions options = new ReplaceOptions();
        options.upsert(true);
        collection.replaceOne(Filters.eq("_id", serverName), new Document(dbObject), options);
    }

    public void unregister(String serverName) {
        collection.deleteOne(Filters.eq("_id", serverName));
    }


    public void updateStatus(String serverName, String status) {
        collection.updateOne(Filters.eq("_id", serverName),
                new Document("$set", new Document("status", status)));
    }

    public void updateOnline(String serverName, int online) {
        collection.updateOne(Filters.eq("_id", serverName),
                new Document("$set", new Document("online", online)));
    }

    public List<ServerInfo> getSavedGameServers() {
        return savedGameServers;
    }

    public ServerInfo getSavedLobbyServer() {
        return savedLobbyServer;
    }
}
