package by.dero.gvh.lobby;

import by.dero.gvh.utils.Position;
import by.dero.gvh.model.StorageInterface;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LobbyData {
    private final StorageInterface storage;

    public LobbyData(StorageInterface storage) {
        this.storage = storage;
    }

    public Position getLastLobbyPosition() {
        String json = storage.load("lobby", "lastPosition");
        if (json == null) {
            return new Position(0,64,0);
        }
        return new Gson().fromJson(json, Position.class);
    }

    public void updateLastLobbyPosition(Position position) {
        try {
            storage.save("lobby", "lastPosition",
                    new GsonBuilder().setPrettyPrinting().create().toJson(position));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public LobbyRecord getRecord(String playerName) {
        String recordJson = storage.load("lobbyRecords", playerName);
        if (recordJson == null) {
            return null;
        }
        return new Gson().fromJson(recordJson, LobbyRecord.class);
    }

    public boolean isRecordExists(String playerName) {
        return storage.exists("lobbyRecords", playerName);
    }

    public void saveRecord(String playerName, LobbyRecord record) {
        try {
            storage.save("lobbyRecords", playerName,
                    new GsonBuilder().setPrettyPrinting().create().toJson(record));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public StorageInterface getStorage() {
        return storage;
    }
}
