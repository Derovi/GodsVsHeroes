package by.dero.gvh.lobby;

import by.dero.gvh.model.StorageInterface;
import by.dero.gvh.utils.Position;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LobbyData {
    private final StorageInterface storage;

    public LobbyData(StorageInterface storage) {
        this.storage = storage;
    }

    public void load() {
        if (!storage.exists("lobby", "lastPosition")) {
            try {
                storage.save("lobby", "lastPosition",
                        new GsonBuilder().setPrettyPrinting().create().toJson(new Position(0, 0, 70)));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public Position getLastLobbyPosition() {
        String json = storage.load("lobby", "lastPosition");
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

    public boolean recordExists(String playerName) {
        return storage.exists("lobbyRecords", playerName);
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
